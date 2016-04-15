package org.aksw.r2v.learning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.jblas.DoubleMatrix;
import org.jblas.Solve;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class InstanceMatching {

	public static void main(String[] args) throws IOException {
		
//		run(3);
		// TODO compute median();
//		DoubleMatrix Mmed = median();
		wekaInput(10);
		
	}
	
	private static DoubleMatrix median() throws IOException {
		DoubleMatrix Mmed = DoubleMatrix.loadCSVFile("etc/learning/M_median.csv");
		System.out.println(Mmed);
		return Mmed;
	}

	public static void wekaInput(int dim) throws IOException {
		
		// load matrices
		DoubleMatrix c1 = loadMatrix("person11-D10", dim);
		DoubleMatrix c2 = loadMatrix("person12-D10", dim);
		
		// load labels
		ArrayList<String> l1 = loadLabels("person11-D10");
		ArrayList<String> l2 = loadLabels("person12-D10");
				
		// index vectors from dataset 2
		HashMap<String, DoubleMatrix> c2index = new HashMap<>();
		for(int i=0; i<l2.size(); i++)
			c2index.put(l2.get(i), c2.getRow(i));
		
		String file = "etc/learning/weka_"+(int)(Math.random()*10000)+".csv";
		PrintWriter pw = new PrintWriter(new File(file));
		for(int i=0; i < dim; i++) {
			if(i<5)
				pw.print("p1c"+i+",");
			else
				pw.print("p2c"+(i-5)+",");
		}
		pw.println("label");
		
		for(int i=0; i < c1.rows; i++) {
			
			// extract submatrices with rows = i, ..., i+limit
			String p1 = l1.get(i);
			String p2 = sameEntityOf(p1);
			DoubleMatrix v2 = c2index.get(p2);
			if(v2 == null) // no sameas here...
				continue;
			
			DoubleMatrix v1 = c1.getRow(i);
			
			pw.print(v1.toString().substring(1, v1.toString().length() - 1).replaceAll(";", ",") + ",");
			pw.print(v2.toString().substring(1, v2.toString().length() - 1).replaceAll(";", ",") + ",");
			pw.println("POS");
			
			// create k negative examples
			for(int k=0; k<9; k++) {
				int j;
				do {
					j = (int) (Math.random() * c1.rows);
				} while(i == j);
				DoubleMatrix v1n = c1.getRow(j);
				pw.print(v1n.toString().substring(1, v1n.toString().length() - 1).replaceAll(";", ",") + ",");
				pw.print(v2.toString().substring(1, v2.toString().length() - 1).replaceAll(";", ",") + ",");
				pw.println("NEG");
			}
			
		}
		
		pw.close();
		System.out.println("Output file: "+file);

		
	}
	
	public static void run(int dim) throws IOException {
		
		// load matrices
		DoubleMatrix c1 = loadMatrix("person11", dim);
		DoubleMatrix c2 = loadMatrix("person12", dim);
		
		// load labels
		ArrayList<String> l1 = loadLabels("person11");
		ArrayList<String> l2 = loadLabels("person12");
		
//		for(String l : l1)
//			System.out.println(l + " => "+ sameEntityOf(l));
		
		// index vectors from dataset 2
		HashMap<String, DoubleMatrix> c2index = new HashMap<>();
		for(int i=0; i<l2.size(); i++)
			c2index.put(l2.get(i), c2.getRow(i));
		
		String file = "etc/learning/M_"+(int)(Math.random()*10000)+".csv";
		PrintWriter pw = new PrintWriter(new File(file));
		
		int pos = 0, eq = 0;
		DoubleMatrix t1 = new DoubleMatrix(dim, c1.columns);
		DoubleMatrix t2 = new DoubleMatrix(dim, c2.columns);
		for(int i=0; i < (c1.rows / dim) * dim; i++) {
			
//			System.out.println("Row #"+i);
			
			// extract submatrices with rows = i, ..., i+limit
			String p1 = l1.get(i);
			String p2 = sameEntityOf(p1);
			DoubleMatrix v2 = c2index.get(p2);
			if(v2 == null) // no sameas here...
				continue;
			
			DoubleMatrix v1 = c1.getRow(i);
			t1.putRow(pos, v1);
			t2.putRow(pos, v2);
			pos++;
			
			
			if(pos == dim) {
				System.out.println("t1 = "+t1);
				System.out.println("t2 = "+t2);
				DoubleMatrix M = Solve.pinv(t1).mmul(t2);
				String s = "";
				for(double d : M.data)
					s += d + "\t";
				pw.println(s.trim());
				System.out.println("M = " + M);
				eq += dim;
				pos = 0;
				t1 = new DoubleMatrix(dim, c1.columns);
				t2 = new DoubleMatrix(dim, c2.columns);
			}
			
		}
		
		pw.close();
		System.out.println("eq="+eq);
		System.out.println("Output file: "+file);
		
	}

	
	private static String sameEntityOf(String string) {
		string = string.replaceFirst("person1", "person2");
		for(String type : new String[] {"Person", "Address", "Suburb", "State"})
			string = sameEntityOfType(string, type);
		return string; 
	}


	private static String sameEntityOfType(String string, String type) {
		if(string.contains(type)) {
			int x = string.indexOf(type);
			int y = Integer.parseInt(string.substring(x+type.length()));
			string = string.substring(0, x+type.length()) + (y + 1);
		}
		return string;
	}


	private static ArrayList<String> loadLabels(String dir) throws FileNotFoundException {
		ArrayList<String> labels = new ArrayList<>();
		Scanner in = new Scanner(new File("etc/"+dir+"/labels_out.txt"));
		while(in.hasNextLine()) {
			String line = in.nextLine();
			labels.add(line);
		}
		in.close();
		return labels;
	}

	private static DoubleMatrix loadMatrix(String dir, int dim) throws FileNotFoundException {
		
		String input = "etc/"+dir+"/D"+dim+".csv";
		
		Scanner in = new Scanner(new File(input));
		int length = 0;
		while(in.hasNextLine()) {
			in.nextLine();
			length++;
		}
		System.out.println(length + " x "+dim);
		DoubleMatrix t = new DoubleMatrix(length, dim);
		in.close();
		in = new Scanner(new File(input));
		for(int i=0; in.hasNextLine(); i++) {
			String[] coord = in.nextLine().split("\t");
			DoubleMatrix v = new DoubleMatrix(1, coord.length);
			for(int j=0; j<coord.length; j++)
				v.put(j, Double.parseDouble(coord[j]));
			t.putRow(i, v);
		}
		in.close();

		return t;
	}
	
}
