package com.github.thiagomarqs.gerenciamentopessoas.integration.address.viacep;

import com.github.thiagomarqs.gerenciamentopessoas.integration.address.AddressFinder;
import com.github.thiagomarqs.gerenciamentopessoas.integration.address.AddressResult;
import com.github.thiagomarqs.gerenciamentopessoas.mapper.AddressMapper;
import com.google.gson.Gson;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class ViaCepAddressFinder implements AddressFinder {

    @Value("${addressfinder.endpoint.viacep}")
    String viaCepEndpoint;
    HttpClient httpClient = HttpClient.newHttpClient();
    Gson gson = new Gson();
    AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);

    @Override
    public AddressResult findAddressByCep(String cep) {
        var request = buildRequestFromCep(cep);
        var response = getHttpResponse(request).body();
        var isInvalid = response.contains("\"erro\": true");

        if (isInvalid) {
            throw new RuntimeException("Invalid CEP");
        }

        var viaCepAddressResult = gson.fromJson(response, ViaCepAddressResult.class);
        return addressMapper.viaCepAddressResultToAddressResult(viaCepAddressResult);
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
