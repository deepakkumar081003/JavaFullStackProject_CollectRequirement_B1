package user;

import com.example.collectionRequirements.approval.Approval;
import com.example.collectionRequirements.request.Request;
import department.Department;
import jakarta.persistence.*;
import region.Region;

import java.util.List;

@Entity
public class UserInfo {

    @Id
    @GeneratedValue
    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    @ManyToOne
    private Department department;

    private String role;

    @ManyToOne
    private Region region;

    //doubt need to be asked :Dharshan,Kevin
    @OneToOne
    @JoinColumn(name = "managerId",referencedColumnName = "userId")
    private UserInfo manager;

    @ManyToMany(mappedBy = "requestedParticipants")
    private List<Request> requests;

    @OneToOne(mappedBy = "approvedBy")
    private Approval approval;

    public UserInfo() {
    }

    public UserInfo(Long userId, String firstName, String lastName, String email, Department department, String role, Region region, UserInfo manager, List<Request> requests, Approval approval) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
        this.role = role;
        this.region = region;
        this.manager = manager;
        this.requests = requests;
        this.approval = approval;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public UserInfo getManager() {
        return manager;
    }

    public void setManager(UserInfo manager) {
        this.manager = manager;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    public Approval getApproval() {
        return approval;
    }

    public void setApproval(Approval approval) {
        this.approval = approval;
    }
}
