package com.github.plantfern.foodDiary.meals.domain.services;


import com.github.plantfern.foodDiary.common.services.NutrientService;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryProfileApi;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.ProfileFeatureSettingsApi;
import com.github.plantfern.foodDiary.food.api.apis.FoodServingApi;
import com.github.plantfern.foodDiary.food.api.dto.ProductServingDto;
import com.github.plantfern.foodDiary.meals.api.dto.*;
import com.github.plantfern.foodDiary.meals.domain.MealPolicy;
import com.github.plantfern.foodDiary.meals.domain.repositories.MealTemplateRepository;
import com.github.plantfern.foodDiary.meals.domain.repositories.VMealDayRecordRepository;
import com.github.plantfern.foodDiary.meals.domain.views.VMealDayRecordEntity;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
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

    private final VMealDayRecordRepository vMealDayRecordRepository;
    private final MealTemplateRepository mealTemplateRepository;
    private final FoodServingApi foodServingApi;
    private final DiaryProfileApi diaryProfileApi;
    private final MealPolicy mealPolicy;
    private final CurrentUser currentUser;
    private final ProfileFeatureSettingsApi profileFeatureSettingsApi;
    private final NutrientService nutrientService;

    public DayMealsDto getDay(Long diaryProfileId, LocalDate date) {
        var ownerId = diaryProfileApi.getOwnerUserIdInternal(diaryProfileId);
        mealPolicy.ensureCanGet(currentUser, ownerId);

        var rows = vMealDayRecordRepository
                .findAllByDiaryProfileIdAndMealDate(diaryProfileId, date);

        var servingIds = rows.stream()
                .map(VMealDayRecordEntity::getServingId)
                .collect(Collectors.toSet());

        var settings = profileFeatureSettingsApi.getActiveByDiaryProfileInternal(diaryProfileId);
        var hiddenNutrientIds = settings.hiddenNutrientIds();
        var accepted = nutrientService.getFirstByIdNotIn(hiddenNutrientIds);
        var checkedAccepted = hiddenNutrientIds != null ? accepted : 1L;

        Map<Long, ProductServingDto> productServingMap = servingIds.isEmpty()
                ? Map.of()
                : foodServingApi.getInfoByServingIds(servingIds, checkedAccepted);

        var byMeal = rows.stream()
                .collect(Collectors.groupingBy(VMealDayRecordEntity::getMealId));

        List<MealSectionDto> sections = byMeal.values().stream()
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

        var appliedTemplateIds = rows.stream()
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

        return new DayMealsDto(date, sections, pendingTemplates);
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