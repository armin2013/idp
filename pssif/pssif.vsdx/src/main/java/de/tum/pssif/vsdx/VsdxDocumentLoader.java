package de.tum.pssif.vsdx;

import java.io.InputStream;

import de.tum.pssif.vsdx.exception.VsdxException;


public interface VsdxDocumentLoader {

  VsdxDocument loadDocument(InputStream inputStream) throws VsdxException;

}
