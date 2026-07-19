package com.air.practice.service;

import com.air.practice.dto.UserRegisterRequest;
import com.air.practice.dto.UserRegisterResponse;
import com.air.practice.mapper.UserMapper;
import com.air.practice.repository.UserRepository;
import com.air.sec.config.exceptions.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserRegisterResponse register(UserRegisterRequest userRegisterRequest ) {

        if (userRepository.existsByEmail(userRegisterRequest.email())){
            log.warn("User with email {} already exists!", userRegisterRequest.email());
            throw new EmailAlreadyExistsException("User with email " + userRegisterRequest.email() + " already exists!");
        }
        var user = userMapper.requestToEntity(userRegisterRequest, passwordEncoder);

        userRepository.save(user);

        return userMapper.toResponseRegister(user);
    }

}
