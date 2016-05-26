package org.aksw.r2v.strategy;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class TfidfFEXStrategy implements FEXStrategy {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2993219097153481513L;

	public TfidfFEXStrategy() {
		super();
	}

	@Override
	public String getName() {
		return "Tf-Idf Strategy";
	}
	
}
