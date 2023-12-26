package demo.cb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

import static demo.cb.CbConfiguration.DURATION_THRESHOLD_CB;
import static demo.cb.CbConfiguration.TIME_LIMITER_CB;

@Service
public class ThirdPartyCaller {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThirdPartyCaller.class);

    public static final String OK = "OK";
    public static final String FALLBACK = "Fallback value";

    private final CircuitBreaker dtCb; // duration threshold
    private final CircuitBreaker tlCb; // time limiter

    public ThirdPartyCaller(CircuitBreakerFactory circuitBreakerFactory) {
        dtCb = circuitBreakerFactory.create(DURATION_THRESHOLD_CB);
        tlCb = circuitBreakerFactory.create(TIME_LIMITER_CB);
    }

    public String callThroughDt(int delay) {
        return dtCb.run(
                () -> call(delay),
                this::getFallbackValue
        );
    }

    public String callThroughTl(int delay) {
        return tlCb.run(
                () -> call(delay),
                this::getFallbackValue
        );
    }

    private String call(int delay) {
        LOGGER.info("Waiting for {}", delay);
        sleepFor(delay);
        return OK;
    }

    private String getFallbackValue(Throwable throwable) {
        LOGGER.warn("Fallback: ", throwable);
        return FALLBACK;
    }

    private void sleepFor(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException iex) {
            LOGGER.error("", iex);
        }
    }

}
