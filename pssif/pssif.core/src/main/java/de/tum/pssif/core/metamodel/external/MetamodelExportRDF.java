package de.tum.pssif.core.metamodel.external;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.hp.hpl.jena.ontology.AllValuesFromRestriction;
import com.hp.hpl.jena.ontology.HasValueRestriction;
import com.hp.hpl.jena.ontology.IntersectionClass;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Tupel;

/**
 * Responsible for exporting the Metamodel to RDF
 * 
 * @author Armin
 * 
 */
public class MetamodelExportRDF {

	String SOURCE = "http://www.sfb768.tum.de/voc/pssif/ns";
	String NS = SOURCE + "#";
	OntModel base;
	private HashMap<String, MetamodelConjunction> conjunctions;
	private HashMap<String, MetamodelNode> nodes;
	private HashMap<String, MetamodelEdge> edges;
	private HashMap<String, MetamodelAttribute> attributes;

	private ArrayList<MetamodelNode> byDeletedParentAffectedNodes = new ArrayList<MetamodelNode>();
	private ArrayList<MetamodelEdge> byDeletedParentAffectedEdges = new ArrayList<MetamodelEdge>();

	/**
	 * Constructor for the exporter
	 */
	public MetamodelExportRDF() {
		base = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF);
		this.base.setNsPrefix("", URIs.pssifNS);

