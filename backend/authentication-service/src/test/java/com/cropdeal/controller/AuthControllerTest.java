package com.cropdeal.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;

import com.cropdeal.model.AuthRequest;
import com.cropdeal.model.CustomUserDetails;
import com.cropdeal.model.Dealer;
import com.cropdeal.model.Farmer;
import com.cropdeal.model.User;
import com.cropdeal.repository.DealerRepository;
import com.cropdeal.repository.FarmerRepository;
import com.cropdeal.repository.UserRepository;
import com.cropdeal.service.CustomerUserDetailsService;
import com.cropdeal.util.JwtUtil;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock AuthenticationManager authenticationManager;
    @Mock CustomerUserDetailsService userDetailsService;
    @Mock JwtUtil jwtUtil;
    @Mock PasswordEncoder passwordEncoder;
    @Mock UserRepository userRepository;
    @Mock FarmerRepository farmerRepository;
    @Mock DealerRepository dealerRepository;

    @InjectMocks AuthController controller;

    private User activeUser;

    @BeforeEach
    void setUp() {
        activeUser = new User();
        activeUser.setId(1);
        activeUser.setUsername("john");
        activeUser.setPassword("plain");
        activeUser.setRole("FARMER");
        activeUser.setActive(true);
    }

    private AuthRequest makeAuthReq() {
        AuthRequest req = new AuthRequest();
        req.setUsername("john");
        req.setPassword("plain");
        return req;
    }

    // ---------- /signin ----------
    @Nested class SignIn {

        @Test
        void successful_login_returns_jwt() {
            AuthRequest req = makeAuthReq();
            CustomUserDetails cud = new CustomUserDetails(activeUser);

            given(userRepository.findUserByUsername("john")).willReturn(activeUser);
            given(authenticationManager
                    .authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willReturn(mock(Authentication.class));
            given(userDetailsService.loadUserByUsername("john")).willReturn(cud);
            given(jwtUtil.generateToken(cud)).willReturn("jwt-token");

            ResponseEntity<?> resp = controller.createAuthenticationToken(req);

            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(resp.getBody()).isEqualTo("jwt-token");
        }

        @Test
        void inactive_user_returns_forbidden() {
            activeUser.setActive(false);
            given(userRepository.findUserByUsername("john")).willReturn(activeUser);

            ResponseEntity<?> resp = controller.createAuthenticationToken(makeAuthReq());

            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        @Test
        void unknown_user_returns_unauthorized() {
            given(userRepository.findUserByUsername("john")).willReturn(null);

            ResponseEntity<?> resp = controller.createAuthenticationToken(makeAuthReq());

            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        void authentication_manager_throws_bad_credentials_returns_unauthorized() {
            given(userRepository.findUserByUsername("john")).willReturn(activeUser);
            doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

            ResponseEntity<?> resp = controller.createAuthenticationToken(makeAuthReq());

            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    // ---------- /signup ----------
    @Nested class SignUp {

        @Test
        void username_taken_returns_bad_request() {
            given(userRepository.findUserByUsername("john")).willReturn(activeUser);

            ResponseEntity<?> resp = controller.registerUser(activeUser);

            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        void new_farmer_user_is_saved_and_ok() {
            activeUser.setRole("FARMER");
            given(userRepository.findUserByUsername("john")).willReturn(null);
            given(passwordEncoder.encode("plain")).willReturn("hashed-pw");
            given(userRepository.save(any(User.class))).willAnswer(i -> i.getArgument(0));

            ResponseEntity<?> resp = controller.registerUser(activeUser);

            then(farmerRepository).should().save(any(Farmer.class));
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void new_dealer_user_is_saved_and_ok() {
            activeUser.setRole("DEALER");
            given(userRepository.findUserByUsername("john")).willReturn(null);
            given(passwordEncoder.encode("plain")).willReturn("hashed-pw");
            given(userRepository.save(any(User.class))).willAnswer(i -> i.getArgument(0));

            ResponseEntity<?> resp = controller.registerUser(activeUser);

            then(dealerRepository).should().save(any(Dealer.class));
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void new_admin_user_is_saved_and_ok_without_farmer_or_dealer_creation() {
            activeUser.setRole("ADMIN");
            given(userRepository.findUserByUsername("john")).willReturn(null);
            given(passwordEncoder.encode("plain")).willReturn("hashed-pw");
            given(userRepository.save(any(User.class))).willAnswer(i -> i.getArgument(0));

            ResponseEntity<?> resp = controller.registerUser(activeUser);

            // Should not call farmerRepository or dealerRepository
            then(farmerRepository).shouldHaveNoInteractions();
            then(dealerRepository).shouldHaveNoInteractions();
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    // ---------- /health ----------
    @Test
    void health_check_ok() {
        assertThat(controller.healthCheck().getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // ---------- /validate ----------
    @Nested class Validate {

        @Test
        void valid_farmer_token_returns_redirect_info() {
            CustomUserDetails cud = new CustomUserDetails(activeUser);
            String token = "jwt-token";
            Farmer farmer = new Farmer();
            farmer.setId(99);

            given(jwtUtil.extractUsername(token)).willReturn("john");
            given(userDetailsService.loadUserByUsername("john")).willReturn(cud);
            given(jwtUtil.validateToken(token, cud)).willReturn(true);
            given(farmerRepository.findByUser_Username("john")).willReturn(farmer);

            ResponseEntity<?> resp = controller.validateToken("Bearer " + token);

            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertThat(body.get("username")).isEqualTo("john");
            assertThat(body.get("role")).isEqualTo("FARMER");
            assertThat(body.get("id")).isEqualTo(99L);
        }

        @Test
        void valid_dealer_token_returns_redirect_info() {
            activeUser.setRole("DEALER");
            CustomUserDetails cud = new CustomUserDetails(activeUser);
            String token = "jwt-token";
            Dealer dealer = new Dealer();
            dealer.setId(88);

            given(jwtUtil.extractUsername(token)).willReturn("john");
            given(userDetailsService.loadUserByUsername("john")).willReturn(cud);
            given(jwtUtil.validateToken(token, cud)).willReturn(true);
            given(dealerRepository.findByUser_Username("john")).willReturn(dealer);

            ResponseEntity<?> resp = controller.validateToken("Bearer " + token);

            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertThat(body.get("username")).isEqualTo("john");
            assertThat(body.get("role")).isEqualTo("DEALER");
            assertThat(body.get("id")).isEqualTo(88L);
        }

        @Test
        void valid_admin_token_returns_redirect_info() {
            activeUser.setRole("ADMIN");
            CustomUserDetails cud = new CustomUserDetails(activeUser);
            String token = "jwt-token";

            given(jwtUtil.extractUsername(token)).willReturn("john");
            given(userDetailsService.loadUserByUsername("john")).willReturn(cud);
            given(jwtUtil.validateToken(token, cud)).willReturn(true);
            given(userRepository.findUserByUsername("john")).willReturn(activeUser);

            ResponseEntity<?> resp = controller.validateToken("Bearer " + token);

            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertThat(body.get("username")).isEqualTo("john");
            assertThat(body.get("role")).isEqualTo("ADMIN");
            assertThat(body.get("id")).isEqualTo(1L);
        }

        @Test
        void valid_token_unknown_role_returns_redirect_to_user_profile() {
            activeUser.setRole("GUEST");
            CustomUserDetails cud = new CustomUserDetails(activeUser);
            String token = "jwt-token";

            given(jwtUtil.extractUsername(token)).willReturn("john");
            given(userDetailsService.loadUserByUsername("john")).willReturn(cud);
            given(jwtUtil.validateToken(token, cud)).willReturn(true);

            ResponseEntity<?> resp = controller.validateToken("Bearer " + token);

            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertThat(body.get("redirectUrl")).isEqualTo("http://localhost:8080/api/user/profile/john");
        }

        @Test
        void valid_farmer_token_profile_not_found_returns_not_found() {
            CustomUserDetails cud = new CustomUserDetails(activeUser);
            String token = "jwt-token";

            given(jwtUtil.extractUsername(token)).willReturn("john");
            given(userDetailsService.loadUserByUsername("john")).willReturn(cud);
            given(jwtUtil.validateToken(token, cud)).willReturn(true);
            given(farmerRepository.findByUser_Username("john")).willReturn(null);

            ResponseEntity<?> resp = controller.validateToken("Bearer " + token);

            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        void valid_dealer_token_profile_not_found_returns_not_found() {
            activeUser.setRole("DEALER");
            CustomUserDetails cud = new CustomUserDetails(activeUser);
            String token = "jwt-token";

            given(jwtUtil.extractUsername(token)).willReturn("john");
            given(userDetailsService.loadUserByUsername("john")).willReturn(cud);
            given(jwtUtil.validateToken(token, cud)).willReturn(true);
            given(dealerRepository.findByUser_Username("john")).willReturn(null);

            ResponseEntity<?> resp = controller.validateToken("Bearer " + token);

            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        void valid_admin_token_profile_not_found_returns_not_found() {
            activeUser.setRole("ADMIN");
            CustomUserDetails cud = new CustomUserDetails(activeUser);
            String token = "jwt-token";

            given(jwtUtil.extractUsername(token)).willReturn("john");
            given(userDetailsService.loadUserByUsername("john")).willReturn(cud);
            given(jwtUtil.validateToken(token, cud)).willReturn(true);
            given(userRepository.findUserByUsername("john")).willReturn(null);

            ResponseEntity<?> resp = controller.validateToken("Bearer " + token);

            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        void invalid_token_returns_unauthorized() {
            String token = "badToken";
            given(jwtUtil.extractUsername(token)).willThrow(new RuntimeException("boom"));

            ResponseEntity<?> resp = controller.validateToken(token);

            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }
}
