package org.aksw.r2v.pca;

import org.jblas.DoubleMatrix;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class JblasRecursiveSVD extends JblasSVD {
	
	public JblasRecursiveSVD(String dir) {
		super(dir);
	}

	public DoubleMatrix recursivePCA(DoubleMatrix A) {
		
		DoubleMatrix C = A.dup();
		
		while(C.columns > 3) {
			int dim = (C.columns / 2) + 1;
			logger.info("C.col = "+C.columns);
			logger.info("dim = "+dim);
			C = pca(C, dim);
			visual("C(dim="+dim+")", C);
		}

		return C;
	}

	public static void main(String[] args) {
		
//		double[][] A = { { 36, 49, 47, 11 }, { 2, 68, 27, 42 },
//				{ 42, 25, 38, 3 } };
		
//		recursivePCA(DoubleMatrix.randn(100, 100));
		
		double[][] A = new double[100][100];
		for(int i=0; i<A.length; i++)
			for(int j=0; j<A[i].length; j++) {
				if(Math.random() > 0.01)
					A[i][j] = 0d;
				else
					A[i][j] = Math.random() * 10;
			}
		
		new JblasRecursiveSVD("recursive").recursivePCA(new DoubleMatrix(A));

	}

}
