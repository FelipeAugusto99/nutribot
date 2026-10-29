package br.com.fiap.nutribot.controller;

import java.time.LocalDate;

import br.com.fiap.nutribot.ai.ChatRequest;
import br.com.fiap.nutribot.ai.ChatResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

// Chat com a NutriBot: system prompt, memória por conversationId, tools e RAG vêm do ChatClient (AiConfig)
@RestController
@RequestMapping("/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final ChatClient chatClient;

    public ChatController(@Qualifier("nutriBotChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody @Valid ChatRequest request) {
        String answer;
        try {
            answer = chatClient.prompt()
                    .system(system -> system.param("today", LocalDate.now().toString()))
                    .user(request.message())
                    .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, request.conversationId().trim()))
                    .call()
                    .content();
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Falha ao chamar o modelo no /chat", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Não foi possível falar com o modelo de IA");
        }
        if (answer == null || answer.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "O modelo não retornou uma resposta");
        }
        return new ChatResponse(answer.trim());
    }
}
