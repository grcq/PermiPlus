package dev.grcq.http.OLD;

import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IHttpServer {

    void start();

    @NotNull
    List<IHttpRequest> getRequestPaths();

    void handle(HttpExchange exchange);

}
