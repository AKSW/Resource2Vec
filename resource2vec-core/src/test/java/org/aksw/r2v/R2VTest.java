package org.aksw.r2v;

import org.aksw.r2v.controller.R2VManager;
import org.aksw.r2v.model.R2VModel;
import org.aksw.r2v.strategy.TfidfFEXStrategy;
import org.junit.Test;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class R2VTest {

	@Test
	public void test() {
		
		R2VModel model = R2VManager.train("src/test/resources/person11.rdf", new TfidfFEXStrategy());

		System.out.println(model.info());
		
	}
	


}
