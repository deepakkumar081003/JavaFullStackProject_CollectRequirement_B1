package com.ford.collectionRequirements.repository;

import com.ford.collectionRequirements.event.Event;
import com.ford.collectionRequirements.request.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request,Long> {

    @Query("SELECT request FROM Request request WHERE request.requestStatus = 'APPROVED' and request.event IS NULL ")
    Collection<Request> getAllNewApprovedRequestsNotAssignedToEvent();

    List<Request> findByJustificationContains(String requestName);

    List<Request> findByEvent(Event event);
}
