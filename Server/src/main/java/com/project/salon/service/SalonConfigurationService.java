package com.project.salon.service;

import com.project.salon.dto.SalonConfigurationRequest;
import com.project.salon.dto.SalonConfigurationResponse;
import com.project.salon.entity.SalonConfiguration;
import com.project.salon.repository.SalonConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
public class SalonConfigurationService {

    private static final Logger log = LoggerFactory.getLogger(SalonConfigurationService.class);

    private final SalonConfigurationRepository configurationRepository;

    public SalonConfigurationService(SalonConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Transactional(readOnly = true)
    public SalonConfigurationResponse getConfiguration() {
        SalonConfiguration config = getOrCreateDefaultConfig();
        return mapToResponse(config);
    }

    @Transactional
    public SalonConfigurationResponse updateConfiguration(SalonConfigurationRequest request) {
        if (request.getClosingTime().isBefore(request.getOpeningTime()) || request.getClosingTime().equals(request.getOpeningTime())) {
            throw new IllegalArgumentException("Closing time must be after opening time");
        }

        SalonConfiguration config = getOrCreateDefaultConfig();
        config.setProviderCount(request.getProviderCount());
        config.setOpeningTime(request.getOpeningTime());
        config.setClosingTime(request.getClosingTime());
        config.setSlotDuration(request.getSlotDuration());

        config = configurationRepository.save(config);
        log.info("Salon configuration updated: opening={}, closing={}, duration={}m, providers={}",
                config.getOpeningTime(), config.getClosingTime(), config.getSlotDuration(), config.getProviderCount());

        return mapToResponse(config);
    }

    @Transactional
    public SalonConfiguration getOrCreateDefaultConfig() {
        return configurationRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> configurationRepository.save(
                        SalonConfiguration.builder()
                                .providerCount(5)
                                .openingTime(LocalTime.of(10, 0))
                                .closingTime(LocalTime.of(20, 0))
                                .slotDuration(30)
                                .build()
                ));
    }

    private SalonConfigurationResponse mapToResponse(SalonConfiguration config) {
        return SalonConfigurationResponse.builder()
                .id(config.getId())
                .providerCount(config.getProviderCount())
                .openingTime(config.getOpeningTime())
                .closingTime(config.getClosingTime())
                .slotDuration(config.getSlotDuration())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}
