package com.example.retailloyalty.dto;

import java.time.LocalDateTime;

public class CustomerResponseDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Integer lifetimePoints;
    private Integer availablePoints;
    private Long currentTierId;
    private String currentTierName;
    private Double tierDiscountPercentage;
    private LocalDateTime createdAt;

    public CustomerResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getLifetimePoints() {
        return lifetimePoints;
    }

    public void setLifetimePoints(Integer lifetimePoints) {
        this.lifetimePoints = lifetimePoints;
    }

    public Integer getAvailablePoints() {
        return availablePoints;
    }

    public void setAvailablePoints(Integer availablePoints) {
        this.availablePoints = availablePoints;
    }

    public Long getCurrentTierId() {
        return currentTierId;
    }

    public void setCurrentTierId(Long currentTierId) {
        this.currentTierId = currentTierId;
    }

    public String getCurrentTierName() {
        return currentTierName;
    }

    public void setCurrentTierName(String currentTierName) {
        this.currentTierName = currentTierName;
    }

    public Double getTierDiscountPercentage() {
        return tierDiscountPercentage;
    }

    public void setTierDiscountPercentage(Double tierDiscountPercentage) {
        this.tierDiscountPercentage = tierDiscountPercentage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
