package com.microservices.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.microservices.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(String role);

    List<User> findByStatus(String status);

    List<User> findByActive(Boolean active);

    Page<User> findByRole(String role, Pageable pageable);

    Page<User> findByStatus(String status, Pageable pageable);

    Page<User> findByActive(Boolean active, Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
           "(:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
           "(:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:status IS NULL OR u.status = :status) AND " +
           "(:active IS NULL OR u.active = :active)")
    Page<User> searchUsers(
        @Param("username") String username,
        @Param("email") String email,
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        @Param("role") String role,
        @Param("status") String status,
        @Param("active") Boolean active,
        Pageable pageable
    );

    @Query("SELECT DISTINCT u.role FROM User u WHERE u.role IS NOT NULL ORDER BY u.role")
    List<String> findDistinctRoles();

    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role")
    List<Object[]> countByRole();

    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status")
    List<Object[]> countByStatus();

    long countByActive(Boolean active);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
