package com.example.retailloyalty.dto;

public class TierResponseDto {

    private Long id;
    private String name;
    private Integer minimumPoints;
    private Double discountPercentage;

    public TierResponseDto() {
    }

    public TierResponseDto(Long id, String name, Integer minimumPoints, Double discountPercentage) {
        this.id = id;
        this.name = name;
        this.minimumPoints = minimumPoints;
        this.discountPercentage = discountPercentage;
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
