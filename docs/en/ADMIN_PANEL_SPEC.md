> [Back to Documentation Contents](./README.md)

# Technical Specification for the Admin Panel

> For key terms and technologies definitions, please refer to the **[Glossary](./GLOSSARY.md)**.
>
> **[Detailed Next.js Frontend Implementation Description](../../frontends/nextjs/README.md)**

This document outlines the technical requirements for Phoebe CMS admin panel interface.

---

## 1. Roles and Permissions

### 1.1 Role-Based Access Control

Phoebe CMS uses a two-role system: **ADMIN** (full access) and **EDITOR** (own content only).

**Implementation Details**: See [Security & Roles Guide](./SECURITY_ROLES.md) for complete requirements.

### 1.2 UI Security
- **Role-based interface**: UI elements shown/hidden based on role and content ownership.
- **Real-time validation**: Backend prevents unauthorized operations.

---

## 2. Content Management

### 2.1 Content List Page

#### For ADMIN Users
- **Paginated list** of all content items.
- **Full action buttons** for each item:
  - **Create**: Navigate to creation form.
  - **Edit**: Navigate to editing form (any item).
  - **Delete**: Remove any item with confirmation.
  - **Publish/Unpublish**: Change status of any item.
- **Bulk operations**:
  - Checkboxes for selecting multiple articles.
  - "Select All" button to select all articles on the page.
  - "Delete Selected" button for bulk deletion.
  - "Unpublish Selected" button for bulk unpublishing.
- **Visual indicators**:
  - Colored status chips (Published/Draft).
  - Author column.
  - Success messages for operations.

