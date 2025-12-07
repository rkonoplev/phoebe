package com.example.phoebe.unit.controller;

import com.example.phoebe.controller.RoleController;
import com.example.phoebe.dto.request.RoleCreateRequestDto;
import com.example.phoebe.dto.request.RoleUpdateRequestDto;
import com.example.phoebe.dto.response.RoleDto;
import com.example.phoebe.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController controller;

    private RoleDto roleDto;

    @BeforeEach
    void setUp() {
        roleDto = new RoleDto(1L, "ADMIN", "Administrator role", Set.of());
    }

    @Test
    void getAllRoles_ShouldReturnListOfRoles() {
        when(roleService.getAllRoles()).thenReturn(List.of(roleDto));

        ResponseEntity<List<RoleDto>> response = controller.getAllRoles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getRoleById_ShouldReturnRole() {
        when(roleService.getRoleById(1L)).thenReturn(roleDto);

        ResponseEntity<RoleDto> response = controller.getRoleById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ADMIN", response.getBody().name());
    }

    @Test
    void createRole_ShouldReturnCreatedRole() {
        RoleCreateRequestDto createDto = new RoleCreateRequestDto();
        createDto.setName("EDITOR");
        createDto.setDescription("Editor role");
        when(roleService.createRole(any())).thenReturn(roleDto);

        ResponseEntity<RoleDto> response = controller.createRole(createDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("ADMIN", response.getBody().name());
    }

    @Test
    void updateRole_ShouldReturnUpdatedRole() {
        RoleUpdateRequestDto updateDto = new RoleUpdateRequestDto();
        updateDto.setName("ADMIN");
        updateDto.setDescription("Updated description");
        when(roleService.updateRole(eq(1L), any())).thenReturn(roleDto);

        ResponseEntity<RoleDto> response = controller.updateRole(1L, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteRole_ShouldReturnNoContent() {
        ResponseEntity<Void> response = controller.deleteRole(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(roleService).deleteRole(1L);
    }

    @Test
    void getRolesByUserId_ShouldReturnUserRoles() {
        when(roleService.findRolesByUserId(1L)).thenReturn(Set.of(roleDto));

        ResponseEntity<Set<RoleDto>> response = controller.getRolesByUserId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
