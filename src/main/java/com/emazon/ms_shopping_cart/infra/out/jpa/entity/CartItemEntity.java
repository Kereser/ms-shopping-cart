package com.emazon.ms_shopping_cart.infra.out.jpa.entity;

import com.emazon.ms_shopping_cart.ConsUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Entity(name = "cart_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = ConsUtils.FALSE, unique = true)
    private Long id;

    @Column(nullable = ConsUtils.FALSE)
    private Long articleId;

    @Column(nullable = false)
    private Long quantity;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false, updatable = false)
    private CartEntity cart;

    public void addQuantity(Long toAdd) {
        if (toAdd < 0) return;
        quantity += toAdd;
    }
}
