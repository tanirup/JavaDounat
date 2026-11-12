package com.example.demo.repository;

import com.example.demo.entity.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

    // ユーザーの登録済み住所一覧を取得（デフォルト設定の住所を先頭に表示）
    List<ShippingAddress> findByUserIdOrderByIsDefaultDescCreatedAtDesc(Long userId);

    // ユーザーのデフォルト配送先を取得
    Optional<ShippingAddress> findByUserIdAndIsDefaultTrue(Long userId);
}