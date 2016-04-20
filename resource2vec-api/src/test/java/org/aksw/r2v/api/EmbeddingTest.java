package org.aksw.r2v.api;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class EmbeddingTest {

	public static void main(String[] args) {
		String dataset = "http://tommaso-soru.it/rdf/OAEI-Person11.rdf";
		String method = "RESCAL";
		String hyperp = "rank=2";
		
		RDFEmbeddingController c = new RDFEmbeddingController();
		c.rdfEmbedding(dataset, method, hyperp);
		

	}

}
