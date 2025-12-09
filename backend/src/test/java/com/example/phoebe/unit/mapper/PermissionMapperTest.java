package com.example.phoebe.unit.mapper;

import com.example.phoebe.dto.response.PermissionDto;
import com.example.phoebe.entity.Permission;
import com.example.phoebe.mapper.PermissionMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionMapperTest {

    private final PermissionMapper mapper = Mappers.getMapper(PermissionMapper.class);

    @Test
    void toDtoShouldMapSinglePermission() {
        Permission permission = new Permission("READ");
        permission.setId(1L);

        PermissionDto dto = mapper.toDto(permission);

        assertEquals("read", dto.name()); // Permission normalizes to lowercase
    }

    @Test
    void toDtoShouldMapListOfPermissions() {
        Permission perm1 = new Permission("READ");
        perm1.setId(1L);
        Permission perm2 = new Permission("WRITE");
        perm2.setId(2L);

        List<PermissionDto> dtos = mapper.toDto(List.of(perm1, perm2));

        assertEquals(2, dtos.size());
        assertEquals("read", dtos.get(0).name()); // Permission normalizes to lowercase
        assertEquals("write", dtos.get(1).name());
    }
}
