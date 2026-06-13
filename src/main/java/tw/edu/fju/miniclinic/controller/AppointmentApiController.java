package tw.edu.fju.miniclinic.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class AppointmentApiController {

    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;
    private final AppointmentRepository appointmentRepo;

    public AppointmentApiController(DoctorRepository doctorRepo, PatientRepository patientRepo, AppointmentRepository appointmentRepo) {
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
        this.appointmentRepo = appointmentRepo;
    }

    @PostMapping("/api/appointments")
    public ResponseEntity<Appointment> createAppointment(
            @RequestBody Map<String, String> request) {

        String chartNo = request.get("chartNo");
        String doctorId = request.get("doctorId");
        LocalDate apptDate = LocalDate.parse(request.get("apptDate"));
        String timeSlot = request.get("timeSlot");

        Patient patient = patientRepo.findById(chartNo).orElse(null);
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);

        if (patient == null || doctor == null) {
            return ResponseEntity.badRequest().build();
        }

        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setDoctor(doctor);
        appt.setApptDate(apptDate);
        appt.setTimeSlot(timeSlot);
        appt.setStatus("BOOKED");

        Appointment saved = appointmentRepo.save(appt);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/api/appointments/count")
    public ResponseEntity<Map<String, Long>> getAppointmentsCount() {
        return ResponseEntity.ok(Map.of("count", appointmentRepo.count()));
    }

    @GetMapping("/api/appointments")
    public ResponseEntity<List<Appointment>> getAppointments(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String doctorId) {
        
        LocalDate localDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
        Doctor doctor = (doctorId != null && !doctorId.isEmpty()) ? 
                        doctorRepo.findById(doctorId).orElse(null) : null;

        if (localDate != null && doctor != null) {
            return ResponseEntity.ok(appointmentRepo.findByDoctorAndApptDate(doctor, localDate));
        } else if (localDate != null) {
            return ResponseEntity.ok(appointmentRepo.findByApptDate(localDate));
        } else if (doctorId != null && !doctorId.isEmpty()) {
            // 使用方法開頭已取得的 doctor 物件，不需重複宣告與查詢
            if (doctor != null) {
                return ResponseEntity.ok(appointmentRepo.findByDoctor(doctor));
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.ok(appointmentRepo.findAll());
        }
    }

    @PutMapping("/api/appointments/{apptId}/status")
    public ResponseEntity<Appointment> updateStatus(
            @PathVariable Long apptId,
            @RequestBody Map<String, String> payload,
            HttpSession session) {

        String loggedInDoctorId = (String) session.getAttribute("loggedInDoctorId");
        
        // 檢查登入狀態，避免後續 equals 發生 NPE
        if (loggedInDoctorId == null) {
            return ResponseEntity.status(401).build();
        }

        Appointment appt = appointmentRepo.findById(apptId).orElse(null);
        if (appt == null) {
            return ResponseEntity.notFound().build();
        }

        // 只能修改自己的掛號
        if (!appt.getDoctor().getDoctorId().equals(loggedInDoctorId)) {
            return ResponseEntity.status(403).build();
        }

        String newStatus = payload.get("status");
        if (!List.of("BOOKED", "COMPLETED", "CANCELLED").contains(newStatus)) {
            return ResponseEntity.badRequest().build();
        }

        appt.setStatus(newStatus);
        return ResponseEntity.ok(appointmentRepo.save(appt));
    }
}
