package org.aksw.r2v.visual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class SageVisualization {

	private static final int DIM = 3;
	private static final Double TEXT_Z_OFFSET = 0.03;

	public static void main(String[] args) throws FileNotFoundException {
		
//		run("etc/pca/C3.csv", "etc/pca/pca_scatter_plot.py", "http://dbpedia.org/resource/");
		run("person12", "http://www.okkam.org/oaie/person2-", 2);
		
	}
	
	/**
	 * @param dir
	 * @param namespace
	 * @param dim
	 * @throws FileNotFoundException
	 */
	public static void run(String dir, String namespace, int dim) throws FileNotFoundException {
		
		PrintWriter pw = new PrintWriter(new File("etc/"+dir+"/pca_scatter_plot.py"));
		
		ArrayList<String> uris = new ArrayList<>();
		Scanner lab = new Scanner(new File("etc/"+dir+"/labels.txt"));
		while(lab.hasNextLine())
			uris.add(lab.nextLine());
		lab.close();
		
		StringBuffer sb = new StringBuffer();
		StringBuffer pt = new StringBuffer();
	
		PrintWriter labOut = new PrintWriter(new File("etc/"+dir+"/labels_out.txt"));
		
		Scanner in = new Scanner(new File("etc/"+dir+"/C"+dim+".csv"));
		pw.print("points = [");
		for(int r=0; in.hasNextLine(); r++) {
			String uri = uris.get(r);
			String[] line = in.nextLine().split("\t");
			if(!uri.startsWith(namespace))
				continue;
			String coord = "(", textCoord = "(";
			for(int i=0; i<DIM && i<dim; i++) {
				String val = line[i].trim();
				if(val.startsWith("["))
					val = val.substring(1);
				if(val.endsWith("]"))
					val = val.substring(0, val.length() - 1);
				Double d = Double.parseDouble(val);
				if(i == 2) // write names slightly below
					textCoord += (d - TEXT_Z_OFFSET) + ",";
				else
					textCoord += d + ",";
				coord += d + ",";
			}
			coord += ")";
			textCoord += ")";
			pw.println(coord + ",");
			sb.append("t"+r+" = text3d(\""+uri.substring(namespace.length())+"\", "+textCoord+", color=(0.5,0,0))\n");
			pt.append(" + t" + r);
			labOut.println(uri);
		}
		in.close();
		
		labOut.close();

		pw.println("]");
		pw.println(sb.toString());
		pw.println("p = point3d(points,size=10,color='blue')");
		pw.println("(p" + pt.toString() + ").show(xmin=0, xmax=1, ymin=0, ymax=1, zmin=0, zmax=1)");
		
		pw.close();
		
	}

	/**
	 * @param dir
	 * @param namespace
	 * @param dimInt
	 * @throws FileNotFoundException
	 */
	public static void submatrix(String dir, String namespace, int dimInt) throws FileNotFoundException {
		// labels out
		TreeSet<String> urisOut = new TreeSet<>();
		Scanner labOut = new Scanner(new File("etc/"+dir+"/labels_out.txt"));
		while(labOut.hasNextLine())
			urisOut.add(labOut.nextLine());
		labOut.close();

		// labels
		ArrayList<String> uris = new ArrayList<>();
		Scanner lab = new Scanner(new File("etc/"+dir+"/labels.txt"));
		while(lab.hasNextLine())
			uris.add(lab.nextLine());
		lab.close();

		PrintWriter pw = new PrintWriter(new File("etc/"+dir+"/D"+dimInt+".csv"));
		Scanner in = new Scanner(new File("etc/"+dir+"/C"+dimInt+".csv"));
		for(int i=0; in.hasNextLine(); i++) {
			String line = in.nextLine();
			if(urisOut.contains(uris.get(i)))
				pw.println(line);
		}
		in.close();
		pw.close();
		
	}

}
