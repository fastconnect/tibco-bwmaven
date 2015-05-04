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

import COM.TIBCO.hawk.console.hawkeye.AgentManager;
import COM.TIBCO.hawk.console.hawkeye.ConsoleInitializationException;
import COM.TIBCO.hawk.console.hawkeye.TIBHawkConsole;

/**
 * <p>
 * This class wraps Hawk Console and Hawk Agent Manager objects provided by the
 * TIBCO Hawk API.
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
public class Hawk {
	private TIBHawkConsole hawkConsole;
	private AgentManager hawkAgentManager;

	public Hawk(String hawkDomain, String rvService, String rvNetwork, String rvDaemon) throws ConsoleInitializationException {
		this.hawkConsole = new TIBHawkConsole(hawkDomain, rvService, rvNetwork,	rvDaemon);
		this.hawkAgentManager = this.hawkConsole.getAgentManager();

		this.hawkAgentManager.initialize();
	}
	
	public AgentManager getAgentManager() {
		return this.hawkAgentManager;
	}

}
