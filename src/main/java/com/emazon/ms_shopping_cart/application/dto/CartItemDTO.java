package com.emazon.ms_shopping_cart.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    @NotNull
    @Positive
    private Long articleId;

    @NotNull
    @Positive
    private Long quantity;
}
