package com.example.retailloyalty.controller;

import com.example.retailloyalty.dto.TierRequestDto;
import com.example.retailloyalty.dto.TierResponseDto;
import com.example.retailloyalty.service.TierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tiers")
public class TierController {

    private final TierService tierService;

    public TierController(TierService tierService) {
        this.tierService = tierService;
    }

    @GetMapping
    public ResponseEntity<List<TierResponseDto>> getAllTiers() {
        List<TierResponseDto> tiers = tierService.getAllTiers();
        return ResponseEntity.ok(tiers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TierResponseDto> getTierById(@PathVariable Long id) {
        TierResponseDto tier = tierService.getTierById(id);
        return ResponseEntity.ok(tier);
    }

    @PostMapping
    public ResponseEntity<TierResponseDto> createTier(@Valid @RequestBody TierRequestDto requestDto) {
        TierResponseDto createdTier = tierService.createTier(requestDto);
        return new ResponseEntity<>(createdTier, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TierResponseDto> updateTier(@PathVariable Long id,
                                                      @Valid @RequestBody TierRequestDto requestDto) {
        TierResponseDto updatedTier = tierService.updateTier(id, requestDto);
        return ResponseEntity.ok(updatedTier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTier(@PathVariable Long id) {
        tierService.deleteTier(id);
        return ResponseEntity.noContent().build();
    }
}
