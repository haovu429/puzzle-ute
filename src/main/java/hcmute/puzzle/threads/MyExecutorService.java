//package hcmute.puzzle.threads;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Service;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//@Service
//public class MyExecutorService {
//    @Bean("fixedThreadPool")
//    public ExecutorService fixedThreadPool() {
//        return Executors.newFixedThreadPool(5);
//    }
//
//    @Bean("singleThreaded")
//    public ExecutorService singleThreadedExecutor() {
//        return Executors.newSingleThreadExecutor();
//    }
//
//    @Bean("cachedThreadPool")
//    public ExecutorService cachedThreadPool() {
//        return Executors.newCachedThreadPool();
//    }
//
//    @Bean("worksteelThreadPool")
//    public ExecutorService worksteelThreadPool() {
//        return Executors.newWorkStealingPool();
//    }
//
//    @Bean("scheduledThreadPool")
//    public ExecutorService scheduledThreadPool() {
//        return Executors.newScheduledThreadPool(10);
//    }
//}
