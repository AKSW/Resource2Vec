package org.aksw.r2v.pca;

import java.util.Arrays;

import org.jblas.DoubleMatrix;
import org.jblas.Singular;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class JblasSVD {

	public static void main(String[] args) {

		double[][] A = { { 36, 49, 47, 11 }, { 2, 68, 27, 42 },
				{ 42, 25, 38, 3 } };
		
		for(int i=0; i<=2; i++) {
			DoubleMatrix B = pca(A, i);
			System.out.println("B("+i+") = "+vis(B));
		}

	}
	
	public static DoubleMatrix pca(double[][] A, int n) {
		
		for(double[] row : A)
			System.out.println(Arrays.toString(row));

		DoubleMatrix[] usv = Singular.fullSVD(new DoubleMatrix(A));
		DoubleMatrix U = usv[0];
		DoubleMatrix S = usv[1];
		DoubleMatrix V = usv[2];
		
		for(int i=0; i<n; i++)
			S.put(S.length - 1 - i, 0d);
		System.out.println("U = " + vis(U));
		System.out.println("S = " + vis(S));
		System.out.println("V = " + vis(V));
		
		DoubleMatrix Sm = new DoubleMatrix(S.length, S.length + 1);
		for (int i = 0; i < S.length; i++) {
			Sm.put(i, i, S.get(i));
		}
		DoubleMatrix Aout = U.mmul(Sm).mmul(V.transpose());
		System.out.println("Aout = " + vis(Aout));
		
		return U.mmul(Sm);
		
	}

	public static String vis(Object o) {
		return o.toString().replace(";", "\n");
	}

}
