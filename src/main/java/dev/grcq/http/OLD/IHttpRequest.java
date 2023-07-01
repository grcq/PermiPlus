package dev.grcq.http.OLD;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public interface IHttpRequest {

    void handleRequest(HttpExchange exchange) throws IOException;

}
