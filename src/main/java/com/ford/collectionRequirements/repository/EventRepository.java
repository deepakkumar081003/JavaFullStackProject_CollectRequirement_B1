package com.ford.collectionRequirements.repository;

import com.ford.collectionRequirements.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {

    @Query("SELECT e FROM Event e WHERE " +
            // Search by eventId (converted to string) OR eventName using searchTerm
            "(LOWER(CAST(e.eventId AS string)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.eventName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            ":searchTerm IS NULL) AND " + // If searchTerm is null, this part of the condition is always true
            // Filter by description
            "(LOWER(e.description) LIKE LOWER(CONCAT('%', :description, '%')) OR :description IS NULL) AND " +
            // Filter by eventType
            "(LOWER(e.eventType) LIKE LOWER(CONCAT('%', :eventType, '%')) OR :eventType IS NULL) AND " +
            // Filter by status
            "(LOWER(e.status) LIKE LOWER(CONCAT('%', :status, '%')) OR :status IS NULL)")
    List<Event> findByFilters(
            @Param("searchTerm") String searchTerm,
            @Param("description") String description,
            @Param("eventType") String eventType,
            @Param("status") String status
    );

}
