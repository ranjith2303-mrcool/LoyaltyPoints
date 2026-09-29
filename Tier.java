package com.example.retailloyalty.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity representing a membership tier in the loyalty program.
 * Examples: Silver (0 points, 0%), Gold (1000 points, 10%), Platinum (5000 points, 20%).
 */
@Entity
@Table(name = "tiers")
public class Tier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer minimumPoints;

    @Column(nullable = false)
    private Double discountPercentage;

    public Tier() {
    }

    public Tier(Long id, String name, Integer minimumPoints, Double discountPercentage) {
        this.id = id;
        this.name = name;
        this.minimumPoints = minimumPoints;
        this.discountPercentage = discountPercentage;
    }

    public Tier(String name, Integer minimumPoints, Double discountPercentage) {
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
