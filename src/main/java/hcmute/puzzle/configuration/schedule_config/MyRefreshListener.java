package hcmute.puzzle.configuration.schedule_config;

import hcmute.puzzle.services.schedule_service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

@Service
public class MyRefreshListener implements ApplicationListener<EnvironmentChangeEvent> {
    @Autowired
    ScheduleManager scheduleManager;

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {

        if(event.getKeys().stream().anyMatch(cfg -> cfg.startsWith("cronjob"))) {
            try {
                scheduleManager.refresh();
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
