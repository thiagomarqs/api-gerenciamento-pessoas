package integration.junit.config;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static integration.junit.config.TestContainersConfiguration.wireMockContainer;

@Configuration
public class WireMockConfiguration {

    private WireMock wireMock;

    @Bean
    public WireMock wireMock() {
        if(wireMock == null) {
            wireMock = new WireMock(wireMockContainer.getHost(), wireMockContainer.getMappedPort(8080));
        }
        return wireMock;
    }
}
