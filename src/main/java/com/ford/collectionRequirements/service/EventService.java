package com.ford.collectionRequirements.service;

import com.ford.collectionRequirements.dto.EventCreationRequestDTO;
import com.ford.collectionRequirements.event.Event;
import com.ford.collectionRequirements.request.Request;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface EventService {

    List<Event> getAllEvents();

    Event createEvent(EventCreationRequestDTO eventCreationRequestDTO);

    List<Request> getAllNewApprovedRequestsNotAssignedToEvent();

    Request getAllRequestsForSeachById(Long requestid);

    List<Request> getAllRequestsForSeachByName(String requestName);

    Event deleteEvent(Long eventid);


}
