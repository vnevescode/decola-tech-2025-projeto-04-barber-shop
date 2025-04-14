package br.com.dio.barber_shop_api.unitTests.config;

import br.com.dio.barber_shop_api.config.RateLimitingFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;



public class RateLimitingFilterTest {

    private RateLimitingFilter filter;

    @BeforeEach
    void setUp() {
        filter = new RateLimitingFilter();
    }

    @AfterEach
    void tearDown() {
        filter.clearBucketsForTesting(); // ✅ Garante isolamento entre os testes
    }

    // Testa se requisições dentro do limite são permitidas normalmente
    @Test
    void doFilter_WhenWithinLimit_ShouldAllowRequest() throws IOException, ServletException {
        HttpServletRequest request = createRequest("127.0.0.1", "/test");
        HttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // Executa dentro do limite (5 vezes)
        for (int i = 0; i < 5; i++) {
            chain = new MockFilterChain();
            filter.doFilter(request, response, chain);
            assertEquals(200, ((MockHttpServletResponse) response).getStatus());
        }
    }

    // Testa se a sexta requisição do mesmo IP é bloqueada
    /*@Test
    void doFilter_WhenLimitExceeded_ShouldBlockRequest() throws IOException, ServletException {
        HttpServletRequest request = createRequest("192.168.1.1", "/secure");

        // Realiza 5 requisições válidas (dentro do limite)
        for (int i = 0; i < 5; i++) {
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(request, response, new MockFilterChain());
            assertEquals(200, response.getStatus(), "Esperado status 200 na requisição " + (i + 1));
        }

        // 6ª requisição deve ser bloqueada
        MockHttpServletResponse finalResponse = new MockHttpServletResponse();
        filter.doFilter(request, finalResponse, new MockFilterChain());

        assertEquals(429, finalResponse.getStatus());
        assertEquals("Rate limit exceeded. Try again later.", finalResponse.getContentAsString());
    }*/

    // Testa se requisições para /swagger-ui ou /v3/api-docs são sempre permitidas
    @Test
    void doFilter_WhenPathIsSwagger_ShouldBypassRateLimit() throws IOException, ServletException {
        HttpServletRequest request = createRequest("10.10.10.10", "/swagger-ui/index.html");
        HttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        for (int i = 0; i < 10; i++) {
            chain = new MockFilterChain();
            filter.doFilter(request, response, chain);
            assertEquals(200, ((MockHttpServletResponse) response).getStatus());
        }
    }

    // Testa se IPs diferentes possuem contadores de limite independentes
    /*@Test
    void doFilter_WithDifferentIPs_ShouldHaveSeparateLimits() throws IOException, ServletException {
        String[] ips = {"1.1.1.1", "2.2.2.2"};

        for (String ip : ips) {
            HttpServletRequest request = createRequest(ip, "/api/resource");

            // 5 requisições válidas
            for (int i = 0; i < 5; i++) {
                MockHttpServletResponse response = new MockHttpServletResponse();
                filter.doFilter(request, response, new MockFilterChain());
                assertEquals(200, response.getStatus(), "Esperado status 200 na requisição " + (i + 1) + " do IP " + ip);
            }

            // 6ª requisição deve ser bloqueada
            MockHttpServletResponse finalResponse = new MockHttpServletResponse();
            filter.doFilter(request, finalResponse, new MockFilterChain());

            assertEquals(429, finalResponse.getStatus(), "Esperado 429 para IP " + ip + " após exceder o limite");
        }
    }*/

    private HttpServletRequest createRequest(String ip, String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(ip);
        request.setRequestURI(uri);
        return request;
    }

}
