package com.microservices.product.dto.v2;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Paged Response DTO (v2)
 * Generic wrapper for paginated results
 * Contains data and pagination metadata
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Paginated response wrapper with metadata")
public class PagedResponse<T> {
    
    @Schema(description = "List of items in current page")
    private List<T> content;
    
    @Schema(description = "Current page number (0-based)", example = "0")
    private int pageNumber;
    
    @Schema(description = "Page size", example = "10")
    private int pageSize;
    
    @Schema(description = "Total number of elements across all pages", example = "100")
    private long totalElements;
    
    @Schema(description = "Total number of pages", example = "10")
    private int totalPages;
    
    @Schema(description = "Is this the first page?", example = "true")
    private boolean first;
    
    @Schema(description = "Is this the last page?", example = "false")
    private boolean last;
    
    @Schema(description = "Does next page exist?", example = "true")
    private boolean hasNext;
    
    @Schema(description = "Does previous page exist?", example = "false")
    private boolean hasPrevious;
}
