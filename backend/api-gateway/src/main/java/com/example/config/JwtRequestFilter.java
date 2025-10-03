package com.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import com.example.dto.ErrorResponse;
import com.example.feign.AuthServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@EnableFeignClients(basePackages = "com.example.feign")
@Component
@Slf4j
public class JwtRequestFilter implements WebFilter {

    private final AuthServiceClient authServiceClient;
    private final ObjectMapper objectMapper;

    private static final Map<String, String> ROLE_MAP = Map.of(
        "ADMIN", "ROLE_ADMIN",
        "FARMER", "ROLE_FARMER",
        "DEALER", "ROLE_DEALER"
    );

    @Autowired
    public JwtRequestFilter(AuthServiceClient authServiceClient, ObjectMapper objectMapper) {
        this.authServiceClient = authServiceClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.warn("In JWT Filter");
        System.out.println("************* Filter ****************");
        String requestTokenHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        System.out.println("Authorization header: " + requestTokenHeader);

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            String jwtToken = requestTokenHeader.substring(7);
            log.info("Extracted JWT token: {}", jwtToken.substring(0, Math.min(20, jwtToken.length())) + "...");

            // Validate the token and get the user details - FIX: Add subscribeOn for blocking call
            return Mono.fromCallable(() -> {
                log.info("Calling auth service to validate token");
                return authServiceClient.validateToken(jwtToken); // FIX: Don't add "Bearer " again
            })
            .subscribeOn(Schedulers.boundedElastic()) // FIX: Handle blocking call properly
            .flatMap(validationResponse -> {
                log.info("Token validation successful for user: {}", validationResponse.username());
                SimpleGrantedAuthority authority = mapRoleToAuthority(validationResponse.role());

                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        validationResponse.username(),
                        null,
                        Collections.singleton(authority)
                    );

                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
            })
            .onErrorResume(e -> {
                log.error("Token validation failed: {}", e.getMessage(), e);
                return createErrorResponse(exchange, "Unauthorized: Invalid or expired token", HttpStatus.UNAUTHORIZED);
            });
        }

        log.warn("No Bearer token found in Authorization header: {}", requestTokenHeader);
        return chain.filter(exchange);
    }

    private SimpleGrantedAuthority mapRoleToAuthority(String role) {
        String mappedRole = ROLE_MAP.getOrDefault(role, "ROLE_UNKNOWN");
        log.info("Mapping role {} to {}", role, mappedRole);
        return new SimpleGrantedAuthority(mappedRole);
    }

    private Mono<Void> createErrorResponse(ServerWebExchange exchange, String message, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(message, status.value());

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
        } catch (IOException e) {
            return Mono.error(e);
        }
    }
}