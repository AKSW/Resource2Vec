package org.aksw.r2v.pca;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ujmp.core.Matrix;
import org.ujmp.core.SparseMatrix;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.util.UJMPSettings;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class UjmpSVD {

	public static void main(String[] args) {

		SparseMatrix A = SparseMatrix.Factory.zeros(123, 64);

		for (int i = 0; i < 123; i++)
			A.setAsDouble(Math.random(), (long) (Math.random() * 123),
					(long) (Math.random() * 64));
		
		
		
		System.out.println(A instanceof SparseMatrix);
		UjmpSVD u = new UjmpSVD("XYZ");
		SparseMatrix B = u.pca(A, 3);
		System.out.println(B instanceof SparseMatrix);
		u.normalize(B);
		System.out.println(B instanceof SparseMatrix);

		B.showGUI();
		
	}

	private String dir;

	protected static Logger logger = LogManager.getLogger(UjmpSVD.class);

	public UjmpSVD(String dir) {
		super();
		this.dir = dir;
		new File("etc/" + dir).mkdirs();
		UJMPSettings.getInstance().setUseJBlas(true);
	}

	private void centerData(SparseMatrix A) {
		logger.info("Centering data...");
		A.center(Ret.ORIG, 0, true);
	}

	public SparseMatrix pca(SparseMatrix A, int dim) {

		logger.info("Type 'pca' started (target dimensions = " + dim + ").");

		centerData(A);

		logger.info("Computing SVD...");
		Matrix[] svd = A.svd();
		Matrix U = svd[0];
		Matrix S = svd[1];

		logger.info("Reducing U to Uk...");
		Matrix Uk = U.select(Ret.NEW, "*;0-" + dim);

		logger.info("Reducing S to Sk...");
		Matrix Sk = S.select(Ret.NEW, "0-" + dim + ";0-" + dim);

		logger.info("Computing principal components...");
		return (SparseMatrix) Uk.mtimes(Sk);
	}

	public void normalize(SparseMatrix A) {
		logger.info("Normalizing hypercube into [0,1]^n...");
		A.normalize(Ret.ORIG, 0);
	}

	public void saveAs(String output, SparseMatrix Cn) {

		// Cn.showGUI();

		String filename = "etc/" + dir + "/" + output + ".csv";

		logger.info("Saving Cn to file {}...", filename);

		try {

			Cn.exportTo().file(filename).asDenseCSV('\t');

		} catch (IOException e) {
			logger.error(e);
		}

	}

}
