package com.cropdeal.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

class CustomUserDetailsTest {

    @Test
    void authorities_include_role_prefix() {
        User u = new User();
        u.setUsername("john");
        u.setPassword("pw");
        u.setRole("dealer");

        CustomUserDetails cud = new CustomUserDetails(u);

        assertThat(cud.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_DEALER");
    }

    @Test
    void authorities_with_null_role_defaults_to_USER() {
        User u = new User();
        u.setUsername("jane");
        u.setPassword("pw");
        u.setRole(null);

        CustomUserDetails cud = new CustomUserDetails(u);

        assertThat(cud.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void equals_and_hashCode_rely_on_username_only() {
        User a = new User(); a.setUsername("same");
        User b = new User(); b.setUsername("same");
        CustomUserDetails one = new CustomUserDetails(a);
        CustomUserDetails two = new CustomUserDetails(b);

        assertThat(one).isEqualTo(two)
                       .hasSameHashCodeAs(two);

        HashSet<CustomUserDetails> set = new HashSet<>();
        set.add(one);
        set.add(two); // should not create a new entry
        assertThat(set).hasSize(1);
    }

    @Test
    void getters_return_expected_values() {
        User u = new User();
        u.setUsername("user1");
        u.setPassword("secret");
        u.setRole("admin");
        u.setActive(true);

        CustomUserDetails cud = new CustomUserDetails(u);

        assertThat(cud.getUsername()).isEqualTo("user1");
        assertThat(cud.getPassword()).isEqualTo("secret");
        assertThat(cud.getRole()).isEqualTo("admin");
        assertThat(cud.getUser()).isSameAs(u);
    }

    @Test
    void account_status_methods_reflect_user_active_flag() {
        User u = new User();
        u.setUsername("activeUser");
        u.setPassword("pw");
        u.setRole("farmer");
        u.setActive(true);

        CustomUserDetails cudActive = new CustomUserDetails(u);

        assertThat(cudActive.isAccountNonExpired()).isTrue();
        assertThat(cudActive.isAccountNonLocked()).isTrue();
        assertThat(cudActive.isCredentialsNonExpired()).isTrue();
        assertThat(cudActive.isEnabled()).isTrue();

        u.setActive(false);
        CustomUserDetails cudInactive = new CustomUserDetails(u);

        assertThat(cudInactive.isAccountNonExpired()).isFalse();
        assertThat(cudInactive.isAccountNonLocked()).isFalse();
        assertThat(cudInactive.isCredentialsNonExpired()).isFalse();
        assertThat(cudInactive.isEnabled()).isFalse();
    }

    @Test
    void toString_contains_important_info() {
        User u = new User();
        u.setUsername("toStringUser");
        u.setPassword("pw");
        u.setRole("dealer");
        u.setActive(true);

        CustomUserDetails cud = new CustomUserDetails(u);

        String str = cud.toString();

        assertThat(str).contains("toStringUser");
        assertThat(str).contains("dealer");
        assertThat(str).contains("true");
    }
}
