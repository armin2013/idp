package reqtool.controller;

import graph.model.MyEdge;
import graph.model.MyEdgeType;
import graph.model.MyNode;

import java.util.LinkedList;

import model.ModelBuilder;
import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.transform.mapper.db.PssifToDBMapperImpl;

/**
 * The Class VersionManager.
 */
public class VersionManager {

	/** The Constant SEPARATOR. */
	private static final String SEPARATOR = "_";

	/** The selected node. */
	private MyNode selectedNode;

	/** The old version value. */
	private PSSIFOption<PSSIFValue> oldVersion;

	/** The new version node. */
	private MyNode newVersionNode;

	/** The evolves to edge type. */
	private MyEdgeType evolvesTo;

	/** The selected node type. */
	private NodeType selectedNodeType;

	/**
	 * Instantiates a new version manager.
	 *
	 * @param myNode
	 *            the selected node
	 */
	public VersionManager(MyNode myNode) {
		this.evolvesTo = ModelBuilder.getEdgeTypes().getValue(
				PSSIFCanonicMetamodelCreator.TAGS
						.get("E_RELATIONSHIP_CHRONOLOGICAL_EVOLVES_TO"));
		initSelectedNode(myNode);
	}

	/**
	 * Inits the selected node.
	 *
	 * @param node
	 *            the node
	 */
	private void initSelectedNode(MyNode node) {
		this.selectedNode = node;
		this.selectedNodeType = ModelBuilder.getMetamodel()
				.getNodeType(selectedNode.getNodeType().getName()).getOne();
		this.oldVersion = selectedNodeType
				.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION)
				.getOne().get(selectedNode.getNode());
		if (oldVersion.isNone() || oldVersion.getOne().asString().length() == 0) {
			setNodeVersion(selectedNode, "0.1");
		}
	}

	/**
	 * Creates the new version node.
	 *
	 * @param newVersion
	 *            the new version
	 * @return true, if successful
	 */
	public boolean createNewVersion(String newVersion) {
		try {
			initSelectedNode(getMaxVersion());
			if (Double.parseDouble(oldVersion.getOne().asString()) < (Double
					.parseDouble(newVersion))) {
				createNewVersionNode(newVersion);
				moveEdges(selectedNode, newVersionNode);
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("NEW VERSION: FALSE!!!!!!");
		return false;
	}

	/**
	 * Move the selected nodes edges to the new version node.
	 *
	 * @param selNode
	 *            the selected node
	 * @param newVersNode
	 *            the new version node
	 */
	private void moveEdges(MyNode selNode, MyNode newVersNode) {
		LinkedList<MyEdge> allEdges = new LinkedList<MyEdge>();
		allEdges.addAll(ModelBuilder.getAllEdges());

		for (MyEdge e : allEdges) {
			if (!e.getEdgeType().equals(evolvesTo)) {
				if (e.getDestinationNode().equals(selNode)
						&& !((MyNode) e.getSourceNode())
								.getNodeType()
								.toString()
								.equals(PSSIFCanonicMetamodelCreator.TAGS
										.get("N_TEST_CASE"))) {
					ModelBuilder.addNewEdgeGUI((MyNode) e.getSourceNode(),
							newVersNode, e.getEdgeType(), e.isDirected());
					ModelBuilder.getAllEdges().remove(e);
					// If edge should be deleted from DB
					PssifToDBMapperImpl.deletedEdges.add(e);
				} else if (e.getSourceNode().equals(selNode)) {
					ModelBuilder.addNewEdgeGUI(newVersNode,
							(MyNode) e.getDestinationNode(), e.getEdgeType(),
							e.isDirected());
					ModelBuilder.getAllEdges().remove(e);
					// If edge should be deleted from DB
					PssifToDBMapperImpl.deletedEdges.add(e);
				}
			}
		}

	}

	/**
	 * Checks for previous versions.
	 *
	 * @param myNode
	 *            the my node
	 * @return true, if successful
	 */
	public boolean hasPreviousVersions(MyNode myNode) {
		for (MyEdge e : ModelBuilder.getAllEdges()) {
			if (e.getEdgeType().equals(evolvesTo)
					&& e.getDestinationNode().getNode()
							.equals(myNode.getNode())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks for next versions.
	 *
	 * @param myNode
	 *            the my node
	 * @return true, if successful
	 */
	public boolean hasNextVersions(MyNode myNode) {
		for (MyEdge e : ModelBuilder.getAllEdges()) {
			if (e.getEdgeType().equals(evolvesTo)
					&& e.getSourceNode().getNode().equals(myNode.getNode())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates the new version node.
	 *
	 * @param newVersion
	 *            the new version
	 */
	public void createNewVersionNode(String newVersion) {
		this.newVersionNode = ModelBuilder.addNewNodeFromGUI(selectedNode
				.getNode().getId() + SEPARATOR, selectedNode.getNodeType());
		ModelBuilder.addNewEdgeGUI(selectedNode, newVersionNode, evolvesTo,
				true);
		setNodeVersion(newVersionNode, newVersion);
		newVersionNode
				.getNodeType()
				.getType()
				.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID)
				.getOne()
				.set(newVersionNode.getNode(),
						PSSIFOption.one(PSSIFValue.create(selectedNode
								.getNode().getId())));
		newVersionNode
				.getNodeType()
				.getType()
				.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME)
				.getOne()
				.set(newVersionNode.getNode(),
						PSSIFOption.one(PSSIFValue.create(selectedNode
								.getName())));
		selectedNode
				.getNodeType()
				.getType()
				.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID)
				.getOne()
				.set(selectedNode.getNode(),
						PSSIFOption.one(PSSIFValue.create(newVersionNode
								.getNode().getId()
								+ "_"
								+ oldVersion.getOne().asString())));
		selectedNode
				.getNodeType()
				.getType()
				.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION)
				.getOne()
				.set(selectedNode.getNode(),
						PSSIFOption.one(PSSIFValue.create(String
								.valueOf(oldVersion.getOne().asString()))));
	}

	/**
	 * Gets the max version node.
	 *
	 * @return the max version node
	 */
	public MyNode getMaxVersion() {
		MyNode maxVersionNode = null;
		double maxVersion = Integer.MIN_VALUE;
		String[] idNV = selectedNode.getNode().getId().toString()
				.split(SEPARATOR);
		for (MyNode newNode : ModelBuilder.getAllNodes()) {
			String[] idNVNew = newNode.getNode().getId().toString()
					.split(SEPARATOR);
			if (idNVNew.length > 0 && idNVNew[0].equals(idNV[0])) {
				PSSIFOption<PSSIFValue> version = selectedNodeType
						.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION)
						.getOne().get(newNode.getNode());
				if (!version.isNone()) {
					double versionDbl = Double.parseDouble(version.getOne()
							.asString());
					if (versionDbl > maxVersion) {
						maxVersion = versionDbl;
						maxVersionNode = newNode;
					}
				}
			}
		}
		return maxVersionNode;
	}

	/**
	 * Gets the node version.
	 *
	 * @param node
	 *            the node
	 * @return the node version
	 */
	private Attribute getNodeVersion(MyNode node) {
		return node.getNodeType().getType()
				.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION)
				.getOne();
	}

	/**
	 * Sets the node version.
	 *
	 * @param node
	 *            the node
	 * @param version
	 *            the version
	 */
	private void setNodeVersion(MyNode node, String version) {
		getNodeVersion(node).set(node.getNode(),
				PSSIFOption.one(PSSIFValue.create(String.valueOf(version))));
	}
}
