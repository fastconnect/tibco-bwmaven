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
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * <p>
 * This goal extracts the XML Deployment Descriptor from a TIBCO BusinessWorks
 * EAR.
 * </p>
 *
 * @author Mathieu Debove
 *
 */
@Mojo( name="extract-xml-from-ear",
defaultPhase=LifecyclePhase.PREPARE_PACKAGE )
public class ExtractXMLFromEARMojo extends AbstractPackagingMojo {

	protected final static String CREATE_XML_FROM_EAR_SUCCESS = "Successfully extracted the XML Deployment Descriptor to ";
	protected final static String CREATE_XML_FROM_EAR_FAILED = "The extraction of the XML Deployment Descriptor file from the EAR file has failed.";

	@Override
	protected String getArtifactFileExtension() {
		return XML_EXTENSION;
	}

	private void createXML() throws MojoExecutionException, IOException {
		checkAppManage();

		String earPath = getProject().getArtifact().getFile().getPath();
		String xmlOutputFile = deploymentDescriptor.getPath(); 
		
		ArrayList<String> arguments = new ArrayList<String>();		
		arguments.add("-export");
		arguments.add("-max");
		arguments.add("-ear");
		arguments.add(earPath); 
		arguments.add("-out");
		arguments.add(xmlOutputFile); 

		ArrayList<File> tras = new ArrayList<File>();
		tras.add(tibcoAppManageTRAPath);

		launchTIBCOBinary(tibcoAppManagePath, tras, arguments, directory, CREATE_XML_FROM_EAR_FAILED);
	}

	public void execute() throws MojoExecutionException {
		if (super.skip()) {
			return;
		}

		try {
			createXML();
			getLog().info(CREATE_XML_FROM_EAR_SUCCESS + " '" + deploymentDescriptor + "'");
		} catch (IOException e) {
			throw new MojoExecutionException(CREATE_XML_FROM_EAR_FAILED, e);
		}
	}

}
