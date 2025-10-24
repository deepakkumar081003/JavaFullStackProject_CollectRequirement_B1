package com.ford.collectionRequirements.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDetailsDTO {

    private Long requestId;
    private Long requestorId; // From UserInfo
    private Integer departmentId; // From Department
    private Long eventId; // From Event
    private LocalDate requestDate;
    private String requestStatus;
    private Boolean groupRequest;
    private String justification;
    private Long noOfParticipants;
    private String tanNumber;
    private String curriculumLink;




}
