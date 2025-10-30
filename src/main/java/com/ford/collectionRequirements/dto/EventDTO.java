package com.ford.collectionRequirements.dto;


import java.util.Objects; // Used for equals and hashCode methods

public class EventDTO {

    private Long eventId; // Matches frontend's eventId
    private String eventName; // Matches frontend's eventName
    private String description;
    private Integer participantsCount; // Matches frontend's participantsCount
    private Integer duration; // Matches frontend's duration (as number)
    private String eventType; // Matches frontend's eventType
    private String fundingSource; // Matches frontend's fundingSource
    private String status;

    // --- Constructors ---
    public EventDTO() {
        // Default constructor
    }

    public EventDTO(Long eventId, String eventName, String description, Integer participantsCount, String duration, String eventType, String fundingSource, String status) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.description = description;
        this.participantsCount = participantsCount;
        // Note: The constructor takes String duration, but the field is Integer.
        // This might need adjustment if your backend entity's duration is also String.
        // For consistency with the JSON response you provided earlier (duration: 30),
        // I've kept the field as Integer. If your backend Event entity has String duration,
        // you'll need to parse it here or adjust the DTO field type.
        try {
            this.duration = Integer.parseInt(duration);
        } catch (NumberFormatException e) {
            this.duration = 0; // Default or handle error appropriately
        }
        this.eventType = eventType;
        this.fundingSource = fundingSource;
        this.status = status;
    }

    // A more appropriate constructor if duration is always an Integer in the DTO
    public EventDTO(Long eventId, String eventName, String description, Integer participantsCount, Integer duration, String eventType, String fundingSource, String status) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.description = description;
        this.participantsCount = participantsCount;
        this.duration = duration;
        this.eventType = eventType;
        this.fundingSource = fundingSource;
        this.status = status;
    }


    // --- Getters and Setters (standard boilerplate) ---
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

    // --- equals, hashCode, and toString (good practice for DTOs) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventDTO eventDTO = (EventDTO) o;
        return Objects.equals(eventId, eventDTO.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "EventDTO{" +
                "eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                ", description='" + description + '\'' +
                ", participantsCount=" + participantsCount +
                ", duration=" + duration +
                ", eventType='" + eventType + '\'' +
                ", fundingSource='" + fundingSource + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

