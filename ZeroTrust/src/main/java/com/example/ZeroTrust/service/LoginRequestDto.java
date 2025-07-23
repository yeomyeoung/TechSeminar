package com.example.ZeroTrust.service;

/**
 * JWT 로그인 요청 시 클라이언트가 보내는 userID, password를 담는 DTO
 */
public class LoginRequestDto {
    /**
     * 사용자 ID
     */
    private String userID;
    /**
     * 사용자 비밀번호(평문)
     */
    private String password;

    /**
     * 기본 생성자
     */
    public LoginRequestDto() {}

    /**
     * 모든 필드를 받는 생성자
     */
    public LoginRequestDto(String userID, String password) {
        this.userID = userID;
        this.password = password;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 