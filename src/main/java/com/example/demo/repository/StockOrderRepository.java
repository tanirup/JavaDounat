package com.example.demo.repository;

import com.example.demo.model.StockOrderEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockOrderRepository extends JpaRepository<StockOrderEntry, Long> {
    List<StockOrderEntry> findAllByOrderByIdDesc();
}