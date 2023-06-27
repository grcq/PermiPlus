package dev.grcq.http.impl;

import com.google.common.collect.Lists;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dev.grcq.http.*;
import dev.grcq.http.impl.requests.GetGroupRequest;
import dev.grcq.permiplus.PermiPlus;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermiHttpServer implements IHttpServer {

    private HttpServer server;

    private Map<String, IHttpRequest> requests;

    @SneakyThrows
    @Override
    public void start() {
        this.server = HttpServer.create(new InetSocketAddress(26383), 0);
        this.requests = new HashMap<>();

        getRequestPaths().forEach((request) -> {
            Path path = request.getClass().getDeclaredAnnotation(Path.class);
            if (path == null) return;

            requests.put(path.value(), request);

            HttpContext context = server.createContext(path.value());
            context.setHandler(this::handle);

        });

        server.start();
    }

    @Override
    public @NotNull List<IHttpRequest> getRequestPaths() {
        return Lists.newArrayList(new GetGroupRequest());
    }

    @Override
    public void handle(HttpExchange exchange) {
        IHttpRequest request = requests.get(exchange.getHttpContext().getPath());
        if (request == null) throw new RuntimeException("An unknown error has occurred, contact the developers!");

        Type type = request.getClass().getDeclaredAnnotation(Type.class);
        if (type == null) return;

        if (!type.value().toString().equalsIgnoreCase(exchange.getRequestMethod())) return;

        try {
            request.handleRequest(exchange);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
