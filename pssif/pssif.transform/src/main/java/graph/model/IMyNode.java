package graph.model;

import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.model.Node;

public interface IMyNode {

	public String getName();

	public String getRealName();

	public String getNodeInformations(boolean details);

	public boolean isDetailedOutput();

	public void setDetailedOutput(boolean detailedOutput);

	public Node getNode();

	public NodeTypeBase getBaseNodeType();

	public boolean isVisible();
}
