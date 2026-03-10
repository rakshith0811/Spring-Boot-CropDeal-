package com.cropdeal.chatbot.controller;
import com.cropdeal.chatbot.dto.ChatRequest;
import com.cropdeal.chatbot.dto.ChatResponse;
import com.cropdeal.chatbot.service.OpenAiService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/chat")
//@CrossOrigin(origins = "*") 
public class ChatController {
	 private final OpenAiService openAiService;

	    public ChatController(OpenAiService openAiService) {
	        this.openAiService = openAiService;
	    }

	    @PostMapping
	    public ChatResponse chat(@RequestBody ChatRequest request) {
	        return openAiService.getChatResponse(request.getMessage());
	    }
}
