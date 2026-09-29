package com.example.retailloyalty.service;

import com.example.retailloyalty.dto.TierRequestDto;
import com.example.retailloyalty.dto.TierResponseDto;
import com.example.retailloyalty.exception.InvalidTierException;
import com.example.retailloyalty.exception.ResourceNotFoundException;
import com.example.retailloyalty.model.Tier;
import com.example.retailloyalty.repository.TierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TierService {

    private final TierRepository tierRepository;

    public TierService(TierRepository tierRepository) {
        this.tierRepository = tierRepository;
    }

    @Transactional(readOnly = true)
    public List<TierResponseDto> getAllTiers() {
        return tierRepository.findAllByOrderByMinimumPointsAsc()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TierResponseDto getTierById(Long id) {
        Tier tier = getTierEntityById(id);
        return mapToResponseDto(tier);
    }

    @Transactional(readOnly = true)
    public Tier getTierEntityById(Long id) {
        return tierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Tier getDefaultTier() {
        return tierRepository.findFirstByOrderByMinimumPointsAsc()
                .orElseThrow(() -> new ResourceNotFoundException("No loyalty tiers are configured in the database"));
    }

    @Transactional
    public TierResponseDto createTier(TierRequestDto requestDto) {
        tierRepository.findByNameIgnoreCase(requestDto.getName()).ifPresent(t -> {
            throw new InvalidTierException("Tier already exists with name: " + requestDto.getName());
        });

        Tier tier = new Tier();
        tier.setName(requestDto.getName().trim());
        tier.setMinimumPoints(requestDto.getMinimumPoints());
        tier.setDiscountPercentage(requestDto.getDiscountPercentage());

        Tier savedTier = tierRepository.save(tier);
        return mapToResponseDto(savedTier);
    }

    @Transactional
    public TierResponseDto updateTier(Long id, TierRequestDto requestDto) {
        Tier existingTier = getTierEntityById(id);

        tierRepository.findByNameIgnoreCase(requestDto.getName()).ifPresent(t -> {
            if (!t.getId().equals(id)) {
                throw new InvalidTierException("Another tier already exists with name: " + requestDto.getName());
            }
        });

        existingTier.setName(requestDto.getName().trim());
        existingTier.setMinimumPoints(requestDto.getMinimumPoints());
        existingTier.setDiscountPercentage(requestDto.getDiscountPercentage());

        Tier updatedTier = tierRepository.save(existingTier);
        return mapToResponseDto(updatedTier);
    }

    @Transactional
    public void deleteTier(Long id) {
        Tier tier = getTierEntityById(id);
        tierRepository.delete(tier);
    }

    public TierResponseDto mapToResponseDto(Tier tier) {
        return new TierResponseDto(
                tier.getId(),
                tier.getName(),
                tier.getMinimumPoints(),
                tier.getDiscountPercentage()
        );
    }
}
