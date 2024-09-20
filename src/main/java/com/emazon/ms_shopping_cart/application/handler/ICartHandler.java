package com.emazon.ms_shopping_cart.application.handler;

import com.emazon.ms_shopping_cart.application.dto.CartItemDTO;

import java.util.Set;

public interface ICartHandler {
    void handleAddOperation(Set<CartItemDTO> setDTO);
    void handleDeletionFromCart(Long cartId, Long articleId);
}
