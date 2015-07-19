package de.tum.pssif.vsdx.impl;

import java.util.Set;

import com.google.common.collect.Sets;

import de.tum.pssif.vsdx.VsdxDocument;
import de.tum.pssif.vsdx.VsdxDocumentWriter;
import de.tum.pssif.vsdx.VsdxMaster;
import de.tum.pssif.vsdx.zip.ZipArchiveEntryWithData;


public class VsdxDocumentImpl implements VsdxDocument {

  private final Set<ZipArchiveEntryWithData> transferOnlyEntries;
  private final VsdxPageImpl                 page;
  private final VsdxMasterRepository         masterRepository;
  private int                                shapesMaxId;

  public VsdxDocumentImpl(Set<ZipArchiveEntryWithData> transferOnlyEntries, VsdxPageImpl page, VsdxMasterRepository masterRepo) {
    this.transferOnlyEntries = transferOnlyEntries;
    this.page = page;
    this.masterRepository = masterRepo;
    shapesMaxId = page.setDocument(this);
    masterRepository.setDocument(this);
  }

  @Override
  public Set<VsdxMaster> getMasters() {
    return Sets.<VsdxMaster> newHashSet(masterRepository.getMasters());
  }

  @Override
  public VsdxPageImpl getPage() {
    return page;
  }

  @Override
  public VsdxMasterImpl getMaster(int id) {
    return masterRepository.getMaster(id);
  }

  @Override
  public VsdxMasterImpl getMaster(String name) {
    return masterRepository.getMaster(name);
  }

  @Override
  public boolean hasMaster(String name) {
    return masterRepository.hasMaster(name);
  }

  @Override
  public boolean hasMaster(int id) {
    return masterRepository.hasMaster(id);
  }

  int getNewShapeId() {
    shapesMaxId++;
    return shapesMaxId;
  }

  Set<ZipArchiveEntryWithData> getTransferOnlyEntries() {
    return Sets.newHashSet(this.transferOnlyEntries);
  }

  @Override
  public VsdxDocumentWriter getDocumentWriter() {
    return new VsdxDocumentWriterImpl(this);
  }
}
