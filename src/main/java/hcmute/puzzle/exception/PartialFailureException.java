package hcmute.puzzle.exception;

public class PartialFailure extends Exception {
  public PartialFailure(String message) {
    super(message);
  }

  public PartialFailure() {
    super(Error.BatchExecuteError.PARTIAL_FAILURE);
  }
}
