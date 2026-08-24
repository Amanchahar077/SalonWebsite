package com.project.salon.service;

import com.project.salon.entity.Appointment;
import com.project.salon.entity.Notification;
import com.project.salon.entity.User;
import com.project.salon.entity.enums.NotificationStatus;
import com.project.salon.repository.AppointmentRepository;
import com.project.salon.repository.NotificationRepository;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;
    private final AppointmentRepository appointmentRepository;

    @Value("${app.admin-email:admin@salon.com}")
    private String adminEmail;

    @Value("${spring.mail.username:noreply@salon.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender,
                        NotificationRepository notificationRepository,
                        AppointmentRepository appointmentRepository) {
        this.mailSender = mailSender;
        this.notificationRepository = notificationRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public void sendAppointmentConfirmationEmail(Appointment appointment) {
        // Re-fetch inside transaction to ensure lazy-loaded proxies (User, Provider) initialize safely
        Appointment app = appointmentRepository.findById(appointment.getId()).orElse(appointment);
        User customer = app.getUser();

        String customerSubject = "Appointment Confirmed - " + app.getAppointmentReference();
        String customerContent = buildCustomerConfirmationHtml(app);
        sendHtmlEmail(customer, app, customer.getEmail(), customerSubject, customerContent, "CONFIRMATION_CUSTOMER");

        String adminSubject = "New Booking Confirmed - " + app.getAppointmentReference();
        String adminContent = buildAdminNotificationHtml(app);
        sendHtmlEmail(customer, app, adminEmail, adminSubject, adminContent, "CONFIRMATION_ADMIN");
    }

    @Transactional
    public void sendAppointmentCancellationEmail(Appointment appointment, String cancelledBy, String reason) {
        Appointment app = appointmentRepository.findById(appointment.getId()).orElse(appointment);
        User customer = app.getUser();
        String subject = "Appointment Cancelled - " + app.getAppointmentReference();
        String content = buildCancellationHtml(app, cancelledBy, reason);
        sendHtmlEmail(customer, app, customer.getEmail(), subject, content, "CANCELLATION_CUSTOMER");
    }

    private void sendHtmlEmail(User user, Appointment appointment, String to, String subject, String htmlContent, String type) {
        Notification notification = Notification.builder()
                .user(user)
                .appointment(appointment)
                .type(type)
                .recipient(to)
                .status(NotificationStatus.PENDING)
                .build();
        notification = notificationRepository.save(notification);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            log.info("Email sent successfully: Type={}, Recipient={}", type, to);
        } catch (Exception ex) {
            log.error("Failed to send email to {}: ", to, ex);
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
        }
    }

    private String buildCustomerConfirmationHtml(Appointment app) {
        String providerName = app.getProvider() != null ? app.getProvider().getName() : "Salon Stylist";
        String providerSpec = app.getProvider() != null && app.getProvider().getSpecialization() != null
                ? " (" + app.getProvider().getSpecialization() + ")" : "";

        return "<html><body style='font-family: Arial, sans-serif; color: #333;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;'>"
                + "<h2 style='color: #6b21a8;'>Appointment Confirmed!</h2>"
                + "<p>Dear <strong>" + app.getUser().getName() + "</strong>,</p>"
                + "<p>Your salon appointment has been successfully booked and confirmed.</p>"
                + "<table style='width: 100%; border-collapse: collapse; margin-top: 15px;'>"
                + "<tr style='background: #f3e8ff;'><td style='padding: 10px; font-weight: bold;'>Booking Reference:</td><td style='padding: 10px;'>" + app.getAppointmentReference() + "</td></tr>"
                + "<tr><td style='padding: 10px; font-weight: bold;'>Date:</td><td style='padding: 10px;'>" + app.getAppointmentDate() + "</td></tr>"
                + "<tr style='background: #f9fafb;'><td style='padding: 10px; font-weight: bold;'>Time:</td><td style='padding: 10px;'>" + app.getStartTime() + " - " + app.getEndTime() + "</td></tr>"
                + "<tr><td style='padding: 10px; font-weight: bold;'>Stylist / Provider:</td><td style='padding: 10px;'>" + providerName + providerSpec + "</td></tr>"
                + "<tr style='background: #f9fafb;'><td style='padding: 10px; font-weight: bold;'>Amount Paid:</td><td style='padding: 10px;'>₹" + app.getAmount() + "</td></tr>"
                + "<tr><td style='padding: 10px; font-weight: bold;'>Payment Status:</td><td style='padding: 10px; color: green; font-weight: bold;'>" + app.getPaymentStatus() + "</td></tr>"
                + "</table>"
                + "<p style='margin-top: 20px;'>Thank you for choosing our Salon!</p>"
                + "</div></body></html>";
    }

    private String buildAdminNotificationHtml(Appointment app) {
        String providerName = app.getProvider() != null ? app.getProvider().getName() : "Unassigned";

        return "<html><body style='font-family: Arial, sans-serif; color: #333;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;'>"
                + "<h2 style='color: #15803d;'>New Appointment Booking</h2>"
                + "<p>A new appointment has been confirmed.</p>"
                + "<ul>"
                + "<li><strong>Reference:</strong> " + app.getAppointmentReference() + "</li>"
                + "<li><strong>Customer:</strong> " + app.getUser().getName() + " (" + app.getUser().getEmail() + ")</li>"
                + "<li><strong>Stylist:</strong> " + providerName + "</li>"
                + "<li><strong>Date & Time:</strong> " + app.getAppointmentDate() + " at " + app.getStartTime() + "</li>"
                + "<li><strong>Amount Paid:</strong> ₹" + app.getAmount() + "</li>"
                + "</ul>"
                + "</div></body></html>";
    }

    private String buildCancellationHtml(Appointment app, String cancelledBy, String reason) {
        return "<html><body style='font-family: Arial, sans-serif; color: #333;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;'>"
                + "<h2 style='color: #dc2626;'>Appointment Cancelled</h2>"
                + "<p>Dear <strong>" + app.getUser().getName() + "</strong>,</p>"
                + "<p>Your appointment <strong>#" + app.getAppointmentReference() + "</strong> scheduled for " + app.getAppointmentDate() + " at " + app.getStartTime() + " has been cancelled.</p>"
                + "<p><strong>Cancelled By:</strong> " + cancelledBy + "</p>"
                + "<p><strong>Reason:</strong> " + (reason != null && !reason.isBlank() ? reason : "N/A") + "</p>"
                + "<p style='margin-top: 20px;'>If you have any questions, please contact us.</p>"
                + "</div></body></html>";
    }
}
