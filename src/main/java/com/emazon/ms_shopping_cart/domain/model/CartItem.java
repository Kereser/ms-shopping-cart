package com.emazon.ms_shopping_cart.domain.model;

public class CartItem {
    private Long id;
    private Long articleId;
    private Long quantity;

    public CartItem() {
    }

    public CartItem(Long id, Long articleId, Long quantity) {
        this.id = id;
        this.articleId = articleId;
        this.quantity = quantity;
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

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(Long toAdd) {
        if (toAdd < 0) return;
        this.quantity += toAdd;
    }
}
