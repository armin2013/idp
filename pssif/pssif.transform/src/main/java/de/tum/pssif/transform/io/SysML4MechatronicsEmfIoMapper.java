package de.tum.pssif.transform.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import com.google.common.collect.Maps;

import de.tum.pssif.transform.IoMapper;
import de.tum.pssif.transform.graph.Edge;
import de.tum.pssif.transform.graph.Graph;
import de.tum.pssif.transform.graph.Node;


/**
 * SysML4Mechatronics eCore/XMI mapper. Dumb. Re-naming and stuff should be done by the model mapper
 * and/or the viewpoint.
 */
public class SysML4MechatronicsEmfIoMapper implements IoMapper {

  private static final String ECORE_RESOURCE = "/sysml/sysml4mechatronics.ecore";

  @Override
  public Graph read(InputStream in) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void write(Graph graph, OutputStream out) {
    try {
      Resource resource = new XMIResourceFactoryImpl().createResource(URI.createFileURI(""));
      translateToEObjects(graph, resource);
      resource.save(out, getOptions());
    } catch (IOException e) {
      throw new PSSIFIoException("Failed to write XMI to stream.", e);
    }
  }

  private void translateToEObjects(Graph graph, Resource resource) {
    EPackage ePackage = loadEmfPackage();

    Map<Node, EObject> node2eObjectMap = Maps.newHashMap();
    for (Node node : graph.getNodes()) {
      writeEObject(node, node2eObjectMap, ePackage);
    }
    for (Edge edge : graph.getEdges()) {
      referenceEObjects(edge, node2eObjectMap, ePackage);
    }
  }

  private void writeEObject(Node node, Map<Node, EObject> eObjects, EPackage ePackage) {
    EClassifier eClassifier = ePackage.getEClassifier(node.getType());
    if (eClassifier instanceof EClass) {
      EClass eClass = (EClass) eClassifier;
      EObject eObject = ePackage.getEFactoryInstance().create(eClass);
      for (String attrName : node.getAttributeNames()) {
        if (node.getAttributeValue(attrName) != null && eClass.getEStructuralFeature(attrName) instanceof EAttribute) {
          //FIXME data types and stuff. is emf clever enough to do it by itself?
          eObject.eSet(eClass.getEStructuralFeature(attrName), node.getAttributeValue(attrName));
        }
      }
      eObjects.put(node, eObject);
    }
  }

  private void referenceEObjects(Edge edge, Map<Node, EObject> eObjects, EPackage ePackage) {
    EObject fromEObject = eObjects.get(edge.getSource());
    EObject toEObject = eObjects.get(edge.getTarget());

    if (fromEObject == null || toEObject == null) {
      //something was not mapped, return
      return;
    }
    //check if reference exists
    if (!(fromEObject.eClass().getEStructuralFeature(edge.getType()) instanceof EReference)) {
      //eReference not found
      return;
    }
    //TODO multiplicities should be taken care of here?
    EReference eReference = (EReference) fromEObject.eClass().getEStructuralFeature(edge.getType());
    fromEObject.eSet(eReference, toEObject);
  }

  private EPackage loadEmfPackage() {
    Resource ecoreResource = new EcoreResourceFactoryImpl().createResource(URI.createURI(""));
    InputStream ecoreInputStream = getClass().getResourceAsStream(ECORE_RESOURCE);
    try {
      ecoreResource.load(ecoreInputStream, getOptions());
    } catch (IOException e) {
      throw new PSSIFIoException("Failed to load SysML4Mechatronics eCore.", e);
    }
    return (EPackage) ecoreResource.getContents().get(0);
  }

  private static Map<String, Object> getOptions() {
    Map<String, Object> options = Maps.newHashMap();
    options.put(XMLResource.OPTION_ENCODING, "UTF-8");
    options.put(XMIResource.OPTION_EXTENDED_META_DATA, ExtendedMetaData.INSTANCE);
    options.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
    options.put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
    options.put(XMIResource.OPTION_LAX_FEATURE_PROCESSING, Boolean.TRUE);
    options.put(XMLResource.OPTION_USE_LEXICAL_HANDLER, Boolean.TRUE);
    options.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);

    return options;
  }

}
