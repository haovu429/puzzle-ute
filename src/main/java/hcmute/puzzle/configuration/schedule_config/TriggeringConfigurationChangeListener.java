package hcmute.puzzle.configuration.schedule_config;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

public class TriggeringConfigurationChangeListener implements ConfigurationChangeListener {

    private volatile ScheduledFuture<?> future;

    private final TriggeredService service;
    private final TaskScheduler taskScheduler;

    public TriggeringConfigurationChangeListener(TriggeredService service, TaskScheduler taskScheduler) {
        this.service = service;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public Optional<String> keyToReactOn() {
        return service.getKey();
    }

    @Override
    public synchronized void onConfigurationChange(String newValue) {
        if (future != null) {
            future.cancel(false);
        }

        Trigger trigger = service.constructTrigger(newValue);
        future = taskScheduler.schedule(service, trigger);
    }
}