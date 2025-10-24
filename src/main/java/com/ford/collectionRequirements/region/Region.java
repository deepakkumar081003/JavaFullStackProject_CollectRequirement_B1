package com.ford.collectionRequirements.region;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.ford.collectionRequirements.user.UserInfo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode()
@Entity
@Table(name = "region")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Long regionId;

    @Column(length = 255, nullable = false)
    private String regionName;

    @Column(length = 255)
    private String location;

    @OneToMany(mappedBy = "region")
    @JsonIgnore
    private List<UserInfo> users;

}
