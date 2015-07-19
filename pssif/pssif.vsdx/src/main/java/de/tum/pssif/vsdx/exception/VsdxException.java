package de.tum.pssif.vsdx.exception;

public class VsdxException extends RuntimeException {

  private static final long serialVersionUID = 5892982850857711047L;

  public VsdxException(String msg) {
    super(msg);
  }

  public VsdxException(String msg, Throwable rootCause) {
    super(msg, rootCause);
  }

  public VsdxException(Throwable rootCause) {
    super(rootCause);
  }

}
