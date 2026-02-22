package com.blob;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.ClientAuth;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * SSL configuration for HTTPS server using netty-tcnative (OpenSSL).
 */
public class SslConfig {

    private final SslContext sslContext;

    private SslConfig(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    public SslContext getSslContext() {
        return sslContext;
    }

    /**
     * Creates SSL context from a PKCS12 or JKS key store.
     *
     * @param keyStorePath     Path to the key store file
     * @param keyStorePassword Password for the key store
     * @param keyPassword      Password for the private key (can be same as keyStorePassword)
     * @return SslConfig instance
     */
    public static SslConfig fromKeyStore(String keyStorePath, String keyStorePassword, String keyPassword) 
            throws Exception {
        
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream fis = new FileInputStream(keyStorePath)) {
            keyStore.load(fis, keyStorePassword.toCharArray());
        }

        // Initialize KeyManagerFactory
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
            KeyManagerFactory.getDefaultAlgorithm()
        );
        keyManagerFactory.init(keyStore, keyPassword.toCharArray());

        // Build SSL context using netty-tcnative (OpenSSL)
        SslContext sslContext = SslContextBuilder.forServer(keyManagerFactory)
            .sslProvider(io.netty.handler.ssl.SslProvider.OPENSSL)
            .clientAuth(ClientAuth.NONE)
            .build();

        return new SslConfig(sslContext);
    }

    /**
     * Creates SSL context with both key and trust stores for mutual TLS (optional).
     */
    public static SslConfig fromKeyStoreWithTrust(
            String keyStorePath, 
            String keyStorePassword, 
            String keyPassword,
            String trustStorePath,
            String trustStorePassword) throws Exception {
        
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream fis = new FileInputStream(keyStorePath)) {
            keyStore.load(fis, keyStorePassword.toCharArray());
        }

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream fis = new FileInputStream(trustStorePath)) {
            trustStore.load(fis, trustStorePassword.toCharArray());
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
            KeyManagerFactory.getDefaultAlgorithm()
        );
        keyManagerFactory.init(keyStore, keyPassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        );
        trustManagerFactory.init(trustStore);

        SslContext sslContext = SslContextBuilder.forServer(keyManagerFactory)
            .sslProvider(io.netty.handler.ssl.SslProvider.OPENSSL)
            .trustManager(trustManagerFactory)
            .clientAuth(ClientAuth.REQUIRE)
            .build();

        return new SslConfig(sslContext);
    }

    /**
     * Returns the default SSL context (useful for checking if OpenSSL is loaded).
     */
    public static String getNettyVersionInfo() {
        return "Netty: " + io.netty.util.Version.identify();
    }
}
