package com.example.phoebe.unit.mapper;

import com.example.phoebe.entity.Permission;
import com.example.phoebe.entity.Role;
import com.example.phoebe.entity.Term;
import com.example.phoebe.mapper.BaseMapper;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link BaseMapper}.
 * Tests all common mapping methods including edge cases with null and empty collections.
 */
class BaseMapperTest {

    private final BaseMapper mapper = new BaseMapper() {};

    @Test
    void termsToNamesShouldMapTermsToNames() {
        Term term1 = new Term("Technology", "category");
        Term term2 = new Term("Science", "category");
        Set<Term> terms = Set.of(term1, term2);

        Set<String> names = mapper.termsToNames(terms);

        assertEquals(2, names.size());
        assertTrue(names.contains("Technology"));
        assertTrue(names.contains("Science"));
    }

    @Test
    void termsToNamesShouldReturnEmptySetWhenNull() {
        Set<String> names = mapper.termsToNames(null);

        assertNotNull(names);
        assertTrue(names.isEmpty());
    }

    @Test
    void termsToNamesShouldReturnEmptySetWhenEmpty() {
        Set<String> names = mapper.termsToNames(Collections.emptySet());

        assertNotNull(names);
        assertTrue(names.isEmpty());
    }

    @Test
    void rolesToNamesShouldMapRolesToNames() {
        Role admin = new Role("ADMIN", "Administrator role");
        Role editor = new Role("EDITOR", "Editor role");
        Set<Role> roles = Set.of(admin, editor);

        Set<String> names = mapper.rolesToNames(roles);

        assertEquals(2, names.size());
        assertTrue(names.contains("ADMIN"));
        assertTrue(names.contains("EDITOR"));
    }

    @Test
    void rolesToNamesShouldReturnDefaultEditorWhenNull() {
        Set<String> names = mapper.rolesToNames(null);

        assertNotNull(names);
        assertEquals(1, names.size());
        assertTrue(names.contains("EDITOR"));
    }

    @Test
    void rolesToNamesShouldReturnDefaultEditorWhenEmpty() {
        Set<String> names = mapper.rolesToNames(Collections.emptySet());

        assertNotNull(names);
        assertEquals(1, names.size());
        assertTrue(names.contains("EDITOR"));
    }

    @Test
    void permissionsToNamesShouldMapPermissionsToNames() {
        Permission read = new Permission("READ");
        Permission write = new Permission("WRITE");
        Set<Permission> permissions = Set.of(read, write);

        Set<String> names = mapper.permissionsToNames(permissions);

        assertEquals(2, names.size());
        assertTrue(names.contains("read"));
        assertTrue(names.contains("write"));
    }

    @Test
    void permissionsToNamesShouldReturnEmptySetWhenNull() {
        Set<String> names = mapper.permissionsToNames(null);

        assertNotNull(names);
        assertTrue(names.isEmpty());
    }

    @Test
    void permissionsToNamesShouldReturnEmptySetWhenEmpty() {
        Set<String> names = mapper.permissionsToNames(Collections.emptySet());

        assertNotNull(names);
        assertTrue(names.isEmpty());
    }
}
