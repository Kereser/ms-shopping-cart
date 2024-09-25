package com.emazon.ms_shopping_cart.application.dto.input;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticlesPriceDTO {
    private Long id;
    private BigDecimal price;
    private Long quantity;
}
