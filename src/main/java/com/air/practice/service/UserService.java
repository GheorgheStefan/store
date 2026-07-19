package com.air.practice.service;

import com.air.practice.dto.UserLoginRequest;
import com.air.practice.dto.UserLoginResponse;
import com.air.practice.dto.UserRegisterRequest;
import com.air.practice.dto.UserRegisterResponse;
import com.air.practice.mapper.UserMapper;
import com.air.practice.repository.UserRepository;
import com.air.sec.config.AuthTokenGateway;
import com.air.sec.config.exceptions.EmailAlreadyExistsException;
import com.air.sec.config.exceptions.InvalidCredentialsException;
import com.air.sec.config.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenGateway authTokenGateway;

    public UserRegisterResponse register(UserRegisterRequest userRegisterRequest ) {

        if (userRepository.existsByEmail(userRegisterRequest.email())){
            log.warn("User with email {} already exists!", userRegisterRequest.email());
            throw new EmailAlreadyExistsException("User with email " + userRegisterRequest.email() + " already exists!");
        }
        var user = userMapper.requestToEntity(userRegisterRequest, passwordEncoder);

        userRepository.save(user);

        return userMapper.toResponseRegister(user);
    }

    public UserLoginResponse login(UserLoginRequest userLoginRequest) {

        log.info("User with email {} is trying to log in", userLoginRequest.email());

        var user = userRepository.findByEmail(userLoginRequest.email())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found. Please register.")
                );

        if (!passwordEncoder.matches(
                userLoginRequest.password(),
                user.getPassword()
        )) {
            throw new InvalidCredentialsException("Invalid password.");
        }

        return userMapper.toResponseLogin(user, authTokenGateway);
    }
}
