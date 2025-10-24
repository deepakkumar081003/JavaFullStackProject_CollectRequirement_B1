package com.ford.collectionRequirements.controller;


import com.ford.collectionRequirements.dto.EventCreationRequestDTO;
import com.ford.collectionRequirements.event.Event;
import com.ford.collectionRequirements.request.Request;
import com.ford.collectionRequirements.service.EventService;
import org.apache.logging.log4j.message.ReusableMessage;
import org.springframework.beans.factory.annotation.Autowired;
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
