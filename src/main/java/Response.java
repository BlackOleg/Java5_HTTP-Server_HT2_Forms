import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Response {

    private static final int BUFFER_SIZE = 1024;
    Request request;
    BufferedOutputStream output;


    HttpServer server;

    public Response(BufferedOutputStream output) {
        this.output = output;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public HttpServer getServer() {
        return server;
    }

    public void setServer(HttpServer server) {
        this.server = server;
    }

    public void sendError404() throws IOException {
        String errorMessage = new StringBuilder()
                .append("HTTP/1.1 404 Method Not Found\r\n")
                .append("Content-Type: text/html\r\n")
                .append("Content-Length: 0\r\n")
                .append("\r\n")
                .toString();
        output.write(errorMessage.getBytes());
        output.flush();
    }

    public void sendGood200(String type, String length) throws IOException {
        String goodMessage = new StringBuilder()
                .append("HTTP/1.1 200 OK\r\n")
                .append("Content-Type: " + type + "\r\n")
                .append("Content-Length: " + length + "\r\n")
                .append("Connection: close\r\n")
                .append("\r\n")
                .toString();
        output.write(goodMessage.getBytes());
        output.flush();
    }


    public void handler() throws IOException {
        var methodMap = server.getHandlers().get(request.getMethod());
        if (methodMap == null) {
            sendError404();
            return;
        }
        var handler =  methodMap.get(request.getUri());
        if (handler == null) {
            sendError404();
            return;
        }

        handler.handle(request, output);

    }

}

