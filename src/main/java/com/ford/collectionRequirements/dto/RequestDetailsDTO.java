package com.ford.collectionRequirements.dto;


import com.ford.collectionRequirements.user.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDetailsDTO {

    private Long requestId;
    private Long requestorId; // From UserInfo
    private String requestorName; // From UserInfo
    private Integer departmentId; // From Department
    private String departmentName;
    private Long eventId; // From Event
    private String eventName;
    private LocalDate requestDate;
    private String requestStatus;
    private Boolean groupRequest;
    private String justification;
    private Long noOfParticipants;
    private String tanNumber;
    private String curriculumLink;
    private List<BasicUserDTO> participants; // List of UserInfo for participants


}
