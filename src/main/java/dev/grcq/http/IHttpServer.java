package dev.grcq.http;

import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface IHttpServer {

    void start();

    @NotNull
    List<IHttpRequest> getRequestPaths();

    void handle(HttpExchange exchange);

}
