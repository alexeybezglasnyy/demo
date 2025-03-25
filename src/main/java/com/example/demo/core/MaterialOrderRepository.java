package com.example.demo.core;

import com.example.demo.core.model.MaterialOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialOrderRepository extends JpaRepository<MaterialOrder, Long> {
}