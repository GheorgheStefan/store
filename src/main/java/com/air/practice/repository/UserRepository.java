package com.air.practice.repository;

import com.air.practice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query(
            value = """
        SELECT EXISTS (
            SELECT 1
            FROM users
            WHERE email = :email
        )
        """, nativeQuery = true
    )
    boolean existsByEmail(@Param("email") String email);

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);
}
