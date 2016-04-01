package org.aksw.r2v;

import org.aksw.r2v.controller.R2VManager;
import org.aksw.r2v.strategy.TfidfFEXStrategy;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class R2VMain {

	public static void main(String[] args) {
		
		R2VManager.train(args[0], new TfidfFEXStrategy());

	}

}
