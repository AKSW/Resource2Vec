package org.aksw.r2v.api;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.aksw.mex.log4mex.MEXSerializer;
import org.aksw.mex.log4mex.MyMEX;
import org.aksw.mex.util.*;
import org.aksw.mex.util.MEXEnum.EnumAlgorithms;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public final class MEXHandler {
	
	private static final String VOID = "http://rdfs.org/ns/void#";
	private static final String PROV = "http://www.w3.org/ns/prov#";

	private static final String DC_TERMS = "http://purl.org/dc/terms/";
	
	private static final String MEX_CORE = "http://mex.aksw.org/mex-core#";
	private static final String MEX_ALGO = "http://mex.aksw.org/mex-algo#";
	private static final String MEX_PERF = "http://mex.aksw.org/mex-perf#";

//	private static final MyMEX mex = new MyMEX();
	
	private final static Logger log = LogManager
			.getLogger(MEXHandler.class);

	public static String getJSONString(RDFEmbedding emb) {
//		
//		try {
//
//			mex.setAuthor("Resource2Vec Agent", "resource2vec@googlegroups.com");
//
//			String idAlgo = mex.Configuration().addAlgorithm(
//					MEXEnum.EnumAlgorithms.RESCAL);
//			mex.Configuration().setDataSet(
//					"tmp/resource2vec/datasets/yagoSchema.ttl", "yagoSchema");
//			mex.Configuration().Algorithm(MEXEnum.EnumAlgorithms.RESCAL)
//					.addParameter("rank", "2");
//			mex.Configuration().addFeature(
//					"http://w3id.org/resource2vec/embeddings/"
//							+ DigestUtils.sha1Hex(emb.toString()));
//
//			String idExec = mex.Configuration().addExecution(
//					MEXEnum.EnumExecutionsType.SINGLE, MEXEnum.EnumPhases.TEST);
//			mex.Configuration().Execution(idExec).setAlgorithm(idAlgo);
//
//			mex.Configuration().Execution(idExec)
//					.addPerformance(MEXEnum.EnumMeasures.PROCESSINGTIME, 3245);
//			
//			String path = "";
//			
//			MEXSerializer.getInstance().saveToDisk("resource2vec",
//					"http://w3id.org/resource2vec/", mex,
//					MEXConstant.EnumRDFFormats.JSON_LD);
//			
//			log.info("JSON-LD file saved to "+path);
//
//		} catch (Exception e) {
//			log.error(e.getMessage());
//		}

		return emb.toString();
	}
	
	public static String getJSONStringAlt(RDFEmbedding emb, String name) {
//		Model m = ModelFactory.createDefaultModel();
//		
////		Resource dataset = m.createResource("http://w3id.org/resource2vec/embeddings/" + DigestUtils.sha1Hex(emb.toString()));
//		
//		String methodEnc = "DefaultMethod";
//		try {
//			methodEnc = URLEncoder.encode(emb.getMethod(), "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		Resource method = m.createResource("http://w3id.org/resource2vec/method/" + methodEnc);
//		Resource embeddingsURI = m.createResource(emb.getEmbeddings());
//		HashMap<String, String> hyperp = emb.getHyperp();
//		
//		m.add(embeddingsURI,
//				RDF.type,
//				m.createResource(MEX_CORE + "Dataset"));
//		m.add(embeddingsURI,
//				RDF.type,
//				m.createResource(VOID + "Dataset"));
//		m.add(embeddingsURI,
//				m.createProperty(DC_TERMS + "title"),
//				name);
//		m.add(method,
//				RDF.type,
//				m.createResource(MEX_ALGO + "Algorithm"));
//		m.add(embeddingsURI,
//				m.createProperty(PROV + "wasGeneratedBy"),
//				method);
//		for(String hyp : hyperp.keySet()) {
//			String val = hyperp.get(hyp);
//			Resource hypRes = m.createResource("http://w3id.org/resource2vec/hyperp/"
//						+ DigestUtils.sha1Hex(embeddingsURI + "/" + method + "#" + hyp + "=" + val));
//			m.add(hypRes,
//					RDF.type,
//					m.createResource(MEX_CORE + "HyperParameter"));
//			m.add(hypRes,
//					m.createProperty(DC_TERMS + "identifier"),
//					hyp);
//			m.add(hypRes,
//					m.createProperty(PROV + "value"),
//					val);
//			m.add(method,
//					m.createProperty(MEX_ALGO + "hasParameterCollection"),
//					hypRes);
//		}
//		StringWriter sw = new StringWriter();
//		RDFDataMgr.write(sw, m, Lang.JSONLD);
//		return sw.toString();
		
		return "<html><meta http-equiv='refresh' content='2;url="+emb.getEmbeddings()
				+"'><body>Redirecting to OpenML dataset page...</body></html>"; 
		
	}


}
