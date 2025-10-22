package com.ford.collectionRequirements.repository;

import com.ford.collectionRequirements.approval.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval,Integer> {
}
