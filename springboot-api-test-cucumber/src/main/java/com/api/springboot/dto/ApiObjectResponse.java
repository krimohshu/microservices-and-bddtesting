package com.api.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiObjectResponse {
    private Long id;
    private String name;
    private Object data;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
