package de.tum.pssif.vsdx.zip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

import com.google.common.collect.Sets;

import de.tum.pssif.vsdx.exception.VsdxIOException;


public class ZipReader {

  private final InputStream in;

  public static ZipReader create(InputStream in) {
    return new ZipReader(in);
  }

  private ZipReader(InputStream in) {
    this.in = in;
  }

  public Set<ZipArchiveEntryWithData> read() {
    Set<ZipArchiveEntryWithData> zaEntries = Sets.newHashSet();
    try {
      ZipArchiveInputStream zis = new ZipArchiveInputStream(in);
      ZipArchiveEntry entry = null;
      while ((entry = zis.getNextZipEntry()) != null) {
        if (zis.canReadEntryData(entry)) {
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          int nextByte;
          while ((nextByte = zis.read()) != -1) {
            bos.write(nextByte);
          }
          zaEntries.add(new ZipArchiveEntryWithData((ZipArchiveEntry) entry.clone(), bos.toByteArray()));
        }
      }
      zis.close();
    } catch (IOException e) {
      throw new VsdxIOException("Failed to read archive.", e);
    }
    return zaEntries;
  }
}
