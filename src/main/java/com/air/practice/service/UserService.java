package com.air.practice.service;

import com.air.practice.dto.users.*;
import com.air.practice.mapper.UserMapper;
import com.air.practice.repository.UserRepository;
import com.air.sec.config.AuthTokenGateway;
import com.air.sec.config.exceptions.EmailAlreadyExistsException;
import com.air.sec.config.exceptions.InvalidCredentialsException;
import com.air.sec.config.exceptions.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenGateway authTokenGateway;

    @Transactional
    public UserRegisterResponse register(UserRegisterRequest userRegisterRequest ) {

        if (userRepository.existsByEmail(userRegisterRequest.email())){
            log.warn("User with email {} already exists!", userRegisterRequest.email());
            throw new EmailAlreadyExistsException("User with email " + userRegisterRequest.email() + " already exists!");
        }
        var user = userMapper.requestToEntity(userRegisterRequest, passwordEncoder);

        userRepository.save(user);

        return userMapper.toResponseRegister(user);
    }

    @Transactional
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

    @Transactional
    public UserDetailsResponse getUserDetails(UUID userId) {
        log.info("Fetching details for user with ID: {}", userId);

        var  user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found.")
                );

        return userMapper.toResponseDetails(user);
    }

    @Transactional
    public List<UserDetailsResponse> getUserDetailsList() {
        log.info("Fetching details for all users");
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDetails)
                .toList();
    }

    @Transactional
    public void deleteUser(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found.")
                );

        userRepository.deleteById(userId);
    }

    @Transactional
    public UserDetailsResponse updateUser(UUID userId, UserUpdateRequest userUpdateRequest) {
        var user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found.")
                );

        var userUpdated = userMapper.updateEntityFromRequest(user, userUpdateRequest);
        userRepository.save(userUpdated);

        return userMapper.toResponseDetails(userUpdated);
    }
}
