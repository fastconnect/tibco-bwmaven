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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import fr.fastconnect.factory.tibco.bw.maven.source.POMManager;

/**
 * <p>
 * This goal creates a POM with "<a href="./bwmaven-lifecycles.html#Deployment">
 * bw-ear-deploy</a>" packaging.<br/>
 * This POM will be used at deployment time.
 * </p>
 *
 * @author Mathieu Debove
 *
 */
@Mojo( name="generate-deployment-root-pom",
defaultPhase=LifecyclePhase.PREPARE_PACKAGE )
public class GenerateRootDeploymentPOM extends AbstractPOMGenerator {

	protected final static String DEPLOYMENT_POM_CLASSIFIER = "root-deployment";
	protected final static String DEPLOYMENT_POM_GENERATION = "Generating root deployment POM in ";
	protected final static String DEPLOYMENT_POM_FAILURE = "Unable to create root deployment POM";

	protected final static String WARN_NO_PARENT = "The property 'useParent' is set but no parent was found";

	@Parameter (property="deploy.pom", required=true, defaultValue="pom.xml")
	protected String pomDeployFilename;

	/**
	 * The deployment POM template is a partial Maven POM which will be used to
	 * generate the deployment POM.
	 */
	@Parameter (property="deploy.pom.template", required=false, defaultValue="${project.basedir}/src/main/maven/deployment-pom.xml")
	protected File pomDeployTemplate;

	/**
	 * Whether to merge the deployment POM template with the built-in
	 * deployment POM.
	 */
	@Parameter (property="deploy.pom.template.merge", required=false, defaultValue="true")
	protected Boolean pomDeployTemplateMerge;

	/**
	 * Use the parent POM as the source instead of the current POM.
	 * This might be useful for the delivery POM pattern.
	 */
	@Parameter ( property = "generate.pom.use.parent", required = false, defaultValue = "false")
	protected boolean useParent;

	/**
	 * To skip the generation of the POM.
	 * This might be useful to ignore intermediate parent POM between "bw-ear"
	 * POMs and the real top level parent POM.
	 */
	@Parameter ( property = "generate.pom.root.skip", required = false, defaultValue = "true")
	protected boolean skipRootDeploymentPOM;

    /**
     * Whether to "touch" the deployment POM file when deployment POM generation
     * is skipped.<br /><br />
     *
     * NB: must be used with 'bw.package.skip' or 'generate.pom.standalone.skip'
     * set to true.
     */
    @Parameter(property = "generate.pom.root.skip.touch", required=false, defaultValue="false")
    protected Boolean touchRootDeploymentPOMIfSkipped;

	@Parameter
	protected List<String> excludedModules;

	@Parameter
	protected List<String> includedModules;

	/* parent definition */
	/**
	 * The groupId of the parent.
	 */
	@Parameter ( property = "generate.pom.parent.groupId", required = true)
	protected String parentGroupId;

	/**
	 * The artifactId of the parent.
	 */
	@Parameter ( property = "generate.pom.parent.artifactId", required = true)
	protected String parentArtifactId;

	/**
	 * The version of the parent.
	 */
	@Parameter ( property = "generate.pom.parent.version", required = true)
	protected String parentVersion;

	/**
	 * The suffix of artifactId of the parent.
	 */
	@Parameter ( property = "generate.pom.parent.suffix", required = true, defaultValue = "deploy")
	protected String parentArtifactSuffix;

	@Parameter(property="package.bw.applications.root", defaultValue="applications", required=true)
	public String applicationsRoot;

	/* modules */
	private List<String> getActiveModules(List<Profile> activeProfiles, Model model) {
		List<String> modules = model.getModules();

		for (Profile profile : model.getProfiles()) {
			Boolean found = false;
			for (Profile p : activeProfiles) {
				if (p.getId().equals(profile.getId())) {
					found = true;
					break;
				}
			}
			if (!found) continue;

			for (String module : profile.getModules()) {
				if (modules.indexOf(module) < 0) {
					modules.add(module);
				}
			}
		}

		return modules;
	}

	@SuppressWarnings("unchecked")
	private List<Profile> getActiveProfiles(List<MavenProject> projects) {
		List<Profile> result = new ArrayList<Profile>();
		for (MavenProject project : projects) {
			result.addAll(project.getActiveProfiles());
		}

		return result;
	}

	private Model addModules(Model model, MavenProject project) throws IOException, XmlPullParserException {
		if (model == null || project == null) return null;

		// we need to include only the active projects (see the -am command line switch for detail)
		List<MavenProject> activeProjects = getSession().getProjects();

		List<String> modules = addModules(project, activeProjects);
		for (String module : modules) {
			getLog().debug("module : " + module);
			boolean excluded = false;
			for (String exclude : excludedModules) {
				String regex = wildcardToRegex(exclude);
				if (exclude != null && Pattern.matches(regex, module)) {
					excluded = true;
					break;
				}
			}
			if (excluded) continue;
			if (includedModules.isEmpty()) {
				model.addModule(module);
			} else {
				for (String include : includedModules) {
					String regex = wildcardToRegex(include);
					if (include != null && Pattern.matches(regex, module)) {
						model.addModule(module);
//						break;
					}
				}
			}
		}

		return model;
	}

