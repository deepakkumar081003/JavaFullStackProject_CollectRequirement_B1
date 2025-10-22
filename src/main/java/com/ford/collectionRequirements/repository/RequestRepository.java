package com.ford.collectionRequirements.repository;

import com.ford.collectionRequirements.request.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<Request,Integer> {
}
