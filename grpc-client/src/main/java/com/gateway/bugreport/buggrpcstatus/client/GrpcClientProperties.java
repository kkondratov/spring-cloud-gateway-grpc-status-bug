package com.gateway.bugreport.buggrpcstatus.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "grpc.client")
public record GrpcClientProperties(String host, int port) {
}
