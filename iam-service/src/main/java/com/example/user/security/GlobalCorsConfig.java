package com.example.user.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
//public class GlobalCorsConfig {
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry
//                    .addMapping("/**") // allow all paths
//                    .allowedOrigins(
//                        "http://localhost:3000",
//                        "https://healthcare-group-2.netlify.app",
//                        "https://netlify.khoa.email",
//                        "https://healthcare.khoa.email"
//                    ) // frontend URL
//                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                    .allowedHeaders("*")
//                    .allowCredentials(true);
//            }
//        };
//    }
//}

@Configuration
public class GlobalCorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**") // allow all paths
                        .allowedOrigins(
                                "http://localhost:3000",
                                "https://healthcare-group-2.netlify.app",
                                "https://netlify.khoa.email",
                                "https://healthcare.khoa.email",
                                "http://172.16.1.175:3000"
                        ) // frontend URL
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/upload/images/**")
                        .addResourceLocations("file:/Users/hoangnguyen/Downloads/healthcare/microservices/iam-service/src/main/java/com/example/user/upload/images/");
            }
        };
    }
}
