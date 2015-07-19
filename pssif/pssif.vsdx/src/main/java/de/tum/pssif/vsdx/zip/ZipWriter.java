package de.tum.pssif.vsdx.zip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import de.tum.pssif.vsdx.exception.VsdxException;


public class ZipWriter {

  private final OutputStream out;

  public static ZipWriter create(OutputStream out) {
    return new ZipWriter(out);
  }

  private ZipWriter(OutputStream out) {
    this.out = out;
  }

  public void write(Set<ZipArchiveEntryWithData> entries) {
    ZipArchiveOutputStream zos = new ZipArchiveOutputStream(out);
    try {
      for (ZipArchiveEntryWithData entry : entries) {
        if (zos.canWriteEntryData(entry.getZipEntry())) {
          zos.putArchiveEntry(entry.getZipEntry());
          zos.write(entry.getData());
          zos.closeArchiveEntry();
        }
      }
      zos.finish();
      zos.flush();
      zos.close();
    } catch (IOException e) {
      throw new VsdxException("Failed to write archive.", e);
    }
  }
}
