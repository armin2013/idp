package de.tum.pssif.core.exception;

public class PSSIFIllegalAccessException extends PSSIFException {

  private static final long serialVersionUID = 7256568439596588482L;

  public PSSIFIllegalAccessException(String msg) {
    super(msg);
  }

  public PSSIFIllegalAccessException(String msg, Throwable rootCause) {
    super(msg, rootCause);
  }

  public PSSIFIllegalAccessException(Throwable rootCause) {
    super(rootCause);
  }

}
