package hcmute.puzzle.infrastructure.models;

import java.lang.reflect.Method;

public interface Job {
    public void execute(Method method);
}
