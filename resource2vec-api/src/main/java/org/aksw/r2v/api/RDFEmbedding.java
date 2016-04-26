package org.aksw.r2v.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class RDFEmbedding {
	
	private final String dataset;
	private final String method;
	private final String hyperp;
	private String embeddings;
	
	public RDFEmbedding(String dataset, String method, String hyperp, String embeddings) {
		super();
		this.dataset = dataset;
		this.method = method;
		this.hyperp = hyperp;
		this.embeddings = embeddings;
	}
	
	public String getDataset() {
		return dataset;
	}
	
	public String getEmbeddings() {
		return embeddings;
	}

	public String getMethod() {
		return method;
	}

	public void setEmbeddings(String embeddings) {
		this.embeddings = embeddings;
	}
	
	public String getHyperp() {
		return hyperp;
	}
	
}