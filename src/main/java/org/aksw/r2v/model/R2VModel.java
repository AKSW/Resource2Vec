package org.aksw.r2v.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.aksw.r2v.pca.JblasSVD;
import org.aksw.r2v.pca.PCAnalysis;
import org.aksw.r2v.strategy.FEXStrategy;
import org.aksw.r2v.strategy.TfidfFEXStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jblas.DoubleMatrix;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import com.mkobos.pca_transform.PCA.TransformationType;


/**
 * Resource2Vec model.
 * 
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class R2VModel {
	
	private final static Logger logger = LogManager.getLogger(R2VModel.class);
	
	private OWLOntology ontology;
	
	// as many indexes (tf-idf) as properties
	private HashMap<String, R2VProperty> properties = new HashMap<>();
	private HashMap<String, R2VInstance> instances = new HashMap<>();
	
	private HashMap<String, Double> mean;
	
	private FEXStrategy strategy;

	public R2VModel(OWLOntology ontology, FEXStrategy strategy) {
		super();
		this.ontology = ontology;
		this.strategy = strategy;
	}

	public FEXStrategy getStrategy() {
		return strategy;
	}
	
	public void normalize() {
		HashMap<String, Double> min = new HashMap<>();
		HashMap<String, Double> max = new HashMap<>();
		
		// find min/max
		for(R2VInstance instance : instances.values()) {
			HashMap<R2VProperty, R2VFeature> features = instance.getFeatures();
			for(R2VProperty property : features.keySet()) {
				String key = property.getUri();
				R2VFeature feature = features.get(property);
				for(R2VSubfeature subf : feature.getSubfeatures().values()) {
					Double d = subf.getValue();
					if(!min.containsKey(key))
						min.put(key, d);
					else {
						if(d < min.get(key))
							min.put(key, d);
					}
					if(!max.containsKey(key))
						max.put(key, d);
					else {
						if(d > max.get(key))
							max.put(key, d);
					}
				}
			}
		}
		
		// handle min=max
		for(String key : min.keySet())
			if(min.get(key) == max.get(key)) {
				if(min.get(key) == 0d) { // (0,0) becomes (0,1) => all zeroes
					max.put(key, 1d);
				} else { // (n,n) with n != 0 becomes (0,n) => all ones
					min.put(key, 0d);
				}
			}
		
		// normalize
		for(R2VInstance instance : instances.values()) {
			HashMap<R2VProperty, R2VFeature> features = instance.getFeatures();
			for(R2VProperty property : features.keySet()) {
				String key = property.getUri();
				R2VFeature feature = features.get(property);
				for(R2VSubfeature subf : feature.getSubfeatures().values()) {
					subf.setNormValue((subf.getValue() - min.get(key)) / (max.get(key) - min.get(key)));
				}
			}
			logger.trace(instance.getUri() + "\t" + instance.getFlatSparseVector());
		}
	}
	
	/**
	 * 	Compute features for string values.
	 */
	public void stringFeatures() {
		
		if(strategy instanceof TfidfFEXStrategy) {
			// for each property
			for(R2VProperty property : properties.values())
				property.getTextIndex().compute();
			
			// compute indexes (word2vec or tf-idf)
			for(R2VInstance instance : instances.values()) {
				for(R2VProperty property : instance.getFeatures().keySet()) {
					TfidfIndex index = property.getTextIndex();
					R2VFeature feature = instance.getFeatures().get(property);
					if(feature.getType().equals(R2VFeatureType.STRING)) {
						Map<String, Double> map = index.tfidf(feature.getStringValue());
						for(String term : map.keySet()) {
							R2VSubfeature sub = new R2VSubfeature(feature, term, map.get(term));
							feature.getSubfeatures().put(term, sub);
						}
					}
				}
			}
		} else {
			logger.warn("Unknown FEX strategy type! Skipping string processing...");
		}
		
	}
	
	/**
	 * @param ind
	 */
	public void add(OWLNamedIndividual ind) {
		
		logger.trace("Processing individual "+ind);
		
		// index individual
		R2VInstance instance = new R2VInstance(this, ind);
		instances.put(instance.getUri(), instance);

		// get CBD
		Set<OWLAxiom> cbd = new HashSet<>();
		cbd.addAll(ontology.getAnnotationAssertionAxioms(ind.getIRI()));
		cbd.addAll(ontology.getDataPropertyAssertionAxioms(ind));
		cbd.addAll(ontology.getObjectPropertyAssertionAxioms(ind));
		
		logger.trace("CBD size = "+cbd.size());
				
		// compute sparse vector
		
		// for each triple
		for(OWLAxiom axiom : cbd) {
//			logger.info(axiom.toString());
			// check object type
			Triple triple = new Triple();
			if(axiom.isOfType(AxiomType.ANNOTATION_ASSERTION)) {
				OWLAnnotationAssertionAxiom ax = (OWLAnnotationAssertionAxiom) axiom;
				triple.setSubjURI(ax.getSubject().toString());
				triple.setPropURI(ax.getProperty().getIRI().toString());
				try {
					OWLLiteral lit = ax.getValue().asLiteral().get();
					// datatype property
					triple.setDatatype(lit.getDatatype());
					triple.setValue(lit.getLiteral());
				} catch (Exception e) {
					// object property
					triple.setValue(ax.getValue().toString());
				}
			} else if(axiom.isOfType(AxiomType.DATA_PROPERTY_ASSERTION)) {
				OWLDataPropertyAssertionAxiom ax = (OWLDataPropertyAssertionAxiom) axiom;
				triple.setSubjURI(ax.getSubject().toString());
				triple.setPropURI(ax.getProperty().asOWLDataProperty().getIRI().toString());
				OWLLiteral lit = ax.getObject();
				// datatype property
				triple.setDatatype(lit.getDatatype());
				triple.setValue(lit.getLiteral());
			} else if(axiom.isOfType(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
				OWLObjectPropertyAssertionAxiom ax = (OWLObjectPropertyAssertionAxiom) axiom;
				triple.setSubjURI(ax.getSubject().toString());
				triple.setPropURI(ax.getProperty().asOWLObjectProperty().getIRI().toString());
				// object property
				triple.setValue(ax.getObject().toString());
			} else {
				logger.warn("Axiom not processed: "+axiom);
				continue;
			}
			logger.trace(triple.toString());
			
			// index property if not done already
			R2VProperty property;
			if(!properties.containsKey(triple.getPropURI())) {
				property = new R2VProperty(this, triple.getPropURI());
				properties.put(property.getUri(), property);
			} else {
				property = properties.get(triple.getPropURI());
			}
			
			// index feature
			R2VFeature feature;
			if(!instance.getFeatures().containsKey(property)) {
				feature = new R2VFeature(this, instance, property);
				instance.getFeatures().put(property, feature);
			} else {
				feature = instance.getFeatures().get(property);
			}
			
			if(triple.hasObjectProperty()) {
				// uri -> add to sparse vectors (boolean value)
				logger.trace("   ######## URI #########");
				feature.getSubfeatures().put(triple.getValue(), new R2VSubfeature(feature, triple.getValue(), 1.0));
				feature.setType(R2VFeatureType.URI);
			} else {
				OWLDatatype dt = triple.getDatatype();
				logger.trace(dt.toString());
				if(dt.isBoolean() || dt.isDouble() || dt.isFloat() || dt.isInteger()) {
					// numeric/date -> add to sparse vectors
					logger.trace("   ######## NUMERIC #########");
					feature.add(Double.parseDouble(triple.getValue()));
					feature.setType(R2VFeatureType.NUMERICAL);
				} else {
					// string -> add to property index (property->index)
					logger.trace("   ######## STRING #########");
					feature.setType(R2VFeatureType.STRING);
					feature.setStringValue(triple.getValue());
					property.getTextIndex().addNumeric(triple.getValue());
				}
			}
			
		} // end for each axiom
				
	}

	/**
	 * @return
	 */
	private HashMap<String, Double> getMeanPoint() {
		// return cached mean point
		if(mean != null)
			return mean;
		// otherwise, calculate it
		mean = new HashMap<>();
		TreeSet<String> feat = new TreeSet<>();
		for(R2VInstance instance : instances.values()) {
			HashMap<String, Double> sparse = instance.getFlatSparseVector();
			for(String key : sparse.keySet()) {
				Double d = mean.containsKey(key) ? mean.get(key) : 0d;
				mean.put(key, d + sparse.get(key));
			}
			feat.addAll(sparse.keySet());
		}
		for(String key : mean.keySet())
			mean.put(key, mean.get(key) / feat.size());
		return mean;
	}
	
	/**
	 * Euclidean distance of two individuals within the model.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public Double distance(OWLNamedIndividual a, OWLNamedIndividual b) {
		HashMap<String, Double> aV = instances.get(a.toString()).getFlatSparseVector();
		HashMap<String, Double> bV = instances.get(b.toString()).getFlatSparseVector();
		TreeSet<String> commonSpace = new TreeSet<>();
		commonSpace.addAll(aV.keySet());
		commonSpace.addAll(bV.keySet());
		Double sum = 0d;
		for(String dim : commonSpace) {
			Double aVal = aV.containsKey(dim) ? aV.get(dim) : 0d;
			Double bVal = bV.containsKey(dim) ? bV.get(dim) : 0d;
			logger.trace(aVal + "\t" + bVal + "\t" + Math.pow(aVal - bVal, 2));
			sum += Math.pow(aVal - bVal, 2);
		}
		return Math.sqrt(sum);
	}
	
	@Override
	public String toString() {
		return "R2VModel [instances=" + instances + ", strategy=" + strategy
				+ "]";
	}

	public String info() {
		TreeSet<String> subf = new TreeSet<>();
		for(R2VInstance instance : instances.values())
			for(R2VFeature feature : instance.getFeatures().values())
				subf.addAll(feature.getSubfeatures().keySet());
		return "R2VModel #instances="+instances.size()+" #properties="+properties.size()+" #features="+subf.size();
	}

	/**
	 * @param ind
	 * @return
	 */
	public Double distanceFromMeanPoint(OWLNamedIndividual ind) {
		HashMap<String, Double> aV = instances.get(ind.toString()).getFlatSparseVector();
		HashMap<String, Double> bV = getMeanPoint();
		Double sum = 0d;
		for(String dim : bV.keySet()) {
			Double aVal = aV.containsKey(dim) ? aV.get(dim) : 0d;
			Double bVal = bV.get(dim);
			sum += Math.pow(aVal - bVal, 2);
		}
		return Math.sqrt(sum);
	}

	/**
	 * @param args 
	 * 
	 */
	public void reduce(String args) {
		
		logger.info("Starting dimensionality reduction with argument = '"+args+"'...");
		
		HashMap<String, Double> mp = getMeanPoint();
		// assign an integer index to each feature
		HashMap<String, Integer> index = new HashMap<>();
		int j = 0;
		for(String feat : mp.keySet())
			index.put(feat, j++);
		
		String mkobos = "mkobos-";
		if(args.startsWith(mkobos)) {
			
			TransformationType type;
			try {
				type = TransformationType.valueOf(args.substring(7).toUpperCase());
			} catch (IllegalArgumentException e) {
				logger.error("Unknown transformation type. Allowed: rotation, whitening.");
				return;
			}
			
			PCAnalysis pca = new PCAnalysis(instances.size(), mp.size(), type);
			
			// fill out PCA input matrix
			Iterator<R2VInstance> it = instances.values().iterator();
			for(int i=0; it.hasNext(); i++) {
				R2VInstance inst = it.next();
				HashMap<String, Double> fsv = inst.getFlatSparseVector();
				for(String feat : fsv.keySet())
					pca.addValue(i, index.get(feat), fsv.get(feat));
			}
			
			// return to first element
			logger.info("Computing PCA...");
			it = instances.values().iterator();
			double[][] out = pca.transform();
			logger.info("===== OUTPUT VECTORS =====");
			for(int i=0; i<out.length; i++) {
				double[] outR = out[i];
				String str = it.next() + "\t";
				for(double outV : outR)
					str += (outV + ", ");
				logger.info(str);
			}
			
		} else if(args.equals("jblas-svd")) {
			
			DoubleMatrix A = new DoubleMatrix(instances.size(), mp.size());
			// fill out PCA input matrix
			Iterator<R2VInstance> it = instances.values().iterator();
			for(int i=0; it.hasNext(); i++) {
				R2VInstance inst = it.next();
				HashMap<String, Double> fsv = inst.getFlatSparseVector();
				for(String feat : fsv.keySet())
					A.put(i, index.get(feat), fsv.get(feat));
			}
			
			logger.info("Computing PCA...");
			DoubleMatrix C3 = JblasSVD.pca2(A, 3);
			logger.info("===== OUTPUT VECTORS =====");
			JblasSVD.visual("C3", C3);
			
		} else {
			
			logger.error("Argument not understood: "+args);
			
		}
		
	}

	
}
