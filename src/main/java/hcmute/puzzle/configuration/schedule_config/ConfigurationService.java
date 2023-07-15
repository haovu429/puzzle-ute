package hcmute.puzzle.configuration.schedule_config;

import org.springframework.stereotype.Service;

import java.util.Map;

//@Service
public interface ConfigurationService {

    String get(String key);
    Map<String, String> getAll();
    void set(String key, String value);
    void addListener(ConfigurationChangeListener listener);

}