package com.example.demo.core;

import com.example.demo.api.OrderState;
import com.example.demo.core.model.MaterialOrder;
import com.example.demo.core.model.MaterialOrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.demo.api.OrderState.PARTIALLY_DELIVERED;
import static com.example.demo.api.OrderState.PENDING;

@Component
@Slf4j
public class MaterialOrderFactory {
    public final static String CODE1 = "123";
    public final static String CODE2 = "456";
    public final static String FR1 = "FR1";
    public final static String FR2 = "FR2";

    public Optional<MaterialOrder> findMaterialOrderByVendorReferenceId(Long id) {
        if ("1".equals(id.toString())) {
            return Optional.of(getStoredOrder());
        }
        return Optional.empty();
    }

    private static MaterialOrder getStoredOrder() {
        var mainOrderItem1 = createMaterialOrderItem(CODE1, 7, FR1);
        var mainOrderItem2 = createMaterialOrderItem(CODE1, 8, FR2);
        var mainOrderItem3 = createMaterialOrderItem(CODE2, 3, FR2);
        var mainOrderItems = new ArrayList<>(List.of(mainOrderItem1, mainOrderItem2, mainOrderItem3));

        var childOrderItem1 = createMaterialOrderItem(CODE1, 5, FR2);
        var childOrderItem2 = createMaterialOrderItem(CODE2, 5, FR2);
        var childOrderItems = new ArrayList<>(List.of(childOrderItem1, childOrderItem2));

        var lastChildOrderItem1 = createMaterialOrderItem(CODE1, 10, FR2);
        var lastChildOrderItem2 = createMaterialOrderItem(CODE2, 10, FR2);
        var lastChildOrderItems = new ArrayList<>(List.of(lastChildOrderItem1, lastChildOrderItem2));


        var lastChild = createMaterialOrder(PENDING, "3", lastChildOrderItems, null);
        var child = createMaterialOrder(PARTIALLY_DELIVERED, "2", childOrderItems, lastChild);
        return createMaterialOrder(PARTIALLY_DELIVERED, "1", mainOrderItems, child);
    }

    private static MaterialOrder createMaterialOrder(OrderState state, String id, ArrayList<MaterialOrderItem> items, MaterialOrder child) {
        return MaterialOrder.builder()
                .state(state)
                .vendorReferenceId(id)
                .items(items)
                .childOrder(child)
                .build();
    }

    private static MaterialOrderItem createMaterialOrderItem(String code, Integer amount, String finalizationReport) {
        return MaterialOrderItem.builder()
                .code(code)
                .quantity(amount)
                .finalizationReportItem(finalizationReport)
                .build();
    }
}