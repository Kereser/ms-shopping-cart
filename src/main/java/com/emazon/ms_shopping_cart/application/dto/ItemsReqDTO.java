package com.emazon.ms_shopping_cart.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemsReqDTO {

    @NotNull
    @Size(min = 1)
    private Set<@Valid CartItemDTO> items;

    public void addAll(Set<CartItemDTO> items) {
        this.items.addAll(items);
    }
}
