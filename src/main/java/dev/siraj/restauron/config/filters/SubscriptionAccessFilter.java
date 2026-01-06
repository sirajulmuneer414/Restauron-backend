package dev.siraj.restauron.config.filters;

import dev.siraj.restauron.entity.enums.AccessLevelStatus;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.repository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class SubscriptionAccessFilter extends OncePerRequestFilter {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private IdEncryptionService idEncryptionService;

    // Paths that should always be allowed (e.g., Subscription Payment/Renewal endpoints)
    private static final List<String> WHITELISTED_PATHS = Arrays.asList(
            "/api/owner/payments",
            "/api/subscription/plans"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // 1. Skip checks for Public APIs, Auth, or Whitelisted Payment paths
        // (Public customer page blocking is better handled in the specific controller to avoid ID parsing issues here)
        if (path.startsWith("/api/public") || path.startsWith("/api/auth") || isWhitelisted(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Check for X-Restaurant-Id Header (Standard for Dashboard requests)
        String encryptedRestaurantId = request.getHeader("X-Restaurant-Id");

        if (encryptedRestaurantId != null) {
            try {
                Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);
                Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);

                if (restaurant != null) {
                    AccessLevelStatus accessLevel = restaurant.getAccessLevel();

                    // --- ENFORCEMENT LOGIC ---

                    // CASE 1: BLOCKED (Day 12+)
                    // Complete lockout except for payment routes (handled in whitelist above)
                    if (AccessLevelStatus.BLOCKED.equals(accessLevel)) {
                        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE); // 503
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Service Suspended\", \"message\": \"" +
                                (restaurant.getCustomerPageMessage() != null ? restaurant.getCustomerPageMessage() : "Contact Support") + "\"}");
                        return; // Stop request
                    }

                    // CASE 2: READ_ONLY (Day 2 - 11)
                    // Block state-changing methods (POST, PUT, DELETE, PATCH)
                    // Allow GET and OPTIONS
                    if (AccessLevelStatus.READ_ONLY.equals(accessLevel)) {
                        if (isWriteMethod(method)) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Read-Only Mode\", \"message\": \"Subscription expired. Renew to perform this action.\"}");
                            return; // Stop request
                        }
                    }

                    // CASE 3: PARTIAL / GRACE_PERIOD (Day 1)
                    // Usually allows full access, but you can add a warning header if needed
                    if (AccessLevelStatus.PARTIAL.equals(accessLevel)) {
                        response.setHeader("X-Subscription-Warning", "Subscription in Grace Period");
                    }
                }
            } catch (Exception e) {
                // If ID decryption fails, we ignore it here and let the Controller handle the Bad Request
                log.warn("Failed to process subscription check for header: {}", encryptedRestaurantId);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isWhitelisted(String path) {
        return WHITELISTED_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isWriteMethod(String method) {
        return "POST".equalsIgnoreCase(method) ||
                "PUT".equalsIgnoreCase(method) ||
                "DELETE".equalsIgnoreCase(method) ||
                "PATCH".equalsIgnoreCase(method);
    }
}