package org.aksw.r2v.api;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class DatasetInfo {
	
	private String dataset;
	private String name;
	
	public DatasetInfo(String dataset, String name) {
		super();
		this.dataset = dataset;
		this.name = name;
	}
	
	public String getDataset() {
		return dataset;
	}
	public String getName() {
		return name;
	}

}
