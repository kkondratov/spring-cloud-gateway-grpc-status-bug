package com.gateway.bugreport.buggrpcstatus.client;

import com.gateway.bugreport.ReactorSimpleGrpcServiceGrpc;
import com.gateway.bugreport.SimpleRequestMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import static java.util.UUID.randomUUID;

@Component
@EnableConfigurationProperties(GrpcClientProperties.class)
@Profile("errorRequest")
public class GrpcErrorRequestClientRunner extends BaseGrpcClientRunner {

    public GrpcErrorRequestClientRunner(GrpcClientProperties properties,
                                        @Value("${classpath:localhost.crt}") Resource certChain,
                                        @Value("${classpath:localhost.key}") Resource privateKey) {
        super(properties, certChain, privateKey);
    }

    @Override
    protected void runRequest(ReactorSimpleGrpcServiceGrpc.ReactorSimpleGrpcServiceStub client) {
        final var response = client.errorProducingGrpcRequest(SimpleRequestMessage.newBuilder()
                .setId(randomUUID().toString())
                .setMessage("Ping")
                .build()).block();

        LOG.info("Response: {}", response);
    }
}