	private List<String> addModules(MavenProject project, List<MavenProject> activeProjects) throws IOException, XmlPullParserException {
		return addModules(project.getBasedir() + File.separator, project.getModel(), getActiveProfiles(activeProjects), activeProjects);
	}

	private List<String> addModules(String basedir, Model model, List<Profile> activeProfiles, List<MavenProject> activeProjects) throws IOException, XmlPullParserException {
		List<String> result = new ArrayList<String>();

		List<String> modules = getActiveModules(activeProfiles, model);

		for (String module : modules) {
			String childFileName = basedir + module + File.separator + "pom.xml";
			if (!new File(childFileName).exists()) continue;

			// load the model of the module
			Model child = POMManager.getModelFromPOM(new File(childFileName), getLog());
			if (child.getPackaging().equals(BWEAR_TYPE)) { // add POMs with "bw-ear" packaging
				if (isProjectActive(child, activeProjects) != null) { // exclude inactive projects (if -am command line switch is used)

					String relativePath = getModuleRelativePath(child);
					result.add(applicationsRoot + MODULE_SEPARATOR + relativePath);
				}
			} else if (child.getPackaging().equals(POM_TYPE)) {
				// recursively add children found in POMs with "pom" packaging
				result.addAll(addModules(basedir + module + File.separator, child, activeProfiles, activeProjects));
			}
		}

		return result;
	}

	private String getModuleRelativePath(Model child) {
		String result = child.getProperties().getProperty("assembly.relative.path");
		if (result == null || result.isEmpty()) {
			result = ".";
		}
		result += MODULE_SEPARATOR + child.getArtifactId() + MODULE_SEPARATOR + pomDeployFilename;
		return result;
	}

	private MavenProject isProjectActive(Model model, List<MavenProject> activeProjects) {
		for (MavenProject mavenProject : activeProjects) {
			String packageSkipProperty = mavenProject.getProperties().getProperty("bw.package.skip");
			boolean packageSkip = packageSkipProperty != null && packageSkipProperty.equals("true");
			if ((mavenProject.getGroupId().equals(model.getGroupId()) || (model.getGroupId() == null)) && // == null in case of [inherited] value
			    mavenProject.getArtifactId().equals(model.getArtifactId()) &&
			    (mavenProject.getVersion().equals(model.getVersion()) || (model.getVersion() == null)) && // == null in case of [inherited] value
			    !packageSkip) {
				return mavenProject;
			}
		}
		return null;
	}
	/* /modules */

	private String getArtifactSuffix() {
		if (parentArtifactSuffix != null && !parentArtifactSuffix.isEmpty()) {
			if (parentArtifactSuffix.startsWith("-")) {
				return parentArtifactSuffix;
			} else {
				return "-" + parentArtifactSuffix;
			}
		} else {
			return "";
		}
	}

	public void execute() throws MojoExecutionException {
		if (excludedModules == null) {
			excludedModules = new ArrayList<String>();
		}
		if (includedModules == null) {
			includedModules = new ArrayList<String>();
		}

		super.execute();
	}

	@Override
	protected File getOutputFile() {
		return new File(packageDirectory + File.separator + pomDeployFilename);
	}

	@Override
	protected File getTemplateFile() {
		return pomDeployTemplate;
	}

	@Override
	protected Boolean getTemplateMerge()  {
		return pomDeployTemplateMerge;
	}

	@Override
	protected String getClassifier() {
		return DEPLOYMENT_POM_CLASSIFIER;
	}

	@Override
	protected Boolean getSkipGeneratePOM() {
		return skipRootDeploymentPOM;
	}

	@Override
	protected Boolean getTouchWhenSkipped() {
		return touchRootDeploymentPOMIfSkipped;
	}

	@Override
	protected InputStream getBuiltinTemplateFile() {
		return this.getClass().getClassLoader().getResourceAsStream("deploy/pom/pom.xml");
	}

	@Override
	protected Model updateModel(Model model, MavenProject project) throws MojoExecutionException {
		model.setArtifactId(project.getArtifactId() + getArtifactSuffix());
		model.setPackaging("pom");

		updatePluginVersion(model);

		try {
			model = addModules(model, project);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getLocalizedMessage(), e);
		} catch (XmlPullParserException e) {
			throw new MojoExecutionException(e.getLocalizedMessage(), e);
		}

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

	@Override
	protected MavenProject getProject() {
		MavenProject project = super.getProject();

		if (useParent) {
			MavenProject parent = project.getParent();

			if (parent != null) {
				project = parent;
			} else {
				getLog().warn(WARN_NO_PARENT);
			}
		}

		return project;
	}

}
