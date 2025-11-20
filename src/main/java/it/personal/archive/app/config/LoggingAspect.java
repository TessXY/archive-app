package it.personal.archive.app.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("within(@org.springframework.stereotype.Controller *)")
    public Object logAroundControllers(ProceedingJoinPoint joinPoint) throws Throwable {

        String method = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();

        log.info("[AOP] → Entrata in {}", method);

        try {
            Object result = joinPoint.proceed();
            log.info("[AOP] → Uscita da {}", method);
            return result;
        } catch (Exception ex) {
            log.error("[AOP] → Errore in {}", method, ex);
            throw ex;
        } finally {
            log.info("[AOP] → Esecuzione {} ms", System.currentTimeMillis() - start);
        }
    }
}
