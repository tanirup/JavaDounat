package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ログイン認証やユーザー検索用
    Optional<User> findByEmail(String email);

    // 新規会員登録時のメールアドレス重複チェック用
    boolean existsByEmail(String email);
}