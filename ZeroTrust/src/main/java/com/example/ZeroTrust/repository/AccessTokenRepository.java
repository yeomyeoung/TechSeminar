package com.example.ZeroTrust.repository;

import com.example.ZeroTrust.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * AccessToken 엔티티에 대한 DB 접근을 담당하는 리포지토리 인터페이스
 * 사용자별 access 토큰 저장/조회/삭제 기능 제공
 */
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
    /**
     * userId로 access 토큰 조회
     */
    AccessToken findByUserId(String userId);
    /**
     * userId로 access 토큰 삭제
     */
    void deleteByUserId(String userId);
} 