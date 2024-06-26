import request.HttpRequest;
import response.ContentType;
import response.HttpResponse;
import response.ResponseCode;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private final int port;
    private final ExecutorService executorService;

    public HttpServer(final int port, final int concurrencyLevel) {
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(concurrencyLevel);
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            // Not shutdown enabled
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleRequest(clientSocket));

            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    private void handleRequest(Socket clientSocket) {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             final OutputStream outputStream = clientSocket.getOutputStream())
        {
            final HttpRequest request = HttpRequest.from(bufferedReader);

            HttpResponse response;
            if (request.getPath().equals("/")) {
                response = new HttpResponse.Builder()
                        .withResponseCode(ResponseCode.OK)
                        .build();
            } else if (request.getPath().equals("/user-agent")) {
                response = new HttpResponse.Builder()
                        .withResponseCode(ResponseCode.OK)
                        .withContentType(ContentType.TEXT_PLAIN)
                        .body(request.headers().get("User-Agent"))
                        .build();
            } else if (request.getPath().contains("/echo/")) {
                final String param = request.getPath().split("/echo/")[1];

                response = new HttpResponse.Builder()
                        .withResponseCode(ResponseCode.OK)
                        .withContentType(ContentType.TEXT_PLAIN)
                        .body(param)
                        .build();
            } else {
                response = new HttpResponse.Builder()
                        .withResponseCode(ResponseCode.NOT_FOUND)
                        .build();
            }

            outputStream.write(response.serialize());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
