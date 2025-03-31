package io.github.admiral.config;

import io.github.admiral.service.SimpleHR;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceCenterConfig {

    @Bean
    //@ConditionalOnProperty(prefix = "service_center", havingValue = "nacos")
    public SimpleHR serviceCenter() {
        return new SimpleHR();
    }
}
