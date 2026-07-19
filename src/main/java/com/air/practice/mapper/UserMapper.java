package com.air.practice.mapper;

import com.air.practice.dto.*;
import com.air.practice.entity.User;
import com.air.sec.config.AuthTokenGateway;
import jdk.jfr.Name;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(com.air.practice.dto.Role.USER)")
    @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    User requestToEntity(UserRegisterRequest request, @Context PasswordEncoder passwordEncoder);

    @Mapping(target = "token", source = "user", qualifiedByName = "generateToken")
    @Mapping(target = "role", source = "user.role")
    UserLoginResponse toResponseLogin(User user, @Context AuthTokenGateway authTokenGateway);

    UserRegisterResponse toResponseRegister(User user);

    UserDetailsResponse toResponseDetails(User user);


    @Named("generateToken")
    default String generateToken(User user, @Context AuthTokenGateway authTokenGateway) {
        return authTokenGateway.generateAccessToken(
                user.getEmail(),
                user.getRole().name());
    }

    @Named("encodePassword")
    default String encodePassword(String password, @Context PasswordEncoder passwordEncoder) {
        return passwordEncoder.encode(password);
    }

    @Mapping(target = "firstName", source = "userUpdateRequest.firstName")
    @Mapping(target = "lastName", source = "userUpdateRequest.lastName")
    User updateEntityFromRequest(User user, UserUpdateRequest userUpdateRequest);
}
