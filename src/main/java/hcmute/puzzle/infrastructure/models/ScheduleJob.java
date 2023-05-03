package hcmute.puzzle.infrastructure.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledFuture;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScheduleJob {
    private String key;
    private Method method;
    private ScheduledFuture<?> scheduledTask;
    private Class<?> clazz;

    public ScheduleJob(String key, Method method) {
        this.key = key;
        this.method = method;
    }
}
