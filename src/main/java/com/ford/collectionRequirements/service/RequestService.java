package com.ford.collectionRequirements.service;

import com.ford.collectionRequirements.dto.BasicUserDTO;
import com.ford.collectionRequirements.dto.CreateRequestDTO;
import com.ford.collectionRequirements.dto.RequestCountsDTO;
import com.ford.collectionRequirements.dto.RequestDetailsDTO;

import java.time.LocalDate;
import java.util.List;

import com.ford.collectionRequirements.dto.EditRequestDTO;
import com.ford.collectionRequirements.entity.Request;
import com.ford.collectionRequirements.exception.RequestNotFoundException;
import com.ford.collectionRequirements.exception.UnauthorizedActionException;

public interface RequestService {
    List<RequestDetailsDTO> getAllRequests(
            Long ldUserId,
            String status,
            String departmentName,
            String eventName,
            String requestorName,
            LocalDate fromDate,
            LocalDate toDate
    ) throws UnauthorizedActionException;
    RequestCountsDTO getRequestSummaryCounts(Long requestorId);

    List<RequestDetailsDTO> getFilteredRequests(
            Long requestorId,
            String status,
            String departmentName,
            String eventName,
            LocalDate fromDate,
            LocalDate toDate
    );

    RequestDetailsDTO getRequestDetails(Long requestId) throws RequestNotFoundException;

    RequestDetailsDTO createRequest(CreateRequestDTO createRequestDTO);


    RequestCountsDTO getAllRequestsSummaryCounts(Long requestorId) throws UnauthorizedActionException;

    List<BasicUserDTO> getAllUsers();
    Request updateRequest( Long requestId ,EditRequestDTO editRequestDTO) throws RequestNotFoundException;
    void deleteRequest(Long requestId) throws RequestNotFoundException;
}
