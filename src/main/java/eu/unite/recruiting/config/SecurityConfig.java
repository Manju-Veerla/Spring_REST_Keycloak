package eu.unite.recruiting.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
    /**
     * URLS that can be accessed without any authentication.
     */
    private static final String[] WHITELIST_URLS = {"/api/v1/workshops/upcoming", "/h2-console",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/swagger-resources"};
    // The JWT authentication converter for converting JWT tokens to authentication objects.
    private final JwtAuthConverter jwtAuthConverter;

    /**
     * Security filter chain for the application.
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(WHITELIST_URLS).permitAll()
                            .anyRequest().authenticated(); // Authenticate any other request
                })
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                        jwt.jwtAuthenticationConverter(jwtAuthConverter))
                                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception ->
                        exception.accessDeniedHandler(new CustomAccessDeniedHandler())
                                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
