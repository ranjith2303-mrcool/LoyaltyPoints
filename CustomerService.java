package com.example.retailloyalty.service;

import com.example.retailloyalty.dto.CustomerLoyaltySummaryDto;
import com.example.retailloyalty.dto.CustomerRequestDto;
import com.example.retailloyalty.dto.CustomerResponseDto;
import com.example.retailloyalty.exception.InvalidPurchaseException;
import com.example.retailloyalty.exception.ResourceNotFoundException;
import com.example.retailloyalty.model.Customer;
import com.example.retailloyalty.model.Tier;
import com.example.retailloyalty.repository.CustomerRepository;
import com.example.retailloyalty.repository.TierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final TierService tierService;
    private final TierRepository tierRepository;

    public CustomerService(CustomerRepository customerRepository,
                           TierService tierService,
                           TierRepository tierRepository) {
        this.customerRepository = customerRepository;
        this.tierService = tierService;
        this.tierRepository = tierRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerById(Long id) {
        Customer customer = getCustomerEntityById(id);
        return mapToResponseDto(customer);
    }

    @Transactional(readOnly = true)
    public Customer getCustomerEntityById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
    }

    @Transactional
    public CustomerResponseDto createCustomer(CustomerRequestDto requestDto) {
        String normalizedEmail = requestDto.getEmail().trim().toLowerCase();
        if (customerRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Customer with email '" + normalizedEmail + "' already exists");
        }

        Tier initialTier;
        if (requestDto.getTierId() != null) {
            initialTier = tierService.getTierEntityById(requestDto.getTierId());
        } else {
            initialTier = tierService.getDefaultTier();
        }

        Customer customer = new Customer();
        customer.setName(requestDto.getName().trim());
        customer.setEmail(normalizedEmail);
        customer.setPhone(requestDto.getPhone());
        customer.setCurrentTier(initialTier);
        customer.setLifetimePoints(0);
        customer.setAvailablePoints(0);

        Customer savedCustomer = customerRepository.save(customer);
        return mapToResponseDto(savedCustomer);
    }

    @Transactional
    public CustomerResponseDto updateCustomer(Long id, CustomerRequestDto requestDto) {
        Customer existingCustomer = getCustomerEntityById(id);

        customerRepository.findByEmail(requestDto.getEmail().trim().toLowerCase()).ifPresent(c -> {
            if (!c.getId().equals(id)) {
                throw new IllegalArgumentException("Email '" + requestDto.getEmail() + "' is already used by another customer");
            }
        });

        existingCustomer.setName(requestDto.getName().trim());
        existingCustomer.setEmail(requestDto.getEmail().trim().toLowerCase());
        existingCustomer.setPhone(requestDto.getPhone());

        if (requestDto.getTierId() != null) {
            Tier newTier = tierService.getTierEntityById(requestDto.getTierId());
            existingCustomer.setCurrentTier(newTier);
        }

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return mapToResponseDto(updatedCustomer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = getCustomerEntityById(id);
        customerRepository.delete(customer);
    }

    @Transactional(readOnly = true)
    public CustomerLoyaltySummaryDto getCustomerLoyaltySummary(Long customerId) {
        Customer customer = getCustomerEntityById(customerId);
        Tier currentTier = customer.getCurrentTier();

        List<Tier> sortedTiers = tierRepository.findAllByOrderByMinimumPointsAsc();
        Optional<Tier> nextTierOpt = sortedTiers.stream()
                .filter(tier -> tier.getMinimumPoints() > customer.getLifetimePoints())
                .findFirst();

        CustomerLoyaltySummaryDto summary = new CustomerLoyaltySummaryDto();
        summary.setCustomerId(customer.getId());
        summary.setCustomerName(customer.getName());
        summary.setCustomerEmail(customer.getEmail());
        summary.setLifetimePoints(customer.getLifetimePoints());
        summary.setAvailablePoints(customer.getAvailablePoints());
        summary.setCurrentTierName(currentTier != null ? currentTier.getName() : "None");
        summary.setTierDiscountPercentage(currentTier != null ? currentTier.getDiscountPercentage() : 0.0);

        if (nextTierOpt.isPresent()) {
            Tier nextTier = nextTierOpt.get();
            summary.setNextTierName(nextTier.getName());
            summary.setPointsNeededForNextTier(nextTier.getMinimumPoints() - customer.getLifetimePoints());
        } else {
            summary.setNextTierName("Highest Tier Reached");
            summary.setPointsNeededForNextTier(0);
        }

        return summary;
    }

    public CustomerResponseDto mapToResponseDto(Customer customer) {
        CustomerResponseDto dto = new CustomerResponseDto();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setLifetimePoints(customer.getLifetimePoints());
        dto.setAvailablePoints(customer.getAvailablePoints());
        if (customer.getCurrentTier() != null) {
            dto.setCurrentTierId(customer.getCurrentTier().getId());
            dto.setCurrentTierName(customer.getCurrentTier().getName());
            dto.setTierDiscountPercentage(customer.getCurrentTier().getDiscountPercentage());
        }
        dto.setCreatedAt(customer.getCreatedAt());
        return dto;
    }
}
