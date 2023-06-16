package hcmute.puzzle.configuration.schedule_config;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/schedule-config")
public class ConfigurationRestController {

    private final ConfigurationService configurationService;

    public ConfigurationRestController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping
    public String home() {
        return "Configuration Service Controller";
    }

    @RequestMapping(method = { RequestMethod.POST, RequestMethod.PUT}, path = "/keys", consumes = { MediaType.APPLICATION_JSON_VALUE})
    public void update(@RequestBody Map<String, String> keyValues) {
        keyValues.forEach((k, v) -> configurationService.set(k, v));
    }

    @GetMapping(path = "/keys/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> get(@PathVariable String key) {
        return Collections.singletonMap(key, configurationService.get(key));
    }

    @GetMapping(path = "/keys", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> get() {
        return configurationService.getAll();
    }

}
