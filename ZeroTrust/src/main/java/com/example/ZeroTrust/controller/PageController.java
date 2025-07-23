package com.example.ZeroTrust.controller;


import com.example.ZeroTrust.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    // 홈 페이지
    @GetMapping("/home")
    public String home() {
        return "home"; // home.html 반환
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html 반환
    }

    // 로그인 처리 (폼 방식) - JWT 도입으로 주석 처리
    /*
    @PostMapping("/login")
    public String doLogin(@RequestParam String userID,
                          @RequestParam String password,
                          Model model) {
        // 🔥 입력값 확인
        System.out.println("[DEBUG] 입력된 ID: " + userID);
        System.out.println("[DEBUG] 입력된 PW: " + password);

        User user = userService.loginUser(userID, password);
        if (user == null) {
            model.addAttribute("error", "ID 또는 비밀번호가 잘못되었습니다.");
            return "login_page";
        }

        // 관리자면 Kibana로 리다이렉트
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            System.out.println("[DEBUG] 관리자 로그인 → Kibana 리다이렉트");
            return "dashboard";  // Kibana로 이동
        } else {
            System.out.println("[DEBUG] 일반 사용자 로그인 → userpage 이동");
            model.addAttribute("user", user);
            return "userpage"; // 일반 사용자 페이지
        }
    }
    */

    /**
     * JWT 기반 로그인 REST API
     * 클라이언트에서 JSON(userID, password)로 요청 → access/refresh 토큰을 JSON으로 반환
     * @param request LoginRequestDto (userID, password)
     * @return LoginResponseDto (accessToken, refreshToken) 또는 401 Unauthorized
     */
    @PostMapping("/api/login")
    @ResponseBody // JSON 반환
    public ResponseEntity<?> jwtLogin(@RequestBody com.example.ZeroTrust.service.LoginRequestDto request) {
        // 입력값 디버깅
        System.out.println("[DEBUG] [API] 입력된 ID: " + request.getUserID());
        System.out.println("[DEBUG] [API] 입력된 PW: " + request.getPassword());

        // JWT 로그인 처리
        com.example.ZeroTrust.service.LoginResponseDto tokens = userService.loginUser(request.getUserID(), request.getPassword());
        if (tokens == null) {
            // 인증 실패 시 401 반환
            return ResponseEntity.status(401).body("ID 또는 비밀번호가 잘못되었습니다.");
        }
        // 성공 시 토큰 JSON 반환
        return ResponseEntity.ok(tokens);
    }

    /**
     * JWT 인증이 필요한 보호 API 예시
     * - /api/userinfo
     * - access 토큰이 있어야만 접근 가능
     * - 인증된 사용자의 userID, role을 반환
     */
    @GetMapping("/api/userinfo")
    @ResponseBody
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        // Authentication 객체에서 userID, role 추출
        String userId = (String) authentication.getPrincipal();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority())
                .orElse("");

        // 결과를 JSON 형태로 반환
        return ResponseEntity.ok(new java.util.HashMap<String, String>() {{
            put("userID", userId);
            put("role", role);
        }});
    }
}
