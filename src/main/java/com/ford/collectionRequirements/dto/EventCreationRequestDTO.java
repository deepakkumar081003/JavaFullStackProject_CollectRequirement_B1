package com.ford.collectionRequirements.dto;

import com.ford.collectionRequirements.entity.Event; // Assuming Event is in this package
import java.util.List;

public class EventCreationRequestDTO {
    private Event newEvent;
    private List<Long> requests;

    // Constructors, getters, and setters
    public EventCreationRequestDTO() {
    }

    public EventCreationRequestDTO(Event newEvent, List<Long> requests) {
        this.newEvent = newEvent;
        this.requests = requests;
    }

    public Event getNewEvent() {
        return newEvent;
    }

    public void setNewEvent(Event newEvent) {
        this.newEvent = newEvent;
    }

    public List<Long> getRequests() {
        return requests;
    }

    public void setRequests(List<Long> requests) {
        this.requests = requests;
    }
}

