package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByEmailAndRoleName(String email, String roleName);

    // Search users by email keyword for autocomplete
    List<User> findByEmailContainingIgnoreCaseAndRoleName(String email, String roleName);
    @Query("SELECT u.email FROM User u WHERE u.id = :userId")
    Optional<String> findEmailByUserId(@Param("userId") Integer userId);
}
