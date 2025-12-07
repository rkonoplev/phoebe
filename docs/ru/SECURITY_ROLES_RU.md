> [Вернуться к содержанию документации](./README.md)

# Руководство по реализации безопасности на основе ролей

> Для определения ключевых терминов и технологий, пожалуйста, обратитесь к **[Глоссарию](./GLOSSARY_RU.md)**.

Этот документ описывает **правильные** требования к реализации безопасности для ролей ADMIN и EDITOR в Phoebe CMS.

## Определения ролей

### Роль ADMIN
- **Полный доступ к системе** - может выполнять любые операции.
- **Управление всем контентом** - создание, чтение, обновление, удаление любых статей.
- **Управление пользователями** - управление профилями и ролями пользователей.
- **Конфигурация системы** - управление терминами, категориями, настройками.

### Роль EDITOR
- **Доступ ко всему контенту** - может просматривать все статьи.
- **Только собственный контент** - может управлять (создавать, редактировать, удалять, публиковать/снимать с публикации) только статьями, автором которых он является.
- **Создание контента** - может создавать новые статьи (становится их автором).
- **Операции, ограниченные авторством** - редактирование/удаление только там, где `author_id` совпадает с ID
  пользователя.
- **Контроль публикации** - публикация/снятие с публикации только собственных статей.

## Реализация безопасности

### 0. Защита от перебора паролей (Rate Limiting)

**Предотвращение brute-force атак:**

Phoebe CMS реализует строгое ограничение частоты запросов для защиты от атак перебора паролей:

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

**Лимиты запросов:**
- **Эндпоинт аутентификации** (`/api/admin/auth/me`): 5 попыток за 5 минут с одного IP
- **Admin API**: 50 запросов в минуту с одного IP
- **Public API**: 100 запросов в минуту с одного IP

**Реализация:**
```java
@GetMapping("/me")
public ResponseEntity<UserDto> getCurrentUser(Authentication authentication, HttpServletRequest request) {
    String ipAddress = getClientIp(request);
    Bucket bucket = rateLimitConfig.getAuthBucket(ipAddress);
    
    if (!bucket.tryConsume(1)) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
    // ... логика аутентификации
}
```

**Обработка на фронтенде:**
- HTTP 429 (Too Many Requests) отображает: "Слишком много попыток входа. Пожалуйста, подождите 5 минут и попробуйте снова."
- Понятные сообщения об ошибках помогают пользователям понять, когда можно повторить попытку

### 1. Безопасность на уровне сервисов

#### Методы NewsService - Правильная авторизация

```java
// ADMIN: полный доступ, EDITOR: только свой контент
@PreAuthorize("@newsServiceImpl.canAccessNews(#id, authentication)")
public NewsDto update(Long id, NewsUpdateRequestDto request, Authentication auth) {
    // Проверка безопасности выполняется через @PreAuthorize
}

@PreAuthorize("@newsServiceImpl.canAccessNews(#id, authentication)")
public void delete(Long id, Authentication auth) {
    // Проверка безопасности выполняется через @PreAuthorize
}

// ADMIN: весь контент, EDITOR: весь контент (только для чтения чужого)
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')") // Базовая проверка роли для доступа к методу
public Page<NewsDto> findAll(Pageable pageable, Authentication auth) {
    // Здесь нет ручной фильтрации по автору, EDITOR видит все новости.
    // Детальные проверки на изменение/удаление чужого контента выполняются в update/delete через @PreAuthorize.
    return newsRepository.findAll(pageable).map(newsMapper::toDto);
}
```

### 2. Методы сервиса авторизации

```java
public boolean canAccessNews(Long newsId, Authentication authentication) {
    if (hasAdminRole(authentication)) {
        return true; // ADMIN может получить доступ к любой новости
    }
    
    if (hasEditorRole(authentication)) {
        // EDITOR может получить доступ только к своим новостям для операций изменения/удаления
        return isAuthor(newsId, authentication);
    }
    
    return false;
}

public boolean isAuthor(Long newsId, Authentication authentication) {
    Long currentUserId = getCurrentUserId(authentication);
    return newsRepository.existsByIdAndAuthorId(newsId, currentUserId);
}

// Вспомогательные методы для извлечения информации об аутентификации
private boolean hasAdminRole(Authentication authentication) {
    return authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
}

private boolean hasEditorRole(Authentication authentication) {
    return authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_EDITOR"));
}

private Long getCurrentUserId(Authentication authentication) {
    // Предполагаем, что Principal является UserDetails и содержит ID
    // Или извлекаем ID из кастомного Principal
    // Пример: return ((MyUserDetails) authentication.getPrincipal()).getId();
    // Для простоты примера:
    return 1L; // Заглушка, должна быть реальная реализация
}
```

### 3. Улучшения на уровне репозитория

```java
// Проверка, является ли пользователь автором конкретной статьи
boolean existsByIdAndAuthorId(Long id, Long authorId);

// Для роли EDITOR - находить только собственный контент (если это требуется для специфичных UI)
// В findAll() выше EDITOR видит весь контент, но этот метод может быть полезен для других целей.
Page<News> findByAuthorId(Long authorId, Pageable pageable);
```

### 4. Конфигурация безопасности

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Включает поддержку аннотаций @PreAuthorize и @PostAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "EDITOR") // Базовый доступ для ADMIN/EDITOR
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .build();
    }
}
```
*   **Примечание**: `authorizeHttpRequests` обеспечивает базовый уровень доступа к эндпоинтам. Более детальные проверки на основе ролей и авторства выполняются на уровне сервисов с помощью аннотаций `@PreAuthorize`.

### 5. Стратегия тестирования

```java
@Test
void findAll_AdminRole_ShouldReturnAllNews() {
    // Учитывая аутентификацию администратора
    Authentication adminAuth = createAdminAuthentication();
    // Когда
    var result = newsService.findAll(pageable, adminAuth);
    // Тогда - должны вернуться все новости
    assertEquals(3, result.getContent().size());
}

@Test
void findAll_EditorRole_ShouldReturnAllNewsButCannotModifyOthers() {
    // Учитывая аутентификацию редактора
    Authentication editorAuth = createEditorAuthentication();
    // Когда
    var result = newsService.findAll(pageable, editorAuth);
    // Тогда - должны вернуться все новости (редактор может их просматривать)
    assertEquals(3, result.getContent().size());
}

@Test
void update_EditorRole_ShouldDenyUpdatingOthersNews() {
    // Редактор пытается обновить чужую новость - должно завершиться ошибкой
    assertThrows(AccessDeniedException.class, 
        () -> newsService.update(1L, request, editorAuth));
}
```
*   **Примечание**: В реальных тестах `createAdminAuthentication()` и `createEditorAuthentication()` должны возвращать мок-объекты `Authentication` с соответствующими ролями и `Principal` (например, `UserDetails) для извлечения `userId`.
