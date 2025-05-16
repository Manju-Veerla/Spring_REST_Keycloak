package eu.unite.recruiting.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converts a JWT token into an {@link AbstractAuthenticationToken}.
 */
@Component
@Slf4j
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    // The default JwtGrantedAuthoritiesConverter used to extract authorities from the JWT
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    public JwtAuthConverter() {
        this.jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    }

    /**
     * Converts a JWT token into an {@link AbstractAuthenticationToken}.
     * This method extracts the authorities from the JWT and creates a new
     * {@link JwtAuthenticationToken} with the extracted authorities.
     *
     * @param jwt the JWT token to convert
     * @return an {@link AbstractAuthenticationToken} representing the JWT
     */
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        log.info("Converting JWT to Authentication Token and adding roles...");
        final Set<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractUserRoles(jwt).stream()).collect(Collectors.toSet());
        log.info("Authorities: {}", authorities);
        return new JwtAuthenticationToken(jwt, authorities);
    }

    /**
     * Extracts user roles from the JWT token.
     *
     * @param jwt the JWT token
     * @return a set of granted authorities representing user roles
     */
    private Set<? extends GrantedAuthority> extractUserRoles(Jwt jwt) {
        final Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        final List<String> realmRoles = (List<String>) realmAccess.get("roles");
        if (CollectionUtils.isNotEmpty(realmRoles)) {
            return realmRoles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

}