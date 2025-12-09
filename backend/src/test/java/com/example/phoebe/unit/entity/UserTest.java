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
 * Unit tests for the User entity.
 */
class UserTest {

    @Test
    void constructorShouldNormalizeAndSetFields() {
        String username = "  TestUser  ";
        String email = "  TestEmail@Example.COM  ";

        User user = new User(username, "password", email, true);

        assertEquals("testuser", user.getUsername());
        assertEquals("testemail@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertTrue(user.isActive());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void constructorWithNullsShouldSetNull() {
        User user = new User(null, null, null, false);
        
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getEmail());
        assertFalse(user.isActive());
    }

    @Test
    void defaultConstructorShouldInitializeFields() {
        User user = new User();
        
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getEmail());
        assertFalse(user.isActive());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void settersAndGettersShouldWork() {
        User user = new User();
        
        user.setId(1L);
        user.setPassword("newpass");
        user.setEmail("new@test.com");
        user.setActive(true);
        
        assertEquals(1L, user.getId());
        assertEquals("newpass", user.getPassword());
        assertEquals("new@test.com", user.getEmail());
        assertTrue(user.isActive());
    }

    @Test
    void addRoleShouldMaintainBidirectionalConsistency() {
        User user = new User("testuser", "pass", "email@test.com", true);
        Role role = new Role("ADMIN", "Admin role");

        user.addRole(role);

        assertTrue(user.getRoles().contains(role));
        assertTrue(role.getUsers().contains(user));
    }

    @Test
    void addRoleWithNullShouldNotThrowException() {
        User user = new User("testuser", "pass", "email@test.com", true);
        
        assertDoesNotThrow(() -> user.addRole(null));
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void addRoleTwiceShouldBeIdempotent() {
        User user = new User("testuser", "pass", "email@test.com", true);
        Role role = new Role("ADMIN", null);
        
        user.addRole(role);
        user.addRole(role);
        
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void removeRoleShouldMaintainBidirectionalConsistency() {
        User user = new User("testuser", "pass", "email@test.com", true);
        Role role = new Role("ADMIN", "Admin role");
        user.addRole(role);

        user.removeRole(role);

        assertFalse(user.getRoles().contains(role));
        assertFalse(role.getUsers().contains(user));
    }

    @Test
    void removeRoleWithNullShouldNotThrowException() {
        User user = new User("testuser", "pass", "email@test.com", true);
        
        assertDoesNotThrow(() -> user.removeRole(null));
    }

    @Test
    void equalsAndHashCodeShouldBeBasedOnUsername() {
        User user1 = new User("testuser", "password123", "email1@test.com", true);
        User user2 = new User("testuser", "password456", "email2@test.com", false);
        User user3 = new User("anotheruser", "password123", "email1@test.com", true);

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testEqualsSameObject() {
        User user = new User("testuser", "pass", "email@test.com", true);
        assertEquals(user, user);
    }

    @Test
    void testEqualsNull() {
        User user = new User("testuser", "pass", "email@test.com", true);
        assertNotEquals(user, null);
    }

    @Test
    void testEqualsDifferentClass() {
        User user = new User("testuser", "pass", "email@test.com", true);
        assertNotEquals(user, "testuser");
    }

    @Test
    void equalsShouldReturnFalseForTransientEntityWithNullUsername() {
        User user1 = new User();
        User user2 = new User();

        assertNotEquals(user1, user2);
    }

    @Test
    void testEqualsWithNullUsername() {
        User user1 = new User(null, "pass", "email@test.com", true);
        User user2 = new User(null, "pass", "email@test.com", true);
        User user3 = new User("testuser", "pass", "email@test.com", true);
        
        assertNotEquals(user1, user2);
        assertNotEquals(user1, user3);
    }

    @Test
    void testHashCodeWithNullUsername() {
        User user = new User(null, "pass", "email@test.com", true);
        assertNotNull(user.hashCode());
    }

    @Test
    void testHashCodeConsistency() {
        User user = new User("testuser", "pass", "email@test.com", true);
        int hash1 = user.hashCode();
        int hash2 = user.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void toStringShouldContainKeyFields() {
        User user = new User("testuser", "password", "test@example.com", true);
        user.setId(1L);

        String toString = user.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("username='testuser'"));
        assertTrue(toString.contains("email='test@example.com'"));
        assertTrue(toString.contains("active=true"));
    }

    @Test
    void toStringShouldHandleNullFields() {
        User user = new User();
        
        assertDoesNotThrow(() -> user.toString());
        String result = user.toString();
        assertTrue(result.contains("User{"));
    }

    @Test
    void activeDefaultsToFalse() {
        User user = new User();
        assertFalse(user.isActive());
    }

    @Test
    void rolesCollectionIsInitialized() {
        User user = new User();
        assertNotNull(user.getRoles());
        assertEquals(0, user.getRoles().size());
    }

    @Test
    void canAddMultipleRoles() {
        User user = new User("testuser", "pass", "email@test.com", true);
        Role role1 = new Role("ADMIN", null);
        Role role2 = new Role("EDITOR", null);
        
        user.addRole(role1);
        user.addRole(role2);
        
        assertEquals(2, user.getRoles().size());
    }

    @Test
    void setRolesShouldReplaceCollection() {
        User user = new User("testuser", "pass", "email@test.com", true);
        Role role1 = new Role("ADMIN", null);
        user.addRole(role1);
        
        assertEquals(1, user.getRoles().size());
        
        java.util.Set<Role> newRoles = new java.util.HashSet<>();
        Role role2 = new Role("EDITOR", null);
        newRoles.add(role2);
        
        user.setRoles(newRoles);
        
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(role2));
        assertFalse(user.getRoles().contains(role1));
    }
}
