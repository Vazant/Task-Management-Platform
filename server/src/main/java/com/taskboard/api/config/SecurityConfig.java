package com.taskboard.api.config;

import com.taskboard.api.service.JwtService;
import com.taskboard.api.service.SecurityMetricsService;
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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
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

    @Autowired
    private DpopAuthenticationFilter dpopAuthenticationFilter;

    @Autowired
    private SecurityMetricsService securityMetricsService;

    @Autowired
    private OneTimeTokenConfig oneTimeTokenConfig;

    @Value("${security.csrf.enabled:false}")
    private boolean csrfEnabled;

    @Value("${security.h2-console.enabled:true}")
    private boolean h2ConsoleEnabled;

    @Value("${security.permit-all-paths:/api/auth/**,/api/test/public,/h2-console/**,/api/webauthn/**,/api/one-time-tokens/**}")
    private String[] permitAllPaths;

    @Value("${security.dpop.enabled:false}")
    private boolean dpopEnabled;

    @Value("${security.oauth2.enabled:false}")
    private boolean oauth2Enabled;

    @Value("${security.webauthn.enabled:true}")
    private boolean webauthnEnabled;

    @Value("${security.one-time-token.enabled:true}")
    private boolean oneTimeTokenEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain with Spring Security 6.5 features");
        log.info("Permit all paths: {}", String.join(", ", permitAllPaths));
        log.info("DPoP enabled: {}", dpopEnabled);
        log.info("OAuth2 enabled: {}", oauth2Enabled);
        log.info("WebAuthn enabled: {}", webauthnEnabled);
        log.info("One-Time Token enabled: {}", oneTimeTokenEnabled);

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
                .requestMatchers("/api/projects/**").authenticated()
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("/api/webauthn/register/**").authenticated()
                .requestMatchers("/api/webauthn/credentials/**").authenticated()
                .requestMatchers("/api/one-time-tokens/admin-access/**").hasRole("ADMIN")
                .requestMatchers("/api/one-time-tokens/cleanup/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider(userDetailsService, passwordEncoder()));

        // Add DPoP filter if enabled
        if (dpopEnabled) {
            http.addFilterBefore(dpopAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            log.info("DPoP authentication filter added");
        } else {
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            log.info("JWT authentication filter added");
        }

        // Configure OAuth2 Resource Server if enabled
        if (oauth2Enabled) {
            http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );
            log.info("OAuth2 Resource Server configured");
        } else {
            // Use custom JWT filter instead of OAuth2
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            log.info("Custom JWT Authentication Filter added");
        }

        // Configure OAuth2 Client if enabled
        if (oauth2Enabled) {
            http.oauth2Client(oauth2 -> oauth2
                .authorizedClientRepository(authorizedClientRepository())
            );
            log.info("OAuth2 Client configured with PKCE support");
        }

        // Configure WebAuthn if enabled
        if (webauthnEnabled) {
            // WebAuthn DSL configuration will be implemented separately
            // http.webAuthn(webAuthn -> webAuthn
            //     .relyingPartyName("TaskBoard Pro")
            //     .allowedOrigins("http://localhost:4200")
            // );
            log.info("WebAuthn configuration placeholder - will be implemented separately");
        }

        // Configure One-Time Token Login if enabled
        if (oneTimeTokenEnabled) {
            // TODO: Fix OneTimeToken configuration
            // http.oneTimeTokenLogin(ott -> ott
            //     .loginPage("/api/one-time-tokens/login")
            //     .defaultSuccessUrl("/api/auth/ott-success")
            //     .failureUrl("/api/auth/ott-failure")
            //     .successHandler(oneTimeTokenConfig.oneTimeTokenGenerationSuccessHandler())
            // );
            log.info("One-Time Token Login temporarily disabled - needs configuration fix");
        }

        // Для H2 консоли
        if (h2ConsoleEnabled) {
            http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
        }

        log.info("Security filter chain configured successfully with Spring Security 6.5 features");
        
        // Record security configuration metrics
        if (dpopEnabled) {
            securityMetricsService.recordDpopProofCreated();
        }
        if (webauthnEnabled) {
            securityMetricsService.recordWebAuthnRegistrationAttempt();
        }
        if (oneTimeTokenEnabled) {
            securityMetricsService.recordOneTimeTokenGenerated();
        }
        if (oauth2Enabled) {
            securityMetricsService.recordOAuth2LoginAttempt();
        }
        
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

    /**
     * Конвертер для извлечения authorities из JWT токена
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        authoritiesConverter.setAuthoritiesClaimName("authorities");

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return authenticationConverter;
    }


    /**
     * OAuth2 Authorized Client Repository
     */
    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository();
    }


    // CORS конфигурация уже определена в CorsConfig
    // Используем существующий bean corsConfigurationSource
}
