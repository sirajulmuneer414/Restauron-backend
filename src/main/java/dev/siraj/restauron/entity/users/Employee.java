package dev.siraj.restauron.entity.users;

import dev.siraj.restauron.entity.restaurant.Restaurant;
import jakarta.persistence.*;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserAll user;

    @Column(nullable = false)
    private String personalEmail;

    @Column(nullable = false)
    private String adhaarNo;

    @Column(nullable = false)
    private String adhaarPhoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

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

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
