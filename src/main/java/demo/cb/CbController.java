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
}
