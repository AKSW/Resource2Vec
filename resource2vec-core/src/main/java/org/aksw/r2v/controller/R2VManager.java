package org.aksw.r2v.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.aksw.r2v.model.R2VModel;
import org.aksw.r2v.strategy.TfidfFEXStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;


/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class R2VManager {

	private final static Logger logger = LogManager.getLogger(R2VManager.class);
			
	/**
	 * Train on resources of a given class.
	 * 
	 * @param filename
	 * @param strategy
	 * @param classname
	 * @return
	 */
	public static R2VModel train(String filename,
			TfidfFEXStrategy strategy, String classname) {
		
		OWLOntology o = getOntology(filename);
		R2VModel model = new R2VModel(o, strategy);
		
		for(OWLIndividual ind : getIndividuals(classname, o)) {
			model.add(ind.asOWLNamedIndividual());
		}
		
		model.stringFeatures();
		model.normalize();
		
		return model;
	}
	
	/**
	 * Train on all resources.
	 * 
	 * @param filename
	 * @param strategy
	 * @return
	 */
	public static R2VModel train(String filename,
			TfidfFEXStrategy strategy) {
		return train(filename, strategy, OWL.Thing.toStringID());
	}
	
	/**
	 * @param filename
	 * @return
	 */
	private static OWLOntology getOntology(String filename) {
		File file = new File(filename);
		
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();

		OWLOntology o;
		try {
			o = m.loadOntologyFromOntologyDocument(IRI.create(file.toURI()));
		} catch (OWLOntologyCreationException e) {
			fail("Cannot load ontology.");
			return null;
		}
		assertNotNull(o);

		return o;
	}
	
	/**
	 * @param superclass
	 * @param o
	 * @return
	 */
	private static Set<OWLIndividual> getIndividuals(String superclass,
			OWLOntology o) {
		
		OWLReasoner reasoner = PelletReasonerFactory.getInstance()
				.createReasoner(o);
		Set<OWLNamedIndividual> instances = reasoner.getInstances(
				OWL.Class(IRI.create(superclass)), false).getFlattened();
		
		// filter out all owl:sameAs instances...
		Set<OWLIndividual> ind = new TreeSet<>();
		for (OWLNamedIndividual i : instances) {
			ind.add(i);
		}
		logger.info("|I| = " + ind.size());
		logger.debug("I = " + ind);

		return ind;

	}
}
