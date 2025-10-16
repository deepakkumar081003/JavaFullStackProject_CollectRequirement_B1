package department;

import com.example.collectionRequirements.request.Request;
import jakarta.persistence.*;
import org.apache.catalina.User;
import user.UserInfo;

import java.util.List;

@Entity
public class Department {

    @Id
    @GeneratedValue
    private Long departmentId;

    private String departmentName;

    @OneToOne(mappedBy = "manager")
    private UserInfo manager;

    //@OneToOne
    //private Budget budget

    @OneToMany(mappedBy = "department")
    private List<UserInfo> users;

    @OneToMany(mappedBy = "department")
    private List<Request> requests;

    public Department() {
    }

    public Department(Long departmentId, String departmentName, UserInfo manager, List<UserInfo> users, List<Request> requests) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.manager = manager;
        this.users = users;
        this.requests = requests;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public UserInfo getManager() {
        return manager;
    }

    public void setManager(UserInfo manager) {
        this.manager = manager;
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }
}
