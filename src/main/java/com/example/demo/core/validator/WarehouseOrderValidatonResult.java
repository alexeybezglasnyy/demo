package com.example.demo.core.validator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WarehouseOrderValidatonResult {
    SUCCESS("Validation successful"),
    MISSING_ORDER_ID_FAILURE("Cannot synchronize order, order ID is missing"),
    ORDER_ITEMS_FAILURE("TODO: extract to OrderItemValidator and finish later if needed");

    private final String description;

    public boolean isValid() {
        return this == SUCCESS;
    }
}
