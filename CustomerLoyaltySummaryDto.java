package com.example.retailloyalty.dto;

public class CustomerLoyaltySummaryDto {

    private Long customerId;
    private String customerName;
    private String customerEmail;
    private Integer lifetimePoints;
    private Integer availablePoints;
    private String currentTierName;
    private Double tierDiscountPercentage;
    private String nextTierName;
    private Integer pointsNeededForNextTier;

    public CustomerLoyaltySummaryDto() {
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
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

    public String getNextTierName() {
        return nextTierName;
    }

    public void setNextTierName(String nextTierName) {
        this.nextTierName = nextTierName;
    }

    public Integer getPointsNeededForNextTier() {
        return pointsNeededForNextTier;
    }

    public void setPointsNeededForNextTier(Integer pointsNeededForNextTier) {
        this.pointsNeededForNextTier = pointsNeededForNextTier;
    }
}
