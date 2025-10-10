package com.auth.auth_app.repository;

import com.auth.auth_app.domain.TestUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestUserRepo extends JpaRepository<TestUser, String> {
    Optional<TestUser> findByUserName(String userName);

}
