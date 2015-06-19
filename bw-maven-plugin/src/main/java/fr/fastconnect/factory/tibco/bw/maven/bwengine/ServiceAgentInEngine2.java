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
public abstract class ServiceAgentInEngine2<Service extends javax.xml.ws.Service, PortType extends java.rmi.Remote> {
 
	protected Service service;
	protected PortType port;
	
	public abstract boolean isStarted();
	public abstract void stopEngine();
	
}
