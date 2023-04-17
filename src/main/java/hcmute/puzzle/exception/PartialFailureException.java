package hcmute.puzzle.exception;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PartialFailureException extends CustomException {

  public PartialFailureException(String message) {
    super(413, message);
  }

  public PartialFailureException() {
    super(413, ErrorDefine.ServerError.PARTIAL_FAILURE);
  }


  public static String processingMsg(List<String> failureObject) {
    AtomicReference<String> msg = new AtomicReference<>("partial item failure, detail: ");
    failureObject.forEach(item -> {
      msg.set(msg + item + ", ");
    });
    return msg.get();
  }
}
