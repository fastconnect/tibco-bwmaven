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
package fr.fastconnect.factory.tibco.bw.maven.packaging.pom;

import java.io.File;
import java.io.InputStream;

import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * <p>
 * This goal creates a standalone deployment POM with
 * "<a href="./bwmaven-lifecycles.html#Deployment">bw-ear-deploy</a>" packaging.
 * <br/>
 * This POM will be used at deployment time and does not depend on any other
 * POM.
 * </p>
 *
 * @author Mathieu Debove
 *
 */
@Mojo( name="generate-standalone-deployment-pom",
defaultPhase=LifecyclePhase.PREPARE_PACKAGE )
public class GenerateStandaloneDeploymentPOM extends AbstractPOMGenerator {

	protected final static String DEPLOYMENT_POM_CLASSIFIER = "standalone-deployment";
	protected final static String DEPLOYMENT_POM_GENERATION = "Generating standalone deployment POM in ";
	protected final static String DEPLOYMENT_POM_FAILURE = "Unable to create standalone deployment POM";

	@Parameter (property="deploy.pom.standalone", required=true, defaultValue="pom.xml")
	protected String pomStandaloneDeploymentFilename;

	/**
	 * The deployment POM template is a partial Maven POM which will be used to
	 * generate the deployment POM.
	 */
	@Parameter (property="deploy.pom.standalone.template", required=false, defaultValue="${project.basedir}/src/main/maven/standalone-deployment-pom.xml")
	protected File pomStandaloneDeploymentTemplate;

	/**
	 * Whether to merge the deployment POM template with the built-in
	 * deployment POM.
	 */
	@Parameter (property="deploy.pom.standalone.template.merge", required=false, defaultValue="true")
	protected Boolean pomStandaloneDeploymentTemplateMerge;

	/**
	 * To skip the generation of the POM.
	 * This might be useful to ignore intermediate parent POM between "bw-ear"
	 * POMs and the real top level parent POM.
	 */
	@Parameter ( property = "generate.pom.standalone.skip", required = false, defaultValue = "false")
	protected boolean skipStandaloneDeploymentPOM;

    /**
     * Whether to "touch" the deployment POM file when deployment POM generation
     * is skipped.<br /><br />
     *
     * NB: must be used with 'bw.package.skip' or 'generate.pom.standalone.skip'
     * set to true.
     */
    @Parameter(property = "generate.pom.standalone.skip.touch", required=false, defaultValue="false")
    protected Boolean touchStandaloneDeploymentPOMIfSkipped;

	@Override
	protected File getOutputFile() {
		return new File(packageDirectory + File.separator + pomStandaloneDeploymentFilename);
	}

	@Override
	protected File getTemplateFile() {
		return pomStandaloneDeploymentTemplate;
	}

	@Override
	protected Boolean getTemplateMerge()  {
		return pomStandaloneDeploymentTemplateMerge;
	}

	@Override
	protected String getClassifier() {
		return DEPLOYMENT_POM_CLASSIFIER;
	}

	@Override
	protected Boolean getSkipGeneratePOM() {
		return skipStandaloneDeploymentPOM;
	}

	@Override
	protected Boolean getTouchWhenSkipped() {
		return touchStandaloneDeploymentPOMIfSkipped;
	}

	@Override
	protected InputStream getBuiltinTemplateFile() {
		return this.getClass().getClassLoader().getResourceAsStream("deploy/pom/pom.xml");
	}

	@Override
	protected Model updateModel(Model model, MavenProject project) throws MojoExecutionException {
		model.setPackaging("bw-ear-deploy");
		model.getModules().clear();

		updatePluginVersion(model);
		addPlugin(model, true);

		return model;
	}

	@Override
	protected String getGenerationMessage() {
		return DEPLOYMENT_POM_GENERATION;
	}

	@Override
	protected String getFailureMessage() {
		return DEPLOYMENT_POM_FAILURE;
	}

}
