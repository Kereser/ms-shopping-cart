package com.emazon.ms_shopping_cart.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Cart {

    private Long id;
    private Long userId;
    private Set<CartItem> cartItems = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Cart() {
    }

    public Cart(Long id, Long userId, Set<CartItem> cartItems, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.cartItems = cartItems;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCartItems(Set<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public Set<CartItem> getCartItems() {
        return cartItems;
    }

    public void addItems(Set<CartItem> cartItems) {
        this.cartItems.addAll(cartItems);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
