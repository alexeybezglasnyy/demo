package com.example.demo.warehouse;

import com.example.demo.warehouse.model.WarehouseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseOrderRepository extends JpaRepository<WarehouseOrder, Long> {
}