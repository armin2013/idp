package org.pssif.consistencyExtern.consistencyGui;

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.pssif.comparedDataStructures.ComparedNodePair;
import org.pssif.mergedDataStructures.MergedNodePair;

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
 * used to display the possible trace candidates in a JTable in the dialogue
 * after the two model versions were analysed.
 * 
 * @author Andreas
 * 
 */
public class TraceCandidateTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8571148330311284772L;

	/**
	 * The names of the column of the table
	 */
	private static final String[] COLUMN_NAMES = { "Node Origin", "Node New",
			"Exact Sim", "Attr Sim", "LD Sim", "Tracelink?", "Merge?" };

	/**
	 * indexes for the columns
	 */
	private static final int COLUMN_IDX_NODEORIGIN = 0;
	private static final int COLUMN_IDX_NODENEW = 1;
	private static final int COLUMN_IDX_EXACTSIM = 2;
	private static final int COLUMN_IDX_ATTRSIM = 3;
	private static final int COLUMN_IDX_LDSIM = 4;
	private static final int COLUMN_IDX_TRACELINK = 5;
	private static final int COLUMN_IDX_MERGE = 6;

	/**
	 * the table data
	 */
	private final List<MergedNodePair> traceCandidates;

	/**
	 * the types of the columns
	 */
	private static final Class<?>[] COLUMN_CLASSES = { String.class,
			String.class, Double.class, Double.class, Double.class,
			Boolean.class,Boolean.class };

	public TraceCandidateTableModel(final List<MergedNodePair> traceCandidates) {
		this.traceCandidates = traceCandidates;
	}

	@Override
	public int getRowCount() {
		return traceCandidates.size();
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
		final MergedNodePair mergedNodePair = traceCandidates.get(rowIndex);

		if (columnIndex == COLUMN_IDX_NODEORIGIN) {
			return mergedNodePair.getLabelComparison().getLabelOrigin();
		} else if (columnIndex == COLUMN_IDX_NODENEW) {
			return mergedNodePair.getLabelComparison().getLabelNew();
		} else if (columnIndex == COLUMN_IDX_EXACTSIM) {
			return mergedNodePair.getLabelComparison().getExactMatchResult();
		} else if (columnIndex == COLUMN_IDX_ATTRSIM) {
			return mergedNodePair.getAttributeMatchResult();
		} else if (columnIndex == COLUMN_IDX_LDSIM) {
			return mergedNodePair.getStringEditDistanceResult();
		} else if (columnIndex == COLUMN_IDX_TRACELINK) {
			return mergedNodePair.isTraceLink();
		} else if (columnIndex == COLUMN_IDX_MERGE){
			return mergedNodePair.isMerge();
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
		return (columnIndex >= COLUMN_IDX_TRACELINK);
	}

	@Override
	public void setValueAt(final Object value, final int rowIndex,
			final int columnIndex) {
		final MergedNodePair mergedNodePair = traceCandidates.get(rowIndex);

		if (columnIndex == COLUMN_IDX_TRACELINK) {
			mergedNodePair.setTraceLink((Boolean) value);
		}
		if (columnIndex == COLUMN_IDX_MERGE){
			mergedNodePair.setMerge((Boolean) value);
			mergedNodePair.getLabelComparison().setEquals((Boolean) value);
		}

		fireTableCellUpdated(rowIndex, columnIndex);
	}

	/**
	 * This method takes a JTable and adjusts the column widths to make the
	 * looking of the result of the matching process nicer.
	 * 
	 * @param table
	 *            the table which columns shall be initialized
	 */
	public static void initColumnWidths(final JTable table) {
		final TableColumnModel tableColumnModel = table.getColumnModel();

		tableColumnModel.getColumn(COLUMN_IDX_NODEORIGIN).setMinWidth(250);
		tableColumnModel.getColumn(COLUMN_IDX_NODENEW).setMinWidth(250);
		tableColumnModel.getColumn(COLUMN_IDX_EXACTSIM).setPreferredWidth(
				60);
		tableColumnModel.getColumn(COLUMN_IDX_ATTRSIM)
				.setPreferredWidth(60);
		tableColumnModel.getColumn(COLUMN_IDX_LDSIM).setPreferredWidth(60);
		tableColumnModel.getColumn(COLUMN_IDX_TRACELINK).setPreferredWidth(60);
		tableColumnModel.getColumn(COLUMN_IDX_MERGE).setPreferredWidth(60);
	}
	
}
