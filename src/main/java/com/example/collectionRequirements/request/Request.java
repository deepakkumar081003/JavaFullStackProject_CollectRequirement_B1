package com.example.collectionRequirements.request;

import com.example.collectionRequirements.approval.Approval;
import com.example.collectionRequirements.event.Event;
import department.Department;
import jakarta.persistence.*;
import user.UserInfo;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Request {

    @Id
    @GeneratedValue
    private Long requestId;

    @OneToOne
    @JoinColumn
    private UserInfo user;

    @ManyToOne
    private Department departmentId;

    @ManyToOne
    @JoinColumn(name="eventId")
    private Event event;

    @OneToOne(mappedBy = "request")
    private Approval approval;

    private LocalDate requestDate;

    private String requestStatus;

    private Boolean groupRequest;

    private String justification;

    private String TAN_Number;

    private String curriculamLink;

    private Long noOfParticipants;

    @ManyToMany
    @JoinTable(
            name="RequestedParticipants",
            joinColumns = @JoinColumn(name="Request_Id",referencedColumnName = "requestId"),
            inverseJoinColumns = @JoinColumn(name="User_Id",referencedColumnName = "UserId")
    )
    private List<UserInfo> requestedParticipants;

    public Request() {
    }

    public Request(Long requestId, UserInfo user, Department departmentId, Event event, Approval approval, LocalDate requestDate, String requestStatus, Boolean groupRequest, String justification, String TAN_Number, String curriculamLink, Long noOfParticipants, List<UserInfo> requestedParticipants) {
        this.requestId = requestId;
        this.user = user;
        this.departmentId = departmentId;
        this.event = event;
        this.approval = approval;
        this.requestDate = requestDate;
        this.requestStatus = requestStatus;
        this.groupRequest = groupRequest;
        this.justification = justification;
        this.TAN_Number = TAN_Number;
        this.curriculamLink = curriculamLink;
        this.noOfParticipants = noOfParticipants;
        this.requestedParticipants = requestedParticipants;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public Department getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Department departmentId) {
        this.departmentId = departmentId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Approval getApproval() {
        return approval;
    }

    public void setApproval(Approval approval) {
        this.approval = approval;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Boolean getGroupRequest() {
        return groupRequest;
    }

    public void setGroupRequest(Boolean groupRequest) {
        this.groupRequest = groupRequest;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getTAN_Number() {
        return TAN_Number;
    }

    public void setTAN_Number(String TAN_Number) {
        this.TAN_Number = TAN_Number;
    }

    public String getCurriculamLink() {
        return curriculamLink;
    }

    public void setCurriculamLink(String curriculamLink) {
        this.curriculamLink = curriculamLink;
    }

    public Long getNoOfParticipants() {
        return noOfParticipants;
    }

    public void setNoOfParticipants(Long noOfParticipants) {
        this.noOfParticipants = noOfParticipants;
    }

    public List<UserInfo> getRequestedParticipants() {
        return requestedParticipants;
    }

    public void setRequestedParticipants(List<UserInfo> requestedParticipants) {
        this.requestedParticipants = requestedParticipants;
    }
}
