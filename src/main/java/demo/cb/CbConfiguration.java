package demo.cb;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CbConfiguration {

    public static final String DURATION_THRESHOLD_CB = "dt-cb";
    public static final String TIME_LIMITER_CB = "tl-cb";

    @Value("${openStatePauseInSeconds}")
    int openStatePauseInSeconds;

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> durationThresholdWithWindow() {
        // Consider a set of the last 10 executions (window size)
        // When 20% (2/10) is considered slow (>= 100ms), go the fallback path
        // Wait for at least 5 calls to calculate a potential state transition
        // In HALF-OPEN state permit only 2 calls
        var circuitBreakerConfig = CircuitBreakerConfig
                .custom()
                .slowCallDurationThreshold(Duration.ofMillis(100)) // 100ms
                .slowCallRateThreshold(20) // 20%
                .slidingWindowSize(10) //last 10 executions
                .waitDurationInOpenState(Duration.ofSeconds(openStatePauseInSeconds))
                .enableAutomaticTransitionFromOpenToHalfOpen()
                .minimumNumberOfCalls(5) // number of calls before failure aggregate is calculated and a state transition can occur
                .permittedNumberOfCallsInHalfOpenState(2) // after 2 calls in HALF-OPEN calculate state transition
                .build();

        var timeLimiterConfig = TimeLimiterConfig.ofDefaults();

        return factory -> factory
                .configure(builder -> builder
                                .circuitBreakerConfig(circuitBreakerConfig)
                                .timeLimiterConfig(timeLimiterConfig)
                                .build(),
                        DURATION_THRESHOLD_CB
                );
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> timeLimiter() {
        // Consider a set of the last 10 executions (window size)
        // When 20% (2/10) is considered a failure (>= 100ms), go the fallback path
        // Wait for at least 5 calls to calculate a potential state transition
        // In HALF-OPEN state permit only 2 calls
        var circuitBreakerConfig = CircuitBreakerConfig
                .custom()
                .failureRateThreshold(20) // 20 %
                .slidingWindowSize(10) //last 10 executions
                .waitDurationInOpenState(Duration.ofSeconds(openStatePauseInSeconds))
                .enableAutomaticTransitionFromOpenToHalfOpen()
                .minimumNumberOfCalls(5) // number of calls before failure aggregate is calculated and a state transition can occur
                .permittedNumberOfCallsInHalfOpenState(2) // after 2 calls in HALF-OPEN calculate state transition
                .build();

        // Every execution taking longer than 100ms returns fallback value and increments the failure counter
        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig
                .custom()
                .timeoutDuration(Duration.ofMillis(100))
                .build();

        return factory -> factory.configure(builder -> builder.circuitBreakerConfig(circuitBreakerConfig)
                .timeLimiterConfig(timeLimiterConfig).build(), TIME_LIMITER_CB);
    }
}
