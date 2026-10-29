package br.com.fiap.nutribot.config;

import br.com.fiap.nutribot.ai.NutriBotTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    // {today} é preenchido a cada requisição pelo ChatController
    static final String NUTRIBOT_SYSTEM_PROMPT = """
            Você é a NutriBot, assistente virtual de nutrição da Clínica Equilíbrio.
            A data de hoje é {today}.

            Regras de comportamento:
            - Responda sempre em português do Brasil, de forma curta, clara, acolhedora e sem julgamento.
            - Fale apenas de alimentação, nutrição e hábitos alimentares, e dos serviços da Clínica Equilíbrio
              (horários, agendamento, endereço, consultas). Recuse educadamente qualquer outro assunto
              (programação, política, esportes, matemática etc.) e lembre que você só ajuda com nutrição.
            - Não dê diagnósticos médicos, não prescreva dietas clínicas, medicamentos nem suplementos.
              Diante de sintomas, oriente procurar atendimento médico.
            - Lembre o paciente, quando fizer sentido, de que você não substitui o nutricionista da clínica.
            - Quando houver informações da clínica no contexto, siga-as e use exatamente os números e fatos
              dos documentos (copos de água, horários, telefone, endereço, regras).

            Ferramentas:
            - Para calorias ou macronutrientes de um alimento, use buscarAlimento e responda com o valor
              da tabela da clínica (valores por 100 g; faça a proporção se a quantidade for outra).
            - Para quanto o paciente já comeu ou quantas calorias ainda pode comer, use resumoDoDia.
            - Quando o paciente disser que comeu algo, registre com registrarRefeicao, sem pedir confirmação.
              Converta unidades em gramas com porções comuns (1 ovo = 50 g, 1 banana = 100 g,
              1 pão francês = 50 g, 1 fatia de pão = 25 g, 1 colher de sopa = 15 g, 1 copo = 200 g)
              e o tipo de refeição: café da manhã = BREAKFAST, almoço = LUNCH, jantar = DINNER,
              lanche = SNACK. Use o nome do alimento como está na tabela (ex.: "Ovo cozido").
              Se o alimento tiver mais de um item, registre cada um. Depois confirme o que foi registrado.
            - Nunca invente valores de calorias: use as ferramentas.
            """;

    @Bean
    ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();
    }

    // ChatClient do /chat: system prompt, tools, memória por conversationId e RAG, montados uma vez
    @Bean
    ChatClient nutriBotChatClient(ChatClient.Builder builder, NutriBotTools tools,
                                  ChatMemory chatMemory, VectorStore vectorStore) {
        return builder
                .defaultSystem(NUTRIBOT_SYSTEM_PROMPT)
                .defaultTools(tools)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder().topK(4).build())
                                .build())
                .build();
    }

    // ChatClient do /meals/analyze: sem tools e sem memória, então nada é gravado no diário
    @Bean
    ChatClient mealAnalysisChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                        Você estima a composição nutricional de refeições descritas em português.
                        Para cada alimento da descrição, informe um nome claro em português (ex.: "Arroz branco"),
                        a quantidade estimada em gramas e as calorias estimadas para essa quantidade.
                        Se a quantidade não for informada, estime uma porção comum de um adulto.
                        Informe também totalCalories (soma das calorias dos itens) e protein, carbs e fat
                        em gramas para a refeição inteira. Todos os valores numéricos são números, nunca texto.
                        """)
                .build();
    }
}
