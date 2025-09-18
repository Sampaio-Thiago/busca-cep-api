package com.sampaiodev.buscacep.domain.exceptions;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ErrorResponse> handleCepNotFound(FeignException.NotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("CEP não encontrado", extractMessage(ex)));
    }

    @ExceptionHandler(FeignException.InternalServerError.class)
    public ResponseEntity<ErrorResponse> handleApiInternalError(FeignException.InternalServerError ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro interno na API ViaCEP", extractMessage(ex)));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleGenericFeignError(FeignException ex) {
        HttpStatus status = ex.getMessage().contains("Read timed out")
                ? HttpStatus.GATEWAY_TIMEOUT
                : HttpStatus.BAD_GATEWAY;

        String mensagem = status == HttpStatus.GATEWAY_TIMEOUT
                ? "Timeout ao consultar API ViaCEP"
                : "Erro ao consultar API ViaCEP";

        return ResponseEntity.status(status)
                .body(new ErrorResponse(mensagem, extractMessage(ex)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro inesperado", extractMessage(ex)));
    }

    private String extractMessage(Throwable ex) {
        return ex.getMessage() != null ? ex.getMessage() : ex.toString();
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiError(ExternalApiException ex) {
        HttpStatus status = switch (ex.getStatusCode()) {
            case 404 -> HttpStatus.NOT_FOUND;
            case 500 -> HttpStatus.INTERNAL_SERVER_ERROR;
            case 504, -1 -> HttpStatus.GATEWAY_TIMEOUT;
            default -> HttpStatus.BAD_GATEWAY;
        };

        return ResponseEntity.status(status)
                .body(new ErrorResponse("Erro externo: " + status.value(), ex.getMessage()));
    }
}
