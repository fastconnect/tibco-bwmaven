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

import java.io.File;
import java.util.ArrayList;

import org.apache.maven.plugins.annotations.Parameter;

import fr.fastconnect.factory.tibco.bw.maven.deployment.AbstractBWDeployMojo;

/**
 * <p>
 * This class inherits from {@link AbstractBWDeployMojo} and specializes its
 * behaviour for batch goals.
 * </p>
 *
 * @author Mathieu Debove
 *
 */
public abstract class AbstractBWBatchDeployMojo extends AbstractBWDeployMojo {

	protected final static String deployBatchDir = "deploy.batch.dir";
	protected final static String deployBatchNoEAR = "deploy.batch.noear";
	protected final static String deployBatchTemplate = "deploy.batch.template";
	protected final static String deployBatchMin = "deploy.batch.min";
	protected final static String deployBatchMax = "deploy.batch.max";
	protected final static String deployBatchExportDeployed = "deploy.batch.exportDeployed";
	protected final static String deployBatchNoStart = "deploy.batch.nostart";
	protected final static String deployBatchNoStop = "deploy.batch.nostop";
	protected final static String deployBatchForce = "deploy.batch.force";

	/**
	 * <p>
	 * Directory to use for batch commands (<i>-dir</i> switch from AppManage).
	 * </p>
	 * <p>
	 * This directory is where the exported archives and deployment
	 * configuration files are stored or used.
	 * </p>
	 */
	@Parameter ( property = deployBatchDir, required = true )
	protected File dir;

	@Override
	public String getInitMessage() {
		if (dir != null) {
			getLog().info("Batch directory is: '" + dir.getAbsolutePath() + "'.");
			getLog().info("");
		}
		return ""; 
	}

	@Override
	public ArrayList<String> commonArguments() {
		ArrayList<String> commonArguments = super.commonArguments();
		commonArguments.add("-dir");
		commonArguments.add(dir.getAbsolutePath());

		return commonArguments;
	}
}
