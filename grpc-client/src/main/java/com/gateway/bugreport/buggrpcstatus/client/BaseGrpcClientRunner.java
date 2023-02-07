package com.gateway.bugreport.buggrpcstatus.client;

import com.gateway.bugreport.ReactorSimpleGrpcServiceGrpc;
import com.gateway.bugreport.buggrpcstatus.BugCloudGatewayGrpcClient;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;

public abstract class BaseGrpcClientRunner implements CommandLineRunner {

    protected static final Logger LOG = LogManager.getLogger(BugCloudGatewayGrpcClient.class);

    private final GrpcClientProperties properties;

    private final Resource certChain;

    private final Resource privateKey;

    public BaseGrpcClientRunner(GrpcClientProperties properties,
                                @Value("${classpath:localhost.crt}") Resource certChain,
                                @Value("${classpath:localhost.key}") Resource privateKey) {
        this.properties = properties;
        this.certChain = certChain;
        this.privateKey = privateKey;
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("Making request to host {} and port {}", properties.host(), properties.port());

        final var channelBuilder = ManagedChannelBuilder.forAddress(properties.host(), properties.port());
        final var channel = ((NettyChannelBuilder) channelBuilder)
                .useTransportSecurity()
                .sslContext(GrpcSslContexts.forClient()
                        .trustManager(certChain.getFile())
                        .keyManager(certChain.getFile(), privateKey.getFile())
                        .build())
                .build();

        final var client = ReactorSimpleGrpcServiceGrpc.newReactorStub(channel);

        runRequest(client);
    }

    protected abstract void runRequest(ReactorSimpleGrpcServiceGrpc.ReactorSimpleGrpcServiceStub client);
}
