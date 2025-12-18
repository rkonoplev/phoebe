package com.example.phoebe.filter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import com.example.phoebe.config.RateLimitConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private RateLimitConfig rateLimitConfig;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Bucket bucket;

    @Mock
    private ConsumptionProbe consumptionProbe;

    private RateLimitFilter rateLimitFilter;

    @BeforeEach
    void setUp() {
        rateLimitFilter = new RateLimitFilter(rateLimitConfig);
    }

    @Test
    void doFilterInternalWhenTokensAvailableShouldAllowRequest() throws ServletException, IOException {
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(request.getRequestURI()).thenReturn("/api/public/news");
        when(rateLimitConfig.getPublicBucket("192.168.1.1")).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        when(consumptionProbe.getRemainingTokens()).thenReturn(99L);

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(response).addHeader("X-Rate-Limit-Remaining", "99");
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    }

    @Test
    void doFilterInternalWhenRateLimitExceededShouldRejectRequest() throws ServletException, IOException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(request.getRequestURI()).thenReturn("/api/public/news");
        when(rateLimitConfig.getPublicBucket("192.168.1.1")).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(false);
        when(response.getWriter()).thenReturn(printWriter);

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        verify(response).setContentType("application/json");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternalWhenAdminEndpointShouldUseAdminBucket() throws ServletException, IOException {
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(request.getRequestURI()).thenReturn("/api/admin/news");
        when(rateLimitConfig.getAdminBucket("192.168.1.1")).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        when(consumptionProbe.getRemainingTokens()).thenReturn(49L);

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(rateLimitConfig).getAdminBucket("192.168.1.1");
        verify(rateLimitConfig, never()).getPublicBucket(anyString());
    }

    @Test
    void doFilterInternalWhenPublicEndpointShouldUsePublicBucket() throws ServletException, IOException {
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(request.getRequestURI()).thenReturn("/api/public/news");
        when(rateLimitConfig.getPublicBucket("192.168.1.1")).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        when(consumptionProbe.getRemainingTokens()).thenReturn(99L);

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(rateLimitConfig).getPublicBucket("192.168.1.1");
        verify(rateLimitConfig, never()).getAdminBucket(anyString());
    }

    @Test
    void getClientIpAddressWhenXForwardedForPresentShouldReturnFirstIP() throws ServletException, IOException {
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.1, 192.168.1.1");
        when(request.getRequestURI()).thenReturn("/api/public/news");
        when(rateLimitConfig.getPublicBucket("203.0.113.1")).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        when(consumptionProbe.getRemainingTokens()).thenReturn(99L);

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(rateLimitConfig).getPublicBucket("203.0.113.1");
    }

    @Test
    void getClientIpAddressWhenXRealIPPresentShouldReturnXRealIP() throws ServletException, IOException {
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn("203.0.113.2");
        when(request.getRequestURI()).thenReturn("/api/public/news");
        when(rateLimitConfig.getPublicBucket("203.0.113.2")).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        when(consumptionProbe.getRemainingTokens()).thenReturn(99L);

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(rateLimitConfig).getPublicBucket("203.0.113.2");
    }

    @Test
    void getClientIpAddressWhenNoProxyHeadersShouldReturnRemoteAddr() throws ServletException, IOException {
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");
        when(request.getRequestURI()).thenReturn("/api/public/news");
        when(rateLimitConfig.getPublicBucket("192.168.1.100")).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        when(consumptionProbe.getRemainingTokens()).thenReturn(99L);

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(rateLimitConfig).getPublicBucket("192.168.1.100");
    }
}