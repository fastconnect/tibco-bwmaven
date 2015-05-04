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

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import fr.fastconnect.factory.tibco.bw.maven.bwengine.ServiceAgentInEngine;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.FCUnit_PortType;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.FCUnit_Service;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.FCUnit_ServiceLocator;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.Empty;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.Settings;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.holders.EmptyHolder;

/**
 * <p>
 * FCUnit possède un Service Agent qui permet d'invoquer les processes
 * d'exécution des tests comme des méthodes d'un Web Service.<br/>
 * 
 * Cette classe définit les méthodes pour les différents appels possibles qui
 * sont ensuite délégués aux éléments du framework Axis (consommation de Web
 * Services), contenus dans le package
 * "fr.fastconnect.factory.tibco.bw.maven.tester.ws"
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
public class FCUnitService extends ServiceAgentInEngine<FCUnit_Service, FCUnit_PortType> {
	
	private String bwEnginePort;
	
	public FCUnitService(String bwEnginePort) throws ServiceException {
		service = new FCUnit_ServiceLocator();
		((FCUnit_ServiceLocator) service).setEndpointAddress("FCUnit", "http://localhost:"+bwEnginePort+"/FCUnit");
		port = service.getFCUnit();	
	}

	public boolean isStarted() {
		if (port == null) {
			return false;
		}
		
		EmptyHolder _null = new EmptyHolder(new Empty());

		try {
			port.isStarted(_null);
		} catch (RemoteException e) {
			return false; // the isStarted method in the WebService is used to ping
		}
		
		return true;
	}

	public boolean runAllTests(Settings settings) {
		if (port == null) {
			return false;
		}
		
		try {
			port.runAllTests(settings);
		} catch (RemoteException e) {
			e.printStackTrace(); // FIXME : remove printStackTrace()
			return false;
		}
		
		return true;
	}
	
	public void stopEngine() {
		if (port == null) {
			return;
		}
		
		EmptyHolder _null = new EmptyHolder(new Empty());

		try {
			port.stopEngine(_null);
		} catch (RemoteException e) {
			e.printStackTrace(); // FIXME : remove printStackTrace()
		}
	}

	public String getBWEnginePort() {
		return bwEnginePort;
	}

	public void setBWEnginePort(String bwEnginePort) {
		this.bwEnginePort = bwEnginePort;
	}

}
