package com.example.retailloyalty.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TierRequestDto {

    @NotBlank(message = "Tier name is required")
    private String name;

    @NotNull(message = "Minimum points is required")
    @Min(value = 0, message = "Minimum points cannot be negative")
    private Integer minimumPoints;

    @NotNull(message = "Discount percentage is required")
    @Min(value = 0, message = "Discount percentage cannot be negative")
    @Max(value = 100, message = "Discount percentage cannot exceed 100")
    private Double discountPercentage;

    public TierRequestDto() {
    }

    public TierRequestDto(String name, Integer minimumPoints, Double discountPercentage) {
        this.name = name;
        this.minimumPoints = minimumPoints;
        this.discountPercentage = discountPercentage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMinimumPoints() {
        return minimumPoints;
    }

    public void setMinimumPoints(Integer minimumPoints) {
        this.minimumPoints = minimumPoints;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
}
