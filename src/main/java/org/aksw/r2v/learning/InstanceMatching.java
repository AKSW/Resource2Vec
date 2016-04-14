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
		
		int limit = 3;
		
		// load matrices
		DoubleMatrix c1 = loadMatrix("person11");
		DoubleMatrix c2 = loadMatrix("person12");
		
		// load labels
		ArrayList<String> l1 = loadLabels("person11");
		ArrayList<String> l2 = loadLabels("person12");
		
//		for(String l : l1)
//			System.out.println(l + " => "+ sameEntityOf(l));
		
		// index vectors from dataset 2
		HashMap<String, DoubleMatrix> c2index = new HashMap<>();
		for(int i=0; i<l2.size(); i++)
			c2index.put(l2.get(i), c2.getRow(i));
		
		PrintWriter pw = new PrintWriter(new File("etc/learning/M.csv"));
		
		int pos = 0;
		for(int i=0; i < (c1.rows / limit) * limit; i++) {
			
//			System.out.println("Row #"+i);
			
//			// extract submatrices with rows = i, ..., i+limit
			String p1 = l1.get(i);
			String p2 = sameEntityOf(p1);
			DoubleMatrix v = c2index.get(p2);
			if(v == null) // no sameas here...
				continue;
			
			DoubleMatrix t1 = new DoubleMatrix(limit, c1.columns);
			t1.putRow(pos, c1.getRow(i));
			DoubleMatrix t2 = new DoubleMatrix(limit, c2.columns);
			t2.putRow(pos, v);
			pos++;
//			System.out.println("pos = " + pos);
			
			if(pos == limit) {
				DoubleMatrix M = Solve.pinv(t1).mmul(t2);
				String s = "";
				for(double d : M.data)
					s += d + "\t";
				pw.println(s.trim());
				System.out.println("M = " + M);
				pos = 0;
			}
			
		}
		
		pw.close();
		
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

	private static DoubleMatrix loadMatrix(String dir) throws FileNotFoundException {
		
		
		Scanner in = new Scanner(new File("etc/"+dir+"/C3.csv"));
		int length = 0, dim = 0;
		while(in.hasNextLine()) {
			String line = in.nextLine();
			if(dim == 0)
				dim = line.split("\t").length;
			length++;
		}
		System.out.println(length + " x "+dim);
		DoubleMatrix t = new DoubleMatrix(length, dim);
		in.close();
		in = new Scanner(new File("etc/"+dir+"/C3.csv"));
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
