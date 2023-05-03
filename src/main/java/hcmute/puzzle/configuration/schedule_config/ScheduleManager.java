package hcmute.puzzle.configuration.schedule_config;

import hcmute.puzzle.infrastructure.models.ScheduleJob;
import hcmute.puzzle.infrastructure.models.annotation.HasScheduleJobProcessing;
import hcmute.puzzle.infrastructure.models.annotation.JobAnnotationProcessing;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@Getter
@Setter
public class ScheduleManager {

    @Autowired
    private Environment env;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private TaskScheduler taskScheduler;

    public void ScheduleManager(){};

    public void refresh() throws InvocationTargetException, IllegalAccessException {
        List<ScheduleJob> scheduleJobs = collectScheduleMethod();

        for (ScheduleJob scheduleJob : scheduleJobs) {
//            ApplicationContext context = new AnnotationConfigApplicationContext(scheduleJob.getClazz());
//                scheduleJob.getMethod().invoke(context.getBean(scheduleJob.getClazz()));
            refreshScheduleJob(scheduleJob);
        }
    }

    private List<ScheduleJob> collectScheduleMethod() {
        List<ScheduleJob> scheduleJobs = new ArrayList<>();
        List<Class<?>> clazzContainScheduleMethod = HasScheduleJobProcessing.getAnnotatedClass();
        for (Class<?> clazz : clazzContainScheduleMethod) {
            List<ScheduleJob> scheduleJobsInClass = JobAnnotationProcessing.getAnnotatedMethods(clazz);
            scheduleJobsInClass.forEach(job -> {
                job.setClazz(clazz);
            });
        }
        return scheduleJobs;
    }

    public void refreshScheduleJob(ScheduleJob scheduleJob){
        log.info("current cron: " + env.getProperty("cronjob.service"));
        stopScheduledTask(scheduleJob);
        startScheduledTask(scheduleJob);
    }

    private void startScheduledTask(ScheduleJob scheduleJob) {
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(() -> {
                    try {
                        scheduleJob.getMethod().invoke(scheduleJob.getClazz());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                , new CronTrigger(Objects.requireNonNull(env.getProperty(scheduleJob.getKey()))));
        scheduleJob.setScheduledTask(scheduledTask);
        log.info("start cron: ");
    }

    private void stopScheduledTask(ScheduleJob scheduleJob) {
        if (scheduleJob.getScheduledTask() != null) {
            boolean resultCancel = scheduleJob.getScheduledTask().cancel(true);
            log.info("stop cron: " + resultCancel);
        }
    }
}
