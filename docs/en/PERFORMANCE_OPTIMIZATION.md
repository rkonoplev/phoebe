# API Performance Optimization Guide

## Overview

This guide documents performance optimizations implemented in Phoebe CMS and provides recommendations for high-traffic scenarios.

## Implemented Optimizations

### 1. N+1 Query Prevention

**Problem**: Loading a list of News entities triggered separate queries for each `author` and `terms` relationship.

**Solution**: Added `@EntityGraph` annotations to NewsRepository methods:

```java
@EntityGraph(attributePaths = {"author", "terms"})
Page<News> findByPublished(boolean published, Pageable pageable);
```

**Impact**: Reduces queries from `1 + N + N` to `1` for listing operations.

### 2. SQL Logging for Diagnostics

**Configuration** (`application-local.yml`):
```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true

logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

**Usage**: Monitor console output to identify:
- N+1 query patterns
- Missing indexes
- Inefficient joins

### 3. Batch Operations

**Configuration**:
```yaml
hibernate:
  jdbc:
    batch_size: 20
  order_inserts: true
  order_updates: true
```

**Impact**: Groups multiple INSERT/UPDATE operations into batches, reducing database round-trips.

### 4. Database Indexes

**Existing indexes** (News entity):
- `idx_news_published` - for filtering by publication status
- `idx_news_publication_date` - for date-based sorting
- `idx_news_published_pubdate` - composite index for common queries
- `idx_news_author` - for author-based filtering

### 5. Lazy Loading Strategy

**Implementation**:
- `body` and `teaser` fields use `@Basic(fetch = FetchType.LAZY)`
- All relationships (`author`, `terms`) use `FetchType.LAZY`
- EntityGraph explicitly loads required associations

**Benefit**: Reduces initial query size, loads data only when needed.

## Performance Testing Recommendations

### 1. Load Testing

Use tools like Apache JMeter or Gatling to simulate high traffic:

```bash
# Example: 100 concurrent users, 1000 requests
ab -n 1000 -c 100 http://localhost:8080/api/public/news
```

### 2. Query Analysis

Enable SQL logging and check for:
- Multiple SELECT queries for the same entity type (N+1)
- Full table scans (missing indexes)
- Cartesian products in joins

### 3. Database Profiling

**MySQL**:
```sql
-- Enable slow query log
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;

-- Analyze query performance
EXPLAIN SELECT * FROM content WHERE published = true;
```

## Optimization Checklist

- [x] EntityGraph for eager loading of associations
- [x] SQL logging enabled in local profile
- [x] Batch operations configured
- [x] Database indexes on frequently queried columns
- [x] Lazy loading for large text fields
- [ ] Connection pool tuning for production
- [ ] Redis caching for frequently accessed data
- [ ] CDN for static content
- [ ] Database query result caching

## Future Optimizations

### 1. Caching Layer

Implement Redis caching for:
- Published news listings
- Term/category data
- User authentication tokens

### 2. Read Replicas

For high-traffic scenarios:
- Configure read replicas for SELECT queries
- Route write operations to primary database
- Use Spring's `@Transactional(readOnly = true)` to leverage replicas

### 3. Database Partitioning

For large datasets:
- Partition `content` table by publication date
- Archive old news to separate tables

### 4. API Response Compression

Enable GZIP compression in Spring Boot:
```yaml
server:
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/xml,text/plain
```

## Monitoring

### Key Metrics to Track

1. **Response Time**: Average API response time (target: <200ms)
2. **Throughput**: Requests per second
3. **Database Connection Pool**: Active/idle connections
4. **Query Execution Time**: Slow query log analysis
5. **Cache Hit Rate**: If caching is implemented

### Tools

- **Spring Boot Actuator**: `/actuator/metrics`
- **Prometheus + Grafana**: Time-series metrics
- **MySQL Performance Schema**: Query analysis
- **New Relic / DataDog**: APM solutions

## Best Practices

1. **Always use pagination** for list endpoints
2. **Limit result set size** (max 100 items per page)
3. **Use EntityGraph** instead of JOIN FETCH in queries
4. **Profile before optimizing** - measure actual bottlenecks
5. **Test with production-like data volumes**

## References

- [Hibernate Performance Tuning](https://docs.jboss.org/hibernate/orm/6.0/userguide/html_single/Hibernate_User_Guide.html#performance)
- [Spring Data JPA Best Practices](https://spring.io/guides/gs/accessing-data-jpa/)
- [MySQL Query Optimization](https://dev.mysql.com/doc/refman/8.0/en/optimization.html)
