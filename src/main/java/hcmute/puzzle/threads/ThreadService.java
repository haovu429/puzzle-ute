package hcmute.puzzle.threads;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.*;

import hcmute.puzzle.configuration.SystemInfo;
import hcmute.puzzle.entities.TokenEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static hcmute.puzzle.services.impl.SecurityServiceImpl.RESET_PASSWORD_TOKEN;

@Service
public class ThreadService {

  @Autowired private TokenRepository tokenRepository;

  public static final String MAIL_TASK = "MAIL_TASK";

//  @Autowired
//  @Qualifier("fixedThreadPool")
  public ExecutorService executorService;

  public <T> Future<T> execute(Callable<T> callable, String taskType, TokenEntity token)
      throws InterruptedException, ExecutionException {
    executorService = Executors.newFixedThreadPool(2);
    Future<T> future = executorService.submit(callable);
    executorService.shutdown();

    while (!future.isDone() && !future.isCancelled()) {
      Thread.sleep(200);
      System.out.println("Waiting for task completion...");
    }

    T result = future.get();

    if (taskType != null
            && taskType.equals(MAIL_TASK)
            && result instanceof Boolean)
    {
      Boolean boolResult = (Boolean) result;
      if (boolResult.equals(true)) {
        tokenRepository.save(token);
      }
    }

      System.out.println("Retrieved result from the task - " + result);

//    try {
//      if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
//        executorService.shutdownNow();
//      }
//    } catch (InterruptedException e) {
//      executorService.shutdownNow();
//    }
    return future;
  }

  private TokenEntity createTokenForgotPassword(UserEntity foundUser) {
    String tokenValue = UUID.randomUUID().toString();
    Date nowTime = new Date();
    Date expiredTime = new Date(nowTime.getTime() + SystemInfo.TOKEN_RESET_PASSWORD_DURATION);

    TokenEntity createToken = new TokenEntity();
    createToken.setToken(tokenValue);
    createToken.setExpiryTime(expiredTime);
    createToken.setUser(foundUser);
    createToken.setType(RESET_PASSWORD_TOKEN);

    return tokenRepository.save(createToken);
  }

}
