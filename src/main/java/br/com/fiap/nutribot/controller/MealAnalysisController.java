package br.com.fiap.nutribot.controller;

import br.com.fiap.nutribot.ai.MealAnalysisRequest;
import br.com.fiap.nutribot.ai.MealAnalysisResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

// Estimativa de uma refeição com structured output; nada é gravado no diário
@RestController
@RequestMapping("/meals/analyze")
public class MealAnalysisController {

    private static final Logger log = LoggerFactory.getLogger(MealAnalysisController.class);

    private final ChatClient chatClient;

    public MealAnalysisController(@Qualifier("mealAnalysisChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping
    public MealAnalysisResponse analyze(@RequestBody @Valid MealAnalysisRequest request) {
        MealAnalysisResponse response;
        try {
            response = chatClient.prompt()
                    .user(request.description())
                    .call()
                    .entity(MealAnalysisResponse.class);
        } catch (Exception ex) {
            log.error("Falha ao chamar o modelo no /meals/analyze", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Não foi possível analisar a refeição");
        }
        if (response == null || response.items() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "O modelo não retornou uma estimativa");
        }
        return response;
    }
}
