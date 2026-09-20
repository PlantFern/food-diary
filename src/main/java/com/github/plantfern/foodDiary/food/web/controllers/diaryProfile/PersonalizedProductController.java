package com.github.plantfern.foodDiary.food.web.controllers.diaryProfile;

import com.github.plantfern.foodDiary.food.api.dto.PersonalizedProductDetailDto;
import com.github.plantfern.foodDiary.food.api.dto.PersonalizedProductListItemDto;
import com.github.plantfern.foodDiary.food.api.dto.PersonalizedProductSearchRequest;
import com.github.plantfern.foodDiary.food.domain.services.ProductQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/products")
public class PersonalizedProductController {

    private final ProductQueryService productQueryService;

    public PersonalizedProductController(ProductQueryService productQueryService) {
        this.productQueryService = productQueryService;
    }

    @GetMapping("/diary-profile/{diaryProfileId}")
    public Page<PersonalizedProductListItemDto> getAllPersonalized(
            @PathVariable Long diaryProfileId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean onlyFavorites,
            @RequestParam(required = false) Boolean onlyMy,
            Pageable pageable
    ) {
        return productQueryService.getAllPersonalized(
                diaryProfileId,
                new PersonalizedProductSearchRequest(query, categoryId, onlyFavorites, onlyMy),
                pageable
        );
    }

    @GetMapping("/diary-profile/{diaryProfileId}/products/{productId}")
    public PersonalizedProductDetailDto getDetailPersonalized(
            @PathVariable Long diaryProfileId,
            @PathVariable Long productId
    ) {
        return productQueryService.getPersonalizedDetailAboutProduct(diaryProfileId, productId);
    }
}
