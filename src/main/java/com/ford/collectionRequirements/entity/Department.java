package com.ford.collectionRequirements.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Integer departmentId;

    @Column(length = 255, nullable = false)
    private String departmentName;

//    @OneToMany
//    @JoinColumn(name = "manager_id", referencedColumnName = "user_id")
//    private List<UserInfo> managers;

    //@OneToOne
    //private Budget budget

    @OneToMany(mappedBy = "department")
    @JsonIgnore
    private List<UserInfo> users;

    @OneToMany(mappedBy = "department")
    @JsonIgnore
    private List<Request> requests;

}