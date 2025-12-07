# Архитектура технических/статических страниц

> [Вернуться к содержанию документации](./README.md)

## Обзор

Технические страницы (О нас, Контакты, Политика конфиденциальности и т.д.) хранятся в базе данных и используют существующую систему News/Content вместо статических файлов на фронтенде.

## Архитектурное решение

### Почему используется существующая система News?

**Преимущества:**
- ✅ Не требует создания отдельной entity Page
- ✅ Использует существующую инфраструктуру (CRUD, валидация, безопасность)
- ✅ Единая система управления контентом через админку
- ✅ Поддержка версионирования и аудита
- ✅ Гибкость в управлении через интерфейс администратора

### Как это работает?

Технические страницы отличаются от новостных статей специальным **vocabulary "page_type"**:

```
Таблица: terms
+----+-------------------+-------------+
| id | name              | vocabulary  |
+----+-------------------+-------------+
| 1  | about             | page_type   |
| 2  | contact           | page_type   |
| 3  | privacy           | page_type   |
| 4  | terms_of_service  | page_type   |
| 5  | advertising       | page_type   |
+----+-------------------+-------------+

Таблица: content (News)
+----+------------------+--------+------------+
| id | title            | body   | published  |
+----+------------------+--------+------------+
| 10 | About Us         | ...    | 1          |
| 11 | Contact Us       | ...    | 1          |
+----+------------------+--------+------------+

Таблица: content_terms (связь)
+------------+---------+
| content_id | term_id |
+------------+---------+
| 10         | 1       | (About Us -> about)
| 11         | 2       | (Contact Us -> contact)
+------------+---------+
```

## База данных

### Миграция

Файл: `backend/src/main/resources/db/migration/common/V11__add_static_pages.sql`

Миграция создает:
1. **Термины vocabulary "page_type"**: about, contact, privacy, terms_of_service, advertising, editorial_statute, community_rules, technical_sheet
2. **Записи в таблице content**: Технические страницы с начальным контентом
3. **Связи content_terms**: Привязка страниц к соответствующим терминам page_type

### Запуск миграции

Миграция выполняется автоматически при запуске приложения через Flyway:

```bash
# Запуск бэкенда (миграция выполнится автоматически)
cd /Users/rk/dev/java/phoebe
make run
```

## Backend API

### Существующие эндпоинты

Технические страницы доступны через стандартные эндпоинты News API:

**Публичные (без аутентификации):**
```
GET /api/public/news/{id}              - Получить страницу по ID
GET /api/public/news/term/{termId}     - Получить все страницы по page_type
```

**Admin (с Basic Auth):**
```
GET    /api/admin/news                 - Список всех страниц
POST   /api/admin/news                 - Создать новую страницу
PUT    /api/admin/news/{id}            - Обновить страницу
DELETE /api/admin/news/{id}            - Удалить страницу
```

### Фильтрация технических страниц

Для получения только технических страниц используйте фильтр по vocabulary:

```javascript
// Получить ID термина "about"
GET /api/public/terms?vocabulary=page_type

// Получить страницу "About Us" по term_id
GET /api/public/news/term/{about_term_id}
```

## Frontend

### Структура страниц

**Удалены статические заглушки:**
- ❌ `pages/contact.js`
- ❌ `pages/advertising.js`
- ❌ `pages/community-rules.js`
- ❌ `pages/editorial-statute.js`
- ❌ `pages/privacy-settings.js`
- ❌ `pages/technical-sheet.js`

**Используется динамическая страница:**
- ✅ `pages/page/[slug].js` - Универсальная страница для всех технических страниц

### Маршрутизация

```
URL                          -> Vocabulary Term -> Content
/page/about                  -> page_type:about  -> "About Us"
/page/contact                -> page_type:contact -> "Contact Us"
/page/privacy                -> page_type:privacy -> "Privacy Policy"
/page/terms-of-service       -> page_type:terms_of_service -> "Terms of Service"
/page/advertising            -> page_type:advertising -> "Advertise With Us"
```

### Компонент Layout.js

Footer динамически формирует ссылки на технические страницы:

```jsx
<FooterLink href="/page/about">About Us</FooterLink>
<FooterLink href="/page/terms-of-service">Terms of Service</FooterLink>
<FooterLink href="/page/privacy">Privacy Policy</FooterLink>
<FooterLink href="/page/advertising">Advertise</FooterLink>
<FooterLink href="/page/contact">Contact</FooterLink>
```

## Управление через админку

### Создание новой технической страницы

1. Войти в админку: `/admin/login`
2. Перейти в "News Management": `/admin/news`
3. Создать новую запись: `/admin/news/new`
4. Заполнить поля:
   - **Title**: Название страницы (например, "FAQ")
   - **Body**: Содержимое страницы (HTML)
   - **Teaser**: Краткое описание
   - **Published**: Установить в true
   - **Terms**: Выбрать соответствующий термин из vocabulary "page_type"

### Редактирование существующей страницы

1. Перейти в список: `/admin/news`
2. Найти нужную страницу (фильтр по page_type)
3. Нажать "Edit"
4. Внести изменения и сохранить

## Преимущества подхода

### Для разработчиков
- Единая кодовая база для всего контента
- Не нужно дублировать логику CRUD
- Переиспользование существующих компонентов
- Простота поддержки

### Для администраторов
- Управление всем контентом в одном месте
- Единый интерфейс для редактирования
- История изменений (через auditing)
- Гибкость в создании новых страниц

### Для SEO
- Динамические meta-теги
- Возможность использования SSG/ISR
- Контроль над URL структурой
- Структурированные данные

## Будущие улучшения

### Настройки сайта (Site Settings)

Планируется создать отдельную таблицу для хранения настроек футера:

```sql
CREATE TABLE site_settings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  setting_key VARCHAR(100) UNIQUE NOT NULL,
  setting_value TEXT,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Пример: хранение ссылок футера
INSERT INTO site_settings (setting_key, setting_value) VALUES
  ('footer_links', '[
    {"label": "About Us", "slug": "about"},
    {"label": "Contact", "slug": "contact"},
    {"label": "Privacy", "slug": "privacy"}
  ]');
```

Это позволит:
- Управлять порядком ссылок в футере
- Добавлять/удалять ссылки без изменения кода
- Настраивать отображаемые названия

### Slug-based URLs

Вместо `/page/about` можно использовать более гибкую систему:
- Добавить поле `slug` в таблицу content
- Использовать `/page/{slug}` вместо vocabulary term
- Поддержка кастомных URL для каждой страницы

## Примеры использования

### Получение страницы "About Us"

**Backend (Java):**
```java
// Получить термин page_type:about
Term aboutTerm = termRepository.findByNameAndVocabulary("about", "page_type");

// Получить страницу с этим термином
List<News> pages = newsRepository.findByTermsContaining(aboutTerm);
News aboutPage = pages.get(0);
```

**Frontend (Next.js):**
```javascript
// pages/page/[slug].js
export async function getStaticProps({ params }) {
  const { slug } = params;
  
  // Получить термин по slug
  const term = await api.get(`/api/public/terms?vocabulary=page_type&name=${slug}`);
  
  // Получить страницу по term_id
  const page = await api.get(`/api/public/news/term/${term.id}`);
  
  return { props: { page } };
}
```

## Заключение

Использование существующей системы News для технических страниц - это pragmatic решение, которое:
- Упрощает архитектуру
- Ускоряет разработку
- Обеспечивает гибкость
- Сохраняет единообразие системы

Все технические страницы управляются через единый интерфейс администратора, что делает систему простой в использовании и поддержке.
