package org.pssif.comparedDataStructures;

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
 * every object of this class saves the labels of two compared nodes in their
 * original and normalized (whitespace removed & converted to lowercase) form.
 * In addition it saves the according match result of these labels. Saving this
 * information allows, in further matchings, to look up if similar labels have
 * once been matched (and maybe merged). So this information can be useful as
 * more and more models have been matched.
 * 
 * @author Andreas
 * 
 * 
 */
public class ComparedLabelPair extends Compared {

	/**
	 * @param labelOrigin
	 *            the label of the original node
	 * @param labelNew
	 *            the label of the new node
	 * @param labelOriginNormalized
	 *            the normalized label of the original node
	 * @param labelNewNormalized
	 *            the normalized label of the new node
	 */
	public ComparedLabelPair(String labelOrigin, String labelNew,
			String labelOriginNormalized, String labelNewNormalized) {
		super();
		this.labelOrigin = labelOrigin;
		this.labelNew = labelNew;
		this.labelOriginNormalized = labelOriginNormalized;
		this.labelNewNormalized = labelNewNormalized;
	}

	/**
	 * the original labels of the two nodes
	 */
	private String labelOrigin, labelNew;

	/**
	 * two Strings representing Labels of Nodes (whitespace removed & to
	 * lowercase) to be able to lookup if two labels have been already matched
	 * once
	 */
	private String labelOriginNormalized, labelNewNormalized;

	/**
	 * the result of a direct string/token comparison
	 */
	private double exactMatchResult;

	/**
	 * @return the exactMatchResult
	 */
	public double getExactMatchResult() {
		return exactMatchResult;
	}

	/**
	 * @return the labelOriginNormalized
	 */
	public String getLabelOriginNormalized() {
		return labelOriginNormalized;
	}

	/**
	 * @return the labelNewNormalized
	 */
	public String getLabelNewNormalized() {
		return labelNewNormalized;
	}

	/**
	 * @return the labelOrigin
	 */
	public String getLabelOrigin() {
		return labelOrigin;
	}

	/**
	 * @return the labelNew
	 */
	public String getLabelNew() {
		return labelNew;
	}

	/**
	 * @param exactMatchResult
	 *            the exactMatchResult to set
	 */
	public void setExactMatchResult(double exactMatchResult) {
		this.exactMatchResult = exactMatchResult;
	}

}
