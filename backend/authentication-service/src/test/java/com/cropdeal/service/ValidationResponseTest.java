package com.cropdeal.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ValidationResponseTest {

    @Test
    void record_should_store_and_return_values() {
        ValidationResponse response = new ValidationResponse("user1", "pass123", "admin");

        // Accessors
        assertThat(response.username()).isEqualTo("user1");
        assertThat(response.password()).isEqualTo("pass123");
        assertThat(response.role()).isEqualTo("admin");

        // toString contains fields
        assertThat(response.toString())
            .contains("user1")
            .contains("pass123")
            .contains("admin");

        // equals and hashCode
        ValidationResponse same = new ValidationResponse("user1", "pass123", "admin");
        ValidationResponse different = new ValidationResponse("user2", "pass123", "admin");

        assertThat(response).isEqualTo(same);
        assertThat(response.hashCode()).isEqualTo(same.hashCode());

        assertThat(response).isNotEqualTo(different);
    }
}
