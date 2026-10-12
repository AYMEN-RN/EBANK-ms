package com.aymen.ebankbot.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EbankChatbotController {
    private ChatClient chatClient;

    public EbankChatbotController(ChatClient.Builder chatClient, ChatMemory chatMemory) {
        this.chatClient = chatClient
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
    @GetMapping("/chat")
    public String chat(
            @RequestParam(name = "query", defaultValue = "Bonjour")
            String query,
            @RequestParam(name = "conversationId", defaultValue = "user-1")
            String conversationId) {
        return chatClient.prompt()
                .user(query)
                .advisors(advisorSpec -> advisorSpec.param(
                        ChatMemory.CONVERSATION_ID,
                        conversationId
        )).call().content();
    }
}
