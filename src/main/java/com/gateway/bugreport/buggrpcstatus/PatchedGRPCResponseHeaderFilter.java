package com.gateway.bugreport.buggrpcstatus;


import org.springframework.cloud.gateway.filter.headers.GRPCResponseHeadersFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.netty.http.server.HttpServerResponse;

public class PatchedGRPCResponseHeaderFilter extends GRPCResponseHeadersFilter {

    private static final String GRPC_STATUS_HEADER = "grpc-status";

    @Override
    public HttpHeaders filter(HttpHeaders headers, ServerWebExchange exchange) {
        if (isGRPC(exchange)) {
            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders responseHeaders = response.getHeaders();

            String trailerHeaderValue = getTrailerHeaderValue(responseHeaders);
            responseHeaders.set(HttpHeaders.TRAILER, trailerHeaderValue);

            String grpcStatus = getGrpcStatus(headers);

            // DEVELOPERS NOTE:
            // "grpc-status" needs to be set in trailers: https://github.com/spring-cloud/spring-cloud-gateway/issues/2679
            ((HttpServerResponse) ((AbstractServerHttpResponse) response).getNativeResponse())
                    .trailerHeaders(h -> h.set(GRPC_STATUS_HEADER, grpcStatus));
        }

        return headers;
    }

    private static String getTrailerHeaderValue(HttpHeaders responseHeaders) {
        String trailerHeaderValue = GRPC_STATUS_HEADER;
        String originalTrailerHeaderValue = responseHeaders.getFirst(HttpHeaders.TRAILER);

        if (originalTrailerHeaderValue != null) {
            trailerHeaderValue += "," + originalTrailerHeaderValue;
        }

        return trailerHeaderValue;
    }

    private String getGrpcStatus(HttpHeaders headers) {
        final var grpcStatusValue = headers.getFirst(GRPC_STATUS_HEADER);

        return StringUtils.hasText(grpcStatusValue) ? grpcStatusValue : "0";
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private boolean isGRPC(ServerWebExchange exchange) {
        String contentTypeValue = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.CONTENT_TYPE);

        return StringUtils.startsWithIgnoreCase(contentTypeValue, "application/grpc");
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
