package com.example.demo.repository;

import com.example.demo.model.OrderHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistoryEntry, Long> {
    List<OrderHistoryEntry> findAllByOrderByIdDesc();
}