package com.emazon.ms_shopping_cart.application.dto.out;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryArticleResDTO {
    private Long id;
    private String name;
}
