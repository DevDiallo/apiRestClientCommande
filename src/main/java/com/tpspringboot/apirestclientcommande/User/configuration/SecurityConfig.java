// Partie Securité

package com.tpspringboot.apirestclientcommande.User.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpspringboot.apirestclientcommande.Exceptions.ApiError;
import com.tpspringboot.apirestclientcommande.User.filter.JwtFilter;
import com.tpspringboot.apirestclientcommande.User.modeleCL.User;
import com.tpspringboot.apirestclientcommande.User.repositoryCL.UserRepository;
import com.tpspringboot.apirestclientcommande.User.serviceCL.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService ;

    private final JwtFilter jwtFilter ;
    private final ObjectMapper objectMapper;

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository , PasswordEncoder passwordEncoder){
        return args -> {
            User existingAdmin = userRepository.findByEmail("admin@exemple.com");
            if (existingAdmin == null) {
                existingAdmin = userRepository.findByUsername("admin@exemple.com");
            }
            // Compatibilite avec un ancien seed admin@example.com
            if (existingAdmin == null) {
                existingAdmin = userRepository.findByEmail("admin@example.com");
            }
            if (existingAdmin != null && "admin@example.com".equalsIgnoreCase(existingAdmin.getEmail())) {
                existingAdmin.setEmail("admin@exemple.com");
                existingAdmin.setUsername("admin@exemple.com");
                userRepository.save(existingAdmin);
            }

            if(existingAdmin == null){
                // Créer l'admin
                User admin = new User();
                admin.setNom("Admin");
                admin.setPrenom("System");
                admin.setEmail("admin@exemple.com");
                admin.setTelephone("+33612345678");
                admin.setUsername("admin@exemple.com");

                // Encoder le mot de passe
                admin.setPassword(passwordEncoder.encode("admin123"));

                // Définir le rôle
                admin.setRole("ROLE_ADMIN");

                // Sauvegarder en base
                userRepository.save(admin);

                System.out.println("ADMIN cree avec succes : admin@exemple.com / admin123");
            }
        } ;
    }



    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder() ;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http , PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class) ;
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder) ;

        return authenticationManagerBuilder.build() ;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        config.setExposedHeaders(List.of("Set-Cookie", "Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                        writeApiError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required", authException.getMessage()))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        writeApiError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied", accessDeniedException.getMessage()))
            )
            .authorizeHttpRequests(auth -> auth
                // 1) PREFLIGHT - toujours public
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 2) AUTH - public
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // 3) PRODUITS - GET public (catalogue visible sans login)
                .requestMatchers(HttpMethod.GET, "/api/produits").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/produits/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/produits/**").permitAll()

                // 4) CATEGORIES - GET public
                .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/categories").permitAll()
                .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()

                // 4-bis) LIGNESTOCKS / STOCKS - GET public
                .requestMatchers(HttpMethod.GET, "/api/ligneStocks").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/ligneStocks/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/stocks").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/stocks/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/ligneStocks").permitAll()
                .requestMatchers(HttpMethod.GET, "/ligneStocks/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/stocks").permitAll()
                .requestMatchers(HttpMethod.GET, "/stocks/**").permitAll()

                // 4-ter) COMMANDES ADMIN details
                .requestMatchers(HttpMethod.GET, "/api/commandes/*/details").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/commandes/*/details").hasRole("ADMIN")

                // 5) PRODUITS - CUD réservé ADMIN
                .requestMatchers(HttpMethod.POST,   "/api/produits/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/produits/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/produits/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/produits/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/produits/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/produits/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/ligneStocks/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/ligneStocks/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/ligneStocks/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/ligneStocks/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/stocks/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/stocks/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/stocks/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/stocks/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/stocks/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/stocks/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN")

                // 6) USERS - réservé ADMIN
                .requestMatchers("/users/**").hasRole("ADMIN")
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("/clients/**").hasRole("ADMIN")
                .requestMatchers("/api/clients/**").hasRole("ADMIN")

                // 7) COMMANDES - USER peut créer
                .requestMatchers(HttpMethod.POST, "/commandes/**").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/commandes/**").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/commandes/**").hasAnyRole("USER","ADMIN")
                .requestMatchers(HttpMethod.PUT, "/users/commandes/**").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/commandesProduits/**").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/commandesProduits/**").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/commandesProduits/**").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/commandes/**").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/commandes/**").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/commandes/**").hasAnyRole("USER","ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/commandes/**").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/commandesProduits/**").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/commandesProduits/**").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/commandesProduits/**").hasRole("USER")

                // 8) COMMANDES + COMMANDESPRODUITS GET - USER et ADMIN
                .requestMatchers(HttpMethod.GET, "/commandes/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/commandesProduits/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/commandes/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/commandesProduits/**").hasAnyRole("USER", "ADMIN")

                // 8-bis) USER history routes
                .requestMatchers(HttpMethod.GET, "/my/commandes/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/my/commandesProduits/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/my/commandes/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/my/commandesProduits/**").hasAnyRole("USER", "ADMIN")

                // 9) Tout le reste - authentifié
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void writeApiError(HttpServletResponse response, int status, String message, Object details) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ApiError body = ApiError.of(status, message, details);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
