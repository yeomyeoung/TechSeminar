package com.example.ZeroTrust.service;

/**
 * 로그인 성공 시 accessToken, refreshToken을 클라이언트에 반환하기 위한 DTO
 */
public class LoginResponseDto {
    /**
     * 발급된 access 토큰
     */
    private String accessToken;
    /**
     * 발급된 refresh 토큰
     */
    private String refreshToken;

    /**
     * 생성자: accessToken, refreshToken을 받아서 초기화
     */
    public LoginResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
} 