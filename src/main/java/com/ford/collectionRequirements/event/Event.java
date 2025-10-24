package com.ford.collectionRequirements.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ford.collectionRequirements.request.Request;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode()
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "event_name", length = 255, nullable = false)
    private String eventName;

    @Column(name ="description",columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy="event")
    @JsonIgnore
    private List<Request> requests;

    @Column(name="participants_count")
    private Integer participantsCount;

    @Column(name ="duration")
    private Integer duration;

    @Column(name = "event_type",length = 100)
    private String eventType;

    @Column(name = "funding_source",length = 100)
    private String fundingSource;

    @Column(name="status",length = 50)
    private String status;
}