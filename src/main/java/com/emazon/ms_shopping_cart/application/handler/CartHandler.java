package com.emazon.ms_shopping_cart.application.handler;

import com.emazon.ms_shopping_cart.application.dto.CartItemDTO;
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
}
