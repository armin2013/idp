/**
 * Source: Levenshtein (modified by Andreas Genz)
 * from folder SimMetrics --> Src
 */







/**
 * SimMetrics - SimMetrics is a java library of Similarity or Distance
 * Metrics, e.g. Levenshtein Distance, that provide float based similarity
 * measures between String Data. All metrics return consistant measures
 * rather than unbounded similarity scores.
 *
 * Copyright (C) 2005 Sam Chapman - Open Source Release v1.1
 *
 * Please Feel free to contact me about this library, I would appreciate
 * knowing quickly what you wish to use it for and any criticisms/comments
 * upon the SimMetric library.
 *
 * email:       s.chapman@dcs.shef.ac.uk
 * www:         http://www.dcs.shef.ac.uk/~sam/
 * www:         http://www.dcs.shef.ac.uk/~sam/stringmetrics.html
 *
 * address:     Sam Chapman,
 *              Department of Computer Science,
 *              University of Sheffield,
 *              Sheffield,
 *              S. Yorks,
 *              S1 4DP
 *              United Kingdom,
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.pssif.levenstheinDistance;


import java.io.Serializable;

/**
 * Package: uk.ac.shef.wit.simmetrics.similaritymetrics.levensteindistance
 * Description: levensteindistance implements the levenstein distance function.

 * Date: 24-Mar-2004
 * Time: 10:54:06
 * @author Sam Chapman <a href="http://www.dcs.shef.ac.uk/~sam/">Website</a>, <a href="mailto:sam@dcs.shef.ac.uk">Email</a>.
 * @version 1.1
 */
public final class Levenshtein implements Serializable {

    /**
     * constructor - default (empty).
     */
    public Levenshtein() {
    }

    /**
     * the private cost function used in the levenstein distance.
     */
    private final SubCost01 dCostFunc = new SubCost01();

    /**
     * gets the similarity of the two strings using levenstein distance.
     *
     * @param string1
     * @param string2
     * @return a value between 0-1 of the similarity
     */
    public float getSimilarity(final String string1, final String string2) {
        final float levensteinDistance = getUnNormalisedSimilarity(string1, string2);
        //convert into zero to one return

        //get the max possible levenstein distance score for string
        float maxLen = string1.length();
        if (maxLen < string2.length()) {
            maxLen = string2.length();
        }

        //check for 0 maxLen
        if (maxLen == 0) {
            return 1.0f; //as both strings identically zero length
        } else {
            //return actual / possible levenstein distance to get 0-1 range
            return 1.0f - (levensteinDistance / maxLen);
        }

    }

    /**
     * implements the levenstein distance function
     * <p/>
     * Copy character from string1 over to string2 (cost 0)
     * Delete a character in string1 (cost 1)
     * Insert a character in string2 (cost 1)
     * Substitute one character for another (cost 1)
     * <p/>
     * D(i-1,j-1) + d(si,tj) //subst/copy
     * D(i,j) = min D(i-1,j)+1 //insert
     * D(i,j-1)+1 //delete
     * <p/>
     * d(i,j) is a function whereby d(c,d)=0 if c=d, 1 else.
     *
     * @param s
     * @param t
     * @return the levenstein distance between given strings
     */
    public float getUnNormalisedSimilarity(final String s, final String t) {
        final float[][] d; // matrix
        final int n; // length of s
        final int m; // length of t
        int i; // iterates through s
        int j; // iterates through t
        float cost; // cost

        // Step 1
        n = s.length();
        m = t.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        d = new float[n + 1][m + 1];

        // Step 2
        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        // Step 3
        for (i = 1; i <= n; i++) {
            // Step 4
            for (j = 1; j <= m; j++) {
                // Step 5
                cost = dCostFunc.getCost(s, i - 1, t, j - 1);

                // Step 6
                d[i][j] = MathFuncs.min3(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
            }
        }

        // Step 7
        return d[n][m];
    }
}
