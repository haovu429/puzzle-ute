package hcmute.puzzle.configuration.schedule_config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.TaskScheduler;

import java.util.Set;

public abstract class SpringEventTriggeredServiceSchedulerBase<T extends TriggeredService> implements ApplicationListener<ContextRefreshedEvent> {

    private final ApplicationContext applicationContextToReactOn;

    private final ConfigurationService configurationService;

    protected SpringEventTriggeredServiceSchedulerBase(
            ApplicationContext applicationContextToReactOn,
            ConfigurationService configurationService) {
        this.applicationContextToReactOn = applicationContextToReactOn;
        this.configurationService = configurationService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!this.applicationContextToReactOn.getId().equals(event.getApplicationContext().getId())) {
            return;
        }

        getServices().forEach(this::startService);
    }

    private final void startService(T service) {

        TriggeringConfigurationChangeListener listener = new TriggeringConfigurationChangeListener(service, getScheduler());

        service.getKey().ifPresent(key -> configurationService.addListener(listener));

        String configValue = service.getKey()
                .map(configurationService::get)
                .orElse(TriggeredService.NO_CONFIG_VALUE);

        // initial start
        listener.onConfigurationChange(configValue);
    }

    protected abstract Set<T> getServices();
    protected abstract TaskScheduler getScheduler();
}
