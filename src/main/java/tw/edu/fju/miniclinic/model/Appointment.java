package tw.edu.fju.miniclinic.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcTypeCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.sql.Types;

@Entity
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long apptId;

    @ManyToOne
    @JoinColumn(name = "chart_no", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @JdbcTypeCode(Types.VARCHAR)
    @Convert(converter = LocalDateConverter.class)
    @Column(name = "appt_date", nullable = false, columnDefinition = "TEXT")
    private LocalDate apptDate;

    @Converter(autoApply = false)
    public static class LocalDateConverter implements AttributeConverter<LocalDate, String> {
        @Override
        public String convertToDatabaseColumn(LocalDate attribute) {
            return (attribute == null) ? null : attribute.toString();
        }

        @Override
        public LocalDate convertToEntityAttribute(String dbData) {
            return (dbData == null || dbData.isEmpty()) ? null : LocalDate.parse(dbData);
        }
    }

    @Column(nullable = false, length = 20)
    private String timeSlot;

    @Column(nullable = false, length = 20)
    private String status = "BOOKED";

    public Appointment() {}

    // Getters and Setters
    public Long getApptId() { return apptId; }
    public void setApptId(Long apptId) { this.apptId = apptId; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public LocalDate getApptDate() { return apptDate; }
    public void setApptDate(LocalDate apptDate) { this.apptDate = apptDate; }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}