package com.example.phoebe.unit.service;

import com.example.phoebe.dto.request.RoleCreateRequestDto;
import com.example.phoebe.dto.request.RoleUpdateRequestDto;
import com.example.phoebe.dto.response.RoleDto;
import com.example.phoebe.entity.Permission;
import com.example.phoebe.entity.Role;
import com.example.phoebe.exception.ResourceNotFoundException;
import com.example.phoebe.mapper.RoleMapper;
import com.example.phoebe.repository.PermissionRepository;
import com.example.phoebe.repository.RoleRepository;
import com.example.phoebe.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl service;

    private Role role;
    private RoleDto roleDto;
    private Permission permission;

    @BeforeEach
    void setUp() {
        role = new Role("ADMIN", "Administrator role");
        role.setId(1L);

        roleDto = new RoleDto(1L, "ADMIN", "Administrator role", Set.of());

        permission = new Permission("READ");
        permission.setId(1L);
    }

    @Test
    void getAllRoles_ReturnsListOfRoles() {
        when(roleRepository.findAll()).thenReturn(List.of(role));
        when(roleMapper.toDto(role)).thenReturn(roleDto);

        List<RoleDto> result = service.getAllRoles();

        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).name());
    }

    @Test
    void getRoleById_ExistingId_ReturnsRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleMapper.toDto(role)).thenReturn(roleDto);

        RoleDto result = service.getRoleById(1L);

        assertNotNull(result);
        assertEquals("ADMIN", result.name());
    }

    @Test
    void getRoleById_NonExistingId_ThrowsException() {
        when(roleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getRoleById(999L));
    }

    @Test
    void createRole_SavesAndReturnsRole() {
        RoleCreateRequestDto createDto = new RoleCreateRequestDto();
        createDto.setName("EDITOR");
        createDto.setDescription("Editor role");
        createDto.setPermissionIds(Set.of(1L));

        when(permissionRepository.findAllById(any())).thenReturn(List.of(permission));
        when(roleRepository.save(any())).thenReturn(role);
        when(roleMapper.toDto(any())).thenReturn(roleDto);

        RoleDto result = service.createRole(createDto);

        assertNotNull(result);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void updateRole_UpdatesAndReturnsRole() {
        RoleUpdateRequestDto updateDto = new RoleUpdateRequestDto();
        updateDto.setName("ADMIN");
        updateDto.setDescription("Updated description");
        updateDto.setPermissionIds(Set.of(1L));

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findAllById(any())).thenReturn(List.of(permission));
        when(roleMapper.toDto(role)).thenReturn(roleDto);

        RoleDto result = service.updateRole(1L, updateDto);

        assertNotNull(result);
        verify(roleRepository).findById(1L);
    }

    @Test
    void deleteRole_ExistingId_DeletesRole() {
        when(roleRepository.existsById(1L)).thenReturn(true);

        service.deleteRole(1L);

        verify(roleRepository).deleteById(1L);
    }

    @Test
    void deleteRole_NonExistingId_ThrowsException() {
        when(roleRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.deleteRole(999L));
    }

    @Test
    void assignPermission_AddsPermissionToRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(roleMapper.toDto(role)).thenReturn(roleDto);

        RoleDto result = service.assignPermission(1L, 1L);

        assertNotNull(result);
        verify(roleRepository).findById(1L);
        verify(permissionRepository).findById(1L);
    }

    @Test
    void removePermission_RemovesPermissionFromRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(roleMapper.toDto(role)).thenReturn(roleDto);

        RoleDto result = service.removePermission(1L, 1L);

        assertNotNull(result);
        verify(roleRepository).findById(1L);
        verify(permissionRepository).findById(1L);
    }

    @Test
    void findRolesByUserId_ReturnsUserRoles() {
        when(roleRepository.findByUsersId(1L)).thenReturn(Set.of(role));
        when(roleMapper.toDto(role)).thenReturn(roleDto);

        Set<RoleDto> result = service.findRolesByUserId(1L);

        assertEquals(1, result.size());
        verify(roleRepository).findByUsersId(1L);
    }
}
