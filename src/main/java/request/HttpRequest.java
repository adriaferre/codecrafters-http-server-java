package request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final Method method;
    private final String path;

    private HttpRequest(
            final Method method,
            final String path
    ) {
        this.method = method;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        String[] parts = bufferedReader.readLine().split(" ");
        Method method = switch (parts[0]) {
            case "GET" -> Method.GET;
            case "POST" -> Method.POST;
            default -> throw new IllegalArgumentException("Method not allowed");
        };

        return new HttpRequest(method, parts[1]);
    }
}