# API Usage Guide

This guide provides practical examples for testing Phoebe CMS API endpoints
with `curl` and Postman.

## Table of Contents
- [Working with Postman](#working-with-postman)
- [Rate Limiting](#rate-limiting)
- [Public API Endpoints (No Authentication Required)](#public-api-endpoints-no-authentication-required)
- [Admin API Endpoints (Authentication Required)](#admin-api-endpoints-authentication-required)
- [Pagination Parameters](#pagination-parameters)
- [Rate Limiting Testing](#rate-limiting-testing)
- [HTTP Status Codes](#http-status-codes)
- [Example Validation Error Response](#example-validation-error-response)
- [Notes](#notes)
- [Alternative API Documentation: Swagger UI](#alternative-api-documentation-swagger-ui)

---

## Working with Postman

While all examples use `curl` for universal terminal access, using a tool like [Postman](https://www.postman.com/)
is highly recommended for a better testing experience.

**How to Import a `curl` Command into Postman:**
1.  Open Postman and click the `Import` button.
2.  Select the `Raw text` tab.
3.  Copy and paste any `curl` command from this guide.
4.  Postman will automatically create a new, ready-to-use request.

---

## Rate Limiting

All API endpoints are rate-limited by IP address:
- **Public API** (`/api/public/**`): 100 requests per minute
- **Admin API** (`/api/admin/**`): 50 requests per minute

Responses include the `X-Rate-Limit-Remaining` header. If the limit is exceeded, the API will return
an `HTTP 429 Too Many Requests` error with a JSON body:
```json
{"error":"Rate limit exceeded","retryAfter":60}
```

---

## Public API Endpoints (No Authentication Required)

### 1. Get All Published News (with Pagination)
```bash
curl -i "http://localhost:8080/api/public/news?page=0&size=10&sort=publicationDate,desc"
```

### 2. Get Published News by ID
```bash
curl -i "http://localhost:8080/api/public/news/1"
```

### 3. Get Published News by Term ID (Category/Tag)
```bash
curl -i "http://localhost:8080/api/public/news/term/5?page=0&size=15"
```

### 4. Get Published News by Multiple Term IDs
```bash
curl -i "http://localhost:8080/api/public/news/terms?termIds=1,3,5&page=0&size=20"
```

### 5. Check Rate Limiting Headers
```bash
curl -i "http://localhost:8080/api/public/news" | grep "X-Rate-Limit"
```

---

## Admin API Endpoints (Authentication Required)

### Authentication
Access to admin endpoints requires authentication. Examples use basic HTTP authentication (`-u admin:password`). In production, it is recommended to use more robust authentication mechanisms such as OAuth 2.0 or JWT, as described in the [Authentication Guide](./AUTHENTICATION_GUIDE.md).

### 1. Get All News (Published + Unpublished)
```bash
curl -u admin:password -i "http://localhost:8080/api/admin/news?page=0&size=10"
```

### 2. Create News Item

- **Endpoint**: `POST /api/admin/news`
- **Description**: Creates a new news article.

**Request Body Fields:**
| Field             | Type      | Description                                       | Required |
|-------------------|-----------|---------------------------------------------------|----------|
| `title`           | `String`  | The title of the article. Max 255 chars.          | Yes      |
| `body`            | `String`  | The full content of the article.                  | No       |
| `teaser`          | `String`  | A short summary for list views.                   | No       |
| `published`       | `boolean` | `true` to publish immediately, `false` for draft. | Yes      |
| `publicationDate` | `String`  | ISO-8601 date-time (e.g., `2024-01-15T10:30:00`). | No       |
| `termIds`         | `Array`   | An array of integer IDs for associated terms.     | No       |

**Example Request:**
```bash
curl -u admin:password \
  -H "Content-Type: application/json" \
  -X POST http://localhost:8080/api/admin/news \
  -d '{
    "title": "Breaking News Title",
    "body": "Full article content here...",
    "teaser": "Short summary for preview",
    "published": true,
    "publicationDate": "2024-01-15T10:30:00",
    "termIds": [5]
  }'
```

### 3. Update News Item

- **Endpoint**: `PUT /api/admin/news/{id}`
- **Description**: Updates an existing news article. All fields are optional.

**Example Request:**
```bash
curl -u admin:password \
  -H "Content-Type: application/json" \
  -X PUT http://localhost:8080/api/admin/news/1 \
  -d '{
    "title": "Updated News Title",
    "body": "Updated content...",
    "published": false
  }'
```

### 4. Delete News Item
```bash
curl -u admin:password -X DELETE "http://localhost:8080/api/admin/news/1"
```

---

## Channel Settings API

### 1. Get Channel Settings (Public)
```bash
curl -i "http://localhost:8080/api/public/channel-settings"
```

### 2. Get Channel Settings (Admin)
```bash
curl -u admin:password -i "http://localhost:8080/api/admin/channel-settings"
```

### 3. Update Channel Settings (Admin Only)

- **Endpoint**: `PUT /api/admin/channel-settings`
- **Description**: Updates site-wide configuration settings.

**Request Body Fields:**
| Field              | Type     | Description                                    | Required |
|--------------------|----------|------------------------------------------------|----------|
| `siteTitle`        | `String` | Site title for browser tab. Max 255 chars.   | No       |
| `metaDescription`  | `String` | Meta description tag. Max 500 chars.          | No       |
| `metaKeywords`     | `String` | Meta keywords tag. Max 500 chars.             | No              |
| `headerHtml`       | `String` | HTML code for site header (SafeHtml validated)| No       |
| `logoUrl`          | `String` | URL to site logo. Max 500 chars.              | No       |
| `footerHtml`       | `String` | HTML code for site footer (SafeHtml validated)| No       |
| `mainMenuTermIds`  | `String` | String representation of a JSON array of term IDs for the main menu (e.g., `"[1,2,3]"`) | No       |
| `siteUrl`          | `String` | Base URL for the site. Max 255 chars.         | No       |

**Example Request:**
```bash
curl -u admin:password \
  -H "Content-Type: application/json" \
  -X PUT http://localhost:8080/api/admin/channel-settings \
  -d '{
    "siteTitle": "My News Site",
    "metaDescription": "Latest news and updates",
    "metaKeywords": "news, updates, breaking",
    "logoUrl": "/assets/logo.png",
    "mainMenuTermIds": "[1,2,3]",
    "siteUrl": "https://dniester.ru"
  }'
```

**Security Restrictions for HTML Fields:**

⚠️ **HTML Content Limitations:**
- **No JavaScript**: Script tags and JavaScript code are blocked
- **No iframes**: Cannot embed external content or videos
- **No links**: Anchor tags (`<a>`) are not allowed
- **No images**: Image tags (`<img>`) are not permitted
- **Allowed tags only**: `b`, `i`, `u`, `strong`, `em`, `p`, `br`

These restrictions prevent XSS attacks and ensure content security.

**Backend Validation Implementation:**

✅ **Server-Side Validation:**
- `@ValidUrl` annotation validates HTTP/HTTPS URLs for `siteUrl` field
- `@ValidJsonArray` annotation validates JSON array format for `mainMenuTermIds` field
- `@SafeHtml` annotation restricts HTML to safe tags only
- All validations return clear error messages on failure

**Frontend Implementation Recommendations:**

✅ **For Taxonomy Panel (mainMenuTermIds field):**
- Use the `mainMenuTermIds` field to store JSON array of term IDs
- HTML formatting is limited to safe tags only
- Frontend should provide additional input validation

✅ **Frontend Validation Guidelines:**
- Add client-side URL validation for the `siteUrl` field
- Warn users about HTML restrictions before they enter content
- Show HTML preview with safe tags before saving
- Validate JSON format for `mainMenuTermIds` field
- Provide user-friendly error messages for validation failures

---

## Pagination Parameters

All list endpoints support pagination:
- `page`: Page number (0-based, default: 0)
- `size`: Items per page (default: 10, max: 100)
- `sort`: Sort field and direction (e.g., `publicationDate,desc`)

### Example with Pagination:
```bash
curl -i "http://localhost:8080/api/public/news?page=1&size=5&sort=title,asc"
```

### Response Format:
```json
{
  "content": [
    {
      "id": 1,
      "title": "Sample News Title",
      "teaser": "Brief summary...",
      "publicationDate": "2024-01-15T10:30:00"
    }
  ],
  "totalElements": 25,
  "totalPages": 3,
  "size": 10,
  "number": 0,
  "first": true,
  "last": false,
  "numberOfElements": 10,
  "empty": false
}
```

---

## Rate Limiting Testing

### Test Public API Rate Limit (100/min):
```bash
for i in {1..105}; do 
  curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/public/news
done
```

### Test Admin API Rate Limit (50/min):
```bash
for i in {1..55}; do 
  curl -s -o /dev/null -w "%{http_code}\n" -u admin:password http://localhost:8080/api/admin/news
done
```
*Expected: First requests return `200`, then `429` after limit exceeded.*

---

## HTTP Status Codes

Below are the standard HTTP status codes that the API may return:

| Code | Status                 | Description                                                              |
|-----|------------------------|--------------------------------------------------------------------------|
| `200` | `OK`                   | The request was successfully processed.                                  |
| `201` | `Created`              | A resource was successfully created (e.g., `POST /api/admin/news`).      |
| `204` | `No Content`           | The request was successfully processed, but there is no content to return (e.g., `DELETE`). |
| `400` | `Bad Request`          | The request was malformed, e.g., due to validation errors.               |
| `401` | `Unauthorized`         | Authentication is required.                                              |
| `403` | `Forbidden`            | Authentication was successful, but the user does not have access rights. |
| `404` | `Not Found`            | The resource was not found.                                              |
| `429` | `Too Many Requests`    | The rate limit has been exceeded.                                        |
| `500` | `Internal Server Error`| An internal server error occurred.                                       |

---

## Example Validation Error Response

When validation errors occur (e.g., due to invalid data in a `POST` or `PUT` request body), the API returns a standardized JSON response:

```json
{
  "timestamp": "2024-01-15T10:30:00.123+00:00",
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_ERROR",
  "message": "Input validation error",
  "details": [
    {
      "field": "title",
      "message": "must not be blank"
    },
    {
      "field": "publicationDate",
      "message": "must be a valid ISO-8601 format"
    }
  ],
  "path": "/api/admin/news"
}
```

---

## Notes
- All examples assume local development: `http://localhost:8080`
- Replace `admin:password` with your configured credentials.
- Rate limits are per IP address and reset every minute.
- Use the `-i` flag to see response headers, including rate limit info.
- Never use real production passwords in scripts or documentation.

---

## Alternative API Documentation: Swagger UI

In addition to this guide, you can access live API documentation and test endpoints in your browser:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Swagger UI is useful for exploring all available endpoints, viewing schemas, and making test requests
directly in the browser.
