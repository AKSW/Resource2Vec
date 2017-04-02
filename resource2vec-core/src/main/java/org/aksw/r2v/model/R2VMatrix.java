package org.aksw.r2v.model;

import org.ujmp.core.Matrix;

/**
 * @author Tommaso Soru {@literal tsoru@informatik.uni-leipzig.de}
 *
 */
public class R2VMatrix {
	
	Object matrix;
	
	/**
	 * @param m
	 */
	public R2VMatrix(Matrix m) {
		super();
		matrix = m;
	}
	
	/**
	 * @return
	 */
	public double[] getVector() {
		double[] v = null;
		if(matrix instanceof Matrix) {
			Matrix m = (Matrix) matrix;
			v = new double[(int) m.getSize()[0]];
			for(int i=0; i<v.length; i++)
				v[i] = m.getAsDouble(i);
		}
		return v;
	}
	
	@Override
	public String toString() {
		return matrix.toString().trim();
	}

}
