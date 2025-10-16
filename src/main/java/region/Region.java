package region;

import jakarta.persistence.*;
import user.UserInfo;

import java.util.List;

@Entity
public class Region {

    @Id
    @GeneratedValue
    private Long regionId;

    private String regionName;

    private String location;

    @OneToMany(mappedBy = "region")
    private List<UserInfo> users;

    public Region() {
    }

    public Region(Long regionId, String regionName, String location, List<UserInfo> users) {
        this.regionId = regionId;
        this.regionName = regionName;
        this.location = location;
        this.users = users;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }
}
