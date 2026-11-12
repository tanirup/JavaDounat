package com.example.demo.repository;

import com.example.demo.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 特定ユーザーのカート内アイテム一覧を取得
    List<CartItem> findByUserIdOrderByCreatedAtAsc(Long userId);

    // すでに同じ商品がカートに入っているか確認（数量加算用）
    Optional<CartItem> findByUserIdAndItemId(Long userId, Long itemId);

    // 注文完了後にユーザーのカートを空にする（※Service側で @Transactional が必要）
    void deleteByUserId(Long userId);
}