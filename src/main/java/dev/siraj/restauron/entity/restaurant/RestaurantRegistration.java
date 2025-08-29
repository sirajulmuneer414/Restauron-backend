package dev.siraj.restauron.entity.restaurant;

import dev.siraj.restauron.entity.enums.PendingStatuses;
import dev.siraj.restauron.entity.enums.VerificationStatus;
import jakarta.persistence.*;

@Entity
public class RestaurantRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ownerName;

    @Column(unique = true)
    private String ownerEmail;

    private String ownerPhone;

    private String password;

    private String ownerAdhaarNo;

    private String ownerAdhaarPhoto;

    private String restaurantName;

    private String restaurantEmail;

    private String restaurantPhone;

    private String restaurantAddress;

    private String district;

    private String state;

    private String pincode;

    @Enumerated(value = EnumType.STRING)
    private VerificationStatus otpStatus;

    @Enumerated(value = EnumType.STRING)
    private PendingStatuses status;


    public Long getId() {
        return id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerAdhaarNo() {
        return ownerAdhaarNo;
    }

    public void setOwnerAdhaarNo(String ownerAdhaarNo) {
        this.ownerAdhaarNo = ownerAdhaarNo;
    }

    public String getOwnerAdhaarPhoto() {
        return ownerAdhaarPhoto;
    }

    public void setOwnerAdhaarPhoto(String ownerAdhaarPhoto) {
        this.ownerAdhaarPhoto = ownerAdhaarPhoto;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantEmail() {
        return restaurantEmail;
    }

    public void setRestaurantEmail(String restaurantEmail) {
        this.restaurantEmail = restaurantEmail;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public PendingStatuses getStatus() {
        return status;
    }

    public void setStatus(PendingStatuses status) {
        this.status = status;
    }

    public VerificationStatus getOtpStatus() {
        return otpStatus;
    }

    public void setOtpStatus(VerificationStatus otpStatus) {
        this.otpStatus = otpStatus;
    }
}
