package com.example.demo.core;

import com.example.demo.api.OrderState;
import com.example.demo.core.model.MaterialOrder;
import com.example.demo.core.model.MaterialOrderItem;
import com.example.demo.core.validator.WarehouseOrderValidator;
import com.example.demo.core.validator.WarehouseStateException;
import com.example.demo.warehouse.model.WarehouseOrder;
import com.example.demo.warehouse.model.WarehouseOrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.demo.api.OrderState.*;
import static com.example.demo.utils.Constants.WAREHOUSE_SYNC_ERROR;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Component
@Slf4j
public class MaterialOrderService {

    private final MaterialOrderFactory materialOrderFactory;
    private final WarehouseOrderValidator warehouseOrderValidator;

    public MaterialOrderService(MaterialOrderFactory materialOrderFactory, WarehouseOrderValidator warehouseOrderValidator) {
        this.materialOrderFactory = materialOrderFactory;
        this.warehouseOrderValidator = warehouseOrderValidator;
    }

    @Transactional
    public ResponseEntity<?> synchronizeState(WarehouseOrder warehouseOrder) {
        var validationResult = warehouseOrderValidator.validate(warehouseOrder);
        if (!validationResult.isValid()) {
            return ResponseEntity.status(UNPROCESSABLE_ENTITY).body(validationResult);
        }

        var storedMaterialOrder = materialOrderFactory.findMaterialOrderByVendorReferenceId(warehouseOrder.getId());
        if (storedMaterialOrder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        var rootOrderAfterSync = syncOrders(warehouseOrder, storedMaterialOrder.get());

        //I would not return full MaterialOrder to Warehouse in real case, OK/NOK plus triggered events should be enough
        //Body is filled for faster manual testing
        return ResponseEntity.status(HttpStatus.OK).body(rootOrderAfterSync);
    }

    private MaterialOrder syncOrders(WarehouseOrder warehouseOrder, MaterialOrder storedOrder) {
        log.info("Sync started for MaterialOrder with vendorReferenceId " + storedOrder.getVendorReferenceId());
        var storedQuantities = collectPartiallyDeliveredMaterialQuantities(storedOrder);
        var pendingQuantities = collectPendingMaterialQuantities(storedOrder);
        //Assumption - the info about these delivered items is true and is synchronized already
        log.info("Synchronized Material items delivered before: " + storedQuantities);
        log.info("Pending Material items before sync: " + pendingQuantities);

        Map<String, Integer> zeroedCodeMap = getCopyWithZeroedValues(storedQuantities);
        var currentWarehouseOrder = findWHOrderForSync(warehouseOrder, storedQuantities, zeroedCodeMap);
        if (!zeroedCodeMap.equals(storedQuantities)) {
            log.info(zeroedCodeMap.toString());
            log.info(storedQuantities.toString());
            throw new WarehouseStateException(String.format(WAREHOUSE_SYNC_ERROR, warehouseOrder.getId()));
        }

        var logmsg = "First WH Order which should be syncronized: id={}, state={}";
        log.info(logmsg, currentWarehouseOrder.getId(), currentWarehouseOrder.getState());

        var storedPendingOrder = findPendingMaterialOrder(storedOrder);

        var lastWarehouseOrder = syncPartiallyDeliveredOrders(currentWarehouseOrder, storedPendingOrder);
        syncLastWHOrder(lastWarehouseOrder, storedPendingOrder);

        return storedOrder;
    }

    private static void syncLastWHOrder(WarehouseOrder warehouseOrder, MaterialOrder materialOrder) {
        if (warehouseOrder == null) {
            materialOrder.setState(DELIVERED);
            return;
        }

        var newPendingOrder = buildPendingMaterialOrder(warehouseOrder, materialOrder);
        materialOrder.setChildOrder(newPendingOrder);
        //Assumption: don't forget to save changes in Materials DB
        log.info("SYNC RESULT - created new pending order: " + newPendingOrder);
    }

    private static MaterialOrder buildPendingMaterialOrder(WarehouseOrder warehouseOrder, MaterialOrder materialOrder) {
        List<MaterialOrderItem> newPendingItems = buildPendingMaterialOrderItems(warehouseOrder, materialOrder);

        return MaterialOrder.builder()
                .state(PENDING)
                .vendorReferenceId(warehouseOrder.getId().toString())
                .items(newPendingItems)
                .build();
    }

    private static List<MaterialOrderItem> buildPendingMaterialOrderItems(WarehouseOrder warehouseOrder, MaterialOrder materialOrder) {
        List<MaterialOrderItem> newPendingItems = new ArrayList<>();
        for (WarehouseOrderItem item : warehouseOrder.getItems()) {
            var newPendingItem = MaterialOrderItem.builder()
                    .code(item.getCode())
                    .quantity(item.getAmount())
                    .finalizationReportItem(getCurrentFinalizationReport(materialOrder))
                    .build();
            newPendingItems.add(newPendingItem);
        }
        return newPendingItems;
    }

    private static WarehouseOrder syncPartiallyDeliveredOrders(WarehouseOrder warehouseOrder, MaterialOrder storedPendingOrder) {
        var currentFinalizationReport = getCurrentFinalizationReport(storedPendingOrder);
        var shouldGoDeep = true;
        var lastWarehouseOrder = warehouseOrder;
        List<MaterialOrderItem> newPartiallyDeliveredItems = new ArrayList<>();
        while (shouldGoDeep) {
            if (lastWarehouseOrder.getChildOrder().getState() == PENDING || lastWarehouseOrder.getState() == DELIVERED) {
                shouldGoDeep = false;
            }
            for (WarehouseOrderItem whItem : lastWarehouseOrder.getItems()) {
                MaterialOrderItem newItem = MaterialOrderItem.builder()
                        .code(whItem.getCode())
                        .quantity(whItem.getAmount())
                        .finalizationReportItem(currentFinalizationReport)
                        .build();
                newPartiallyDeliveredItems.add(newItem);
            }
            lastWarehouseOrder = lastWarehouseOrder.getChildOrder();
        }

        //Assumption: don't forget to save changes later in Materials DB
        storedPendingOrder.setItems(newPartiallyDeliveredItems);
        storedPendingOrder.setState(PARTIALLY_DELIVERED);
        log.info("SYNC RESULT - updated existing order: " + storedPendingOrder);
        return lastWarehouseOrder;
    }

    private static String getCurrentFinalizationReport(MaterialOrder storedPendingOrder) {
        return storedPendingOrder.getItems().getLast().getFinalizationReportItem();
    }

    private static MaterialOrder findPendingMaterialOrder(MaterialOrder storedOrder) {
        var currentStoredOrder = storedOrder;
        while (currentStoredOrder.getState() != OrderState.PENDING) {
            currentStoredOrder = currentStoredOrder.getChildOrder();
        }
        return currentStoredOrder;
    }

    private static Map<String, Integer> getCopyWithZeroedValues(HashMap<String, Integer> storedQuantities) {
        return storedQuantities.keySet().stream().collect(Collectors.toMap(key -> key, value -> 0));
    }

    private WarehouseOrder findWHOrderForSync(WarehouseOrder warehouseOrder, HashMap<String, Integer> storedQuantities, Map<String, Integer> zeroedCodeMap) {
        var currentWarehouseOrder = warehouseOrder;
        while (currentWarehouseOrder.getChildOrder() != null) {
            if (storedQuantities.equals(zeroedCodeMap)) {
                break;
            }
            currentWarehouseOrder = fillQuantitiesAndGetChild(currentWarehouseOrder, zeroedCodeMap);
        }
        return currentWarehouseOrder;
    }

    private WarehouseOrder fillQuantitiesAndGetChild(WarehouseOrder warehouseOrder, Map<String, Integer> targetMap) {
        if (warehouseOrder == null) {
            return null;
        }

        for (WarehouseOrderItem item : warehouseOrder.getItems()) {
            var code = item.getCode();
            if (!targetMap.containsKey(code)) {
                var msg = "WH PROBLEM, unexpected item with code %s, orderId %d";
                throw new WarehouseStateException(String.format(msg, code, warehouseOrder.getId()));
            }
            targetMap.merge(code, item.getAmount(), Integer::sum);
        }
        return warehouseOrder.getChildOrder();
    }

    private static HashMap<String, Integer> collectPartiallyDeliveredMaterialQuantities(MaterialOrder order) {
        HashMap<String, Integer> itemMap = new HashMap<>();
        collectOrderQuantities(order, itemMap);
        return itemMap;
    }

    private static HashMap<String, Integer> collectPendingMaterialQuantities(MaterialOrder order) {
        HashMap<String, Integer> resultMap = new HashMap<>();
        getQuantitiesFromPendingChild(order, resultMap);
        return resultMap;
    }

    private static void collectOrderQuantities(MaterialOrder order, Map<String, Integer> itemMap) {
        if (order == null) {
            return;
        }

        for (MaterialOrderItem item : order.getItems()) {
            itemMap.merge(item.getCode(), item.getQuantity(), Integer::sum);
        }

        if (order.getChildOrder() != null && order.getChildOrder().getState() == PARTIALLY_DELIVERED) {
            collectOrderQuantities(order.getChildOrder(), itemMap);
        }
    }

    private static void getQuantitiesFromPendingChild(MaterialOrder order, Map<String, Integer> map) {
        if (order == null) {
            return;
        }

        if (order.getState() == PENDING) {
            for (MaterialOrderItem item : order.getItems()) {
                map.merge(item.getCode(), item.getQuantity(), Integer::sum);
            }
            return;
        }

        if (order.getChildOrder() != null) {
            getQuantitiesFromPendingChild(order.getChildOrder(), map);
        }
    }
}