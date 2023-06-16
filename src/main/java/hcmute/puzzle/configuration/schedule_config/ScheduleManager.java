package hcmute.puzzle.configuration.schedule_config;

import hcmute.puzzle.PuzzleUteApplication;
import hcmute.puzzle.configuration.ThreadPoolTaskSchedulerConfig;
import hcmute.puzzle.infrastructure.models.ScheduleJob;
import hcmute.puzzle.infrastructure.models.annotation.HasScheduleJobProcessing;
import hcmute.puzzle.infrastructure.models.annotation.JobAnnotationProcessing;
import hcmute.puzzle.services.schedule_service.ScheduleService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@Getter
@Setter
public class ScheduleManager {

	@Value("${cronjob.batch.enable}")
	Boolean enableFlag;

	@Autowired
	private Environment env;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	//    @Qualifier("threadPoolTaskScheduler")
	private TaskScheduler taskScheduler;

	@Autowired
	private ApplicationContext context;

	List<ScheduleJob> scheduleJobs;

	public void ScheduleManager() {
	}

	;

	private void cancelAllService() {
		if (scheduleJobs != null && !scheduleJobs.isEmpty()) {
			for (ScheduleJob scheduleJob : scheduleJobs) {
				stopScheduledTask(scheduleJob);
			}
		}
	}

	@PostConstruct
	public void refresh() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		if (enableFlag != null && !enableFlag) {
			return;
		}

		cancelAllService();
		scheduleJobs = collectScheduleMethod();

		for (ScheduleJob scheduleJob : scheduleJobs) {
			// ApplicationContext context = new AnnotationConfigApplicationContext(scheduleJob.getClazz());
			Object myBean = null;
			// check if MyBean exists in the context
			boolean containsServiceBean = context.getBeansWithAnnotation(Service.class).containsKey("myService");
			//            if (containsServiceBean) {
			//                try {
			// include multiple configuration classes
			//                    context.register(scheduleJob.getClazz(), AppConfig1.class, AppConfig2.class);
			//
			//                    context.refresh();
			// get the MyBean instance from the context
			myBean = context.getBean(scheduleJob.getClazz());

			//                    // inject the MyBean instance into another bean if needed
			//                    ThreadPoolTaskSchedulerConfig anotherBean = context.getBean(ThreadPoolTaskSchedulerConfig.class);
			//
			//                    // use reflection to find the setter method for the OtherBean instance
			//                    Method setOtherBeanMethod = scheduleJob.getClazz().getMethod("setOtherBean", ThreadPoolTaskSchedulerConfig.class);
			//
			//                    // set the OtherBean instance in the MyBean instance
			//                    setOtherBeanMethod.invoke(myBean, anotherBean);
			//
			//                    // use reflection to find the setter method for the OtherBean instance
			//                    Method rể = scheduleJob.getClazz().getMethod("setOtherBean", ThreadPoolTaskSchedulerConfig.class);
			//
			//                    // set the OtherBean instance in the MyBean instance
			//                    setOtherBeanMethod.invoke(myBean, anotherBean);
			//
			//                    Method method = scheduleJob.getClazz().getMethod(scheduleJob.getMethod().getName());
			//
			//                    method.invoke(myBean);

			//context.getBean(scheduleJob.getClazz().getName()).se(myBean);

			//                } catch (NoSuchBeanDefinitionException e) {
			//                    // handle the case when MyBean is not found in the context
			//                    System.out.println("MyBean not found in the context.");
			//                }
			//            }
			refreshScheduleJob(scheduleJob, myBean);
		}
	}

	private List<ScheduleJob> collectScheduleMethod() {
		String packageName = "hcmute.puzzle";
		scheduleJobs = new ArrayList<>();
		List<Class<?>> clazzContainScheduleMethod = HasScheduleJobProcessing.getAnnotatedClass();
		for (Class<?> clazz : clazzContainScheduleMethod) {
			List<ScheduleJob> scheduleJobsInClass = JobAnnotationProcessing.getAnnotatedMethods(clazz);
			scheduleJobsInClass.forEach(job -> {
				log.info("Job schedule: " + job.getMethod().getName());
				job.setClazz(clazz);
				scheduleJobs.add(job);
			});
		}
		return scheduleJobs;
	}

	public void refreshScheduleJob(ScheduleJob scheduleJob, Object serviceBean) {
		log.info("current cron: " + env.getProperty(scheduleJob.getKey()));
		stopScheduledTask(scheduleJob);
		startScheduledTask(scheduleJob, serviceBean);
	}

	private void startScheduledTask(ScheduleJob scheduleJob, Object serviceBean) {
		try {
			ScheduledFuture<?> scheduledTask = taskScheduler.schedule(() -> {
				try {
					scheduleJob.getMethod().invoke(serviceBean);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}, new CronTrigger(Objects.requireNonNull(env.getProperty(scheduleJob.getKey()))));
			scheduleJob.setScheduledTask(scheduledTask);
			log.info("start cron: ");
		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	private void stopScheduledTask(ScheduleJob scheduleJob) {
		if (scheduleJob.getScheduledTask() != null) {
			boolean resultCancel = scheduleJob.getScheduledTask().cancel(true);
			log.info("stop cron: " + resultCancel);
		}
	}
}
