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
    @GetMapping("/all/user/{ldUserId}")
    public ResponseEntity<List<RequestDetailsDTO>> getAllRequestByUserIdWithFilters(
            @PathVariable Long ldUserId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "departmentId", required = false) Integer departmentId,
            @RequestParam(value = "eventId", required = false) Long eventId,
            @RequestParam(value ="requestorId", required = false) Long requestorId,
            @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) LocalDate toDate
            ) {
        try {
            List<RequestDetailsDTO> requests = requestService.getAllRequests(ldUserId, status, departmentId, eventId, requestorId,fromDate, toDate);

            if (requests.isEmpty()) {
                return ResponseEntity.ok(requests); // Return 200 OK with an empty list
            }
            return ResponseEntity.ok(requests);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/summary/user/{lcUserId}")
    public ResponseEntity<RequestCountsDTO> getRequestSummaryForUser(@PathVariable Long lcUserId) {
        RequestCountsDTO counts = requestService.getRequestSummaryCounts(lcUserId);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("all/summary/user/{ldUserId}")
    public ResponseEntity<RequestCountsDTO> getAllRequestSummaryForUser(@PathVariable Long ldUserId) {
        try {
            RequestCountsDTO counts = requestService.getAllRequestsSummaryCounts(ldUserId);
            return ResponseEntity.ok(counts);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    //GetMapping for all requests with filters(status,date range,department(id),event(id))
    @GetMapping("/user/{lcUserId}") // Renamed path variable for clarity
    public ResponseEntity<List<RequestDetailsDTO>> getRequestsByUserIdWithFilters(
            @PathVariable Long lcUserId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "departmentId", required = false) Integer departmentId,
            @RequestParam(value = "eventId", required = false) Long eventId,
            @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) LocalDate toDate
    ) {
        List<RequestDetailsDTO> requests = requestService.getFilteredRequests(
                lcUserId, status, departmentId, eventId, fromDate, toDate);

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

    @GetMapping("/users/ids-names")
    public ResponseEntity<List<BasicUserDTO>> getAllUsersIdAndName() {
        List<BasicUserDTO> users = requestService.getAllUsersIdAndName();
        return ResponseEntity.ok(users);
    }
}


