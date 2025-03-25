package com.example.demo.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "core_order_items")
public class MaterialOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    private int quantity;

    private String finalizationReportItem;

    @ManyToOne
    @JoinColumn(name = "core_order_id")
    private MaterialOrder materialOrder;
}