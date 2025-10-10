package com.auth.auth_app.service;

import com.auth.auth_app.domain.TestUser;
import com.auth.auth_app.repository.TestUserRepo;
import com.auth.auth_app.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService   {

    @Autowired
    private TestUserRepo testUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TestUser user = testUserRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new UserPrincipal(user);

    }
}
