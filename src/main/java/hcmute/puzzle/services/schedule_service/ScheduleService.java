package hcmute.puzzle.services.schedule_service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    @Scheduled(cron = "0 29 2 * * ?")
    public void ScheduleTask() {
        System.out.println("This is Schedule task!");
    }
}
