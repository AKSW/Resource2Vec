package org.aksw.r2v.api;

import java.io.File;
import java.util.HashMap;

import org.aksw.r2v.api.utils.Shell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class KGEMethodController {
	
	private final static Logger log = LogManager
			.getLogger(KGEMethodController.class);

	public static void call(String method, File rdfDataset, String tmpPath, HashMap<String, String> hyperpMap)
			throws Exception {
		switch (method.toLowerCase()) {
		case "rescal":
			String command = Application.PYTHON_PATH + " python/rdf_rescal.py "
					+ rdfDataset.getAbsolutePath() + " " + tmpPath + "/ "
					+ hyperpMap.get("rank");
			log.info("Executing command: " + command);
			Shell.execute(command);
			return;

		default:
			throw new Exception("Embedding method not found.");
		}
	}

}
