package reqtool.handler;

import graph.model.MyNode;
import graph.model.MyNodeType;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JTextField;

import model.ModelBuilder;
import reqtool.event.NewDeriveRequirementEvent;
import reqtool.graph.DeriveRequirementPopup;

import com.google.common.eventbus.Subscribe;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;

/**
 * The Class NewDeriveRequirementHandler.
 */
public class NewDeriveRequirementHandler implements EventHandler {
	
	/**
	 * Handle derive requirement event.
	 *
	 * @param event the event
	 */
	@Subscribe
	public void handleDeriveRequirement(NewDeriveRequirementEvent event) {
		DeriveRequirementPopup popup = new DeriveRequirementPopup();
		popup.showPopup(event.getSelectedNode());
		addNode(event.getSelectedNode(), popup.getAttrTextFields().entrySet());
		event.getGViz().updateGraph();
	}
	
	public void addNode(MyNode selectedNode, Set<Entry<Attribute, JTextField>> entries) {
		MyNodeType reqType = ModelBuilder.getNodeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("N_REQUIREMENT"));
		MyNode derivedReq = null;
		HashMap< String, String> entriesStr = new HashMap<String, String>();
		for (Entry<Attribute, JTextField> input:entries) {
			String key = input.getKey().getName();
			String value = input.getValue().getText();
        	if ( key.equals(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME)  && value !=null && value.length()>0 ) {
        		derivedReq = ModelBuilder.addNewNodeFromGUI(input.getValue().getText(), reqType);
        		ModelBuilder.addNewEdgeGUI(derivedReq, selectedNode, ModelBuilder.getEdgeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_CHRONOLOGICAL_BASED_ON")), true);
        	} else if ( value !=null && value.length()>0 ) {
        		entriesStr.put(key, value);
        	}
        	
    	}
		
		for(Entry<String, String> i:entriesStr.entrySet()) {
			try {
				reqType.getType().getAttribute(i.getKey()).getOne().set(derivedReq.getNode(), PSSIFOption.one(PSSIFValue.create(i.getValue())));
			}catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
