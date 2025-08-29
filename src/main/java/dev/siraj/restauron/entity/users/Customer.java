package dev.siraj.restauron.entity.users;

import jakarta.persistence.*;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserAll customerDetails;



    public Long getId() {
        return id;
    }

    public UserAll getCustomer() {

        return customerDetails;
    }

    public void setCustomer(UserAll customer) {


        this.customerDetails = customer;
    }
}
