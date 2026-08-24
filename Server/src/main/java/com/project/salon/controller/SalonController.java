package com.project.salon.controller;

import com.project.salon.dto.ProviderResponse;
import com.project.salon.dto.SalonConfigurationResponse;
import com.project.salon.service.ProviderService;
import com.project.salon.service.SalonConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/salon")
@Tag(name = "Salon", description = "Public salon information endpoints")
public class SalonController {

    private final SalonConfigurationService configurationService;
    private final ProviderService providerService;

    public SalonController(SalonConfigurationService configurationService,
                           ProviderService providerService) {
        this.configurationService = configurationService;
        this.providerService = providerService;
    }

    @GetMapping("/configuration")
    @Operation(summary = "Get salon operating configuration (opening hours, duration, provider count)")
    public ResponseEntity<SalonConfigurationResponse> getConfiguration() {
        return ResponseEntity.ok(configurationService.getConfiguration());
    }

    @GetMapping("/providers")
    @Operation(summary = "Get list of all salon providers / barbers")
    public ResponseEntity<List<ProviderResponse>> getProviders() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }
}
