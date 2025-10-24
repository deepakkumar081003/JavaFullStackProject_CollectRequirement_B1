package com.ford.collectionRequirements.dto; // Assuming you want to keep DTOs in this package

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCountsDTO {
    private Long totalSubmitted;
    private Long approved;
    private Long pending;
    private Long closed;
}
