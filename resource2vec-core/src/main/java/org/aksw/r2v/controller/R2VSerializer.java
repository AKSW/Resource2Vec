package org.aksw.r2v.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.aksw.r2v.model.R2VModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class R2VSerializer {

	private final static Logger logger = LogManager
			.getLogger(R2VSerializer.class);

	public static boolean write(R2VModel model, String filename) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(filename));
			oos.writeObject(model);
			oos.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;
		}
		logger.info("Model written to {}.", filename);
		return true;
	}

	public static R2VModel read(String filename) {
		R2VModel model = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					filename));
			model = (R2VModel) ois.readObject();
			ois.close();
		} catch (ClassNotFoundException | IOException e) {
			logger.error(e.getMessage());
		}
		logger.info("Model loaded from {}.", filename);
		return model;
	}

}
