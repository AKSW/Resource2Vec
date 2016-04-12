package org.aksw.r2v.visual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class SageVisualization {

	private static final int DIM = 3;

	public static void main(String[] args) throws FileNotFoundException {
		
		run("etc/person11-akswnc9/rec/C3.csv", "etc/person11/pca_scatter_plot_rec.py");
		
	}
	
	public static void run(String input, String output) throws FileNotFoundException {
		
		PrintWriter pw = new PrintWriter(new File(output));
		
		ArrayList<String> uris = new ArrayList<>();
		Scanner lab = new Scanner(new File("etc/labels.txt"));
		while(lab.hasNextLine())
			uris.add(lab.nextLine());
		lab.close();
		
		StringBuffer sb = new StringBuffer();
		
		Scanner in = new Scanner(new File(input));
		pw.print("points = [");
		for(int r=0; in.hasNextLine(); r++) {
			String[] line = in.nextLine().split("\t");
			String coord = "(";
			for(int i=0; i<DIM; i++) {
				String val = line[i].trim();
				if(val.startsWith("["))
					val = val.substring(1);
				if(val.endsWith("]"))
					val = val.substring(0, val.length() - 1);
				Double d = Double.parseDouble(val);
				coord += d + ",";
			}
			coord += ")";
			pw.println(coord + ",");
			sb.append("t"+r+" = text3d(\""+uris.get(r)+"\", "+coord+", color=(0.5,0,0))\n");
		}
		in.close();

		pw.println("]");
		pw.println(sb.toString());
		pw.println("p = point3d(points,size=10,color='blue')");
		String s = "show(p";
		for(int i=0; i<uris.size(); i++)
			s += " + t" + i;
		pw.println(s + ")");
		
		pw.close();
		
	}

}
