package org.aksw.r2v.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class CollectData {

	public static ArrayList<String> resources(String tmpPath) throws FileNotFoundException {
		ArrayList<String> res = new ArrayList<>();
		Scanner in1 = new Scanner(new File(tmpPath + "/resources.tsv"));
		while (in1.hasNextLine())
			res.add(in1.nextLine());
		in1.close();
		return res;
	}

	public static File vectors(String tmpPath, String method, String hyperp, ArrayList<String> res) throws FileNotFoundException {
		File arff = new File(tmpPath + "/dataset.arff");
		Scanner in1 = new Scanner(new File(tmpPath + "/vectors.tsv"));
		// build arff file
		PrintWriter pw = new PrintWriter(arff);
		String hyp = hyperp.isEmpty() ? "" : " " + hyperp;
		pw.println("@RELATION \"" + method.toUpperCase() + hyp + "\"\n");
		pw.println("@ATTRIBUTE URI STRING");
		for (int i = 0; in1.hasNextLine(); i++) {
			// for each instance...
			String uri = res.get(i);
			String[] vec = in1.nextLine().split("\t");
			if (i == 0)
				header(pw, vec.length);
			StringBuffer sb = new StringBuffer();
			sb.append("\"" + uri + "\",");
			for (String v : vec)
				sb.append(v + ",");
			sb.deleteCharAt(sb.length() - 1);
			pw.println(sb.toString());
		}
		pw.close();
		in1.close();
		return arff;
	}
	
	private static void header(PrintWriter pw, int length) {
		for (int i = 0; i < length; i++)
			pw.println("@ATTRIBUTE dim" + (i + 1) + " NUMERIC");
		pw.println();
		pw.println("@DATA");
	}


}
