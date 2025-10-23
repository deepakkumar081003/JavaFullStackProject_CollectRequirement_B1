package com.ford.collectionRequirements.service.impl;

import com.ford.collectionRequirements.repository.EventRepository;
import com.ford.collectionRequirements.repository.RequestRepository;
import com.ford.collectionRequirements.request.Request;
import com.ford.collectionRequirements.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {


    private EventRepository eventRepository;
    private RequestRepository requestRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, RequestRepository requestRepository) {
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
    }



}
