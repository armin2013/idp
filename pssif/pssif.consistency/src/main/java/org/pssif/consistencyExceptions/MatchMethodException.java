package org.pssif.consistencyExceptions;

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
 * This exception is thrown if there exists a problem with one of the
 * matchMethods: For example if a match method isn't implemented, if a match
 * method object couldn't be initialized or if the prerequisites for the
 * execution of a match method aren't fullfilled.
 * 
 * @author Andreas
 * 
 */
public class MatchMethodException extends ConsistencyException {

	public MatchMethodException(String message, Throwable reason) {
		super(message, reason);
	}

	public MatchMethodException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6683130474170484776L;

}
