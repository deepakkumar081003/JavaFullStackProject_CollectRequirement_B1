package com.ford.collectionRequirements.service.impl;

import com.ford.collectionRequirements.dto.EventCreationRequestDTO;
import com.ford.collectionRequirements.event.Event;
import com.ford.collectionRequirements.repository.EventRepository;
import com.ford.collectionRequirements.repository.RequestRepository;
import com.ford.collectionRequirements.request.Request;
import com.ford.collectionRequirements.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
