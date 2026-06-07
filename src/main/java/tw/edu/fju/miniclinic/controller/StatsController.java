package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tw.edu.fju.miniclinic.model.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StatsController {

    @Autowired private DoctorRepository doctorRepo;
    @Autowired private PatientRepository patientRepo;
    @Autowired private AppointmentRepository appointmentRepo;

    @GetMapping("/stats")
    public String showStats(Model model) {
        model.addAttribute("doctorCount", doctorRepo.count());
        model.addAttribute("patientCount", patientRepo.count());
        model.addAttribute("apptCount", appointmentRepo.count());
        model.addAttribute("deptStats", appointmentRepo.countAppointmentsByDepartment());
        return "stats";
    }

    @GetMapping("/api/appointments/count")
    @ResponseBody
    public Map<String, Long> getApptCount() {
        Map<String, Long> result = new HashMap<>();
        result.put("count", appointmentRepo.count());
        return result;
    }

    @GetMapping("/api/appointments")
    @ResponseBody
    public List<Appointment> getAppointments(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String doctorId) {
        
        LocalDate localDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
        Doctor doctor = (doctorId != null && !doctorId.isEmpty()) ? 
                        doctorRepo.findById(doctorId).orElse(null) : null;

        // 處理三種情況：都有傳、只傳日期、只傳醫師、都沒傳
        if (localDate != null && doctor != null) {
            return appointmentRepo.findByDoctorAndApptDate(doctor, localDate);
        } else if (localDate != null) {
            return appointmentRepo.findByApptDate(localDate);
        } else if (doctor != null) {
            return appointmentRepo.findByDoctor(doctor);
        } else {
            return appointmentRepo.findAll();
        }
    }
}