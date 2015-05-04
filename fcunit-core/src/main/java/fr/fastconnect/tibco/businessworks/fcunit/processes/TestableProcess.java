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

public class TestableProcess extends AbstractProcess {

	public TestableProcess(String path) {
		super(path);
	}

	/**
	 * 
	 * @return true si le process dont le chemin est "path" commence par un starter (JMS, RV...), false sinon
	 */
	private boolean processHasAStarter() {
		String expression = "/pd:ProcessDefinition/pd:starter";
		return getNodeFromXPath(expression) != null;
	}

	/**
	 * Un process testable ne doit pas être un process de test ni avoir un starter (JMS, RV, ...).
	 */
	public boolean addProcessToList(HashMap<String, ? extends AbstractProcess> processes) {
		TestProcess testProcess = new TestProcess(getPath());
		return (!testProcess.addProcessToList(processes) &&
				!processHasAStarter());
	}

}
