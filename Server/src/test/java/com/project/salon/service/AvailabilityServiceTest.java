package com.project.salon.service;

import com.project.salon.dto.AvailabilityResponse;
import com.project.salon.entity.Provider;
import com.project.salon.entity.SalonConfiguration;
import com.project.salon.entity.enums.ProviderStatus;
import com.project.salon.repository.AppointmentRepository;
import com.project.salon.repository.ProviderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private SalonConfigurationService configurationService;

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ProviderService providerService;

    @InjectMocks
    private AvailabilityService availabilityService;

    @Test
    void testGetAvailabilityForDate_Success() {
        SalonConfiguration config = SalonConfiguration.builder()
                .openingTime(LocalTime.of(10, 0))
                .closingTime(LocalTime.of(12, 0))
                .slotDuration(30)
                .providerCount(2)
                .build();

        Provider p1 = Provider.builder().id(1L).name("Rahul").status(ProviderStatus.AVAILABLE).build();
        Provider p2 = Provider.builder().id(2L).name("Amit").status(ProviderStatus.AVAILABLE).build();

        when(configurationService.getOrCreateDefaultConfig()).thenReturn(config);
        when(providerRepository.findByStatus(ProviderStatus.AVAILABLE)).thenReturn(List.of(p1, p2));
        when(appointmentRepository.findOverlappingActiveAppointments(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        AvailabilityResponse response = availabilityService.getAvailabilityForDate(LocalDate.now().plusDays(1));

        assertNotNull(response);
        assertEquals(4, response.getSlots().size()); // 10:00, 10:30, 11:00, 11:30
        assertTrue(response.getSlots().get(0).isAvailable());
        assertEquals(2, response.getSlots().get(0).getAvailableProvidersCount());
    }
}
