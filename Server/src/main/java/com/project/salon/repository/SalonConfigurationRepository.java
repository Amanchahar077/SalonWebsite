package com.project.salon.repository;

import com.project.salon.entity.SalonConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalonConfigurationRepository extends JpaRepository<SalonConfiguration, Long> {
    Optional<SalonConfiguration> findFirstByOrderByIdAsc();
}
