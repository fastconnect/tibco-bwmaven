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
package fr.fastconnect.factory.tibco.bw.maven.packaging;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.commons.io.FilenameUtils;

import com.tibco.xmlns.applicationmanagement.ActionType;
import com.tibco.xmlns.applicationmanagement.Actions;
import com.tibco.xmlns.applicationmanagement.Adapter;
import com.tibco.xmlns.applicationmanagement.AlertAction;
import com.tibco.xmlns.applicationmanagement.ApplicationType;
import com.tibco.xmlns.applicationmanagement.Binding;
import com.tibco.xmlns.applicationmanagement.Bindings;
import com.tibco.xmlns.applicationmanagement.Bw;
import com.tibco.xmlns.applicationmanagement.Bwprocess;
import com.tibco.xmlns.applicationmanagement.Bwprocesses;
import com.tibco.xmlns.applicationmanagement.Checkpoints;
import com.tibco.xmlns.applicationmanagement.CustomAction;
import com.tibco.xmlns.applicationmanagement.EmailAction;
import com.tibco.xmlns.applicationmanagement.Events;
import com.tibco.xmlns.applicationmanagement.EventType;
import com.tibco.xmlns.applicationmanagement.FailureEvent;
import com.tibco.xmlns.applicationmanagement.FaultTolerant;
import com.tibco.xmlns.applicationmanagement.LogEvent;
import com.tibco.xmlns.applicationmanagement.Monitor;
import com.tibco.xmlns.applicationmanagement.NVPairType;
import com.tibco.xmlns.applicationmanagement.NVPairs;
import com.tibco.xmlns.applicationmanagement.NameValuePair;
import com.tibco.xmlns.applicationmanagement.Product;
import com.tibco.xmlns.applicationmanagement.RepoInstances;
import com.tibco.xmlns.applicationmanagement.ServiceType;
import com.tibco.xmlns.applicationmanagement.Setting;
import com.tibco.xmlns.applicationmanagement.Setting.Java;
import com.tibco.xmlns.applicationmanagement.Setting.NTService;
import com.tibco.xmlns.applicationmanagement.Shutdown;

