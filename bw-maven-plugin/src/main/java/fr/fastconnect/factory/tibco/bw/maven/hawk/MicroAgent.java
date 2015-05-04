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
import java.util.Arrays;
import java.util.List;

import COM.TIBCO.hawk.console.hawkeye.AgentManager;
import COM.TIBCO.hawk.console.hawkeye.ConsoleInitializationException;
import COM.TIBCO.hawk.talon.MicroAgentDescriptor;
import COM.TIBCO.hawk.talon.MicroAgentException;
import COM.TIBCO.hawk.talon.MicroAgentID;

/**
 * <p>
 * This class wraps a Micro Agent object provided by the TIBCO Hawk API.
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
public class MicroAgent {
	private Hawk hawk;

	private List<MicroAgentID> microAgentIDs;
	private String microAgentName;

	public MicroAgent(Hawk hawk) {
		this.hawk = hawk;
		this.microAgentIDs = new ArrayList<MicroAgentID>();
		this.microAgentName = null;	
	}

	public MicroAgent(String hawkDomain, String rvService, String rvNetwork, String rvDaemon) throws ConsoleInitializationException {
		this(new Hawk(hawkDomain, rvService, rvNetwork, rvDaemon));
	}

	public MicroAgent(String hawkDomain, String rvService, String rvNetwork, String rvDaemon, String microAgentName) throws ConsoleInitializationException, MicroAgentException {
		this(new Hawk(hawkDomain, rvService, rvNetwork, rvDaemon));
		setMicroAgentName(microAgentName);
	}

	public void setMicroAgentName(String microAgentName) throws MicroAgentException {
		this.microAgentName = microAgentName;
		initMicroAgent();
	}

	private void initMicroAgent() throws MicroAgentException {
		microAgentIDs = Arrays.asList(this.hawk.getAgentManager().getMicroAgentIDs(microAgentName, 1)); // TODO: add hostname parameter
		if (microAgentIDs.isEmpty()) {
			throw new MicroAgentException("No Hawk Micro Agent found for this name: '" + microAgentName + "'");
		}
	}

	private List<MicroAgentID> getMicroAgentIDs() {
		return microAgentIDs;
	}

	public MicroAgentID getMicroAgentID() {
		return getMicroAgentIDs().get(0);
	}

	public AgentManager getAgentManager() {
		return this.hawk.getAgentManager();
	}

	public void showDescription() throws MicroAgentException {
		MicroAgentDescriptor mad = null;
		mad = getAgentManager().describe(getMicroAgentID());
		System.out.println(mad.toFormattedString());
	}

	public void shutdown() {
		this.getAgentManager().shutdown();
	}
}
