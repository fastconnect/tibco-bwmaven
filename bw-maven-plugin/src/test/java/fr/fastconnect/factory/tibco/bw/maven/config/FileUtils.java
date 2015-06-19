/**
 * (C) Copyright 2011-2015 FastConnect SAS
 * (http://www.fastconnect.fr/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.fastconnect.factory.tibco.bw.maven.config;

// FIXED : renamed this class from TestUtils from FileUtils to avoid Surefire messing around
// cf. http://jira.codehaus.org/browse/SUREFIRE-482

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;


public class FileUtils {

	/**
	 * Read the content of parameter file and return it in a String.
	 * 
	 * @param filePath
	 *            the path of the file
	 * @return the String content of the file
	 * @throws IOException
	 */
	public static String getStringFromFile(String filePath) throws IOException {
		StringBuilder result = new StringBuilder();
		BufferedReader br = null;
		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(filePath));

			while ((sCurrentLine = br.readLine()) != null) {
				result.append(sCurrentLine).append("\n");
			}

		} finally {
			if (br != null)
				br.close();
		}
		return result.toString();
	}

	public static Object decodeFromFile(String filePath) throws FileNotFoundException {
		Object result = null;
		// ouverture de decodeur
		XMLDecoder decoder = new XMLDecoder(new FileInputStream(filePath));
		try {
			// deserialisation de l'objet
			result = decoder.readObject();

		} finally {
			// fermeture du decodeur
			decoder.close();
		}
		return result;
	}
	
	public static void encodeToFile(Object serializableObject, String filePath) throws FileNotFoundException{
		 XMLEncoder encoder = new XMLEncoder(new FileOutputStream(filePath));
	        try {
	            // serialisation de l'objet
	            encoder.writeObject(serializableObject);
	            encoder.flush();
	        } finally {
	            // fermeture de l'encodeur
	            encoder.close();
	        }
	}

}
