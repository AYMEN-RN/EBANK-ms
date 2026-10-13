package com.aymen.ebankbot.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;


@Service
public class EbankAIAgent {
        private ChatClient chatClient;

        public EbankAIAgent(
                ChatClient.Builder chatClient,
                ChatMemory chatMemory,
                ToolCallbackProvider tools) {
            this.chatClient = chatClient
                    .defaultSystem("""
                            Vous etes un assistant qui se charge de répondre aux question
                            de l'utilisateur s propos des clients et comptes bancaires, en fonction du contexte fourni.
                            Si aucun contexte n'est fourni, repond avec JE NE SAIS PAS.
                            """)
                    .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).
                    defaultTools(tools)
                    .build();
        }

        public String chat(
                String query,
                String conversationId) {
            return chatClient.prompt()
                    .user(query)
                    .advisors(advisorSpec -> advisorSpec.param(
                            ChatMemory.CONVERSATION_ID,
                            conversationId
                    )).call().content();
        }
    }
