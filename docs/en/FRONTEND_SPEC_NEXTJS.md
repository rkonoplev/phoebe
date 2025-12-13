# Frontend Technical Specification (Next.js)

> **Current Implementation Details**: For a detailed overview of the implemented features,
> setup instructions, and further development plans, please refer to the
> **[Next.js Frontend README](../../frontends/nextjs/README.md)**.
>
> **Implementation Status**: 🚧 **IN ACTIVE DEVELOPMENT** - The frontend is currently under active development.

## 1. General Objectives
- Develop a **modern, responsive news portal frontend**.
- Follow **Google News–inspired design principles**: clean, spacious, mobile-first,
  highly readable.
- Provide a **SEO-friendly structure** with static URLs for all news articles.
- Ensure **accessibility (WCAG 2.1 AA)** and **fast loading performance**.

---

## 2. Technology Stack
- **Framework:** Next.js (React, supports SSR/SSG for SEO and performance).
- **UI Library:** Material UI (MUI).
- **Styling:** Custom Material theme (overrides for branding, typography, and colors).
- **Typography:** Roboto (base) + Roboto Slab / Serif for article headlines.

---

## 3. Layout & Pages
### 3.1 Homepage
- **Dynamic Layout**: In addition to the standard grid layout, the homepage supports two display modes:
  - **Simple Mode**: A clean grid layout of the latest news.
  - **Custom Mode**: A flexible block-based structure managed from the admin panel.
- Category navigation bar at the top.

### 3.2 Category Page
- Paginated list of articles belonging to the selected category.
- Category title highlighted with branding accent color.

### 3.3 Article Page
- Large headline with serif font.
- Featured image (responsive, up to ~800px wide).
- Publication date, author, and category.
- Full content with images and inline HTML elements.
- Related articles at the bottom.
- SEO metadata, OpenGraph, and Twitter card support.

### 3.4 Static Pages
- About page.
- Archive page (filter by date/category).

---

## 4. Functionality
### 4.1 SEO & URLs
- **Hybrid Rendering Strategy**: Utilize Next.js's rendering capabilities to maximize
  performance and SEO.
  - **Server-Side Rendering (SSR)**: For dynamic pages like the homepage and category
    listings, ensuring content is always fresh and immediately indexable.
  - **Static Site Generation (SSG)**: For individual article pages and static pages
    (`About`, `Contact`), providing the fastest possible load times.
  - **Incremental Static Regeneration (ISR)**: Optionally use ISR to rebuild static
    pages in the background at a set interval, combining the speed of static with the
    freshness of dynamic content.
- **Static URLs**: Static, human-readable URLs for articles (format `/news/{slug}-{id}`,
  e.g., `/news/article-title-15378`) and categories (`/category/{slug}-{id}`).
- **Structured Data**: Implement JSON-LD (`NewsArticle` schema) to provide rich
  metadata to search engines, enhancing search result appearance (rich snippets).
- **Meta Tags**: Dynamically generate `<title>`, `<meta name="description">`, and
  OpenGraph tags for each page to ensure optimal sharing on social media and correct
  indexing.

### 4.2 Responsive Design
- Mobile-first layout.
- Adaptive typography and fluid images.
- Recommended image sizes:
  - Top article: ~600–800px width.
  - Standard articles: ~320–400px width.
  - Thumbnails: ~160–200px width.

### 4.3 Accessibility
- WCAG 2.1 AA compliance.
- ARIA labels, proper contrast, keyboard navigation.

