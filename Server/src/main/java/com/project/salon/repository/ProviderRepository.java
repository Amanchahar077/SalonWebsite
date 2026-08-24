package com.project.salon.repository;

import com.project.salon.entity.Provider;
import com.project.salon.entity.enums.ProviderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    List<Provider> findByStatus(ProviderStatus status);
    long countByStatus(ProviderStatus status);
}
