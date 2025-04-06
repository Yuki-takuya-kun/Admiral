package io.github.admiral.config;

import io.github.admiral.hr.*;
import io.github.admiral.hr.standalone.SimpleHR;
import io.github.admiral.hr.standalone.SimpleHRClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceCenterConfig {

    @Bean
    //@ConditionalOnProperty(prefix = "service_center", havingValue = "nacos")
    public PersonnelMinistry serviceCenter() {
        SimpleHR hr = new SimpleHR(null);
        SimpleTroopFactory tf = new SimpleTroopFactory(hr);
        return new PersonnelMinistry(hr, tf);
    }

    @Bean
    public HumanResource humanResource() {
        return new SimpleHR(new RandomLoadBalancer());
    }

    @Bean
    public HumanResourceClient humanResourceClient() {
        return new SimpleHRClient();
    }
}
