package hcmute.puzzle.infrastructure.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledFuture;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MethodDetail {
    private Method method;
    private Class<?> clazz;
    private ScheduledFuture<?> scheduledTask;
}
