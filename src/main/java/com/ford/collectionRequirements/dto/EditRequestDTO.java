package com.ford.collectionRequirements.dto;

import com.ford.collectionRequirements.user.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditRequestDTO {
    private Long requestId;
    private Long requestorId;
    private Long approvedBy;
    private String approvalNotes;
    private Long departmentId;
    private Long eventId;
    private LocalDate requestDate;
    private String requestStatus;
    private Boolean groupRequest;
    private String justification;
    private String TAN_Number;
    private String curriculamLink;
    private List<Long> requestedParticipants;


}

