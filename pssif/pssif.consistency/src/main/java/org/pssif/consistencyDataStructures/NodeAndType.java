package org.pssif.consistencyDataStructures;

import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.model.Node;

/**
 This file is part of PSSIF Consistency. It is responsible for keeping consistency between different requirements models or versions of models.
 Copyright (C) 2014 Andreas Genz

 PSSIF Consistency is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 PSSIF Consistency is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with PSSIF Consistency.  If not, see <http://www.gnu.org/licenses/>.

 Feel free to contact me via eMail: genz@in.tum.de
 */

/**
 * A class that brings more convenience when working with nodes. Every object of
 * this class stores a node and its according nodeType. When you have an
 * instance of this class you are able to get more information about the stored
 * node like it's attributes.
 * 
 * @author Andreas
 * 
 */
public class NodeAndType {

	/**
	 * @param node
	 * @param actNodeType
	 */
	public NodeAndType(Node node, NodeTypeBase actNodeType) {
		super();
		this.node = node;
		this.type = actNodeType;
	}

	private Node node;
	private NodeTypeBase type;

	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @param node
	 *            the node to set
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/**
	 * @return the type
	 */
	public NodeTypeBase getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(NodeType type) {
		this.type = type;
	}

}
