package com.ford.collectionRequirements.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequestDTO {
    private Long requestorId;
    private String justification;
    private Boolean groupRequest;
    private String tanNumber;
    private String curriculumLink;
    private List<Long> participantUserIds; // List of user IDs for participants
    // No eventId here, as per your instruction that requestor can't assign it
}
