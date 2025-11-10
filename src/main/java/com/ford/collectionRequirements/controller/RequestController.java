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
import com.ford.collectionRequirements.request.Request;
import com.ford.collectionRequirements.dto.EditRequestDTO;

@RestController
@RequestMapping("/requests")
@CrossOrigin("http://localhost:4200/")
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    //lnd tasks

    //To get all the requests submitted by the requestors(lc) with filters
    @GetMapping("/all/{ldUserId}")
    public ResponseEntity<List<RequestDetailsDTO>> getAllRequestByUserIdWithFilters(
            @PathVariable Long ldUserId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "departmentName", required = false) String departmentName,
            @RequestParam(value = "eventName", required = false) String eventName,
            @RequestParam(value = "requestorName", required = false) String requestorName,
            @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) LocalDate toDate
    ) {
        List<RequestDetailsDTO> requests = requestService.getAllRequests(ldUserId, status, departmentName, eventName, requestorName, fromDate, toDate);
        return ResponseEntity.ok(requests);
    }

    // To get request counts for lnd user
    @GetMapping("all/counts/{ldUserId}")
    public ResponseEntity<RequestCountsDTO> getAllRequestSummaryForUser(@PathVariable Long ldUserId) {
        RequestCountsDTO counts = requestService.getAllRequestsSummaryCounts(ldUserId);
        return ResponseEntity.ok(counts);
    }


    //lc tasks

    //To get all the requests submitted by the user(lc) with filters
    @GetMapping("/{lcUserId}")
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
        return ResponseEntity.ok(requests);
    }

    // To get request counts for lc user
    @GetMapping("/counts/{lcUserId}")
    public ResponseEntity<RequestCountsDTO> getRequestSummaryForUser(@PathVariable Long lcUserId) {
        RequestCountsDTO counts = requestService.getRequestSummaryCounts(lcUserId);
        return ResponseEntity.ok(counts);
    }

    //Both lc and lnd tasks

    //  to create a new request
    @PostMapping("/create")
    public ResponseEntity<RequestDetailsDTO> createRequest(@RequestBody CreateRequestDTO createRequestDTO) {
        RequestDetailsDTO createdRequest = requestService.createRequest(createRequestDTO);
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    // To get the details of a specific request by its ID
    @GetMapping("/id/{requestId}")
    public ResponseEntity<RequestDetailsDTO> getRequestById(@PathVariable Long requestId) {
        RequestDetailsDTO requestDetailsDTO = requestService.getRequestDetails(requestId);
        return ResponseEntity.ok(requestDetailsDTO);
    }


    //To get all users for participant selection
    @GetMapping("/users")
    public ResponseEntity<List<BasicUserDTO>> getAllUsers() {
        List<BasicUserDTO> users = requestService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    //---------------------------------------------------------------------------------


    // To update a request
    @PutMapping("/edit/{requestId}")
    public ResponseEntity<Request> updateRequest(@PathVariable Long requestId, @RequestBody EditRequestDTO editRequestDTO) {
        Request savedRequest = requestService.updateRequest(requestId, editRequestDTO);
        return ResponseEntity.ok(savedRequest);
    }

    // To delete a request
    @DeleteMapping("/delete/{requestId}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long requestId) {
        requestService.deleteRequest(requestId);
        return ResponseEntity.noContent().build();
    }


}


