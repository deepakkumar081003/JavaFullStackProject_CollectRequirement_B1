package com.example.collectionRequirements.event;

import com.example.collectionRequirements.request.Request;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Event {

    @Id
    @GeneratedValue
    private Long eventId;

    private String eventName;

    private String description;

    @OneToMany(mappedBy="event")
    private List<Request> requests;

    private Integer participantsCount;

    private Integer duration;

    private String eventType;

    private String fundingSource;

    private String status;

    public Event() {
    }

    public Event(Long eventId, String eventName, String description, List<Request> requests, Integer participantsCount, Integer duration, String eventType, String fundingSource, String status) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.description = description;
        this.requests = requests;
        this.participantsCount = participantsCount;
        this.duration = duration;
        this.eventType = eventType;
        this.fundingSource = fundingSource;
        this.status = status;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    public Integer getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(Integer participantsCount) {
        this.participantsCount = participantsCount;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(String fundingSource) {
        this.fundingSource = fundingSource;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
