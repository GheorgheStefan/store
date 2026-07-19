package com.air.practice.service;

import com.air.practice.dto.UserRegisterRequest;
import com.air.practice.dto.UserRegisterResponse;
import com.air.practice.mapper.UserMapper;
import com.air.practice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserRegisterResponse register(UserRegisterRequest userRegisterRequest ) {

        try {
            userRepository.existsByEmail(userRegisterRequest.email());
        } catch (Exception exception) {
            log.error(
                    "Error while checking email {}",
                    userRegisterRequest.email(),
                    exception
            );

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while checking the email",
                    exception
            );
        }
        var user = userMapper.requestToEntity(userRegisterRequest, passwordEncoder);

        userRepository.save(user);

        return userMapper.toResponseRegister(user);
    }

}
