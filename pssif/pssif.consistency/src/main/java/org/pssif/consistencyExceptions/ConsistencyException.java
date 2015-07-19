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
 * A superclass, derived of the RuntimeException, for other runtime exceptions
 * that might occur while the running of the pssif consistency programm due to
 * wrong user input or changes in the pssif core.
 * 
 * @author Andreas
 * 
 */
public class ConsistencyException extends RuntimeException {

	private static final long serialVersionUID = -1220406763001425332L;

	public ConsistencyException(String message) {
		super(message);
	}

	public ConsistencyException(String message, Throwable reason) {
		super(message, reason);
	}

}
