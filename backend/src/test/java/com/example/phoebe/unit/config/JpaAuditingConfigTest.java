package com.example.phoebe.unit.config;

import com.example.phoebe.config.JpaAuditingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JpaAuditingConfigTest {

    @Test
    void auditorAware_ShouldReturnEmptyOptional() {
        JpaAuditingConfig config = new JpaAuditingConfig();

        AuditorAware<String> auditorAware = config.auditorAware();

        assertNotNull(auditorAware);
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
        assertTrue(currentAuditor.isEmpty());
    }
}
