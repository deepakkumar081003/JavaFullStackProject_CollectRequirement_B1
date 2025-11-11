package com.ford.collectionRequirements.service.impl;

import com.ford.collectionRequirements.dto.EventDTO;
import com.ford.collectionRequirements.event.Event;
import com.ford.collectionRequirements.dto.EventCreationRequestDTO;
import com.ford.collectionRequirements.event.Event;
import com.ford.collectionRequirements.repository.EventRepository;
import com.ford.collectionRequirements.repository.RequestRepository;
import com.ford.collectionRequirements.request.Request;
import com.ford.collectionRequirements.service.EventService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {


    private EventRepository eventRepository;
    private RequestRepository requestRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, RequestRepository requestRepository) {
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
    }

    // Get Event by ID
    @Override
    public Event getEventById(Long id) {
        Optional<Event> event = eventRepository.findById(id);
        return event.orElse(null);
    }

    // Get Requests associated with an Event
    @Override
    public List<Request> getRequestsByEventId(Long eventId) {

        return requestRepository.findByEvent_EventId(eventId);
    }

    // Update Event details
    @Override
    @Transactional
    public Event updateEvent(Long eventId, Event updatedEvent) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        existingEvent.setEventName(updatedEvent.getEventName());
        existingEvent.setDescription(updatedEvent.getDescription());
//        existingEvent.setParticipantsCount(updatedEvent.getParticipantsCount());
        existingEvent.setDuration(updatedEvent.getDuration());
        existingEvent.setEventType(updatedEvent.getEventType());
        existingEvent.setFundingSource(updatedEvent.getFundingSource());
        existingEvent.setStatus(updatedEvent.getStatus());
        return eventRepository.save(existingEvent);
    }

    // Assign existing Request to Event
    @Override
    @Transactional
    public Request assignExistingRequestToEvent(Long eventId, Long requestId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with id: " + requestId));
        request.setEvent(event);
        Integer eventParticipants = event.getParticipantsCount() != null ? event.getParticipantsCount() : 0;
        Long requestParticipants = request.getNoOfParticipants() != null ? request.getNoOfParticipants() : 0L;
        // Update participants count
        event.setParticipantsCount(eventParticipants + requestParticipants.intValue());
        eventRepository.save(event);
        return requestRepository.save(request);
    }

    // Remove existing Request from Event
    @Override
    @Transactional
    public Request removeExistingRequestFromEvent(Long eventId, Long requestId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with id: " + requestId));
        Integer eventParticipants = event.getParticipantsCount() != null ? event.getParticipantsCount() : 0;
        Long requestParticipants = request.getNoOfParticipants() != null ? request.getNoOfParticipants() : 0L;
        request.setEvent(null);

        // Update participants count
        int updatedCount = eventParticipants - requestParticipants.intValue();
        event.setParticipantsCount(updatedCount);
        eventRepository.save(event);
        return requestRepository.save(request);
    }

    // Get all Events with filtering
    public List<EventDTO> getAllEvents(
            String searchTerm,
            String description,
            String eventType,
            String status
    ) {
        // Convert empty strings to null so the JPA query's IS NULL check works.
        // StringUtils.hasText() checks for non-null, non-empty, non-whitespace strings.
        String effectiveSearchTerm = StringUtils.hasText(searchTerm) ? searchTerm : null;
        String effectiveDescription = StringUtils.hasText(description) ? description : null;
        String effectiveEventType = StringUtils.hasText(eventType) ? eventType : null;
        String effectiveStatus = StringUtils.hasText(status) ? status : null;

        List<Event> events = eventRepository.findByFilters(
                effectiveSearchTerm,
                effectiveDescription,
                effectiveEventType,
                effectiveStatus
        );

        // Convert List<Event> to List<EventDTO>
        return events.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // This method converts your JPA Entity 'Event' to your DTO 'EventDTO'
    public EventDTO convertToDto(Event event) {
        EventDTO dto = new EventDTO();
        dto.setEventId(event.getEventId()); // Assuming your Event entity has getEventId()
        dto.setEventName(event.getEventName());
        dto.setDescription(event.getDescription());
        dto.setParticipantsCount(event.getParticipantsCount());
        dto.setDuration(event.getDuration());
        dto.setEventType(event.getEventType());
        dto.setFundingSource(event.getFundingSource());
        dto.setStatus(event.getStatus());
        // request_id is intentionally omitted as per previous discussions
        return dto;
    }

    // Create Event with associated Requests
    @Override
    public Event createEvent(EventCreationRequestDTO eventCreationRequestDTO) {
        int participantcount=0;

        Event newEvent = eventCreationRequestDTO.getNewEvent();
        List<Long> requests = eventCreationRequestDTO.getRequests();

        // Calculate total participants from associated requests
        for (Long requestId : requests) {
            Optional<Request> optionalRequest = requestRepository.findById(requestId);
            if (optionalRequest.isPresent()) {
                Request request = optionalRequest.get();
                participantcount+=request.getNoOfParticipants();
            }
        }

        // Set the calculated participants count to the new event
        newEvent.setParticipantsCount(participantcount);
        Event savedEvent = eventRepository.save(newEvent);

        // Associate requests with the newly created event
        for (Long requestId : requests) {
            Optional<Request> optionalRequest = requestRepository.findById(requestId);
            if (optionalRequest.isPresent()) {
                Request request = optionalRequest.get();
                request.setEvent(newEvent);
                this.requestRepository.save(request);
            }
        }
        return savedEvent;
    }

    // Get all new approved requests not assigned to any event
    @Override
    public List<Request> getAllNewApprovedRequestsNotAssignedToEvent() {
        Collection<Request> collectionRequests=this.requestRepository.getAllNewApprovedRequestsNotAssignedToEvent();
        List<Request> requests= new ArrayList<>(collectionRequests);
        return requests;
    }

    // Search Requests by ID
    @Override
    public Request getAllRequestsForSeachById(Long requestid) {
        return this.requestRepository.findById(requestid).get();
    }

    // Search Requests by Name
    @Override
    public List<Request> getAllRequestsForSeachByName(String requestName) {
        return this.requestRepository.findByJustificationContainsIgnoreCase(requestName);
    }

    // Delete Event and dissociate from Requests
    @Override
    public Event deleteEvent(Long eventid) {

        Optional<Event> optionalEvent=this.eventRepository.findById(eventid);
        if (optionalEvent.isPresent()) {
            Event event=optionalEvent.get();
            List<Request> requests = this.requestRepository.findByEvent(event);

            // Dissociate each request from the event
            for (Request request : requests) {
                request.setEvent(null);
                this.requestRepository.save(request);
            }
            this.eventRepository.delete(event);
            return event;
        }

        return null;
    }

}
