package com.ford.collectionRequirements.controller;


import com.ford.collectionRequirements.dto.BasicUserDTO;
import com.ford.collectionRequirements.dto.CreateRequestDTO;
import com.ford.collectionRequirements.dto.RequestCountsDTO;
import com.ford.collectionRequirements.dto.RequestDetailsDTO;
import com.ford.collectionRequirements.service.RequestService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.ford.collectionRequirements.request.Request;
import com.ford.collectionRequirements.service.RequestService;
import jakarta.persistence.EntityNotFoundException;
import com.ford.collectionRequirements.dto.EditRequestDTO;

@RestController
@RequestMapping("/requests")
@CrossOrigin("http://localhost:4200/")
public class RequestController {

    private RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    //To get the all the requests submitted by the requestors with filters
    @GetMapping("/all/{ldUserId}")
    public ResponseEntity<List<RequestDetailsDTO>> getAllRequestByUserIdWithFilters(
            @PathVariable Long ldUserId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "departmentName", required = false) String departmentName,
            @RequestParam(value = "eventName", required = false) String eventName,
            @RequestParam(value ="requestorName", required = false) String requestorName,
            @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) LocalDate toDate
            ) {
        try {
            List<RequestDetailsDTO> requests = requestService.getAllRequests(ldUserId, status, departmentName, eventName, requestorName,fromDate, toDate);

            if (requests.isEmpty()) {
                return ResponseEntity.ok(requests); // Return 200 OK with an empty list
            }
            return ResponseEntity.ok(requests);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/counts/{lcUserId}")
    public ResponseEntity<RequestCountsDTO> getRequestSummaryForUser(@PathVariable Long lcUserId) {
        RequestCountsDTO counts = requestService.getRequestSummaryCounts(lcUserId);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("all/counts/{ldUserId}")
    public ResponseEntity<RequestCountsDTO> getAllRequestSummaryForUser(@PathVariable Long ldUserId) {
        try {
            RequestCountsDTO counts = requestService.getAllRequestsSummaryCounts(ldUserId);
            return ResponseEntity.ok(counts);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    //GetMapping for all requests with filters(status,date range,department(id),event(id))
    @GetMapping("/{lcUserId}") // Renamed path variable for clarity
    public ResponseEntity<List<RequestDetailsDTO>> getRequestsByUserIdWithFilters(
            @PathVariable Long lcUserId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "departmentName", required = false) String departmentName,
            @RequestParam(value = "eventName", required = false) String eventName,
            @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) LocalDate toDate
    ) {
        List<RequestDetailsDTO> requests = requestService.getFilteredRequests(
                lcUserId, status, departmentName, eventName, fromDate, toDate);

        if (requests.isEmpty()) {
            return ResponseEntity.ok(requests); // Return 200 OK with an empty list
        }
        return ResponseEntity.ok(requests);
    }

    //  to create a new request
    @PostMapping("/create")
    public ResponseEntity<RequestDetailsDTO> createRequest(@RequestBody CreateRequestDTO createRequestDTO) {
        try {
            RequestDetailsDTO createdRequest = requestService.createRequest(createRequestDTO);
            return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            // Return 404 if requestor or participants are not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Catch any other unexpected errors
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/id/{requestId}")
    public ResponseEntity<RequestDetailsDTO> getRequestById(@PathVariable Long requestId) {
        RequestDetailsDTO requestDetailsDTO = requestService.getRequestDetails(requestId);
        return ResponseEntity.ok(requestDetailsDTO);
    }

    @PutMapping("/edit/{requestId}")
    public ResponseEntity<Request> updateRequest(@PathVariable Long requestId,@RequestBody EditRequestDTO editRequestDTO) {
        try {
            Request savedRequest = requestService.updateRequest(requestId,editRequestDTO);
            return ResponseEntity.ok(savedRequest);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/delete/{requestId}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long requestId) {
        try {
            requestService.deleteRequest(requestId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<BasicUserDTO>> getAllUsers() {
        List<BasicUserDTO> users = requestService.getAllUsers();
        return ResponseEntity.ok(users);
    }

}


