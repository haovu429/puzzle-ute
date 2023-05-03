package hcmute.puzzle.infrastructure.models.annotation;

import hcmute.puzzle.infrastructure.models.ScheduleJob;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JobAnnotationProcessing {

    public static List<ScheduleJob> getAnnotatedMethods(Class<?> clazz) {
        List<ScheduleJob> scheduleJobs = new ArrayList<>();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(JobAnnotation.class)) {
                JobAnnotation annotation = method.getAnnotation(JobAnnotation.class);
                ScheduleJob scheduleJob = new ScheduleJob(annotation.key(), method);
                scheduleJobs.add(scheduleJob);
//                if (annotation.key().equals(key)) {
//                    annotatedMethods.add(method);
//                }

            }
        }
        return scheduleJobs;
    }
}
