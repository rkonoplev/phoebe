# Static/Technical Pages Architecture

> [Back to Documentation](./README.md)

## Overview

Technical pages (About Us, Contact, Privacy Policy, etc.) are stored in the database and use the existing News/Content system instead of static files on the frontend.

## Architectural Decision

### Why Use the Existing News System?

**Advantages:**
- ✅ No need to create a separate Page entity
- ✅ Uses existing infrastructure (CRUD, validation, security)
- ✅ Unified content management through admin panel
- ✅ Supports versioning and auditing
- ✅ Flexibility in management through admin interface

### How Does It Work?

Technical pages are distinguished from news articles by a special **vocabulary "page_type"**:

```
Table: terms
+----+-------------------+-------------+
| id | name              | vocabulary  |
+----+-------------------+-------------+
| 1  | about             | page_type   |
| 2  | contact           | page_type   |
| 3  | privacy           | page_type   |
| 4  | terms_of_service  | page_type   |
| 5  | advertising       | page_type   |
+----+-------------------+-------------+

Table: content (News)
+----+------------------+--------+------------+
| id | title            | body   | published  |
+----+------------------+--------+------------+
| 10 | About Us         | ...    | 1          |
| 11 | Contact Us       | ...    | 1          |
+----+------------------+--------+------------+

Table: content_terms (relationship)
+------------+---------+
| content_id | term_id |
+------------+---------+
| 10         | 1       | (About Us -> about)
| 11         | 2       | (Contact Us -> contact)
+------------+---------+
```

## Database

### Migration

File: `backend/src/main/resources/db/migration/common/V11__add_static_pages.sql`

The migration creates:
1. **Terms for vocabulary "page_type"**: about, contact, privacy, terms_of_service, advertising, editorial_statute, community_rules, technical_sheet
2. **Records in content table**: Technical pages with initial content
3. **content_terms relationships**: Links pages to corresponding page_type terms

### Running the Migration

The migration runs automatically when starting the application via Flyway:

```bash
# Start backend (migration will run automatically)
cd /Users/rk/dev/java/phoebe
make run
```

## Backend API

### Existing Endpoints

Technical pages are accessible through standard News API endpoints:

**Public (no authentication):**
```
GET /api/public/news/{id}              - Get page by ID
GET /api/public/news/term/{termId}     - Get all pages by page_type
```

**Admin (with Basic Auth):**
```
GET    /api/admin/news                 - List all pages
POST   /api/admin/news                 - Create new page
PUT    /api/admin/news/{id}            - Update page
DELETE /api/admin/news/{id}            - Delete page
```

### Filtering Technical Pages

To get only technical pages, use vocabulary filter:

```javascript
// Get ID of "about" term
GET /api/public/terms?vocabulary=page_type

// Get "About Us" page by term_id
GET /api/public/news/term/{about_term_id}
```

## Frontend

### Page Structure

**Removed static placeholders:**
- ❌ `pages/contact.js`
- ❌ `pages/advertising.js`
- ❌ `pages/community-rules.js`
- ❌ `pages/editorial-statute.js`
- ❌ `pages/privacy-settings.js`
- ❌ `pages/technical-sheet.js`

**Using dynamic page:**
- ✅ `pages/page/[slug].js` - Universal page for all technical pages

### Routing

```
URL                          -> Vocabulary Term -> Content
/page/about                  -> page_type:about  -> "About Us"
/page/contact                -> page_type:contact -> "Contact Us"
/page/privacy                -> page_type:privacy -> "Privacy Policy"
/page/terms-of-service       -> page_type:terms_of_service -> "Terms of Service"
/page/advertising            -> page_type:advertising -> "Advertise With Us"
```

### Layout.js Component

Footer dynamically generates links to technical pages:

```jsx
<FooterLink href="/page/about">About Us</FooterLink>
<FooterLink href="/page/terms-of-service">Terms of Service</FooterLink>
<FooterLink href="/page/privacy">Privacy Policy</FooterLink>
<FooterLink href="/page/advertising">Advertise</FooterLink>
<FooterLink href="/page/contact">Contact</FooterLink>
```

## Management via Admin Panel

### Creating a New Technical Page

1. Login to admin: `/admin/login`
2. Go to "News Management": `/admin/news`
3. Create new entry: `/admin/news/new`
4. Fill in fields:
   - **Title**: Page name (e.g., "FAQ")
   - **Body**: Page content (HTML)
   - **Teaser**: Brief description
   - **Published**: Set to true
   - **Terms**: Select corresponding term from vocabulary "page_type"

### Editing an Existing Page

1. Go to list: `/admin/news`
2. Find the page (filter by page_type)
3. Click "Edit"
4. Make changes and save

## Approach Benefits

### For Developers
- Single codebase for all content
- No need to duplicate CRUD logic
- Reuse existing components
- Easy maintenance

### For Administrators
- Manage all content in one place
- Unified editing interface
- Change history (via auditing)
- Flexibility in creating new pages

### For SEO
- Dynamic meta tags
- Ability to use SSG/ISR
- Control over URL structure
- Structured data

## Future Improvements

### Site Settings

Planned separate table for storing footer settings:

```sql
CREATE TABLE site_settings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  setting_key VARCHAR(100) UNIQUE NOT NULL,
  setting_value TEXT,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Example: storing footer links
INSERT INTO site_settings (setting_key, setting_value) VALUES
  ('footer_links', '[
    {"label": "About Us", "slug": "about"},
    {"label": "Contact", "slug": "contact"},
    {"label": "Privacy", "slug": "privacy"}
  ]');
```

This will allow:
- Managing footer link order
- Adding/removing links without code changes
- Customizing displayed labels

### Slug-based URLs

Instead of `/page/about`, use a more flexible system:
- Add `slug` field to content table
- Use `/page/{slug}` instead of vocabulary term
- Support custom URLs for each page

## Usage Examples

### Getting "About Us" Page

**Backend (Java):**
```java
// Get page_type:about term
Term aboutTerm = termRepository.findByNameAndVocabulary("about", "page_type");

// Get page with this term
List<News> pages = newsRepository.findByTermsContaining(aboutTerm);
News aboutPage = pages.get(0);
```

**Frontend (Next.js):**
```javascript
// pages/page/[slug].js
export async function getStaticProps({ params }) {
  const { slug } = params;
  
  // Get term by slug
  const term = await api.get(`/api/public/terms?vocabulary=page_type&name=${slug}`);
  
  // Get page by term_id
  const page = await api.get(`/api/public/news/term/${term.id}`);
  
  return { props: { page } };
}
```

## Conclusion

Using the existing News system for technical pages is a pragmatic solution that:
- Simplifies architecture
- Speeds up development
- Provides flexibility
- Maintains system consistency

All technical pages are managed through a unified admin interface, making the system simple to use and maintain.
