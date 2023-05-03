//package hcmute.puzzle.configuration.schedule_config;
//
//import hcmute.puzzle.services.schedule_service.ScheduleService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.SmartLifecycle;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.scheduling.support.CronTrigger;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.ScheduledFuture;
//
//@Component
//public class ScheduledTaskManager implements SmartLifecycle {
//
//    @Autowired
//    private TaskScheduler taskScheduler;
//
//    @Value("${my.cron.expression}")
//    private String cronExpression;
//
//    private ScheduledFuture<?> scheduledFuture;
//
//    private boolean running = false;
//
//    @Override
//    public void start() {
//        if (!this.running) {
//            this.scheduledFuture = taskScheduler.schedule((Runnable) new ScheduleService(taskScheduler), new CronTrigger(this.cronExpression));
//            this.running = true;
//        }
//    }
//
//    @Override
//    public void stop() {
//        if (this.scheduledFuture != null) {
//            this.scheduledFuture.cancel(true);
//        }
//        this.running = false;
//    }
//
//    @Override
//    public boolean isRunning() {
//        return this.running;
//    }
//
//    @Override
//    public int getPhase() {
//        return Integer.MAX_VALUE;
//    }
//
//    @Override
//    public boolean isAutoStartup() {
//        return true;
//    }
//
//    @Override
//    public void stop(Runnable callback) {
//        stop();
//        callback.run();
//    }
//}
