package com.ford.collectionRequirements.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private Integer DepartmentId;
    private String role;
    private Long regionId;
    private Long managerId;
}
