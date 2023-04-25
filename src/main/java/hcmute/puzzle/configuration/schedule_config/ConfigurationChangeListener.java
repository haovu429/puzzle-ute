package hcmute.puzzle.configuration.schedule_config;

import java.util.Optional;

public interface ConfigurationChangeListener {

    Optional<String> keyToReactOn();
    void onConfigurationChange(String newValue);

}