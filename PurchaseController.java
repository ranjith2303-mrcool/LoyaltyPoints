package com.example.retailloyalty.controller;

import com.example.retailloyalty.dto.PurchaseRequestDto;
import com.example.retailloyalty.dto.PurchaseResponseDto;
import com.example.retailloyalty.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping("/api/purchases")
    public ResponseEntity<PurchaseResponseDto> createPurchase(@Valid @RequestBody PurchaseRequestDto requestDto) {
        PurchaseResponseDto responseDto = purchaseService.processPurchase(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/api/purchases/{id}")
    public ResponseEntity<PurchaseResponseDto> getPurchaseById(@PathVariable Long id) {
        PurchaseResponseDto responseDto = purchaseService.getPurchaseById(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/api/customers/{customerId}/purchases")
    public ResponseEntity<List<PurchaseResponseDto>> getPurchasesByCustomerId(@PathVariable Long customerId) {
        List<PurchaseResponseDto> purchases = purchaseService.getPurchasesByCustomerId(customerId);
        return ResponseEntity.ok(purchases);
    }
}
