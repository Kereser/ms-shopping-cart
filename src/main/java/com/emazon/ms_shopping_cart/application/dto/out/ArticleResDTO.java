package com.emazon.ms_shopping_cart.application.dto.out;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@FieldNameConstants
public class ArticleResDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long quantity;
    private Long cartQuantity;
    private Set<CategoryArticleResDTO> categories;
    private BrandResDTO brand;
    private LocalDateTime updatedAt;
}
