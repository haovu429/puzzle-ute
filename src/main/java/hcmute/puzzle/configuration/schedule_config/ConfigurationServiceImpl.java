package hcmute.puzzle.configuration.schedule_config;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    private Map<String, String> configs;
    private Map<String, List<ConfigurationChangeListener>> listeners;

    public ConfigurationServiceImpl() {
        configs = new HashMap<>();
        listeners = new HashMap<>();
    }

    @Override
    public String get(String key) {
        return configs.get(key);
    }

    @Override
    public Map<String, String> getAll() {
        return new HashMap<>(configs);
    }

    @Override
    public void set(String key, String value) {
        configs.put(key, value);

        listeners.getOrDefault(key, Collections.emptyList())
                .forEach(listener -> listener.onConfigurationChange(value));
    }

    @Override
    public void addListener(ConfigurationChangeListener listener) {
        listener.keyToReactOn()
                .ifPresent(key -> listeners.computeIfAbsent(key, k -> new ArrayList<>()).add(listener));
    }

}

