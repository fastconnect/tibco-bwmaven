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
 * FCUnit_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.fastconnect.factory.tibco.bw.maven.tester.ws;

import javax.xml.namespace.QName;

public class FCUnit_ServiceLocator extends org.apache.axis.client.Service implements FCUnit_Service {
	private static final long serialVersionUID = 4982101582843219976L;

	public FCUnit_ServiceLocator() {
    }


    public FCUnit_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public FCUnit_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for FCUnit
    private java.lang.String serviceAddress = "http://localhost:9099/FCUnit"; // default

    public java.lang.String getFCUnitAddress() {
        return serviceAddress;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String FCUnitWSDDServiceName = "FCUnit";

    public java.lang.String getFCUnitWSDDServiceName() {
        return FCUnitWSDDServiceName;
    }

    public void setFCUnitWSDDServiceName(java.lang.String name) {
        FCUnitWSDDServiceName = name;
    }

    public FCUnit_PortType getFCUnit() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(serviceAddress);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getFCUnit(endpoint);
    }

    public FCUnit_PortType getFCUnit(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            FCUnitBindingStub _stub = new FCUnitBindingStub(portAddress, this);
            _stub.setPortName(getFCUnitWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(@SuppressWarnings("rawtypes") Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (FCUnit_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                FCUnitBindingStub _stub = new FCUnitBindingStub(new java.net.URL(serviceAddress), this);
                _stub.setPortName(getFCUnitWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, @SuppressWarnings("rawtypes") Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("FCUnit".equals(inputPortName)) {
            return getFCUnit();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://fastconnect.fr/fcunit-concrete", "FCUnit");
    }

    private java.util.HashSet<QName> ports = null;

    public java.util.Iterator<QName> getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet<QName>();
            ports.add(new javax.xml.namespace.QName("http://fastconnect.fr/fcunit-concrete", "FCUnit"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        serviceAddress = address;
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }


}
