package com.github.plantfern.foodDiary.meals.domain.services;


import com.github.plantfern.foodDiary.common.services.NutrientService;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.*;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalDto;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalNutrientDto;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.SleepLogDto;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.WeightLogDto;
import com.github.plantfern.foodDiary.food.api.apis.FoodServingApi;
import com.github.plantfern.foodDiary.food.api.dto.ProductServingDto;
import com.github.plantfern.foodDiary.meals.api.dto.*;
import com.github.plantfern.foodDiary.meals.domain.MealPolicy;
import com.github.plantfern.foodDiary.meals.domain.repositories.MealTemplateRepository;
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

    private final VMealDayRecordRepository vMealDayRecordRepository;
    private final MealTemplateRepository mealTemplateRepository;
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

        boolean sleepEnabled = settings.showSleep();
        boolean weightEnabled =  settings.showWeight();

        Map<Long, Float> targetByNutrientId = loadVisibleGoalTargets(diaryProfileId, hiddenNutrientIds);

        List<Long> headerNutrientIds = targetByNutrientId
                .keySet()
                .stream()
                .sorted()
                .limit(HEADER_TARGET_LIMIT)
                .toList();

        Long primaryNutrientId = headerNutrientIds.isEmpty()
                ? nutrientService.getFirstByIdNotIn(hiddenNutrientIds)
                : headerNutrientIds.getFirst();

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

        for(var meal : meals) {

            var serving = productServingMap.get(meal.getServingId());
            var nutrientAmountPer100g = nutrientsByServing.getOrDefault(meal.getServingId(), Map.of());

            if(serving == null || serving.gramWeight() == null || meal.getAmount() == null)
                continue;

            float grams = meal.getAmount() * serving.gramWeight() * serving.servingAmount();
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
                            String code = nutrientService.getById(nutrientId).getCode();
                            Float remaining = (target == null) ? null : target - fact;
                            Float percent = (target == null || target == 0f) ? null : fact/target *100f;
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

        var byMeal = meals.stream()
                .collect(Collectors.groupingBy(VMealDayRecordEntity::getMealId));

        List<MealSectionDto> sections = byMeal
                .values()
                .stream()
                .map(mealRows -> {
                    var first = mealRows.getFirst();

                    var items = mealRows.stream()
                            .map(r -> toItemDto(r, productServingMap.get(r.getServingId())))
                            .toList();

                    return new MealSectionDto(
                            first.getMealId(),
                            first.getMealTypeId(),
                            first.getMealTypeCode(),
                            first.getGeneratedFromTemplateId(),
                            items,
                            sumNutrient(items)
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

        String primaryCode = nutrientService.getById(primaryNutrientId).getCode();

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

    private Map<Long, Float> loadVisibleGoalTargets(Long diaryProfileId, Set<Long> hidden){

        GoalDto goal;

        try {
            goal = goalApi.getActiveByDiaryProfileInternal(diaryProfileId);
        } catch (EntityNotFoundException e) {
            return Map.of();
        }

        if(goal == null || goal.nutrientSet() == null)
            return Map.of();

        Map<Long, Float> targets = new HashMap<>();

        for(GoalNutrientDto goalNutrient : goal.nutrientSet()){

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
            nutrient = (r.getAmount() * productServing.gramWeight() / 100f) * productServing.nutrientPer100g();
        }

        return new MealFoodRecordItemDto(
                r.getRecordId(),
                r.getServingId(),
                productServing != null ? productServing.productDescription() : null,
                r.getAmount(),
                productServing != null ? productServing.servingAmount() : null,
                productServing != null ? productServing.gramWeight() : null,
                productServing != null ? productServing.servingUnitCode() : null,
                nutrient
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