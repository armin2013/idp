package de.tum.pssif.transform.mapper.db;

import graph.model.MyEdge;
import graph.model.MyJunctionNode;
import graph.model.MyNode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import model.ModelBuilder;
import model.MyModelContainer;

import org.pssif.mainProcesses.Methods;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.external.URIs;
import de.tum.pssif.core.metamodel.impl.GetValueOperation;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.JunctionNode;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.transform.mapper.rdf.RDFOutputMapper;

// Model to DB Mapper
public class PssifToDBMapperImpl extends RDFOutputMapper implements PssifToDBMapper {

	public static OntModel rdfModel;
	public static DatabaseImpl db;

	// Variables to record changes that have to be saved in the Database
	public static LinkedList<MyNode> newNodes = new LinkedList<>();
	public static LinkedList<MyEdge> newEdges = new LinkedList<>();
	public static LinkedList<MyJunctionNode> newJunctionNodes = new LinkedList<>();
	public static LinkedList<MyNode> changedNodes = new LinkedList<>();
	public static LinkedList<MyEdge> changedEdges = new LinkedList<>();
	public static LinkedList<MyJunctionNode> changedJunctionNodes = new LinkedList<>();
	public static LinkedList<MyNode> deletedNodes = new LinkedList<>();
	public static LinkedList<MyEdge> deletedEdges = new LinkedList<>();
	public static List<Resource> deletedEdgesRes = new LinkedList<>();
	public static LinkedList<MyJunctionNode> deletedJunctionNodes = new LinkedList<>();
	// Flag, if the database could be deleted completely because of
	// not importing the database before importing the model...
	public static boolean deleteAll = false;
	// Flat if there was a Merge between 2 models
	public static boolean merge = false;
	private MyModelContainer mymodel;

	public PssifToDBMapperImpl() {
		super(ModelBuilder.activeModel);
		db = new DatabaseImpl();
		rdfModel = super.model ;
		mymodel=ModelBuilder.activeModel;
	}

	// MODEL

	@Override
	public void modelToDB() {
		// db.begin(ReadWrite.WRITE);
		// rdfModel.begin();
		rdfModel.removeAll();
		saveNodes(mymodel.getAllNodes());
		saveJunctionNodes(mymodel.getAllJunctionNodes());
		saveEdges(mymodel.getAllEdges());

		db.saveModel(rdfModel);
		clearAll();

		// db.commit();
		// db.end();
		// db.close();
	}

	@Override
	public void saveToDB() {
		// db.begin(ReadWrite.WRITE);
		// rdfModel.begin();

		// save first then delete because of Version Management -> so it can
		// happen that Edges are in new and deleted List, so delete should be
		// your last operation.
		saveNodes(newNodes);
		saveEdges(newEdges);
		saveJunctionNodes(newJunctionNodes);

		for (MyNode node : deletedNodes)
			removeNode(node);
		for (MyJunctionNode junctionnode : deletedJunctionNodes)
			removeJunctionNode(junctionnode);
		for (MyEdge edge : deletedEdges)
			removeEdge(edge);

		changeElements();

		db.saveModel(rdfModel);

		// db.commit();
		// db.end();
		// db.close();

		// changes are in DB -> clear all Lists
		clearAll();
	}

	@Override
	public void removeModel() {
		// db.begin(ReadWrite.WRITE);
		// rdfModel.begin();
		rdfModel.removeAll();
		// rdfModel.commit();
		db.removeNamedModel(rdfModel.toString());
		// db.commit();
		// db.end();
	}

	// NODES

	/**
	 * Saves all given Nodes of the Class MyNode in Database
	 * 
	 * @param nodes
	 *            List of Class MyNode to be saved
	 */
	private void saveNodes(LinkedList<MyNode> nodes) {
		if (nodes != null) {
			for (Iterator<MyNode> it = nodes.iterator(); it.hasNext();) {
				MyNode myNode = it.next();
				addNode(myNode);
			}
		}
	}

	/**
	 * Removes a given Node of Class MyNode from Database
	 * 
	 * @param mynode
	 *            Node of Class MyNode to be removed
	 */
	private void removeNode(MyNode mynode) {
		Node node = mynode.getNode();
		String globalID = Methods.findGlobalID(node, mynode.getNodeType()
				.getType());
		removeElement(globalID);

	}

	private void removeElement(String globalID) {

		ResIterator individuals2 = rdfModel.listResourcesWithProperty(
				rdfModel.getDatatypeProperty(URIs.PROP_GLOBALID), globalID);

		// ATTRIBUTES

		if (individuals2.hasNext()) {
			Resource next = individuals2.next();
			Individual ind = next.as(Individual.class);
			NodeIterator individuals1 = ind.listPropertyValues(rdfModel
					.getObjectProperty(URIs.PROP_ATTR));
			if (individuals1.hasNext()) {
				RDFNode next1 = individuals1.next();
				next1.as(Individual.class).remove();
			}
			ind.remove();
		}
	}

