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

import org.w3c.dom.Node;

public class UniqueTestProcess extends AbstractProcess {

	private final static String callProcessToTestXML = "/pd:ProcessDefinition/pd:activity[@name='callProcessToTest']/config/processNameXPath";
	private final static String callProcessToTestClassic = "/pd:ProcessDefinition/pd:activity/config/processName";
	
	public UniqueTestProcess(String path) {
		super(path);
	}

	private String getProcessTested() {
		String result = null;
		
		Node node = getNodeFromXPath(callProcessToTestXML);
		if (node != null) {
			result = node.getTextContent();
			result = result.substring(1, result.length()-1);
		}
		else {
			node = getNodeFromXPath(callProcessToTestClassic);
			if (node != null) {
				result = node.getTextContent();
			}
		}
			
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean addProcessToList(HashMap<String, ? extends AbstractProcess> processes) {
		TestProcess testProcess = new TestProcess(getPath());
		if (!testProcess.addProcessToList(processes)) {
			return false; // le process n'est pas un process de test
		}
		
		if (this.getProcessTested() == null) {
			return false; // le process de test ne teste pas réellement un process
		}
		
		for (UniqueTestProcess process : ((HashMap<String, UniqueTestProcess>) processes).values()) {			
			if (process.getProcessTested().equals(this.getProcessTested())) {
				return false;
			}
		}
				
		return true;
	}

}
