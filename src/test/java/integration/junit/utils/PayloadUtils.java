package integration.junit.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PayloadUtils {

    public static String readResponsePayload(String responseFileName) throws IOException {
        return readPayloadFromPath(Paths.RESPONSE_PAYLOADS, responseFileName);
    }

    public static String readRequestPayload(String requestFileName) throws IOException {
        return readPayloadFromPath(Paths.REQUEST_PAYLOADS, requestFileName);
    }

    public static String readWireMockResponsePayload(String responseFileName) throws IOException {
        return readPayloadFromPath(Paths.WIREMOCK_RESPONSE_PAYLOADS, responseFileName);
    }

    private static String readPayloadFromPath(String path, String fileName) throws IOException {
        return Files.readString(Path.of(path + fileName));
    }

}