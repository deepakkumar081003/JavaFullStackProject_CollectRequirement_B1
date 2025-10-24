package com.ford.collectionRequirements.service.impl;

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

import java.util.List;
import java.util.Optional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {


    private EventRepository eventRepository;
    private RequestRepository requestRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, RequestRepository requestRepository) {
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public Event getEventById(Long id) {
        Optional<Event> event = eventRepository.findById(id);
        return event.orElse(null);
    }

    @Override
    public List<Request> getRequestsByEventId(Long eventId) {

        return requestRepository.findByEvent_EventId(eventId);
    }
    @Override
    @Transactional
    public Event updateEvent(Long eventId, Event updatedEvent) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        existingEvent.setEventName(updatedEvent.getEventName());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setParticipantsCount(updatedEvent.getParticipantsCount());
        existingEvent.setDuration(updatedEvent.getDuration());
        existingEvent.setEventType(updatedEvent.getEventType());
        existingEvent.setFundingSource(updatedEvent.getFundingSource());
        existingEvent.setStatus(updatedEvent.getStatus());
        return eventRepository.save(existingEvent);
    }
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
        event.setParticipantsCount(eventParticipants + requestParticipants.intValue());
        eventRepository.save(event);
        return requestRepository.save(request);
    }
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

        int updatedCount = eventParticipants - requestParticipants.intValue();
        event.setParticipantsCount(updatedCount);
        eventRepository.save(event);
        return requestRepository.save(request);
    }



    @Override
    public List<Event> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events;
    }

    @Override
    public Event createEvent(EventCreationRequestDTO eventCreationRequestDTO) {
        int participantcount=0;

        Event newEvent = eventCreationRequestDTO.getNewEvent();
        List<Long> requests = eventCreationRequestDTO.getRequests();

        for (Long requestId : requests) {
            Optional<Request> optionalRequest = requestRepository.findById(requestId);
            if (optionalRequest.isPresent()) {
                Request request = optionalRequest.get();
                participantcount+=request.getNoOfParticipants();
            }
        }

        newEvent.setParticipantsCount(participantcount);
        Event savedEvent = eventRepository.save(newEvent);

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

    @Override
    public List<Request> getAllNewApprovedRequestsNotAssignedToEvent() {
        Collection<Request> collectionRequests=this.requestRepository.getAllNewApprovedRequestsNotAssignedToEvent();
        List<Request> requests= new ArrayList<>(collectionRequests);
        return requests;
    }

    @Override
    public Request getAllRequestsForSeachById(Long requestid) {
        return this.requestRepository.findById(requestid).get();
    }

    @Override
    public List<Request> getAllRequestsForSeachByName(String requestName) {
        return this.requestRepository.findByJustificationContains(requestName);
    }

    @Override
    public Event deleteEvent(Long eventid) {

        Optional<Event> optionalEvent=this.eventRepository.findById(eventid);
        if (optionalEvent.isPresent()) {
            Event event=optionalEvent.get();
            List<Request> requests = this.requestRepository.findByEvent(event);
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
