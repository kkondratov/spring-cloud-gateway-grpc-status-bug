# spring-cloud-gateway-grpc-status-bug

This repository is an example that describes a bug in spring-cloud-gateway when using grpc and a service returns grpc errors.
Whenever gRPC responses return a status that is results in an error (grpc-status != 0) then gateway does not reply properly.


## Project structure
This project contains three main modules:
- Root: spring-cloud-gateway
- grpc-server
- grpc-client

### spring-cloud-gateway
This is an implementation of gateway to reproduce the buggy behaviour.
The gateway is configured to route traffic for GRPC requests based on the header to localhost:8082

### gRPC server
Simple gRPC server that servers one endpoint to pong a request

### gRPC client
Simple gRPC client with two configs:
- `direct` this profile will directly do calls to the gRPC server
- `gateway` this profile will call the gRPC server via the gateway.

The client has two command line runners
- One that runs an OK request, this will result in an OK "Pong" response from the gRPC service.
- One that runs a request which will result in an error being returned by the server

## How to reproduce the bug

1. Build the project
    `./gradlew clean build`

2. Start the gateway and the grpc server
    `./gradlew :bootRun`
    `./gradlew :grpc-server:bootRun`

3. Run the client with direct OK profile to validate grpc server running:
    `./gradlew :grpc-client:bootRunDirectOk`. You should see a log with `message: "Pong"`
 
4. Run the client with direct Error profile to validate grpc server running and returning an error result:
    `./gradlew :grpc-client:bootRunDirectError`. The runner should throw an exception with root cause `io.grpc.StatusRuntimeException: INTERNAL`

5. Run the client with gateway OK to display correct behaviour
    `./gradlew :grpc-client:bootRunGatewayOk`. You should see a log with `message: "Pong"`

6. Run the client with gateway Error to display the buggy behaviour
    `./gradlew :grpc-client:bootRunGatewayError`. The runner will hang because gateway returns a "grpc-status" with value "0" while it should've returned the correct status.
    


## How to reproduce potential fix
1. Build the project
   `./gradlew clean build`

2. Start the gateway and the grpc server
   `./gradlew :bootRun`
   `./gradlew :grpc-server:bootRunWithPatchedFilter`
This will run the gateway with a patched filter that properly returns the "grpc-status" as a trailing header.

3. Run the client with direct OK profile to validate grpc server running:
   `./gradlew :grpc-client:bootRunDirectOk`. You should see a log with `message: "Pong"`

4. Run the client with direct Error profile to validate grpc server running and returning an error result:
   `./gradlew :grpc-client:bootRunDirectError`. The runner should throw an exception

5. Run the client with gateway OK to display correct behaviour
   `./gradlew :grpc-client:bootRunGatewayOk`. You should see a log with `message: "Pong"`

6. Run the client with gateway Error to display the buggy behaviour
   `./gradlew :grpc-client:bootRunGatewayError`. the runner will return a correct value 

The fix here is a `PatchedGRPCResponseHeaderFilter` that is run after the default `GRPCResponseHeadersFilter` which returns the `grpc-stats` properly.
   
## Bug Explanation
The current `GRPCResponseHeadersFilter` **always** returns the `grpc-status` trailing header with a value "0".
This is an `OK` result according to the gRPC protocol see: [grpc-status-codes](https://grpc.github.io/grpc/core/md_doc_statuscodes.html)

The `GRPCResponseHeadersFilter` should return a proper `grpc-status` which is copied from the request to properly handle requests that result in an error.