package com.example.retailloyalty.service;

import com.example.retailloyalty.dto.PurchaseRequestDto;
import com.example.retailloyalty.dto.PurchaseResponseDto;
import com.example.retailloyalty.exception.InsufficientPointsException;
import com.example.retailloyalty.exception.InvalidPurchaseException;
import com.example.retailloyalty.exception.ResourceNotFoundException;
import com.example.retailloyalty.model.Customer;
import com.example.retailloyalty.model.PointsLedger;
import com.example.retailloyalty.model.Purchase;
import com.example.retailloyalty.model.Tier;
import com.example.retailloyalty.model.TransactionType;
import com.example.retailloyalty.repository.CustomerRepository;
import com.example.retailloyalty.repository.PurchaseRepository;
import com.example.retailloyalty.repository.TierRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final CustomerRepository customerRepository;
    private final TierRepository tierRepository;
    private final PointsLedgerService pointsLedgerService;

    @Value("${loyalty.points-per-rupee:1.0}")
    private double pointsPerRupee;

    @Value("${loyalty.redemption-rate-per-point:1.0}")
    private double redemptionRatePerPoint;

    public PurchaseService(PurchaseRepository purchaseRepository,
                           CustomerRepository customerRepository,
                           TierRepository tierRepository,
                           PointsLedgerService pointsLedgerService) {
        this.purchaseRepository = purchaseRepository;
        this.customerRepository = customerRepository;
        this.tierRepository = tierRepository;
        this.pointsLedgerService = pointsLedgerService;
    }

    @Transactional
    public PurchaseResponseDto processPurchase(PurchaseRequestDto requestDto) {
        // 1. Find the customer
        Customer customer = customerRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + requestDto.getCustomerId()));

        // 2. Validate the purchase amount
        double purchaseAmount = requestDto.getPurchaseAmount();
        if (purchaseAmount <= 0) {
            throw new InvalidPurchaseException("Purchase amount must be greater than zero");
        }

        int pointsToRedeem = requestDto.getPointsToRedeem();
        if (pointsToRedeem < 0) {
            throw new InvalidPurchaseException("Points to redeem cannot be negative");
        }

        // 3. Get the customer's current tier
        Tier currentTier = customer.getCurrentTier();
        double tierDiscountPercentage = currentTier != null ? currentTier.getDiscountPercentage() : 0.0;

        // 4. Calculate tier discount
        double tierDiscount = purchaseAmount * (tierDiscountPercentage / 100.0);

        // 5. Validate requested point redemption
        if (pointsToRedeem > customer.getAvailablePoints()) {
            throw new InsufficientPointsException("Insufficient available points for redemption. Available: " 
                    + customer.getAvailablePoints() + " points, requested: " + pointsToRedeem + " points");
        }

        // 6. Calculate the redemption discount
        double redemptionDiscount = pointsToRedeem * redemptionRatePerPoint;

        // 7. Calculate final purchase amount
        double subtotalAfterTierDiscount = purchaseAmount - tierDiscount;
        if (redemptionDiscount > subtotalAfterTierDiscount) {
            throw new InvalidPurchaseException("Redemption discount (₹" + redemptionDiscount 
                    + ") cannot exceed amount after tier discount (₹" + subtotalAfterTierDiscount + ")");
        }
        double finalAmount = subtotalAfterTierDiscount - redemptionDiscount;
        if (finalAmount < 0) {
            finalAmount = 0.0;
        }

        // 8. Calculate earned points (based on purchase amount)
        int pointsEarned = (int) Math.floor(purchaseAmount * pointsPerRupee);

        // Track previous state for detailed response
        int previousLifetimePoints = customer.getLifetimePoints();
        int previousAvailablePoints = customer.getAvailablePoints();
        String previousTierName = currentTier != null ? currentTier.getName() : "None";

        // 9. Update lifetime points & 10. Update available points
        int newLifetimePoints = previousLifetimePoints + pointsEarned;
        int newAvailablePoints = previousAvailablePoints - pointsToRedeem + pointsEarned;

        customer.setLifetimePoints(newLifetimePoints);
        customer.setAvailablePoints(newAvailablePoints);

        // 11. Check whether customer qualifies for a higher tier & 12. Upgrade automatically if required
        List<Tier> sortedTiersDesc = tierRepository.findAllByOrderByMinimumPointsDesc();
        Tier highestQualifyingTier = currentTier;
        for (Tier tier : sortedTiersDesc) {
            if (newLifetimePoints >= tier.getMinimumPoints()) {
                if (currentTier == null || tier.getMinimumPoints() > currentTier.getMinimumPoints()) {
                    highestQualifyingTier = tier;
                }
                break;
            }
        }

        boolean tierUpgraded = false;
        String newTierName = previousTierName;
        if (highestQualifyingTier != null && (currentTier == null || !highestQualifyingTier.getId().equals(currentTier.getId()))) {
            customer.setCurrentTier(highestQualifyingTier);
            tierUpgraded = true;
            newTierName = highestQualifyingTier.getName();
        }

        // Save updated customer
        customerRepository.save(customer);

        // 13. Save the purchase
        Purchase purchase = new Purchase();
        purchase.setCustomer(customer);
        purchase.setPurchaseAmount(purchaseAmount);
        purchase.setTierDiscount(tierDiscount);
        purchase.setPointsRedeemed(pointsToRedeem);
        purchase.setRedemptionDiscount(redemptionDiscount);
        purchase.setFinalAmount(finalAmount);
        purchase.setPointsEarned(pointsEarned);
        
        Purchase savedPurchase = purchaseRepository.save(purchase);

        // 14. Create appropriate PointsLedger records
        if (pointsToRedeem > 0) {
            pointsLedgerService.createLedgerEntry(
                    customer,
                    savedPurchase,
                    TransactionType.REDEEMED,
                    pointsToRedeem,
                    "Redeemed " + pointsToRedeem + " points for discount on purchase #" + savedPurchase.getId()
            );
        }

        if (pointsEarned > 0) {
            pointsLedgerService.createLedgerEntry(
                    customer,
                    savedPurchase,
                    TransactionType.EARNED,
                    pointsEarned,
                    "Earned " + pointsEarned + " points from purchase #" + savedPurchase.getId()
            );
        }

        // 15. Return a meaningful response
        PurchaseResponseDto responseDto = new PurchaseResponseDto();
        responseDto.setPurchaseId(savedPurchase.getId());
        responseDto.setCustomerId(customer.getId());
        responseDto.setCustomerName(customer.getName());
        responseDto.setPurchaseAmount(purchaseAmount);
        responseDto.setTierName(currentTier != null ? currentTier.getName() : "None");
        responseDto.setTierDiscountPercentage(tierDiscountPercentage);
        responseDto.setTierDiscount(tierDiscount);
        responseDto.setPointsRedeemed(pointsToRedeem);
        responseDto.setRedemptionDiscount(redemptionDiscount);
        responseDto.setFinalAmount(finalAmount);
        responseDto.setPointsEarned(pointsEarned);
        responseDto.setPreviousLifetimePoints(previousLifetimePoints);
        responseDto.setUpdatedLifetimePoints(newLifetimePoints);
        responseDto.setPreviousAvailablePoints(previousAvailablePoints);
        responseDto.setUpdatedAvailablePoints(newAvailablePoints);
        responseDto.setTierUpgraded(tierUpgraded);
        responseDto.setPreviousTierName(previousTierName);
        responseDto.setNewTierName(newTierName);
        responseDto.setPurchaseDate(savedPurchase.getPurchaseDate());

        return responseDto;
    }

    @Transactional(readOnly = true)
    public PurchaseResponseDto getPurchaseById(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with ID: " + purchaseId));
        return mapToResponseDto(purchase);
    }

    @Transactional(readOnly = true)
    public List<PurchaseResponseDto> getPurchasesByCustomerId(Long customerId) {
        // Ensure customer exists
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
        return purchaseRepository.findByCustomerIdOrderByPurchaseDateDesc(customerId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public PurchaseResponseDto mapToResponseDto(Purchase purchase) {
        Customer customer = purchase.getCustomer();
        PurchaseResponseDto dto = new PurchaseResponseDto();
        dto.setPurchaseId(purchase.getId());
        dto.setCustomerId(customer.getId());
        dto.setCustomerName(customer.getName());
        dto.setPurchaseAmount(purchase.getPurchaseAmount());
        if (customer.getCurrentTier() != null) {
            dto.setTierName(customer.getCurrentTier().getName());
            dto.setTierDiscountPercentage(customer.getCurrentTier().getDiscountPercentage());
        }
        dto.setTierDiscount(purchase.getTierDiscount());
        dto.setPointsRedeemed(purchase.getPointsRedeemed());
        dto.setRedemptionDiscount(purchase.getRedemptionDiscount());
        dto.setFinalAmount(purchase.getFinalAmount());
        dto.setPointsEarned(purchase.getPointsEarned());
        dto.setPreviousLifetimePoints(customer.getLifetimePoints());
        dto.setUpdatedLifetimePoints(customer.getLifetimePoints());
        dto.setPreviousAvailablePoints(customer.getAvailablePoints());
        dto.setUpdatedAvailablePoints(customer.getAvailablePoints());
        dto.setTierUpgraded(false);
        dto.setPreviousTierName(customer.getCurrentTier() != null ? customer.getCurrentTier().getName() : "None");
        dto.setNewTierName(customer.getCurrentTier() != null ? customer.getCurrentTier().getName() : "None");
        dto.setPurchaseDate(purchase.getPurchaseDate());
        return dto;
    }
}
