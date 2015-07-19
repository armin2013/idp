package reqtool.graph;

import graph.model.MyNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;

/**
 * The Class DeriveRequirementPopup.
 */
public class DeriveRequirementPopup {
	
	/** The Constant RESULT_OK. */
	public static final int RESULT_OK = 1;
	
	/** The Constant RESULT_NOK. */
	public static final int RESULT_NOK = 0;
	
	/** The Constant RESULT_CANCEL. */
	public static final int RESULT_CANCEL = -1;
	
	/** The attr text fields. */
	private HashMap<Attribute, JTextField> attrTextFields;
	
	/**
	 * Show the popup for deriving a requirement node.
	 *
	 * @param node the selected node
	 */
	public void showPopup(MyNode node) {
		Collection<Attribute> nodeAttributes = node.getNodeType().getType().getAttributes();
		
    	JComponent[] inputs = new JComponent[nodeAttributes.size()*2];
		attrTextFields = new HashMap<Attribute, JTextField>();
		int i=0;
		Iterator<Attribute> iterator = nodeAttributes.iterator();
		while (iterator.hasNext()) {
			Attribute attr = iterator.next();
			JTextField attrText = new JTextField();
			if (!(attr.getName().equalsIgnoreCase(PSSIFConstants.BUILTIN_ATTRIBUTE_VALIDITY_START) ||
					attr.getName().equalsIgnoreCase(PSSIFConstants.BUILTIN_ATTRIBUTE_VALIDITY_END)	)) {
				inputs[i] = new JLabel(attr.getName().toString()); 
				i++;
				inputs[i] = attrText;
				attrTextFields.put(attr, attrText);
				i++;
			}
		}
		
    	JOptionPane.showMessageDialog(null, inputs, "derive requirement node", JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * Gets the attr text fields.
	 *
	 * @return the attr text fields
	 */
	public HashMap<Attribute, JTextField> getAttrTextFields() {
		return attrTextFields;
	}

}
