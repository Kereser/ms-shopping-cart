package com.emazon.ms_shopping_cart.infra.exception;

import com.emazon.ms_shopping_cart.ConsUtils;

public class ResourceOwnershipViolationException extends BaseEntityException {
    public ResourceOwnershipViolationException() {
        super(null, null, ConsUtils.RESOURCE_OWNING_EXCEPTION);
    }
}
