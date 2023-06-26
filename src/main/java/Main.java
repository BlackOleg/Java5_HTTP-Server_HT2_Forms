

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {

    public static void main(String[] args) {
        final var server = new HttpServer();
        server.addHandler("GET", "/messages", (request, responseStream) -> {
            String hello = "Hello World! This is Get!";
            String message = new StringBuilder()
                    .append("HTTP/1.1 200 OK\r\n")
                    .append("Content-Type: " + "text/plain" + "\r\n")
                    .append("Content-Length: " + hello.length() + "\r\n")
                    .append("Connection: close\r\n")
                    .append("\r\n")
                    .toString();
            responseStream.write(message.getBytes());
            responseStream.write(hello.getBytes());
        });
        server.addHandler("POST", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                String hello = "Hello World! This is Post!";
                String message = new StringBuilder()
                        .append("HTTP/1.1 200 OK\r\n")
                        .append("Content-Type: " + "text/plain" + "\r\n")
                        .append("Content-Length: " + hello.length() + "\r\n")
                        .append("Connection: close\r\n")
                        .append("\r\n")
                        .toString();
                responseStream.write(message.getBytes());
                responseStream.write(hello.getBytes());
                //output.flush();
            }
        });

        server.go();
    }
}


