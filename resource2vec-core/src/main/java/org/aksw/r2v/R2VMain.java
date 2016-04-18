package org.aksw.r2v;

import org.aksw.r2v.controller.R2VManager;
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
		
		// dataset
		String dataset = args[0];
		// class URI (null means OWL.Thing)
		String classUri = (args[1].equals("null")) ? OWL.Thing.toStringID() : args[1];
		
		R2VModel model = R2VManager.train(dataset, new TfidfFEXStrategy(), classUri);
		logger.info(model.info());

		if(args.length > 2) { // dimensionality reduction
			// namespace restriction
			String ns = args[2];
			// dimensionality reduction type
			String ttype = args[3];
			// target dimensions for dimensionality reduction
			String dim = args[4];
			logger.info("Dimensionality reduction called.");
			model.reduce(ttype, ns, dim);
		}

	}

}
