package hcmute.puzzle.threads;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.*;

import hcmute.puzzle.configuration.SystemInfo;
import hcmute.puzzle.infrastructure.entities.Token;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static hcmute.puzzle.services.impl.SecurityServiceImpl.RESET_PASSWORD_TOKEN;

@Service
public class ThreadService {

  @Autowired TokenRepository tokenRepository;

  public static final String MAIL_TASK = "MAIL_TASK";

//  @Autowired
//  @Qualifier("fixedThreadPool")
  public ExecutorService executorService;

  public <T> Future<T> execute(Callable<T> callable, String taskType, Token token)
      throws InterruptedException, ExecutionException {
    executorService = Executors.newFixedThreadPool(5);
    Token savedToken = tokenRepository.save(token);
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
      if (boolResult.equals(false)) {
        tokenRepository.delete(savedToken);
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

  private Token createTokenForgotPassword(User foundUser) {
    String tokenValue = UUID.randomUUID().toString();
    Date nowTime = new Date();
    Date expiredTime = new Date(nowTime.getTime() + SystemInfo.TOKEN_RESET_PASSWORD_DURATION);

    Token createToken = new Token();
    createToken.setToken(tokenValue);
    createToken.setExpiryTime(expiredTime);
    createToken.setUser(foundUser);
    createToken.setType(RESET_PASSWORD_TOKEN);

    return tokenRepository.save(createToken);
  }

}
