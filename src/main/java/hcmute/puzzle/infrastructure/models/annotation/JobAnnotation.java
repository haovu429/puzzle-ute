package hcmute.puzzle.infrastructure.models.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JobAnnotation {
    String key();
}
