package integration.cucumber.client;

import com.github.tomakehurst.wiremock.client.WireMock;

import static integration.cucumber.config.CucumberSpringConfiguration.wireMock;

public class WireMockClient {

    private static WireMock wireMockClient;

    public static WireMock getClient() {
        if(wireMockClient == null) {
            wireMockClient = new WireMock(wireMock.getHost(), wireMock.getMappedPort(8080));
        }
        return wireMockClient;
    }

}
