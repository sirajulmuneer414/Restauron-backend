package dev.siraj.restauron.entity.users;

import dev.siraj.restauron.entity.enums.AccountStatus;
import dev.siraj.restauron.entity.enums.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


// UserAll entity representing all users in the system

@Entity
@Data

public class UserAll implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO )
    private Long id;

    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    private String phone;

    @Enumerated(value = EnumType.STRING)
    private Roles role;

    @Enumerated(value = EnumType.STRING)
    private AccountStatus status;


}
