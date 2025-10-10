package com.auth.auth_app.resource;

import com.auth.auth_app.domain.TestUser;
import com.auth.auth_app.dto.TestUserDTO;
import com.auth.auth_app.service.TestUserService;
import com.auth.auth_app.service.TokenBlacklistService;
import com.auth.auth_app.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api")
public class LoginResource {

    private static final Logger log = LoggerFactory.getLogger(LoginResource.class);
    @Autowired
    TestUserService testUserService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody TestUserDTO testUserDTO) {
        log.info("check payload {}", testUserDTO.toString());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            testUserDTO.getUserName(),
                            testUserDTO.getPassword()
                    )
            );
            String token = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        log.info("api call for logout");
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            log.info("token {}", token);
            tokenBlacklistService.blacklistToken(token);
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header");
        }
    }

    @PostMapping("/create/user")
    public ResponseEntity<?> createUser(@RequestBody TestUserDTO testUserDTO) {
        try {
            testUserService.save(testUserDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    @GetMapping("/test/{id}")
    public ResponseEntity<String> test(@PathVariable String id) {
        return ResponseEntity.ok("hello");
    }
}
