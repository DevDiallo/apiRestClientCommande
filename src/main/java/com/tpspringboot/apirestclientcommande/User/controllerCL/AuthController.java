package com.tpspringboot.apirestclientcommande.User.controllerCL;

import com.tpspringboot.apirestclientcommande.User.configuration.JwtUtils;
import com.tpspringboot.apirestclientcommande.User.dto.LoginRequest;
import com.tpspringboot.apirestclientcommande.User.dto.RegisterRequest;
import com.tpspringboot.apirestclientcommande.User.modeleCL.User;
import com.tpspringboot.apirestclientcommande.User.repositoryCL.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {


    private final UserRepository userRepository ;
    private final PasswordEncoder passwordEncoder ;
    private final AuthenticationManager authenticationManager ;
    private final JwtUtils jwtUtils ;

    @Value("${app.expiration-time}")
    private long expirationTime;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        String email = request.getEmail().trim().toLowerCase();

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Les deux mots de passe doivent etre identiques.");
        }

        if(userRepository.findByUsername(email) != null || userRepository.findByEmail(email) != null){
            return ResponseEntity.badRequest().body("Cet email est deja utilise.") ;
        }

        User user = new User();
        user.setNom(request.getNom().trim());
        user.setPrenom(request.getPrenom().trim());
        user.setEmail(email);
        user.setTelephone(request.getTelephone().trim());
        user.setUsername(email);
        user.setRole("ROLE_USER"); // toutes les connexions ont un role USER

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("id", savedUser.getId());
        response.put("nom", savedUser.getNom());
        response.put("prenom", savedUser.getPrenom());
        response.put("email", savedUser.getEmail());
        response.put("telephone", savedUser.getTelephone());
        response.put("role", savedUser.getRole());

        return ResponseEntity.status(HttpStatus.CREATED).body(response) ;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        try {
            String email = request.getEmail().trim().toLowerCase();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email ,
                            request.getPassword()
                    )
            ) ;
            if (authentication.isAuthenticated()){
                String userName = authentication.getName() ;

                // Récupérer les rôles depuis Spring Security
                List<String> roles = authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();

                // Récupérer les infos utilisateur depuis la DB
                User userFromDb = userRepository.findByUsername(userName);
                if (userFromDb == null) {
                    userFromDb = userRepository.findByEmail(userName);
                }

                String jwt = jwtUtils.generateToken(userName, roles);

                Map<String,Object> authData = new HashMap<>() ;
                authData.put("token", jwt);
                authData.put("accessToken", jwt);
                authData.put("jwt", jwt);
                authData.put("type",   "Bearer");
                authData.put("roles",  roles);

                // Infos user pour le front (évite un 2e appel)
                if (userFromDb != null) {
                    authData.put("id",        userFromDb.getId());
                    authData.put("email",     userFromDb.getEmail());
                    authData.put("username",  userFromDb.getUsername());
                    authData.put("nom",       userFromDb.getNom());
                    authData.put("prenom",    userFromDb.getPrenom());
                    authData.put("telephone", userFromDb.getTelephone());

                    Map<String, Object> user = new HashMap<>();
                    user.put("id", userFromDb.getId());
                    user.put("email", userFromDb.getEmail());
                    user.put("username", userFromDb.getUsername());
                    user.put("nom", userFromDb.getNom());
                    user.put("prenom", userFromDb.getPrenom());
                    user.put("telephone", userFromDb.getTelephone());
                    user.put("role", userFromDb.getRole());
                    user.put("roles", roles);
                    authData.put("user", user);
                }

                ResponseCookie cookie = ResponseCookie.from("accessToken", jwt)
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Lax")
                        .path("/")
                        .maxAge(expirationTime / 1000)
                        .build();

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(authData) ;
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou mot de passe incorrect.") ;
        } catch (AuthenticationException e){
            log.error(e.getMessage()) ;
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiants invalides.") ;
        }
    }


}
