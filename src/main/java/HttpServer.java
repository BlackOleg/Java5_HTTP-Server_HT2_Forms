import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class HttpServer {
    private static final int PORT_DEFAULT=9999;
    private final int port;
    ExecutorService threadPoolExecutor;
    private static final int pool = 64;
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "public";
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
    private boolean shutdown = false;

    public ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> getHandlers() {
        return handlers;
    }

    private final  ConcurrentHashMap<String, ConcurrentHashMap<String,Handler>> handlers = new ConcurrentHashMap<>();

    public HttpServer() {
        this.port = PORT_DEFAULT;
    }

    public HttpServer(int port) {
        this.port = port;
    }

    public void go() {
        threadPoolExecutor = Executors.newFixedThreadPool(pool);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port: " + port);
            // Loop waiting for a request
            while (true) {
                try {
                    final var socket = serverSocket.accept();
                    System.out.println("New client connected");
                    // instead of - new ServerThreads(socket).start();
                    threadPoolExecutor.execute(new ServerThreads(socket, this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            threadPoolExecutor.shutdown();
        }

    }

    public void addHandler(String method, String uri, Handler handler) {
        var methodMap = handlers.get(method);
        if (methodMap==null){
            methodMap=new ConcurrentHashMap<>();
            handlers.put(method,methodMap);
        }
        if (!methodMap.containsKey(uri)) {
            methodMap.put(uri,handler);
        }
    }
    public void topUpHandlers(){
        addHandler("GET", "/messages", (request, responseStream) -> {
            //String hello = "Hello World! This is Get!";
            String hello = "Hello World! This is Get with following params: " + request.getQueryParams()
                    .stream()
                    .map( n -> n.toString() )
                    .collect( Collectors.joining( "," ) );
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
        addHandler("POST", "/messages", new Handler() {
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
    }
}
