package com.ford.collectionRequirements.controller;


import com.ford.collectionRequirements.request.Request;
import com.ford.collectionRequirements.dto.EventCreationRequestDTO;
import com.ford.collectionRequirements.event.Event;
import com.ford.collectionRequirements.request.Request;
import com.ford.collectionRequirements.service.EventService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.message.ReusableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ford.collectionRequirements.event.Event;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {


    private EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }
    @GetMapping("/{id}/requests")
    public List<Request> getRequestsForEvent(@PathVariable("id") Long eventId) {
        return eventService.getRequestsByEventId(eventId);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event updatedEvent) {
        try {
            Event savedEvent = eventService.updateEvent(id, updatedEvent);
            return ResponseEntity.ok(savedEvent);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PutMapping("/{eventId}/requests/{requestId}/add")
    public Request assignExistingRequestToEvent(
            @PathVariable Long eventId,
            @PathVariable Long requestId) {
        try {
            // The service method handles finding both entities and updating the request
            return eventService.assignExistingRequestToEvent(eventId, requestId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
    @PutMapping("/{eventId}/requests/{requestId}/remove")
    public Request removeExistingRequestFromEvent(
            @PathVariable Long eventId,
            @PathVariable Long requestId) {
        try {
            return eventService.removeExistingRequestFromEvent(eventId, requestId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return this.eventService.getAllEvents();
    }

    //FOR CREATING A NEW EVENT
    @PostMapping
    public Event createEvent(@RequestBody EventCreationRequestDTO eventCreationRequestDTO) {
        return this.eventService.createEvent(eventCreationRequestDTO);
    }

    //FOR SHOWING NEWLY APPROVED REQUESTS WHICH HAS NO EVENT SET TO SELECT FOR AN NEWLY CREATED EVENT
    @GetMapping("/newApprovedRequestsNotAssignedToEvent")
    public List<Request> getAllNewApprovedRequestsNotAssignedToEvent(){
        return this.eventService.getAllNewApprovedRequestsNotAssignedToEvent();
    }

    //SEARCH REQUEST TO SELECT BASED ON ID
    @GetMapping("/requestid/{requestid}")
    public Request getRequestById(@PathVariable("requestid") Long requestid){
        return this.eventService.getAllRequestsForSeachById(requestid);
    }

    //SEARCH REQUEST TO SELECT BASED ON NAME
    @GetMapping("/requestname/{requestName}")
    public List<Request> getRequestById(@PathVariable("requestName") String requestName){
        return this.eventService.getAllRequestsForSeachByName(requestName);
    }

    //DELETE EVENT
    @DeleteMapping("/{eventid}")
    public Event deleteEvent(@PathVariable("eventid") Long eventid){
        return this.eventService.deleteEvent(eventid);
    }

}
