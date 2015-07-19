package org.pssif.consistencyExtern.consistencyGui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.pssif.matchingLogic.MatchMethod;

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
 * This class is an own implementation of the class AbstractTable Model. It is
 * used to display the possible match methods in a JTable in the dialogue after
 * the user hits the button "Copy new to existing".
 * 
 * @author Andreas
 * 
 */
@SuppressWarnings("serial")
public class MethodChooseTableModel extends AbstractTableModel {

	/**
	 * The names of the column of the table
	 */
	private static final String[] COLUMN_NAMES = { "Methodname", "Weigth",
			"Active?" };

	/**
	 * indexes for the columns
	 */
	private static final int COLUMN_IDX_METHODNAME = 0;
	private static final int COLUMN_IDX_WEIGTH = 1;
	private static final int COLUMN_IDX_ACTIVE = 2;

	/**
	 * the table data
	 */
	private final List<MatchMethod> methods;

	/**
	 * the types of the columns
	 */
	private static final Class<?>[] COLUMN_CLASSES = { String.class,
			Double.class, Boolean.class };

	public MethodChooseTableModel(final List<MatchMethod> methods) {
		this.methods = methods;
	}

	@Override
	public int getRowCount() {
		return methods.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public String getColumnName(final int columnIndex) {
		return (COLUMN_NAMES[columnIndex]);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		final MatchMethod method = methods.get(rowIndex);

		if (columnIndex == COLUMN_IDX_METHODNAME) {
			return method.getMatchMethod().getDescription();
		} else if (columnIndex == COLUMN_IDX_WEIGTH) {
			return method.getWeigth();
		} else if (columnIndex == COLUMN_IDX_ACTIVE) {
			return method.isActive();
		}

		throw new IllegalArgumentException("Invalid column index "
				+ columnIndex);
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		return COLUMN_CLASSES[columnIndex];
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return (columnIndex > COLUMN_IDX_METHODNAME);
	}

	@Override
	public void setValueAt(final Object value, final int rowIndex,
			final int columnIndex) {
		final MatchMethod method = methods.get(rowIndex);

		if (columnIndex == COLUMN_IDX_WEIGTH) {
			method.setWeigth((Double) value);

		} else if (columnIndex == COLUMN_IDX_ACTIVE) {
			method.setActive((Boolean) value);
		}

		fireTableCellUpdated(rowIndex, columnIndex);
	}

}
