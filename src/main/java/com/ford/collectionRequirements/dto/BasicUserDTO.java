package com.ford.collectionRequirements.dto;

import com.ford.collectionRequirements.department.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicUserDTO {
    private Long userId;
    private String userName;
    private String email;
    private Integer departmentId;
    private String role;
    private Long regionId;
    private Long managerId;

}
