package br.com.fiap.nutribot.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

// RAG: os documentos de resources/docs vão para um SimpleVectorStore (em memória) na subida da aplicação
@Configuration
public class RagConfig {

    private static final Logger log = LoggerFactory.getLogger(RagConfig.class);

    @Bean
    VectorStore vectorStore(EmbeddingModel embeddingModel) throws IOException {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();

        List<Document> documents = new ArrayList<>();
        Resource[] files = new PathMatchingResourcePatternResolver().getResources("classpath:docs/*.md");
        for (Resource file : files) {
            documents.addAll(splitBySection(file));
        }

        vectorStore.add(documents);
        log.info("RAG: {} trechos de {} documentos carregados no vector store", documents.size(), files.length);
        return vectorStore;
    }

    // cada seção "## ..." vira um trecho, com o título do documento junto para não perder o contexto
    private List<Document> splitBySection(Resource file) throws IOException {
        String text = file.getContentAsString(StandardCharsets.UTF_8);
        String[] parts = text.split("(?m)^(?=## )");
        String title = parts[0].lines().findFirst().orElse("").replace("#", "").trim();

        List<Document> chunks = new ArrayList<>();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            String content = part.startsWith("## ") ? title + "\n\n" + part.trim() : part.trim();
            chunks.add(new Document(content, Map.of("source", file.getFilename())));
        }
        return chunks;
    }
}
