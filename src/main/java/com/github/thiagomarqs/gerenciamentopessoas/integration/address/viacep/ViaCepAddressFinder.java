package com.github.thiagomarqs.gerenciamentopessoas.integration.address.viacep;

import com.github.thiagomarqs.gerenciamentopessoas.integration.address.AddressFinder;
import com.github.thiagomarqs.gerenciamentopessoas.integration.address.AddressIntegrationResult;
import com.github.thiagomarqs.gerenciamentopessoas.mapper.AddressMapper;
import com.google.gson.Gson;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class ViaCepAddressFinder implements AddressFinder {

    @Value("${addressfinder.endpoint.viacep}")
    private String viaCepEndpoint;
    private HttpClient httpClient = HttpClient.newHttpClient();
    private Gson gson = new Gson();
    private AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);

    public ViaCepAddressFinder() {}

    public ViaCepAddressFinder(String viaCepEndpoint) {
        this.viaCepEndpoint = viaCepEndpoint;
        this.httpClient = httpClient;
        this.gson = gson;
        this.addressMapper = addressMapper;
    }

    @Override
    public AddressIntegrationResult findAddressByCep(String cep) {
        var request = buildRequestFromCep(cep);
        var response = getHttpResponse(request).body();
        var isInvalid = response.contains("\"erro\": true");

        if (isInvalid) {
            return AddressIntegrationResult.failure(List.of("Invalid Address"));
        }

        var viaCepAddressResponse = gson.fromJson(response, ViaCepAddressResponse.class);
        var addressResponse = addressMapper.viaCepAddressResponseToAddressIntegrationResponse(viaCepAddressResponse);
        return AddressIntegrationResult.success(addressResponse);
    }

    URI buildUriFromCep(String cep) {
        return URI.create(viaCepEndpoint + cep + "/json");
    }

    public HttpRequest buildRequestFromCep(String cep) {
        return HttpRequest.newBuilder()
                .uri(buildUriFromCep(cep))
                .GET()
                .build();
    }

    public HttpResponse<String> getHttpResponse(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Error while sending request", e);
        }
    }

}
