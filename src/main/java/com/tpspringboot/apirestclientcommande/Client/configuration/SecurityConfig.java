// Partie Securité

package com.tpspringboot.apirestclientcommande.Client.configuration;

import com.tpspringboot.apirestclientcommande.Client.filter.JwtFilter;
import com.tpspringboot.apirestclientcommande.Client.modeleCL.User;
import com.tpspringboot.apirestclientcommande.Client.repositoryCL.UserRepository;
import com.tpspringboot.apirestclientcommande.Client.serviceCL.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService ;

    private final JwtFilter jwtFilter ;

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository , PasswordEncoder passwordEncoder){
        return args -> {
            if(userRepository.findByUsername("admin")==null){
                // Créer l'admin
                User admin = new User();
                admin.setUsername("admin");

                // Encoder le mot de passe
                admin.setPassword(passwordEncoder.encode("admin123"));

                // Définir le rôle
                admin.setRole("ROLE_ADMIN");

                // Sauvegarder en base
                userRepository.save(admin);

                System.out.println("ADMIN créé : admin / admin123");
            }
        } ;
    }



    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder() ;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http , PasswordEncoder passwordEncoder){
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class) ;
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder) ;

        return authenticationManagerBuilder.build() ;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers( "/users/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET,"/commandes").hasRole("USER")
                                .requestMatchers(HttpMethod.GET, "/commandes/**").hasRole("USER")
                                .requestMatchers(HttpMethod.POST, "/commandes/**").hasRole("USER")
                                .requestMatchers(HttpMethod.GET ,"/commandesProduits").hasRole("USER")
                                .requestMatchers(HttpMethod.POST , "/commandesProduits/**").hasRole("USER")
                                .requestMatchers(HttpMethod.GET, "/produits/**").hasRole("USER")
                                .anyRequest().hasRole("ADMIN")
                                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build() ;
    }
}
