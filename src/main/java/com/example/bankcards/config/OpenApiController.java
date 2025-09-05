package com.example.bankcards.config;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class OpenApiController {

    @GetMapping(value = "/docs/openapi.yaml", produces = "application/yaml")
    public ResponseEntity<byte[]> getOpenApi() throws Exception {
        Path path = Path.of("docs/openapi.yaml");
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/yaml"))
                .body(Files.readAllBytes(path));
    }
}
