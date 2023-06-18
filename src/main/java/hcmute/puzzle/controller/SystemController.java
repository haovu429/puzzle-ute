package hcmute.puzzle.controller;

import hcmute.puzzle.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/system")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class SystemController {
	@GetMapping("/generate-thread-dump")
	public String generateThreadDump() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				String infoText = "";
				final java.lang.management.ThreadMXBean threadMXBean = java.lang.management.ManagementFactory.getThreadMXBean();
				final java.lang.management.ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 100);
				for (java.lang.management.ThreadInfo threadInfo : threadInfos) {
					System.out.println(threadInfo.getThreadName());
					infoText = infoText.concat(threadInfo.getThreadName());
					final Thread.State state = threadInfo.getThreadState();
					System.out.println("   java.lang.Thread.State: " + state);
					infoText = infoText.concat("   java.lang.Thread.State: " + state);
					final StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();
					for (final StackTraceElement stackTraceElement : stackTraceElements) {
						System.out.println("        at " + stackTraceElement);
						infoText = infoText.concat("        at " + stackTraceElement);
					}
					System.out.println("\n");
					infoText = infoText.concat("\n");
					log.info("============== Thread dump info ===============");
					log.info(infoText);
					log.info("============== End ===============");
				}
			}
		});
		return "Generate done. Please check log!";
	}
}