#### For EDITOR Users
- **Paginated list** of all items (read-only view for others' content).
- **Limited action buttons** based on authorship:
  - **Create**: Navigate to creation form.
  - **Edit**: Only available for own articles.
  - **Delete**: Only available for own articles with confirmation.
  - **Publish/Unpublish**: Only available for own articles.

---

## 3. Taxonomy Terms

### 3.1 Terms Management Page (ADMIN only)
- **Paginated list** of taxonomy terms.
- **ID column** for term identification.
- **Add new terms** functionality.
- **Edit existing terms**.
- **Delete existing terms** with confirmation.
- **Vocabulary management** (e.g., categories, tags, page_type).
- **Colored chips** for visual role distinction.

---

## 4. Bulk Operations

### 4.1 Bulk Operations for ADMIN
- **Bulk delete/unpublish** selected content.
- **Checkboxes for selection**: Individual article selection.
- **Select All**: Select all articles on current page.
- **Bulk role management** for users.
- **System-wide operations** with confirmation dialogs.
- **Bulk operation filters**:
  - BY_IDS: Operations with selected IDs.
  - BY_TERM: Select content by term.
  - BY_AUTHOR: Select content by author.
  - ALL: Operations with all content.
- **Visual feedback**: Success messages for operations.

### 4.2 Bulk Operations for EDITOR (RESTRICTED)
- **NO BULK OPERATIONS** - EDITOR role is restricted to single item operations.
- **Delete**: Can only delete one item at a time (own content).
- **Publish**: Can only change status of one item at a time (own content).
- **Security**: Backend prevents EDITOR from accessing bulk operation endpoints.

---

## 5. Content Creation/Editing Page

### 5.1 Form Fields
- **Title**: Text input with validation (5-50 characters).
- **Teaser**: Textarea for preview (10-250 characters).
- **Body**: Textarea with HTML support (minimum 20 characters).
- **Term Selection**: Dropdown menu for term assignment.
- **Publication Status**: Published/Draft toggle.
- **Publication Date**: Auto-assigned on creation but can be modified. Date format should conform to ISO-8601 (e.g., `2024-01-15T10:30:00`).

### 5.2 Undo Feature
- **5-second delay** before actual save.
- **Snackbar notification** at bottom of screen with "Saving in 5 seconds..." text.
- **UNDO button**: Blue button to cancel save.
- **Form lock**: Create/Cancel buttons disabled during wait.
- **Auto-cleanup**: Timer cleared on component unmount.
- **Data preservation**: All fields remain filled on undo.

### 5.3 HTML Content Support
- **HTML tags allowed**: Teaser and Body fields support HTML.
- **Image embedding**: `<img src='...' />` allowed.
- **YouTube embedding**: `<iframe>` for YouTube videos allowed.
- **Backend validation**: Sanitization of dangerous tags and attributes.
- **UI hints**: Helper text informs about HTML support.

---

## 6. Security and Input Validation

### 6.1 Input Validation
- **Strict validation rules** for all input fields.
- **Prevention of hacking techniques**: SQL injection, XSS.
- **CSRF Protection**: Disabled on backend for API (standard practice for RESTful APIs). Frontend should be designed with this in mind.

### 6.2 HTML Content Security
- **Allowed tags**: `<img>`, `<iframe>`, `<a>`, `<p>`, `<br>`, `<strong>`, `<em>`, and other safe tags.
- **Content sanitization**: Removal of dangerous attributes (onclick, onerror, etc.).
- **Whitelist approach** for allowed HTML tags and attributes.
- **XSS protection**: Validation and sanitization on backend.

### 6.3 Authentication and Security
- **Current authentication**: Spring Security with Basic Auth.
- **Rate Limiting**: 5 login attempts per 5 minutes per IP.
- **Brute-force protection**: Bucket4j for request limiting.
- **HTTP 429**: Message "Too many login attempts. Please wait 5 minutes and try again."
- **Password management**: BCrypt hashing (strength 12), passwords stored in DB.
- **Password change**: Via user edit page (minimum 8 characters).
- **Admin access**: Controlled through application configuration (e.g., via Flyway migrations or manual creation), not user self-registration.
- **Planned migration**: OAuth 2.0 + 2FA for all roles (ADMIN, EDITOR).

---

## 7. AdminBar (Admin Navigation Panel)

### 7.1 Appearance
- **Color**: Black background (#000) for visual distinction.
- **Position**: Replaces regular header for authenticated users.
- **User information**:
  - Username display.
  - Colored role chip (🔴 red #cc0000 for ADMIN, 🔵 blue #1c355e for EDITOR).

### 7.2 Navigation (for all authenticated)
- **Home**: Return to main page.
- **Create News**: Create new article.
- **News List**: List of all articles.

### 7.3 Navigation (ADMIN only)
- **Taxonomy**: Manage categories and tags.
- **Users**: Manage users.
- **Settings**: Site settings.

### 7.4 Logout Button
- **Logout** with redirect to main page.

---

## 8. User Management (ADMIN only)

### 8.1 User List Page
- **List of all users** with pagination.
- **Information**: Username, Email, Active status, Roles.
- **Colored role chips**: Red for ADMIN, blue for EDITOR.
- **Create button**: Navigate to user creation form.
- **Action buttons**: Edit and Delete for each user.

### 8.2 User Creation/Editing
- **Form fields**:
  - Username (3-50 characters, required).
  - Email (email validation, required).
  - Password (minimum 8 characters, required on creation).
  - Roles (multiple selection: ADMIN, EDITOR).
  - Active status (toggle).
- **Password change**: On edit - leave empty to keep current.
- **Validation**: On frontend and backend.

## 9. Site Settings (ADMIN only)

### 9.1 Settings Page
- **Basic information**:
  - Site Title.
  - Site URL.
  - Logo URL.
- **SEO settings**:
  - Meta Description.
  - Meta Keywords.
- **Custom HTML**:
  - Header HTML (insert into `<head>`).
  - Footer HTML (insert before `</body>`).
- **Navigation menu**:
  - Main Menu Term IDs (list of term IDs for main menu).

## 10. Static Pages

### 10.1 Architecture
- **Database**: Technical pages stored as regular news items.
- **Vocabulary**: Special `page_type` vocabulary for identification.
- **Available pages**: About, Contact, Privacy, Terms, Advertising, Editorial Statute, Community Rules, Technical Sheet.
- **Dynamic routing**: `/page/[slug]` on frontend.
- **SSG + ISR**: Static generation with incremental regeneration (revalidate: 3600).

---

## 11. Technical Requirements

### 11.1 Pagination
- **All list pages** must include pagination.
- **Configurable page sizes** (10, 25, 50, 100 items per page) for UI selection. Backend supports page size up to 100 items.
- **Navigation controls** (Previous, Next, page numbers).

### 11.2 User Experience
- **Confirmation dialogs** for destructive actions (delete, bulk operations).
- **Success/error notifications** for all operations.
- **Snackbar notifications**: For Undo feature and other temporary messages.
- **Colored indicators**: Chips for statuses and roles.
- **AdminBar**: Black navigation panel with user information.
- **Responsive design** for tablets and desktops.
- **Keyboard navigation** support for accessibility.
