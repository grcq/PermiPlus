package dev.grcq.http;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.util.function.Consumer;

public class HTTPServer {

    private final Javalin app;

    private HTTPConfig config;

    public HTTPServer(int port) {
        this.config = HTTPConfig.builder().port(port).build();
        this.app = Javalin.create();
    }

    public void start() {
        app.start(config.getPort());
    }

    public HTTPServer get(String path, Consumer<Context> listener) {
        app.get(path, listener::accept);
        return this;
    }

    public HTTPServer post(String path, Consumer<Context> listener) {
        app.post(path, listener::accept);
        return this;
    }

}
