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
package fr.fastconnect.factory.tibco.bw.maven.deployment;

import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * <p>
 * This goal extracts the XML Deployment Descriptor from a TIBCO BusinessWorks
 * application deployed on a TIBCO domain.
 * </p>
 *
 * @author Mathieu Debove
 *
 */
@Mojo( name="extract-xml-bw",
defaultPhase=LifecyclePhase.PREPARE_PACKAGE )
public class ExtractXMLFromDomainMojo extends AbstractBWDeployMojo {

	protected final static String CREATING_XML_FROM_DOMAIN = "Extracting the XML Deployment Descriptor from domain ";
	protected final static String CREATE_XML_FROM_DOMAIN_SUCCESS = "Successfully extracted the XML Deployment Descriptor to ";
	protected final static String CREATE_XML_FROM_DOMAIN_FAILED = "The extraction of the XML Deployment Descriptor file from the deployed application file has failed.";

	@Override
	public String getInitMessage() {
		return CREATING_XML_FROM_DOMAIN + "'" + domainName + "'";
	}

	@Override
	public String getFailureMessage() {
		return CREATE_XML_FROM_DOMAIN_FAILED;
	}

	@Override
	public ArrayList<String> arguments() {
		ArrayList<String> arguments = super.commonArguments();

		String xmlOutputFile = deploymentDescriptor.getPath();

		arguments.add("-export");
		arguments.add("-out");
		arguments.add(xmlOutputFile);

		return arguments;
	}

	@Override
	public void postAction() throws MojoExecutionException {
		getLog().info(CREATE_XML_FROM_DOMAIN_SUCCESS + " '" + deploymentDescriptor + "'");
	}

}
