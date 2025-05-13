package com.omnixys.payment.config;

import com.omnixys.payment.security.JwtToUserDetailsConverter;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

sealed interface SecurityConfig permits ApplicationConfig {
  /**
   * Konfiguriert die Sicherheitsfilterkette für die Anwendung.
   *
   * @param httpSecurity Das HttpSecurity-Objekt zur Konfiguration der Sicherheitsrichtlinien.
   * @return Die konfigurierte `SecurityFilterChain`-Instanz.
   * @throws Exception Falls eine Sicherheitskonfiguration fehlschlägt.
   */
  @Bean
  default SecurityFilterChain securityFilterChain(
      final HttpSecurity httpSecurity,
      JwtToUserDetailsConverter converter
  ) throws Exception {
    return httpSecurity
        .authorizeHttpRequests(authorize -> {
          authorize
              // ✅ Erlaubt alle GraphQL-Anfragen (Schema-Ladung & Queries)
              .requestMatchers(POST, "/graphql").permitAll()

              .requestMatchers(
                  // Actuator: Health for liveness and readiness for Kubernetes
                  EndpointRequest.to(HealthEndpoint.class),
                  // Actuator: Prometheus for monitoring
                  EndpointRequest.to(PrometheusScrapeEndpoint.class)
              ).permitAll()
              // OpenAPI or Swagger UI and GraphiQL
              .requestMatchers(GET, "/v3/api-docs.yaml", "/v3/api-docs", "/graphiql").permitAll()
              .requestMatchers("/error", "/error/**").permitAll()

              .anyRequest().authenticated();
        })
        //.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
        // Spring Security does not create or use HttpSession for SecurityContext
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .formLogin(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
        .build();
  }

  /**
   * Definiert die Passwortkodierung für die Anwendung.
   *
   * @return Ein PasswordEncoder zur sicheren Speicherung von Passwörtern.
   */
  @Bean
  default PasswordEncoder passwordEncoder() {
    return createDelegatingPasswordEncoder();
  }

  @Bean
  default CompromisedPasswordChecker compromisedPasswordChecker() {
    return new HaveIBeenPwnedRestApiPasswordChecker();
  }
}