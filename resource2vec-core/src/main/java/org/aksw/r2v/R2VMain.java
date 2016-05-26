package org.aksw.r2v;

import java.util.HashMap;

import org.aksw.r2v.controller.R2VManager;
import org.aksw.r2v.controller.R2VSerializer;
import org.aksw.r2v.model.R2VModel;
import org.aksw.r2v.strategy.TfidfFEXStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.clarkparsia.owlapiv3.OWL;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class R2VMain {

	protected static Logger logger = LogManager.getLogger(R2VMain.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		HashMap<String, String> params = new HashMap<>();
		for (int i = 0; i < args.length; i += 2) {
			params.put(args[i], args[i + 1]);
			logger.info("{}={}", args[i], args[i+1]);
		}

		// R2V model save?
		R2VModel model;
		if (params.containsKey("--modelIn")) { // read from file
			model = R2VSerializer.read(params.get("--modelIn"));
		} else { // train on dataset
			// dataset
			String dataset = params.get("--dataset");
			// class URI (null means OWL.Thing)
			String cUri = params.get("--classUri");
			String classUri = (cUri.equals("null")) ? OWL.Thing.toStringID()
					: cUri;
			model = R2VManager.train(dataset, new TfidfFEXStrategy(), classUri);
		}
		logger.info(model.info());

		// save R2V model
		if (params.containsKey("--modelOut"))
			R2VSerializer.write(model, params.get("--modelOut"));

		if (params.containsKey("--ns") && params.containsKey("--ttype")
				&& params.containsKey("--dim")) { // dimensionality reduction
			// namespace restriction
			String ns = params.get("--ns");
			// dimensionality reduction type
			String ttype = params.get("--ttype");
			// target dimensions for dimensionality reduction
			String dim = params.get("--dim");
			logger.info("Dimensionality reduction called.");
			model.reduce(ttype, ns, dim);
		}

	}

}
