package com.example.phoebe.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the Role entity, focusing on its business key-based equals/hashCode and helper methods.
 */
class RoleTest {

    @Test
    void constructorShouldNormalizeName() {
        Role role = new Role("  editor  ", "Test Description");
        assertEquals("EDITOR", role.getName());
        assertEquals("Test Description", role.getDescription());
    }

    @Test
    void constructorWithNullNameShouldSetNull() {
        Role role = new Role(null, "Description");
        assertNull(role.getName());
        assertEquals("Description", role.getDescription());
    }

    @Test
    void constructorWithNullDescriptionShouldWork() {
        Role role = new Role("ADMIN", null);
        assertEquals("ADMIN", role.getName());
        assertNull(role.getDescription());
    }

    @Test
    void defaultConstructorShouldInitializeFields() {
        Role role = new Role();
        assertNull(role.getId());
        assertNull(role.getName());
        assertNull(role.getDescription());
        assertNotNull(role.getUsers());
        assertNotNull(role.getPermissions());
        assertTrue(role.getUsers().isEmpty());
        assertTrue(role.getPermissions().isEmpty());
    }

    @Test
    void settersAndGettersShouldWork() {
        Role role = new Role("ADMIN", null);
        role.setId(1L);
        role.setDescription("Administrator role");
        
        assertEquals(1L, role.getId());
        assertEquals("ADMIN", role.getName());
        assertEquals("Administrator role", role.getDescription());
    }

    @Test
    void testAddUser() {
        Role role = new Role("ADMIN", null);
        User user = new User("testuser", "pass", "email", true);

        role.addUser(user);

        assertTrue(role.getUsers().contains(user));
        assertTrue(user.getRoles().contains(role));
    }

    @Test
    void addUserWithNullShouldNotThrowException() {
        Role role = new Role("ADMIN", null);
        assertDoesNotThrow(() -> role.addUser(null));
        assertTrue(role.getUsers().isEmpty());
    }

    @Test
    void addUserTwiceShouldBeIdempotent() {
        Role role = new Role("ADMIN", null);
        User user = new User("testuser", "pass", "email", true);
        
        role.addUser(user);
        role.addUser(user);
        
        assertEquals(1, role.getUsers().size());
    }

    @Test
    void removeUserShouldRemoveBidirectionalRelationship() {
        Role role = new Role("ADMIN", null);
        User user = new User("testuser", "pass", "email", true);
        role.addUser(user);
        
        role.removeUser(user);
        
        assertFalse(role.getUsers().contains(user));
        assertFalse(user.getRoles().contains(role));
    }

    @Test
    void removeUserWithNullShouldNotThrowException() {
        Role role = new Role("ADMIN", null);
        assertDoesNotThrow(() -> role.removeUser(null));
    }

    @Test
    void testAddPermission() {
        Role role = new Role("ADMIN", null);
        Permission permission = new Permission("news:create");

        role.addPermission(permission);

        assertTrue(role.getPermissions().contains(permission));
        assertTrue(permission.getRoles().contains(role));
    }

    @Test
    void addPermissionWithNullShouldNotThrowException() {
        Role role = new Role("ADMIN", null);
        assertDoesNotThrow(() -> role.addPermission(null));
        assertTrue(role.getPermissions().isEmpty());
    }

    @Test
    void addPermissionTwiceShouldBeIdempotent() {
        Role role = new Role("ADMIN", null);
        Permission permission = new Permission("news:create");
        
        role.addPermission(permission);
        role.addPermission(permission);
        
        assertEquals(1, role.getPermissions().size());
    }

    @Test
    void removePermissionShouldRemoveBidirectionalRelationship() {
        Role role = new Role("ADMIN", null);
        Permission permission = new Permission("news:create");
        role.addPermission(permission);
        
        role.removePermission(permission);
        
        assertFalse(role.getPermissions().contains(permission));
        assertFalse(permission.getRoles().contains(role));
    }

    @Test
    void removePermissionWithNullShouldNotThrowException() {
        Role role = new Role("ADMIN", null);
        assertDoesNotThrow(() -> role.removePermission(null));
    }

    @Test
    void testEqualsAndHashCode() {
        Role role1 = new Role("  ADMIN  ", "First description");
        Role role2 = new Role("ADMIN", "Second description");
        Role role3 = new Role("EDITOR", "First description");

        assertEquals(role1, role2);
        assertEquals(role1.hashCode(), role2.hashCode());
        assertNotEquals(role1, role3);
    }

    @Test
    void testEqualsSameObject() {
        Role role = new Role("ADMIN", null);
        assertEquals(role, role);
    }

    @Test
    void testEqualsNull() {
        Role role = new Role("ADMIN", null);
        assertNotEquals(role, null);
    }

    @Test
    void testEqualsDifferentClass() {
        Role role = new Role("ADMIN", null);
        assertNotEquals(role, "ADMIN");
    }

    @Test
    void testEqualsWithNullName() {
        Role role1 = new Role(null, null);
        Role role2 = new Role(null, null);
        Role role3 = new Role("ADMIN", null);
        
        assertNotEquals(role1, role2);
        assertNotEquals(role1, role3);
    }

    @Test
    void testHashCodeWithNullName() {
        Role role = new Role(null, null);
        assertNotNull(role.hashCode());
    }

    @Test
    void toStringShouldContainKeyFields() {
        Role role = new Role("ADMIN", "Administrator");
        role.setId(1L);
        
        String result = role.toString();
        
        assertTrue(result.contains("Role"));
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("name='ADMIN'"));
    }
}
