package de.tum.pssif.transform.mapper.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import javax.swing.JOptionPane;







import org.apache.jena.atlas.web.HttpException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetAccessor;
import com.hp.hpl.jena.query.DatasetAccessorFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;

import de.tum.pssif.core.metamodel.external.URIs;

public class DatabaseImpl implements Database {
	public static Dataset ds = null;
	public static DatasetAccessor accessor;
//	private static RDFModelImpl rdfModel = null;
	private static Model model = null;

	public DatabaseImpl() {
		// Load Database from Fuseki Server
		String serviceURI = URIs.serviceUri.concat("/data");
		try {
			accessor = DatasetAccessorFactory.createHTTP(serviceURI);
			// Get Default Model from Server
			model = accessor.getModel();
			// Create RDFModel
//			rdfModel = new RDFModelImpl(URIs.modelname, model);
		} catch (HttpException exp) {
			String error = "An HttpException was raised.\n Cause: "
					+ exp.getMessage();
			JOptionPane.showMessageDialog(null, error, "PSSIF",
					JOptionPane.ERROR_MESSAGE);

		} catch (Exception e) {
			String error = "An error orrcured during connecting to database.\n";
			if (accessor == null)
				error = "Problems with connecting to Server!\n Location might be wrong or not accessible.\n";
			if (model == null)
				error = "Problems with getting the model from the database.\n";

			JOptionPane.showMessageDialog(null, error, "PSSIF",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// Getter
	public Model getRdfModel() {
		return model;
	}

	@Override
	public void printModelNames() {
		if (ds != null) {
			ds.begin(ReadWrite.READ);
			// get names of all stored models
			for (Iterator<String> is = ds.listNames(); is.hasNext();) {
				String modelName = is.next();
				System.out.println(modelName);
			}
			ds.end();
		}
	}

//	@Override
//	public RDFModelImpl createModel(String name) {
//		if (ds != null) {
//			// Create Model
//			// Alternative default model: model = ds.getDefaultModel() -> But
//			// it's good practice to use a named model. The default model always
//			// exist in the Dataset. It will exist even if it is not used.
//			if (model == null)
//				model = ds.getNamedModel(name);
//				
//			// If model doesn't exist already
//			if (rdfModel == null) {
//				// create new RDFModel
//				rdfModel = new RDFModelImpl(name, model);
//			}
//
//			return rdfModel;
//		}
//		return null;
//	}

	@Override
	public void saveModel(Model model) {
		// For testing:
		// rdfModel.writeModelToFile("TestPSSIF",
		// "C:\\Users\\Andrea\\Desktop\\");
		if (accessor != null)
			accessor.putModel(model);
		else if (ds != null)
			model.commit();
	}

	@Override
	public void commit() {
		if (ds != null) {
			ds.commit();
		}
	}

	@Override
	public void begin(ReadWrite rw) {
		if (ds != null) {
			ds.begin(rw);
		}
	}

	@Override
	public void end() {
		if (ds != null) {
			ds.end();
		}
	}

	@Override
	public void close() {
		if (ds != null) {
			ds.close();
		}
	}

	@Override
	public void removeNamedModel(String uri) {
		if (ds != null)
			ds.removeNamedModel(uri);
		else if (accessor != null)
			accessor.deleteDefault();
	}
}
