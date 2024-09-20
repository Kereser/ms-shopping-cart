package com.emazon.ms_shopping_cart.domain.spi;

import com.emazon.ms_shopping_cart.domain.model.Cart;
import com.emazon.ms_shopping_cart.infra.security.model.CustomUserDetails;

import java.util.Optional;

public interface ICartPersistencePort {
    Optional<Cart> findByUserId(Long userId);
    void save(Cart cart);
    CustomUserDetails getSecurityPrincipal();
}
