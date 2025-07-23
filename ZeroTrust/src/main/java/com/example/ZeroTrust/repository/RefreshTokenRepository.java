package com.example.ZeroTrust.repository;

import com.example.ZeroTrust.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RefreshToken 엔티티에 대한 DB 접근을 담당하는 리포지토리 인터페이스
 * 사용자별 refresh 토큰 저장/조회/삭제 기능 제공
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    /**
     * userId로 refresh 토큰 조회
     */
    RefreshToken findByUserId(String userId);
    /**
     * userId로 refresh 토큰 삭제
     */
    void deleteByUserId(String userId);
} 