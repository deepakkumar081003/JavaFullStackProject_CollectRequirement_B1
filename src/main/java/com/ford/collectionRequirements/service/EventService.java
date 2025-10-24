package com.ford.collectionRequirements.service;

import com.ford.collectionRequirements.event.Event;
import com.ford.collectionRequirements.request.Request;

import java.util.List;

public interface EventService {
    Event getEventById(Long id);
    List<Request> getRequestsByEventId(Long id);
    Event updateEvent(Long id, Event updatedEvent);
    Request assignExistingRequestToEvent(Long requestId, Long eventId);
    Request removeExistingRequestFromEvent(Long eventId, Long requestId);
}
