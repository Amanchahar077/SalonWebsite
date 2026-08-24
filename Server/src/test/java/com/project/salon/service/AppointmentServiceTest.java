package com.project.salon.service;

import com.project.salon.dto.AppointmentResponse;
import com.project.salon.dto.CreateAppointmentRequest;
import com.project.salon.entity.Appointment;
import com.project.salon.entity.Provider;
import com.project.salon.entity.SalonConfiguration;
import com.project.salon.entity.User;
import com.project.salon.entity.enums.AppointmentStatus;
import com.project.salon.entity.enums.ProviderStatus;
import com.project.salon.entity.enums.Role;
import com.project.salon.exception.InvalidAppointmentStateException;
import com.project.salon.exception.SlotUnavailableException;
import com.project.salon.repository.AppointmentRepository;
import com.project.salon.repository.AppointmentStatusHistoryRepository;
import com.project.salon.repository.ProviderRepository;
import com.project.salon.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private ProviderRepository providerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SalonConfigurationService configurationService;
    @Mock
    private AppointmentStatusHistoryRepository historyRepository;
    @Mock
    private ProviderService providerService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AppointmentService appointmentService;

    private User testUser;
    private Provider testProvider;
    private SalonConfiguration testConfig;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).email("user@example.com").name("Test User").role(Role.USER).build();
        testProvider = Provider.builder().id(10L).name("Rahul").status(ProviderStatus.AVAILABLE).build();
        testConfig = SalonConfiguration.builder()
                .openingTime(LocalTime.of(10, 0))
                .closingTime(LocalTime.of(20, 0))
                .slotDuration(30)
                .build();
    }

    @Test
    void testBookAppointment_Success() {
        CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                .appointmentDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(11, 0))
                .amount(new BigDecimal("500.00"))
                .build();

        when(configurationService.getOrCreateDefaultConfig()).thenReturn(testConfig);
        when(providerRepository.findByStatus(ProviderStatus.AVAILABLE)).thenReturn(List.of(testProvider));
        when(appointmentRepository.findProviderOverlappingAppointmentsWithLock(any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        AppointmentResponse response = appointmentService.bookAppointment(request, testUser);

        assertNotNull(response);
        assertEquals(AppointmentStatus.PENDING_PAYMENT, response.getStatus());
        assertEquals(new BigDecimal("500.00"), response.getAmount());
        verify(historyRepository, times(1)).save(any());
    }

    @Test
    void testBookAppointment_SlotUnavailableException() {
        CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                .appointmentDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(11, 0))
                .amount(new BigDecimal("500.00"))
                .build();

        when(configurationService.getOrCreateDefaultConfig()).thenReturn(testConfig);
        when(providerRepository.findByStatus(ProviderStatus.AVAILABLE)).thenReturn(List.of(testProvider));
        // Simulate provider already booked
        when(appointmentRepository.findProviderOverlappingAppointmentsWithLock(any(), any(), any(), any(), any()))
                .thenReturn(List.of(new Appointment()));

        assertThrows(SlotUnavailableException.class, () -> appointmentService.bookAppointment(request, testUser));
    }

    @Test
    void testCancelAppointment_InvalidState() {
        Appointment cancelledApp = Appointment.builder()
                .id(100L)
                .user(testUser)
                .status(AppointmentStatus.CANCELLED)
                .build();

        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(cancelledApp));

        assertThrows(InvalidAppointmentStateException.class, () ->
                appointmentService.cancelAppointment(100L, "User change of mind", testUser));
    }
}
