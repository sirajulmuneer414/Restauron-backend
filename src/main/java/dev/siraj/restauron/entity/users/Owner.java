package dev.siraj.restauron.entity.users;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserAll user;

    private String adhaarNo;

    private String adhaarPhoto;

    public Long getId() {
        return id;
    }

    public UserAll getUser() {
        return user;
    }

    public void setUser(UserAll user) {
        this.user = user;
    }

    public String getAdhaarNo() {
        return adhaarNo;
    }

    public void setAdhaarNo(String adhaarNo) {
        this.adhaarNo = adhaarNo;
    }

    public String getAdhaarPhoto() {
        return adhaarPhoto;
    }

    public void setAdhaarPhoto(String adhaarPhoto) {
        this.adhaarPhoto = adhaarPhoto;
    }

}
