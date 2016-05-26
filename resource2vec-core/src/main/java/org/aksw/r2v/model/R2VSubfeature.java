package org.aksw.r2v.model;

import java.io.Serializable;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class R2VSubfeature implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1230271134490452879L;
	
	private Double value;
	private Double normValue;
	private String name;
	private R2VFeature feature;
	
	public R2VSubfeature(R2VFeature feature, String name, Double value) {
		super();
		this.feature = feature;
		this.name = name;
		this.value = value;
	}
	
	public Double getValue() {
		return value;
	}
	
	public R2VFeature getFeature() {
		return feature;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public String getName() {
		return name;
	}
	
	public Double getNormValue() {
		return normValue;
	}

	public void setNormValue(Double normValue) {
		this.normValue = normValue;
	}
}
