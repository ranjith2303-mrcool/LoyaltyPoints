package com.example.retailloyalty.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entity representing a retail purchase made by a customer.
 */
@Entity
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private Double purchaseAmount;

    @Column(nullable = false)
    private Double tierDiscount;

    @Column(nullable = false)
    private Integer pointsRedeemed;

    @Column(nullable = false)
    private Double redemptionDiscount;

    @Column(nullable = false)
    private Double finalAmount;

    @Column(nullable = false)
    private Integer pointsEarned;

    @Column(nullable = false, updatable = false)
    private LocalDateTime purchaseDate;

    public Purchase() {
    }

    @PrePersist
    protected void onCreate() {
        this.purchaseDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Double getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(Double purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
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

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
