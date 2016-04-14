package org.aksw.r2v;

import org.aksw.r2v.controller.R2VManager;
import org.aksw.r2v.model.R2VModel;
import org.aksw.r2v.strategy.TfidfFEXStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class R2VMain {
	
	protected static Logger logger = LogManager.getLogger(R2VMain.class);

	public static void main(String[] args) {
		
		R2VModel model = R2VManager.train(args[0], new TfidfFEXStrategy());
		
		logger.info(model.info());

		String ns = (args.length == 3) ? args[2] : null;
		model.reduce(args[1], ns);

	}

}
