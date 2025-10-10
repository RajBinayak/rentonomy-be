package com.auth.auth_app.service;

import com.auth.auth_app.domain.TestUser;
import com.auth.auth_app.dto.TestUserDTO;
import com.auth.auth_app.helper.TestUserMapper;
import com.auth.auth_app.repository.TestUserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TestUserService {
    private static final Logger log = LoggerFactory.getLogger(TestUserService.class);

    @Autowired
    TestUserRepo testUserRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TestUserMapper testUserMapper;

    public void save(TestUserDTO testUserDTO) {
        log.info("password: {}", testUserDTO.getPassword());
        log.info("encrypted password: {}", passwordEncoder.encode(testUserDTO.getPassword()));
        if (testUserDTO.getPassword() == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        testUserDTO.setPassword(passwordEncoder.encode(testUserDTO.getPassword()));
        TestUser testUser = testUserMapper.testUserDtoToTestUser(testUserDTO);
        String uuid = UUID.randomUUID().toString();
        if (testUser.getId() == null)
            testUser.setId(uuid);
        testUserRepo.save(testUser);
    }

}
