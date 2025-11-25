package com.microservices.user.dto.v2;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUserRequest {
    @NotEmpty(message = "Users list cannot be empty")
    @Valid
    private List<UserRequest> users;
}
