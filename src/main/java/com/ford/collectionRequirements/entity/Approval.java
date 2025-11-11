package com.ford.collectionRequirements.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode()
@Entity
@Table(name = "approvals")
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Long approvalId;

    @OneToOne
    @JoinColumn(name="request_id")
    @JsonIgnore // Prevent recursion
    private Request request;

    @ManyToOne
    @JoinColumn(name="approved_by", referencedColumnName = "user_id")
    @JsonIgnore // Prevent recursion
    private UserInfo approvedBy;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name="approval_status",length = 50)
    private String approvalStatus;

    @Column(name = "approval_notes",columnDefinition = "TEXT")
    private String approvalNotes;

}
