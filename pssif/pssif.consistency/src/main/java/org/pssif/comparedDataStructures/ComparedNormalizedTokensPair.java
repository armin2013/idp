package org.pssif.comparedDataStructures;

import java.util.List;

import org.pssif.consistencyDataStructures.Token;

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
 * This class saves the tokenized and normalized labels (differed in
 * syntactically normalized and semantically normalized) as well as the
 * normalized labels of two compared labels. Additionally it saves the result of
 * several syntactic and semantic metrics applied to the two token sets.
 * 
 * Saving this information allows, in further matchings to look up if similar
 * labels have once been matched and maybe merged so this information can be
 * useful as more and more models have been matched
 * 
 * @author Andreas
 * 
 */
public class ComparedNormalizedTokensPair extends Compared {

	/**
	 * @param tokensOrigin
	 * @param tokensNew
	 */
	public ComparedNormalizedTokensPair() {
		super();
	}

	/**
	 * The tokenized and normalized forms of two labels on which semantic
	 * metrics were applied
	 */
	private List<Token> tokensOriginNodeNormalized;
	private List<Token> tokensNewNodeNormalized;

	/**
	 * The tokenized and normalized (without compound splitting and without
	 * stemming) forms of two labels on which syntactic metrics were applied
	 */
	private List<Token> tokensOriginNodeNormalizedCompundedUnstemmed;
	private List<Token> tokensNewNodeNormalizedCompundedUnstemmed;

	/**
	 * @param tokensOriginNodeNormalized
	 *            the tokensOriginNodeNormalized to set
	 */
	public void setTokensOriginNodeNormalized(
			List<Token> tokensOriginNodeNormalized) {
		if (this.tokensOriginNodeNormalized == null) {
			this.tokensOriginNodeNormalized = tokensOriginNodeNormalized;
		}
	}

	/**
	 * @param tokensNewNodeNormalized
	 *            the tokensNewNodeNormalized to set
	 */
	public void setTokensNewNodeNormalized(List<Token> tokensNewNodeNormalized) {
		if (this.tokensNewNodeNormalized == null) {
			this.tokensNewNodeNormalized = tokensNewNodeNormalized;
		}
	}

	/**
	 * @param tokensOriginNodeNormalizedCompundedUnstemmed
	 *            the tokensOriginNodeNormalizedCompundedUnstemmed to set
	 */
	public void setTokensOriginNodeNormalizedCompundedUnstemmed(
			List<Token> tokensOriginNodeNormalizedCompundedUnstemmed) {
		if (this.tokensOriginNodeNormalizedCompundedUnstemmed == null) {
			this.tokensOriginNodeNormalizedCompundedUnstemmed = tokensOriginNodeNormalizedCompundedUnstemmed;
		}
	}

	/**
	 * @param tokensNewNodeNormalizedCompundedUnstemmed
	 *            the tokensNewNodeNormalizedCompundedUnstemmed to set
	 */
	public void setTokensNewNodeNormalizedCompundedUnstemmed(
			List<Token> tokensNewNodeNormalizedCompundedUnstemmed) {
		if (this.tokensNewNodeNormalizedCompundedUnstemmed == null) {
			this.tokensNewNodeNormalizedCompundedUnstemmed = tokensNewNodeNormalizedCompundedUnstemmed;
		}
	}

	/**
	 * the result of the matching between two tokens based on their synonym
	 * similarity
	 */
	private double linguisticMatchResult;

	/**
	 * the result of the calculation of the string edit distance metric between
	 * tokens
	 */
	private double stringEditDistanceResult;

	/**
	 * the result of the compairson of the hyphens of tokens
	 */
	private double hyphenMatchResult;

	/**
	 * the result of the application of the information retrieval technique
	 * vector space model to the two token sets
	 */
	private double vsmMatchResult;

	/**
	 * the result of the application of the information retrieval technique
	 * latent semantic indexing to the two token sets
	 */
	private double lsiMatchResult;

	/**
	 * @return the linguisticMatchResult
	 */
	public double getLinguisticMatchResult() {
		return linguisticMatchResult;
	}

	/**
	 * @return the stringEditDistanceResult
	 */
	public double getStringEditDistanceResult() {
		return stringEditDistanceResult;
	}

	/**
	 * @return the hyphenMatchResult
	 */
	public double getHyphenMatchResult() {
		return hyphenMatchResult;
	}

	/**
	 * @return the vsmMatchResult
	 */
	public double getVsmMatchResult() {
		return vsmMatchResult;
	}

	/**
	 * @return the lsiMatchResult
	 */
	public double getLsiMatchResult() {
		return lsiMatchResult;
	}

	/**
	 * @return the tokensOriginNodeNormalized
	 */
	public List<Token> getTokensOriginNodeNormalized() {
		return tokensOriginNodeNormalized;
	}

	/**
	 * @return the tokensNewNodeNormalized
	 */
	public List<Token> getTokensNewNodeNormalized() {
		return tokensNewNodeNormalized;
	}

	/**
	 * @return the tokensOriginNodeNormalizedCompundedUnstemmed
	 */
	public List<Token> getTokensOriginNodeNormalizedCompundedUnstemmed() {
		return tokensOriginNodeNormalizedCompundedUnstemmed;
	}

	/**
	 * @return the tokensNewNodeNormalizedCompundedUnstemmed
	 */
	public List<Token> getTokensNewNodeNormalizedCompundedUnstemmed() {
		return tokensNewNodeNormalizedCompundedUnstemmed;
	}

	/**
	 * @param linguisticMatchResult
	 *            the linguisticMatchResult to set
	 */
	public void setLinguisticMatchResult(double linguisticMatchResult) {
		this.linguisticMatchResult = linguisticMatchResult;
	}

	/**
	 * @param stringEditDistanceResult
	 *            the stringEditDistanceResult to set
	 */
	public void setStringEditDistanceResult(double stringEditDistanceResult) {
		this.stringEditDistanceResult = stringEditDistanceResult;
	}

	/**
	 * @param hyphenMatchResult
	 *            the hyphenMatchResult to set
	 */
	public void setHyphenMatchResult(double hyphenMatchResult) {
		this.hyphenMatchResult = hyphenMatchResult;
	}

	/**
	 * @param vsmMatchResult
	 *            the vsmMatchResult to set
	 */
	public void setVsmMatchResult(double vsmMatchResult) {
		this.vsmMatchResult = vsmMatchResult;
	}

	/**
	 * @param lsiMatchResult
	 *            the lsiMatchResult to set
	 */
	public void setLsiMatchResult(double lsiMatchResult) {
		this.lsiMatchResult = lsiMatchResult;
	}

}
