package com.emazon.ms_shopping_cart.infra.exception;

public class NoDataFoundException extends BaseEntityException {
    public NoDataFoundException(String entityName, String field) {
      super(entityName, field);
    }
}
