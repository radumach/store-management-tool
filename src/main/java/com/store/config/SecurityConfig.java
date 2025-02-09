package com.store.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import com.store.security.JwtRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs (enable for web apps)
                .headers(headers -> headers
                        // XSS Protection header
                        .xssProtection(xss -> xss
                                .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                        // Content Security Policy (CSP)
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'"))
                        // HTTP Strict Transport Security (HSTS)
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .preload(true)
                                .maxAgeInSeconds(31536000) // 1 year
                        )
                        // Frame Options (prevent clickjacking)
                        .frameOptions(frame -> frame
                                .sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll() // Allow access to /login
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/products").hasRole("ADMIN") // Only ADMIN can access POST /api/products
                        .requestMatchers("/api/products/*/price").hasRole("ADMIN") // Only ADMIN can access PUT
                                                                                   // /api/products/{id}/price
                        .requestMatchers("/api/products/**").hasAnyRole("USER", "ADMIN") // USER and ADMIN can access
                                                                                         // other endpoints
                        .anyRequest().permitAll() // Allow all other requests
                )
                .httpBasic(Customizer.withDefaults()); // Use HTTP Basic authentication
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

//     @Bean
//     public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//         UserDetails user = User.builder()
//                 .username("user")
//                 .password(passwordEncoder.encode("password")) // Encode password
//                 .roles("USER")
//                 .build();

//         UserDetails admin = User.builder()
//                 .username("admin")
//                 .password(passwordEncoder.encode("admin")) // Encode password
//                 .roles("ADMIN")
//                 .build();

//         return new InMemoryUserDetailsManager(user, admin);
//     }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for password encoding
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}