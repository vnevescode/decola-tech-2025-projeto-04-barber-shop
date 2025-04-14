package br.com.dio.barber_shop_api.unitTests.config;


import br.com.dio.barber_shop_api.config.LoggingFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.mockito.Mockito.*;


public class LoggingFilterTest {
    private LoggingFilter loggingFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        loggingFilter = new LoggingFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    /**
     * 🧪 Deve logar corretamente método e URI da requisição
     */
    @Test
    void doFilter_ShouldLogMethodAndUri() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/clients");
        when(request.getQueryString()).thenReturn(null);
        when(response.getStatus()).thenReturn(200);
        when(request.getHeaderNames()).thenReturn(new java.util.Vector<String>().elements());

        // Act
        loggingFilter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * 🧪 Deve logar headers da requisição (nível debug, mas testamos execução)
     */
    @Test
    void doFilter_ShouldLogHeaders() throws ServletException, IOException {
        var headers = new java.util.Vector<String>();
        headers.add("User-Agent");
        headers.add("Authorization");

        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/schedules");
        when(request.getQueryString()).thenReturn("page=1");
        when(request.getHeaderNames()).thenReturn(headers.elements());
        when(request.getHeader("User-Agent")).thenReturn("JUnit-Test");
        when(request.getHeader("Authorization")).thenReturn("Bearer test123");
        when(response.getStatus()).thenReturn(201);

        loggingFilter.doFilter(request, response, filterChain);

        verify(request, atLeastOnce()).getHeader(anyString());
        verify(filterChain).doFilter(request, response);
    }

    /**
     * 🧪 Deve registrar corretamente o status da resposta
     */
    @Test
    void doFilter_ShouldLogResponseStatus() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn("/clients/1");
        when(request.getQueryString()).thenReturn(null);
        when(request.getHeaderNames()).thenReturn(new java.util.Vector<String>().elements());
        when(response.getStatus()).thenReturn(204); // No Content

        loggingFilter.doFilter(request, response, filterChain);

        verify(response).getStatus();
        verify(filterChain).doFilter(request, response);
    }

}
