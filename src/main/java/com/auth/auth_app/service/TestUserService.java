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

import java.util.Optional;
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

    public void save(TestUser testUser) {
        if (testUser.getPassword() == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
        String uuid = UUID.randomUUID().toString();
        if (testUser.getId() == null)
            testUser.setId(uuid);
        testUserRepo.save(testUser);
    }

    public TestUserDTO editUser(TestUserDTO testUserDTO) {
        Optional<TestUser> user = testUserRepo.findById(testUserDTO.getId());

        if (user.isPresent()) {
            TestUser testUser = user.get();
            testUser.setProfileImage(testUserDTO.getProfileImage());
            testUser.setUserName(testUserDTO.getUserName());
            return (testUserMapper.testUserToTestUserDTO(testUserRepo.save(testUser)));

        } else return new TestUserDTO();
    }

}
