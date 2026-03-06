package com.uniquindio.CINEMAX.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminTestController {

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok("ADMIN OK");
    }
}