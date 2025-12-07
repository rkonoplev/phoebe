package com.example.phoebe.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Permission entity, focusing on its business key-based equals and hashCode.
 */
class PermissionTest {

    @Test
    void constructorShouldNormalizeName() {
        Permission permission = new Permission("  news:READ  ");
        assertEquals("news:read", permission.getName());
    }

    @Test
    void constructorWithNullNameShouldSetNull() {
        Permission permission = new Permission(null);
        assertNull(permission.getName());
    }

    @Test
    void defaultConstructorShouldInitializeFields() {
        Permission permission = new Permission();
        assertNull(permission.getId());
        assertNull(permission.getName());
        assertNull(permission.getDescription());
        assertNotNull(permission.getRoles());
        assertTrue(permission.getRoles().isEmpty());
    }

    @Test
    void settersAndGettersShouldWork() {
        Permission permission = new Permission("news:read");
        permission.setId(1L);
        permission.setDescription("Read news articles");
        
        assertEquals(1L, permission.getId());
        assertEquals("news:read", permission.getName());
        assertEquals("Read news articles", permission.getDescription());
    }

    @Test
    void testEquals() {
        Permission permission1 = new Permission("news:read");
        Permission permission2 = new Permission("  news:READ  ");
        Permission permission3 = new Permission("news:write");

        assertEquals(permission1, permission2);
        assertNotEquals(permission1, permission3);
    }

    @Test
    void testEqualsSameObject() {
        Permission permission = new Permission("news:read");
        assertEquals(permission, permission);
    }

    @Test
    void testEqualsNull() {
        Permission permission = new Permission("news:read");
        assertNotEquals(permission, null);
    }

    @Test
    void testEqualsDifferentClass() {
        Permission permission = new Permission("news:read");
        assertNotEquals(permission, "news:read");
    }

    @Test
    void testEqualsWithNullName() {
        Permission permission1 = new Permission(null);
        Permission permission2 = new Permission(null);
        Permission permission3 = new Permission("news:read");
        
        assertNotEquals(permission1, permission2);
        assertNotEquals(permission1, permission3);
    }

    @Test
    void testHashCode() {
        Permission permission1 = new Permission("news:read");
        Permission permission2 = new Permission("  news:READ  ");
        assertEquals(permission1.hashCode(), permission2.hashCode());
    }

    @Test
    void testHashCodeWithNullName() {
        Permission permission = new Permission(null);
        assertNotNull(permission.hashCode());
    }

    @Test
    void addRoleShouldEstablishBidirectionalRelationship() {
        Permission permission = new Permission("news:read");
        Role role = new Role("EDITOR");
        
        permission.addRole(role);
        
        assertTrue(permission.getRoles().contains(role));
        assertTrue(role.getPermissions().contains(permission));
    }

    @Test
    void addRoleWithNullShouldNotThrowException() {
        Permission permission = new Permission("news:read");
        assertDoesNotThrow(() -> permission.addRole(null));
        assertTrue(permission.getRoles().isEmpty());
    }

    @Test
    void addRoleTwiceShouldBeIdempotent() {
        Permission permission = new Permission("news:read");
        Role role = new Role("EDITOR");
        
        permission.addRole(role);
        permission.addRole(role);
        
        assertEquals(1, permission.getRoles().size());
    }

    @Test
    void removeRoleShouldRemoveBidirectionalRelationship() {
        Permission permission = new Permission("news:read");
        Role role = new Role("EDITOR");
        permission.addRole(role);
        
        permission.removeRole(role);
        
        assertFalse(permission.getRoles().contains(role));
        assertFalse(role.getPermissions().contains(permission));
    }

    @Test
    void removeRoleWithNullShouldNotThrowException() {
        Permission permission = new Permission("news:read");
        assertDoesNotThrow(() -> permission.removeRole(null));
    }

    @Test
    void toStringShouldContainKeyFields() {
        Permission permission = new Permission("news:read");
        permission.setId(1L);
        
        String result = permission.toString();
        
        assertTrue(result.contains("Permission"));
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("name='news:read'"));
    }
}
