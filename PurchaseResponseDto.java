package com.example.retailloyalty.dto;

import java.time.LocalDateTime;

public class PurchaseResponseDto {

    private Long purchaseId;
    private Long customerId;
    private String customerName;
    private Double purchaseAmount;
    private String tierName;
    private Double tierDiscountPercentage;
    private Double tierDiscount;
    private Integer pointsRedeemed;
    private Double redemptionDiscount;
    private Double finalAmount;
    private Integer pointsEarned;
    private Integer previousLifetimePoints;
    private Integer updatedLifetimePoints;
    private Integer previousAvailablePoints;
    private Integer updatedAvailablePoints;
    private boolean tierUpgraded;
    private String previousTierName;
    private String newTierName;
    private LocalDateTime purchaseDate;

    public PurchaseResponseDto() {
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
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

    public Double getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(Double purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public Double getTierDiscountPercentage() {
        return tierDiscountPercentage;
    }

    public void setTierDiscountPercentage(Double tierDiscountPercentage) {
        this.tierDiscountPercentage = tierDiscountPercentage;
    }

    public Double getTierDiscount() {
        return tierDiscount;
    }

    public void setTierDiscount(Double tierDiscount) {
        this.tierDiscount = tierDiscount;
    }

    public Integer getPointsRedeemed() {
        return pointsRedeemed;
    }

    public void setPointsRedeemed(Integer pointsRedeemed) {
        this.pointsRedeemed = pointsRedeemed;
    }

    public Double getRedemptionDiscount() {
        return redemptionDiscount;
    }

    public void setRedemptionDiscount(Double redemptionDiscount) {
        this.redemptionDiscount = redemptionDiscount;
    }

    public Double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Integer getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public Integer getPreviousLifetimePoints() {
        return previousLifetimePoints;
    }

    public void setPreviousLifetimePoints(Integer previousLifetimePoints) {
        this.previousLifetimePoints = previousLifetimePoints;
    }

    public Integer getUpdatedLifetimePoints() {
        return updatedLifetimePoints;
    }

    public void setUpdatedLifetimePoints(Integer updatedLifetimePoints) {
        this.updatedLifetimePoints = updatedLifetimePoints;
    }

    public Integer getPreviousAvailablePoints() {
        return previousAvailablePoints;
    }

    public void setPreviousAvailablePoints(Integer previousAvailablePoints) {
        this.previousAvailablePoints = previousAvailablePoints;
    }

    public Integer getUpdatedAvailablePoints() {
        return updatedAvailablePoints;
    }

    public void setUpdatedAvailablePoints(Integer updatedAvailablePoints) {
        this.updatedAvailablePoints = updatedAvailablePoints;
    }

    public boolean isTierUpgraded() {
        return tierUpgraded;
    }

    public void setTierUpgraded(boolean tierUpgraded) {
        this.tierUpgraded = tierUpgraded;
    }

    public String getPreviousTierName() {
        return previousTierName;
    }

    public void setPreviousTierName(String previousTierName) {
        this.previousTierName = previousTierName;
    }

    public String getNewTierName() {
        return newTierName;
    }

    public void setNewTierName(String newTierName) {
        this.newTierName = newTierName;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
