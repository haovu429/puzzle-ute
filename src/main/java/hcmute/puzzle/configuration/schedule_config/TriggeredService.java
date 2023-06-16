package hcmute.puzzle.configuration.schedule_config;

import org.springframework.scheduling.Trigger;

import java.util.Optional;

public interface TriggeredService extends Runnable {

    String NO_CONFIG_VALUE = "noConfigValue";

    Optional<String> getKey();

    Trigger constructTrigger(String config);

}
