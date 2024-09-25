package com.emazon.ms_shopping_cart.domain.api;

import com.emazon.ms_shopping_cart.application.dto.handlers.PageDTO;
import com.emazon.ms_shopping_cart.application.dto.out.ArticleResDTO;
import com.emazon.ms_shopping_cart.domain.model.Cart;

public interface ICartServicePort {
    void handleAddOperation(Cart dto);
    void save(Cart cart);
    void deleteArticleFromCart(Long cartId, Long articleId);
    PageDTO<ArticleResDTO> getAllCartItems(String direction, Integer pageSize, Integer page, String columns, Long cartId);
    Cart findById(Long id);
}
