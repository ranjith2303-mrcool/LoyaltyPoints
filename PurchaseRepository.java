package com.example.retailloyalty.repository;

import com.example.retailloyalty.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByCustomerIdOrderByPurchaseDateDesc(Long customerId);
}
