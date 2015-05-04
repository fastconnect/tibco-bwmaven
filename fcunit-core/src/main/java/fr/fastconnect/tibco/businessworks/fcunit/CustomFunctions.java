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
package fr.fastconnect.tibco.businessworks.fcunit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.tibco.bw.store.RepoAgent;
import com.tibco.pe.core.Engine;

/**
 * Quelques méthodes statiques utilisées par FCUnit.
 * 
 * @author Mathieu Debove
 *
 */
public class CustomFunctions {
	private static Map<String, String> errorCodes = initErrorCodes();

	private static String assertPrefix = "FCUNIT-ASSERT-";

	/**
	 * 
	 * @param assertProcess
	 * @return un code d'erreur en fonction du type de test utilisé
	 * 
	 * @see initErrorCodes
	 */
	public static String assertErrorCode(String assertProcess) {
		String rval = (String) errorCodes.get(assertProcess);
		return assertPrefix + ((rval == null) ? "9999" : rval);
	}

	/**
	 * 
	 * @param msg
	 * @param assertProcess
	 * 
	 * @return Le message d'erreur qui sera affiché dans les résultats du test
	 */
	public static String assertMsg(String msg, String assertProcess) {
		if ((msg != null) && (msg.length() != 0)) {
			return msg;
		}

		if (assertProcess == null) {
			return "Unknown assert failed";
		}
		
		String name = assertProcess.replaceAll("Core/Asserts/(.*)\\.process", "$1");
		return name + " failed";
	}

	/**
	 * Correspondance entre les types de test utilisés et un code d'erreur
	 * à 4 chiffres.
	 * 
	 * @return la liste des codes d'erreur
	 */
	private static Map<String, String> initErrorCodes() {
		final Map<String, String> errorCodes = new HashMap<String, String>();
		
		errorCodes.put("Core/Asserts/Fail.process", "0001");
		errorCodes.put("Core/Asserts/AssertTrue.process", "0002");
		errorCodes.put("Core/Asserts/AssertFalse.process", "0003");
		errorCodes.put("Core/Asserts/AssertEqual.process", "0004");
		errorCodes.put("Core/Asserts/AssertEqualDouble.process", "0005");
		
		errorCodes.put("Core/Asserts/AssertEqualXML.process", "1001");
		errorCodes.put("Core/Asserts/AssertAtLeastEqualXML.process", "1002");
		
		return errorCodes;
	}

	/**
	 * @deprecated
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isXMLTest(String path) {
		RepoAgent ra = Engine.getRepoAgent();
		String uri = ra.getAbsoluteURIFromProjectRelativeURI("");
		// le test est considéré "XML" s'il y a un fichier input-data.xml dans
		// le même répertoire.
		
		return (new File(uri + path + "/input-data.xml")).exists();
		// TODO : faire ça plus proprement...
	}

}
