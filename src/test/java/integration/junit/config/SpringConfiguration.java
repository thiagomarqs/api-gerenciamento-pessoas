package integration.junit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static integration.junit.config.TestContainersConfiguration.wireMockContainer;

@Configuration
public class SpringConfiguration {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("addressfinder.endpoint.viacep",
                () -> "http://" + wireMockContainer.getHost() + ":" + wireMockContainer.getMappedPort(8080) + "/");
    }

}
