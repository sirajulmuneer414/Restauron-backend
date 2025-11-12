package dev.siraj.restauron.entity.restaurant;

import dev.siraj.restauron.entity.enums.AccountStatus;
import dev.siraj.restauron.entity.users.Employee;
import dev.siraj.restauron.entity.users.Owner;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true) // Email should be unique
    private String email;

    @Column(nullable = false)
    private String phone;

    private String address;

    private String district;

    private String state;

    private String pincode;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private Owner owner;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude // IMPORTANT: Prevents infinite loops in toString()
    private List<Employee> employees = new ArrayList<>();


    // --- Helper methods for bidirectional relationship ---
    // These are good practice for keeping both sides of the relationship in sync

    public void addEmployee(Employee employee) {
        this.employees.add(employee);
        employee.setRestaurant(this);
    }

    public void removeEmployee(Employee employee) {
        this.employees.remove(employee);
        employee.setRestaurant(null);
    }
}
