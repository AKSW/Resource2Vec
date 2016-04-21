package org.aksw.r2v.api;

import java.io.File;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.UploadDataSet;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class OpenMLController {
	
	private final static Logger log = LogManager
			.getLogger(OpenMLController.class);

	public static int upload(File arff, String method, String name, HashMap<String, String> hyperpMap) throws Exception {
		OpenmlConnector client = new OpenmlConnector(
				"http://www.openml.org/", Application.OPENML_API_KEY);
		XStream xstream = XstreamXmlMapping.getInstance();
		String desc = "Knowledge Graph Embedding model for dataset " + name
				+ " using method " + method
				+ " with hyperparameters " + hyperpMap
				+ ".";
		DataSetDescription dsd = new DataSetDescription(name,
				desc, "arff", "class", "public");
		String dsdXML = xstream.toXML(dsd);
		File description = Conversion
				.stringToTempFile(dsdXML, name, "arff");
		log.info(dsdXML);
		UploadDataSet ud = client.dataUpload(description, arff);
		int id = ud.getId();
		log.info("Dataset created with id=" + id);
		return id;
	}

}
