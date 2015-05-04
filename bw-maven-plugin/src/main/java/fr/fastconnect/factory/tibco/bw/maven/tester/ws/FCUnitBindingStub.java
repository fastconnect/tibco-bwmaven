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
 * FCUnitBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.fastconnect.factory.tibco.bw.maven.tester.ws;

import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.Anydata;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.Empty;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.Fixture;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.Settings;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.TestCaseResult;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.TestCaseType;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.TestFailure;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.TestFailureErrorReport;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.TestResult;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.TestSuccess;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.TestSuccessDuration;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.TestSuiteResult;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.TestSuiteType;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.TestType;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.TestWithFixture;
import fr.fastconnect.factory.tibco.bw.maven.tester.ws.xsd.holders.EmptyHolder;

@SuppressWarnings("rawtypes")
public class FCUnitBindingStub extends org.apache.axis.client.Stub implements
		FCUnit_PortType {
	private java.util.Vector cachedSerClasses = new java.util.Vector();
	private java.util.Vector cachedSerQNames = new java.util.Vector();
	private java.util.Vector cachedSerFactories = new java.util.Vector();
	private java.util.Vector cachedDeserFactories = new java.util.Vector();

	static org.apache.axis.description.OperationDesc[] _operations;

	static {
		_operations = new org.apache.axis.description.OperationDesc[4];
		_initOperationDesc1();
	}

	private static void _initOperationDesc1() {
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("runAllTests");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName(
						"http://fastconnect.fr/fcunit.xsd", "settings"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(
						"http://fastconnect.fr/fcunit.xsd", ">settings"),
				Settings.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", ">test-suites-results"));
		oper.setReturnClass(TestSuiteResult[].class);
		oper.setReturnQName(new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", "test-suites-results"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[0] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("isStarted");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName(
						"http://fastconnect.fr/fcunit.xsd", "empty"),
				org.apache.axis.description.ParameterDesc.INOUT,
				new javax.xml.namespace.QName(
						"http://fastconnect.fr/fcunit.xsd", ">empty"),
				Empty.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[1] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("getTestSuites");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName(
						"http://fastconnect.fr/fcunit.xsd", "settings"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(
						"http://fastconnect.fr/fcunit.xsd", ">settings"),
				Settings.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", "test-suite-collection"));
		oper.setReturnClass(TestSuiteType[].class);
		oper.setReturnQName(new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", "test-suites"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[2] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("stopEngine");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName(
						"http://fastconnect.fr/fcunit.xsd", "empty"),
				org.apache.axis.description.ParameterDesc.INOUT,
				new javax.xml.namespace.QName(
						"http://fastconnect.fr/fcunit.xsd", ">empty"),
				Empty.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[3] = oper;

	}

	public FCUnitBindingStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public FCUnitBindingStub(java.net.URL endpointURL,
			javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public FCUnitBindingStub(javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault {
		if (service == null) {
			super.service = new org.apache.axis.client.Service();
		} else {
			super.service = service;
		}
		((org.apache.axis.client.Service) super.service)
				.setTypeMappingVersion("1.2");
		java.lang.Class cls;
		javax.xml.namespace.QName qName;
		javax.xml.namespace.QName qName2;
		java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
		java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
		java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
		java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
		java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
		java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
		java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
		java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
		java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
		java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd",
				">>>test-failure>error-report>ProcessStack");
		cachedSerQNames.add(qName);
		cls = java.lang.String[].class;
		cachedSerClasses.add(cls);
		qName = new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string");
		qName2 = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", "ProcessName");
		cachedSerFactories
				.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(
						qName, qName2));
		cachedDeserFactories
				.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd",
				">>test-failure>error-report");
		cachedSerQNames.add(qName);
		cls = TestFailureErrorReport.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", ">>test-success>duration");
		cachedSerQNames.add(qName);
		cls = TestSuccessDuration.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", ">empty");
		cachedSerQNames.add(qName);
		cls = Empty.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", ">fixture");
		cachedSerQNames.add(qName);
		cls = Fixture.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		/*
		 * qName = new
		 * javax.xml.namespace.QName("http://fastconnect.fr/fcunit.xsd",
		 * ">license-content"); cachedSerQNames.add(qName); cls =
		 * LicenseContent.class; cachedSerClasses.add(cls);
		 * cachedSerFactories.add(beansf); cachedDeserFactories.add(beandf);
		 */

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", ">settings");
		cachedSerQNames.add(qName);
		cls = Settings.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", ">test-case-result");
		cachedSerQNames.add(qName);
		cls = TestCaseResult.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", ">test-failure");
		cachedSerQNames.add(qName);
		cls = TestFailure.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", ">test-result");
		cachedSerQNames.add(qName);
		cls = TestResult.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", ">test-success");
		cachedSerQNames.add(qName);
		cls = TestSuccess.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", ">test-suite-result");
		cachedSerQNames.add(qName);
		cls = TestSuiteResult.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", ">test-suites-results");
		cachedSerQNames.add(qName);
		cls = TestSuiteResult[].class;
		cachedSerClasses.add(cls);
		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", "test-suite-result");
		qName2 = null;
		cachedSerFactories
				.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(
						qName, qName2));
		cachedDeserFactories
				.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", ">test-with-fixture");
		cachedSerQNames.add(qName);
		cls = TestWithFixture.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", "anydata");
		cachedSerQNames.add(qName);
		cls = Anydata.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", "path");
		cachedSerQNames.add(qName);
		cls = org.apache.axis.types.URI.class;
		cachedSerClasses.add(cls);
		cachedSerFactories
				.add(org.apache.axis.encoding.ser.BaseSerializerFactory
						.createFactory(
								org.apache.axis.encoding.ser.SimpleSerializerFactory.class,
								cls, qName));
		cachedDeserFactories
				.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
						.createFactory(
								org.apache.axis.encoding.ser.SimpleDeserializerFactory.class,
								cls, qName));

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", "test-case-type");
		cachedSerQNames.add(qName);
		cls = TestCaseType.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", "test-suite-collection");
		cachedSerQNames.add(qName);
		cls = TestSuiteType[].class;
		cachedSerClasses.add(cls);
		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", "test-suite");
		qName2 = null;
		cachedSerFactories
				.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(
						qName, qName2));
		cachedDeserFactories
				.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", "test-suite-type");
		cachedSerQNames.add(qName);
		cls = TestSuiteType.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(
				"http://fastconnect.fr/fcunit.xsd", "test-type");
		cachedSerQNames.add(qName);
		cls = TestType.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

	}

	protected org.apache.axis.client.Call createCall()
			throws java.rmi.RemoteException {
		try {
			org.apache.axis.client.Call _call = super._createCall();
			if (super.maintainSessionSet) {
				_call.setMaintainSession(super.maintainSession);
			}
			if (super.cachedUsername != null) {
				_call.setUsername(super.cachedUsername);
			}
			if (super.cachedPassword != null) {
				_call.setPassword(super.cachedPassword);
			}
			if (super.cachedEndpoint != null) {
				_call.setTargetEndpointAddress(super.cachedEndpoint);
			}
			if (super.cachedTimeout != null) {
				_call.setTimeout(super.cachedTimeout);
			}
			if (super.cachedPortName != null) {
				_call.setPortName(super.cachedPortName);
			}
			java.util.Enumeration keys = super.cachedProperties.keys();
			while (keys.hasMoreElements()) {
				java.lang.String key = (java.lang.String) keys.nextElement();
				_call.setProperty(key, super.cachedProperties.get(key));
			}
			// All the type mapping information is registered
			// when the first call is made.
			// The type mapping information is actually registered in
			// the TypeMappingRegistry of the service, which
			// is the reason why registration is only needed for the first call.
			synchronized (this) {
				if (firstCall()) {
					// must set encoding style before registering serializers
					_call.setEncodingStyle(null);
					for (int i = 0; i < cachedSerFactories.size(); ++i) {
						java.lang.Class cls = (java.lang.Class) cachedSerClasses
								.get(i);
						javax.xml.namespace.QName qName = (javax.xml.namespace.QName) cachedSerQNames
								.get(i);
						java.lang.Object x = cachedSerFactories.get(i);
						if (x instanceof Class) {
							java.lang.Class sf = (java.lang.Class) cachedSerFactories
									.get(i);
							java.lang.Class df = (java.lang.Class) cachedDeserFactories
									.get(i);
							_call.registerTypeMapping(cls, qName, sf, df, false);
						} else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
							org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory) cachedSerFactories
									.get(i);
							org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory) cachedDeserFactories
									.get(i);
							_call.registerTypeMapping(cls, qName, sf, df, false);
						}
					}
				}
			}
			return _call;
		} catch (java.lang.Throwable _t) {
			throw new org.apache.axis.AxisFault(
					"Failure trying to get the Call object", _t);
		}
	}

	public TestSuiteResult[] runAllTests(Settings settings)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("/FCUnit/runAllTests");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
				Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
				Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "runAllTests"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[] { settings });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (TestSuiteResult[]) _resp;
				} catch (java.lang.Exception _exception) {
					return (TestSuiteResult[]) org.apache.axis.utils.JavaUtils
							.convert(_resp, TestSuiteResult[].class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void isStarted(EmptyHolder _null) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[1]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("/FCUnit/isStarted");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
				Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
				Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "isStarted"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[] { _null.value });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				java.util.Map _output;
				_output = _call.getOutputParams();
				try {
					_null.value = (Empty) _output
							.get(new javax.xml.namespace.QName(
									"http://fastconnect.fr/fcunit.xsd", "empty"));
				} catch (java.lang.Exception _exception) {
					_null.value = (Empty) org.apache.axis.utils.JavaUtils
							.convert(_output
									.get(new javax.xml.namespace.QName(
											"http://fastconnect.fr/fcunit.xsd",
											"empty")), Empty.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public TestSuiteType[] getTestSuites(Settings settings)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[2]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("/FCUnit/getTestSuites");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
				Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
				Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("",
				"getTestSuites"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[] { settings });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (TestSuiteType[]) _resp;
				} catch (java.lang.Exception _exception) {
					return (TestSuiteType[]) org.apache.axis.utils.JavaUtils
							.convert(_resp, TestSuiteType[].class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}
	
	public void stopEngine(EmptyHolder _null) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[3]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("/FCUnit/stopEngine");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
				Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
				Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "stopEngine"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[] { _null.value });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				java.util.Map _output;
				_output = _call.getOutputParams();
				try {
					_null.value = (Empty) _output
							.get(new javax.xml.namespace.QName(
									"http://fastconnect.fr/fcunit.xsd", "empty"));
				} catch (java.lang.Exception _exception) {
					_null.value = (Empty) org.apache.axis.utils.JavaUtils
							.convert(_output
									.get(new javax.xml.namespace.QName(
											"http://fastconnect.fr/fcunit.xsd",
											"empty")), Empty.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

}
