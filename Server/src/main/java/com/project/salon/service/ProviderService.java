package com.project.salon.service;

import com.project.salon.dto.ProviderRequest;
import com.project.salon.dto.ProviderResponse;
import com.project.salon.entity.Provider;
import com.project.salon.entity.enums.ProviderStatus;
import com.project.salon.exception.ResourceNotFoundException;
import com.project.salon.repository.ProviderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProviderService {

    private static final Logger log = LoggerFactory.getLogger(ProviderService.class);

    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Transactional(readOnly = true)
    public List<ProviderResponse> getAllProviders() {
        return providerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProviderResponse> getAvailableProviders() {
        return providerRepository.findByStatus(ProviderStatus.AVAILABLE).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProviderResponse getProviderById(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with ID: " + id));
        return mapToResponse(provider);
    }

    @Transactional
    public ProviderResponse addProvider(ProviderRequest request) {
        Provider provider = Provider.builder()
                .name(request.getName())
                .specialization(request.getSpecialization())
                .phone(request.getPhone())
                .email(request.getEmail())
                .status(request.getStatus() != null ? request.getStatus() : ProviderStatus.AVAILABLE)
                .build();

        provider = providerRepository.save(provider);
        log.info("Provider created: ID={}, Name={}", provider.getId(), provider.getName());
        return mapToResponse(provider);
    }

    @Transactional
    public ProviderResponse updateProvider(Long id, ProviderRequest request) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with ID: " + id));

        provider.setName(request.getName());
        provider.setSpecialization(request.getSpecialization());
        provider.setPhone(request.getPhone());
        provider.setEmail(request.getEmail());
        if (request.getStatus() != null) {
            provider.setStatus(request.getStatus());
        }

        provider = providerRepository.save(provider);
        log.info("Provider updated: ID={}, Name={}", provider.getId(), provider.getName());
        return mapToResponse(provider);
    }

    @Transactional
    public ProviderResponse updateProviderStatus(Long id, ProviderStatus status) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with ID: " + id));

        provider.setStatus(status);
        provider = providerRepository.save(provider);
        log.info("Provider status updated: ID={}, Status={}", id, status);
        return mapToResponse(provider);
    }

    @Transactional
    public void deleteProvider(Long id) {
        if (!providerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Provider not found with ID: " + id);
        }
        providerRepository.deleteById(id);
        log.info("Provider deleted: ID={}", id);
    }

    public ProviderResponse mapToResponse(Provider provider) {
        return ProviderResponse.builder()
                .id(provider.getId())
                .name(provider.getName())
                .specialization(provider.getSpecialization())
                .phone(provider.getPhone())
                .email(provider.getEmail())
                .status(provider.getStatus())
                .createdAt(provider.getCreatedAt())
                .build();
    }
}
