package com.example.retailloyalty.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PurchaseRequestDto {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Purchase amount is required")
    @Positive(message = "Purchase amount must be positive")
    private Double purchaseAmount;

    @Min(value = 0, message = "Points to redeem cannot be negative")
    private Integer pointsToRedeem = 0;

    public PurchaseRequestDto() {
    }

    public PurchaseRequestDto(Long customerId, Double purchaseAmount, Integer pointsToRedeem) {
        this.customerId = customerId;
        this.purchaseAmount = purchaseAmount;
        this.pointsToRedeem = pointsToRedeem != null ? pointsToRedeem : 0;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Double getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(Double purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public Integer getPointsToRedeem() {
        return pointsToRedeem != null ? pointsToRedeem : 0;
    }

    public void setPointsToRedeem(Integer pointsToRedeem) {
        this.pointsToRedeem = pointsToRedeem;
    }
}
