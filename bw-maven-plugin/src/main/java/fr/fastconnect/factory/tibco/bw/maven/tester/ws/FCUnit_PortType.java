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
/**
 * FCUnit_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.fastconnect.factory.tibco.bw.maven.tester.ws;

import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.Settings;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.TestSuiteResult;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.TestSuiteType;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.holders.EmptyHolder;

public interface FCUnit_PortType extends java.rmi.Remote {
    public TestSuiteResult[] runAllTests(Settings settings) throws java.rmi.RemoteException;
    public void isStarted(EmptyHolder _null) throws java.rmi.RemoteException;
    public TestSuiteType[] getTestSuites(Settings settings) throws java.rmi.RemoteException;
    public void stopEngine(EmptyHolder _null) throws java.rmi.RemoteException;
}
