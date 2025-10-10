package com.taskboard.userservice.infrastructure.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * AOP aspect for automatic monitoring of service methods. Provides automatic timing and counting of
 * method executions.
 */
@Aspect
@Component
public class MonitoringAspect {

  private static final Logger logger = LoggerFactory.getLogger(MonitoringAspect.class);

  private final MeterRegistry meterRegistry;

  /**
   * Constructs a new MonitoringAspect.
   *
   * @param meterRegistry The meter registry for recording metrics.
   */
  public MonitoringAspect(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  /**
   * Monitors domain service methods for performance and error tracking.
   *
   * @param joinPoint The join point representing the method execution.
   * @return The result of the method execution.
   * @throws Throwable If the method execution throws an exception.
   */
  @Around("execution(* com.taskboard.userservice.domain.service.*.*(..))")
  public Object monitorDomainServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getTarget().getClass().getSimpleName();
    String metricName = "domain.service." + className.toLowerCase() + "." + methodName;

    Timer.Sample sample = Timer.start(meterRegistry);
    Counter successCounter =
        Counter.builder(metricName + ".success")
            .description("Successful executions of " + methodName)
            .register(meterRegistry);
    Counter errorCounter =
        Counter.builder(metricName + ".errors")
            .description("Failed executions of " + methodName)
            .register(meterRegistry);

    try {
      Object result = joinPoint.proceed();
      successCounter.increment();
      logger.debug(
          "Domain service method executed successfully - Class: {}, Method: {}",
          className,
          methodName);
      return result;
    } catch (Exception e) {
      errorCounter.increment();
      logger.error(
          "Domain service method failed - Class: {}, Method: {}, Error: {}",
          className,
          methodName,
          e.getMessage());
      throw e;
    } finally {
      sample.stop(
          Timer.builder(metricName + ".duration")
              .description("Execution time of " + methodName)
              .register(meterRegistry));
    }
  }

  /**
   * Monitors infrastructure service methods for performance and error tracking.
   *
   * @param joinPoint The join point representing the method execution.
   * @return The result of the method execution.
   * @throws Throwable If the method execution throws an exception.
   */
  @Around("execution(* com.taskboard.userservice.infrastructure.service.*.*(..))")
  public Object monitorInfrastructureServiceMethods(ProceedingJoinPoint joinPoint)
      throws Throwable {
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getTarget().getClass().getSimpleName();
    String metricName = "infrastructure.service." + className.toLowerCase() + "." + methodName;

    Timer.Sample sample = Timer.start(meterRegistry);
    Counter successCounter =
        Counter.builder(metricName + ".success")
            .description("Successful executions of " + methodName)
            .register(meterRegistry);
    Counter errorCounter =
        Counter.builder(metricName + ".errors")
            .description("Failed executions of " + methodName)
            .register(meterRegistry);

    try {
      Object result = joinPoint.proceed();
      successCounter.increment();
      logger.debug(
          "Infrastructure service method executed successfully - Class: {}, Method: {}",
          className,
          methodName);
      return result;
    } catch (Exception e) {
      errorCounter.increment();
      logger.error(
          "Infrastructure service method failed - Class: {}, Method: {}, Error: {}",
          className,
          methodName,
          e.getMessage());
      throw e;
    } finally {
      sample.stop(
          Timer.builder(metricName + ".duration")
              .description("Execution time of " + methodName)
              .register(meterRegistry));
    }
  }

  /**
   * Monitors repository methods for performance and error tracking.
   *
   * @param joinPoint The join point representing the method execution.
   * @return The result of the method execution.
   * @throws Throwable If the method execution throws an exception.
   */
  @Around("execution(* com.taskboard.userservice.infrastructure.repository.*.*(..))")
  public Object monitorRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getTarget().getClass().getSimpleName();
    String metricName = "repository." + className.toLowerCase() + "." + methodName;

    Timer.Sample sample = Timer.start(meterRegistry);
    Counter successCounter =
        Counter.builder(metricName + ".success")
            .description("Successful executions of " + methodName)
            .register(meterRegistry);
    Counter errorCounter =
        Counter.builder(metricName + ".errors")
            .description("Failed executions of " + methodName)
            .register(meterRegistry);

    try {
      Object result = joinPoint.proceed();
      successCounter.increment();
      logger.debug(
          "Repository method executed successfully - Class: {}, Method: {}", className, methodName);
      return result;
    } catch (Exception e) {
      errorCounter.increment();
      logger.error(
          "Repository method failed - Class: {}, Method: {}, Error: {}",
          className,
          methodName,
          e.getMessage());
      throw e;
    } finally {
      sample.stop(
          Timer.builder(metricName + ".duration")
              .description("Execution time of " + methodName)
              .register(meterRegistry));
    }
  }
}
