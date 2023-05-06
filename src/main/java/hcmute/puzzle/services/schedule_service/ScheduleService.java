package hcmute.puzzle.services.schedule_service;

import hcmute.puzzle.infrastructure.models.annotation.HasScheduleJob;
import hcmute.puzzle.infrastructure.models.annotation.JobAnnotation;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service(value = "scheduleService")
@Getter
@Setter
@HasScheduleJob
public class ScheduleService {

    @Autowired
    private Environment env;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

//    @Autowired
//    private TaskScheduler taskScheduler;

    private ScheduledFuture<?> scheduledTask;

    //@Scheduled(cron = "${cronjob.service}")
    @JobAnnotation(key = "cronjob.service")
    public void runScheduledJob() {
        log.info("Hello world! abc");
    }

    @JobAnnotation(key = "cronjob.xyz")
    public void runScheduledJobOther() {
        log.info("Hello world! xyz");
    }

//    @PostConstruct
//    public void refresh(){
//        log.info("current cron: " + env.getProperty("cronjob.service"));
//        stopScheduledTask();
//        startScheduledTask();
//    }

//    private void startScheduledTask() {
//        log.info("start cron: ");
//        scheduledTask = taskScheduler.schedule(() -> runScheduledJob()
//                , new CronTrigger(Objects.requireNonNull(env.getProperty("cronjob.service"))));
//    }

//    private void stopScheduledTask() {
//        if (scheduledTask != null) {
//            boolean resultCancel = scheduledTask.cancel(true);
//            log.info("stop cron: " + resultCancel);
//        }
//    }

//    public void updateScheduleTime(String newActivityName, String newStartTime, long newIntervalInSeconds) {
//        activityName = newActivityName;
//        //taskScheduler.schedule()
//    }

//    @EventListener
//    public void handleConfigChangeEvent(EnvironmentChangeEvent event) {
//        if (event.getKeys().contains("myapp.schedule.start-time") ||
//                event.getKeys().contains("myapp.schedule.interval")) {
//            String activityName = "Print hello world";
//            String startTime = event.getNewValue("myapp.schedule.start-time");
//            long intervalInSeconds = event.getNewValue("myapp.schedule.interval");
//            updateScheduleTime(activityName, startTime, intervalInSeconds);
//        }
//    }
}
