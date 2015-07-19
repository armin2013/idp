package de.tum.pssif.core.exception;

public class PSSIFException extends RuntimeException {

  private static final long serialVersionUID = 4032667967275050712L;

  public PSSIFException(String msg) {
    super(msg);
  }

  public PSSIFException(String msg, Throwable rootCause) {
    super(msg, rootCause);
  }

  public PSSIFException(Throwable rootCause) {
    super(rootCause);
  }
}
