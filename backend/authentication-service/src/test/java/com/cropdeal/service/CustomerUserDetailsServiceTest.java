package com.cropdeal.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.cropdeal.model.User;
import com.cropdeal.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;   // ★ add
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomerUserDetailsServiceTest {

    @Mock UserRepository repo;
    @InjectMocks CustomerUserDetailsService service;

    @Test
    void existing_user_is_wrapped_in_custom_details() {
        User u = new User();
        u.setUsername("john");
        given(repo.findUserByUsername("john")).willReturn(u);

        var details = service.loadUserByUsername("john");

        assertThat(details.getUsername()).isEqualTo("john");
    }

    @Test
    void unknown_user_throws_exception() {
        given(repo.findUserByUsername("nope")).willReturn(null);

        assertThatThrownBy(() -> service.loadUserByUsername("nope"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
