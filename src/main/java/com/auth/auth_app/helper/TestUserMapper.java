package com.auth.auth_app.helper;

import com.auth.auth_app.domain.TestUser;
import com.auth.auth_app.dto.TestUserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TestUserMapper {
    TestUser testUserDtoToTestUser(TestUserDTO testUserDTO);
    TestUserDTO testUserToTestUserDTO(TestUser testUser);
}
