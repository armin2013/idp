package org.pssif.matchingLogic;

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
 * This enum represents all match methods in the PSSIF-FW.
 * 
 * If one match method shall be added. The method checkMatchMethods() in the
 * class Normalizer and the methods saveMatchMethodResult
 * (),getWeightedSyntacticSimilarity(),getWeightedSemanticSimilarity() and
 * getWeightedContextSimilarity in the class "MatchingProcess" and
 * "ContextMatcher" have to be adapted properly. Furthermore the method
 * createMatchMethodObject() in the class MatchMethod has to be adapted.
 * 
 * Additionally the new matching method has to be implemented through extending
 * the abstract Class "MatchMethod".
 * 
 * @author Andreas
 * 
 */
public enum MatchingMethods {

	EXACT_STRING_MATCHING(0, "Exact String Comparison"), DEPTH_MATCHING(1,
			"Depth Comparison (no Impl)"), STRING_EDIT_DISTANCE_MATCHING(2,
			"String Edit Distance Comparison"), HYPHEN_MATCHING(3,
			"Hyphen Comparison (no Impl)"), LINGUISTIC_MATCHING(4,
			"Linguistic Comparison"), VECTOR_SPACE_MODEL_MATCHING(5,
			"VSM Comparison"), LATENT_SEMANTIC_INDEXING_MATCHING(6,
			"LSI Comparison (no Impl)"), ATTRIBUTE_MATCHING(7,
			"Attribute Comparison"), CONTEXT_MATCHING(8,
			"Contextual Comparison");

	private final int value;
	private final String description;

	private MatchingMethods(final int value, final String description) {
		this.value = value;
		this.description = description;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return The names of all matchingMethods as a String array
	 */
	public static String[] methods() {
		MatchingMethods[] methods = values();
		String[] names = new String[methods.length];

		for (int i = 0; i < methods.length; i++) {
			names[i] = methods[i].name();
		}

		return names;
	}

}
