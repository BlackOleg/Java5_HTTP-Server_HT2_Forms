import java.io.*;
import java.net.*;

public class ServerThreads extends Thread {
    private final Socket socket;
    private final HttpServer server;

    public ServerThreads(Socket socket, HttpServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream())) {
             String line = in.readLine();

            if (line != null) {
                System.out.println("Request is: " + line);
                Request request = new Request(line);

                request.getQueryParams().forEach(System.out::println);

                Response response = new Response(out);
                response.setRequest(request);
                response.setServer(server);
                response.handler();
            }
            //socket.close();
            System.out.println("Client disconnected.");
        } catch (IOException | URISyntaxException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

