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

import java.io.File;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * 
 * <p>
 * This goal deploys a TIBCO BusinessWorks application to a TIBCO domain.
 * </p>
 * 
 * @author Mathieu Debove
 * 
 */
@Mojo( name="deploy-bw",
defaultPhase=LifecyclePhase.DEPLOY ) // FIXME: should be deployEAR
//@Execute ( goal="deploy-bw", lifecycle="deploy")
public class DeployEARMojo extends AbstractBWDeployMojo {

	protected final static String DEPLOY_EAR_FAILED = "The deployment of the application failed.";
	protected final static String DEPLOYING_APPLICATION = "Deploying application to ";
	protected final static String DEPLOYING_ON_DOMAIN = " on domain ";

	protected final static String USING_EAR = "Using EAR : ";
	protected final static String USING_XML = "Using XML : ";

	@Parameter
	private boolean redeploy;
	
	/**
	 * If true, the successfully deployed service instances won't be started.
	 * Default is false.
	 */
	@Parameter ( property = "deploy.nostart", defaultValue = "false")
	private boolean noStart;

	/**
	 * If true, the service instances running before deployment won't be stopped.
	 * Default is false.
	 */
	@Parameter ( property = "deploy.nostop", defaultValue = "false")
	private boolean noStop;
	
	/**
	 * If true, deploy service instances one at a time instead of in parallel.
	 * Default is true.
	 */
	@Parameter ( property = "deploy.serialize", defaultValue = "true")
	private boolean serialize;
	
	@Parameter
	private File deployConfigXML;

	@Override
	public String getInitMessage() {
		return DEPLOYING_APPLICATION + "'" + deployedProjectName + "'" +  DEPLOYING_ON_DOMAIN + "'" +  domainName+ "'";
	}

	@Override
	public String getFailureMessage() {
		return DEPLOY_EAR_FAILED;
	}

	@Override
	public ArrayList<String> arguments() {
		File ear = getOutputFile();
		if (ear == null || !ear.exists()) {
			MavenProject project = getProject();
			if (project != null && project.getBasedir() != null && project.getBasedir().exists()) {
				ear = getArtifactFile(getProject().getBasedir(), finalName, classifier);
			}
		}

		getLog().info(USING_EAR + ear.getAbsolutePath());
		getLog().info(USING_XML + deploymentDescriptorFinal.getAbsolutePath());

		ArrayList<String> arguments = super.commonArguments();
		arguments.add("-deploy");
		arguments.add("-ear");
		arguments.add(ear.getAbsolutePath());
		arguments.add("-deployConfig");
		arguments.add(deploymentDescriptorFinal.getAbsolutePath());
		if (serialize) {
			arguments.add("-serialize");
		}
		if (noStart) {
			arguments.add("-nostart");
		}
		if (noStop) {
			arguments.add("-nostop");
		}
		arguments.add("-force");

		return arguments;
	}

	@Override
	public void postAction() throws MojoExecutionException {
		// nothing to do
	}

}
