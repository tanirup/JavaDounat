package com.example.demo.repository;

import com.example.demo.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // 販売中（isActive = true）の商品一覧を取得
    List<Item> findByIsActiveTrueOrderByCreatedAtDesc();

    // カテゴリー別の販売中商品を取得
    List<Item> findByIsActiveTrueAndCategoryIdOrderByCreatedAtDesc(Long categoryId);

    // おすすめ商品（トップページ掲載用）を取得
    List<Item> findByIsActiveTrueAndIsRecommendedTrueOrderByCreatedAtDesc();

    // 商品名でのキーワード検索（販売中のみ）
    List<Item> findByIsActiveTrueAndNameContainingOrderByCreatedAtDesc(String keyword);

    // 指定したアレルギー品目（複数可）を含まない販売中商品を検索（食品EC向け）
    @Query("SELECT i FROM Item i WHERE i.isActive = true AND i.id NOT IN (" +
           "SELECT i2.id FROM Item i2 JOIN i2.allergens a WHERE a.id IN :allergenIds" +
           ") ORDER BY i.createdAt DESC")
    List<Item> findActiveItemsExcludingAllergens(@Param("allergenIds") List<Long> allergenIds);
}