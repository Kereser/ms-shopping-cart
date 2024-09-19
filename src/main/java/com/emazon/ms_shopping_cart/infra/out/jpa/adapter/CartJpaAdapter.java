package com.emazon.ms_shopping_cart.infra.out.jpa.adapter;

import com.emazon.ms_shopping_cart.domain.model.Cart;
import com.emazon.ms_shopping_cart.domain.spi.ICartPersistencePort;
import com.emazon.ms_shopping_cart.infra.out.jpa.entity.CartEntity;
import com.emazon.ms_shopping_cart.infra.out.jpa.mapper.CartEntityMapper;
import com.emazon.ms_shopping_cart.infra.out.jpa.repository.CartJpaRepository;
import com.emazon.ms_shopping_cart.infra.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
public class CartJpaAdapter implements ICartPersistencePort {

    private final CartJpaRepository repository;
    private final CartEntityMapper mapper;

    @Override
    public Optional<Cart> findByUserId(Long userId) {
        Optional<CartEntity> opt = repository.findByUserId(userId);
        return opt.map(mapper::cartEntityToCart);
    }

    @Override
    public void save(Cart cart) {
        CartEntity entity = mapper.cartToCartEntity(cart);

        entity.addCartItems(entity.getCartItems());
        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity);
    }

    @Override
    public CustomUserDetails getSecurityContext() {
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
