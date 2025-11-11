package com.ford.collectionRequirements.service;

import com.ford.collectionRequirements.dto.EventDTO;
import com.ford.collectionRequirements.entity.Event;
import com.ford.collectionRequirements.entity.Request;

import java.util.List;
import com.ford.collectionRequirements.dto.EventCreationRequestDTO;

public interface EventService {
    Event getEventById(Long id);
    List<Request> getRequestsByEventId(Long id);
    Event updateEvent(Long id, Event updatedEvent);
    Request assignExistingRequestToEvent(Long requestId, Long eventId);
    Request removeExistingRequestFromEvent(Long eventId, Long requestId);

    List<EventDTO> getAllEvents(
            String searchTerm,
            String description,
            String eventType,
            String status
    );

    EventDTO convertToDto(Event event);

    Event createEvent(EventCreationRequestDTO eventCreationRequestDTO);

    List<Request> getAllNewApprovedRequestsNotAssignedToEvent();

    Request getAllRequestsForSeachById(Long requestid);

    List<Request> getAllRequestsForSeachByName(String requestName);

    Event deleteEvent(Long eventid);


}
