package com.ford.collectionRequirements.repository;

import com.ford.collectionRequirements.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event,Integer> {
}
