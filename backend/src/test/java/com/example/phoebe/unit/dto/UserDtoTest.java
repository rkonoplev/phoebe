package com.example.phoebe.unit.dto;

import com.example.phoebe.dto.request.UserCreateRequestDto;
import com.example.phoebe.dto.request.UserUpdateRequestDto;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void userCreateRequestDto_ShouldSetAndGetAllFields() {
        UserCreateRequestDto dto = new UserCreateRequestDto();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setActive(true);
        dto.setRoleIds(Set.of(1L, 2L));

        assertEquals("testuser", dto.getUsername());
        assertEquals("test@example.com", dto.getEmail());
        assertTrue(dto.isActive());
        assertEquals(Set.of(1L, 2L), dto.getRoleIds());
    }

    @Test
    void userCreateRequestDto_ShouldHaveDefaultActiveTrue() {
        UserCreateRequestDto dto = new UserCreateRequestDto();

        assertTrue(dto.isActive());
    }

    @Test
    void userUpdateRequestDto_ShouldSetAndGetAllFields() {
        UserUpdateRequestDto dto = new UserUpdateRequestDto();
        dto.setUsername("updateduser");
        dto.setEmail("updated@example.com");
        dto.setActive(false);
        dto.setRoleIds(Set.of(3L));

        assertEquals("updateduser", dto.getUsername());
        assertEquals("updated@example.com", dto.getEmail());
        assertFalse(dto.getActive());
        assertEquals(Set.of(3L), dto.getRoleIds());
    }
}
