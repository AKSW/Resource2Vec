package org.aksw.r2v.visual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class SageVisualization {

	private static final int DIM = 3;

	public static void main(String[] args) throws FileNotFoundException {
		
		PrintWriter pw = new PrintWriter(new File("etc/person11/pca_scatter_plot_svd3.py"));
		
		Scanner in = new Scanner(new File("etc/person11/C3.csv"));
		pw.print("points = [");
		while(in.hasNextLine()) {
			String[] line = in.nextLine().split("\t");
			pw.print("(");
			for(int i=0; i<DIM; i++) {
				String val = line[i];
				val = val.trim();
				if(val.startsWith("["))
					val = val.substring(1);
				if(val.endsWith("]"))
					val = val.substring(0, val.length() - 1);
				Double d = Double.parseDouble(val);
				pw.print(d + ",");
			}
			pw.print("),");
		}
		in.close();

		pw.println("]");
		pw.println("p = point3d(points,size=10,color='blue')");
		pw.println("show(p)");
		
		pw.close();
		
	}

}
