package br.com.dio.barber_shop_api.unitTests.exception;


import br.com.dio.barber_shop_api.exception.GlobalExceptionHandler;
import br.com.dio.barber_shop_api.exception.custom.EmailAlreadyUsedException;
import br.com.dio.barber_shop_api.exception.custom.ResourceNotFoundException;
import br.com.dio.barber_shop_api.exception.custom.ScheduleConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import jakarta.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.time.OffsetDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;



public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    /**
     * Deve retornar status 404 para exceções de recurso não encontrado
     */
    @Test
    void handleNotFound_ShouldReturn404() {
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(
                new ResourceNotFoundException("Cliente não encontrado"));

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Cliente não encontrado", response.getBody().get("message"));
    }

    /**
     * Deve retornar status 409 para email duplicado
     */
    @Test
    void handleEmailInUse_ShouldReturn409() {
        ResponseEntity<Map<String, Object>> response = handler.handleEmailInUse(
                new EmailAlreadyUsedException("Email já em uso"));

        assertEquals(409, response.getStatusCode().value());
        assertEquals("Email já em uso", response.getBody().get("message"));
    }

    /**
     * Deve retornar status 409 para conflito de agendamento
     */
    @Test
    void handleScheduleConflict_ShouldReturn409() {
        ResponseEntity<Map<String, Object>> response = handler.handleScheduleConflict(
                new ScheduleConflictException("Horário ocupado"));

        assertEquals(409, response.getStatusCode().value());
        assertEquals("Horário ocupado", response.getBody().get("message"));
    }

    /**
     * Deve retornar status 400 para ConstraintViolation
     */
    @Test
    void handleConstraintViolation_ShouldReturn400() {
        ConstraintViolationException ex = new ConstraintViolationException("Campo inválido", null);
        ResponseEntity<Map<String, Object>> response = handler.handleConstraintViolation(ex);

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().get("message").toString().contains("Campo inválido"));
    }

    /**
     * Deve retornar status 403 para erro de acesso negado
     */
    @Test
    void handleAccessDenied_ShouldReturn403() {
        ResponseEntity<Map<String, Object>> response = handler.handleAccessDenied(new AccessDeniedException("Sem permissão"));

        assertEquals(403, response.getStatusCode().value());
        assertEquals("Acesso negado", response.getBody().get("message"));
    }

    /**
     * Deve retornar status 500 para erro inesperado
     */
    @Test
    void handleGeneric_ShouldReturn500() {
        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(new RuntimeException("Erro interno"));

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Erro interno inesperado. Tente novamente.", response.getBody().get("message"));
        assertTrue(response.getBody().get("timestamp") instanceof OffsetDateTime);
    }
}
