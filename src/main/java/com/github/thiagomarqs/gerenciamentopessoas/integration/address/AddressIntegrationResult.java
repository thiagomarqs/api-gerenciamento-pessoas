package com.github.thiagomarqs.gerenciamentopessoas.integration.address;

import java.util.List;
import java.util.Objects;

public final class AddressIntegrationResult {

    private boolean isSuccessful;
    private List<String> errorMessages;
    private AddressIntegrationResponse response;

    public AddressIntegrationResult() {}

    public AddressIntegrationResult(boolean isSuccessful, List<String> errorMessages, AddressIntegrationResponse response) {
        this.isSuccessful = isSuccessful;
        this.errorMessages = errorMessages;
        this.response = response;
    }

    public static AddressIntegrationResult failure(List<String> errorMessages) {
        return builder()
                .isSuccessful(false)
                .errorMessages(errorMessages)
                .build();
    }

    public static AddressIntegrationResult success(AddressIntegrationResponse addressResponse) {
        return builder()
                .isSuccessful(true)
                .response(addressResponse)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    private static class Builder {

        private AddressIntegrationResult addressIntegrationResult = new AddressIntegrationResult();

        public Builder isSuccessful(boolean isSuccessful) {
            addressIntegrationResult.isSuccessful = isSuccessful;
            return this;
        }

        public Builder errorMessages(List<String> errorMessages) {
            addressIntegrationResult.errorMessages = errorMessages;
            return this;
        }

        public Builder response(AddressIntegrationResponse response) {
            addressIntegrationResult.response = response;
            return this;
        }

        public AddressIntegrationResult build() {
            return addressIntegrationResult;
        }

    }

    public boolean getIsSuccessful() {
        return isSuccessful;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public AddressIntegrationResponse getResponse() {
        return response;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AddressIntegrationResult) obj;
        return this.isSuccessful == that.isSuccessful &&
                Objects.equals(this.errorMessages, that.errorMessages) &&
                Objects.equals(this.response, that.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSuccessful, errorMessages, response);
    }

    @Override
    public String toString() {
        return "AddressResult[" +
                "isSuccessful=" + isSuccessful + ", " +
                "errorMessage=" + errorMessages + ", " +
                "response=" + response + ']';
    }
}