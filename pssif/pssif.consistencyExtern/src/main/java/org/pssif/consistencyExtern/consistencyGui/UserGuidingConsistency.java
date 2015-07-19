package org.pssif.consistencyExtern.consistencyGui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.TableModel;

import org.pssif.comparedDataStructures.ComparedNodePair;
import org.pssif.consistencyDataStructures.ConsistencyData;
import org.pssif.consistencyExceptions.MatchMethodException;
import org.pssif.mainProcesses.ComparisonProcess;
import org.pssif.matchingLogic.MatchMethod;
import org.pssif.matchingLogic.MatchingMethods;
import org.pssif.mergedDataStructures.MergedNodePair;

import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.model.Model;

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
 * This class is responsible for guiding the user through the whole consistency
 * process. For example asking whether the user wants to merge the models or do
 * the similarity analysis between the old and the new model. Another thing is
 * that the class lets the user select which match methods shall be applied with
 * which weight, which thresholds for each metric group shall be active and
 * which nodes shall be linked as equals.
 * 
 * Furthermore this class lets the user in the model merge choose which nodes
 * shall be linked by a tracelink, merged into node one or the old node just be
 * copied to the new model.
 * 
 * @author Andreas
 */
public class UserGuidingConsistency {

	static boolean cancelMerge = false;

	/**
	 * This method controls the gui of the consistency process. First the dialog
	 * to let the user choose the match methods is opened. Then the user is
	 * supposed to choose the tresholds for the syntactic, semantic and
	 * contextual metrics. Afterwards the matching process is started and the
	 * results are displayed to the user where he can choose which nodes shall
	 * be linked as equal.
	 * 
	 * @param originalModel
	 *            The original model
	 * @param newModel
	 *            The newly imported model
	 * @param metaModelOriginal
	 *            the metamodel of the original model
	 * @param metaModelNew
	 *            the metamodel of the new model
	 * @return the list of node pairs which shall be merged
	 */
	public static List<ComparedNodePair> startConsistencyCheck(
			Model originalModel, Model newModel, Metamodel metaModelOriginal,
			Metamodel metaModelNew) {

		List<MatchMethod> matchMethods = openChooseMatchingMethodsPopup();

		if (matchMethods != null) {

			openChooseTresholdsPopup();

			ComparisonProcess.main(originalModel, newModel, metaModelOriginal,
					metaModelNew, matchMethods);

			openChooseMergeCandidatesPopup();

			List<ComparedNodePair> result = ConsistencyData.getInstance()
					.getEqualsList();

			ConsistencyData.getInstance().resetComparedNodePairList();

			return result;
		} else {
			return null;
		}
	}

	/**
	 * This method opens a dialog where the user can choose the different
	 * tresholds. After the user confirmed his input the inputs are validated.
	 * If the input was valid the tresholds are set in the ConsistencyData
	 * Class. Otherwise the dialog remains open unitl the user enters valid
	 * data.
	 */
	private static void openChooseTresholdsPopup() {

		final JDialog dialog = new JDialog();
		dialog.setLayout(new BorderLayout());
		dialog.setSize(280, 180);
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		final JLabel labelSyn = new JLabel("Syntactic Threshold:");
		final JTextField synTresh = new JTextField();
		synTresh.setColumns(4);

		final JLabel labelSem = new JLabel("Semantic Threshold:");
		final JTextField semTresh = new JTextField();
		semTresh.setColumns(4);

		final JLabel labelCon = new JLabel("Context Threshold:");
		final JTextField conTresh = new JTextField();
		conTresh.setColumns(4);

		JButton buttonApply = new JButton("Choose these thresholds");
		buttonApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				double synTreshold = 0, semTreshold = 0, conTreshold = 0;
				boolean nonEmptyFields = true;
				boolean valuesAreDouble = true;

				if (!synTresh.getText().isEmpty()) {
					try {
						synTreshold = Double.parseDouble(synTresh.getText());
					} catch (NumberFormatException nfE) {
						valuesAreDouble = false;
					}
				} else {
					nonEmptyFields = false;
				}
				if (!semTresh.getText().isEmpty()) {
					try {
						semTreshold = Double.parseDouble(semTresh.getText());
					} catch (NumberFormatException nfE) {
						valuesAreDouble = false;
					}
				} else {
					nonEmptyFields = false;
				}
				if (!conTresh.getText().isEmpty()) {
					try {
						conTreshold = Double.parseDouble(conTresh.getText());
					} catch (NumberFormatException nfE) {
						valuesAreDouble = false;
					}
				} else {
					nonEmptyFields = false;
				}

				if (nonEmptyFields
						&& valuesAreDouble
						&& tresholdsAreValid(synTreshold, semTreshold,
								conTreshold)) {
					dialog.setVisible(false);

					ConsistencyData.initThresholds(synTreshold, semTreshold,
							conTreshold);
				} else {
					dialog.setVisible(true);
				}
			}

