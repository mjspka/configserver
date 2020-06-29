package com.pmalla.configclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pmalla
 *
 */

@RestController
@SpringBootApplication
public class ConfigClientApplication {
	
	@Autowired
	private Configuration configuration;

    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApplication.class, args);
    }

    @GetMapping("/property")
    public ResponseEntity<String> getProperty() {
        return ResponseEntity.ok(configuration.getProperty());
    }
}
