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
            "/api/auth/*",
            "/api/owner/payments",
            "/api/subscription/plans"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (shouldSkip(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String encryptedRestaurantId = request.getHeader("X-Restaurant-Id");
        if (encryptedRestaurantId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);
            Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);

            if (restaurant == null) {
                filterChain.doFilter(request, response);
                return;
            }

            boolean handled = handleAccess(restaurant, response, method);
            if (handled) {
                return; // response already set (blocked/read-only)
            }
        } catch (Exception e) {
            // If ID decryption or DB lookup fails, ignore here and let Controller handle the Bad Request
            log.warn("Failed to process subscription check for header: {}", encryptedRestaurantId);
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldSkip(String path) {
        return path.startsWith("/api/public") || path.startsWith("/api/auth") || isWhitelisted(path);
    }

    private boolean handleAccess(Restaurant restaurant, HttpServletResponse response, String method) throws IOException {
        AccessLevelStatus accessLevel = restaurant.getAccessLevel();

        if (AccessLevelStatus.BLOCKED.equals(accessLevel)) {
            writeBlockedResponse(response, restaurant);
            return true;
        }

        if (AccessLevelStatus.READ_ONLY.equals(accessLevel) && isWriteMethod(method)) {
            writeReadOnlyResponse(response);
            return true;
        }

        if (AccessLevelStatus.PARTIAL.equals(accessLevel)) {
            response.setHeader("X-Subscription-Warning", "Subscription in Grace Period");
        }

        return false;
    }

    private void writeBlockedResponse(HttpServletResponse response, Restaurant restaurant) throws IOException {
        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE); // 503
        response.setContentType("application/json");
        String message = restaurant.getCustomerPageMessage() != null ? restaurant.getCustomerPageMessage() : "Contact Support";
        response.getWriter().write("{\"error\": \"Service Suspended\", \"message\": \"" + message + "\"}");
    }

    private void writeReadOnlyResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Read-Only Mode\", \"message\": \"Subscription expired. Renew to perform this action.\"}");
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
