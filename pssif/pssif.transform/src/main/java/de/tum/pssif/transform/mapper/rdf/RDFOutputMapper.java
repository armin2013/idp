package de.tum.pssif.transform.mapper.rdf;

import graph.model.MyEdge;
import graph.model.MyJunctionNode;
import graph.model.MyNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import model.MyModelContainer;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.LocationMapper;
import com.hp.hpl.jena.vocabulary.RDF;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.exception.PSSIFIllegalAccessException;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.core.metamodel.external.URIs;
import de.tum.pssif.core.metamodel.impl.GetValueOperation;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.JunctionNode;
import de.tum.pssif.core.model.Node;

public class RDFOutputMapper {
	public OntModel model;
	public OntModel pssifModel;

	public RDFOutputMapper(de.tum.pssif.core.model.Model model) {
		this(new MyModelContainer(model, PSSIFCanonicMetamodelCreator.create()));
	}

	public RDFOutputMapper(MyModelContainer mymodelContainer) {
		// MyModelContainer mymodelContainer = new MyModelContainer(model,
		// metamodel);

		this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		pssifModel = OntDocumentManager.getInstance().getOntology(URIs.pssifNS,
				OntModelSpec.OWL_DL_MEM);
		String path = PSSIFConstants.META_MODEL_PATH;
		try {
			pssifModel.read(new FileInputStream(new File(path)), URIs.pssifNS,
					"TURTLE");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		pssifModel.setNsPrefix("pssif", URIs.pssifNS);
		this.model.setNsPrefix("", URIs.modelNS);
		LinkedList<MyNode> nodes = mymodelContainer.getAllNodes();
		if (nodes != null) {
			for (Iterator<MyNode> it = nodes.iterator(); it.hasNext();) {
				MyNode myNode = it.next();
				addNode(myNode);
			}
		}

		LinkedList<MyJunctionNode> jnodes = mymodelContainer
				.getAllJunctionNodes();
		if (jnodes != null) {
			for (Iterator<MyJunctionNode> it = jnodes.iterator(); it.hasNext();) {
				MyJunctionNode myJNode = it.next();
				addJunctionNode(myJNode);
			}
		}

		LinkedList<MyEdge> edges = mymodelContainer.getAllEdges();
		if (edges != null) {
			for (Iterator<MyEdge> it = edges.iterator(); it.hasNext();) {
				MyEdge myEdge = it.next();
				addEdge(myEdge);
			}
		}
		Ontology ont = this.model.createOntology(URIs.modelUri);
		ont.addImport(this.model.createResource(URIs.pssifUri));
	}

	protected void addNode(MyNode myNode) {
		Node n = myNode.getNode();

		// myNode.getNodeType().getType().
		Individual node = this.model.createIndividual(
				URIs.modelNS + n.getId(),
				pssifModel.getOntClass(URIs.pssifNS
						+ myNode.getNodeType().getName()
								.replaceAll("\\s+", "_")));

		// Add Attributes from Node
		addAllAttributes(myNode.getAttributesHashMap(), node, myNode);

	}

	protected void addJunctionNode(MyJunctionNode myJNode) {
		Node jn = myJNode.getNode();

		// create Subject with URI from NodeID
		Individual subjectJNode = this.model.createIndividual(
				URIs.modelNS + jn.getId(),
				pssifModel.getOntClass(URIs.pssifNS
						+ myJNode.getNodeType().getName()
								.replaceAll("\\s+", "_")));

		// Add Attributes from JNode
		addAllAttributes(myJNode.getAttributesHashMap(), subjectJNode, myJNode);
	}

	protected void addEdge(MyEdge myEdge) {
		Edge e = myEdge.getEdge();

		// create Subject with URI from NodeID
		Individual subjectEdge = this.model.createIndividual(
				URIs.modelNS + e.getId(),
				pssifModel.getOntClass(URIs.pssifNS
						+ myEdge.getEdgeType().getName()
								.replaceAll("\\s+", "_")));

		// Add Attributes from Edge
		addAllAttributes(myEdge.getAttributesHashMap(), subjectEdge, myEdge);

		// Add outgoing Nodes to Edge
		Node out = myEdge.getDestinationNode().getNode();
		Node in = myEdge.getSourceNode().getNode();

		subjectEdge.addProperty(
				pssifModel.getObjectProperty(URIs.PROP_NODE_IN),
				this.model.getIndividual(URIs.modelNS + in.getId()));

		subjectEdge.addProperty(
				pssifModel.getObjectProperty(URIs.PROP_NODE_OUT),
				this.model.getIndividual(URIs.modelNS + out.getId()));
	}

	// JUNCTION NODES

	// MODEL

	private <T> void addAllAttributes(HashMap<String, Attribute> attrmap,
			Individual subject, T n) {

		for (Iterator<Entry<String, Attribute>> it = attrmap.entrySet()
				.iterator(); it.hasNext();) {
			Entry<String, Attribute> attrEntry = it.next();

			Attribute attr = attrEntry.getValue();
			String datatype = attr.getType().getName();
			String unit = attr.getUnit().getName();
			String category = attr.getCategory().getName();

			// Get Value and define what Type n is
			PSSIFOption<PSSIFValue> value = null;

			if (n instanceof MyNode)
				value = ((MyNode) n).getNode().apply(
						new GetValueOperation(attr));
			if (n instanceof MyJunctionNode)
				value = ((MyJunctionNode) n).getNode().apply(
						new GetValueOperation(attr));
			if (n instanceof MyEdge)
				value = ((MyEdge) n).getEdge().apply(
						new GetValueOperation(attr));

			// if there is no Attribute value don't save anything
			if (!value.isNone()) {

				// Get the ID of subject to add it to the Attribute Node URI
				PSSIFValue v = value.getOne();
				String attrValue = v.getValue().toString();
				String attrName = attr.getName();
				// TODO ANONYMOUS INDIVIDUAL

				// Add Value
				// You have to differ weather the Attribute isOne or isMany
				if (value.isOne()) {
					addPSSIFValue(attrName, subject, attrValue, unit, datatype,
							category);
				} else {
					if (value.isMany()) {
						Set<PSSIFValue> values = value.getMany();
						for (PSSIFValue val : values)
							addPSSIFValue(attrName, subject, val.getValue()
									.toString(), unit, datatype, category);
					}
				}

			}
		}
		// for (String attribute : PSSIFConstants.builtinAttributes) {
		// String value = null;
		// if (n instanceof MyNode) {
		// NodeType nodeType = ((MyNode) n).getNodeType().getType();
		// if (nodeType.getAttribute(attribute).isOne()) {
		// Attribute attr = nodeType.getAttribute(attribute).getOne();
		// value = attr.get(((MyNode) n).getNode()).getOne()
		// .asString();
		// }
		// }
		// if (n instanceof MyJunctionNode) {
		// NodeType nodeType = ((MyNode) n).getNodeType().getType();
		// if (nodeType.getAttribute(attribute).isOne()) {
		// Attribute attr = nodeType.getAttribute(attribute).getOne();
		// value = attr.get(((MyNode) n).getNode()).getOne()
		// .asString();
		// }
		// }
		// if (n instanceof MyEdge) {
		// EdgeType edgeType = ((MyEdge) n).getEdgeType().getType();
		// if (edgeType.getAttribute(attribute).isOne()) {
		// Attribute attr = edgeType.getAttribute(attribute).getOne();
		// value = attr.get(((MyEdge) n).getEdge()).getOne()
		// .asString();
		// }
		// }
		// if (value != null) {
		// subject.addProperty(pssifModel.getDatatypeProperty(URIs.pssifNS
		// + attribute), value);
		// }
		// }
	}

	/**
	 * Adds PSSIF Attribute Values to Attribute
	 * 
	 * @param subject
	 *            Resource of Attribute
	 * @param value
	 *            Value of Attribute to be saved
	 * @param unit
	 *            Unit of Attribute to be saved
	 * @param datatype
	 *            Datatype of Attribute to be saved
	 * @param category
	 *            Category of Attribute to be saved
	 */
	private void addPSSIFValue(String attrName, Individual subject,
			String value, String unit, String datatype, String category) {
		OntClass attrClass = pssifModel.getOntClass(URIs.pssifNS
				+ attrName.replaceAll("\\s+", "_"));
		// Add Attribute Node
		if (attrClass != null) {
			Individual subjectAttr = model.createIndividual(null, (attrClass));

			// Add Value
			subjectAttr.addProperty(
					pssifModel.getDatatypeProperty(URIs.pssifNS + "value"),
					value);
			// Add Unit
			subjectAttr
					.addProperty(
							pssifModel.getDatatypeProperty(URIs.pssifNS
									+ "unit"), unit);
			// Add Datatype
			subjectAttr.addProperty(
					pssifModel.getDatatypeProperty(URIs.pssifNS + "datatype"),
					datatype);
			// Add Category
			subjectAttr.addProperty(
					pssifModel.getDatatypeProperty(URIs.pssifNS + "category"),
					category);
			subject.addProperty(
					pssifModel.getObjectProperty(URIs.PROP_ATTR),
					subjectAttr);
		} else {
			if (PSSIFConstants.builtinAttributes.contains(attrName))
				subject.addProperty(
						pssifModel.getDatatypeProperty(URIs.pssifNS + attrName),
						value);
		}
	}

}
