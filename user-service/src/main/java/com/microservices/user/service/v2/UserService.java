package com.microservices.user.service.v2;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import com.microservices.user.dto.v2.BulkUserRequest;
import com.microservices.user.dto.v2.PagedResponse;
import com.microservices.user.dto.v2.UserFilterRequest;
import com.microservices.user.dto.v2.UserStatsResponse;
import com.microservices.user.dto.v2.UserRequest;
import com.microservices.user.dto.v2.UserResponse;
import com.microservices.user.exception.UserNotFoundException;
import com.microservices.user.model.User;
import com.microservices.user.repository.UserRepository;

@Service("userServiceV2")
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> searchUsers(UserFilterRequest filterRequest) {
        Pageable pageable = PageRequest.of(
            filterRequest.getPage(),
            filterRequest.getSize(),
            Sort.by("createdAt").descending()
        );

        Page<User> userPage = userRepository.searchUsers(
            filterRequest.getUsername(),
            filterRequest.getEmail(),
            filterRequest.getFirstName(),
            filterRequest.getLastName(),
            filterRequest.getRole(),
            filterRequest.getStatus(),
            filterRequest.getActive(),
            pageable
        );

        List<UserResponse> users = userPage.getContent().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());

        return new PagedResponse<>(
            users,
            userPage.getNumber(),
            userPage.getSize(),
            userPage.getTotalElements(),
            userPage.getTotalPages(),
            userPage.isLast()
        );
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        return convertToResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return convertToResponse(user);
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        user.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        user.setActive(request.getActive() != null ? request.getActive() : true);

        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public List<UserResponse> bulkCreateUsers(BulkUserRequest bulkRequest) {
        List<User> users = bulkRequest.getUsers().stream()
            .map(request -> {
                User user = new User();
                user.setUsername(request.getUsername());
                user.setEmail(request.getEmail());
                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());
                user.setPhone(request.getPhone());
                user.setRole(request.getRole() != null ? request.getRole() : "USER");
                user.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
                user.setActive(request.getActive() != null ? request.getActive() : true);
                return user;
            })
            .collect(Collectors.toList());

        List<User> savedUsers = userRepository.saveAll(users);
        return savedUsers.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getAllRoles() {
        return userRepository.findDistinctRoles();
    }

    @Transactional(readOnly = true)
    public UserStatsResponse getUserStatistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByActive(true);
        long inactiveUsers = userRepository.countByActive(false);

        List<Object[]> roleData = userRepository.countByRole();
        Map<String, Long> usersByRole = roleData.stream()
            .filter(row -> row[0] != null)
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));

        List<Object[]> statusData = userRepository.countByStatus();
        Map<String, Long> usersByStatus = statusData.stream()
            .filter(row -> row[0] != null)
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));

        return new UserStatsResponse(totalUsers, activeUsers, inactiveUsers, usersByRole, usersByStatus);
    }

    @Transactional
    public UserResponse updateUserStatus(Long id, String status) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        user.setStatus(status);
        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }

    @Transactional(readOnly = true)
    public Map<String, String> generateUsername(String firstName, String lastName) {
        String baseUsername = (firstName.substring(0, 1) + lastName).toLowerCase();
        String username = baseUsername;
        int counter = 1;
        
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }
        
        return Map.of("username", username);
    }

    private UserResponse convertToResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhone(),
            user.getRole(),
            user.getStatus(),
            user.getActive(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getVersion()
        );
    }
}
