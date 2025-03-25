package com.example.demo.core;

import com.example.demo.warehouse.model.WarehouseOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/order")
public class MaterialOrderController {

    private final MaterialOrderService materialOrderService;

    public MaterialOrderController(MaterialOrderService materialOrderService) {
        this.materialOrderService = materialOrderService;
    }

    @PostMapping("/sync")
    public ResponseEntity<?> synchronizeState(@RequestBody WarehouseOrder warehouseOrder) {
        return materialOrderService.synchronizeState(warehouseOrder);
    }

}
