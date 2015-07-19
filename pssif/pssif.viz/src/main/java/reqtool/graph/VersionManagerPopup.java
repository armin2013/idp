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

import reqtool.controller.VersionManager;
import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;

/**
 * The Class VersionManagerPopup.
 */
public class VersionManagerPopup {
	
	/** The Constant RESULT_OK. */
	public static final int RESULT_OK = 1;
	
	/** The Constant RESULT_NOK. */
	public static final int RESULT_NOK = 0;
	
	/** The Constant RESULT_CANCEL. */
	public static final int RESULT_CANCEL = -1;
	
	/**
	 * Show the popup for creating a new version.
	 *
	 * @param node the selected node
	 * @return true, if successful
	 */
	public static boolean showPopup(MyNode node) {
		Collection<Attribute> nodeAttributes = node.getNodeType().getType().getAttributes();
		
    	JComponent[] inputs = new JComponent[nodeAttributes.size()*2];
		HashMap<Attribute, JTextField> attrTextFields = new HashMap<Attribute, JTextField>();
		int i=0;
		Iterator<Attribute> iterator = nodeAttributes.iterator();
		while (iterator.hasNext()) {
			Attribute attr = iterator.next();
			if (!attr.getName().equals(PSSIFConstants.BUILTIN_ATTRIBUTE_ID) && !attr.getName().equals(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME)){
				JTextField attrText = new JTextField();
				PSSIFOption<PSSIFValue> oldAttr = node.getNodeType().getType().getAttribute(attr.getName().toString()).getOne().get(node.getNode());
				String attrValue = "No entry";
				if (!oldAttr.isNone()) {
					attrValue = oldAttr.getOne().asString();
				}
				inputs[i] = new JLabel(attr.getName().toString()+" ("+attrValue +")");
				
				i++;
				inputs[i] = attrText;
				attrTextFields.put(attr, attrText);
				i++;
			}
		}
		
    	
    	JOptionPane.showMessageDialog(null, inputs, "Create new version node", JOptionPane.PLAIN_MESSAGE);
		 
    	for (Entry<Attribute, JTextField> input:attrTextFields.entrySet()) {
        	if (  input.getKey().getName().equals(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION)  &&
        			input.getValue().getText()!=null && input.getValue().getText().length()>0  )
        	{
        		VersionManager vm = new VersionManager(node);
        		return vm.createNewVersion(input.getValue().getText());
        	}          
    	}
    	return false;
	}

}
