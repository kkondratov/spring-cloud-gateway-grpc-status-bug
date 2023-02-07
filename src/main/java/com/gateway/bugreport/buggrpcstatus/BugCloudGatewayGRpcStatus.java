package com.gateway.bugreport.buggrpcstatus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.headers.GRPCResponseHeadersFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class BugCloudGatewayGRpcStatus {

	public static void main(String[] args) {
		SpringApplication.run(BugCloudGatewayGRpcStatus.class, args);
	}

	@Bean
	@Primary
	@Profile("patchedFilter")
	public GRPCResponseHeadersFilter patchedResponseFilter() {
		return new PatchedGRPCResponseHeaderFilter();
	}
}
