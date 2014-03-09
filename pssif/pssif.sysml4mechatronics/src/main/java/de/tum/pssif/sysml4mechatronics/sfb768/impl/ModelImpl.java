package de.tum.pssif.sysml4mechatronics.sfb768.impl;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.tum.pssif.sysml4mechatronics.sfb768.SFB768Block;
import de.tum.pssif.sysml4mechatronics.sfb768.SFB768Model;
import de.tum.pssif.sysml4mechatronics.sfb768.SFB768Port;
import de.tum.pssif.sysml4mechatronics.sfb768.SFB768PortAssociation;
import de.tum.pssif.sysml4mechatronics.sfb768.SFB768Identifier;
import de.tum.pssif.sysml4mechatronics.sfb768.SFB768Layer;
import de.tum.pssif.sysml4mechatronics.sfb768.SFB768Name;


public class ModelImpl extends IdentifiableNamedImpl implements SFB768Model {

  private final Map<SFB768Identifier, BlockImpl> blocks           = Maps.newHashMap();
  private final Set<PortAssociationImpl>         portAssociations = Sets.newHashSet();

  ModelImpl(SFB768Identifier identifier, SFB768Name name) {
    super(identifier, name);
  }

  @Override
  public Set<SFB768Block> getBlocks() {
    return Sets.<SFB768Block> newHashSet(this.blocks.values());
  }

  @Override
  public Set<SFB768PortAssociation> getPortAssociations() {
    return Sets.<SFB768PortAssociation> newHashSet(this.portAssociations);
  }

  @Override
  public SFB768Block createBlock(SFB768Name name, SFB768Identifier identifier, SFB768Layer layer) {
    BlockImpl result = null;
    if (SFB768Layer.ELECTRICAL.equals(layer)) {
      result = new ElectricalBlock(identifier, name);
    }
    else if (SFB768Layer.INTERFACE.equals(layer)) {
      result = new InterfaceBlock(identifier, name);
    }
    else if (SFB768Layer.PHYSICAL.equals(layer)) {
      result = new PhysicalBlock(identifier, name);
    }
    else {
      result = new SoftwareBlock(identifier, name);
    }
    this.blocks.put(identifier, result);
    return result;
  }

  @Override
  public SFB768PortAssociation associatePorts(SFB768Port from, SFB768Port to) {
    PortImpl fromPort = findBlock(from.getBlock().getIdentifier()).findPort(from.getIdentifier());
    PortImpl toPort = findBlock(to.getBlock().getIdentifier()).findPort(to.getIdentifier());
    PortAssociationImpl result = new PortAssociationImpl(fromPort, toPort);
    this.portAssociations.add(result);
    return result;
  }

  @Override
  public BlockImpl findBlock(SFB768Identifier identifier) {
    return this.blocks.get(identifier);
  }

}
