package com.ford.collectionRequirements.controller;


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
public class RequestController {

    private RequestService requestService;
    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PutMapping("/{requestId}")
    public ResponseEntity<Request> updateRequest(@RequestBody EditRequestDTO editRequestDTO) {
        try {
            Request savedRequest = requestService.updateRequest(editRequestDTO);
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
}
