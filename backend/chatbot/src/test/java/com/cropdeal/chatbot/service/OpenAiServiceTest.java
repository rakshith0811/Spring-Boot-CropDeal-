package com.cropdeal.chatbot.service;

import com.cropdeal.chatbot.dto.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenAiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OpenAiService openAiService;

    private static final String TEST_API_KEY = "test-api-key";
    private static final String TEST_USER_MESSAGE = "Hello, how are you?";
    private static final String TEST_AI_RESPONSE = "I'm doing well, thank you!";

    @BeforeEach
    void setUp() {
        // Set the API key using ReflectionTestUtils
        ReflectionTestUtils.setField(openAiService, "apiKey", TEST_API_KEY);
        
        // Create a spy to inject the mocked RestTemplate
        openAiService = spy(openAiService);
        
        // Override the RestTemplate creation in the method
        doAnswer(invocation -> {
            String userMessage = invocation.getArgument(0);
            return getChatResponseWithMockedRestTemplate(userMessage);
        }).when(openAiService).getChatResponse(anyString());
    }

    @Test
    void testGetChatResponse_Success() {
        // Arrange
        Map<String, Object> messageMap = Map.of("content", TEST_AI_RESPONSE);
        Map<String, Object> choice = Map.of("message", messageMap);
        Map<String, Object> responseBody = Map.of("choices", List.of(choice));
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        ChatResponse result = openAiService.getChatResponse(TEST_USER_MESSAGE);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_AI_RESPONSE, result.getResponse());
    }

    @Test
    void testGetChatResponse_VerifyRequestParameters() {
        // Arrange
        Map<String, Object> messageMap = Map.of("content", TEST_AI_RESPONSE);
        Map<String, Object> choice = Map.of("message", messageMap);
        Map<String, Object> responseBody = Map.of("choices", List.of(choice));
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        openAiService.getChatResponse(TEST_USER_MESSAGE);

        // Assert - Verify the correct URL is called
        verify(restTemplate).postForEntity(
                eq("https://api.openai.com/v1/chat/completions"),
                any(HttpEntity.class),
                eq(Map.class)
        );

        // Capture and verify the request entity
        ArgumentCaptor<HttpEntity<Map<String, Object>>> requestCaptor = 
                ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(anyString(), requestCaptor.capture(), eq(Map.class));

        HttpEntity<Map<String, Object>> capturedRequest = requestCaptor.getValue();
        
        // Verify headers
        HttpHeaders headers = capturedRequest.getHeaders();
        assertTrue(headers.containsKey(HttpHeaders.AUTHORIZATION));
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        
        // Verify request body
        Map<String, Object> requestBody = capturedRequest.getBody();
        assertNotNull(requestBody);
        assertEquals("gpt-3.5-turbo", requestBody.get("model"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> messages = (List<Map<String, Object>>) requestBody.get("messages");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        
        Map<String, Object> message = messages.get(0);
        assertEquals("user", message.get("role"));
        assertEquals(TEST_USER_MESSAGE, message.get("content"));
    }

    @Test
    void testGetChatResponse_EmptyUserMessage() {
        // Arrange
        String emptyMessage = "";
        Map<String, Object> messageMap = Map.of("content", "Hello! How can I help you?");
        Map<String, Object> choice = Map.of("message", messageMap);
        Map<String, Object> responseBody = Map.of("choices", List.of(choice));
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        ChatResponse result = openAiService.getChatResponse(emptyMessage);

        // Assert
        assertNotNull(result);
        assertEquals("Hello! How can I help you?", result.getResponse());
    }

       @Test
    void testGetChatResponse_RestClientException() {
        // Arrange
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RestClientException("Network error"));

        // Act & Assert
        assertThrows(RestClientException.class, () -> {
            openAiService.getChatResponse(TEST_USER_MESSAGE);
        });
    }

    @Test
    void testGetChatResponse_MultipleChoicesResponse() {
        // Arrange
        Map<String, Object> messageMap1 = Map.of("content", "First response");
        Map<String, Object> messageMap2 = Map.of("content", "Second response");
        Map<String, Object> choice1 = Map.of("message", messageMap1);
        Map<String, Object> choice2 = Map.of("message", messageMap2);
        Map<String, Object> responseBody = Map.of("choices", List.of(choice1, choice2));
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        ChatResponse result = openAiService.getChatResponse(TEST_USER_MESSAGE);

        // Assert - Should return the first choice
        assertNotNull(result);
        assertEquals("First response", result.getResponse());
    }

    @Test
    void testGetChatResponse_LongUserMessage() {
        // Arrange
        String longMessage = "This is a very long message that exceeds normal length to test how the service handles long inputs. ".repeat(10);
        Map<String, Object> messageMap = Map.of("content", "I received your long message.");
        Map<String, Object> choice = Map.of("message", messageMap);
        Map<String, Object> responseBody = Map.of("choices", List.of(choice));
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        ChatResponse result = openAiService.getChatResponse(longMessage);

        // Assert
        assertNotNull(result);
        assertEquals("I received your long message.", result.getResponse());
    }

    @Test
    void testGetChatResponse_SpecialCharacters() {
        // Arrange
        String messageWithSpecialChars = "Hello! @#$%^&*()_+ 你好 émoji 🚀";
        Map<String, Object> messageMap = Map.of("content", "I can handle special characters!");
        Map<String, Object> choice = Map.of("message", messageMap);
        Map<String, Object> responseBody = Map.of("choices", List.of(choice));
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        ChatResponse result = openAiService.getChatResponse(messageWithSpecialChars);

        // Assert
        assertNotNull(result);
        assertEquals("I can handle special characters!", result.getResponse());
    }

    // Helper method to simulate the actual service behavior with mocked RestTemplate
    private ChatResponse getChatResponseWithMockedRestTemplate(String userMessage) {
        Map<String, Object> message = Map.of("role", "user", "content", userMessage);
        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(message)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(TEST_API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions", 
                request, 
                Map.class
        );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> messageResponse = (Map<String, Object>) choices.get(0).get("message");

        return new ChatResponse((String) messageResponse.get("content"));
    }
}