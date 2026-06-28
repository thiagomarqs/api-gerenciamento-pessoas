package integration.cucumber.config;

import com.github.thiagomarqs.gerenciamentopessoas.Application;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@CucumberContextConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class
)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
public class CucumberSpringConfiguration {

    @Container
    public static GenericContainer<?> wireMock = new GenericContainer<>("wiremock/wiremock:3.9.1")
            .withExposedPorts(8080)
            .withClasspathResourceMapping("wiremock", "/home/wiremock/mappings", BindMode.READ_ONLY);

    static {
        wireMock.start();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("addressfinder.endpoint.viacep",
                () -> "http://" + wireMock.getHost() + ":" + wireMock.getMappedPort(8080) + "/");
    }

}
