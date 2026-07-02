package integration.junit.config;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@Configuration
public class TestContainersConfiguration {

    @Container
    public static GenericContainer<?> wireMockContainer = new GenericContainer<>("wiremock/wiremock:3.9.1")
            .withExposedPorts(8080);

    static {
        wireMockContainer.start();
    }

}
