package com.example.phoebe.integration.controller;

import com.example.phoebe.config.RateLimitConfig;
import com.example.phoebe.controller.HomeController;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = HomeController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RateLimitConfig rateLimitConfig;

    @BeforeEach
    void setUp() {
        // Create mock Bucket and ConsumptionProbe
        Bucket mockBucket = mock(Bucket.class);
        ConsumptionProbe mockProbe = mock(ConsumptionProbe.class);

        // Stub the mock objects to allow consumption
        when(mockProbe.isConsumed()).thenReturn(true);
        when(mockBucket.tryConsumeAndReturnRemaining(1)).thenReturn(mockProbe);
        when(rateLimitConfig.getPublicBucket(anyString())).thenReturn(mockBucket);
        when(rateLimitConfig.getAdminBucket(anyString())).thenReturn(mockBucket);
    }

    @Test
    void homeShouldReturnWelcomeMessage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("✅ Server is running!"));
    }
}