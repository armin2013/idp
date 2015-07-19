package de.tum.pssif.core.metamodel.external;

import com.hp.hpl.jena.rdf.model.Property;

import de.tum.pssif.core.common.PSSIFConstants;

public class URIs {
	// URIs
	public static final String serviceUri = "http://vmkrcmar2.informatik.tu-muenchen.de:3030/PSSIF";
	public static final String pssifUri = "http://www.sfb768.tum.de/voc/pssif/ns";
	public static final String pssifNS = pssifUri.concat("#");

	public static final String baseUri = "http://www.sfb768.tum.de/voc/graph/ns";
	public static final String modelUri = "http://www.sfb768.tum.de/pssifmodel";
	public static final String modelNS = modelUri + "#";

	public static final String namespace = baseUri.concat("#");
	public static final String uriNode = pssifNS + "Node";
	public static final String uriEdge = pssifNS + "Edge";
	public static final String uriJunctionNode = pssifNS + "Conjunction";
	public static final String uriAttribute = pssifNS + "Attribute";

	public static final String PROP_ATTR = URIs.pssifNS.concat("attribute");
	public static final String PROP_ATTR_NAME = URIs.pssifNS.concat("name");
	public static final String PROP_ATTR_VALUE = URIs.namespace.concat("value");

	public static final String PROP_ATTR_UNIT = URIs.pssifNS.concat("unit");
	public static final String PROP_ATTR_DATATYPE = URIs.pssifNS
			.concat("datatype");
	public static final String PROP_ATTR_CATEGORY = URIs.pssifNS
			.concat("category");

	// Property of Type

	public static final String PROP_ID = pssifNS + "id";
	public static final String PROP_GLOBALID = pssifNS + "globalId";

	// Properties of in and out going nodes
	public static final String PROP_NODE_IN = pssifNS.concat("source");
	public static final String PROP_NODE_OUT = pssifNS.concat("target");

	// Modelname
	public static final String modelname = "PSSIFModel";
}
