package integration.cucumber.config;

import com.github.thiagomarqs.gerenciamentopessoas.Application;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class
)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class CucumberSpringConfiguration {
}
