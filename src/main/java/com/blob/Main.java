package com.blob;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.PemTrustOptions;

public class Main {

    private static final int HTTPS_PORT = 8443;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);

        router.get("/").handler(ctx -> {
            ctx.response()
                .putHeader("content-type", "application/json")
                .end("{\"message\": \"Hello from Vert.x + Netty + TLS!\"}");
        });

        router.get("/health").handler(ctx -> {
            ctx.response()
                .putHeader("content-type", "application/json")
                .end("{\"status\": \"ok\", \"tls\": \"enabled\"}");
        });

        String keyPath = System.getProperty("key.path", "key.pem");
        String certPath = System.getProperty("cert.path", "cert.pem");

        HttpServerOptions options = new HttpServerOptions()
            .setSsl(true)
            .setPemKeyCertOptions(new PemKeyCertOptions()
                .setKeyPath(keyPath)
                .setCertPath(certPath))
            .setPort(HTTPS_PORT);

        HttpServer server = vertx.createHttpServer(options);

        server.connectionHandler(conn -> {
            System.out.println("New SSL connection: " + conn.remoteAddress());
        });

        server.requestHandler(router)
            .listen()
            .onSuccess(s -> {
                System.out.println("HTTPS Server started on port " + s.actualPort());
                System.out.println("Using Netty: " + io.netty.util.Version.identify());
                System.out.println("SSL Provider: OpenSSL (netty-tcnative)");
            })
            .onFailure(err -> {
                System.err.println("Failed to start HTTPS server: " + err.getMessage());
                err.printStackTrace();
            });


        // Register shutdown hook for graceful cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down gracefully...");
            server.close()
                .onComplete(ar -> {
                    if (ar.succeeded()) {
                        System.out.println("HTTPS server closed");
                    } else {
                        System.err.println("Failed to close HTTPS server: " + ar.cause().getMessage());
                    }
                    vertx.close()
                        .onComplete(ar2 -> {
                            if (ar2.succeeded()) {
                                System.out.println("Vert.x instance closed");
                            } else {
                                System.err.println("Failed to close Vert.x: " + ar2.cause().getMessage());
                            }
                        });
                });
        }));
    }
}
