package com.emazon.ms_shopping_cart.application.handler;

import com.emazon.ms_shopping_cart.application.dto.CartItemDTO;

import java.util.Set;

public interface ICartHandler {
    void handleAddOperation(Long userId, Set<CartItemDTO> setDTO);
}
