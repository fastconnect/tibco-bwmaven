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
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * 
 * <p>
 * This goal undeploys a TIBCO BusinessWorks application from a TIBCO domain.
 * </p>
 * 
 * @author Mathieu Debove
 * 
 */
@Mojo( name="undeploy-bw",
defaultPhase=LifecyclePhase.DEPLOY ) // FIXME: should be deployEAR
public class UndeployEARMojo extends AbstractBWDeployMojo {

	protected final static String UNDEPLOY_EAR_FAILED = "The undeployment of the application failed.";
	protected final static String UNDEPLOYING_EAR = "Undeploying the application...";

	private void undeployEAR() throws MojoExecutionException, IOException {
		checkAppManage();

		getLog().info(UNDEPLOYING_EAR);

		ArrayList<String> arguments = super.commonArguments();
		arguments.add("-undeploy");

		ArrayList<File> tras = new ArrayList<File>();
		tras.add(tibcoAppManageTRAPath);

		launchTIBCOBinary(tibcoAppManagePath, tras, arguments, directory, UNDEPLOY_EAR_FAILED);
	}

	public void execute() throws MojoExecutionException {
		if (super.skip()) {
			return;
		}
		try {
			undeployEAR();
		} catch (IOException e) {
			throw new MojoExecutionException(UNDEPLOY_EAR_FAILED, e);
		}
	}

}
