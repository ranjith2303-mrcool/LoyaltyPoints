package com.example.retailloyalty.repository;

import com.example.retailloyalty.model.PointsLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointsLedgerRepository extends JpaRepository<PointsLedger, Long> {

    List<PointsLedger> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
