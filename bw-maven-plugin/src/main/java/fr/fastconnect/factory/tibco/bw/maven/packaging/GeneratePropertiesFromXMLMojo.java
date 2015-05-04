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

import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * <p>
 * This goal uses the XML Deployment Descriptor extracted from a TIBCO
 * BusinessWorks EAR (see <a href="./extract-xml-from-ear-mojo.html">
 * bw:extract-xml-from-ear</a>) or from a TIBCO BusinessWorks application (see
 * <a href="./extract-xml-from-domain-mojo.html">bw:extract-xml-from-domain</a>)
 * to generate properties files.
 * </p>
 * <p>
 * The result of this generation will be two properties files:
 *  <ul>
 *   <li>
 *    one for Global Variables (application level) 
 *   </li>
 *   <li>
 *    one for Services (= Process Archives, Bindings, Processes...) defined in
 *    the application
 *   </li>
 *  </ul>
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
@Mojo( name="generate-properties-from-xml",
defaultPhase=LifecyclePhase.PREPARE_PACKAGE )
public class GeneratePropertiesFromXMLMojo extends AbstractPackagingMojo {

	protected final static String XML_LOAD_SUCCESS = "Successfully loaded properties from XML file";
	protected final static String XML_LOAD_FAILURE = "Failed to load properties from XML file";

	protected final static String PROPERTIES_SAVE_SERVICES_SUCCESS = "Successfully saved properties to file";
	protected final static String PROPERTIES_SAVE_SERVICES_FAILURE = "Failed to save properties to XML file";

	protected final static String PROPERTIES_SAVE_GVS_SUCCESS = "Successfully saved properties to file";
	protected final static String PROPERTIES_SAVE_GVS_FAILURE = "Failed to save properties to XML file";
	
	@Override
	protected String getArtifactFileExtension() {
		return PROPERTIES_EXTENSION;
	}

	public void execute() throws MojoExecutionException {
		if (super.skip()) {
			return;
		}

		Properties earGlobalVariables = new Properties();
		Properties earServices = new Properties();
		try {
			ApplicationManagement application = new ApplicationManagement(deploymentDescriptor);
			
			earGlobalVariables = application.getGlobalVariables();
			earServices = application.getServices();

			getLog().info(XML_LOAD_SUCCESS + " '" + deploymentDescriptor + "'");
		} catch (JAXBException e) {
			throw new MojoExecutionException(XML_LOAD_FAILURE + " '" + deploymentDescriptor + "'", e);
		}

		// Export Properties in a properties file
		//  Global Variables
		savePropertiesToFile(deploymentGlobalVariables,
							 earGlobalVariables,
							 "Global Variables",
							 PROPERTIES_SAVE_SERVICES_SUCCESS,
							 PROPERTIES_SAVE_SERVICES_FAILURE);
		//  Services (=~ Process Archives)
		savePropertiesToFile(deploymentServices,
							 earServices,
							 "Services (Bindings, Processes)",
							 PROPERTIES_SAVE_GVS_SUCCESS,
							 PROPERTIES_SAVE_GVS_FAILURE);
	}

}
