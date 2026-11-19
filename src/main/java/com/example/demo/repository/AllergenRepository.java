package com.example.demo.repository;

import com.example.demo.entity.Allergen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllergenRepository extends JpaRepository<Allergen, Long> {

    Optional<Allergen> findByName(String name);

    // ID順（表示順）に全アレルギー品目を取得
    List<Allergen> findAllByOrderByIdAsc();
}