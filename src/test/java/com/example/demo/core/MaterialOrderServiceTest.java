package com.example.demo.core;

import com.example.demo.core.model.MaterialOrder;
import com.example.demo.core.validator.WarehouseOrderValidator;
import com.example.demo.warehouse.model.WarehouseOrder;
import com.example.demo.warehouse.model.WarehouseOrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.api.OrderState.PARTIALLY_DELIVERED;
import static com.example.demo.api.OrderState.PENDING;
import static com.example.demo.core.MaterialOrderFactory.CODE1;
import static com.example.demo.core.MaterialOrderFactory.CODE2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaterialOrderServiceTest {

    @Test
    void synchronisationIsOK() {
        var service = new MaterialOrderService(new MaterialOrderFactory(), new WarehouseOrderValidator());
        var warehouseOrder = getTestOrder();

        var result = service.synchronizeState(warehouseOrder);


        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertThat(result.getBody()).isInstanceOf(MaterialOrder.class);

        MaterialOrder root = (MaterialOrder) result.getBody();
        var child1 = root.getChildOrder();
        var child2 = child1.getChildOrder();
        var child3 = child2.getChildOrder();
        var child4 = child3.getChildOrder();


        assertThat(root).satisfies(child -> {
            assertEquals("1", child.getVendorReferenceId());
            assertEquals(PARTIALLY_DELIVERED, child.getState());
            assertThat(child.getItems()).hasSize(3);
            assertEquals(CODE1, child.getItems().get(0).getCode());
            assertEquals(7, child.getItems().get(0).getQuantity());
            assertEquals(CODE1, child.getItems().get(1).getCode());
            assertEquals(8, child.getItems().get(1).getQuantity());
            assertEquals(CODE2, child.getItems().get(2).getCode());
            assertEquals(3, child.getItems().get(2).getQuantity());

        });
        assertThat(child1).satisfies(child -> {
            assertEquals("2", child.getVendorReferenceId());
            assertEquals(PARTIALLY_DELIVERED, child.getState());
            assertThat(child.getItems()).hasSize(2);
            assertEquals(CODE1, child.getItems().get(0).getCode());
            assertEquals(5, child.getItems().get(0).getQuantity());
            assertEquals(CODE2, child.getItems().get(1).getCode());
            assertEquals(5, child.getItems().get(1).getQuantity());
        });
        assertThat(child2).satisfies(child -> {
            assertEquals("3", child.getVendorReferenceId());
            assertEquals(PARTIALLY_DELIVERED, child.getState());
            assertThat(child.getItems()).hasSize(4);
            assertEquals(CODE1, child.getItems().get(0).getCode());
            assertEquals(6, child.getItems().get(0).getQuantity());
            assertEquals(CODE2, child.getItems().get(1).getCode());
            assertEquals(4, child.getItems().get(1).getQuantity());
            assertEquals(CODE1, child.getItems().get(2).getCode());
            assertEquals(1, child.getItems().get(2).getQuantity());
            assertEquals(CODE2, child.getItems().get(3).getCode());
            assertEquals(2, child.getItems().get(3).getQuantity());
        });
        assertThat(child3).satisfies(child -> {
            assertEquals("5", child.getVendorReferenceId());
            assertEquals(PENDING, child.getState());
            assertThat(child.getItems()).hasSize(2);
            assertEquals(CODE1, child.getItems().get(0).getCode());
            assertEquals(3, child.getItems().get(0).getQuantity());
            assertEquals(CODE2, child.getItems().get(1).getCode());
            assertEquals(4, child.getItems().get(1).getQuantity());
        });
        assertThat(child4).isNull();
    }

    private WarehouseOrder getTestOrder() {
        List<WarehouseOrderItem> pendingItems = new ArrayList<>();
        pendingItems.add(new WarehouseOrderItem(CODE1, 3));
        pendingItems.add(new WarehouseOrderItem(CODE2, 4));
        var pendingChild = new WarehouseOrder(5L, PENDING, pendingItems, null);

        List<WarehouseOrderItem> items4 = new ArrayList<>();
        items4.add(new WarehouseOrderItem(CODE1, 1));
        items4.add(new WarehouseOrderItem(CODE2, 2));
        var child4 = new WarehouseOrder(4L, PARTIALLY_DELIVERED, items4, pendingChild);

        List<WarehouseOrderItem> items3 = new ArrayList<>();
        items3.add(new WarehouseOrderItem(CODE1, 6));
        items3.add(new WarehouseOrderItem(CODE2, 4));
        var child3 = new WarehouseOrder(3L, PARTIALLY_DELIVERED, items3, child4);

        List<WarehouseOrderItem> items2 = new ArrayList<>();
        items2.add(new WarehouseOrderItem(CODE1, 5));
        items2.add(new WarehouseOrderItem(CODE2, 5));
        var child2 = new WarehouseOrder(2L, PARTIALLY_DELIVERED, items2, child3);

        List<WarehouseOrderItem> items = new ArrayList<>();
        items.add(new WarehouseOrderItem(CODE1, 15));
        items.add(new WarehouseOrderItem(CODE2, 3));
        return new WarehouseOrder(1L, PARTIALLY_DELIVERED, items, child2);
    }
}