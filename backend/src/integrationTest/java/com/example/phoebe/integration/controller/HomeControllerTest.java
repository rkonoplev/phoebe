package com.example.phoebe.integration.controller;

import com.example.phoebe.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class HomeControllerTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void homeShouldReturnWelcomeMessage() {
        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("✅ Server is running!");
    }
}