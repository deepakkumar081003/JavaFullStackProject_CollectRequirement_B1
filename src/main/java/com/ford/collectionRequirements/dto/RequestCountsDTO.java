package com.ford.collectionRequirements.dto; // Assuming you want to keep DTOs in this package

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCountsDTO {
    @NotNull
    @NotBlank
    private Long totalSubmitted;
    @NotNull
    @NotBlank
    private Long approved;
    @NotNull
    @NotBlank
    private Long pending;
    @NotNull
    @NotBlank
    private Long closed;
}
