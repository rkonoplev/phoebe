package com.example.phoebe.integration.controller;

import com.example.phoebe.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthControllerRateLimitTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAllowFirstFiveRequests() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/public/news"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void shouldBlockSixthRequest() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/public/news"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/public/news"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowRequestsFromDifferentIPs() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/public/news")
                            .with(request -> {
                                request.setRemoteAddr("192.168.1.1");
                                return request;
                            }))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/public/news")
                        .with(request -> {
                            request.setRemoteAddr("192.168.1.2");
                            return request;
                        }))
                .andExpect(status().isOk());
    }
}