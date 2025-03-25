package com.example.demo.core.model;

import com.example.demo.api.OrderState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "core_orders")
public class MaterialOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OrderState state = OrderState.PENDING;

    private String vendorReferenceId;

    @OneToMany(mappedBy = "materialOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MaterialOrderItem> items = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "child_order_id")
    private MaterialOrder childOrder;

}