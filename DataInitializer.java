package com.example.retailloyalty.config;

import com.example.retailloyalty.model.Tier;
import com.example.retailloyalty.repository.TierRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * Initializes default tier records in the database on application startup if none exist.
 */
@Configuration
public class DataInitializer implements CommandLineRunner {

    private final TierRepository tierRepository;

    public DataInitializer(TierRepository tierRepository) {
        this.tierRepository = tierRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (tierRepository.count() == 0) {
            tierRepository.save(new Tier("Silver", 0, 0.0));
            tierRepository.save(new Tier("Gold", 1000, 10.0));
            tierRepository.save(new Tier("Platinum", 5000, 20.0));
        }
    }
}
