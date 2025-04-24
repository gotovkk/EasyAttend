package me.bsuir.easyattend.logs;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VisitCounterFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(VisitCounterFilter.class);
    private final VisitCounter visitCounter;

    public VisitCounterFilter(VisitCounter visitCounter) {
        this.visitCounter = visitCounter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestUri = httpRequest.getRequestURI();

        if (requestUri.startsWith("/api") || requestUri.startsWith("/api/visits")) {
            logger.info("Incrementing visit counter for URI: {}", requestUri);
            visitCounter.incrementVisit(requestUri);
        }

        chain.doFilter(request, response);
    }
}