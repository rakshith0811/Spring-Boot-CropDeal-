package com.cropdeal.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.cropdeal.model.CustomUserDetails;
import com.cropdeal.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.lang.reflect.Field;
//import java.util.Collections;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void initUtil() throws Exception {
        jwtUtil = new JwtUtil();
        // inject a deterministic key
        Field secretField = JwtUtil.class.getDeclaredField("secretString");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "aVeryLongDummySecretKeyThatIsDefinitely32BytesLong!");
        jwtUtil.init();
    }

    @Test
    void generated_token_roundtrips_username_and_validation() {
        User u = new User();
        u.setUsername("john");
        u.setRole("FARMER");
        CustomUserDetails cud = new CustomUserDetails(u);   // ← single-arg ctor

        String token = jwtUtil.generateToken(cud);

        assertThat(jwtUtil.extractUsername(token)).isEqualTo("john");
        assertThat(jwtUtil.validateToken(token, cud)).isTrue();
    }

    @Test
    void expired_token_is_invalid() throws Exception {
        // build a token that expired one hour ago
        User u = new User();
        u.setUsername("john");

        Field keyField = JwtUtil.class.getDeclaredField("secretKey");
        keyField.setAccessible(true);
        SecretKey key = (SecretKey) keyField.get(jwtUtil);

        String expiredToken = Jwts.builder()
                .setSubject("john")
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200_000))   // issued 2h ago
                .setExpiration(new Date(System.currentTimeMillis() - 3600_000)) // expired 1h ago
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        assertThat(jwtUtil.validateToken(expiredToken, new CustomUserDetails(u))).isFalse();
    }
}
