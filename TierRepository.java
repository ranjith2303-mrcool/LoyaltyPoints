package com.example.retailloyalty.repository;

import com.example.retailloyalty.model.Tier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TierRepository extends JpaRepository<Tier, Long> {

    List<Tier> findAllByOrderByMinimumPointsAsc();

    List<Tier> findAllByOrderByMinimumPointsDesc();

    Optional<Tier> findByNameIgnoreCase(String name);

    Optional<Tier> findFirstByOrderByMinimumPointsAsc();
}
