package com.cropdeal.chatbot.controller;

import com.cropdeal.chatbot.dto.ChatRequest;
import com.cropdeal.chatbot.dto.ChatResponse;
import com.cropdeal.chatbot.service.OpenAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpenAiService openAiService;

    @Autowired
    private ObjectMapper objectMapper;

    private ChatRequest chatRequest;
    private ChatResponse chatResponse;

    @BeforeEach
    void setup() {
        chatRequest = new ChatRequest();
        chatRequest.setMessage("Hello AI");

        chatResponse = new ChatResponse("Hello human!");
    }

    @Test
    void testChatEndpoint_returnsChatResponse() throws Exception {
        when(openAiService.getChatResponse(anyString())).thenReturn(chatResponse);

        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Hello human!"));
    }
}
