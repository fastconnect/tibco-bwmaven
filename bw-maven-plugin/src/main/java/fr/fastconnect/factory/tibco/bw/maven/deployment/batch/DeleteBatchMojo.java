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
 * This goal deletes multiple BusinessWorks applications from a domain in batch
 * mode.
 * </p>
 * <p>
 * It is based on an export performed by the
 * <a href="./batch-extract-xml-bw-mojo.html">bw:batch-extract-xml-bw</a> goal.
 * </p>
 * @author Mathieu Debove
 *
 */
@Mojo(name="batch-delete-bw", defaultPhase=LifecyclePhase.PREPARE_PACKAGE, requiresProject = false)
public class DeleteBatchMojo extends AbstractBWBatchDeployMojo {

	protected final static String DELETE_BATCH = "Deleting applications from domain (batch)...";
	protected final static String DELETE_FAILED = "The batch delete failed.";

	/**
	 * <p>
	 * Whether to force delete or not
	 * (<i>-force</i> switch from AppManage).
	 * </p>
	 */
	@Parameter ( property = deployBatchForce, required = true , defaultValue = "false" )
	protected boolean batchForce;

	@Override
	public String getInitMessage() {
		super.getInitMessage();
		return DELETE_BATCH;
	}

	@Override
	public String getFailureMessage() {
		return DELETE_FAILED;
	}

	@Override
	public void postAction() throws MojoExecutionException {
		//
	}

	@Override
	public ArrayList<String> arguments() {
		ArrayList<String> arguments = super.commonArguments();

		arguments.add("-batchDelete");
		if (batchForce) {
			arguments.add("-force");
		}

		return arguments;
	}

}
