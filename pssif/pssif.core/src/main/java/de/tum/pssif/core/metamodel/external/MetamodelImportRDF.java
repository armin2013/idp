package de.tum.pssif.core.metamodel.external;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.HasValueRestriction;
import com.hp.hpl.jena.ontology.IntersectionClass;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.model.Tupel;

/**
 * Initiates the import of the metamodel-rdf file
 * 
 * @author Armin
 *
 */
public class MetamodelImportRDF {
	private XMLContentHandler handler;
	OntModel base;
	String SOURCE = "http://www.sfb768.tum.de/voc/pssif/ns";
	String NS = SOURCE + "#";
	private HashMap<String, MetamodelConjunction> conjunctions = new HashMap<String, MetamodelConjunction>();
	private HashMap<String, MetamodelNode> nodes = new HashMap<String, MetamodelNode>();
	private HashMap<String, MetamodelEdge> edges = new HashMap<String, MetamodelEdge>();
	private HashMap<String, MetamodelAttribute> attributes = new HashMap<String, MetamodelAttribute>();
	private HashMap<String, String> tagMap = new HashMap<String, String>();

	/**
	 * Initiate the parser and return its result
	 * 
	 * @return A list of the component as imported by the parser
	 */
	public void runParser() {
		base = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		try {
			base.read(new FileInputStream(PSSIFConstants.META_MODEL_PATH), null, "TURTLE");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MetamodelConjunction currentConjunction;
		MetamodelNode currentNode;
		MetamodelEdge currentEdge;
		MetamodelAttribute currentAttribute;
		ExtendedIterator<OntClass> iterator = base.getOntClass(
				NS + "Conjunction").listSubClasses();
		// JUNCTIONS
		while (iterator.hasNext()) {
			OntClass o = iterator.next();

			String tag = o.getProperty(base.getAnnotationProperty(NS + "tag"))
					.getString();
			String name = o.getLabel(null);

			currentConjunction = new MetamodelConjunction(tag, name);
			conjunctions.put(name, currentConjunction);
			tagMap.put(tag, name);
		}

		iterator = base.getOntClass(NS + "Node").listSubClasses();
		// NODES
		while (iterator.hasNext()) {
			OntClass o = iterator.next();

			String tag = o.getProperty(base.getAnnotationProperty(NS + "tag"))
					.getString();
			String name = o.getLabel(null);

			currentNode = new MetamodelNode(tag, name);
			ExtendedIterator<OntClass> sc = o.listSuperClasses();
			OntClass parent = null;
			while (sc.hasNext()) {
				OntClass e = sc.next();
				if (e.getURI()!=null && !e.getURI().equals(NS+"Node"))
					parent = e;
			}

			if (parent != null) {
				currentNode.setParentAsString(parent.getLabel(null));
			}
			currentNode.setConnectedAttributes(new ArrayList<String>());

			ExtendedIterator<OntClass> i2 = o.listSuperClasses();
			while (i2.hasNext()) {
				OntClass sclass = i2.next();
				// Source Target Mappings
				if (sclass.isUnionClass()) {

					ExtendedIterator<? extends OntClass> i3 = sclass
							.asUnionClass().listOperands();
					while (i3.hasNext()) {
						Restriction restriction = i3.next().asRestriction();

						if (restriction.isAllValuesFromRestriction()) {

							if (restriction.getOnProperty().getURI()
									.equals(NS + "attribute"))
								currentNode.getConnectedAttributes().add(
										((OntClass) restriction
												.asAllValuesFromRestriction()
												.getAllValuesFrom())
												.getLabel(null));

						}
					}

				}
			}

			nodes.put(name, currentNode);
			tagMap.put(tag, name);
		}

		iterator = base.getOntClass(NS + "Edge").listSubClasses();
		// EDGES
		while (iterator.hasNext()) {
			OntClass o = iterator.next();

			String tag = o.getProperty(base.getAnnotationProperty(NS + "tag"))
					.getString();
			String name = o.getLabel(null);

			ExtendedIterator<OntClass> sc = o.listSuperClasses();
			OntClass parent = null;
			while (sc.hasNext()) {
				OntClass e = sc.next();
				if (e.getURI()!=null && !e.getURI().equals( NS + "Edge"))
					parent = e;
			}
			currentEdge = new MetamodelEdge(tag, name);
			if (parent != null) {
				currentEdge.setTempParent(parent.getLabel(null));
			}
			edges.put(name, currentEdge);
			tagMap.put(tag, name);

			ExtendedIterator<OntClass> i2 = o.listSuperClasses();
			while (i2.hasNext()) {
				OntClass sclass = i2.next();
				// Source Target Mappings
				if (sclass.isUnionClass()) {

					ExtendedIterator<? extends OntClass> i3 = sclass
							.asUnionClass().listOperands();
					while (i3.hasNext()) {
						OntClass op = (OntClass) i3.next();
						if (op.isIntersectionClass()) {
							IntersectionClass iclass = op.asIntersectionClass();
							currentEdge.setMappings(new ArrayList<Tupel>());
							ExtendedIterator<? extends OntClass> i4 = iclass
									.listOperands();

							currentEdge.setMappings(new ArrayList<Tupel>());
							String source = null;
							String target = null;

							while (i4.hasNext()) {
								Restriction restriction = i4.next()
										.asRestriction();

								if (restriction.isAllValuesFromRestriction()) {
									if (restriction.getOnProperty().getURI()
											.equals(NS + "source"))
										source = ((OntClass) restriction
												.asAllValuesFromRestriction()
												.getAllValuesFrom())
												.getLabel(null);
									if (restriction.getOnProperty().getURI()
											.equals(NS + "target"))
										target = ((OntClass) restriction
												.asAllValuesFromRestriction()
												.getAllValuesFrom())
												.getLabel(null);
								}
							}
							if (source != null && target != null)
								currentEdge.getMappings().add(
										new Tupel(source, target));
						}
					}
				}
			}
			// Case: Mapping
			// else if (localName.equals("MAPPINGS")) {
			// currentEdge.setMappings(new ArrayList<Tupel>());
			// } else if (localName.equals("MAPPING")) {
			// currentEdge.getMappings().add(new Tupel(atts.getValue("From"),
			// atts.getValue("To")));
			// }
			//
			// // Case: A new node connected attribute is read
			// else if (localName.equals("SELECTEDATTRIBUTES")) {
			// currentNode.setConnectedAttributes(new ArrayList<String>());
			// } else if (localName.equals("SELECTEDATTRIBUTE")) {
			// currentNode.getConnectedAttributes().add(atts.getValue(0));
			// }

		}

		iterator = base.getOntClass(NS + "Attribute").listSubClasses();
		// ATTRIBUTES
		while (iterator.hasNext()) {
			OntClass o = iterator.next();

			String tag = o.getProperty(base.getAnnotationProperty(NS + "tag"))
					.getString();
			String name = o.getLabel(null);

			currentAttribute = new MetamodelAttribute(tag, name);
			attributes.put(name, currentAttribute);
			tagMap.put(tag, name);

			ExtendedIterator<OntClass> i2 = o.listSuperClasses();
			while (i2.hasNext()) {
				OntClass sclass = i2.next();
				if (sclass.isIntersectionClass()) {
					IntersectionClass iclass = sclass.asIntersectionClass();
					ExtendedIterator<? extends OntClass> i = iclass
							.listOperands();
					while (i.hasNext()) {
						OntClass op = (OntClass) i.next();

						if (op.isRestriction()
								&& op.asRestriction().isHasValueRestriction()) {
							HasValueRestriction asHasValueRestriction = op
									.asRestriction().asHasValueRestriction();

							String uri = asHasValueRestriction.getOnProperty()
									.getURI();

							if (asHasValueRestriction.getHasValue().isLiteral()) {
								String value = asHasValueRestriction
										.getHasValue().asLiteral().getString();

								if (uri.equals(NS + "group")) {
									currentAttribute.setAttributeGroup(value);
								} else if (uri.equals(NS + "datatype")) {
									currentAttribute
											.setAttributeDataType(value);
								} else if (uri.equals(NS + "visible")) {
									currentAttribute
											.setAttributeVisiblity(Boolean
													.parseBoolean(value));
								} else if (uri.equals(NS + "category")) {
									currentAttribute
											.setAttributeCategory(value);
								} else if (uri.equals(NS + "unit")) {
									currentAttribute.setAttributeUnit(value);
								}
							}
						}

					}
				}
			}
		}
	}

	/**
	 * Get a HashMap containing every tag of the imported components
	 * 
	 * @return The tags
	 */
	public HashMap<String, String> getTagMap() {

		return tagMap;
	}

	/**
	 * Get a HashMap containing every conjunction
	 * 
	 * @return The conjunctions
	 */
	public HashMap<String, MetamodelConjunction> getConjunctions() {
		return conjunctions;
	}

	/**
	 * Get a HashMap containing every node
	 * 
	 * @return The nodes
	 */
	public HashMap<String, MetamodelNode> getNodes() {
		return nodes;
	}

	/**
	 * Get a HashMap containing every edge
	 * 
	 * @return The edges
	 */
	public HashMap<String, MetamodelEdge> getEdges() {
		return edges;
	}

	/**
	 * Get a HashMap containing every attribute
	 * 
	 * @return The attributes
	 */
	public HashMap<String, MetamodelAttribute> getAttributes() {
		return attributes;
	}
}
