package com.taskboard.api.config;

import com.taskboard.api.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Value("${security.csrf.enabled:false}")
    private boolean csrfEnabled;

    @Value("${security.h2-console.enabled:true}")
    private boolean h2ConsoleEnabled;

    @Value("${security.permit-all-paths:/api/auth/**,/api/test/public,/h2-console/**}")
    private String[] permitAllPaths;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain");
        log.info("Permit all paths: {}", String.join(", ", permitAllPaths));

        http
            .csrf(csrf -> {
                if (!csrfEnabled) {
                    csrf.disable();
                    log.info("CSRF disabled");
                }
            })
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(permitAllPaths).permitAll()
                .requestMatchers("/api/profile/**").authenticated()
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider(userDetailsService, passwordEncoder()))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Для H2 консоли
        if (h2ConsoleEnabled) {
            http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
        }

        log.info("Security filter chain configured successfully");
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }



    // UserDetailsService is now provided by CustomUserDetailsService in the user module
    // which is annotated with @Service and will be auto-detected

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

    // CORS конфигурация уже определена в CorsConfig
    // Используем существующий bean corsConfigurationSource
}
