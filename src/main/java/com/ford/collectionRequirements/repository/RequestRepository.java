package com.ford.collectionRequirements.repository;

import com.ford.collectionRequirements.entity.Event;
import com.ford.collectionRequirements.entity.Request;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Collection;


@Repository
public interface RequestRepository extends JpaRepository<Request,Long> {


    Long countByUser_UserId(Long userId); // Counts all requests for a user
    Long countByUser_UserIdAndRequestStatus(Long userId, String requestStatus); // Counts requests by user and status

    long count();
    Long countByRequestStatus(String requestStatus);

    //---------------------------------------------------------------


    List<Request> findByEvent_EventId(Long eventId);
    @Query("SELECT request FROM Request request WHERE request.requestStatus = 'APPROVED' and request.event IS NULL ")
    Collection<Request> getAllNewApprovedRequestsNotAssignedToEvent();

    List<Request> findByJustificationContainsIgnoreCase(String requestName);

    List<Request> findByEvent(Event event);





    List<Request> findAll(Specification<Request> spec);
}

