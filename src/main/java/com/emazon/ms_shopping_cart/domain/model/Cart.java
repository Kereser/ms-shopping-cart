package com.emazon.ms_shopping_cart.domain.model;

import java.time.LocalDate;

public class Cart {

    private Long id;

    private Long articleId;

    private Long userId;

    private Long quantity;

    private LocalDate updatedAt;

    private LocalDate createdAt;

    public Cart() {
    }

    public Cart(Long id, Long articleId, Long userId, Long quantity, LocalDate updatedAt, LocalDate createdAt) {
        this.id = id;
        this.articleId = articleId;
        this.userId = userId;
        this.quantity = quantity;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
