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
package fr.fastconnect.factory.tibco.bw.maven.deployment.batch;

import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo( name="batch-deploy-bw",
defaultPhase=LifecyclePhase.PREPARE_PACKAGE )
public class DeployBatchMojo extends AbstractBWBatchDeployMojo {

	protected final static String DEPLOYING_BATCH = "Extracting configuration from domain (batch)...";
	protected final static String DEPLOYING_FAILED = "The batch deployment failed.";

	/**
	 * <p>
	 * Whether to extract in template format or not
	 * (<i>-template</i> switch from AppManage).
	 * </p>
	 */
	@Parameter ( property = deployBatchNoStart, required = true , defaultValue = "false" )
	protected boolean batchNoStart;

	/**
	 * <p>
	 * Whether to perform a minimal export or not
	 * (<i>-min</i> switch from AppManage).
	 * </p>
	 */
	@Parameter ( property = deployBatchNoStop, required = true , defaultValue = "false" )
	protected boolean batchNoStop;

	/**
	 * <p>
	 * Whether to perform a maximal export or not
	 * (<i>-max</i> switch from AppManage).
	 * </p>
	 */
	@Parameter ( property = deployBatchForce, required = true , defaultValue = "false" )
	protected boolean batchForce;

	@Override
	public String getInitMessage() {
		return DEPLOYING_BATCH;
	}

	@Override
	public String getFailureMessage() {
		return DEPLOYING_FAILED;
	}

	@Override
	public void postAction() throws MojoExecutionException {
		//
	}

	@Override
	public ArrayList<String> arguments() {
		ArrayList<String> arguments = super.commonArguments();

		arguments.add("-batchExport");
		if (batchNoStart) {
			arguments.add("-nostart");
		}
		if (batchNoStop) {
			arguments.add("-nostop");
		}
		if (batchForce) {
			arguments.add("-force");
		}

		return arguments;
	}

}
