package hcmute.puzzle.services.schedule_service;

import hcmute.puzzle.infrastructure.models.annotation.HasScheduleJob;
import hcmute.puzzle.infrastructure.models.annotation.JobAnnotation;
import hcmute.puzzle.infrastructure.repository.UserRepository;
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

    @Autowired UserRepository userRepository;
    //@Scheduled(cron = "${cronjob.service}")
    @JobAnnotation(key = "cronjob.service")
    public void runScheduledJob() {
        userRepository.getUserByEmail("admin1@gmail.com");
        log.info("Hello world! abc");
    }

    @JobAnnotation(key = "cronjob.xyz")
    public void runScheduledJobOther() {
        log.info("Hello world! xyz");
    }
}
