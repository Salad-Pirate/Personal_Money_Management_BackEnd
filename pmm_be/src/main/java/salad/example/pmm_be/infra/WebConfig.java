package salad.example.pmm_be.infra;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // ครอบคลุมทุก endpoint
                .allowedOrigins("*") // อนุญาตทุก origin (ทุก port)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // method ที่อนุญาต
                .allowedHeaders("*"); // header ที่อนุญาต
    }
}