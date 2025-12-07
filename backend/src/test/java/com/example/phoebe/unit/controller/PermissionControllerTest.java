package com.example.phoebe.unit.controller;

import com.example.phoebe.controller.PermissionController;
import com.example.phoebe.dto.response.PermissionDto;
import com.example.phoebe.service.PermissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionControllerTest {

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private PermissionController controller;

    @Test
    void getAllPermissions_ShouldReturnListOfPermissions() {
        PermissionDto permissionDto = new PermissionDto("READ");
        when(permissionService.getAllPermissions()).thenReturn(List.of(permissionDto));

        ResponseEntity<List<PermissionDto>> response = controller.getAllPermissions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("READ", response.getBody().get(0).name());
    }
}
