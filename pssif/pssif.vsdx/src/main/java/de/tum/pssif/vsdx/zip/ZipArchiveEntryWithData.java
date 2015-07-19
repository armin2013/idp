package de.tum.pssif.vsdx.zip;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;


public class ZipArchiveEntryWithData {

  private final ZipArchiveEntry zipEntry;
  private final byte[]          data;

  public ZipArchiveEntryWithData(ZipArchiveEntry zipEntry, byte[] data) {
    this.zipEntry = zipEntry;
    this.data = data;
  }

  public ZipArchiveEntry getZipEntry() {
    return zipEntry;
  }

  public byte[] getData() {
    return data;
  }

  public String getName() {
    return zipEntry.getName();
  }

}
