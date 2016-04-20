package org.aksw.r2v.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeSet;

import org.aksw.r2v.api.utils.Shell;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
@RestController
public class RDFEmbeddingController {

	private final static Logger log = LogManager.getLogger(RDFEmbeddingController.class);

	@RequestMapping("/embedding")
	public RDFEmbedding rdfEmbedding(
			@RequestParam(value = "dataset", defaultValue = "") String dataset,
			@RequestParam(value = "method", defaultValue = "") String method,
			@RequestParam(value = "hyperp", defaultValue = "") String hyperp) {

		if (dataset.equals("") || method.equals("")) {
			log.error("Ignoring request: empty string as dataset or method.");
			return null;
		}

		File rdfDataset = download(dataset);

		String tmpPath = getFilename(dataset).replaceAll("\\.", "");
		
		// call rescal
		switch (method.toLowerCase()) {
		case "rescal":
			HashMap<String, String> hyperpMap = toMap(hyperp);
			String command = "/usr/local/bin/python python/rdf_rescal.py "
					+ rdfDataset.getAbsolutePath() + " " + tmpPath
					+ "/ " + hyperpMap.get("rank");
			log.info("Command: "+command);
			Shell.execute(command);
			break;
		default:
			log.error("Embedding method not found.");
			return null;
		}

		// get resources
		ArrayList<String> res = new ArrayList<>();
		try {
			Scanner in1 = new Scanner(new File(tmpPath + "/resources.tsv"));
			while(in1.hasNextLine())
				res.add(in1.nextLine());
			in1.close();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		}
		// get vectors
		try {
			Scanner in1 = new Scanner(new File(tmpPath + "/vectors.tsv"));
			// build arff file
			PrintWriter pw = new PrintWriter(new File(tmpPath + "/dataset.arff"));
			pw.println("@RELATION "+DigestUtils.sha1Hex(dataset)+"\n");
			pw.println("@ATTRIBUTE uri STRING");
			for(int i=0; in1.hasNextLine(); i++) {
				String uri = res.get(i);
				String[] vec = in1.nextLine().split("\t");
				if(i==0)
					header(pw, vec.length);
				StringBuffer sb = new StringBuffer();
				sb.append("\""+uri+"\",");
				for(String v : vec)
					sb.append(v + ",");
				sb.deleteCharAt(sb.length() - 1);
				pw.println(sb.toString());
			}
			pw.close();
			in1.close();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		}

		
		// for each instance...

		// upload arff to openml

		String embeddings = ""; // returned by openml

		RDFEmbedding rdfemb = new RDFEmbedding(dataset, method, hyperp,
				embeddings);
		log.info("Returned: " + rdfemb.getDataset());

		return rdfemb;
	}

	private void header(PrintWriter pw, int length) {
		for(int i=0; i<length; i++)
			pw.println("@ATTRIBUTE dim"+(i+1)+" NUMERIC");
		pw.println();
		pw.println("@DATA");
	}

	private HashMap<String, String> toMap(String hyperp) {
		HashMap<String, String> map = new HashMap<>();
		try {
			for (String entry : hyperp.split(";")) {
				String[] e = entry.split("=");
				map.put(e[0], e[1]);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			log.error(e.getMessage() + " hyperp=" + hyperp);
		}
		return map;
	}

	@RequestMapping("/upload")
	public DatasetInfo upload(
			@RequestParam(value = "dataset", defaultValue = "") String dataset,
			@RequestParam(value = "name", defaultValue = "") String name) {

		if (dataset.equals("") || name.equals(""))
			return null;

		// TODO

		return null;
	}

	private File download(String dataset) {
		try {
			File f = new File(getFilename(dataset));
			FileUtils.copyURLToFile(new URL(dataset), f);
			return f;
		} catch (IOException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	private String getFilename(String dataset) {
		return Application.TMP_PATH + DigestUtils.sha1Hex(dataset)
				+ dataset.substring(dataset.lastIndexOf('.'));
	}
}