package com.microservices.user.controller.v2;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.microservices.user.dto.v2.BulkUserRequest;
import com.microservices.user.dto.v2.PagedResponse;
import com.microservices.user.dto.v2.UserFilterRequest;
import com.microservices.user.dto.v2.UserStatsResponse;
import com.microservices.user.dto.v2.UserRequest;
import com.microservices.user.dto.v2.UserResponse;
import com.microservices.user.service.v2.UserService;

@RestController("userControllerV2")
@RequestMapping("/api/v2/users")
@RequiredArgsConstructor
@Tag(name = "User Management V2", description = "Enhanced user operations with pagination, filtering, and bulk operations")
public class UserController {

    private final UserService userService;

    @PostMapping("/search")
    @Operation(summary = "Search users with pagination", description = "Search and filter users with pagination support")
    public ResponseEntity<PagedResponse<UserResponse>> searchUsers(@Valid @RequestBody UserFilterRequest filterRequest) {
        return ResponseEntity.ok(userService.searchUsers(filterRequest));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user with all v2 fields")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieves a user by their email address")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PostMapping
    @Operation(summary = "Create new user", description = "Creates a new user with role and status")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete user", description = "Soft deletes a user by setting active to false")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    @Operation(summary = "Bulk create users", description = "Creates multiple users in a single request")
    public ResponseEntity<List<UserResponse>> bulkCreateUsers(@Valid @RequestBody BulkUserRequest bulkRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.bulkCreateUsers(bulkRequest));
    }

    @GetMapping("/roles")
    @Operation(summary = "Get all roles", description = "Retrieves all distinct user roles")
    public ResponseEntity<List<String>> getAllRoles() {
        return ResponseEntity.ok(userService.getAllRoles());
    }

    @GetMapping("/stats")
    @Operation(summary = "Get user statistics", description = "Retrieves user statistics including counts by role and status")
    public ResponseEntity<UserStatsResponse> getUserStatistics() {
        return ResponseEntity.ok(userService.getUserStatistics());
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update user status", description = "Updates the status of a user")
    public ResponseEntity<UserResponse> updateUserStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(userService.updateUserStatus(id, status));
    }

    @PostMapping("/generate-username")
    @Operation(summary = "Generate unique username", description = "Generates a unique username based on first and last name")
    public ResponseEntity<Map<String, String>> generateUsername(@RequestParam String firstName, @RequestParam String lastName) {
        return ResponseEntity.ok(userService.generateUsername(firstName, lastName));
    }
}
