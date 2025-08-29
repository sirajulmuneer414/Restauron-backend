package dev.siraj.restauron.entity.users;

import dev.siraj.restauron.entity.enums.AccountStatus;
import jakarta.persistence.*;

@Entity
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)  // Foreign key column in Admin table
    private UserAll user;

    @Enumerated(value = EnumType.STRING)
    private AccountStatus adminStatus;

    public Long getId() {
        return id;
    }

    public UserAll getUser() {
        return user;
    }

    public void setUser(UserAll admin) {
        this.user = admin;
    }

    public AccountStatus getAdminStatus() {
        return adminStatus;
    }

    public void setAdminStatus(AccountStatus adminStatus) {
        this.adminStatus = adminStatus;
    }
}
