package com.emazon.ms_shopping_cart.application.handler;

import com.emazon.ms_shopping_cart.application.dto.CartItemDTO;
import com.emazon.ms_shopping_cart.application.dto.out.ArticleResDTO;
import com.emazon.ms_shopping_cart.application.dto.handlers.PageDTO;

import java.util.Set;

public interface ICartHandler {
    void handleAddOperation(Set<CartItemDTO> setDTO);
    void handleDeletionFromCart(Long cartId, Long articleId);
    PageDTO<ArticleResDTO> getAllCartItems(String direction, Integer pageSize, Integer page, String columns, Long cartId);
}
