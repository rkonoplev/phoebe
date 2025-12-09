package com.example.phoebe.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the Term entity, focusing on its business key-based equals and hashCode.
 */
class TermTest {

    @Test
    void constructorShouldNormalizeAndSetFields() {
        String name = "  Technology  ";
        String vocabulary = "  Category  ";

        Term term = new Term(name, vocabulary);

        assertEquals("Technology", term.getName());
        assertEquals("category", term.getVocabulary());
    }

    @Test
    void constructorWithNullsShouldSetNull() {
        Term term = new Term(null, null);
        assertNull(term.getName());
        assertNull(term.getVocabulary());
    }

    @Test
    void defaultConstructorShouldInitializeFields() {
        Term term = new Term();
        assertNull(term.getId());
        assertNull(term.getName());
        assertNull(term.getVocabulary());
    }

    @Test
    void settersAndGettersShouldWork() {
        Term term = new Term("Technology", "category");
        term.setId(1L);
        
        assertEquals(1L, term.getId());
        assertEquals("Technology", term.getName());
        assertEquals("category", term.getVocabulary());
    }

    @Test
    void equalsAndHashCodeShouldBeBasedOnBusinessKey() {
        Term term1 = new Term("  Technology  ", "  CATEGORY  ");
        Term term2 = new Term("Technology", "category");
        Term term3 = new Term("Sports", "category");
        Term term4 = new Term("Technology", "tag");

        assertEquals(term1, term2);
        assertEquals(term1.hashCode(), term2.hashCode());
        assertNotEquals(term1, term3);
        assertNotEquals(term1, term4);
    }

    @Test
    void testEqualsSameObject() {
        Term term = new Term("Technology", "category");
        assertEquals(term, term);
    }

    @Test
    void testEqualsNull() {
        Term term = new Term("Technology", "category");
        assertNotEquals(term, null);
    }

    @Test
    void testEqualsDifferentClass() {
        Term term = new Term("Technology", "category");
        assertNotEquals(term, "Technology");
    }

    @Test
    void equalsShouldReturnFalseForTransientEntityWithNullKeys() {
        Term term1 = new Term();
        Term term2 = new Term();
        assertNotEquals(term1, term2);
    }

    @Test
    void testEqualsWithNullName() {
        Term term1 = new Term(null, "category");
        Term term2 = new Term(null, "category");
        Term term3 = new Term("Technology", "category");
        
        assertNotEquals(term1, term2);
        assertNotEquals(term1, term3);
    }

    @Test
    void testEqualsWithNullVocabulary() {
        Term term1 = new Term("Technology", null);
        Term term2 = new Term("Technology", null);
        Term term3 = new Term("Technology", "category");
        
        assertNotEquals(term1, term2);
        assertNotEquals(term1, term3);
    }

    @Test
    void testEqualsWithBothNull() {
        Term term1 = new Term(null, null);
        Term term2 = new Term(null, null);
        
        assertNotEquals(term1, term2);
    }

    @Test
    void testHashCodeWithNullFields() {
        Term term = new Term(null, null);
        assertNotNull(term.hashCode());
    }

    @Test
    void testHashCodeConsistency() {
        Term term = new Term("Technology", "category");
        int hash1 = term.hashCode();
        int hash2 = term.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void toStringShouldContainKeyFields() {
        Term term = new Term("Technology", "category");
        term.setId(1L);

        String toString = term.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name='Technology'"));
        assertTrue(toString.contains("vocabulary='category'"));
    }

    @Test
    void toStringShouldHandleNullFields() {
        Term term = new Term();
        String result = term.toString();
        
        assertTrue(result.contains("Term"));
        assertDoesNotThrow(() -> term.toString());
    }
}
