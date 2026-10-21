package com.attendance.attendance_system;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthenticationManager authenticationManager;
    private final AdminRepository adminRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public AdminController(
            AuthenticationManager authenticationManager,
            AdminRepository adminRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {

        String username = request.get("username");
        String password = request.get("password");

        if (username == null || password == null
                || username.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Username and password are required."));
        }

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    username, password));

            HttpSession session = httpRequest.getSession(true);

            SecurityContext context =
                    SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            session.setAttribute(
                    HttpSessionSecurityContextRepository
                            .SPRING_SECURITY_CONTEXT_KEY,
                    context);

            return ResponseEntity.ok(
                    Map.of("message", "Login successful!"));

        } catch (org.springframework.security.core.AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message",
                            "Invalid username or password."));
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (authentication == null
                || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please log in first."));
        }

        if (currentPassword == null || newPassword == null
                || currentPassword.isBlank() || newPassword.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "All fields are required."));
        }

        if (newPassword.length() < 8) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "New password must contain at least 8 characters."));
        }

        Admin admin = adminRepository
                .findByUsername(authentication.getName())
                .orElse(null);

        if (admin == null
                || !passwordEncoder.matches(
                        currentPassword, admin.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message",
                            "Current password is incorrect."));
        }

        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);

        return ResponseEntity.ok(
                Map.of("message", "Password changed successfully!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully!"));
    }
}