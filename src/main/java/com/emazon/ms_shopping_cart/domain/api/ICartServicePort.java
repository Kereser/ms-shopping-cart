package com.emazon.ms_shopping_cart.domain.api;

import com.emazon.ms_shopping_cart.domain.model.Cart;

public interface ICartServicePort {
    void handleAddOperation(Cart dto);
    void save(Cart cart);
    void deleteArticleFromCart(Long cartId, Long articleId);
}
