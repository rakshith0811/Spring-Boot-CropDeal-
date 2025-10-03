package com.cropdeal.controller;

import com.cropdeal.model.AuthRequest;
import com.cropdeal.model.CustomUserDetails;
import com.cropdeal.model.Dealer;
import com.cropdeal.model.Farmer;
import com.cropdeal.model.User;
import com.cropdeal.repository.DealerRepository;
import com.cropdeal.repository.FarmerRepository;
import com.cropdeal.repository.UserRepository;
import com.cropdeal.service.CustomerUserDetailsService;
import com.cropdeal.service.ValidationResponse;
import com.cropdeal.util.JwtUtil;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private DealerRepository dealerRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authenticationRequest) {
        try {
            System.out.println("Attempting login for: " + authenticationRequest.getUsername());

            // First, fetch user from DB to check active status before authentication
            User user = userRepository.findUserByUsername(authenticationRequest.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
            if (!user.isActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Admin deactivated your account for suspicious activity");
            }

            // Now authenticate the user (username and password check)
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
                )
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            String jwt = jwtUtil.generateToken((CustomUserDetails) userDetails);
            return ResponseEntity.ok(jwt);
        } catch (Exception e) {
            e.printStackTrace(); // useful during dev
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        if (userRepository.findUserByUsername(user.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Username is already taken!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        User savedUser = userRepository.save(user);

        if ("FARMER".equalsIgnoreCase(user.getRole())) {
            Farmer farmer = new Farmer();
            farmer.setUser(savedUser);
            farmerRepository.save(farmer);
        } else if ("DEALER".equalsIgnoreCase(user.getRole())) {
            Dealer dealer = new Dealer();
            dealer.setUser(savedUser);
            dealerRepository.save(dealer);
        }

        return ResponseEntity.ok("User registered successfully!");
    }
    
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsernameAvailability(@RequestParam String username) {
        Map<String, Boolean> response = new HashMap<>();
        // Using existsByUsername for a more direct boolean check
        boolean isUnique = !userRepository.existsByUsername(username); // Negate because existsByUsername returns true if found
        response.put("isUnique", isUnique);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Authentication Service is up and running");
    }
    @GetMapping("/username-exists")
    public boolean checkUsernameExists(@RequestParam String username) {
        return userRepository.existsByUsername(username);
    }


   // @SuppressWarnings("unused")
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            String username = jwtUtil.extractUsername(jwtToken);
            UserDetails user = userDetailsService.loadUserByUsername(username);
            CustomUserDetails cuser = (CustomUserDetails) user;
            long id = 0;
            String redirectUrl = "";

            if (jwtUtil.validateToken(jwtToken, user)) {
                if (cuser.getRole().equalsIgnoreCase("FARMER")) {
                    Farmer farmer = farmerRepository.findByUser_Username(cuser.getUsername());
                    if (farmer != null) {
                        id = farmer.getId();
                        redirectUrl = "http://localhost:8083/api/farmer/";
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Farmer profile not found for user: " + cuser.getUsername());
                    }
                } else if (cuser.getRole().equalsIgnoreCase("DEALER")) {
                    Dealer dealer = dealerRepository.findByUser_Username(cuser.getUsername());
                    if (dealer != null) {
                        id = dealer.getId();
                        redirectUrl = "http://localhost:8082/api/dealer/";
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Dealer profile not found for user: " + cuser.getUsername());
                    }
                } else if (cuser.getRole().equalsIgnoreCase("ADMIN")) {
                    User adminUser = userRepository.findUserByUsername(cuser.getUsername());
                    if (adminUser != null) {
                        id = adminUser.getId();
                        redirectUrl = "http://localhost:8084/api/admin/";
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Admin user not found for: " + cuser.getUsername());
                    }
                } else {
                    redirectUrl = "http://localhost:8080/api/user/profile/" + cuser.getUsername();
                }

                Map<String, Object> response = new HashMap<>();
                response.put("username", cuser.getUsername());
                response.put("role", cuser.getRole());
                response.put("redirectUrl", redirectUrl);
                response.put("id", id);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

}
