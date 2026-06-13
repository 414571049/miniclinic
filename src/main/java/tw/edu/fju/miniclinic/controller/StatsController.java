package tw.edu.fju.miniclinic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PatientRepository;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class StatsController {

    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;
    private final AppointmentRepository appointmentRepo;

    public StatsController(DoctorRepository doctorRepo, 
                           PatientRepository patientRepo, 
                           AppointmentRepository appointmentRepo) {
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
        this.appointmentRepo = appointmentRepo;
    }

    @GetMapping("/stats")
    public String showStats(Model model) {
        // 基本總數統計
        model.addAttribute("doctorCount", doctorRepo.count());
        model.addAttribute("patientCount", patientRepo.count());
        model.addAttribute("appointmentCount", appointmentRepo.count());

        // 效能警示：在大數據量下，應避免在 Controller 使用 .findAll().stream()
        // 建議做法：在 AppointmentRepository 寫一個 @Query(GROUP BY)
        // 目前這樣寫在小專案沒問題，但實務上建議由資料庫端進行統計
        Map<String, Long> deptStats = appointmentRepo.findAll().stream()
                .filter(appt -> appt.getDoctor() != null && appt.getDoctor().getDepartment() != null)
                .collect(Collectors.groupingBy(
                        appt -> appt.getDoctor().getDepartment(),
                        Collectors.counting()
                ));
        model.addAttribute("deptStats", deptStats);

        return "stats";
    }
}