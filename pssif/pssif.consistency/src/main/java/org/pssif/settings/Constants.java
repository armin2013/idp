package org.pssif.settings;

import java.util.ArrayList;
import java.util.List;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.metamodel.external.MetamodelNode;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;

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
 * An abstract class that stores constants necessary in the consistency module.
 * 
 * @author Andreas
 * 
 */
public abstract class Constants {

	/**
	 * These are the subclasses of PSIFFDevArtifacts that are checked for
	 * consistency
	 */
	public static String[] PSIFFDevArtifactSubClasses = {
	// PSSIFCanonicMetamodelCreator.N_FUNCTIONALITY,
	// PSSIFCanonicMetamodelCreator.N_REQUIREMENT,
	// PSSIFCanonicMetamodelCreator.N_USE_CASE,
	// PSSIFCanonicMetamodelCreator.N_TEST_CASE,
	// PSSIFCanonicMetamodelCreator.N_VIEW,
	// PSSIFCanonicMetamodelCreator.N_EVENT,
	// PSSIFCanonicMetamodelCreator.N_ISSUE,
	// PSSIFCanonicMetamodelCreator.N_DECISION,
	// PSSIFCanonicMetamodelCreator.N_CHANGE_EVENT
	};

	/**
	 * These are the subclasses of PSIFFSolArtifacts that are checked for
	 * consistency
	 */
	public static String[] PSIFFSolArtifactSubClasses = {
	// PSSIFCanonicMetamodelCreator.N_BLOCK,
	// PSSIFCanonicMetamodelCreator.N_FUNCTION,
	// PSSIFCanonicMetamodelCreator.N_ACTIVITY,
	// PSSIFCanonicMetamodelCreator.N_STATE,
	// PSSIFCanonicMetamodelCreator.N_ACTOR,
	// PSSIFCanonicMetamodelCreator.N_SERVICE,
	// PSSIFCanonicMetamodelCreator.N_SOFTWARE,
	// PSSIFCanonicMetamodelCreator.N_HARDWARE,
	// PSSIFCanonicMetamodelCreator.N_MECHANIC,
	// PSSIFCanonicMetamodelCreator.N_ELECTRONIC,
	// PSSIFCanonicMetamodelCreator.N_MODULE
	};

	/**
	 * the attributes which are compared between the nodes
	 */
	public static final String[] pssifAttributes = {
			PSSIFConstants.BUILTIN_ATTRIBUTE_COMMENT,
			PSSIFConstants.BUILTIN_ATTRIBUTE_VALIDITY_END,
			PSSIFConstants.BUILTIN_ATTRIBUTE_VALIDITY_START,
			PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION,
			PSSIFCanonicMetamodelCreator.TAGS.get("A_BLOCK_COST"),
			PSSIFCanonicMetamodelCreator.TAGS.get("A_DURATION"),
			PSSIFCanonicMetamodelCreator.TAGS.get("A_HARDWARE_WEIGHT"),
			PSSIFCanonicMetamodelCreator.TAGS.get("A_REQUIREMENT_PRIORITY"),
			PSSIFCanonicMetamodelCreator.TAGS.get("A_REQUIREMENT_TYPE") };

	public static void initialize() {
		List<String> resultSolArtifact = new ArrayList<String>();
		List<String> resultDevArtifact = new ArrayList<String>();
		
		for (MetamodelNode node : PSSIFCanonicMetamodelCreator.nodes.values()) {

			String parent = revalidateParent(node);
			
			if (parent.equals("N_DEV_ARTIFACT")) {
				resultDevArtifact.add(node.getName());
			} else if(parent.equals("N_SOL_ARTIFACT")){
				resultSolArtifact.add(node.getName());
			}
		}

		PSIFFDevArtifactSubClasses = resultDevArtifact
				.toArray(new String[resultDevArtifact.size()]);
		PSIFFSolArtifactSubClasses = resultSolArtifact
				.toArray(new String[resultSolArtifact.size()]);
	}

	private static String revalidateParent(MetamodelNode node) {

		if (node.getParent() == null) {
			return "";
		}

		if (node.getParent().getTag().equals("N_DEV_ARTIFACT")) {
			return "N_DEV_ARTIFACT";
		} else if (node.getParent().getTag().equals("N_SOL_ARTIFACT")) {
			return "N_SOL_ARTIFACT";
		}

		return revalidateParent(node.getParent());
	}

}
