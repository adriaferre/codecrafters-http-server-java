import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
     ServerSocket serverSocket = null;
     Socket clientSocket = null;

     try {
         serverSocket = new ServerSocket(4221);
         serverSocket.setReuseAddress(true);
         clientSocket = serverSocket.accept(); // Wait for connection from client.

         handleConnection(clientSocket);

     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }

  private static void handleConnection(final Socket clientSocket) {
      try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
           OutputStream outputStream = clientSocket.getOutputStream())
      {
          final String request = bufferedReader.readLine();
          String path = request.split(" ")[1];

          if ("/".equals(path)) {
              successfulResponse(outputStream);
          } else if (path.contains("/echo/")) {
              final String response = path.split("/echo/")[1];
              outputStream.write("HTTP/1.1 200 OK\r\n".getBytes());
              outputStream.write("Content-Type: text/plain\r\n".getBytes());
              outputStream.write(("Content-Length: " + response.length() + "\r\n").getBytes());
              outputStream.write("\r\n".getBytes());
              outputStream.write(response.getBytes());
          } else {
              notFoundResponse(outputStream);
          }

          outputStream.flush();
      } catch (IOException e) {
          System.out.println("exception occurred " + e.getMessage());
          throw new RuntimeException(e);
      }
  }

  private static void successfulResponse(final OutputStream outputStream) throws IOException {
      outputStream.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
  }

  private static void notFoundResponse(final OutputStream outputStream) throws IOException {
      outputStream.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
  }
}
