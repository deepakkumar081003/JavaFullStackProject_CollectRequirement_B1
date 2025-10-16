package com.example.collectionRequirements.approval;

import com.example.collectionRequirements.request.Request;
import jakarta.persistence.*;
import user.UserInfo;


import java.time.LocalDate;

@Entity
public class Approval {
    @Id
    @GeneratedValue
    private Long approvalId;

    @OneToOne
    @JoinColumn
    private Request request;

    @OneToOne
    @JoinColumn
    private UserInfo approvedBy;

    private LocalDate approvalDate;

    private String approvalStatus;

    private String text;

    public Approval() {
    }

    public Approval(Long approvalId, Request request, UserInfo approvedBy, LocalDate approvalDate, String approvalStatus, String text) {
        this.approvalId = approvalId;
        this.request = request;
        this.approvedBy = approvedBy;
        this.approvalDate = approvalDate;
        this.approvalStatus = approvalStatus;
        this.text = text;
    }

    public Long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public UserInfo getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(UserInfo approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