### 4.4 Theming & Branding
- **Base colors**:
  - Primary: Dark Blue (#1c355e).
  - Secondary: Deep Red (#cc0000).
  - Background: White (#ffffff) or Off-White (#fdfcf8).
  - Neutral gray accents for borders and dividers (#eeeeee).
- **Usage**:
  - Headlines and links: Dark Blue.
  - Category highlights (e.g., Analytics): Red.
  - Navigation bar: Dark Blue background with white text.
- **Typography**:
  - Headlines: Serif (Roboto Slab).
  - Body text: Sans-serif (Roboto).

---

## 5. Backend API Integration

### 5.1 API Configuration
- **Base URL**: `http://phoebe-app:8080/api` (for Docker) or `http://localhost:8080/api` (locally)
- **Public endpoints**: `/api/public/*` - accessible without authentication
- **Admin endpoints**: `/api/admin/*` - require Basic Auth
- **CORS**: Configured for localhost:3000, localhost:3001, and Docker container

### 5.2 Authentication
- **Method**: Basic Authentication with username and password
- **Token storage**: localStorage (base64 encoded credentials)
- **Verification endpoint**: `/api/admin/auth/me` - get current user data
- **Automatic error handling**: Redirect to login on 401/403

### 5.3 Request Structure

**Public requests** (no authentication):
```javascript
GET /api/public/news?page=0&size=10
GET /api/public/news/{id}
GET /api/public/news/term/{termId}
GET /api/public/news/search?q=query
GET /api/public/homepage
GET /api/public/homepage/mode
PATCH /api/public/homepage/mode
```

**Admin requests** (with Basic Auth header):
```javascript
GET /api/admin/auth/me
GET /api/admin/news?page=0&size=20
POST /api/admin/news
PUT /api/admin/news/{id}
DELETE /api/admin/news/{id}
GET /api/admin/terms
POST /api/admin/terms
GET /api/admin/homepage-blocks
POST /api/admin/homepage-blocks
PUT /api/admin/homepage-blocks/{id}
DELETE /api/admin/homepage-blocks/{id}
```

### 5.4 Error Handling
- **401 Unauthorized**: Automatic logout and redirect to `/admin/login`
- **403 Forbidden**: Automatic logout and redirect to `/admin/login`
- **Network errors**: Display error message to user

---

## 6. Implemented Features

### 6.1 Core Setup & Infrastructure
- **Project Setup**: Next.js with React, Material UI (MUI), and Axios.
- **Docker Integration**: Dockerfile for Next.js app and updated docker-compose.yml
  with nextjs-app service on port 3000.
- **Environment Variables**: Configuration for backend API base URL (.env.local).

### 6.2 User Interface & Theming
- **Custom Material UI Theme**: Light and dark mode support with toggle in footer.
- **Global Layout Component**: Consistent header, main content area, and footer.
- **Custom 404 Page**: For unhandled routes.

### 6.3 Authentication & Authorization

**System Login:**
- **Login URL**: `/admin/login` (http://localhost:3000/admin/login)
- **Default Credentials**: username: `admin`, password: `admin`
- **Validation**: Frontend validation (username: 3-50 characters, password: minimum 8 characters)

**Authentication Management:**
- **AuthContext**: Global management of authentication status and roles (ADMIN/EDITOR)
- **Session Storage**: localStorage with base64 encoded credentials
- **Automatic Verification**: Checks for token presence on page load
- **Automatic Logout**: On 401/403 errors with redirect to `/admin/login`

**Route Protection:**
- **ProtectedRoute**: Verifies authentication, redirects to `/admin/login` if not authorized
- **AdminRoute**: Additionally checks for ADMIN role for administrative functions
- **Loading Spinner**: Displayed during authentication status verification

**AdminBar (Black Admin Panel):**
- **Display**: Only for authenticated users (replaces regular header)
- **Color**: Black background (#000) for visual distinction from public interface
- **User Information**: Displays username and role (colored chip)
  - 🔴 Red chip (#cc0000) for ADMIN role
  - 🔵 Dark blue chip (#1c355e) for EDITOR role

**AdminBar Links (Available to All Authorized Users):**
- Home - return to homepage
- Create News - create new article (`/admin/news/new`)
- News List - list of all articles (`/admin/news`)

**AdminBar Links (ADMIN Only):**
- Taxonomy - manage categories and tags (`/admin/taxonomy`)
- Users - manage users and roles (`/admin/users`)
- Homepage - manage homepage blocks (`/admin/homepage-blocks`)

**Logout Button**: Exit system with redirect to homepage

### 6.4 Public Content Pages
- **Homepage** (`/`): An SSR page with two display modes: `Simple` (news list) and `Custom` (block-based structure).
- **Article Detail Page** (`/node/[id]`): SSG with ISR for optimal performance and SEO
- **Category Page** (`/category/[id]`): SSR list of articles for specific category
- **Search Page** (`/search`): Search form integrated with backend API
- **Technical Pages** (`/page/[slug]`): Dynamic pages from database (About, Contact, Privacy, etc.)
- **404 Page**: Custom page for non-existent routes

### 6.5 Admin Panel (CRUD Operations)
- **News Management**: List, create, edit, and delete articles.
- **Taxonomy Management**: Full CRUD for terms and categories.
- **User Management**: View and edit user roles (ADMIN only).
- **Frontend Validation**: Comprehensive form validation according to VALIDATION_GUIDE.md.
- **Universal Undo System**: All save forms support a 5-second window for canceling operations with return to editing.

### 6.6 Homepage Management
- **Block CRUD**: A full interface for creating, editing, and deleting homepage blocks is implemented at `/admin/homepage-blocks`.
- **Dynamic Form**: The block creation/editing form adapts to the selected block type (`News` or `Widget/Ad`), showing only relevant fields.
- **Mode Switcher**: A button in the site footer allows users to switch between `Simple` and `Custom` homepage display modes.
- **Dynamic Rendering**: The homepage fetches data from `/api/public/homepage` and renders content according to the active mode.

### 6.7 Content Handling
- **HTML Rendering**: Support for HTML content (allowed tags only) and YouTube embeds in teaser and body fields.
- **Safe Processing**: Using backend's SafeHtml processing.

---

## 7. Future Enhancements (Optional)
- Full-text search with auto-suggestions (e.g., using Pagefind).
- Lazy loading for images and infinite scroll on category pages.
- Push notifications for breaking news.
- OAuth 2.0 + JWT integration for enhanced authentication security.
- **File upload capabilities for images and other media**: This is directly related to secure
  media embedding. Implementing this functionality will require backend updates for secure
  handling and storage of uploaded files, as well as updates to HTML sanitization rules.

---

## Running the Next.js Frontend Locally

... (rest of the file remains unchanged)
