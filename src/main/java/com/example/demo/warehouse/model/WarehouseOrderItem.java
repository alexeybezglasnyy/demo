package com.example.demo.warehouse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "warehouse_order_items")
public class WarehouseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private int amount;

    @ManyToOne
    @JoinColumn(name = "warehouse_order_id")
    private WarehouseOrder warehouseOrder;

    public WarehouseOrderItem(String code, int amount) {
        this.code = code;
        this.amount = amount;
    }
}