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
package fr.fastconnect.factory.tibco.bw.maven.hawk;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

import COM.TIBCO.hawk.console.hawkeye.AgentManager;
import COM.TIBCO.hawk.console.hawkeye.ConsoleInitializationException;
import COM.TIBCO.hawk.talon.DataElement;
import COM.TIBCO.hawk.talon.MicroAgentException;
import COM.TIBCO.hawk.talon.MicroAgentID;

public abstract class AbstractMethodAction {
	private MicroAgent microAgent;

    private String methodName;
    private List<DataElement> arguments;

	public AbstractMethodAction(MicroAgent microAgent) {
		this.microAgent = microAgent;
	}

	public AbstractMethodAction(String hawkDomain, String rvService, String rvNetwork, String rvDaemon) throws ConsoleInitializationException {
		this(new MicroAgent(hawkDomain, rvService, rvNetwork, rvDaemon));
	}

	public AbstractMethodAction(String hawkDomain, String rvService, String rvNetwork, String rvDaemon, String microAgentName) throws ConsoleInitializationException, MicroAgentException {
		this(new MicroAgent(hawkDomain, rvService, rvNetwork, rvDaemon, microAgentName));
	}

	public void setMicroAgent(MicroAgent microAgent) {
		this.microAgent = microAgent;
	}

	public AgentManager getAgentManager() {
		return this.microAgent.getAgentManager();
	}

    public MicroAgentID getMicroAgentID() {
		return this.microAgent.getMicroAgentID();
	}

	public void setArguments(String... arguments) {
		List<ImmutablePair<String, String>> _arguments = new ArrayList<ImmutablePair<String, String>>();
		
		Integer i = 0;
		for (String argument : arguments) {
			i++;
			_arguments.add(new ImmutablePair<String, String>("arg" + i.toString(), argument));
		}

		this.setArguments(_arguments);
	}

	public List<DataElement> getArguments() {
		return arguments;
	}

	public void setArguments(List<ImmutablePair<String, String>> arguments) {
		if (arguments == null) {
			return;
		}

		this.arguments = new ArrayList<DataElement>();
        
		for (ImmutablePair<String, String> pair : arguments) {
			String key = pair.left;
			String value = pair.right;
	        
	        this.arguments.add(new DataElement(key, value));
		}

		setMethodName(this.getMethodName(), this.arguments);
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
    	this.methodName = methodName;
	}

	public abstract void setMethodName(String methodName,  List<DataElement> arguments);

}
