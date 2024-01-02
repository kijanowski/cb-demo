package demo.cb;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
class CbController {

    private final ThirdPartyCaller caller;

    public CbController(ThirdPartyCaller caller) {
        this.caller = caller;
    }

    @GetMapping("/dt")
    String durationThreshold(@RequestParam int delay) {
        return caller.callThroughDt(delay);
    }

    @GetMapping("/tl")
    String timeLimiter(@RequestParam int delay) {
        return caller.callThroughTl(delay);
    }

    @GetMapping("/dt/description")
    String durationThresholdDescription() {
        return """
                Consider a set of the last 10 executions (window size).
                Wait for at least 5 calls to calculate a potential state transition!.
                When 20% (2/10) is considered slow (>= 100ms), go the fallback path (OPEN state).
                Stay in the OPEN state for 20 seconds until transition into the HALF-OPEN state.
                In HALF-OPEN state permit only 2 calls and recalculate the state.
                """;
    }

    @GetMapping("/tl/description")
    String timeLimiterDescription() {
        return """
                Consider a set of the last 10 executions (window size).
                Wait for at least 5 calls to calculate a potential state transition.
                When 20% (2/10) is considered a failure (>= 100ms), go the fallback path (OPEN state).
                Stay in the OPEN state for 20 seconds until transition into the HALF-OPEN state.
                In HALF-OPEN state permit only 2 calls and recalculate the state.
                Every execution taking longer than 100ms returns fallback value and increments the failure counter.
                """;
    }
}
