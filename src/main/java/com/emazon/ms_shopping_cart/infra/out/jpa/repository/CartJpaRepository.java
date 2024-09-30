package com.emazon.ms_shopping_cart.infra.out.jpa.repository;

import com.emazon.ms_shopping_cart.infra.out.jpa.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartJpaRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByUserId(Long userId);
}
