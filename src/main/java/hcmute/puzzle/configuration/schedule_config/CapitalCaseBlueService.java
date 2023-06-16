package hcmute.puzzle.configuration.schedule_config;

import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Service;

import java.util.Optional;

//@Service
public class CapitalCaseBlueService implements BlueService {

    @Override
    public Optional<String> getKey() {
        return Optional.of("capital-blue");
    }

    @Override
    public Trigger constructTrigger(String cron) {
        return BlueService.super.constructTrigger(cron);
    }

    @Override
    public void run() {
        System.out.println("BLUE SERVICE ");
    }

}