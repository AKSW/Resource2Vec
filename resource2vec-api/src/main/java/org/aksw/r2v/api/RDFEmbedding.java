package org.aksw.r2v.api;

import java.util.HashMap;

/**
 * 
 * dataset = "file:///tmp/resource2vec/datasets/yagoSchema.ttl"; // this is used internally and doesn't have to be revealed
 * method = "RESCAL"; // this is the algorithm which created the embeddings
 * hyperp = "rank=2"; // hyperparameters (then you can use the map)
 * embeddings = "http://w3id.org/resource2vec/embeddings/GENERATEDHASH"; // embeddings URI that will redirect to OpenML
 *  
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
	
	public HashMap<String, String> getHyperp() {
		return RDFEmbeddingController.toMap(hyperp);
	}
	
}