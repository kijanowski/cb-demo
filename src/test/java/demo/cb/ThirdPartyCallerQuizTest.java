package demo.cb;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.time.Duration;

import static demo.cb.ThirdPartyCaller.FALLBACK;
import static demo.cb.ThirdPartyCaller.OK;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ThirdPartyCallerQuizTest {

    private static final int FAST = 50;
    private static final int SLOW = 150;
    private static final String PATH_TO_DT_STATE = "circuitBreakers.dt-cb.state";
    private static final String PATH_TO_TL_STATE = "circuitBreakers.tl-cb.state";

    @Autowired
    private ThirdPartyCaller caller;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testDelayThresholdWithWindowCircuitBreaker() {
        // 8/10 are fast calls
        callDt(8, FAST, "???"); // TODO OK vs FALLBACK
        waitForCbMetrics();
        assertEquals("???", getDtCbState()); // TODO "CLOSED" vs "OPEN"

        // 2/10 are slow (20%)
        callDt(2, SLOW, "???"); // TODO OK vs FALLBACK

        // should go into ??? state
        assertEquals("???", getDtCbState()); // TODO "CLOSED" vs "OPEN"

        // should return ??? value
        callDt(1, FAST, "???"); // TODO OK vs FALLBACK
        callDt(1, SLOW, "???"); // TODO OK vs FALLBACK

        // wait until transitioned from OPEN to HALF_OPEN
        waitUntilDtCbIsInHalfOpenState();

        // permitted to do 2 calls in HALF_OPEN
        callDt(2, SLOW, "???"); // TODO OK vs FALLBACK

        // should go into ??? state
        assertEquals("???", getDtCbState()); // TODO "CLOSED" vs "OPEN"

        // should return ??? value
        callDt(1, FAST, "???"); // TODO OK vs FALLBACK
        callDt(1, SLOW, "???"); // TODO OK vs FALLBACK

        // wait until transitioned from OPEN to HALF_OPEN
        waitUntilDtCbIsInHalfOpenState();

        // permitted to do 2 calls in HALF_OPEN
        callDt(2, FAST, "???"); // TODO OK vs FALLBACK

        // should go into ??? state
        assertEquals("???", getDtCbState()); // TODO "CLOSED" vs "OPEN"

        // should return an ??? value
        callDt(1, SLOW, "???"); // TODO OK vs FALLBACK
    }

    @Test
    void testTimeLimiterCircuitBreaker() {
        // 8/10 are fast calls
        callTl(8, FAST, "???"); // TODO OK vs FALLBACK
        waitForCbMetrics();
        assertEquals("???", getTlCbState()); // TODO "CLOSED" vs "OPEN"

        // 2/10 are slow (20%)
        callTl(2, SLOW, "???"); // TODO OK vs FALLBACK

        // should go into ??? state
        assertEquals("???", getTlCbState()); // TODO "CLOSED" vs "OPEN"

        // should return ??? value
        callTl(1, FAST, "???"); // TODO OK vs FALLBACK
        callTl(1, SLOW, "???"); // TODO OK vs FALLBACK

        // wait until transitioned from OPEN to HALF_OPEN
        waitUntilTlCbIsInHalfOpenState();

        // permitted to do 2 calls in HALF_OPEN
        callTl(2, SLOW, "???"); // TODO OK vs FALLBACK

        // should go into ??? state
        assertEquals("???", getTlCbState()); // TODO "CLOSED" vs "OPEN"

        // should return ??? value
        callTl(1, FAST, "???"); // TODO OK vs FALLBACK
        callTl(1, SLOW, "???"); // TODO OK vs FALLBACK

        // wait until transitioned from OPEN to HALF_OPEN
        waitUntilTlCbIsInHalfOpenState();

        // permitted to do 2 calls in HALF_OPEN
        callTl(2, FAST, "???"); // TODO OK vs FALLBACK

        // should go into ??? state
        assertEquals("???", getTlCbState()); // TODO "CLOSED" vs "OPEN"

        // should return ??? value
        callTl(1, SLOW, "???"); // TODO OK vs FALLBACK
    }

    private void waitUntilDtCbIsInHalfOpenState() {
        await().atMost(Duration.ofMillis(1500)).until(this::getDtCbState, equalTo("HALF_OPEN"));
    }

    private void waitUntilTlCbIsInHalfOpenState() {
        await().atMost(Duration.ofMillis(1500)).until(this::getTlCbState, equalTo("HALF_OPEN"));
    }

    private void waitForCbMetrics() {
        await()
                .atMost(Duration.ofSeconds(5))
                .until(this::getActuator);
    }

    private void callDt(int times, int delay, String expectedResponse) {
        for (int i = 0; i < times; i++) {
            assertEquals(expectedResponse, caller.callThroughDt(delay), "Failed for iteration " + i);
        }
    }

    private void callTl(int times, int delay, String expectedResponse) {
        for (int i = 0; i < times; i++) {
            assertEquals(expectedResponse, caller.callThroughTl(delay), "Failed for iteration " + i);
        }
    }

    private String getDtCbState() {
        return getCbState(PATH_TO_DT_STATE);
    }

    private String getTlCbState() {
        return getCbState(PATH_TO_TL_STATE);
    }

    private String getCbState(String pathToState) {
        return JsonPath.parse(getActuatorMetrics()).read(pathToState, String.class);
    }

    private String getActuatorMetrics() {
        return restTemplate
                .getForEntity("/actuator/circuitbreakers", String.class)
                .getBody();
    }

    private boolean getActuator() {
        return restTemplate
                .getForEntity("/actuator/circuitbreakers", String.class)
                .getStatusCode()
                .is2xxSuccessful();
    }
}
