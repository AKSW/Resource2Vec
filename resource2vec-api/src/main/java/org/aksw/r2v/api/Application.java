package org.aksw.r2v.api;


import java.io.File;

import org.aksw.r2v.api.utils.Bundle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
@SpringBootApplication
public class Application {
	
	public static final String TMP_PATH = "/tmp/resource2vec/";
	public static final String TMP_DATASETS_PATH = TMP_PATH + "datasets/";

	public static String PYTHON_PATH;
	public static String OPENML_API_KEY;

	
	static {
		Bundle.setBundleName("resource2vec");
		
		new File(TMP_DATASETS_PATH).mkdirs();
		
		OPENML_API_KEY = Bundle.getString("openml_api_key");
		PYTHON_PATH = Bundle.getString("python_path");
	}

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
}