package hcmute.puzzle.configuration.schedule_config;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;

import static java.util.Objects.isNull;

public interface BlueService extends TriggeredService {

    @Override
    default Trigger constructTrigger(String cron) {
        if (isNull(cron) || !CronExpression.isValidExpression(cron)) {
            return new CronTrigger(defaultCron());
        }
        return new CronTrigger(cron);
    }

    default String defaultCron() {
        return "0/20 * * * * *";
    }

}
