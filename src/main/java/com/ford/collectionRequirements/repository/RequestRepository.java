package com.ford.collectionRequirements.repository;

import com.ford.collectionRequirements.request.Request;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request,Long> {

    Long countByUser_UserId(Long userId); // Counts all requests for a user
    Long countByUser_UserIdAndRequestStatus(Long userId, String requestStatus); // Counts requests by user and status

    long count();
    Long countByRequestStatus(String requestStatus);



    List<Request> findAll(Specification<Request> spec);
}
