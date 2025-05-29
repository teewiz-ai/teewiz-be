package org.example.tshirtlabbackend.config.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@Order(0)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID = "X-Correlation-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        String id = Optional.ofNullable(req.getHeader(CORRELATION_ID))
                .orElse(UUID.randomUUID().toString());

        MDC.put(CORRELATION_ID, id);
        res.setHeader(CORRELATION_ID, id);

        try { chain.doFilter(req, res); }
        finally { MDC.clear(); }
    }
}