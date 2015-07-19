package de.tum.pssif.transform.mapper.rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import javax.swing.JOptionPane;

import cern.colt.matrix.linalg.Property;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.DataType;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.ElementType;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.PrimitiveDataType;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.core.metamodel.external.URIs;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Element;
import de.tum.pssif.core.model.JunctionNode;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.core.model.impl.ModelImpl;

public class RDFInputMapper {

	private OntModel infModel;
	// OntModel pssifOntModel;

	private de.tum.pssif.core.model.Model pssifModel;
	private Metamodel metamodel = PSSIFCanonicMetamodelCreator.create();
	private HashMap<String, Node> nodes = new HashMap<>();
	private HashMap<String, Node> junctionNodes = new HashMap<>();

	private HashMap<String, Edge> edges = new HashMap<>();
	private static int id = 0;

	public RDFInputMapper(OntModel infModel, Metamodel metamodel, Model model) {
		setPssifModel(model);
		this.infModel = infModel;
		String path = PSSIFConstants.META_MODEL_PATH;
		try {
			this.infModel.read(new FileInputStream(new File(path)),
					URIs.pssifNS, "TURTLE");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		this.metamodel = metamodel;

		ExtendedIterator<? extends OntResource> subjectNodes = infModel
				.listIndividuals();
		HashMap<Individual, EdgeType> tmpEdges = new HashMap<Individual, EdgeType>();
		while (subjectNodes.hasNext()) {
			Individual next = subjectNodes.next().asIndividual();

			ExtendedIterator<OntClass> sc = next.listOntClasses(false);
			OntClass type = null;
			boolean node = false;
			boolean jnode = false;
			boolean edge = false;
			while (sc.hasNext()) {
				OntClass e = sc.next();
				if (!e.getURI().equals(URIs.uriNode)
						&& !e.getURI().equals(URIs.uriJunctionNode)
						&& e.hasSuperClass(infModel.getOntClass(URIs.uriNode),
								false)) {
					type = e;
					node = true;
				}
				if (type == null && e.getURI().equals(URIs.uriNode)) {
					type = e;
					node = true;
				}
				if (!e.getURI().equals(URIs.uriJunctionNode)
						&& e.hasSuperClass(
								infModel.getOntClass(URIs.uriJunctionNode),
								false)) {
					type = e;
					jnode = true;
				}
				// junction node subclass of node, attribute
				if ((type == null || type.getURI().equals(URIs.uriNode) || type
						.getURI().equals(URIs.uriAttribute))
						&& e.getURI().equals(URIs.uriJunctionNode)) {
					type = e;
					jnode = true;
				}
				if (e.hasSuperClass(infModel.getOntClass(URIs.uriEdge), false)
						&& !e.getURI().equals(URIs.uriEdge)) {
					type = e;
					edge = true;
				}
				if (type == null && e.getURI().equals(URIs.uriEdge)) {
					type = e;
					edge = true;
				}
			}
			// NODES
			if (type != null && node && !jnode) {
				try {

					// find NodeType
					String nodeTypeName = type.getLabel(null);
					if (nodeTypeName == null)
						nodeTypeName = "Node";
					// Create new Node and NodeType
					NodeType nodeType = metamodel.getNodeType(nodeTypeName)
							.getOne();
					Node newNode = nodeType.create(getPssifModel());
					String id = createAttributeOrAnnotation(next, nodeType,
							newNode);
					// add node to hashmap
					nodes.put(id, newNode);

				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Problem with constructing a Node", "PSSIF",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			// JUNCTION NODES
			if (type != null && jnode) {
				try {
					// find NodeType
					String jNodeTypeName = type.getLabel(null);
					if (jNodeTypeName == null)
						jNodeTypeName = "JunctionNode";
					// Create new Node and NodeType
					JunctionNodeType jNodeType = metamodel.getJunctionNodeType(
							jNodeTypeName).getOne();
					Node newNode = jNodeType.create(getPssifModel());
					String id = createAttributeOrAnnotation(next, jNodeType,
							newNode);
					// add node to hashmap
					junctionNodes.put(id, newNode);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Problem with constructing a Node", "PSSIF",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			// EDGES

			if (type != null && edge) {

				// find EdgeType
				String edgeTypeName = type.getLabel(null);
				if (edgeTypeName == null)
					edgeTypeName = "Edge";
				// create new Edge and EdgeType
				EdgeType edgeType = metamodel.getEdgeType(edgeTypeName)
						.getOne();
				tmpEdges.put(next, edgeType);
			}
		}

		Iterator<Entry<Individual, EdgeType>> it = tmpEdges.entrySet()
				.iterator();
		while (it.hasNext()) {
			try {
				Entry<Individual, EdgeType> kv = it.next();
				Individual next = kv.getKey();
				EdgeType edgeType = kv.getValue();
				Edge newEdge = constructInOutNodes(next, edgeType);
				// add annotations and attributes to Edge
				String id = createAttributeOrAnnotation(next, edgeType, newEdge);
				// get incoming and outgoing nodes

				// add edge to Hashmap
				edges.put(id, newEdge);

			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Problem with constructing an Edge", "PSSIF",
						JOptionPane.ERROR_MESSAGE);
			}

		}

	}

	public RDFInputMapper(OntModel infModel, Metamodel metamodel) {
		// pssifOntModel = OntDocumentManager.getInstance().getOntology(
		// URIs.pssifNS, OntModelSpec.OWL_DL_MEM_TRANS_INF);
		this(infModel, metamodel, new ModelImpl());
	}

	private String generateId(Resource subject) {
		String id;
		id = String.valueOf(RDFInputMapper.id);
		RDFInputMapper.id += 1;
		return id;
	}

	/**
	 * Gets the incoming and outgoing Nodes of an Edge. Adds the Nodes to the
	 * Edge. Adds the Edge to the Nodes.
	 * 
	 * @param subject
	 *            Resource of the Edge
	 * @param edgeType
	 *            EdgeType of the resource Edge
	 */
	private Edge constructInOutNodes(Individual subject, EdgeType edgeType) {
		Node nodeIn = null;
		Node nodeOut = null;
		NodeTypeBase nodeTypeIn = null;
		NodeTypeBase nodeTypeOut = null;

		// get Statements for incoming and outgoing Nodes
		Individual stmtNodeIn = ((OntResource) subject
				.getPropertyValue(infModel.getObjectProperty(URIs.PROP_NODE_IN)))
				.asIndividual();
		Individual stmtNodeOut = ((OntResource) subject
				.getPropertyValue(infModel
						.getObjectProperty(URIs.PROP_NODE_OUT))).asIndividual();

		if (stmtNodeIn != null && stmtNodeOut != null) {
			// get Node

			OntClass nodeInType = stmtNodeIn.getOntClass();
			OntClass nodeOutType = stmtNodeOut.getOntClass();

			// TODO JunctionNodeType
			// BASENODETYPE
			if (nodeInType.getURI().toString().contains(URIs.uriJunctionNode)) {
				nodeTypeIn = metamodel.getJunctionNodeType(
						nodeInType.getLabel(null)).getOne();
			} else
				nodeTypeIn = metamodel.getNodeType(nodeInType.getLabel(null))
						.getOne();

			if (nodeOutType.getURI().toString().contains(URIs.uriJunctionNode)) {
				nodeTypeOut = metamodel.getJunctionNodeType(
						nodeOutType.getLabel(null)).getOne();
			} else
				nodeTypeOut = metamodel.getNodeType(nodeOutType.getLabel(null))
						.getOne();

			if (nodeInType.getURI().equals(URIs.uriJunctionNode)
					|| nodeInType.hasSuperClass(
							infModel.getOntClass(URIs.uriJunctionNode), false))
				nodeIn = junctionNodes
						.get(stmtNodeIn.getProperty(
								infModel.getDatatypeProperty(URIs.PROP_ID))
								.getString());
			else
				nodeIn = nodes
						.get(stmtNodeIn.getProperty(
								infModel.getDatatypeProperty(URIs.PROP_ID))
								.getString());

			if (nodeOutType.hasSuperClass(infModel
					.getOntClass(URIs.uriJunctionNode))
					|| nodeOutType.hasSuperClass(
							infModel.getOntClass(URIs.uriJunctionNode), false))
				nodeOut = junctionNodes
						.get(stmtNodeOut.getProperty(
								infModel.getDatatypeProperty(URIs.PROP_ID))
								.getString());
			else
				nodeOut = nodes
						.get(stmtNodeOut.getProperty(
								infModel.getDatatypeProperty(URIs.PROP_ID))
								.getString());

		}

		PSSIFOption<ConnectionMapping> mapping = edgeType.getMapping(
				nodeTypeIn, nodeTypeOut);
		Edge newEdge = mapping.getOne()
				.create(getPssifModel(), nodeIn, nodeOut);

		return newEdge;
	}

	// ATTRIBUTES

	/**
	 * Adds Attributes and Annotations to a Node/Edge/JunctionNode from a given
	 * resource
	 * 
	 * @param subject
	 *            Resource of the Node/Edge/JunctionNode
	 * @param type
	 *            Type of the resource
	 * @param elem
	 *            Node/Edge/JunctionNode
	 */
	private String createAttributeOrAnnotation(Individual subject,
			ElementType type, Element elem) {
		String id = null;
		// Element ID
		for (String attr : PSSIFConstants.builtinAttributes) {
			RDFNode propval = subject.getPropertyValue(infModel
					.getDatatypeProperty(URIs.pssifNS + attr));

			if (propval != null) {
				PSSIFOption<Attribute> attrName = type.getAttribute(attr);

				saveAttribute(elem, attrName, propval.asLiteral().getString());
				if (attr == PSSIFConstants.BUILTIN_ATTRIBUTE_ID)
					id = propval.asLiteral().getString();
			}
		}
		if (id == null)
			id = generateId(subject);

		for (NodeIterator iter = subject.listPropertyValues(infModel
				.getObjectProperty(URIs.PROP_ATTR)); iter.hasNext();) {
			RDFNode stmt = iter.next();

			// Attribute Type

			if (stmt.canAs(Individual.class)) {
				Individual att = stmt.as(Individual.class);
				String attType = att.getOntClass().getLabel(null);
				String value = att.getProperty(
						infModel.getDatatypeProperty(URIs.PROP_ATTR_VALUE))
						.getString();
				// get the value of the Attribute

				if (attType != null && value != null) {
					// Add the Attribute to the element
					PSSIFOption<Attribute> attributePssif = type
							.getAttribute(attType);
					saveAttribute(elem, attributePssif, value);

				}
			}
		}
		return id;
	}

	public boolean saveAttribute(Element elem, PSSIFOption<Attribute> tmp,
			String value) {
		if (tmp.isOne()) {
			Attribute attribute = tmp.getOne();
			DataType attrType = attribute.getType();

			if (attrType.equals(PrimitiveDataType.BOOLEAN)) {
				try {
					attribute.set(elem, PSSIFOption
							.one(PrimitiveDataType.BOOLEAN.fromObject(value)));

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					return false;
				}

			} else if (attrType.equals(PrimitiveDataType.DATE)) {
				try {
					SimpleDateFormat formatter = new SimpleDateFormat(
							"dd.MM.yyyy");
					Date date = formatter.parse(value);

					PSSIFValue res = PrimitiveDataType.DATE.fromObject(date);

					attribute.set(elem, PSSIFOption.one(res));
				} catch (IllegalArgumentException | ParseException e) {
					e.printStackTrace();
					return false;
				}
			}

			else if (attrType.equals(PrimitiveDataType.DECIMAL)) {
				try {
					PSSIFValue res = PrimitiveDataType.DECIMAL
							.fromObject(new BigDecimal(value));

					attribute.set(elem, PSSIFOption.one(res));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					return false;
				}
			}

			else {
				try {

					attribute.set(elem,
							PSSIFOption.one(attrType.fromObject(value)));

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public de.tum.pssif.core.model.Model getPssifModel() {
		return pssifModel;
	}

	public void setPssifModel(de.tum.pssif.core.model.Model pssifModel) {
		this.pssifModel = pssifModel;
	}
}
