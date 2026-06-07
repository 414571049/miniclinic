package tw.edu.fju.miniclinic.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        Map<String, String> status = new LinkedHashMap<>();
        status.put("status", "UP");
        return status;
    }

    @GetMapping("/api/about")
    public Map<String, String> about() {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("student_id", "YOUR_STUDENT_ID"); // 請替換為你的學號
        info.put("student_name", "YOUR_NAME");      // 請替換為你的姓名
        info.put("project", "MiniClinic");
        info.put("version", "0.1.0");
        info.put("chapter", "Ch09-A");
        return info;
    }
}