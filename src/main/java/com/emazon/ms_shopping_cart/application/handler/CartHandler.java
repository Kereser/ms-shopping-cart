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
    public void handleAddOperation(Long userId, Set<CartItemDTO> setDTO) {
        Cart cart = new Cart();

        cart.setUserId(userId);
        cart.addItems(mapper.cartItemsReqToCartItems(setDTO));

        cartServicePort.handleAddOperation(cart);
    }
}
