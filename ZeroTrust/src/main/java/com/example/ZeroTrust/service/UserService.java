package com.example.ZeroTrust.service;

import com.example.ZeroTrust.entity.AccessToken;
import com.example.ZeroTrust.entity.RefreshToken;
import com.example.ZeroTrust.entity.User;
import com.example.ZeroTrust.repository.AccessTokenRepository;
import com.example.ZeroTrust.repository.RefreshTokenRepository;
import com.example.ZeroTrust.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // JWT 유틸리티 및 토큰 저장소 주입
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AccessTokenRepository accessTokenRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    /**
     * 로그인 메서드: userID와 password 검증 후 JWT 토큰 발급 및 DB 저장, DTO 반환
     * @param userID 사용자 ID
     * @param rawPassword 입력 비밀번호(평문)
     * @return LoginResponseDto (access, refresh 토큰) 또는 null(실패)
     */
    public LoginResponseDto loginUser(String userID, String rawPassword) {
        // 🔥 입력 값 디버깅
        System.out.println("[DEBUG] 입력된 ID: " + userID);
        System.out.println("[DEBUG] 입력된 PW: " + rawPassword);

        // 1. DB에서 사용자 조회
        User user = userRepository.findByUserID(userID);
        if (user == null) {
            System.out.println("[DEBUG] DB에서 해당 ID를 찾지 못했습니다.");
            return null; // 존재하지 않음
        }

        // 2. 비밀번호 검증
        boolean passwordMatch = passwordEncoder.matches(rawPassword, user.getPassword());
        System.out.println("[DEBUG] 비밀번호 매칭 결과: " + passwordMatch);
        if (!passwordMatch) {
            System.out.println("[DEBUG] 비밀번호 불일치!");
            return null; // 비밀번호 불일치
        }

        // 3. 기존 토큰 삭제 (동일 userId)
        accessTokenRepository.deleteByUserId(userID);
        refreshTokenRepository.deleteByUserId(userID);
        System.out.println("[DEBUG] 기존 토큰 삭제 완료");

        // 4. JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(userID, user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(userID);
        System.out.println("[DEBUG] accessToken 생성: " + accessToken);
        System.out.println("[DEBUG] refreshToken 생성: " + refreshToken);

        // 5. 토큰 만료일 계산
        Date now = new Date();
        Date accessExpiry = new Date(now.getTime() + 1000 * 60 * 15); // 15분
        Date refreshExpiry = new Date(now.getTime() + 1000L * 60 * 60 * 24 * 7); // 7일

        // 6. 토큰 DB 저장
        accessTokenRepository.save(new AccessToken(userID, accessToken, accessExpiry));
        refreshTokenRepository.save(new RefreshToken(userID, refreshToken, refreshExpiry));
        System.out.println("[DEBUG] 토큰 DB 저장 완료");

        // 7. 토큰 DTO로 반환
        return new LoginResponseDto(accessToken, refreshToken);
    }
}
