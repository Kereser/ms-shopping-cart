package com.emazon.ms_shopping_cart.application.dto.out;

import jakarta.validation.Valid;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleDTO {
    private Long userId;
    private Set<@Valid SaleItemDTO> saleArticles;
}
