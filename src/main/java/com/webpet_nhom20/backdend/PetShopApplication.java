package com.webpet_nhom20.backdend;

import com.webpet_nhom20.backdend.service.GeminiEmbeddingService;
import com.webpet_nhom20.backdend.service.QdrantService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
public class PetShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetShopApplication.class, args);
    }

    @Bean
    CommandLineRunner testEmbedding(GeminiEmbeddingService embeddingService) {
        return args -> {
            List<Float> vector =
                    embeddingService.embedText("Chó con nên ăn gì?");

            System.out.println("Vector size = " + vector.size());
        };
    }

    @Bean
    CommandLineRunner testQdrant(
            GeminiEmbeddingService embeddingService,
            QdrantService qdrantService
    ) {
        return args -> {
            var vector = embeddingService.embedText("Chó con nên ăn gì?");
            var contexts = qdrantService.searchTopContents(vector, 3);

            contexts.forEach(System.out::println);
        };
    }

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry
//                        .addMapping("/**")
//                        .allowedOrigins("*") // Có thể thay bằng domain cụ thể cho an toàn
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
//                        .allowedHeaders("*");
//            }
//        };
//    }
}
