package org.aksw.r2v.api;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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

	public static String OPENML_API_KEY;

	
	static {
		new File(TMP_DATASETS_PATH).mkdirs();
		
		try {
			Scanner in = new Scanner(new File("src/main/java/openml_api_key"));
			OPENML_API_KEY = in.nextLine();
			in.close();
		} catch (FileNotFoundException e) {}
		
	}

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
}