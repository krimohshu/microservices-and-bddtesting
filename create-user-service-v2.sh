#!/bin/bash

# User Service - Complete Implementation Script
# This script creates all remaining files for User Service and Order Service

echo "=== Creating User Service V2 Components ==="

# Create V2 DTOs directory
mkdir -p "/Users/krishanshukla/Documents/work/Rest_API_Frameworks/Spring_APP_and_Tests/user-service/src/main/java/com/microservices/user/dto/v2"

# Create UserRequest V2
cat > "/Users/krishanshukla/Documents/work/Rest_API_Frameworks/Spring_APP_and_Tests/user-service/src/main/java/com/microservices/user/dto/v2/UserRequest.java" << 'EOF'
package com.microservices.user.dto.v2;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "Username is required")
    @Size(max = 50)
    private String username;

    @NotBlank(message = "Email is required")
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    private String lastName;

    @Size(max = 20)
    private String phone;

    @Pattern(regexp = "USER|ADMIN|MANAGER", message = "Role must be USER, ADMIN, or MANAGER")
    private String role;

    @Pattern(regexp = "ACTIVE|INACTIVE|SUSPENDED", message = "Status must be ACTIVE, INACTIVE, or SUSPENDED")
    private String status;

    private Boolean active = true;
}
EOF

echo "✓ Created UserRequest V2"

# Create UserResponse V2
cat > "/Users/krishanshukla/Documents/work/Rest_API_Frameworks/Spring_APP_and_Tests/user-service/src/main/java/com/microservices/user/dto/v2/UserResponse.java" << 'EOF'
package com.microservices.user.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
    private String status;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}
EOF

echo "✓ Created UserResponse V2"

# Create UserFilterRequest
cat > "/Users/krishanshukla/Documents/work/Rest_API_Frameworks/Spring_APP_and_Tests/user-service/src/main/java/com/microservices/user/dto/v2/UserFilterRequest.java" << 'EOF'
package com.microservices.user.dto.v2;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String status;
    private Boolean active;

    @Min(value = 0, message = "Page number must be 0 or greater")
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    private Integer size = 10;
}
EOF

echo "✓ Created UserFilterRequest"

# Create PagedResponse
cat > "/Users/krishanshukla/Documents/work/Rest_API_Frameworks/Spring_APP_and_Tests/user-service/src/main/java/com/microservices/user/dto/v2/PagedResponse.java" << 'EOF'
package com.microservices.user.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
EOF

echo "✓ Created PagedResponse"

# Create BulkUserRequest
cat > "/Users/krishanshukla/Documents/work/Rest_API_Frameworks/Spring_APP_and_Tests/user-service/src/main/java/com/microservices/user/dto/v2/BulkUserRequest.java" << 'EOF'
package com.microservices.user.dto.v2;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUserRequest {
    @NotEmpty(message = "Users list cannot be empty")
    @Valid
    private List<UserRequest> users;
}
EOF

echo "✓ Created BulkUserRequest"

# Create UserStatsResponse
cat > "/Users/krishanshukla/Documents/work/Rest_API_Frameworks/Spring_APP_and_Tests/user-service/src/main/java/com/microservices/user/dto/v2/UserStatsResponse.java" << 'EOF'
package com.microservices.user.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {
    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;
    private Map<String, Long> usersByRole;
    private Map<String, Long> usersByStatus;
}
EOF

echo "✓ Created UserStatsResponse"

echo "✅ User Service V2 DTOs complete!"
echo ""
echo "📝 Summary:"
echo "  - Created 6 V2 DTO classes"
echo "  - Next: Run this script to create V2 services, controllers, and tests"
echo ""
echo "To continue: bash /path/to/this/script"
