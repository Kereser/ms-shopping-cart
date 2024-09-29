package com.emazon.ms_shopping_cart.application.dto.out;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemDTO {
    private Long articleId;
    private Long quantity;
}
