package org.aksw.r2v;

import java.util.Iterator;

import org.aksw.r2v.controller.R2VManager;
import org.aksw.r2v.model.R2VInstance;
import org.aksw.r2v.model.R2VModel;
import org.aksw.r2v.strategy.TfidfFEXStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class R2VTest {

	private final static Logger logger = LogManager.getLogger(R2VTest.class);
	
	@Test
	public void test() {
		
		R2VModel model = R2VManager.train("src/test/resources/person11-lite.rdf", new TfidfFEXStrategy());
		logger.info(model.info());
		
		model.reduce("ujmp-svd", "http://www.okkam.org/oaie/person1-", "3");
		Iterator<R2VInstance> it = model.getInstances().values().iterator();
		while(it.hasNext()) {
			R2VInstance inst = it.next();
			logger.info(inst.getUri() + "\t" + inst.getReducedV());
		}
		
		
	}
	


}
