package com.github.plantfern.foodDiary.food.web.controllers.moderation;

import com.github.plantfern.foodDiary.food.api.dto.ProductDetailDto;
import com.github.plantfern.foodDiary.food.api.dto.ProductListItemDto;
import com.github.plantfern.foodDiary.food.web.requests.ProductSearchRequest;
import com.github.plantfern.foodDiary.food.domain.services.ProductQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/moderation/products")
public class ProductModerationController {

    private final ProductQueryService productQueryService;

    public ProductModerationController(ProductQueryService productQueryService) {
        this.productQueryService = productQueryService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductListItemDto>> getAll(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(productQueryService.getAll(
                new ProductSearchRequest(query, categoryId),
                pageable
        ));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailDto> getDetail(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(productQueryService.getDetailAboutProduct(productId));
    }
}
