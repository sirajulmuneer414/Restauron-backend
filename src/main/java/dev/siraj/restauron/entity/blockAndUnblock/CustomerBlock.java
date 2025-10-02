package dev.siraj.restauron.entity.blockAndUnblock;

import dev.siraj.restauron.entity.users.UserAll;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class CustomerBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private UserAll user;

    private String subject;

    @Column(name = "blocked_at")
    private LocalDateTime blockedAt = LocalDateTime.now();

    @Lob
    private String description;

}
