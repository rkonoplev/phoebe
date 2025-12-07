> [Back to Documentation Contents](./README.md)

# Role-Based Security Implementation Guide

This document outlines the **correct** implementation requirements for ADMIN and EDITOR role security in Phoebe CMS.

## Role Definitions

### ADMIN Role
- **Full system access** - can perform any operation.
- **All content management** - create, read, update, delete any article.
- **User management** - manage user profiles and roles.
- **System configuration** - manage terms, categories, settings.

### EDITOR Role
- **Access to all content** - can view all articles.
- **Own content only** - can manage (create, edit, delete, publish/unpublish) only articles they authored.
- **Content creation** - can create new articles (becomes author).
- **Author-restricted operations** - edit/delete only where `author_id` matches user ID.
- **Publication control** - publish/unpublish own articles only.

## Security Implementation

### 0. Rate Limiting Protection

**Brute-Force Attack Prevention:**

Phoebe CMS implements strict rate limiting to protect against brute-force password attacks:

```java
// RateLimitConfig.java
public Bucket getAuthBucket(String ipAddress) {
    return buckets.computeIfAbsent("auth:" + ipAddress, key ->
        Bucket.builder()
            .addLimit(Bandwidth.builder()
                .capacity(5)
                .refillIntervally(5, Duration.ofMinutes(5))
                .build())
            .build()
    );
}
```

**Rate Limits:**
- **Authentication endpoint** (`/api/admin/auth/me`): 5 attempts per 5 minutes per IP
- **Admin API**: 50 requests per minute per IP
- **Public API**: 100 requests per minute per IP

**Implementation:**
```java
@GetMapping("/me")
public ResponseEntity<UserDto> getCurrentUser(Authentication authentication, HttpServletRequest request) {
    String ipAddress = getClientIp(request);
    Bucket bucket = rateLimitConfig.getAuthBucket(ipAddress);
    
    if (!bucket.tryConsume(1)) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
    // ... authentication logic
}
```

**Frontend Handling:**
- HTTP 429 (Too Many Requests) displays: "Too many login attempts. Please wait 5 minutes and try again."
- User-friendly error messages guide users to wait before retrying

### 1. Service Layer Security

#### NewsService Methods - Proper Authorization

```java
// ADMIN: Full access, EDITOR: Own content only
@PreAuthorize("@newsServiceImpl.canAccessNews(#id, authentication)")
public NewsDto update(Long id, NewsUpdateRequestDto request, Authentication auth) {
    // Security check handled by @PreAuthorize
}

@PreAuthorize("@newsServiceImpl.canAccessNews(#id, authentication)")
public void delete(Long id, Authentication auth) {
    // Security check handled by @PreAuthorize
}

// ADMIN: all content, EDITOR: all content (read-only for others')
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')") // Basic role check for method access
public Page<NewsDto> findAll(Pageable pageable, Authentication auth) {
    // No manual author filtering here; EDITOR sees all news.
    // Detailed checks for modifying/deleting others' content are done in update/delete via @PreAuthorize.
    return newsRepository.findAll(pageable).map(newsMapper::toDto);
}
```

### 2. Authorization Service Methods

```java
public boolean canAccessNews(Long newsId, Authentication authentication) {
    if (hasAdminRole(authentication)) {
        return true; // ADMIN can access any news
    }
    
    if (hasEditorRole(authentication)) {
        // EDITOR can only access their own news for modification/deletion operations
        return isAuthor(newsId, authentication);
    }
    
    return false;
}

public boolean isAuthor(Long newsId, Authentication authentication) {
    Long currentUserId = getCurrentUserId(authentication);
    return newsRepository.existsByIdAndAuthorId(newsId, currentUserId);
}

// Helper methods to extract authentication information
private boolean hasAdminRole(Authentication authentication) {
    return authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
}

private boolean hasEditorRole(Authentication authentication) {
    return authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_EDITOR"));
}

private Long getCurrentUserId(Authentication authentication) {
    // Assumes Principal is UserDetails and contains an ID
    // Or extract ID from a custom Principal
    // Example: return ((MyUserDetails) authentication.getPrincipal()).getId();
    // For simplicity of example:
    return 1L; // Placeholder, should be a real implementation
}
```

### 3. Repository Layer Enhancements

```java
// Check if user is author of specific article
boolean existsByIdAndAuthorId(Long id, Long authorId);

// For EDITOR role - find only own content (if needed for specific UI)
// In findAll() above, EDITOR sees all content, but this method can be useful for other purposes.
Page<News> findByAuthorId(Long authorId, Pageable pageable);
```

### 4. Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enables support for @PreAuthorize and @PostAuthorize annotations
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "EDITOR") // Basic access for ADMIN/EDITOR
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .build();
    }
}
```
*   **Note**: `authorizeHttpRequests` provides a basic level of access to endpoints. More detailed checks based on roles and authorship are performed at the service layer using `@PreAuthorize` annotations.

### 5. Testing Strategy

```java
@Test
void findAll_AdminRole_ShouldReturnAllNews() {
    // Given admin authentication
    Authentication adminAuth = createAdminAuthentication();
    // When
    var result = newsService.findAll(pageable, adminAuth);
    // Then - should return all news
    assertEquals(3, result.getContent().size());
}

@Test
void findAll_EditorRole_ShouldReturnAllNewsButCannotModifyOthers() {
    // Given editor authentication
    Authentication editorAuth = createEditorAuthentication();
    // When
    var result = newsService.findAll(pageable, editorAuth);
    // Then - should return all news (editor can view them)
    assertEquals(3, result.getContent().size());
}

@Test
void update_EditorRole_ShouldDenyUpdatingOthersNews() {
    // Editor tries to update another's news - should result in an error
    assertThrows(AccessDeniedException.class, 
        () -> newsService.update(1L, request, editorAuth));
}
```
*   **Note**: In real tests, `createAdminAuthentication()` and `createEditorAuthentication()` should return mock `Authentication` objects with appropriate roles and `Principal` (e.g., `UserDetails`) to extract `userId`.
