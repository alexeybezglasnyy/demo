package com.example.demo.core.validator;

import com.example.demo.warehouse.model.WarehouseOrder;
import com.example.demo.warehouse.model.WarehouseOrderItem;
import org.springframework.stereotype.Component;

import static com.example.demo.core.validator.WarehouseOrderValidatonResult.*;

@Component
public class WarehouseOrderValidator implements DemoValidator<WarehouseOrder> {

    public WarehouseOrderValidatonResult validate(WarehouseOrder order) {
        if (!orderIdIsOk(order)) {
            return MISSING_ORDER_ID_FAILURE;
        }

        if (!orderItemsAreOk(order)) {
            return ORDER_ITEMS_FAILURE;
        }

        //space for other validations
        return SUCCESS;
    }

    private boolean orderIdIsOk(WarehouseOrder order) {
        if (order.getId() == null) {
            return false;
        }
        if (order.getChildOrder() == null) {
            return true;
        }
        return orderIdIsOk(order.getChildOrder());
    }

    private boolean orderItemsAreOk(WarehouseOrder order) {
        // simple validation for orderItems, should be extracted to new OrderItemValidator and validated properly if needed
        var result = true;
        for (WarehouseOrderItem item : order.getItems()) {
            if (item.getCode() == null || item.getCode().isEmpty()) {
                result = false;
                break;
            }
        }
        return result;
    }


}
