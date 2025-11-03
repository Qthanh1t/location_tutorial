package com.ord.core.logging.audit_log.filter;
import com.ord.core.logging.audit_log.AuditLogDto;
import com.ord.core.logging.audit_log.service.AuditLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class AuditLogFilter extends OncePerRequestFilter {

    private final AuditLogService auditLogService;

    // Giới hạn kích thước payload để tránh OutOfMemoryError
    private static final int MAX_PAYLOAD_LENGTH = 2048;

    // Danh sách các đường dẫn không cần audit log (ví dụ: file tĩnh, swagger)
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator",
            ".js",
            ".css",
            ".ico",
            ".png"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Bỏ qua nếu là đường dẫn không cần log
        if (shouldExclude(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Bọc request và response để có thể đọc body
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        String requestBody = "";
        String responseBody = "";

        try {
            // Cho request đi tiếp
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            // 3. Thu thập thông tin sau khi request đã hoàn thành
            long durationMs = System.currentTimeMillis() - startTime;
            requestBody = getRequestBody(requestWrapper);
            responseBody = getResponseBody(responseWrapper);

            // 4. Xây dựng DTO
            AuditLogDto auditLog = AuditLogDto.builder()
                    .method(request.getMethod())
                    .uri(request.getRequestURI())
                    .clientIp(getClientIp(request))
                    .username(getUsername())
                    .status(responseWrapper.getStatus())
                    .durationMs(durationMs)
                    .requestBody(requestBody)
                    .responseBody(responseBody)
                    .timestamp(Instant.now())
                    .build();

            // 5. Gửi log đi (bất đồng bộ)
            auditLogService.send(auditLog);

            // 6. Sao chép response body lại cho client
            responseWrapper.copyBodyToResponse();
        }
    }

    private boolean shouldExclude(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return EXCLUDE_PATHS.stream().anyMatch(uri::contains);
    }

    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return "anonymous";
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        // Lấy IP đầu tiên nếu có nhiều IP (do proxy)
        return xfHeader.split(",")[0].trim();
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length == 0) {
            return "";
        }
        return payloadToString(buf, request.getCharacterEncoding());
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf.length == 0) {
            return "";
        }
        return payloadToString(buf, response.getCharacterEncoding());
    }

    private String payloadToString(byte[] buf, String characterEncoding) {
        try {
            String payload = new String(buf, characterEncoding);
            // Cắt bớt payload nếu quá dài
            if (payload.length() > MAX_PAYLOAD_LENGTH) {
                return payload.substring(0, MAX_PAYLOAD_LENGTH) + "...(truncated)";
            }
            return payload;
        } catch (UnsupportedEncodingException e) {
            log.warn("Không thể đọc payload: {}", e.getMessage());
            return "[payload_unreadable]";
        }
    }
}