package com.example.phoebe.unit.service;

import com.example.phoebe.dto.response.PermissionDto;
import com.example.phoebe.entity.Permission;
import com.example.phoebe.mapper.PermissionMapper;
import com.example.phoebe.repository.PermissionRepository;
import com.example.phoebe.service.impl.PermissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private PermissionServiceImpl service;

    private Permission permission;
    private PermissionDto permissionDto;

    @BeforeEach
    void setUp() {
        permission = new Permission("READ");
        permission.setId(1L);

        permissionDto = new PermissionDto("READ");
    }

    @Test
    void getAllPermissionsReturnsListOfPermissions() {
        when(permissionRepository.findAll()).thenReturn(List.of(permission));
        when(permissionMapper.toDto(permission)).thenReturn(permissionDto);

        List<PermissionDto> result = service.getAllPermissions();

        assertEquals(1, result.size());
        assertEquals("READ", result.get(0).name());
    }

    @Test
    void getPermissionByIdExistingIdReturnsPermission() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(permissionMapper.toDto(permission)).thenReturn(permissionDto);

        PermissionDto result = service.getPermissionById(1L);

        assertNotNull(result);
        assertEquals("READ", result.name());
    }

    @Test
    void getPermissionByIdNonExistingIdReturnsNull() {
        when(permissionRepository.findById(999L)).thenReturn(Optional.empty());

        PermissionDto result = service.getPermissionById(999L);

        assertNull(result);
    }

    @Test
    void getPermissionsByRoleIdReturnsRolePermissions() {
        when(permissionRepository.findByRolesId(1L)).thenReturn(Set.of(permission));
        when(permissionMapper.toDto(permission)).thenReturn(permissionDto);

        List<PermissionDto> result = service.getPermissionsByRoleId(1L);

        assertEquals(1, result.size());
        verify(permissionRepository).findByRolesId(1L);
    }

    @Test
    void existsByNameExistingNameReturnsTrue() {
        when(permissionRepository.existsByName("READ")).thenReturn(true);

        boolean result = service.existsByName("READ");

        assertTrue(result);
    }

    @Test
    void existsByNameNonExistingNameReturnsFalse() {
        when(permissionRepository.existsByName("INVALID")).thenReturn(false);

        boolean result = service.existsByName("INVALID");

        assertFalse(result);
    }
}
