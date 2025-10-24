package com.ford.collectionRequirements.service;

import com.ford.collectionRequirements.dto.BasicUserDTO;
import com.ford.collectionRequirements.dto.CreateRequestDTO;
import com.ford.collectionRequirements.dto.RequestCountsDTO;
import com.ford.collectionRequirements.dto.RequestDetailsDTO;

import java.time.LocalDate;
import java.util.List;

public interface RequestService {
    List<RequestDetailsDTO> getAllRequests(
            Long ldUserId,
            String status,
            Integer departmentId,
            Long eventId,
            Long requestorId,
            LocalDate fromDate,
            LocalDate toDate
    );
    RequestCountsDTO getRequestSummaryCounts(Long requestorId);

    List<RequestDetailsDTO> getFilteredRequests(
            Long requestorId,
            String status,
            Integer departmentId,
            Long eventId,
            LocalDate fromDate,
            LocalDate toDate
    );

    RequestDetailsDTO createRequest(CreateRequestDTO createRequestDTO);


    RequestCountsDTO getAllRequestsSummaryCounts(Long requestorId);

    List<BasicUserDTO> getAllUsersIdAndName();
}
