package com.ford.collectionRequirements.request;

import com.ford.collectionRequirements.approval.Approval;
import com.ford.collectionRequirements.event.Event;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ford.collectionRequirements.department.Department;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.ford.collectionRequirements.user.UserInfo;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode()
@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "requestor_id")
    private UserInfo user;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @OneToOne(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Inverse side of one-to-one
    private Approval approval;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @Column(name = "request_status", length = 50)
    private String requestStatus;

    @Column(name = "group_request")
    private Boolean groupRequest;

    @Column(name = "justification", columnDefinition = "TEXT")
    private String justification;

    @Column(name = "TAN_number", length = 50)
    private String TAN_Number;

    @Column(name = "curriculum", columnDefinition = "TEXT")
    private String curriculamLink;

    @Column(name = "participants_count")
    private Long noOfParticipants;

    @ManyToMany
    @JoinTable(
            name = "RequestedParticipants",
            joinColumns = @JoinColumn(name = "Request_Id", referencedColumnName = "request_id"),
            inverseJoinColumns = @JoinColumn(name = "User_Id", referencedColumnName = "user_id")
    )
    @JsonIgnore // Owning side, but to prevent recursion
    private List<UserInfo> requestedParticipants;

}