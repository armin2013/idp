package de.tum.pssif.transform.io;

import de.tum.pssif.core.exception.PSSIFException;


public class PSSIFIoException extends PSSIFException {

  private static final long serialVersionUID = 4949818352584331372L;

  public PSSIFIoException(String msg) {
    super(msg);
  }

  public PSSIFIoException(String msg, Throwable rootCause) {
    super(msg, rootCause);
  }

  public PSSIFIoException(Throwable rootCause) {
    super(rootCause);
  }
}
