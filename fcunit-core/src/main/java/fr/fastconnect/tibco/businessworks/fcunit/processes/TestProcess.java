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
package fr.fastconnect.tibco.businessworks.fcunit.processes;

import java.util.HashMap;

import fr.fastconnect.tibco.businessworks.fcunit.Helpers;

public class TestProcess extends AbstractProcess {

	public TestProcess(String path) {
		super(path);
	}
	
	/**
	 * On vérifie que le process est un test, c'est-à-dire qu'il est dans un dossier
	 * finissant par TestCase qui est lui-même dans un dossier finissant par TestSuite.
	 * 
	 * @return true si le process est un process de test, false sinon.
	 */
	public boolean addProcessToList(HashMap<String, ? extends AbstractProcess> processes) {
		String process = getPath();
		
		int lastSlash = process.lastIndexOf('/');
		if (lastSlash == -1)
			return false;
		int secondLastSlash = process.lastIndexOf('/', lastSlash - 1);
		if (secondLastSlash == -1)
			return false;
		int thirdLastSlash = process.lastIndexOf('/', secondLastSlash - 1);
		if (thirdLastSlash == -1)
			thirdLastSlash = 0;
		
		return (Helpers.matchSuite(process.substring(thirdLastSlash + 1, secondLastSlash)) &&
			    Helpers.matchCase(process.substring(secondLastSlash + 1, lastSlash)));
	}

}
