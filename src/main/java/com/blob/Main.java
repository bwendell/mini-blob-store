package com.blob;

import com.blob.model.ListObjectsResponse;
import com.blob.model.ObjectSummary;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.PemTrustOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final int HTTPS_PORT = 8443;

    // Stub object storage - in-memory list for demonstration
    // TODO: Replace with actual blob store implementation
    private static final List<ObjectSummary> STUB_OBJECTS = new ArrayList<>();

    static {
        STUB_OBJECTS.add(ObjectSummary.builder().name("logs/app.log").size(1024L).build());
        STUB_OBJECTS.add(ObjectSummary.builder().name("logs/error.log").size(512L).build());
        STUB_OBJECTS.add(ObjectSummary.builder().name("data/config.json").size(256L).build());
        STUB_OBJECTS.add(ObjectSummary.builder().name("images/logo.png").size(4096L).build());
    }

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

        // OCI-compatible ListObjects endpoint
        router.get("/o").handler(ctx -> {
            String prefix = ctx.queryParams().get("prefix");
            String limit = ctx.queryParams().get("limit");
            String startAfter = ctx.queryParams().get("startAfter");

            List<ObjectSummary> objects = listObjects(prefix, limit, startAfter);

            ListObjectsResponse response = ListObjectsResponse.builder()
                .objects(objects)
                .build();

            ctx.response()
                .putHeader("content-type", "application/json")
                .end(response.toJson().encode());
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

    /**
     * Lists objects in the blob store, optionally filtered by prefix.
     * Matches OCI Object Storage ListObjects API behavior.
     *
     * @param prefix optional prefix filter
     * @param limit optional limit (stubbed, not implemented)
     * @param startAfter optional pagination cursor (stubbed, not implemented)
     * @return list of matching ObjectSummary objects
     */
    private static List<ObjectSummary> listObjects(String prefix, String limit, String startAfter) {
        // TODO: Implement pagination with limit and startAfter parameters
        // TODO: Implement actual blob store query

        if (prefix == null || prefix.isEmpty()) {
            return new ArrayList<>(STUB_OBJECTS);
        }

        return STUB_OBJECTS.stream()
            .filter(obj -> obj.getName().startsWith(prefix))
            .collect(Collectors.toList());
    }
}
