package org.aksw.r2v.pca;

import com.mkobos.pca_transform.PCA;
import com.mkobos.pca_transform.PCA.TransformationType;

import Jama.Matrix;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class PCAnalysis {
	
	private Matrix mIn;
	private Matrix mOut;
	private PCA pca;
	private TransformationType type;
	
	public PCAnalysis(int m, int n, TransformationType type) {
		super();
		mIn = new Matrix(m, n);
		this.type = type;
	}
	
	public void addValue(int i, int j, double d) {
		mIn.set(i, j, d);
	}
	
	public double[][] transform() {
		pca = new PCA(mIn);		
		mOut = pca.transform(mIn, type);
		return mOut.getArray();
	}
	
    public static void main(String[] args){
        System.out.println("Running a demonstration program on some sample data ...");
        /** Training data matrix with each row corresponding to data point and
        * each column corresponding to dimension. */
        Matrix trainingData = new Matrix(new double[][] {
            {1, 2, 3, 4, 5, 6},
            {6, 5, 4, 3, 2, 1},
            {2, 2, 2, 2, 2, 2}});
        PCA pca = new PCA(trainingData);
        /** Test data to be transformed. The same convention of representing
        * data points as in the training data matrix is used. */
        Matrix testData = new Matrix(new double[][] {
                {1, 2, 3, 4, 5, 6},
                {1, 2, 1, 2, 1, 2}});
        /** The transformed test data. */
        Matrix transformedData =
            pca.transform(testData, PCA.TransformationType.WHITENING);
        System.out.println("Transformed data (each row corresponding to transformed data point):");
        for(int r = 0; r < transformedData.getRowDimension(); r++){
            for(int c = 0; c < transformedData.getColumnDimension(); c++){
                System.out.print(transformedData.get(r, c));
                if (c == transformedData.getColumnDimension()-1) continue;
                System.out.print(", ");
            }
            System.out.println("");
        }
    }

}