	// JUNCTION NODES

	/**
	 * Saves all given JunctionNodes of the Class MyJunctionNode in Database
	 * 
	 * @param nodes
	 *            List of Class MyJunctionNode to be saved
	 */
	private void saveJunctionNodes(LinkedList<MyJunctionNode> nodes) {
		if (nodes != null) {
			for (Iterator<MyJunctionNode> it = nodes.iterator(); it.hasNext();) {
				MyJunctionNode myJNode = it.next();
				addJunctionNode(myJNode);
			}
		}
	}

	/**
	 * Removes a given JunctionNode of Class MyJunctionNode from Database
	 * 
	 * @param mynode
	 *            Node of Class MyJunctionNode to be removed
	 */
	private void removeJunctionNode(MyJunctionNode mynode) {
		Node jn = mynode.getNode();
		String globalID = Methods.findGlobalID(jn, mynode.getNodeType()
				.getType());
		removeElement(globalID);
	}

	// EDGES

	/**
	 * Saves all given Edges of the Class MyEdge in Database
	 * 
	 * @param edges
	 *            List of Class MyEdge to be saved
	 */
	private void saveEdges(LinkedList<MyEdge> edges) {
		if (edges != null) {
			for (Iterator<MyEdge> it = edges.iterator(); it.hasNext();) {
				MyEdge myEdge = it.next();
				addEdge(myEdge);
			}
		}
	}

	/**
	 * Removes a given Edge of Class MyEdge from Database
	 * 
	 * @param myedge
	 *            Edge of Class MyEdge to be removed
	 */
	private void removeEdge(MyEdge myedge) {
		Edge edge = myedge.getEdge();
		String globalID = Methods.findGlobalID(edge, myedge.getEdgeType()
				.getType());
		removeElement(globalID);
	}

	// ANNOTATIONS
	// TODO: addAllAnnotations(n.getAnnotations(), subjectNode);

	/**
	 * Adds all Annotation to a Node/Edge/JunctionNode
	 * 
	 * @param annots
	 *            List of Annotations to be saved
	 * @param subject
	 *            Resource to which Annotation should be added
	 */
	private void addAllAnnotations(PSSIFOption<Entry<String, String>> annots,
			Resource subject) {
		for (Iterator<Entry<String, String>> it1 = annots.iterator(); it1
				.hasNext();) {
			Entry<String, String> annot = it1.next();
			addAnnotation(annot, subject);
		}
	}

	/**
	 * Removes all Annotation from a Node/Edge/JunctionNode
	 * 
	 * @param annots
	 *            List of Annotations to be removed
	 * @param subject
	 *            Resource from which Annotation should be removed
	 */
	private void removeAllAnnotations(
			PSSIFOption<Entry<String, String>> annots, Resource subject) {
		for (Iterator<Entry<String, String>> it1 = annots.iterator(); it1
				.hasNext();) {
			Entry<String, String> annot = it1.next();
			removeAnnotation(annot, subject);
		}
	}

	/**
	 * Adds a given Annotation to a Node/Edge/JunctionNode
	 * 
	 * @param annot
	 *            Entry-Set of Annotation to be added
	 * @param subject
	 *            Resource to which Annotation should be added
	 */
	private void addAnnotation(Entry<String, String> annot, Resource subject) {

	}

	/**
	 * Removes a given Annotation from a Node/Edge/JunctionNode
	 * 
	 * @param annot
	 *            Entry-Set of Annotation to be removed
	 * @param subject
	 *            Resource from which Annotation should be removed
	 */
	private void removeAnnotation(Entry<String, String> annot, Resource subject) {

	}

	/**
	 * Clears all Lists of Nodes/Edges/JunctionNodes that are changed or deleted
	 */
	public static void clearAll() {
		newNodes.clear();
		newEdges.clear();
		newJunctionNodes.clear();
		changedNodes.clear();
		changedEdges.clear();
		changedJunctionNodes.clear();
		deletedNodes.clear();
		deletedEdges.clear();
		deletedJunctionNodes.clear();
		deletedEdgesRes.clear();
	}

	/**
	 * Saves changed Edges/Nodes/JunctionNodes
	 */
	private void changeElements() {
		// first deletes changed Nodes from DB and than add them to DB
		for (MyNode node : changedNodes) {
			removeNode(node);
			addNode(node);
		}

		for (MyEdge edge : changedEdges) {
			removeEdge(edge);
			addEdge(edge);
		}

		for (MyJunctionNode jNode : changedJunctionNodes) {
			removeJunctionNode(jNode);
			addJunctionNode(jNode);
		}
	}
}
