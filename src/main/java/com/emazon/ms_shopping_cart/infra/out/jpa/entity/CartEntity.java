package com.emazon.ms_shopping_cart.infra.out.jpa.entity;

import com.emazon.ms_shopping_cart.ConsUtils;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;

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
    private Long articleId;

    @Column(nullable = ConsUtils.FALSE)
    private Long userId;

    @Column(nullable = ConsUtils.FALSE)
    private Long quantity;

    private LocalDate updatedAt;

    @Column(nullable = ConsUtils.FALSE)
    private LocalDate createdAt;
}
