package ru.practicum.shareit.annotation;

import org.springframework.boot.logging.LogLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecution {

    LogLevel value() default LogLevel.INFO;

    ChronoUnit chronoUnit() default ChronoUnit.MILLIS;

    boolean withArgs() default false;

    boolean withDuration() default true;
}