package com.emazon.ms_shopping_cart.application.handler;

import com.emazon.ms_shopping_cart.application.dto.CartItemDTO;
import com.emazon.ms_shopping_cart.application.dto.out.ArticleResDTO;
import com.emazon.ms_shopping_cart.application.dto.handlers.PageDTO;
import com.emazon.ms_shopping_cart.application.mapper.CartDTOMapper;
import com.emazon.ms_shopping_cart.domain.api.ICartServicePort;
import com.emazon.ms_shopping_cart.domain.model.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class CartHandler implements ICartHandler {

    private final ICartServicePort cartServicePort;
    private final CartDTOMapper mapper;

    @Override
    public void handleAddOperation(Set<CartItemDTO> cartItemDTOS) {
        Cart cart = new Cart();

        cart.addItems(mapper.cartItemsReqToCartItems(cartItemDTOS));

        cartServicePort.handleAddOperation(cart);
    }

    @Override
    public void handleDeletionFromCart(Long cartId, Long articleId) {
        cartServicePort.deleteArticleFromCart(cartId, articleId);
    }

    @Override
    public PageDTO<ArticleResDTO> getAllCartItems(String direction, Integer pageSize, Integer page, String columns) {
        return cartServicePort.getAllCartItems(direction, pageSize, page, columns);
    }

    @Override
    public void buyCart() {
        cartServicePort.buyCart();
    }
}
