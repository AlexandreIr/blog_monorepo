package br.com.libertadfacilites.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "API online"
        ));
    }

    @PostMapping("/json")
    public ResponseEntity<Map<String, Object>> json(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "received", body
        ));
    }
}