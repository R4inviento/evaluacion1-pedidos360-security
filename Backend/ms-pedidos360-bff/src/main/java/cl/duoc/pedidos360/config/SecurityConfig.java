package cl.duoc.pedidos360.bff.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ISSUER =
        "https://login.microsoftonline.com/5c3f1907-8201-44c1-8785-04aa0addaece/v2.0";

    private static final String BACKEND_CLIENT_ID =
        "ff20868b-f364-439d-a658-8b800bbaf552";

    private static final String APP_ID_URI =
        "api://ff20868b-f364-439d-a658-8b800bbaf552";


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(Customizer.withDefaults())

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                ).permitAll()

                .requestMatchers(
                    "/api/public"
                ).permitAll()

                .anyRequest().authenticated()
            )

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(
                        jwtAuthenticationConverter()
                    )
                )
            );

        return http.build();
    }


    @Bean
    public JwtDecoder jwtDecoder() {

        JwtDecoder decoder =
            JwtDecoders.fromIssuerLocation(
                ISSUER
            );

        OAuth2TokenValidator<Jwt> issuerValidator =
            JwtValidators.createDefaultWithIssuer(
                ISSUER
            );

        OAuth2TokenValidator<Jwt> audienceValidator =
            jwt -> {

                List<String> audiences =
                    jwt.getAudience();

                boolean valid =
                    audiences.contains(
                        BACKEND_CLIENT_ID
                    )
                    ||
                    audiences.contains(
                        APP_ID_URI
                    );

                if (valid) {

                    return OAuth2TokenValidatorResult
                        .success();
                }

                OAuth2Error error =
                    new OAuth2Error(
                        "invalid_token",
                        "Audience invalida: "
                            + audiences,
                        null
                    );

                return OAuth2TokenValidatorResult
                    .failure(error);
            };

        if (decoder instanceof NimbusJwtDecoder nimbus) {

            nimbus.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(
                    issuerValidator,
                    audienceValidator
                )
            );
        }

        return decoder;
    }


    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken>
        jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
            new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
            jwt -> {

                Collection<GrantedAuthority> authorities =
                    new ArrayList<>();


                /*
                 * SCOPES
                 *
                 * Microsoft Entra normalmente
                 * entrega los scopes en el claim "scp".
                 */
                String scopes =
                    jwt.getClaimAsString(
                        "scp"
                    );

                if (
                    scopes != null
                    &&
                    !scopes.isBlank()
                ) {

                    for (
                        String scope :
                        scopes.split(" ")
                    ) {

                        authorities.add(
                            new SimpleGrantedAuthority(
                                "SCOPE_" + scope
                            )
                        );
                    }
                }


                /*
                 * ROLES
                 *
                 * Los App Roles de Microsoft Entra
                 * aparecen en el claim "roles".
                 */
                List<String> roles =
                    jwt.getClaimAsStringList(
                        "roles"
                    );

                if (roles != null) {

                    for (
                        String role :
                        roles
                    ) {

                        authorities.add(
                            new SimpleGrantedAuthority(
                                "ROLE_" + role
                            )
                        );
                    }
                }


                return authorities;
            }
        );

        return converter;
    }


    @Bean
    public CorsConfigurationSource
        corsConfigurationSource() {

        CorsConfiguration configuration =
            new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of(
                "http://localhost:4200"
            )
        );

        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            List.of("*")
        );

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }
}