			/**
			 * @return whether every given tresholds is valid (between 0 and 1)
			 */
			private boolean tresholdsAreValid(double synTreshold,
					double semTreshold, double conTreshold) {
				boolean result = false;

				result = (synTreshold >= 0) && (semTreshold >= 0)
						&& (conTreshold >= 0);
				result = result && (synTreshold <= 1) && (semTreshold <= 1)
						&& (conTreshold <= 1);

				return result;
			}
		});

		JPanel treshholdPanel = new JPanel();
		treshholdPanel.add(labelSyn);
		treshholdPanel.add(synTresh);
		treshholdPanel.add(labelSem);
		treshholdPanel.add(semTresh);
		treshholdPanel.add(labelCon);
		treshholdPanel.add(conTresh);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(buttonApply);

		dialog.add(treshholdPanel, BorderLayout.CENTER);
		dialog.add(buttonPanel, BorderLayout.SOUTH);

		dialog.setVisible(true);
	}

	/**
	 * This method shows the user the node pairs where at least one of the
	 * similarity result exceeds the threshold. Users can then choose which
	 * nodes shall be linked/merged. After Confirming this dialog the
	 * comparedNodePairs list in the consistencyData object is updated.
	 * 
	 */
	private static void openChooseMergeCandidatesPopup() {
		final JDialog dialog = new JDialog();
		dialog.setLayout(new BorderLayout());
		dialog.setSize(900, 600);
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		final TableModel tableModel = new MatchCandidateTableModel(
				ConsistencyData.getInstance().getEqualsCandidateList());

		JTable mergeTable = new JTable(tableModel);

		MatchCandidateTableModel.initColumnWidths(mergeTable);

		mergeTable.setFont(new Font("Arial", Font.PLAIN, 14));
		mergeTable.setGridColor(new Color(808080));
		mergeTable.getTableHeader().setReorderingAllowed(false);

		JScrollPane scrollPane = new JScrollPane(mergeTable);

		JButton buttonApply = new JButton(
				"Link the selected node pairs as 'equal'");
		buttonApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				dialog.setVisible(false);
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(buttonApply);

		dialog.add(scrollPane, BorderLayout.CENTER);
		dialog.add(buttonPanel, BorderLayout.SOUTH);

		dialog.setVisible(true);
	}

	/**
	 * This method opens a modal dialogue where the user has to decide whether
	 * he wants to merge the two models or conduct the similarity analysis
	 * between old and new model.
	 * 
	 * @return a boolean that says whether the user wants to merge the newly
	 *         imported model with the old one or do the similarity analysis
	 */
	public static boolean openConsistencyPopUp() {

		boolean res = false;

		Object[] options = { "Merge existing into new one",
				"Copy new to existing" };

		JOptionPane jop = new JOptionPane(
				"You have imported a second model into the integration framework.\n \n What would you like to do with the original and the newly imported model?");
		jop.setOptionType(JOptionPane.YES_NO_OPTION);
		jop.setMessageType(JOptionPane.QUESTION_MESSAGE);
		jop.setOptions(options);

		JDialog dialog = jop.createDialog(null,
				"Find model corresponces or merge different model versions");
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		dialog.add(jop);
		dialog.setVisible(true);

		String result = (String) jop.getValue();

		if (result.equals("Merge existing into new one")) {
			res = true;
		} else if (result.equals("Copy new to existing")) {
			res = false;
		}

		return res;
	}

	/**
	 * This method creates an object for every possible match method and then
	 * gives this list to the chooseMatchingMethodsPopup() method.
	 * 
	 * @return the set of matchMethods which can be adjusted by the user to
	 *         match his requirements
	 */
	private static List<MatchMethod> openChooseMatchingMethodsPopup() {

		List<MatchMethod> methods = new LinkedList<MatchMethod>();
		MatchingMethods[] allMethods = MatchingMethods.values();

		for (MatchingMethods matchMethod : allMethods) {
			methods.add(MatchMethod.createMatchMethodObject(matchMethod, false,
					0.0));
		}

		List<MatchMethod> result = chooseMatchingMethodsPopup(methods);

		return result;
	}

	/**
	 * This method creates a dialogue where the user can choose the desired
	 * methods together with their preferences. After the user entered the
	 * desired methods with according weigths and if they're active it's checked
	 * if the weights which the user entered are valid.
	 * 
	 * If the user hits "Cancel the merge" nothing happens and no additional
	 * model is imported.
	 * 
	 * @param methods
	 *            the list of match method objects
	 * @return the edited list of match method objects. Now every object
	 *         contains the information if its method is active and which weigth
	 *         it has.
	 */
	private static List<MatchMethod> chooseMatchingMethodsPopup(
			final List<MatchMethod> methods) {

		final JDialog dialog = new JDialog();
		dialog.setLayout(new BorderLayout());
		dialog.setSize(700, 400);
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setResizable(false);
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		final TableModel tableModel = new MethodChooseTableModel(methods);

		JTable methodTable = new JTable(tableModel);

		methodTable.setFont(new Font("Arial", Font.PLAIN, 14));
		methodTable.setGridColor(new Color(808080));
		methodTable.getTableHeader().setReorderingAllowed(false);

		JScrollPane scrollPane = new JScrollPane(methodTable);

		JButton buttonApply = new JButton("Apply the selected merge Methods");
		buttonApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (chosenMethodsAreValid(methods)) {
					dialog.setVisible(false);
				} else {
					dialog.setVisible(false);
					chooseMatchingMethodsPopup(methods);
				}
			}

		});

		JButton buttonCancel = new JButton("Cancel the merge");
		buttonCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cancelMerge = true;

				dialog.setVisible(false);
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(buttonApply);
		buttonPanel.add(buttonCancel);

		dialog.add(scrollPane, BorderLayout.CENTER);
		dialog.add(buttonPanel, BorderLayout.SOUTH);

		dialog.setVisible(true);

		if (!cancelMerge) {
			return methods;
		} else {
			cancelMerge = false;
			return null;
		}
	}

	/**
	 * This method builds the sum of weights for each different metric
	 * catgeories (syntactic, semantic & contextual) and looks whether they are
	 * all in the interval [0..1]. Also at least one metric has to be active to
	 * have a valid list with methods.
	 * 
	 * If the context metric is active at least one other metric has to be
	 * active.
	 * 
	 * @param methods
	 *            the match method list with the weight information of each
	 *            method
	 * @return if the method list is valid(at least one metric is active and the
	 *         syntactic, semantic & contextual weights are in the interval
	 *         [0..1])
	 */
	protected static boolean chosenMethodsAreValid(List<MatchMethod> methods) {
		boolean result;

		double syntacticWeight = 0;
		double semanticWeight = 0;
		double contextualWeight = 0;

		Iterator<MatchMethod> matchMethod = methods.iterator();
		MatchMethod method;
		boolean atLeastOneMetricSelected = false;

		while (matchMethod.hasNext()) {
			method = matchMethod.next();

			if (method.isActive()) {
				if ((method.getMatchMethod() == MatchingMethods.CONTEXT_MATCHING)
						&& (!atLeastOneMetricSelected)) {
				} else {
					atLeastOneMetricSelected = true;
				}

				switch (method.getMatchMethod()) {
				case EXACT_STRING_MATCHING:
				case DEPTH_MATCHING:
				case STRING_EDIT_DISTANCE_MATCHING:
				case HYPHEN_MATCHING:
					syntacticWeight += method.getWeigth();
					break;
				case LINGUISTIC_MATCHING:
				case VECTOR_SPACE_MODEL_MATCHING:
				case LATENT_SEMANTIC_INDEXING_MATCHING:
				case ATTRIBUTE_MATCHING:
					semanticWeight += method.getWeigth();
					break;
				case CONTEXT_MATCHING: {
					contextualWeight += method.getWeigth();
					break;
				}
				default:
					throw new MatchMethodException(
							"Invalid metrics were chosen. "
									+ method.getMatchMethod()
									+ " Please choose correct metrics!");
				}
			}
		}

		result = atLeastOneMetricSelected;
		result = result && (syntacticWeight >= 0) && (syntacticWeight <= 1);
		result = result && (semanticWeight >= 0) && (semanticWeight <= 1);
		result = result && (contextualWeight >= 0) && (contextualWeight <= 1);

		return result;
	}

	/**
	 * This method shows the user the node pairs which the merging process
	 * assessed as to be traced. Users can then choose which node pairs shall be
	 * linked by a trace link or if only the new version shall be kept. If the
	 * user chooses neither trace nor merge the old version of the node is
	 * copied to the new model together with his edges. After Confirming this
	 * dialog the mergedNodePairs list in the consistencyData object is updated.
	 * 
	 */
	public static void openChooseTraceLinksWindows() {
		final JDialog dialog = new JDialog();
		dialog.setLayout(new BorderLayout());
		dialog.setSize(900, 600);
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		final TableModel tableModel = new TraceCandidateTableModel(
				ConsistencyData.getInstance().getTraceCandidateList());

		JTable traceTable = new JTable(tableModel);

		TraceCandidateTableModel.initColumnWidths(traceTable);

		traceTable.setFont(new Font("Arial", Font.PLAIN, 14));
		traceTable.setGridColor(new Color(808080));
		traceTable.getTableHeader().setReorderingAllowed(false);

		JScrollPane scrollPane = new JScrollPane(traceTable);

		JButton buttonApply = new JButton("Handle the node pairs as selected");
		buttonApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (chosenOptionsAreValid()) {
					dialog.setVisible(false);
				} else {
					dialog.setVisible(true);
				}
			}

			/**
			 * @return returns if the options in the model merger dialogue are
			 *         valid. They are valid if merge and trace aren't chosen
			 *         simultaneously and invalid otherwise
			 */
			private boolean chosenOptionsAreValid() {
				boolean result = true;

				for (MergedNodePair actPair : ConsistencyData.getInstance()
						.getTraceCandidateList()) {
					result = result
							&& !(actPair.isMerge() && actPair.isTraceLink());
				}

				return result;
			}
		});

		final JLabel description = new JLabel(
				"Select if the proposed node pairs shall be merged or traced. If nothing is selected the node from the old model will be copied to the new model.");

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(buttonApply);

		dialog.add(description, BorderLayout.NORTH);
		dialog.add(scrollPane, BorderLayout.CENTER);
		dialog.add(buttonPanel, BorderLayout.SOUTH);

		dialog.setVisible(true);
	}
}
