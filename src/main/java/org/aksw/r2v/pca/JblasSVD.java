package org.aksw.r2v.pca;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jblas.DoubleMatrix;
import org.jblas.Singular;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class JblasSVD {
	
	private static Logger logger = LogManager.getLogger(JblasSVD.class);

	public static void main(String[] args) {

		double[][] A = { { 36, 49, 47, 11 }, { 2, 68, 27, 42 },
				{ 42, 25, 38, 3 } };
		
		for(int dim=1; dim<A[0].length; dim++) {
			DoubleMatrix C = pca(A, dim);
			visual("C(dim="+dim+")", C);
//			logger.info("====================");
		}

	}
	
	public static DoubleMatrix pca(double[][] A, int n) {
		
		return pca(new DoubleMatrix(A), n);
		
	}
	
	public static DoubleMatrix pca(DoubleMatrix A, int dim) {
		
//		visual("A", A);
		
		DoubleMatrix[] usv = Singular.fullSVD(A);
		DoubleMatrix U = usv[0];
		DoubleMatrix S = usv[1];
//		DoubleMatrix V = usv[2];
		
		// reduce last 'n' elements of diagonal S...
		int n = S.length - dim;
		for(int i=0; i<n; i++)
			S.put(S.length - 1 - i, 0d);
		
//		visual("U", U);
//		visual("S", S);
//		visual("V", V);
		
		// build S matrix
		DoubleMatrix Sm = new DoubleMatrix(S.length, S.length + 1);
		for (int i = 0; i < S.length; i++) {
			Sm.put(i, i, S.get(i));
		}
		
//		DoubleMatrix Aout = U.mmul(Sm).mmul(V.transpose());
//		visual("Aout", Aout);
		
		// calculate principal component matrix...
		DoubleMatrix B = U.mmul(Sm);
		visual("B", B);
		// ...and its column rank
		int r = colRank(B);
		logger.info("col_rank = "+r);
		// keep only 'r' columns...
		double[][] cData = new double[B.rows][r];
		for(int i=0; i<B.rows; i++)
			for(int j=0; j<r; j++)
				cData[i][j] = B.get(i, j);
		// ...and save them in a new matrix
		DoubleMatrix C = new DoubleMatrix(cData);
		
		return C;
		
	}

	
	/**
	 * http://stats.stackexchange.com/questions/134282/relationship-between-svd-and-pca-how-to-use-svd-to-perform-pca
	 * 
	 * @param A
	 * @param dim
	 * @return
	 */
	public static DoubleMatrix pca2(DoubleMatrix A, int dim) {
		
		DoubleMatrix[] usv = Singular.fullSVD(A);
		DoubleMatrix U = usv[0];
		DoubleMatrix S = usv[1];
		
		// 
		DoubleMatrix Uk = new DoubleMatrix(U.rows, dim);
		for(int i=0; i<dim; i++)
			Uk.putColumn(i, U.getColumn(i));
		visual("Uk", Uk);
		
		// build S matrix
		DoubleMatrix Sm = new DoubleMatrix(dim, dim);
		for (int i = 0; i < dim; i++) {
			Sm.put(i, i, S.get(i));
		}
		visual("Sm", Sm);
		
		// calculate principal component matrix...
		DoubleMatrix B = Uk.mmul(Sm);
		visual("B", B);
		
		return B;
		
	}

	private static int colRank(DoubleMatrix B) {
		DoubleMatrix Bsums = B.columnSums();
		visual("Bsums", Bsums);
		for(int i=0; i<Bsums.length; i++)
			if(Bsums.get(i) == 0d)
				return i;
		return Bsums.length;
	}

	public static void visual(String name, Object o) {
		
		new File("etc/").mkdir();
		logger.info("Saving '"+name+"' to file...");
		
		try {
			PrintWriter pw = new PrintWriter(new File("etc/" + name + ".txt"));
			for(String str : o.toString().replaceAll(", ", "\t").split(";"))
				pw.println(str);
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		logger.info(name + " =");
//		for(String str : o.toString().split(";"))
//			logger.info(str);
	}

}
