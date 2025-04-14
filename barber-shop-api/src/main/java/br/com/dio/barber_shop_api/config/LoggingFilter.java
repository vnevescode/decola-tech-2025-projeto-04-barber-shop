package br.com.dio.barber_shop_api.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@Configuration
@Order(2) // Depois do filtro de rate limit
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        String query = httpRequest.getQueryString();

        log.info("📥 Request - {} {}{}", method, uri, query != null ? ("?" + query) : "");

        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            String value = httpRequest.getHeader(header);
            log.debug("--> Header: {} = {}", header, value);
        }

        chain.doFilter(request, response);

        int status = httpResponse.getStatus();
        log.info("📤 Response - {} {} - Status: {}", method, uri, status);
    }
}
