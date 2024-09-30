package com.emazon.ms_shopping_cart.infra.out.jpa.mapper;

import com.emazon.ms_shopping_cart.domain.model.Cart;
import com.emazon.ms_shopping_cart.domain.model.CartItem;
import com.emazon.ms_shopping_cart.infra.out.jpa.entity.CartEntity;
import com.emazon.ms_shopping_cart.infra.out.jpa.entity.CartItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CartEntityMapper {

    Set<CartItemEntity> cartItemsToCartItemsEntity(Set<CartItem> cartItem);

    CartItemEntity cartItemToCartItemEntity(CartItem cartItem);

    Cart cartEntityToCart(CartEntity entity);

    CartEntity cartToCartEntity(Cart cart);
}
