package com.example.retailloyalty.service;

import com.example.retailloyalty.dto.PointsLedgerResponseDto;
import com.example.retailloyalty.model.Customer;
import com.example.retailloyalty.model.PointsLedger;
import com.example.retailloyalty.model.Purchase;
import com.example.retailloyalty.model.TransactionType;
import com.example.retailloyalty.repository.PointsLedgerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PointsLedgerService {

    private final PointsLedgerRepository pointsLedgerRepository;

    public PointsLedgerService(PointsLedgerRepository pointsLedgerRepository) {
        this.pointsLedgerRepository = pointsLedgerRepository;
    }

    @Transactional
    public PointsLedger createLedgerEntry(Customer customer, Purchase purchase, TransactionType type, Integer points, String description) {
        PointsLedger ledger = new PointsLedger(customer, purchase, type, points, description);
        return pointsLedgerRepository.save(ledger);
    }

    @Transactional(readOnly = true)
    public List<PointsLedgerResponseDto> getPointsHistoryByCustomerId(Long customerId) {
        return pointsLedgerRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public PointsLedgerResponseDto mapToResponseDto(PointsLedger ledger) {
        PointsLedgerResponseDto dto = new PointsLedgerResponseDto();
        dto.setId(ledger.getId());
        dto.setCustomerId(ledger.getCustomer() != null ? ledger.getCustomer().getId() : null);
        dto.setPurchaseId(ledger.getPurchase() != null ? ledger.getPurchase().getId() : null);
        dto.setTransactionType(ledger.getTransactionType() != null ? ledger.getTransactionType().name() : null);
        dto.setPoints(ledger.getPoints());
        dto.setDescription(ledger.getDescription());
        dto.setCreatedAt(ledger.getCreatedAt());
        return dto;
    }
}
