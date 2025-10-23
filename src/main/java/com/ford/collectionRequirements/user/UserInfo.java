package com.ford.collectionRequirements.user;

import com.ford.collectionRequirements.approval.Approval;
import com.ford.collectionRequirements.request.Request;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ford.collectionRequirements.department.Department;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.ford.collectionRequirements.region.Region;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode()
@Entity
@Table(name = "users")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "email", length = 300, nullable = false, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(length = 100, nullable = false)
    private String role;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    @ManyToOne
    @JoinColumn(name = "manager_id", referencedColumnName = "user_id")
    @JsonIgnore
    private UserInfo manager;


    @ManyToMany(mappedBy = "requestedParticipants")
    @JsonIgnore // Prevent recursion for self-reference
    private List<Request> requests;

    @OneToMany(mappedBy = "approvedBy")
    @JsonIgnore // Prevent recursion for self-reference
    private List<Approval> approval;


}