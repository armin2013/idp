package de.tum.pssif.vsdx.exception;

public class VsdxIOException extends VsdxException {

  private static final long serialVersionUID = 5862714888754381789L;

  public VsdxIOException(String msg) {
    super(msg);
  }

  public VsdxIOException(String msg, Throwable rootCause) {
    super(msg, rootCause);
  }

  public VsdxIOException(Throwable rootCause) {
    super(rootCause);
  }
}
