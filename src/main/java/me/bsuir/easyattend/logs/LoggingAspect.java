package me.bsuir.easyattend.logs;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final VisitCounter visitCounter;

    @Autowired
    public LoggingAspect(VisitCounter visitCounter) {
        this.visitCounter = visitCounter;
    }

    @AfterReturning(pointcut = "execution(* me.bsuir.easyattend.controller.*.*(..))")
    public void logVisit(JoinPoint joinPoint) {
        String url = "/api/" + joinPoint.getSignature().getDeclaringType().getSimpleName().replace("Controller", "").toLowerCase();
        if (!url.startsWith("/api/logs") && !url.startsWith("/api/visits")) {
            visitCounter.incrementVisit(url);
            logger.info("Visit recorded for URL: {}", url);
        }
    }
}