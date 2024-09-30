package com.emazon.ms_shopping_cart.infra.exception;

public class PurchaseFailedException extends BaseEntityException {
    public PurchaseFailedException(String entity, String field, String reason) {
        super(entity, field, reason);
    }
}
