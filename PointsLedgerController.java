package com.example.retailloyalty.controller;

import com.example.retailloyalty.dto.PointsLedgerResponseDto;
import com.example.retailloyalty.service.PointsLedgerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers/{customerId}/points-history")
public class PointsLedgerController {

    private final PointsLedgerService pointsLedgerService;

    public PointsLedgerController(PointsLedgerService pointsLedgerService) {
        this.pointsLedgerService = pointsLedgerService;
    }

    @GetMapping
    public ResponseEntity<List<PointsLedgerResponseDto>> getPointsHistory(@PathVariable Long customerId) {
        List<PointsLedgerResponseDto> history = pointsLedgerService.getPointsHistoryByCustomerId(customerId);
        return ResponseEntity.ok(history);
    }
}
