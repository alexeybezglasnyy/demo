package com.example.demo.warehouse.model;

import com.example.demo.api.OrderState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.api.OrderState.PENDING;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "warehouse_orders")
public class WarehouseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OrderState state = PENDING;

    @OneToMany(mappedBy = "warehouseOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<WarehouseOrderItem> items = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "child_order_id")
    private WarehouseOrder childOrder;

}