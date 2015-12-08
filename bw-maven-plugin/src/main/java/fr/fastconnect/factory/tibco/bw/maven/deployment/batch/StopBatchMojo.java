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

/**
 * <p>
 * This goal stops multiple BusinessWorks applications from a domain in batch
 * mode.
 * </p>
 * <p>
 * It is based on an export performed by the
 * <a href="./batch-extract-xml-bw-mojo.html">bw:batch-extract-xml-bw</a> goal.
 * </p>
 * @author Mathieu Debove
 *
 */
@Mojo(name="batch-stop-bw", defaultPhase=LifecyclePhase.PREPARE_PACKAGE, requiresProject = false)
public class StopBatchMojo extends AbstractBWBatchDeployMojo {

	protected final static String STOP_BATCH = "Stopping applications from domain (batch)...";
	protected final static String STOP_FAILED = "The batch stop failed.";

	@Override
	public String getInitMessage() {
		super.getInitMessage();
		return STOP_BATCH;
	}

	@Override
	public String getFailureMessage() {
		return STOP_FAILED;
	}

	@Override
	public void postAction() throws MojoExecutionException {
		//
	}

	@Override
	public ArrayList<String> arguments() {
		ArrayList<String> arguments = super.commonArguments();

		arguments.add("-batchStop");

		return arguments;
	}

}
