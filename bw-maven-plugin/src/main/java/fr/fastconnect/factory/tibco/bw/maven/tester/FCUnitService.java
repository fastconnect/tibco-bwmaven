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
package fr.fastconnect.factory.tibco.bw.maven.tester;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import javax.xml.ws.BindingProvider;

import com.sun.xml.ws.client.ClientTransportException;

import fr.fastconnect.factory.tibco.bw.maven.bwengine.BWService;
import fr.fastconnect.factory.tibco.bw.maven.bwengine.ServiceAgentInEngine;
import fr.fastconnect.factory.tibco.bw.maven.jaxws.FCUnit;
import fr.fastconnect.factory.tibco.bw.maven.jaxws.FCUnit_Service;
import fr.fastconnect.factory.tibco.bw.maven.jaxws.Settings;

/**
 * <p>
 * FCUnit Service Agent representation.
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
class FCUnit_Proxy implements BWService {

	private FCUnit fcUnit;

	public FCUnit_Proxy(FCUnit fcUnit) {
		this.fcUnit = fcUnit;
	}

	@Override
	public void isStarted() {
		fcUnit.isStarted();
	}

	@Override
	public void stopEngine() {
		fcUnit.stopEngine();
	}

	public void runAllTests(Settings settings) {
		fcUnit.runAllTests(settings);
	}
}

public class FCUnitService extends ServiceAgentInEngine<FCUnit_Proxy> {

	private FCUnit fcUnit;
	private FCUnit_Proxy fcUnitProxy;

	public FCUnitService(String bwEnginePort) throws ServiceException {
		super();

		URL wsdlLocation = FCUnitService.class.getResource("/FCUnit-concrete.wsdl");
		FCUnit_Service fcUnitService = new FCUnit_Service(wsdlLocation, new QName("http://fastconnect.fr/fcunit-concrete", "FCUnit"));

		fcUnit = fcUnitService.getPort(FCUnit.class);
		fcUnitProxy = new FCUnit_Proxy(fcUnit);

		BindingProvider bindingProvider = (BindingProvider) fcUnit;
		bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:" + bwEnginePort + "/FCUnit");
	}

	@Override
	public FCUnit_Proxy getService() {
		return fcUnitProxy;
	}

	public boolean runAllTests(Settings settings) {
		FCUnit_Proxy service = getService();

		if (service == null) {
			return false;
		}

		try {
			service.runAllTests(settings);
		} catch (ClientTransportException e) {
			return false; // the isStarted method in the WebService is used to ping
		}

		return true;
	}

}
