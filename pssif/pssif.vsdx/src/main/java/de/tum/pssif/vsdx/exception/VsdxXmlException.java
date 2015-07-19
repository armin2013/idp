package de.tum.pssif.vsdx.exception;

public class VsdxXmlException extends VsdxException {

  private static final long serialVersionUID = 2843528832969187935L;

  public VsdxXmlException(String msg) {
    super(msg);
  }

  public VsdxXmlException(String msg, Throwable rootCause) {
    super(msg, rootCause);
  }

  public VsdxXmlException(Throwable rootCause) {
    super(rootCause);
  }
}
