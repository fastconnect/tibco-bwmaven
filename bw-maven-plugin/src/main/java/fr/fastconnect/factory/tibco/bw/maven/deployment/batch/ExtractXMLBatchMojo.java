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

/**
 * <p>
 * This goal performs an export of a TIBCO domain to a directory.
 * </p>
 * <p>
 * The result can be used by batch goals such as
 * <a href="./batch-delete-bw-mojo.html">bw:batch-delete-bw</a>,
 * <a href="./batch-deploy-bw-mojo.html">bw:batch-deploy-bw</a>,
 * <a href="./batch-start-bw-mojo.html">bw:batch-start-bw</a>,
 * <a href="./batch-stop-bw-mojo.html">bw:batch-stop-bw</a> or
 * <a href="./batch-undeploy-bw-mojo.html">bw:batch-undeploy-bw</a>.
 * </p>
 * @author Mathieu Debove
 *
 */
@Mojo(name="batch-extract-xml-bw", defaultPhase=LifecyclePhase.PREPARE_PACKAGE, requiresProject = false)
public class ExtractXMLBatchMojo extends AbstractBWBatchDeployMojo {

	protected final static String EXTRACTING_CONFIG_BATCH = "Extracting configuration from domain (batch)...";
	protected final static String EXTRACTING_CONFIG_SUCCESS = "Successfully extracted configuration from domain (batch) to ";
	protected final static String EXTRACTING_CONFIG_FAILED = "The extraction (batch) of the XML Deployment Descriptor files from the deployed application file has failed.";

	/**
	 * <p>
	 * Whether to extract in template format or not
	 * (<i>-template</i> switch from AppManage).
	 * </p>
	 */
	@Parameter ( property = deployBatchTemplate, required = true , defaultValue = "false" )
	protected boolean batchTemplate;

	/**
	 * <p>
	 * Whether to perform a minimal export or not
	 * (<i>-min</i> switch from AppManage).
	 * </p>
	 */
	@Parameter ( property = deployBatchMin, required = true , defaultValue = "false" )
	protected boolean batchMin;

	/**
	 * <p>
	 * Whether to perform a maximal export or not
	 * (<i>-max</i> switch from AppManage).
	 * </p>
	 */
	@Parameter ( property = deployBatchMax, required = true , defaultValue = "false" )
	protected boolean batchMax;

	/**
	 * <p>
	 * Whether to extract EAR or not
	 * (<i>-noear</i> switch from AppManage).
	 * </p>
	 */
	@Parameter ( property = deployBatchNoEAR, required = true , defaultValue = "false" )
	protected boolean batchNoEAR;

	/**
	 * <p>
	 * Whether to export deployed configuration
	 * (<i>-exportDeployed</i> switch from AppManage).
	 * </p>
	 */
	@Parameter ( property = deployBatchExportDeployed, required = true , defaultValue = "false" )
	protected boolean batchExportDeployed;

	@Override
	public String getInitMessage() {
		super.getInitMessage();
		return EXTRACTING_CONFIG_BATCH;
	}

	@Override
	public String getFailureMessage() {
		return EXTRACTING_CONFIG_FAILED;
	}

	@Override
	public void postAction() throws MojoExecutionException {
		getLog().info(EXTRACTING_CONFIG_SUCCESS + "'" + dir.getAbsolutePath() + "'.");
	}

	@Override
	public ArrayList<String> arguments() {
		ArrayList<String> arguments = super.commonArguments();

		arguments.add("-batchExport");
		if (batchTemplate) {
			arguments.add("-template");
		}
		if (batchMin) {
			arguments.add("-min");
		}
		if (batchMax) {
			arguments.add("-max");
		}
		if (batchNoEAR) {
			arguments.add("-noear");
		}
		if (batchExportDeployed) {
			arguments.add("-exportDeployed");
		}

		return arguments;
	}

}
