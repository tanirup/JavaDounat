package com.example.demo.repository;

import com.example.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 会員マイページ用：自分の注文履歴を新しい順に取得
    List<Order> findByUserIdOrderByOrderedAtDesc(Long userId);

    // 注文番号（例: ORD-20261112-0001）での照会用
    Optional<Order> findByOrderNumber(String orderNumber);

    // 管理者画面用：全ユーザーの注文一覧を新しい順に取得
    List<Order> findAllByOrderByOrderedAtDesc();

    // 管理者画面用：ステータス別（PENDING, PAID, SHIPPED 等）の絞り込み
    List<Order> findByStatusOrderByOrderedAtDesc(String status);
}