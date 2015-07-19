package de.tum.pssif.vsdx.impl;

import de.tum.pssif.vsdx.VsdxDocumentLoader;


public final class VsdxDocumentLoaderFactory {

  public static VsdxDocumentLoaderFactory INSTANCE = new VsdxDocumentLoaderFactory();

  private VsdxDocumentLoaderFactory() {
    //NOP
  }

  public VsdxDocumentLoader create() {
    return new VsdxDocumentLoaderImpl();
  }

}
