package com.booking.gateway_auth_starter.filter;

import com.booking.gateway_auth_starter.config.GatewayAuthProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class GatewayAuthFilter extends OncePerRequestFilter {

    private final GatewayAuthProperties properties;

    public GatewayAuthFilter(GatewayAuthProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String userEmail = request.getHeader("X-User-Email");
        String userRole = request.getHeader("X-User-Role");
        String signature = request.getHeader("X-Gateway-Signature");

        if (userEmail == null || userEmail.isBlank()
                || userRole == null || userRole.isBlank()
                || signature == null || signature.isBlank()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access denied: request must go through gateway");
            return;
        }

        // Проверяем подпись
        String expectedSignature = computeSignature(userEmail, userRole, properties.getSecret());
        if (!expectedSignature.equals(signature)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access denied: invalid gateway signature");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String computeSignature(String email, String role, String secret) {
        try {
            String data = email + ":" + role;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute signature", e);
        }
    }
}