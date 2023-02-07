package com.gateway.bugreport.buggrpcstatus.grpc;


import com.gateway.bugreport.ReactorSimpleGrpcServiceGrpc;
import com.gateway.bugreport.SimpleRequestMessage;
import com.gateway.bugreport.SimpleResponseMessage;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.lognet.springboot.grpc.GRpcService;
import reactor.core.publisher.Mono;

import static java.util.UUID.randomUUID;

@GRpcService
public class SimpleGrpcService extends ReactorSimpleGrpcServiceGrpc.SimpleGrpcServiceImplBase {

    @Override
    public Mono<SimpleResponseMessage> simpleGrpcRequest(Mono<SimpleRequestMessage> request) {
        return Mono.just(SimpleResponseMessage.newBuilder()
                .setId(randomUUID().toString())
                .setMessage("Pong")
                .build());
    }

    @Override
    public Mono<SimpleResponseMessage> errorProducingGrpcRequest(Mono<SimpleRequestMessage> request) {
        return Mono.error(new StatusRuntimeException(Status.INTERNAL));
    }
}
