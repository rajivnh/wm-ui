package com.hcl.exception;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		ServerHttpResponse httpResponse = exchange.getResponse();
		
		setResponseStatus(httpResponse, ex);
		
		return httpResponse.writeWith(Mono.fromSupplier(() -> {
			DataBufferFactory bufferFactory = httpResponse.bufferFactory();
			
			try {
				String errMsgToSend = (httpResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) ? "" : ex.getMessage().substring(ex.getMessage().indexOf("{"));
				
				return bufferFactory.wrap(errMsgToSend.getBytes());
			} catch(Exception e) {
				return bufferFactory.wrap(new byte[0]);
			}
		}));
	}

	private void setResponseStatus(ServerHttpResponse httpResponse, Throwable ex) {
		httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
	}
}