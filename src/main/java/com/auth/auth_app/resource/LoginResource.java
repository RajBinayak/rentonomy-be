package com.auth.auth_app.resource;

import com.auth.auth_app.domain.TestUser;
import com.auth.auth_app.dto.TestUserDTO;
import com.auth.auth_app.security.UserPrincipal;
import com.auth.auth_app.service.TestUserService;
import com.auth.auth_app.service.TokenBlacklistService;
import com.auth.auth_app.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<?> login(@RequestBody TestUser testUser) {
        log.info("check payload {}", testUser.toString());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            testUser.getUserName(),
                            testUser.getPassword()
                    )
            );
            String token = jwtTokenProvider.generateToken(authentication);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            TestUser user = userPrincipal.getUser();
            user.setPassword(null);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(user);

        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
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
    public ResponseEntity<?> createUser(@RequestBody TestUser testUser) {
        try {
            testUserService.save(testUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    @PatchMapping("/edit/user")
    public ResponseEntity<TestUserDTO> editUser(@RequestBody TestUserDTO testUserDTO) {
        TestUserDTO editedUserDTO = new TestUserDTO();
        try {
            editedUserDTO = testUserService.editUser(testUserDTO);
            return ResponseEntity.ok().body(editedUserDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(editedUserDTO);
        }
    }

    @GetMapping("/test/{id}")
    public ResponseEntity<String> test(@PathVariable String id) {
        return ResponseEntity.ok("hello");
    }
}
