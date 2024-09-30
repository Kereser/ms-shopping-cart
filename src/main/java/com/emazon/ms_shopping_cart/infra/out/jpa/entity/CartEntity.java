package com.emazon.ms_shopping_cart.infra.out.jpa.entity;

import com.emazon.ms_shopping_cart.ConsUtils;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = ConsUtils.FALSE, unique = true)
    private Long id;

    @Column(nullable = ConsUtils.FALSE)
    private Long userId;

    @Column(nullable = ConsUtils.FALSE)
    @OneToMany(mappedBy = "cart", orphanRemoval = true, cascade = CascadeType.ALL)
    @Builder.Default
    private Set<CartItemEntity> cartItems = new HashSet<>();

    @Column(nullable = ConsUtils.FALSE)
    private BigDecimal totalPrice;

    @Column(nullable = ConsUtils.FALSE, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = ConsUtils.FALSE)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void addCartItems(Set<CartItemEntity> cartItems) {
        this.cartItems.addAll(cartItems);
        cartItems.forEach(ci -> ci.setCart(this));
    }
}
