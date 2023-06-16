package hcmute.puzzle.configuration.schedule_config;

import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BlueServiceScheduler extends SpringEventTriggeredServiceSchedulerBase<BlueService> {

    private final ThreadPoolTaskScheduler scheduler;

    private final Set<BlueService> services;

    protected BlueServiceScheduler(
            ApplicationContext applicationContextToReactOn,
            ThreadPoolTaskScheduler scheduler,
            ConfigurationService configurationService,
            Set<BlueService> services) {

        super(applicationContextToReactOn, configurationService);
        this.scheduler = scheduler;
        this.services = services;
    }

    @Override
    protected Set<BlueService> getServices() {
        return services;
    }

    @Override
    protected TaskScheduler getScheduler() {
        return scheduler;
    }

}