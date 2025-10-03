package com.example.config;

import com.example.ApiGatewayApplication;
import com.example.dto.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @SuppressWarnings("unused")
	private final ApiGatewayApplication apiGatewayApplication;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;
    private String dealer="DEALER";
    private String admin="ADMIN";
    private String farmer="FARMER";

    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtRequestFilter jwtRequestFilter, ApiGatewayApplication apiGatewayApplication) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtRequestFilter = jwtRequestFilter;
        this.apiGatewayApplication = apiGatewayApplication;
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
        return http
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                		.pathMatchers("/api/auth/**").permitAll()
                        .pathMatchers("/api/admin/**").hasRole(admin)
                        .pathMatchers("/api/farmer/**").hasRole(farmer)
                        .pathMatchers("/api/dealer/**").hasRole(dealer)
                        .pathMatchers("/api/payment/**").hasRole(dealer)
                        .pathMatchers("/api/orders/**").hasAnyRole(dealer, farmer,admin)
                        .pathMatchers("/api/chat/**").hasAnyRole(dealer, farmer)
                        .pathMatchers("/api/cart/**").hasRole(dealer)
                        .pathMatchers("api/otp/**").hasAnyRole(dealer,farmer)
                        .anyExchange()
                        .authenticated()
                ).exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .addFilterBefore(jwtRequestFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .securityContextRepository(webSessionSecurityContextRepository())
               .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource())) 
                .build();
    }

    @Bean
    public WebSessionServerSecurityContextRepository webSessionSecurityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
    }
    
    
   @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:3000");  // React frontend URL
        config.addAllowedMethod("*");  // Allow all HTTP methods
        config.addAllowedHeader("*");  // Allow all headers
        config.setAllowCredentials(true);  // Allow credentials (cookies, authorization headers, etc.)
        config.setMaxAge(3600L);  // Pre-flight cache time (in seconds)
 
        // UrlBasedCorsConfigurationSource should be used here
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // Apply CORS to all routes
 
        return source;  // Returning UrlBasedCorsConfigurationSource as CorsConfigurationSource
    }

}