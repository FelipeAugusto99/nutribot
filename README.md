# NutriBot API

Projeto base do **Diamante — Java Advanced (segundo semestre)**.

A NutriBot é a nutricionista virtual da Clínica Equilíbrio. O projeto base já tem o diário alimentar, a tabela de alimentos e o perfil do paciente. A equipe vai adicionar a parte de IA com **Spring AI**: chat com system prompt, memória de conversa, tool calling, RAG e structured output.

O enunciado completo e os critérios de avaliação estão no deck da atividade.

## Equipe

| Nome | RM |
|---|---|
| Felipe Augusto Lopes Ferreira | RM563982 |
| Kaique Mascarenhas dos Santos | RM565802 |

## Como rodar

Requisitos: Java 25 e Maven (ou o `mvnw` da sua IDE).

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. O console do H2 não está habilitado; os dados iniciais são carregados pelo `DataLoader` a cada inicialização.

O arquivo `test.http` tem exemplos de todas as rotas.

## Modelo usado

**Google Gemini `gemini-2.5-flash`** (chat) e **`gemini-embedding-001`** (embeddings do RAG), pelo endpoint compatível com a OpenAI.

## Configurando o modelo

O projeto usa o starter da OpenAI do Spring AI, que funciona com **qualquer API compatível com a da OpenAI**. A configuração é feita por variáveis de ambiente — **nunca coloque a chave no código ou no `application.yml`**, o repositório é público.

| Variável | Padrão | Para que serve |
|---|---|---|
| `OPENAI_API_KEY` | — | Chave da API (obrigatória) |
| `OPENAI_BASE_URL` | `https://generativelanguage.googleapis.com/v1beta/openai` | Endereço-base da API |
| `OPENAI_MODEL` | `gemini-2.5-flash` | Modelo de chat |
| `OPENAI_EMBEDDING_MODEL` | `gemini-embedding-001` | Modelo de embeddings (RAG) |

> No Spring AI 2.0 o starter usa o SDK oficial da OpenAI, que acrescenta `/chat/completions` e `/embeddings` ao `OPENAI_BASE_URL` sozinho; por isso as antigas variáveis `OPENAI_COMPLETIONS_PATH` e `OPENAI_EMBEDDINGS_PATH` não são mais necessárias.

Exemplos:

```bash
# Google Gemini (padrão do projeto): só a chave do AI Studio
export OPENAI_API_KEY=sua-chave-do-ai-studio

# OpenAI
export OPENAI_API_KEY=sk-...
export OPENAI_BASE_URL=https://api.openai.com/v1
export OPENAI_MODEL=gpt-4o-mini
export OPENAI_EMBEDDING_MODEL=text-embedding-3-small

# Ollama local (ollama pull llama3.2 && ollama pull nomic-embed-text)
export OPENAI_API_KEY=ollama
export OPENAI_BASE_URL=http://localhost:11434/v1
export OPENAI_MODEL=llama3.2
export OPENAI_EMBEDDING_MODEL=nomic-embed-text
```

No IntelliJ, as variáveis podem ser definidas em *Run → Edit Configurations → Environment variables*.

## O que já está pronto

| Método | Rota | Descrição |
|---|---|---|
| GET | `/foods?name=` | Busca na tabela de alimentos (valores por 100 g) |
| GET | `/foods/{id}` | Um alimento |
| GET | `/meals?date=` | Refeições do dia (sem `date`, hoje) |
| POST | `/meals` | Registra uma refeição `{date?, mealType, foodId, grams}` → 201 |
| DELETE | `/meals/{id}` | Remove uma refeição → 204 |
| GET | `/summary?date=` | Resumo do dia: calorias, macros, meta e o que falta |
| GET | `/profile` | Perfil do paciente |
| PUT | `/profile` | Atualiza `{name, goal, dailyCalories}` |

- `mealType`: `BREAKFAST`, `LUNCH`, `DINNER` ou `SNACK`.
- `goal`: `LOSE_WEIGHT`, `MAINTAIN` ou `GAIN_MUSCLE`.
- Erros sempre no formato `{"error": "..."}`.

Os serviços (`FoodService`, `MealService`, `ProfileService`) já têm os métodos que as tools vão precisar, como `FoodService.findByName`, `MealService.log` e `MealService.summary`.

## Base de conhecimento (RAG)

Os documentos da clínica estão em `src/main/resources/docs/`:

- `guia-alimentar.md` — orientações nutricionais da Clínica Equilíbrio
- `regras-atendimento.md` — o que o NutriBot pode e não pode fazer, horários e contato

Eles devem ser carregados no vector store na inicialização. A escolha do vector store é da equipe.

## Funcionalidades de IA implementadas

| Método | Rota | Corpo | Resposta |
|---|---|---|---|
| POST | `/chat` | `{"conversationId":"app-123","message":"Quantas calorias tem 100 g de tapioca?"}` | `200 {"answer":"..."}` · `400 {"error":"..."}` |
| POST | `/meals/analyze` | `{"description":"arroz, feijão e um bife"}` | `200 {"items":[{"food","grams","calories"}],"totalCalories","protein","carbs","fat"}` · `400` |

Onde está cada requisito:

| Requisito | Implementação |
|---|---|
| Chat e system prompt | `config/AiConfig.java` — `ChatClient` montado uma vez com o builder (`defaultSystem`) com a persona NutriBot da Clínica Equilíbrio: responde em português, recusa o que não é nutrição, não diagnostica nem prescreve dieta clínica e lembra que não substitui o nutricionista. `controller/ChatController.java` expõe o `POST /chat`. |
| Memória | `ChatMemory` (`MessageWindowChatMemory`, últimas 20 mensagens) registrada no `ChatClient` com o `MessageChatMemoryAdvisor`; o `conversationId` da requisição vai em `ChatMemory.CONVERSATION_ID`, então cada conversa tem o seu histórico. |
| Tool calling | `ai/NutriBotTools.java` — `buscarAlimento` (`FoodService.search`), `resumoDoDia` (`MealService.summary`, o mesmo do `/summary`) e `registrarRefeicao` (`MealService.log`, grava no diário), com `@Tool` e registradas com `defaultTools`. |
| RAG | `config/RagConfig.java` — na subida, lê `resources/docs/*.md`, divide por seção e grava num `SimpleVectorStore` (em memória) com os embeddings do modelo; o `QuestionAnswerAdvisor` liga o vector store ao `/chat`. |
| Structured output | `controller/MealAnalysisController.java` — `.entity(MealAnalysisResponse.class)` preenche os records `MealAnalysisResponse`/`Item` (números como `double`), sem parse manual. Usa um `ChatClient` separado, sem tools nem memória, então nada é gravado no diário. |

O arquivo `test.http` tem exemplos de todas as rotas, incluindo os testes do enunciado (memória, tools, RAG e structured output).

O app de testes está em https://github.com/joaocarloslima/nutribot-app.