		// base.read( SOURCE, "RDF/XML" );
		resetData();
	}

	/**
	 * Call if a single components content is changed to save the changes
	 * 
	 * @param originalName
	 *            The original name of the component (before it was potentially
	 *            changed)
	 * @param component
	 *            The changed component
	 */
	public void saveChangedComponent(String originalName,
			MetamodelComponent component) {

		if (component.getType().equals("CONJUNCTION")) {
			if (conjunctions.containsKey(originalName)) {
				conjunctions.remove(originalName);
			}
			conjunctions.put(component.getName(),
					(MetamodelConjunction) component);
			writeMetaModelToXML();

		} else if (component.getType().equals("NODE")) {

			if (nodes.containsKey(originalName)) {
				nodes.remove(originalName);
			}
			nodes.put(component.getName(), (MetamodelNode) component);
			writeMetaModelToXML();

		} else if (component.getType().equals("EDGE")) {

			if (edges.containsKey(originalName)) {
				edges.remove(originalName);
			}
			edges.put(component.getName(), (MetamodelEdge) component);
			writeMetaModelToXML();

		} else {
			if (attributes.containsKey(originalName)) {
				attributes.remove(originalName);
			}
			attributes.put(component.getName(), (MetamodelAttribute) component);
			writeMetaModelToXML();
		}
	}

	/**
	 * Call if a single component is supposed to be removed for the metamodel
	 * 
	 * @param componentToDelete
	 *            The component to delete
	 * @param selectedItem
	 *            The decision what is supposed to happen with 'parent => child'
	 *            relationships if any
	 */
	public void removeComponent(MetamodelComponent componentToDelete,
			int selectedItem) {
		switch (componentToDelete.getType()) {
		// Set affected components
		case "NODE":
			resetByDeletedParentAffectedNodes(componentToDelete);
			// resetAffectedParentNode((MetamodelNode) componentToDelete);
			removeMappingsInvolvingThisNode(componentToDelete);
			break;
		case "EDGE":
			resetByDeletedParentAffectedEdges(componentToDelete);
			// resetAffectedParentEdge((MetamodelEdge) componentToDelete);
			break;
		case "ATTRIBUTE":
			removeAttributeReferencesInvolvingThisAttribute((MetamodelAttribute) componentToDelete);
			break;
		}

		switch (selectedItem) {
		case -1:
			// do nothing at all
			break;
		case 0: // Set parent of children to parent of deleted object
			if (componentToDelete.getType().equals("NODE")) {
				for (MetamodelNode node : byDeletedParentAffectedNodes) {
					node.setParent(((MetamodelNode) componentToDelete)
							.getParent());
				}
			} else {
				for (MetamodelEdge edge : byDeletedParentAffectedEdges) {
					edge.setParent(((MetamodelEdge) componentToDelete)
							.getParent());
				}
			}
			removeComponentFromList(componentToDelete);
			writeMetaModelToXML();
			break;
		case 1: // Set parent of children to null
			if (componentToDelete.getType().equals("NODE")) {
				for (MetamodelNode node : byDeletedParentAffectedNodes) {
					node.setParent(null);
				}
			} else {
				for (MetamodelEdge edge : byDeletedParentAffectedEdges) {
					edge.setParent(null);
				}
			}
			removeComponentFromList(componentToDelete);
			writeMetaModelToXML();
			break;
		case 2:
			// do nothing at all
			break;
		case 3: // just remove the component (for components without any 'parent
				// => child' relations)
			removeComponentFromList(componentToDelete);
			writeMetaModelToXML();
		}
	}

	/**
	 * Write the entire metamodel to RDF
	 */
	public void writeMetaModelToXML() {

		base.createObjectProperty(NS + "attribute");
		base.createObjectProperty(NS + "source");
		base.createObjectProperty(NS + "target");

		OntClass node = base.createClass(NS + "Node");
		node.addLabel("Node", null);
		OntClass edge = base.createClass(NS + "Edge");
		edge.addLabel("Edge", null);
		edge.setDisjointWith(node);
		node.setDisjointWith(edge);
		base.createClass(NS + "Attribute");
		base.createClass(NS + "Conjunction");
		base.createAnnotationProperty(NS + "tag");
		base.createDatatypeProperty(NS + "name");
		base.createDatatypeProperty(NS + "group");

		base.createDatatypeProperty(NS + "value");
		base.createDatatypeProperty(NS + "datatype");
		base.createDatatypeProperty(NS + "visibility");
		base.createDatatypeProperty(NS + "category");
		base.createDatatypeProperty(NS + "unit");

		for (String string : PSSIFConstants.builtinAttributes) {
			base.createDatatypeProperty(NS + string);
		}

		addConjunctions();
		addNodes();
		addEdges();
		addAttributes();

		// Path to file
		// String path = System.getProperty("user.dir");
		// path = path.substring(0, path.length() - 9) + "Meta-Modell.xml";
		String path = PSSIFConstants.META_MODEL_PATH;
		base.createOntology(URIs.pssifUri);
		
		try {
			base.write(new FileOutputStream(new File(path)), "TURTLE");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("File saved!");

	}

	private void addConjunctions() {

		// Conjunctions elements

		for (MetamodelConjunction component : conjunctions.values()) {
			OntClass node = base.createClass(NS
					+ component.getName().replaceAll("\\s+", "_"));
			node.addSuperClass(base.getOntClass(NS + "Conjunction"));

			node.addProperty(base.getAnnotationProperty(NS + "tag"),
					base.createLiteral(component.getTag()));
			node.addLabel(base.createLiteral(component.getName()));

			// node.addProperty(base.getDatatypeProperty(NS + "name"),
			// component.getName());
			// node.addProperty(base.getDatatypeProperty(NS + "tag"),
			// component.getTag());

		}
		
		ExtendedIterator<OntClass> i1 = base.getOntClass(NS+"Conjunction").listSubClasses(false);
		while(i1.hasNext()){
			OntClass c1=i1.next();
			ExtendedIterator<OntClass> i2 = base.getOntClass(NS+"Conjunction").listSubClasses(false);
			while(i2.hasNext()){
				OntClass c2 = i2.next();
				if (!c1.hasSuperClass(c2,false) && !c1.hasSubClass(c2,false))
					c1.addDisjointWith(c2);
			}
		}

	}

	private void addNodes() {

		// Nodes elements

		for (MetamodelNode currentNode : nodes.values()) {

			OntClass node = base.createClass(NS
					+ currentNode.getName().replaceAll("\\s+", "_"));
			node.addSuperClass(base.getOntClass(NS + "Node"));

			node.addProperty(base.getAnnotationProperty(NS + "tag"),
					base.createLiteral(currentNode.getTag()));
			node.addLabel(base.createLiteral(currentNode.getName()));

			if (currentNode.getParent() != null) {
				node.addSuperClass(base.createClass(NS
						+ currentNode.getParent().getName()
								.replaceAll("\\s+", "_")));
			}

			if (currentNode.getConnectedAttributes() != null) {
				ArrayList<AllValuesFromRestriction> rest = new ArrayList<AllValuesFromRestriction>();
				for (String connectedAttribute : currentNode
						.getConnectedAttributes()) {
					rest.add(base.createAllValuesFromRestriction(
							null,
							base.getObjectProperty(NS + "attribute"),
							base.createClass(NS
									+ connectedAttribute
											.replaceAll("\\s+", "_"))));
				}
				if (rest.size() > 0) {
					UnionClass unionClass = base
							.createUnionClass(null, base.createList(rest
									.toArray(new RDFNode[rest.size()])));
					node.addSuperClass(unionClass);
				}
			}
			
		}
		
		ExtendedIterator<OntClass> i1 = base.getOntClass(NS+"Node").listSubClasses(false);
		while(i1.hasNext()){
			OntClass c1=i1.next();
			ExtendedIterator<OntClass> i2 = base.getOntClass(NS+"Node").listSubClasses(false);
			while(i2.hasNext()){
				OntClass c2 = i2.next();
				if (!c1.hasSuperClass(c2,false) && !c1.hasSubClass(c2,false))
					c1.addDisjointWith(c2);
			}
		}
	}

	private void addEdges() {

		// Edges elements

		for (MetamodelEdge currentEdge : edges.values()) {
			OntClass edge = base.createClass(NS
					+ currentEdge.getName().replaceAll("\\s+", "_"));
			edge.addSuperClass(base.getOntClass(NS + "Edge"));
			edge.addProperty(base.getAnnotationProperty(NS + "tag"),
					base.createLiteral(currentEdge.getTag()));
			edge.addLabel(base.createLiteral(currentEdge.getName()));
			// edge.addProperty(base.getDatatypeProperty(NS + "name"),
			// currentEdge.getName());

			if (currentEdge.getParent() != null) {
				edge.addSuperClass(base.createClass(NS
						+ currentEdge.getParent().getName()
								.replaceAll("\\s+", "_")));
			}

			if (currentEdge.getMappings() != null) {
				List<OntClass> l = new ArrayList<OntClass>();
				for (Tupel currentMapping : currentEdge.getMappings()) {
					AllValuesFromRestriction r1 = base
							.createAllValuesFromRestriction(
									null,
									base.getObjectProperty(NS + "source"),
									base.createClass(NS
											+ currentMapping.getFirst()
													.replaceAll("\\s+", "_")));
					AllValuesFromRestriction r2 = base
							.createAllValuesFromRestriction(
									null,
									base.getObjectProperty(NS + "target"),
									base.createClass(NS
											+ currentMapping.getSecond()
													.replaceAll("\\s+", "_")));
					l.add(base.createIntersectionClass(null,
							base.createList(new RDFNode[] { r1, r2 })));
				}

				UnionClass unionClass = base.createUnionClass(null,
						base.createList(l.toArray(new RDFNode[l.size()])));
				edge.addSuperClass(unionClass);
			}
		}
		
		
		ExtendedIterator<OntClass> i1 = base.getOntClass(NS+"Edge").listSubClasses(false);
		while(i1.hasNext()){
			OntClass c1=i1.next();
			ExtendedIterator<OntClass> i2 = base.getOntClass(NS+"Edge").listSubClasses(false);
			while(i2.hasNext()){
				OntClass c2 = i2.next();
				if (!c1.hasSuperClass(c2,false) && !c1.hasSubClass(c2,false))
					c1.addDisjointWith(c2);
			}
		}
	}

	private void addAttributes() {

		for (MetamodelAttribute currentAttribute : attributes.values()) {

			OntClass attribute = base.createClass(NS
					+ currentAttribute.getName().replaceAll("\\s+", "_"));
			attribute.addSuperClass(base.getOntClass(NS + "Attribute"));

			attribute.addProperty(base.getAnnotationProperty(NS + "tag"),
					base.createLiteral(currentAttribute.getTag()));

			attribute.addLabel(base.createLiteral(currentAttribute.getName()));

			ArrayList<HasValueRestriction> rest = new ArrayList<HasValueRestriction>();

			rest.add(base.createHasValueRestriction(null,
					base.getDatatypeProperty(NS + "group"),
					base.createLiteral(currentAttribute.getAttributeGroup())));

			rest.add(base.createHasValueRestriction(null,
					base.getDatatypeProperty(NS + "datatype"),
					base.createLiteral(currentAttribute.getAttributeDataType())));

			rest.add(base.createHasValueRestriction(
					null,
					base.getDatatypeProperty(NS + "visibility"),
					base.createLiteral(currentAttribute.getAttributeVisiblity()
							+ "")));

			rest.add(base.createHasValueRestriction(null,
					base.getDatatypeProperty(NS + "unit"),
					base.createLiteral(currentAttribute.getAttributeUnit())));
			rest.add(base.createHasValueRestriction(null,
					base.getDatatypeProperty(NS + "category"),
					base.createLiteral(currentAttribute.getAttributeCategory())));

			IntersectionClass restrictions = base.createIntersectionClass(null,
					base.createList(rest.toArray(new RDFNode[rest.size()])));
			attribute.addSuperClass(restrictions);
		}

		ExtendedIterator<OntClass> i1 = base.getOntClass(NS+"Attribute").listSubClasses(false);
		while(i1.hasNext()){
			OntClass c1=i1.next();
			ExtendedIterator<OntClass> i2 = base.getOntClass(NS+"Attribute").listSubClasses(false);
			while(i2.hasNext()){
				OntClass c2 = i2.next();
				if (!c1.hasSuperClass(c2,false) && !c1.hasSubClass(c2,false))
					c1.addDisjointWith(c2);
			}
		}
	}

	public void resetData() {
		conjunctions = PSSIFCanonicMetamodelCreator.conjunctions;
		nodes = PSSIFCanonicMetamodelCreator.nodes;
		edges = PSSIFCanonicMetamodelCreator.edges;
		attributes = PSSIFCanonicMetamodelCreator.attributes;
	}

	public void resetByDeletedParentAffectedNodes(
			MetamodelComponent componentToDelete) {

		// Check for interdependencies
		// Get all involved Components
		for (MetamodelNode node : nodes.values()) {
			// Get every component with the deleted component as a
			// parent
			if (node.getParent() != null
					&& node.getParent().equals(componentToDelete)) {
				byDeletedParentAffectedNodes.add(node);
			}
		}
	}

	public void resetByDeletedParentAffectedEdges(
			MetamodelComponent componentToDelete) {

		// Check for interdependencies
		// Get all involved Components
		for (MetamodelEdge edge : edges.values()) {
			// Get every component with the deleted component as a
			// parent
			if (edge.getParent() != null
					&& edge.getParent().equals(componentToDelete)) {
				byDeletedParentAffectedEdges.add(edge);
			}
		}
	}

	/**
	 * Removes every mapping for edges where the deleted node is involved
	 * 
	 * @param nodeToDelete
	 *            The node to delete
	 * @return
	 */
	private void removeMappingsInvolvingThisNode(MetamodelComponent nodeToDelete) {

		for (MetamodelEdge currentEdge : edges.values()) {
			if (currentEdge.getMappings() != null) {
				ArrayList<Tupel> affectedMappings = new ArrayList<Tupel>();
				for (Tupel mapping : currentEdge.getMappings()) {
					if (mapping.contains(nodeToDelete.getName())) {
						affectedMappings.add(mapping);
					}
				}

				for (Tupel tupel : affectedMappings) {
					currentEdge.getMappings().remove(tupel);
				}
			}
		}
	}

	/**
	 * Removes this attribute from every node having it as an attribute
	 * 
	 * @param attributeToDelete
	 *            The attribute to delete
	 * @return
	 */
	private void removeAttributeReferencesInvolvingThisAttribute(
			MetamodelAttribute attributeToDelete) {

		for (MetamodelNode currentNode : nodes.values()) {

			if (currentNode.getConnectedAttributes() != null
					&& currentNode.getConnectedAttributes().contains(
							attributeToDelete.getName())) {

				currentNode.getConnectedAttributes().remove(
						attributeToDelete.getName());
			}
		}
	}

	public void removeComponentFromList(MetamodelComponent componentToDelete) {
		if (componentToDelete.getType().equals("CONJUNCTION")) {
			conjunctions.remove(componentToDelete.getName());
		} else if (componentToDelete.getType().equals("NODE")) {
			nodes.remove(componentToDelete.getName());
		} else if (componentToDelete.getType().equals("EDGE")) {
			edges.remove(componentToDelete.getName());
		} else {
			attributes.remove(componentToDelete.getName());
		}
	}
}
