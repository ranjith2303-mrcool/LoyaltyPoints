package com.example.retailloyalty;

import com.example.retailloyalty.dto.CustomerRequestDto;
import com.example.retailloyalty.dto.CustomerResponseDto;
import com.example.retailloyalty.dto.PurchaseRequestDto;
import com.example.retailloyalty.dto.PurchaseResponseDto;
import com.example.retailloyalty.exception.InsufficientPointsException;
import com.example.retailloyalty.service.CustomerService;
import com.example.retailloyalty.service.PointsLedgerService;
import com.example.retailloyalty.service.PurchaseService;
import com.example.retailloyalty.service.TierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LoyaltyServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private TierService tierService;

    @Autowired
    private PointsLedgerService pointsLedgerService;

    private CustomerResponseDto testCustomer;

    @BeforeEach
    void setUp() {
        // Create a unique test customer
        String uniqueEmail = "raj_" + System.currentTimeMillis() + "@example.com";
        CustomerRequestDto requestDto = new CustomerRequestDto("Raj Kumar", uniqueEmail, "9876543210");
        testCustomer = customerService.createCustomer(requestDto);
    }

    @Test
    @DisplayName("Rule 1, 2, 3 & 4: Award points, Lifetime/Available points, and Automatic Tier Upgrade to Gold")
    void testPurchaseAndTierUpgrade() {
        // Initial customer should be Silver (0 points)
        assertEquals("Silver", testCustomer.getCurrentTierName());
        assertEquals(0, testCustomer.getLifetimePoints());
        assertEquals(0, testCustomer.getAvailablePoints());

        // Make purchase of ₹1000
        PurchaseRequestDto purchaseDto = new PurchaseRequestDto(testCustomer.getId(), 1000.0, 0);
        PurchaseResponseDto response = purchaseService.processPurchase(purchaseDto);

        // Verify points
        assertEquals(1000, response.getPointsEarned());
        assertEquals(1000, response.getUpdatedLifetimePoints());
        assertEquals(1000, response.getUpdatedAvailablePoints());

        // Verify automatic tier upgrade to Gold (threshold = 1000)
        assertTrue(response.isTierUpgraded());
        assertEquals("Gold", response.getNewTierName());
    }

    @Test
    @DisplayName("Rule 5 & 7: Tier discount applied automatically on subsequent purchase")
    void testTierDiscountApplication() {
        // 1st purchase to upgrade to Gold (1000 points)
        purchaseService.processPurchase(new PurchaseRequestDto(testCustomer.getId(), 1000.0, 0));

        // 2nd purchase of ₹2000 as Gold member (10% discount)
        PurchaseRequestDto purchase2 = new PurchaseRequestDto(testCustomer.getId(), 2000.0, 0);
        PurchaseResponseDto response2 = purchaseService.processPurchase(purchase2);

        // Gold discount 10% on 2000 = 200
        assertEquals("Gold", response2.getTierName());
        assertEquals(10.0, response2.getTierDiscountPercentage());
        assertEquals(200.0, response2.getTierDiscount());
        assertEquals(1800.0, response2.getFinalAmount());
    }

    @Test
    @DisplayName("Rule 6: Redeem points for discount & prevent downgrade (Rule 4)")
    void testPointRedemptionAndNoDowngrade() {
        // 1. Customer earns 1000 points (upgraded to Gold)
        purchaseService.processPurchase(new PurchaseRequestDto(testCustomer.getId(), 1000.0, 0));

        // 2. Customer makes purchase of ₹1000, redeems 500 points
        // Gold discount 10% on 1000 = 100. Amount after tier discount = 900.
        // Redemption discount 500 points = ₹500.
        // Final amount = 900 - 500 = 400.
        // Points earned from 1000 purchase = 1000 points.
        // New available points = 1000 - 500 + 1000 = 1500.
        // Lifetime points = 1000 + 1000 = 2000.
        PurchaseRequestDto purchaseDto = new PurchaseRequestDto(testCustomer.getId(), 1000.0, 500);
        PurchaseResponseDto response = purchaseService.processPurchase(purchaseDto);

        assertEquals(100.0, response.getTierDiscount());
        assertEquals(500, response.getPointsRedeemed());
        assertEquals(500.0, response.getRedemptionDiscount());
        assertEquals(400.0, response.getFinalAmount());
        assertEquals(2000, response.getUpdatedLifetimePoints());
        assertEquals(1500, response.getUpdatedAvailablePoints());

        // Verify tier remains Gold (no downgrade)
        assertEquals("Gold", response.getNewTierName());
    }

    @Test
    @DisplayName("Rule 6: Reject redemption exceeding available balance")
    void testRejectExcessiveRedemption() {
        // Customer has 0 available points initially
        PurchaseRequestDto purchaseDto = new PurchaseRequestDto(testCustomer.getId(), 500.0, 100);

        InsufficientPointsException exception = assertThrows(
                InsufficientPointsException.class,
                () -> purchaseService.processPurchase(purchaseDto)
        );

        assertTrue(exception.getMessage().contains("Insufficient available points"));
    }

    @Test
    @DisplayName("Rule 10: Points Ledger records created for EARNED and REDEEMED")
    void testPointsHistoryLedger() {
        // Make purchase with earning and redemption
        purchaseService.processPurchase(new PurchaseRequestDto(testCustomer.getId(), 1000.0, 0)); // earns 1000
        purchaseService.processPurchase(new PurchaseRequestDto(testCustomer.getId(), 500.0, 200)); // redeems 200, earns 500

        var history = pointsLedgerService.getPointsHistoryByCustomerId(testCustomer.getId());

        // Should have 3 entries: 1st purchase EARNED (1000), 2nd purchase REDEEMED (200), 2nd purchase EARNED (500)
        assertEquals(3, history.size());
    }
}
