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
package fr.fastconnect.factory.tibco.bw.maven.bwengine;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.fault.ServerSOAPFaultException;


/**
 * <p>
 * This abstract class defines an interface for a Service Agent (= Web Service)
 * inside a BusinessWorks project.
 * 
 * Once the bwengine containing the Service Agent is launched, it is possible to
 * request methods of the Service.
 * 
 * The minimal methods found in every Service Agent are:
 * <ul>
 *   <li>boolean isStarted() : to ping the Service</li>
 *   <li>void stopEngine() : to stop the engine running the Service</li>
 * </ul>
 * </p>
 * 
 * @author Mathieu Debove
 *
 * @param <Service>
 * @param <PortType>
 */
public abstract class ServiceAgentInEngine<Service extends BWService> {
 
	public ServiceAgentInEngine() {
		Logger.getLogger("com.sun.xml.internal.ws.wsdl.parser").setLevel(Level.SEVERE);
		Logger.getLogger("com.sun.xml.ws.api.streaming").setLevel(Level.SEVERE);
		Logger.getLogger("com.sun.xml.ws.wsdl").setLevel(Level.SEVERE);

	}

	public abstract Service getService();

	public boolean isStarted() {
		Service service = getService();

		if (service == null) {
			return false;
		}

		try {
			service.isStarted();
		} catch (ClientTransportException e) {
			return false; // the isStarted method in the WebService is used to ping
		}

		return true;
	}

	public void stopEngine() {
		Service service = getService();

		if (service == null) {
			return;
		}

		try {
			service.stopEngine();
		} catch (ClientTransportException e) {
			// nothing
		} catch (ServerSOAPFaultException e) {
			// nothing
		}
	}

}
