package org.aksw.r2v.pca;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.aksw.r2v.visual.SageVisualization;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jblas.DoubleMatrix;
import org.jblas.Singular;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class JblasSVD {
	
	protected static Logger logger = LogManager.getLogger(JblasSVD.class);
	
	private String dir;

	public static void main(String[] args) {

		int d = 5;
		DoubleMatrix A = DoubleMatrix.randn(d, d);
		
		JblasSVD svd = new JblasSVD("asd");
		
		for(int dim=1; dim<d; dim++) {
			DoubleMatrix C = svd.pca(A, dim);
			svd.visual("C"+dim, C);
			logger.info("====================");
		}
		
		try {
			SageVisualization.run("asd", "http://dbpedia.org/resource/");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public JblasSVD(String dir) {
		super();
		this.dir = dir;
		new File("etc/" + dir).mkdirs();
	}
	
	private DoubleMatrix centerData(DoubleMatrix A) {
		
		DoubleMatrix means = A.columnMeans();
		
		for(int i=0; i<A.rows; i++)
			for(int j=0; j<A.columns; j++)
				A.put(i, j, A.get(i, j) - means.get(j));
		
		return A;
	}
	
	public DoubleMatrix pca(double[][] A, int n) {
		
		return pca(new DoubleMatrix(A), n);
		
	}
	
	/**
	 * 
	 * 
	 * @param A
	 * @param dim
	 * @return
	 */
	public DoubleMatrix pca(DoubleMatrix A, int dim) {
		
		logger.info("Type 'pca' started.");
		
		A = centerData(A);
		visual("A", A);
		
		logger.info("Computing SVD...");
		DoubleMatrix[] usv = Singular.fullSVD(A);
		DoubleMatrix U = usv[0];
		DoubleMatrix S = usv[1];
		
		visual("U", U);
		visual("S", S);
		
		// 
		logger.info("Reducing U to Uk...");
		DoubleMatrix Uk = new DoubleMatrix(U.rows, dim);
		for(int i=0; i<dim; i++)
			Uk.putColumn(i, U.getColumn(i));
		visual("Uk", Uk);
		
		// build S matrix
		logger.info("Reducing S to Sk...");
		DoubleMatrix Sk = new DoubleMatrix(dim, dim);
		for (int i = 0; i < dim; i++) {
			Sk.put(i, i, S.get(i));
		}
		visual("Sk", Sk);
		
		// calculate principal component matrix...
		logger.info("Computing principal components...");
		DoubleMatrix B = Uk.mmul(Sk);
		visual("B", B);
		
		return B;
		
	}
	
	
	/**
	 * @param A
	 * @param k
	 * @return
	 */
	public DoubleMatrix reconstruct(DoubleMatrix A, int k) {
		
		logger.info("Type 'reconstruct' started...");

		A = centerData(A);
		visual("A", A);
		
		DoubleMatrix[] usv = Singular.fullSVD(A);
		// n x n
		DoubleMatrix U = usv[0];
		// n x p
		DoubleMatrix S = usv[1];
		// p x p (straight)
		DoubleMatrix V = usv[2];
		
		visual("U", U);
		visual("S", S);
		visual("V", V);
		
		// n x k
		DoubleMatrix Uk = new DoubleMatrix(U.rows, k);
		for(int i=0; i<k; i++)
			Uk.putColumn(i, U.getColumn(i));
		visual("Uk", Uk);
		
		// k x k
		DoubleMatrix Sk = new DoubleMatrix(k, k);
		for (int i = 0; i < k; i++) {
			Sk.put(i, i, S.get(i));
		}
		visual("Sk", Sk);
		
		// p x k (straight)
		DoubleMatrix Vk = new DoubleMatrix(A.columns, k);
		for (int i = 0; i < k; i++) {
			Vk.putColumn(i, V.getColumn(i));
		}
		visual("Vk", Vk);
 		
		// 
		DoubleMatrix Aapprox = Uk.mmul(Sk).mmul(Vk.transpose());
		visual("Aapprox", Aapprox);
		
		DoubleMatrix Areduced = new DoubleMatrix(A.rows, k);
		for(int i=0; i<k; i++)
			Areduced.putColumn(i, Aapprox.getColumn(i));
		visual("C"+k, Areduced);
		
		return Areduced;
		
	}
	
	/**
	 * @param A
	 * @param k
	 * @return
	 */
	public DoubleMatrix compress(DoubleMatrix A, int k) {
		
		logger.info("Type 'compress' started...");

		A = centerData(A);
		
		DoubleMatrix[] usv = Singular.fullSVD(A);
		// n x n
		DoubleMatrix U = usv[0];
		// n x p
		DoubleMatrix S = usv[1];
		// p x p (straight)
		DoubleMatrix V = usv[2];
				
		// k x k
		DoubleMatrix Sk = new DoubleMatrix(U.columns, V.columns);
		for (int i = 0; i < k; i++) {
			Sk.put(i, i, S.get(i));
		}
		
		// 
		DoubleMatrix Aapprox = U.mmul(Sk).mmul(V.transpose());
		visual("Aapprox", Aapprox);
		
		return Aapprox;
		
	}
	
	public DoubleMatrix reconstruct2(DoubleMatrix A, int k) {
		
		logger.info("Type 'reconstruct2' started...");

		A = centerData(A);
		visual("A", A);
		
		DoubleMatrix[] usv = Singular.fullSVD(A);
		// n x n
		DoubleMatrix U = usv[0];
		// n x p
		DoubleMatrix S = usv[1];
		// p x p (straight)
		DoubleMatrix V = usv[2];
		
		visual("U", U);
		visual("S", S);
		visual("V", V);
		
		for(int i = k; i < U.columns; i++)
			U.putColumn(i, DoubleMatrix.zeros(U.rows, 1));
		visual("Uk", U);
		
		DoubleMatrix Sm = new DoubleMatrix(A.rows, A.columns);
		for (int i = 0; i < k; i++) {
			Sm.put(i, i, S.get(i));
		}
		visual("Sk", Sm);
		
//		for (int i = k; i < V.rows; i++) {
//			V.putColumn(i, DoubleMatrix.zeros(1, V.columns));
//		}
//		visual("Vk", V);
 		
		// 
		DoubleMatrix Aapprox = U.mmul(Sm).mmul(V.transpose());
		visual("Aapprox", Aapprox);
				
		return Aapprox;
	}


	public void visual(String name, DoubleMatrix o) {
		
		new File("etc/").mkdir();
		logger.info("Saving '"+name+"' to file...");
		
		try {
			PrintWriter pw = new PrintWriter(new File("etc/" + dir + "/" + name + ".csv"));
			for(int i=0; i<o.rows; i++) {
				for(int j=0; j<o.columns; j++) {
					pw.print(o.get(i, j));
					if(j < o.columns - 1)
						pw.print("\t");
				}
				pw.println();
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void normalize(DoubleMatrix A, String outname) {
		
		DoubleMatrix B = new DoubleMatrix(A.rows, A.columns);
		DoubleMatrix max = A.columnMaxs();
		DoubleMatrix min = A.columnMins();
		
		logger.info("Normalizing hypercube into [0,1]^n...");

		for(int j=0; j<B.columns; j++) {
			double mx = max.get(j);
			double mn = min.get(j);
			if(mx == mn) {
				B.putColumn(j, DoubleMatrix.zeros(A.rows, 1));
				continue;
			}
			for(int i=0; i<B.rows; i++)
				B.put(i, j, (A.get(i, j) - mn) / (mx - mn));
		}
		
		visual(outname, B);
	}

}
