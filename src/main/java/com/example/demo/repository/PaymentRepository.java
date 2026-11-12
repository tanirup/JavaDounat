package com.example.demo.repository;

import com.example.demo.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 注文IDに紐づく決済情報を取得
    Optional<Payment> findByOrderId(Long orderId);

    // 外部決済サービスの取引IDから決済情報を照会
    Optional<Payment> findByTransactionId(String transactionId);
}