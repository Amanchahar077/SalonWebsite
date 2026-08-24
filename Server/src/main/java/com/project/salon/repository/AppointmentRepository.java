package com.project.salon.repository;

import com.project.salon.entity.Appointment;
import com.project.salon.entity.enums.AppointmentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentReference(String appointmentReference);

    List<Appointment> findByUserIdOrderByAppointmentDateDescStartTimeDesc(Long userId);

    @Query("SELECT a FROM Appointment a " +
           "WHERE (:date IS NULL OR a.appointmentDate = :date) " +
           "AND (:status IS NULL OR a.status = :status) " +
           "AND (:providerId IS NULL OR a.provider.id = :providerId) " +
           "ORDER BY a.appointmentDate DESC, a.startTime DESC")
    Page<Appointment> findAppointmentsWithFilters(
            @Param("date") LocalDate date,
            @Param("status") AppointmentStatus status,
            @Param("providerId") Long providerId,
            Pageable pageable
    );

    // Active overlapping appointments for a specific date and time slot
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.appointmentDate = :date " +
           "AND a.status IN :activeStatuses " +
           "AND a.startTime < :endTime AND a.endTime > :startTime")
    List<Appointment> findOverlappingActiveAppointments(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("activeStatuses") Collection<AppointmentStatus> activeStatuses
    );

    // Pessimistic lock query to check if a specific provider is booked at a slot
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.provider.id = :providerId " +
           "AND a.appointmentDate = :date " +
           "AND a.status IN :activeStatuses " +
           "AND a.startTime < :endTime AND a.endTime > :startTime")
    List<Appointment> findProviderOverlappingAppointmentsWithLock(
            @Param("providerId") Long providerId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("activeStatuses") Collection<AppointmentStatus> activeStatuses
    );

    List<Appointment> findByStatusAndCreatedAtBefore(AppointmentStatus status, LocalDateTime threshold);

    // Dashboard Statistics Queries
    long countByAppointmentDate(LocalDate date);

    long countByAppointmentDateAndStatus(LocalDate date, AppointmentStatus status);

    long countByStatus(AppointmentStatus status);

    @Query("SELECT COALESCE(SUM(a.amount), 0) FROM Appointment a WHERE a.status = 'CONFIRMED' OR a.status = 'COMPLETED'")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT COALESCE(SUM(a.amount), 0) FROM Appointment a WHERE a.appointmentDate = :date AND (a.status = 'CONFIRMED' OR a.status = 'COMPLETED')")
    BigDecimal calculateRevenueForDate(@Param("date") LocalDate date);
}
