package com.github.plantfern.foodDiary.meals.domain.services;


import com.github.plantfern.foodDiary.common.domain.services.NutrientService;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.*;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalDto;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalNutrientDto;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.SleepLogDto;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.WeightLogDto;
import com.github.plantfern.foodDiary.food.api.apis.FoodServingApi;
import com.github.plantfern.foodDiary.food.api.dto.ProductServingDto;
import com.github.plantfern.foodDiary.meals.api.dto.*;
import com.github.plantfern.foodDiary.meals.domain.MealPolicy;
import com.github.plantfern.foodDiary.meals.domain.entities.MealTypeEntity;
import com.github.plantfern.foodDiary.meals.domain.repositories.MealTemplateRepository;
import com.github.plantfern.foodDiary.meals.domain.repositories.MealTypeRepository;
import com.github.plantfern.foodDiary.meals.domain.repositories.VMealDayRecordRepository;
import com.github.plantfern.foodDiary.meals.domain.views.VMealDayRecordEntity;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class MealQueryService {

    private static final int HEADER_TARGET_LIMIT = 4;

    /** Fixed diary UI order — ignore extra rows in meal_types. */
    private static final List<String> CANONICAL_MEAL_TYPE_CODES = List.of(
            "BREAKFAST",
            "LUNCH",
            "SNACK",
            "DINNER",
            "OTHERS"
    );

    private final VMealDayRecordRepository vMealDayRecordRepository;
    private final MealTemplateRepository mealTemplateRepository;
    private final MealTypeRepository mealTypeRepository;
    private final FoodServingApi foodServingApi;
    private final DiaryProfileApi diaryProfileApi;
    private final MealPolicy mealPolicy;
    private final CurrentUser currentUser;
    private final ProfileFeatureSettingsApi profileFeatureSettingsApi;
    private final NutrientService nutrientService;
    private final GoalApi goalApi;
    private final SleepLogApi sleepLogApi;
    private final WeightLogApi weightLogApi;

    public DayMealsDto getDay(Long diaryProfileId, LocalDate date) {
        var ownerId = diaryProfileApi.getOwnerUserIdInternal(diaryProfileId);
        mealPolicy.ensureCanGet(currentUser, ownerId);

        var meals = vMealDayRecordRepository
                .findAllByDiaryProfileIdAndMealDate(diaryProfileId, date);

        var settings = profileFeatureSettingsApi.getActiveByDiaryProfileInternal(diaryProfileId);
        Set<Long> hiddenNutrientIds = settings.hiddenNutrientIds() != null
                ? settings.hiddenNutrientIds()
                : Set.of();

        boolean sleepEnabled = Boolean.TRUE.equals(settings.showSleep());
        boolean weightEnabled = Boolean.TRUE.equals(settings.showWeight());

        Map<Long, Float> targetByNutrientId = loadVisibleGoalTargets(diaryProfileId, hiddenNutrientIds);

        List<Long> headerNutrientIds = new ArrayList<>(
                targetByNutrientId
                        .keySet()
                        .stream()
                        .sorted()
                        .limit(HEADER_TARGET_LIMIT)
                        .toList()
        );

        Long primaryNutrientId = headerNutrientIds.isEmpty()
                ? nutrientService.getFirstByIdNotIn(hiddenNutrientIds)
                : headerNutrientIds.getFirst();

        if (primaryNutrientId == null) {
            primaryNutrientId = 1L;
        }

        if (!headerNutrientIds.contains(primaryNutrientId)) {
            headerNutrientIds.add(0, primaryNutrientId);
        }

        Set<Long> statNutrientIds = new HashSet<>(headerNutrientIds);
        statNutrientIds.add(primaryNutrientId);

        var servingIds = meals.stream()
                .map(VMealDayRecordEntity::getServingId)
                .collect(Collectors.toSet());

        Map<Long, ProductServingDto> productServingMap = servingIds.isEmpty()
                ? Map.of()
                : foodServingApi.getInfoByServingIds(servingIds, primaryNutrientId);

        Map<Long, Map<Long, Float>> nutrientsByServing = servingIds.isEmpty()
                ? Map.of()
                : foodServingApi.getNutrientsByServingIds(servingIds, statNutrientIds);

        Map<Long, Float> factByNutrientId = new HashMap<>();

        for (var meal : meals) {

            var serving = productServingMap.get(meal.getServingId());
            var nutrientAmountPer100g = nutrientsByServing.getOrDefault(meal.getServingId(), Map.of());

            if (serving == null || serving.gramWeight() == null || meal.getAmount() == null)
                continue;

            Float servingAmount = serving.servingAmount() != null ? serving.servingAmount() : 1f;
            float grams = meal.getAmount() * serving.gramWeight() * servingAmount;
            for (Long nutrientId : statNutrientIds) {
                Float amountPer100g = nutrientAmountPer100g.get(nutrientId);
                if (amountPer100g == null)
                    continue;

                factByNutrientId.merge(nutrientId, grams / 100f * amountPer100g, Float::sum);
            }
        }

        List<DayNutrientStatDto> targets = headerNutrientIds
                .stream()
                .map(
                        nutrientId -> {
                            var target = targetByNutrientId.get(nutrientId);
                            var fact = factByNutrientId
                                    .getOrDefault(nutrientId, 0F);
                            String code;
                            try {
                                code = nutrientService.getById(nutrientId).getCode();
                            } catch (EntityNotFoundException e) {
                                code = "NUTRIENT";
                            }
                            Float remaining = (target == null) ? null : target - fact;
                            Float percent = (target == null || target == 0f) ? null : fact / target * 100f;
                            return new DayNutrientStatDto(
                                    nutrientId,
                                    code,
                                    target,
                                    fact,
                                    remaining,
                                    percent
                            );
                        }
                )
                .toList();

        // Canonical types only (BREAKFAST → OTHERS), ordered
        Map<String, MealTypeEntity> typeByCode = mealTypeRepository
                .findAllByCodeIn(CANONICAL_MEAL_TYPE_CODES)
                .stream()
                .collect(Collectors.toMap(
                        t -> t.getCode().toUpperCase(Locale.ROOT),
                        t -> t,
                        (a, b) -> a
                ));

        List<MealTypeEntity> orderedTypes = CANONICAL_MEAL_TYPE_CODES.stream()
                .map(typeByCode::get)
                .filter(Objects::nonNull)
                .toList();

        // Map non-canonical snack variants into SNACK / OTHERS so records are not lost
        Map<Long, Long> typeIdRemap = buildTypeIdRemap(typeByCode);

        var byMealType = new LinkedHashMap<Long, List<VMealDayRecordEntity>>();
        for (var row : meals) {
            Long rawTypeId = row.getMealTypeId();
            Long mappedId = typeIdRemap.getOrDefault(rawTypeId, rawTypeId);
            byMealType.computeIfAbsent(mappedId, k -> new ArrayList<>()).add(row);
        }

        List<MealSectionDto> sections = orderedTypes.stream()
                .map(type -> {
                    var mealRows = byMealType.getOrDefault(type.getId(), List.of());
                    if (mealRows.isEmpty()) {
                        return new MealSectionDto(
                                -type.getId(),
                                type.getId(),
                                type.getCode(),
                                null,
                                List.of(),
                                0f,
                                null
                        );
                    }
                    // One physical meal per type per day: prefer first non-null mealId group
                    var first = mealRows.getFirst();
                    var items = mealRows.stream()
                            .map(r -> toItemDto(r, productServingMap.get(r.getServingId())))
                            .toList();
                    String mealPhoto = mealRows.stream()
                            .map(VMealDayRecordEntity::getMealPhotoPath)
                            .filter(p -> p != null && !p.isBlank())
                            .findFirst()
                            .orElse(null);
                    return new MealSectionDto(
                            first.getMealId(),
                            type.getId(),
                            type.getCode(),
                            first.getGeneratedFromTemplateId(),
                            items,
                            sumNutrient(items),
                            mealPhoto
                    );
                })
                .toList();

        var appliedTemplateIds = meals.stream()
                .map(VMealDayRecordEntity::getGeneratedFromTemplateId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        var pendingTemplates = mealTemplateRepository
                .findAllByDiaryProfileId(diaryProfileId)
                .stream()
                .filter(t -> !appliedTemplateIds.contains(t.getId()))
                .map(t -> new MealTemplateListItemDto(
                        t.getId(),
                        t.getName(),
                        t.getScheduledTime(),
                        t.getFrequency(),
                        null,
                        null
                ))
                .toList();

        List<SleepLogDto> sleepForDay = sleepEnabled
                ? sleepLogApi.getByDiaryProfileIdAndDateInternal(diaryProfileId, date)
                : List.of();

        WeightLogDto weightLog = weightEnabled
                ? weightLogApi.getLatestByDiaryProfileIdInternal(diaryProfileId)
                : null;

        String primaryCode;
        try {
            primaryCode = nutrientService.getById(primaryNutrientId).getCode();
        } catch (EntityNotFoundException e) {
            primaryCode = "NUTRIENT";
        }

        return new DayMealsDto(
                date,
                primaryNutrientId,
                primaryCode,
                targets,
                sections,
                pendingTemplates,
                sleepEnabled,
                weightEnabled,
                sleepForDay,
                weightLog
        );
    }

    public List<RecentDayFoodGroupDto> getRecentWeek(Long diaryProfileId) {
        var ownerId = diaryProfileApi.getOwnerUserIdInternal(diaryProfileId);
        mealPolicy.ensureCanGet(currentUser, ownerId);

        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(6);

        var rows = vMealDayRecordRepository
                .findAllByDiaryProfileIdAndMealDateBetween(diaryProfileId, from, to);

        var servingIds = rows.stream()
                .map(VMealDayRecordEntity::getServingId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, ProductServingDto> servingMap = servingIds.isEmpty()
                ? Map.of()
                : foodServingApi.getInfoByServingIds(servingIds, 1L);

        Map<LocalDate, List<RecentFoodItemDto>> byDate = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            byDate.put(to.minusDays(i), new ArrayList<>());
        }

        for (var row : rows) {
            if (row.getMealDate() == null) continue;
            var serving = servingMap.get(row.getServingId());
            String name = serving != null && serving.productDescription() != null
                    ? serving.productDescription()
                    : "Food #" + row.getRecordId();
            byDate.computeIfAbsent(row.getMealDate(), d -> new ArrayList<>())
                    .add(new RecentFoodItemDto(
                            row.getRecordId(),
                            name,
                            row.getMealTypeCode()
                    ));
        }

        return byDate.entrySet().stream()
                .filter(e -> !e.getValue().isEmpty())
                .map(e -> new RecentDayFoodGroupDto(e.getKey(), e.getValue()))
                .toList();
    }

    /**
     * Extra DB meal types (MORNING_SNACK, LATE_SNACK, OTHER, …) collapse into SNACK / OTHERS
     * so the home screen never shows duplicate placeholder cards.
     */
    private Map<Long, Long> buildTypeIdRemap(Map<String, MealTypeEntity> canonicalByCode) {
        Map<Long, Long> remap = new HashMap<>();
        Long snackId = optionalTypeId(canonicalByCode, "SNACK");
        Long othersId = optionalTypeId(canonicalByCode, "OTHERS");

        for (MealTypeEntity t : mealTypeRepository.findAll()) {
            String code = t.getCode() != null ? t.getCode().toUpperCase(Locale.ROOT) : "";
            if (CANONICAL_MEAL_TYPE_CODES.contains(code)) {
                remap.put(t.getId(), t.getId());
                continue;
            }
            if (code.contains("SNACK") || code.equals("BRUNCH")) {
                if (snackId != null) remap.put(t.getId(), snackId);
            } else if (othersId != null) {
                remap.put(t.getId(), othersId);
            }
        }
        return remap;
    }

    private static Long optionalTypeId(Map<String, MealTypeEntity> byCode, String code) {
        MealTypeEntity t = byCode.get(code);
        return t != null ? t.getId() : null;
    }

    private Map<Long, Float> loadVisibleGoalTargets(Long diaryProfileId, Set<Long> hidden) {

        GoalDto goal;

        try {
            goal = goalApi.getActiveByDiaryProfileInternal(diaryProfileId);
        } catch (EntityNotFoundException e) {
            return Map.of();
        }

        if (goal == null || goal.nutrientSet() == null)
            return Map.of();

        Map<Long, Float> targets = new HashMap<>();

        for (GoalNutrientDto goalNutrient : goal.nutrientSet()) {

            if (goalNutrient.nutrientId() == null) {
                continue;
            }

            if (hidden.contains(goalNutrient.nutrientId()))
                continue;

            targets.put(goalNutrient.nutrientId(), goalNutrient.amount());
        }

        return targets;
    }

    private MealFoodRecordItemDto toItemDto(
            VMealDayRecordEntity r,
            ProductServingDto productServing
    ) {
        Float nutrient = null;
        if (productServing != null
                && productServing.gramWeight() != null
                && productServing.nutrientPer100g() != null
                && r.getAmount() != null) {
            float servingAmount = productServing.servingAmount() != null
                    ? productServing.servingAmount()
                    : 1f;
            nutrient = (r.getAmount() * productServing.gramWeight() * servingAmount / 100f)
                    * productServing.nutrientPer100g();
        }

        return new MealFoodRecordItemDto(
                r.getRecordId(),
                r.getServingId(),
                productServing != null ? productServing.productDescription() : null,
                r.getAmount(),
                productServing != null ? productServing.servingAmount() : null,
                productServing != null ? productServing.gramWeight() : null,
                productServing != null ? productServing.servingUnitCode() : null,
                nutrient,
                productServing != null ? productServing.photoPath() : null
        );
    }

    private Float sumNutrient(List<MealFoodRecordItemDto> items) {
        double sum = items.stream()
                .map(MealFoodRecordItemDto::nutrient)
                .filter(Objects::nonNull)
                .mapToDouble(Float::doubleValue)
                .sum();
        return (float) sum;
    }
}
