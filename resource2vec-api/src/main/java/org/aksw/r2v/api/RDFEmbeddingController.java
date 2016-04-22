package org.aksw.r2v.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.aksw.r2v.api.utils.Shell;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.UploadDataSet;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.thoughtworks.xstream.XStream;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
@RestController
public class RDFEmbeddingController {

	private final static Logger log = LogManager
			.getLogger(RDFEmbeddingController.class);

//	@RequestMapping("/embedding")
	public RDFEmbedding rdfEmbedding(
			@RequestParam(value = "dataset", defaultValue = "") String dataset,
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "method", defaultValue = "") String method,
			@RequestParam(value = "hyperp", defaultValue = "") String hyperp) {
		
		if(!log.isInfoEnabled())
			log.warn("Info not enabled!");
		
		if (dataset.equals("") || name.equals("") || method.equals("")) {
			log.error("Ignoring request: dataset or name or method is an empty string.");
			return null;
		}
		
		RDFEmbedding rdfemb = new RDFEmbedding(dataset, method, hyperp, null);

		HashMap<String, String> hyperpMap = toMap(hyperp);
		File rdfDataset = download(dataset);
		String tmpPath = getFilename(dataset).replaceAll("\\.", "");
		
		// call KGE method
		try {
			KGEMethodController.call(method, rdfDataset, tmpPath, hyperpMap);
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}

		// get resources
		ArrayList<String> res;
		try {
			res = CollectData.resources(tmpPath);
		} catch (FileNotFoundException e) {
			log.error("Probably a Python error occurred (e.g., incorrect RDF syntax). " + e.getMessage());
			return null;
		}
		// get vectors
		File arff;
		try {
			arff = CollectData.vectors(tmpPath, method, hyperp, res);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
			return null;
		}

		// upload arff to openml
//		try {
//			int id = OpenMLController.upload(arff, method, name, hyperpMap);
//			rdfemb.setEmbeddings("http://www.openml.org/d/" + id);
//		} catch (Exception e) {
//			log.error(e.getMessage());
//			return null;
//		}
		
		// TODO remove me!
		String tmpArff = System.getProperty("user.dir") + "/public/" + arff.getName();
		log.info("Simulating upload to OpenML (moving arff file locally). "+tmpArff);
		try {
			FileUtils.moveFile(arff, new File(tmpArff));
			log.info("File moved.");
		} catch (IOException e1) {
			log.error(e1.getMessage());
		}
		rdfemb.setEmbeddings(Application.APPLICATION_URL + "/" + arff.getName());
		
		// delete tmp files
		try {
			rdfDataset.delete();
			log.info("Deleted "+rdfDataset.getAbsolutePath());
			File tmpDir = new File(tmpPath + "/");
			FileUtils.deleteDirectory(tmpDir);
			log.info("Deleted dir "+tmpDir.getAbsolutePath());
		} catch (IOException e) {
			log.warn("Could not delete file or path " + tmpPath + "! " + e.getMessage());
		}

		// return object with URL returned by openml
		log.info("Returned: " + rdfemb.getEmbeddings());

		return rdfemb;
	}


	private HashMap<String, String> toMap(String hyperp) {
		HashMap<String, String> map = new HashMap<>();
		if(hyperp.isEmpty())
			return map;
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

	// TODO test method to delete dataset on OpenML
	public void delete(String openmlID) {
		
	}

//	@RequestMapping(method = RequestMethod.POST, value = "/upload")
	public UploadResponse upload(@RequestParam("file") MultipartFile file,
			@RequestParam(value = "name", defaultValue = "") String name) {

		if (file.isEmpty() || name.equals("")) {
			String resp = "Ignoring request: file or name are empty.";
			log.error(resp);
			return new UploadResponse(null, null, resp);
		}

		File newfile = new File(Application.TMP_DATASETS_PATH
				+ file.getOriginalFilename());
		try {
			file.transferTo(newfile);
		} catch (IllegalStateException | IOException e) {
			String resp = e.getMessage();
			log.error(resp);
			return new UploadResponse(null, null, resp);
		}

		String url = newfile.toURI().toString();
		String response = "OK";
		log.info("File " + url + " with name " + name + " got response: "
				+ response);

		return new UploadResponse(url, name, response);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/process")
	public RDFEmbedding process(@RequestParam("file") MultipartFile file,
			@RequestParam(value = "dataset", defaultValue = "") String dataset,
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "method", defaultValue = "") String method,
			@RequestParam(value = "hyperp", defaultValue = "") String hyperp) {

		if (!file.isEmpty()) {

			// upload file
			UploadResponse ur = upload(file, name);
			if (ur.getResponse().equals("OK"))
				dataset = ur.getDataset();
			else
				return null;
		}
		
		if(name.isEmpty())
			name = dataset.substring(dataset.lastIndexOf('/') + 1);

		return rdfEmbedding(dataset, name, method, hyperp);
		
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

class UploadResponse {
	private String dataset;
	private String name;
	private String response;

	public UploadResponse(String dataset, String name, String response) {
		super();
		this.dataset = dataset;
		this.name = name;
		this.response = response;
	}

	public String getDataset() {
		return dataset;
	}

	public String getName() {
		return name;
	}

	public String getResponse() {
		return response;
	}
}