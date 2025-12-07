package com.example.phoebe.unit.controller;

import com.example.phoebe.controller.HomeController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @InjectMocks
    private HomeController controller;

    @Test
    void home_ShouldReturnHealthCheckMessage() {
        String result = controller.home();
        assertEquals("✅ Server is running!", result);
    }
}
