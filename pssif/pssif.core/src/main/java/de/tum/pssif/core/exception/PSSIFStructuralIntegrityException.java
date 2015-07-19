package de.tum.pssif.core.exception;

public class PSSIFStructuralIntegrityException extends PSSIFException {

  private static final long serialVersionUID = 6050398078781143898L;

  public PSSIFStructuralIntegrityException(String msg) {
    super(msg);
  }

  public PSSIFStructuralIntegrityException(String msg, Throwable rootCause) {
    super(msg, rootCause);
  }

  public PSSIFStructuralIntegrityException(Throwable rootCause) {
    super(rootCause);
  }

}
