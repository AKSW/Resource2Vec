package org.aksw.r2v.api;

import java.util.ArrayList;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class EntityEmbedding implements Comparable<EntityEmbedding> {

	private final String uri;
	private final ArrayList<Double> vector;

	public EntityEmbedding(String uri, ArrayList<Double> vector) {
		this.uri = uri;
		this.vector = vector;
	}

	public String getURI() {
		return uri;
	}

	public ArrayList<Double> getVector() {
		return vector;
	}

	@Override
	public int compareTo(EntityEmbedding o) {
		return uri.compareTo(o.getURI());
	}
}