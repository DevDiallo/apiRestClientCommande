package com.tpspringboot.apirestclientcommande.Client.controllerCL;

import com.tpspringboot.apirestclientcommande.Client.configuration.JwtUtils;
import com.tpspringboot.apirestclientcommande.Client.modeleCL.User;
import com.tpspringboot.apirestclientcommande.Client.repositoryCL.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
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

    @PostMapping("/register")
    public ResponseEntity<?> resgister(@RequestBody User user){
        if(userRepository.findByUsername(user.getUsername()) != null){
            return ResponseEntity.badRequest().body("Username is already in use !") ;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.ok(userRepository.save(user)) ;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername() ,
                            user.getPassword()
                    )
            ) ;
            if (authentication.isAuthenticated()){
                String userName = authentication.getName() ;
                Map<String,Object> authData = new HashMap<>() ;
                authData.put("token" , jwtUtils.generateToken(userName)) ;
                authData.put("type" , "Bearer") ;
                return ResponseEntity.ok(authData) ;
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username and Password not Correct !") ;
        } catch (AuthenticationException e){
            log.error(e.getMessage()) ;
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Username and Correct !") ;
        }
    }

}