/**
 * <p>
 * This class will<ul>
 * <li>unmarshall XML file of the schema with
 * "http://www.tibco.com/xmlns/ApplicationManagement" namespace to JAXB objects.
 * </li>
 * <li>
 * marshall the {@link ApplicationType} object back to XML file with the same
 * schema.
 * </li>
 * </ul>
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
public class ApplicationManagement {
	private static final String APPLICATION_MANAGEMENT_NAMESPACE = "http://www.tibco.com/xmlns/ApplicationManagement";
	
	private ApplicationType application;
	private JAXBContext jaxbContext;
	private HashMap<String, Object> map;
	private Pattern patternElement;
	private File xmlFile;

	public ApplicationManagement(File xmlFile) throws JAXBException {
		this.map = new HashMap<String, Object>();
		this.patternElement = Pattern.compile("(\\w+)(\\[([\\w- \\*\\.\\/?]*)\\])?");
		this.xmlFile = xmlFile;
		initApplication();
	}
	
	/**
	 * <p>
	 * This will initialize the {@link ApplicationType} object which is a JAXB
	 * representation of the "application" root-element of TIBCO XML Deployment
	 * Descriptor files using the schema with
	 * "http://www.tibco.com/xmlns/ApplicationManagement" namespace.
	 * </p>
	 *
	 * @throws JAXBException
	 */
	private void initApplication() throws JAXBException {
		jaxbContext = JAXBContext.newInstance(
				com.tibco.xmlns.applicationmanagement.ObjectFactory.class,
				com.tibco.xmlns.applicationmanagement.bw.ObjectFactory.class,
				com.tibco.xmlns.dd.ObjectFactory.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Object o =  jaxbUnmarshaller.unmarshal(xmlFile);
		try {
			this.application = (ApplicationType) o;
		} catch (ClassCastException e) {
			// strange workaround: sometimes we get an ApplicationType object,
			// sometimes a JAXBElement<ApplicationType> object
			
			@SuppressWarnings("unchecked")
			JAXBElement<ApplicationType> j = (JAXBElement<ApplicationType>) o;
			this.application = j.getValue();
		}
	}

	/**
	 * <p>
	 * This inner-class extends java.util.Properties with all properties sorted
	 * alphabetically. Also, the setProperty method is overridden to support
	 * multiple input types and check for null values.
	 * </p>
	 *
	 */
	public static class SortedProperties extends Properties {
		private static final long serialVersionUID = 3733070302160913988L;
		
		@Override
	    public synchronized Enumeration<Object> keys() {
	        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
	    }

		@Override
		public synchronized Object setProperty(String key, String value) {
			if (value != null) {
				return super.setProperty(key, value);
			}
			return null;
		}

		public synchronized Object setProperty(String key, BigInteger value) {
			if (value != null) {
				return super.setProperty(key, value.toString());
			}
			return null;
		}

		public synchronized Object setProperty(String key, Boolean value) {
			if (value != null) {
				return super.setProperty(key, value.toString());
			}
			return null;
		}

	}

	/**
	 * <p>
	 * This will marshall the object back to the XML file.
	 * </p>
	 * 
	 * @throws JAXBException
	 */
	public void save() throws JAXBException {
		Marshaller m = jaxbContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);		
		m.marshal(application, xmlFile);
	}

	// private helpers
	/**
	* This method retrieves the Global Variables at application level
	*/
	private NVPairs getGlobalVariablesPairs() {
		if (application.getNVPairs().getName().equals("Global Variables")) {
			return application.getNVPairs();
		} else {
			return null;
		}
	}
	
	/**
	* This method retrieves the list of objects of type "BWServiceType".
	*/
	private List<Bw> getBWServices() {
		List<Bw> result = new ArrayList<Bw>();

		for (JAXBElement<? extends ServiceType> jaxbElement : application.getServices().getBaseService()) {
			if (jaxbElement.getName().getLocalPart().equals("bw")) {
				result.add((Bw) jaxbElement.getValue());
			}
		}
		
		return result;
	}

	private List<Adapter> getAdapterServices() {
		List<Adapter> result = new ArrayList<Adapter>();

		for (JAXBElement<? extends ServiceType> jaxbElement : application.getServices().getBaseService()) {
			if (jaxbElement.getName().getLocalPart().equals("adapter")) {
				result.add((Adapter) jaxbElement.getValue());
			}
		}

		return result;
	}

	public List<String> getInstancesNames(boolean onlyEnabledServices) {
		List<String> result = new ArrayList<String>();
		
		List<Bw> bwServices = getBWServices();
		
		for (Bw bw : bwServices) {
			Bindings bindings = bw.getBindings();
			boolean enabled = bw.isEnabled();
			
			if (!enabled && onlyEnabledServices) {
				continue; // keep only enabled services
			}
			
			for (Binding binding : bindings.getBinding()) {
				String name = binding.getName();
				if (name == null || name.isEmpty()) {
					name = FilenameUtils.getBaseName(bw.getName());
				}
				result.add(name);
			}
		}
		
		return result;
	}

	/**
	* This method retrieves {@link Bwprocesses} object from Bw.getRest() objects
	*/
	private Bwprocesses getBWProcesses(Bw bwService) {
		for (Object o : bwService.getRest()) {
			if (o.getClass().equals(Bwprocesses.class)) {
				return (Bwprocesses) o;
			}
		}
		
		return null;
	}

	/**
	* This method retrieves {@link Checkpoints} object from Bw.getRest() objects
	*/
	private Checkpoints getBWCheckpoints(Bw bwService) {
		for (Object o : bwService.getRest()) {
			if (o.getClass().equals(Checkpoints.class)) {
				return (Checkpoints) o;
			}
		}
		
		return null;
	}

	/**
	* This method retrieves {@link FaultTolerant} object from Bw.getRest()
	* objects
	*/
	private FaultTolerant getBWFaultTolerant(Bw bwService) {
		for (Object o : bwService.getRest()) {
			if (o.getClass().equals(FaultTolerant.class)) {
				return (FaultTolerant) o;
			}
		}
		
		return null;
	}

	/**
	* This method retrieves "isFt" property from Bw.getRest() objects
	*/
	private Boolean isIsFt(Bw bw) {
		Boolean value = false; // default to false (?)
		for (Object o : bw.getRest()) {
			if (o.getClass().equals(JAXBElement.class)) {
				//  should never be other than Boolean according to
				// com.tibco.xmlns.applicationmanagement.BWServiceType
				@SuppressWarnings("unchecked")
				JAXBElement<Boolean> e = (JAXBElement<Boolean>) o;
				value = e.getValue();
			}
		}

		return value;
	}

	/**
	 * This method sets "isFt" property in Bw.getRest() objects
	 */
	private void setIsFt(Bw bw, Boolean value) {
		for (Object o : bw.getRest()) {
			if (o.getClass().equals(JAXBElement.class)) {
				//  should never be other than Boolean according to
				// com.tibco.xmlns.applicationmanagement.BWServiceType
				@SuppressWarnings("unchecked")
				JAXBElement<Boolean> e = (JAXBElement<Boolean>) o;
				e.setValue(value);
			}
		}
	}
	//

	// unmarshalling part / XML (JAXB) to properties
	/**
	 * <p>
	 * The Global Variables are inside the NVPairs element with @name attribute
	 * equals to "Global Variables" at the root level.
	 * </p>
	 * 
	 * @return The Global Variables of the xmlFile in a {@link SortedProperties}
	 * object.
	 */
	public Properties getGlobalVariables() {
		SortedProperties result = new SortedProperties();

		NVPairs globalVariablesPairs = getGlobalVariablesPairs();
		
		if (globalVariablesPairs != null) {
			for (JAXBElement<? extends NVPairType> nvPair : globalVariablesPairs.getNVPair()) {
				String key = nvPair.getValue().getName();
				String value = nvPair.getValue().getValue();
				result.setProperty(key, value);
			}
		}
		
		return result;
	}

	public void setGlobalVariable(String key, String value) {
		if (key == null) return;
		
		NVPairs globalVariablesPairs = getGlobalVariablesPairs();

		if (globalVariablesPairs != null) {
			for (JAXBElement<? extends NVPairType> nvPair : globalVariablesPairs.getNVPair()) {
				if (key.equals(nvPair.getValue().getName())) {
					nvPair.getValue().setValue(value);
				}
			}
		}

	}

	public Properties getServices() {
		SortedProperties result = new SortedProperties();
		
		result.putAll(getProcessArchives());
		result.putAll(getAdapterArchives());
		
		return result;
	}

	/**
	 * <p>
	 * This method focuses on all child elements of <bw name="par">. These 
	 * elements are defined in the "ServiceType" & "BWServiceType" complexTypes
	 * of the XSD schema.
	 * </p>
	 */
	protected Properties getProcessArchives() {
		SortedProperties result = new SortedProperties();

		List<Bw> bwServices = getBWServices();

		String key;
		
		for (Bw bwService : bwServices) {
			String serviceKey = "bw[" + bwService.getName() + "]";

			/// "ServiceType" complexType
			// enabled
			key = serviceKey + "/enabled";
			result.setProperty(key, bwService.isEnabled());

			// bindings
			result.putAll(getBindings(bwService));
			// NVPairs
			result.putAll(getNVPairs(bwService));

			// failureCount
			key = serviceKey + "/failureCount";
			result.setProperty(key, bwService.getFailureCount());

			// failureInterval
			key = serviceKey + "/failureInterval";
			result.setProperty(key, bwService.getFailureInterval());
			
			// monitor

			/// "BWServiceType" complexType
			// bwprocesses
			result.putAll(getProcesses(bwService));

			// checkpoints
			result.putAll(getCheckpoints(bwService));

			// isFt
			key = serviceKey + "/isFt";
			result.setProperty(key, isIsFt(bwService));
			
			// faultTolerant
			result.putAll(getFaultTolerant(bwService));
			
			// plugins
			// not supported (too complex)
		}
		
		return result;
	}

	/**
	 * <p>
	 * This method focuses on all child elements of <adapter name="aar">. These
	 * elements are defined in the "ServiceType" complexType of the XSD schema.
	 * </p>
	 */
	protected Properties getAdapterArchives() {
		SortedProperties result = new SortedProperties();

		List<Adapter> adapters = getAdapterServices();

		String key;

		for (Adapter adapter : adapters) {
			String serviceKey = "adapter[" + adapter.getName() + "]";

			/// "ServiceType" complexType
			// enabled
			key = serviceKey + "/enabled";
			result.setProperty(key, adapter.isEnabled());

			// bindings
			result.putAll(getBindings(adapter));
			// NVPairs
			result.putAll(getNVPairs(adapter));

			// failureCount
			key = serviceKey + "/failureCount";
			result.setProperty(key, adapter.getFailureCount());

			// failureInterval
			key = serviceKey + "/failureInterval";
			result.setProperty(key, adapter.getFailureInterval());

			// monitor

			// plugins
			// not supported (too complex)
		}
		
		return result;
	}

	private <T extends ServiceType> String getElementKey(T service) {
		String elementKey;
		if (service.getClass().getCanonicalName().contains("Adapter")) {
			elementKey = "adapter";
		} else { // WARN: could be another value but only support for "bw" and "adapter"
			elementKey = "bw";
		}

		return elementKey;
	}

	/**
	 * <p>
	 * This method focuses on bindings found in these paths :
	 * "/application/services/bw/bindings/binding"
	 * "/application/services/adapter/bindings/binding"
	 * </p>
	 */
	protected <T extends ServiceType> Properties getBindings(T service) {
		SortedProperties result = new SortedProperties();

		String serviceKey = getElementKey(service) + "[" + service.getName() + "]";

		Bindings bindings = service.getBindings();
		if (bindings != null) {			
			for (Binding binding : bindings.getBinding()) {
				String processKey = serviceKey + "/bindings/binding[" + binding.getName() + "]";
				String key;

				// machine
				key = processKey + "/machine";
				result.setProperty(key, binding.getMachine());

				// product
				if (binding.getProduct() != null) {
					key = processKey + "/product/type";
					result.setProperty(key, binding.getProduct().getType());
					key = processKey + "/product/version";
					result.setProperty(key, binding.getProduct().getVersion());
					key = processKey + "/product/location";
					result.setProperty(key, binding.getProduct().getLocation());
				}
				
				// container
				key = processKey + "/container";
				result.setProperty(key, binding.getContainer());
				
				// description
				key = processKey + "/description";
				result.setProperty(key, binding.getDescription());
				
				// contact
				key = processKey + "/contact";
				result.setProperty(key, binding.getContact());

				// setting
				Setting setting = binding.getSetting();
				if (setting != null) {
					key = processKey + "/setting/startOnBoot";
					result.setProperty(key,  setting.isStartOnBoot());
					key = processKey + "/setting/enableVerbose";
					result.setProperty(key, setting.isEnableVerbose());
					key = processKey + "/setting/maxLogFileSize";
					result.setProperty(key, setting.getMaxLogFileSize());
					key = processKey + "/setting/maxLogFileCount";
					result.setProperty(key, setting.getMaxLogFileCount());
					key = processKey + "/setting/threadCount";
					result.setProperty(key, setting.getThreadCount());
					NTService ntService = setting.getNTService();
					if (ntService != null) {
						key = processKey + "/setting/NTService/runAsNT";
						result.setProperty(key, ntService.isRunAsNT());
						key = processKey + "/setting/NTService/startupType";
						result.setProperty(key,  ntService.getStartupType());
						key = processKey + "/setting/NTService/loginAs";
						result.setProperty(key, ntService.getLoginAs());
						key = processKey + "/setting/NTService/password";
						result.setProperty(key, ntService.getPassword());
					}
					Java java = setting.getJava();
					if (java != null) {
						key = processKey + "/setting/java/prepandClassPath";
						result.setProperty(key, java.getPrepandClassPath());	
						key = processKey + "/setting/java/appendClassPath";
						result.setProperty(key, java.getAppendClassPath());	
						key = processKey + "/setting/java/initHeapSize";
						result.setProperty(key, java.getInitHeapSize());	
						key = processKey + "/setting/java/maxHeapSize";
						result.setProperty(key, java.getMaxHeapSize());	
						key = processKey + "/setting/java/threadStackSize";
						result.setProperty(key, java.getThreadStackSize());	
					}
				}

				// ftWeight
				key = processKey + "/ftWeight";
				result.setProperty(key, binding.getFtWeight());

				// shutdown
				Shutdown shutdown = binding.getShutdown();
				if (shutdown != null) {
					key = processKey + "/shutdown/checkpoint";
					result.setProperty(key, shutdown.isCheckpoint());
					key = processKey + "/shutdown/timeout";
					result.setProperty(key, shutdown.getTimeout());
				}
				
				// plugins
				// not supported (too complex)
				
				// NVPairs
				if (binding.getNVPairs() != null) {
					for (JAXBElement<? extends NVPairType> nvPair : binding.getNVPairs().getNVPair()) {
						key = processKey + "/variables/variable[" + nvPair.getValue().getName() + "]";
						result.setProperty(key, nvPair.getValue().getValue());
					}
				}
			}
		}
		
		return result;
	}

	/**
	 * <p>
	 * This method focuses on Global Variables found in this path :
	 * "/application/services/bw/NVPairs"
	 * "/application/services/adapter/NVPairs"
	 * </p>
	 */
	private <T extends ServiceType> Properties getNVPairs(T service) {
		SortedProperties result = new SortedProperties();

		String serviceKey = getElementKey(service) + "[" + service.getName() + "]";
		
		List<NVPairs> nvPairsList = new ArrayList<NVPairs>();
		nvPairsList = service.getNVPairs();
		
		String variablesKey;

		for (NVPairs nvPairs : nvPairsList) {
			variablesKey = serviceKey + "/variables[" + nvPairs.getName() + "]"; 
			String key;
			for (JAXBElement<? extends NVPairType> nvPair : nvPairs.getNVPair()) {
				key = variablesKey + "/variable[" + nvPair.getValue().getName() + "]";
				result.setProperty(key, nvPair.getValue().getValue());
			}			
		}

		return result;
	}

	/**
	 * <p>
	 * This method focuses on processes found in this path :
	 * "/application/services/bw/bwprocesses/bwprocess"
	 * </p>
	 */
	protected Properties getProcesses(Bw bwService) {
		SortedProperties result = new SortedProperties();

		String serviceKey = "bw[" + bwService.getName() + "]";
		
		Bwprocesses bwProcesses = getBWProcesses(bwService);
		if (bwProcesses != null) {			
			for (Bwprocess process : bwProcesses.getBwprocess()) {
				String processKey = serviceKey + "/bwprocesses/bwprocess[" + process.getName() + "]";
				String key;

				// starter
				key = processKey + "/starter";
				result.setProperty(key, process.getStarter());

				// enabled
				key = processKey + "/enabled";
				result.setProperty(key, process.isEnabled());

				// maxJob
				key = processKey + "/maxJob";
				result.setProperty(key, process.getMaxJob());

				// activation
				key = processKey + "/activation";
				result.setProperty(key, process.isActivation());

				// flowLimit
				key = processKey + "/flowLimit";
				result.setProperty(key, process.getFlowLimit());
			}
		}
		
		return result;
	}

	/**
	 * <p>
	 * This method focuses on checkpoints found in this path :
	 * "/application/services/bw/checkpoints/checkpoint"
	 * </p>
	 */
	protected Properties getCheckpoints(Bw bwService) {
		SortedProperties result = new SortedProperties();

		String serviceKey = "bw[" + bwService.getName() + "]";
		
		Checkpoints checkpoints = getBWCheckpoints(bwService);
		if (checkpoints != null) {
			String key;

			// tablePrefix
			key = serviceKey + "/checkpoints/tablePrefix";
			result.setProperty(key, checkpoints.getTablePrefix());

			for (String checkpoint : checkpoints.getCheckpoint()) {
				if (checkpoint == null) continue;
				
				// checkpoint
				key = serviceKey + "/checkpoints/checkpoint["+checkpoint+"]";
				result.setProperty(key, checkpoint.equals(checkpoints.getSelected()));
			}
		}
		
		return result;
	}

	/**
	 * <p>
	 * This method focuses on faultTolerant object found in this path :
	 * "/application/services/bw/faultTolerant"
	 * </p>
	 */
	protected Properties getFaultTolerant(Bw bwService) {
		SortedProperties result = new SortedProperties();
		
		String serviceKey = "bw[" + bwService.getName() + "]";
		
		FaultTolerant faultTolerant = getBWFaultTolerant(bwService);
		if (faultTolerant != null) {
			String key;
			// hbInterval
			key = serviceKey + "/faultTolerant/hbInterval";
			result.setProperty(key, faultTolerant.getHbInterval());
			
			// activationInterval
			key = serviceKey + "/faultTolerant/activationInterval";
			result.setProperty(key, faultTolerant.getActivationInterval());
			
			// preparationDelay
			key = serviceKey + "/faultTolerant/preparationDelay";
			result.setProperty(key, faultTolerant.getPreparationDelay());
		}
		
		return result;
	}
	// end of unmarshalling part
	
	// marshalling part / Properties to XML (JAXB)
	/**
	 * /application/services/bw
	 */
	private Bw getBw(String name) {
		List<Bw> services = getBWServices();
		
		if (services != null) {
			for (Bw service : services) {
				if (service.getName().equals(name)) {
					return service;
				}
			}
		}
		
		Bw result = new Bw();
		result.setName(name);
		services.add(result);
		
		QName qName = new QName(APPLICATION_MANAGEMENT_NAMESPACE, "bw");
		JAXBElement<Bw> j = new JAXBElement<Bw>(qName, Bw.class, result);
		application.getServices().getBaseService().add(j);
		
		return result;
	}

	/**
	 * /application/services/adapter
	 */
	private Adapter getAdapter(String name) {
		List<Adapter> services = getAdapterServices();

		if (services != null) {
			for (Adapter service : services) {
				if (service.getName().equals(name)) {
					return service;
				}
			}
		}

		Adapter result = new Adapter();
		result.setName(name);
		services.add(result);

		QName qName = new QName(APPLICATION_MANAGEMENT_NAMESPACE, "adapter");
		JAXBElement<Adapter> j = new JAXBElement<Adapter>(qName, Adapter.class, result);
		application.getServices().getBaseService().add(j);

		return result;
	}

	/**
	 * /application/services/bw/bindings/binding
	 * /application/services/adapter/bindings/binding
	 */
	private <T extends ServiceType> Binding getBinding(String nameAttribute, T parent) {
		Bindings bindings = parent.getBindings();
		
		if (bindings != null) {
			for (Binding binding : bindings.getBinding()) {
				if (binding.getName().equals(nameAttribute)) {
					return binding;
				}
			}
		} else {
			bindings = new Bindings();
			parent.setBindings(bindings);
		}
		
		// create new Binding
		Binding binding = new Binding();
		binding.setName(nameAttribute);
		Product p = new Product();
		p.setType("bwengine");
		p.setLocation("");
		p.setVersion("");
		binding.setProduct(p);
		binding.setDescription("");
		binding.setContact("");
		Shutdown s = new Shutdown();
		s.setCheckpoint(false);
		s.setTimeout(new BigInteger("0"));
		binding.setShutdown(s);
		
		bindings.getBinding().add(binding);
		
		return binding;
	}

	/**
	 * /application/services/bw/bindings/binding/*
	 */
	private Object addBindingParameter(Binding binding, String key, String value) {
		if ("machine".equals(key)) {
			binding.setMachine(value);
		} else if ("product".equals(key)) {
			if (binding.getProduct() == null) {
				binding.setProduct(new Product());
			}
			return binding.getProduct();				
		} else if ("container".equals(key)) {
			binding.setContainer(value);
		} else if ("description".equals(key)) {
			binding.setDescription(value);
		} else if ("contact".equals(key)) {
			binding.setContact(value);
		} else if ("setting".equals(key)) {
			if (binding.getSetting() == null) {
				binding.setSetting(new Setting());
			}
			return binding.getSetting();
		} else if ("ftWeight".equals(key)) {
			binding.setFtWeight(BigInteger.valueOf(Long.parseLong(value)));
		} else if ("shutdown".equals(key)) {
			if (binding.getShutdown() == null) {
				binding.setShutdown(new Shutdown());
			}
			return binding.getShutdown();
		}

		return binding;
	}

	/**
	 * /application/services/bw/bindings/binding/product/*
	 */
	private Object addProductParameter(Product product, String key, String value) {
		if ("type".equals(key)) {
			product.setType(value);
		} else if ("version".equals(key)) {
			product.setVersion(value);
		} else if ("location".equals(key)) {
			product.setLocation(value);
		}
		return product;
	}

	/**
	 * /application/services/bw/bindings/binding/setting/*
	 */
	private Object addSettingParameter(Setting setting, String key, String value) {
		if ("startOnBoot".equals(key)) {
			setting.setStartOnBoot(Boolean.parseBoolean(value));
		} else if ("enableVerbose".equals(key)) {
			setting.setEnableVerbose(Boolean.parseBoolean(value));
		} else if ("maxLogFileSize".equals(key)) {
			setting.setMaxLogFileSize(BigInteger.valueOf(Long.parseLong(value)));
		} else if ("maxLogFileCount".equals(key)) {
			setting.setMaxLogFileCount(BigInteger.valueOf(Long.parseLong(value)));
		} else if ("threadCount".equals(key)) {
			setting.setThreadCount(BigInteger.valueOf(Long.parseLong(value)));
		} else if ("NTService".equals(key)) {
			if (setting.getNTService() == null) {
				setting.setNTService(new NTService());
			}
			return setting.getNTService();
		} else if ("java".equals(key)) {
			if (setting.getJava() == null) {
				setting.setJava(new Java());
			}
			return setting.getJava();
		}
		return setting;
	}

	/**
	 * /application/services/bw/bindings/binding/setting/NTService/*
	 */
	private Object addNTServiceParameter(NTService ntService, String key, String value) {
		if ("runAsNT".equals(key)) {
			ntService.setRunAsNT(Boolean.parseBoolean(value));
		} else if ("startupType".equals(key)) {
			ntService.setStartupType(value);
		} else if ("loginAs".equals(key)) {
			ntService.setLoginAs(value);
		} else if ("password".equals(key)) {
			ntService.setPassword(value);
		}
		return ntService;
	}

	/**
	 * /application/services/bw/bindings/binding/setting/java/*
	 */
	private Object addJavaParameter(Java java, String key, String value) {
		if ("prepandClassPath".equals(key)) {
			java.setPrepandClassPath(value);
		} else if ("appendClassPath".equals(key)) {
			java.setAppendClassPath(value);
		} else if ("initHeapSize".equals(key)) {
			java.setInitHeapSize(BigInteger.valueOf(Long.parseLong(value)));
		} else if ("maxHeapSize".equals(key)) {
			java.setMaxHeapSize(BigInteger.valueOf(Long.parseLong(value)));
		} else if ("threadStackSize".equals(key)) {
			java.setThreadStackSize(BigInteger.valueOf(Long.parseLong(value)));
		}

		return java;
	}

	/**
	 * /application/services/bw/bindings/binding/shutdown/*
	 */
	private Object addShutdownParameter(Shutdown shutdown, String key, String value) {
		if ("checkpoint".equals(key)) {
			shutdown.setCheckpoint(Boolean.parseBoolean(value));
		} else if ("timeout".equals(key)) {
			shutdown.setTimeout(BigInteger.valueOf(Long.parseLong(value)));
		}
		
		return shutdown;
	}

	/**
	 * /application/services/bw/bwprocesses/bwprocess
	 */
	private Bwprocess getBWProcess(String nameAttribute, Bw parent) {
		Bwprocesses bwProcesses = getBWProcesses(parent);
		
		if (bwProcesses != null) {
			for (Bwprocess bwProcess : bwProcesses.getBwprocess()) {
				if (bwProcess.getName().equals(nameAttribute)) {
					return bwProcess;
				}
			}
		} else {
			bwProcesses = new Bwprocesses();
			parent.getRest().add(bwProcesses);
		}
		
		Bwprocess bwProcess = new Bwprocess();
		bwProcess.setName(nameAttribute);
		bwProcesses.getBwprocess().add(bwProcess);
		
		return bwProcess;
	}

	/**
	 * /application/services/bw/bwprocesses/bwprocess/*
	 */
	private void addBWProcessParameter(Bwprocess bwProcess, String key, String value) {
		if ("starter".equals(key)) {
			bwProcess.setStarter(value);
		} else if ("enabled".equals(key)) {
			bwProcess.setEnabled(Boolean.parseBoolean(value));
		} else if ("maxJob".equals(key)) {
			bwProcess.setMaxJob(BigInteger.valueOf(Long.parseLong(value)));
		} else if ("activation".equals(key)) {
			bwProcess.setActivation(Boolean.parseBoolean(value));
		} else if ("flowLimit".equals(key)) {
			bwProcess.setFlowLimit(BigInteger.valueOf(Long.parseLong(value)));
		}
	}

	/**
	 * /application/services/bw/faultTolerant/*
	 */
	private void addFaultTolerantParameter(FaultTolerant faultTolerant, String key, String value) {
		if ("activationInterval".equals(key)) {
			faultTolerant.setActivationInterval(BigInteger.valueOf(Long.parseLong(value)));
		} else if ("hbInterval".equals(key)) {
			faultTolerant.setHbInterval(BigInteger.valueOf(Long.parseLong(value)));
		} else if ("preparationDelay".equals(key)) {
			faultTolerant.setPreparationDelay(BigInteger.valueOf(Long.parseLong(value)));
		}
	}

	/**
	 * /application/services/bw/checkpoints/*
	 */
	private void addCheckpointsParameter(Checkpoints parent, String key, String value) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * /application/services/bw/*
	 */
	private Object addBwParameter(Bw bw, String key, String value) {
		if ("enabled".equals(key)) {
			bw.setEnabled(Boolean.parseBoolean(value));
		} else if ("failureCount".equals(key)) {
			bw.setFailureCount(BigInteger.valueOf(Long.parseLong(value)));
		} else if ("failureInterval".equals(key)) {
			bw.setFailureInterval(BigInteger.valueOf(Long.parseLong(value)));
		} else if ("isFt".equals(key)) {
			setIsFt(bw, Boolean.parseBoolean(value));
		} else if ("faultTolerant".equals(key)) {
			return getBWFaultTolerant(bw);
		}
		return bw;
	}

	/**
	 * /application/services/adapter/*
	 */
	private Object addAdapterParameter(Adapter adapter, String key, String value) {
		if ("enabled".equals(key)) {
			adapter.setEnabled(Boolean.parseBoolean(value));
		} else if ("failureCount".equals(key)) {
			adapter.setFailureCount(BigInteger.valueOf(Long.parseLong(value)));
		} else if ("failureInterval".equals(key)) {
			adapter.setFailureInterval(BigInteger.valueOf(Long.parseLong(value)));
		}
		return adapter;
	}

	/**
	 * <p>
	 * This method will create JAXB objects from Properties through recursive
	 * calls.
	 * Each property has a path. The last part of a path (after last /) is the
	 * element.
	 * </p> 
	 */
	public Object getElement(String path, String element, String value, Object parent) {
		if (map.containsKey(path)) {
			return map.get(path);
		} else {
			Matcher matcherElement = patternElement.matcher(element);
			if (matcherElement.matches()) {					
				String elementName = matcherElement.group(1);
				String nameAttribute = matcherElement.group(3);

				if (nameAttribute != null) {

					if (elementName.equals("variables")) {
						NVPairs gvs = null;
						if (parent.getClass().equals(Bw.class) || parent.getClass().equals(Adapter.class)) {
							ServiceType service = (ServiceType) parent;
							for (NVPairs nvPairs : service.getNVPairs()) {
								if (nvPairs.getName().equals(nameAttribute)) {
									gvs = nvPairs;
									break;
								}
							}
							if (gvs == null) {
								gvs = new NVPairs();
								service.getNVPairs().add(gvs);
							}
						} else if (parent.getClass().equals(Binding.class)) {
							gvs = new NVPairs();
							gvs.setName(nameAttribute);
							((Binding) parent).setNVPairs(gvs);
						}
						map.put(path, gvs);
						return gvs;
					} else if (elementName.equals("variable")) {
						NameValuePair simpleGV = new NameValuePair();
						simpleGV.setName(nameAttribute);
						simpleGV.setValue(value);

						QName name = new QName(APPLICATION_MANAGEMENT_NAMESPACE, "NameValuePair");
						JAXBElement<NameValuePair> _simpleGV = new JAXBElement<NameValuePair>(name, NameValuePair.class, simpleGV);
						boolean found = false;
						for (JAXBElement<? extends NVPairType> nvPair : ((NVPairs) parent).getNVPair()) {
							if (nameAttribute.equals(nvPair.getValue().getName())) {
								nvPair.getValue().setValue(value);
								found = true;
							}
						}
						if (!found) {
							((NVPairs) parent).getNVPair().add(_simpleGV);
						}

						return simpleGV;
					} else if (elementName.equals("bw")) {
						Bw service = this.getBw(nameAttribute);
						map.put(path, service);
						return service;
					} else if (elementName.equals("adapter")) {
						Adapter service = this.getAdapter(nameAttribute);
						map.put(path, service);
						return service;
					} else if (elementName.equals("binding")) {
						Binding binding = null;
						if (parent.getClass().equals(Bw.class)) {
							binding = this.getBinding(nameAttribute, (Bw) parent);
						} else if (parent.getClass().equals(Adapter.class)) {
							binding = this.getBinding(nameAttribute, (Adapter) parent);
						} else {
							// throw ?
						}
						map.put(path, binding);
						return binding;
					} else if (elementName.equals("bwprocess")) {
						Bwprocess bwProcess = this.getBWProcess(nameAttribute, (Bw) parent);
						map.put(path, bwProcess);
						return bwProcess;
					} else if (elementName.equals("checkpoint")) {
						Checkpoints checkpoints = (Checkpoints) parent;
						if (!checkpoints.getCheckpoint().contains(nameAttribute)) {
							checkpoints.getCheckpoint().add(nameAttribute);
						}
						if ("true".equals(value)) {
							checkpoints.setSelected(nameAttribute);
						}
					}
				} else {
					if (elementName.equals("variables")) {
						NVPairs gvs = null;
						if (parent.getClass().equals(Bw.class) || parent.getClass().equals(Adapter.class)) {
							ServiceType service = (ServiceType) parent;
							for (NVPairs nvPairs : service.getNVPairs()) {
								if (nvPairs.getName().equals("Runtime Variables")) {
									gvs = nvPairs;
									break;
								}
							}
							if (gvs == null) {
								gvs = new NVPairs();
								service.getNVPairs().add(gvs);
							}
						} else if (parent.getClass().equals(Binding.class)) {
							gvs = new NVPairs();
							gvs.setName("Runtime Variables");
							((Binding) parent).setNVPairs(gvs);
						}
						map.put(path, gvs);
						return gvs;
					} else if (elementName.equals("checkpoints")) {
						Checkpoints checkpoints = this.getBWCheckpoints((Bw) parent);
						map.put(path, checkpoints);
						return checkpoints;
					} else if (elementName.equals("tablePrefix")) {
						Checkpoints checkpoints = (Checkpoints) parent;
						checkpoints.setTablePrefix(value);
					// Binding children
					} else if (parent.getClass().equals(Binding.class)) {
						return addBindingParameter((Binding) parent, elementName, value);
					} else if (parent.getClass().equals(Product.class)) {
						return addProductParameter((Product) parent, elementName, value);
					} else if (parent.getClass().equals(Setting.class)) {
						return addSettingParameter((Setting) parent, elementName, value);
					} else if (parent.getClass().equals(NTService.class)) {
						return addNTServiceParameter((NTService) parent, elementName, value);
					} else if (parent.getClass().equals(Java.class)) {
						return addJavaParameter((Java) parent, elementName, value);
					} else if (parent.getClass().equals(Shutdown.class)) {
						return addShutdownParameter((Shutdown) parent, elementName, value);
					//
					// Bwprocess children
					} else if (parent.getClass().equals(Bwprocess.class)) {
						addBWProcessParameter((Bwprocess) parent, elementName, value);
					} else if (parent.getClass().equals(FaultTolerant.class)) {
						addFaultTolerantParameter((FaultTolerant) parent, elementName, value);
					} else if (parent.getClass().equals(Checkpoints.class)) {
						addCheckpointsParameter((Checkpoints) parent, elementName, value);
					// Bw chidren (direct children)
					} else if (parent.getClass().equals(Bw.class)) {
						return addBwParameter((Bw) parent, elementName, value);
					} else if (parent.getClass().equals(Adapter.class)) {
						return addAdapterParameter((Adapter) parent, elementName, value);
					}
				}

			}
			return parent;
		}
	}

	/**
	 * <p>
	 * This will remove from the XML the second duplicate default binding (with
	 * empty name attribute) to keep only the first one.
	 * This is is because "AppManage -export -max" exports two empty bindings
	 * to prepare fault tolerance configuration.
	 * </p>
	 */
	public void removeDuplicateBinding() {
		List<Bw> bwServices = this.getBWServices();

		for (Bw bw : bwServices) {
			boolean first = true;
			
			List<Binding> bindings = bw.getBindings().getBinding();
			for (Iterator<Binding> iterator = bindings.iterator(); iterator.hasNext();) {
				Binding binding = (Binding) iterator.next();

				if (!first && binding.getName().equals("")) {
					iterator.remove();
				}
				first = false;
			}
		}

		List<Adapter> adapterServices = this.getAdapterServices();

		for (Adapter adapter : adapterServices) {
			boolean first = true;

			List<Binding> bindings = adapter.getBindings().getBinding();
			for (Iterator<Binding> iterator = bindings.iterator(); iterator.hasNext();) {
				Binding binding = (Binding) iterator.next();

				if (!first && binding.getName().equals("")) {
					iterator.remove();
				}
				first = false;
			}
		}
	}

	/**
	 * <p>
	 * This will remove from the XML the default binding (with empty name
	 * attribute) if it is not found in the properties.
	 * </p>
	 */
	public void removeDefaultBindingIfNotExists(Properties properties) {
		List<Bw> bwServices = this.getBWServices();
		
		for (Bw bw : bwServices) {
			String path = "bw[" + bw.getName() + "]/bindings/binding[]/machine";
			
			List<Binding> bindings = bw.getBindings().getBinding();
			for (Iterator<Binding> iterator = bindings.iterator(); iterator.hasNext();) {
				Binding binding = (Binding) iterator.next();
//				if (binding.getName().equals("") && properties.getString(path) == null) {
				if (binding.getName().equals("") && !properties.containsKey(path)) {
					iterator.remove();
				}
			}
		}

		List<Adapter> adapterServices = this.getAdapterServices();

		for (Adapter adapter : adapterServices) {
			String path = "adapter[" + adapter.getName() + "]/bindings/binding[]/machine";

			List<Binding> bindings = adapter.getBindings().getBinding();
			for (Iterator<Binding> iterator = bindings.iterator(); iterator.hasNext();) {
				Binding binding = (Binding) iterator.next();
//				if (binding.getName().equals("") && properties.getString(path) == null) {
				if (binding.getName().equals("") && !properties.containsKey(path)) {
					iterator.remove();
				}
			}
		}
	}
	// end of marshalling part

	public String getName() {
		if (this.application == null) {
			return null;
		}
		return this.application.getName();
	}

	public String getDescription() {
		if (this.application == null) {
			return null;
		}
		return this.application.getDescription();
	}

	public String getContact() {
		if (this.application == null) {
			return null;
		}
		return this.application.getContact();
	}

	public String getMaxDeploymentRevision() {
		if (this.application == null) {
			return null;
		}
		return this.application.getMaxdeploymentrevision();
	}

	public void setName(String name) {
		if (this.application == null) {
			return;
		}
		this.application.setName(name);
	}
	
	public void setContact(String contact) {
		if (this.application == null) {
			return;
		}
		this.application.setContact(contact);
	}

	public void setDescription(String description) {
		if (this.application == null) {
			return;
		}
		this.application.setDescription(description);
	}
	
	public void setMaxDeploymentRevision(String maxDeploymentRevision) {
		if (this.application == null) {
			return;
		}
		this.application.setMaxdeploymentrevision(maxDeploymentRevision);
	}

	public RepoInstances getRepoInstances() {
		return this.application.getRepoInstances();
	}

	public Monitor getMonitor(String service) {
		Monitor result = null;
		Bw bw = this.getBw(service);
		if (bw != null) {
			result = bw.getMonitor();
		}
		return result;
	}
	
	public static <E extends fr.fastconnect.factory.tibco.bw.maven.packaging.monitoring.AbstractEvent> void addEvent(Events events, E event) {
		if (events == null || event == null) return;

		String eventType = event.getClass().getName();
		EventType e;
		if (fr.fastconnect.factory.tibco.bw.maven.packaging.monitoring.FailureEvent.class.getName().equals(eventType)) {
			e = new FailureEvent();
			((FailureEvent) e).setFailure(((fr.fastconnect.factory.tibco.bw.maven.packaging.monitoring.FailureEvent) event).getFailure());
		} else if (fr.fastconnect.factory.tibco.bw.maven.packaging.monitoring.LogEvent.class.getName().equals(eventType)) {
			e = new LogEvent();
			((LogEvent) e).setMatch(((fr.fastconnect.factory.tibco.bw.maven.packaging.monitoring.LogEvent) event).getMatch());
		} else {
			throw new UnsupportedOperationException();
		}
		fr.fastconnect.factory.tibco.bw.maven.packaging.monitoring.Actions _actions = event.getActions();
		
		Actions actions = new Actions();
		for (fr.fastconnect.factory.tibco.bw.maven.packaging.monitoring.AlertAction alert : _actions.getAlerts()) {
			AlertAction action = new AlertAction();
			action.setEnabled(alert.getEnabled());
			action.setPerformPolicy(alert.getPerformPolicy());
			action.setLevel(alert.getLevel());
			action.setMessage(alert.getMessage());
			addAction(actions, action);
		}
		for (fr.fastconnect.factory.tibco.bw.maven.packaging.monitoring.CustomAction custom : _actions.getCustoms()) {
			CustomAction action = new CustomAction();
			action.setEnabled(custom.getEnabled());
			action.setPerformPolicy(custom.getPerformPolicy());
			action.setArguments(custom.getArguments());
			action.setCommand(custom.getCommand());
			addAction(actions, action);
		}
		for (fr.fastconnect.factory.tibco.bw.maven.packaging.monitoring.EmailAction email : _actions.getEmails()) {
			EmailAction action = new EmailAction();
			action.setEnabled(email.getEnabled());
			action.setPerformPolicy(email.getPerformPolicy());
			action.setCc(email.getCc());
			action.setMessage(email.getMessage());
			action.setSMTPServer(email.getSmtpServer());
			action.setSubject(email.getSubject());
			action.setTo(email.getTo());
			addAction(actions, action);
		}
		
		e.setActions(actions);
		e.setDescription(event.getDescription());
		e.setRestart(event.getRestart());
		
		addEvent(events, e);
	}

	protected static <E extends EventType> void addEvent(Events events, E event) {
		if (events == null || event == null) return;
		
		@SuppressWarnings("unchecked")
		Class<E> c = (Class<E>) event.getClass();
		JAXBElement<E> e = new JAXBElement<E>(new QName("name"), c, event);
		events.getEvent().add(e);
	}

	public static <A extends ActionType> void addAction(Actions actions, A action) {
		if (actions == null || action == null) return;

		@SuppressWarnings("unchecked")
		Class<A> c = (Class<A>) action.getClass();
		JAXBElement<A> a = new JAXBElement<A>(new QName("name"), c, action);
		actions.getAction().add(a);	
	}

	/** 
	 * Add &lt;events> in &lt;monitor> element of all &lt;bw> elements.
	 * @param events
	 */
	public void addMonitoringEventsToAllServices(Events events) {
		List<JAXBElement<? extends EventType>> events_ = events.getEvent();
		if (events_ != null && !events_.isEmpty()) {
			for (Bw service : this.getBWServices()) {
				Monitor monitor = service.getMonitor();
				if (monitor != null) {
					monitor.setEvents(events);
				}
			}
		}
	}

}
