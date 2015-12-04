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

import static org.twdata.maven.mojoexecutor.MojoExecutor.element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.enforcer.RequireProperty;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.twdata.maven.mojoexecutor.MojoExecutor;

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
@Mojo( name="generate-pom",
defaultPhase=LifecyclePhase.PREPARE_PACKAGE )
public class GeneratePOM extends AbstractPackagingMojo {

	protected final static String MODULE_SEPARATOR = "/";

	protected final static String GENERATING_POM_DEPLOY = "Generating deployment POM in ";
	protected final static String FAILURE_POM_DEPLOY = "Unable to create deployment POM";
	protected final static String WARN_NO_PARENT = "The property 'useParent' is set but no parent was found";

	@Parameter (property="deploy.pom", required=true, defaultValue="pom.xml")
	protected String pomDeployFilename;

	@Parameter (property="deploy.pom.template", required=false, defaultValue="${project.basedir}/src/main/maven/deployment-pom.xml")
	protected File pomDeployTemplate;

	/**
	 * Whether to merge the deployment POM template with the built-in
	 * deployment POM.
	 */
	@Parameter (property="deploy.pom.template.merge", required=false, defaultValue="true")
	protected Boolean pomDeployTemplateMerge;

	@Parameter
	protected List<String> excludedModules;

	@Parameter
	protected List<String> includedModules;

	/**
	 * The deployProperties will be "forwarded" from main POM to deploy POM.
	 */
	@Parameter
	protected List<String> deployProperties;


// override Parameters from AbstractBWDeployMojo with "required = false"
	/**
	 * Name of the project once deployed in TIBCO domain
	 */
	@Parameter ( property = deployedProjectNameProperty, required = false)
	protected String deployedProjectName;

	/**
	 * TIBCO domain name
	 */
	@Parameter ( property = domainNameProperty, required = false)
	protected String domainName;

	/**
	 * TIBCO domain username
	 */
	@Parameter ( property = domainUsernameProperty, required = false)
	protected String domainUsername;

	/**
	 * TIBCO domain password
	 */
	@Parameter ( property = domainPasswordProperty, required = false)
	protected String domainPassword;
//
	
	/**
	 * Use the parent POM as the source instead of the current POM.
	 * This might be useful for the delivery POM pattern.
	 */
	@Parameter ( property = "generate.pom.use.parent", required = false, defaultValue = "false")
	protected boolean useParent;

	@Parameter (property="generate.pom.ignore.parent", required = false)
	protected String ignoredParent;

    @Parameter( required = false )
    private List<RequireProperty> rules;

//    @Parameter( required = false, property = "generate.pom.incude.external.properties", defaultValue = "false" )
//    private Boolean includeExternalPropertiesFile;

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

	/**
	 * To skip the generation of the POM.
	 * This might be useful to ignore intermediate parent POM between "bw-ear"
	 * POMs and the real top level parent POM. 
	 */
	@Parameter ( property = "generate.pom.skip", required = false, defaultValue = "false")
	protected boolean skipGeneratePom;

	@Parameter(property="package.bw.applications.root", defaultValue="applications", required=true)
	public String applicationsRoot;
	
	@Component
	private ArtifactMetadataSource artifactMetadataSource;

	@Component
	private PluginDescriptor pluginDescriptor;

	@Override
	protected String getArtifactFileExtension() {
		return POM_EXTENSION;
	}

	/* properties */
	private void updateProperty(String propertyName, String propertyValue, Model model) {
		if (propertyValue != null) {
			model.getProperties().setProperty(propertyName, propertyValue);
		}
	}

	/**
	 * 
	 * @param model
	 * @param project
	 * @return the root deployment POM (if found)
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private File updateProperties(Model model, MavenProject project) throws IOException, XmlPullParserException {
		File result = null;

		if (project != null && project.getParent() != null) {
			result = updateProperties(model, project.getParent());
		}

		
		if (project.getPackaging().equals(POM_TYPE)) {
			File packageDirectory = new File((String) project.getProperties().get("project.package.directory"));
			File deployPOM = new File(packageDirectory + File.separator + pomDeployFilename);

			Boolean isEmpty = EmptyFileFilter.EMPTY.accept(deployPOM);
			if (deployPOM.exists() && !isEmpty) {
				Model deployPomModel = POMManager.getModelFromPOM(deployPOM, getLog());
				if (deployPomModel.getGroupId().equals(parentGroupId) &&
					deployPomModel.getArtifactId().equals(parentArtifactId + getArtifactSuffix())) {
					result = deployPOM;
				}
			}
		}
		
		Model projectModel;
		if (project.getFile() != null) {
			getLog().debug("Using model in POM : " + project.getFile().getAbsolutePath());
			projectModel = POMManager.getModelFromPOM(project.getFile(), getLog());
		} else {
			projectModel = project.getModel();
		}
		
		Properties properties = projectModel.getProperties();
		
		@SuppressWarnings("unchecked")
		List<Profile> activeProfiles = project.getActiveProfiles();

		List<String> activeProfilesIds = new ArrayList<String>();
		for (Profile profile : activeProfiles) {
			activeProfilesIds.add(profile.getId());
		}

		for (Profile profile : projectModel.getProfiles()) {
			if (activeProfilesIds.contains(profile.getId())) {
				properties.putAll(profile.getProperties());
			}
		}

		List<String> ignoredProperties = new ArrayList<String>();
		
		String propertyValue = properties.getProperty(deployedProjectNameProperty);
		if (propertyValue != null) {
			model.getProperties().setProperty(deployedProjectNameProperty, propertyValue);
		}
		ignoredProperties.add(deployedProjectNameProperty);
		
		String s = project.getProperties().getProperty(deploymentDescriptorFinalProperty);
		getLog().debug(s);
		File f = new File(s);
		updateProperty(deploymentDescriptorProperty, f.getName(), model);
		updateProperty(deploymentDescriptorFinalProperty, project.getArtifactId() + "-deploy.xml", model);
		
		ignoredProperties.add(deploymentDescriptorProperty);
		ignoredProperties.add(deploymentDescriptorFinalProperty);
		ignoredProperties.add(deploymentGlobalVariablesProperty);
		ignoredProperties.add(deploymentServicesProperty);
		ignoredProperties.add(deploymentGlobalVariablesReferenceProperty);
		ignoredProperties.add(deploymentServicesReferenceProperty);
		ignoredProperties.add(deploymentGlobalVariablesCommonProperty);
		ignoredProperties.add(deploymentServicesCommonProperty);
		
		if (!isParentIgnored(project)) {
			Enumeration<Object> e = properties.keys();
	   		while (e.hasMoreElements()) {
				String propertyName = (String) e.nextElement();
				if (!ignoredProperties.contains(propertyName)) {
					propertyValue = properties.getProperty(propertyName);
					if (propertyValue != null) {
						model.getProperties().setProperty(propertyName, propertyValue);
					}
				}
			}
		}
		
		return result;
	}
	//

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

	/**
	 * This copies the root deployment POM to the "target/" directory of Maven
	 * modules.
	 *
	 * @param outputFile 
	 * @param model
	 * @param project
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private void copyToModules(File outputFile, Model model, MavenProject project) throws IOException, XmlPullParserException {
		if (outputFile == null || model == null || project == null) return;

		// we need to include only the active projects (see the -am command line switch for detail)
		List<MavenProject> activeProjects = getSession().getProjects();

		copyToModules(outputFile, project, activeProjects);
	}

	@SuppressWarnings("unchecked")
	private List<Profile> getActiveProfiles(List<MavenProject> projects) {
		List<Profile> result = new ArrayList<Profile>();
		for (MavenProject project : projects) {
			result.addAll(project.getActiveProfiles());
		}
			
		return result;
	}

	private void copyToModules(File outputFile, MavenProject project, List<MavenProject> activeProjects) throws IOException, XmlPullParserException {
		copyToModules(outputFile, project.getBasedir() + File.separator, project.getModel(), getActiveProfiles(activeProjects), activeProjects);
	}

	private void copyToModules(File outputFile, String basedir, Model model, List<Profile> activeProfiles, List<MavenProject> activeProjects) throws IOException, XmlPullParserException {
		List<String> modules = getActiveModules(activeProfiles, model);

		for (String module : modules) {
			String childFileName = basedir + module + File.separator + "pom.xml";
			if (!new File(childFileName).exists()) continue;
			
			// load the model of the module
			Model child = POMManager.getModelFromPOM(new File(childFileName), getLog());
			MavenProject mavenProject = null;
			if (child.getPackaging().equals(BWEAR_TYPE) && // add POMs with "bw-ear" packaging
				(mavenProject  = isProjectActive(child, activeProjects)) != null) { // exclude inactive projects (if -am command line switch is used)

				File destinationFile = new File(mavenProject.getBuild().getDirectory() + File.separator + deployedProjectName);
				FileUtils.copyFile(outputFile, destinationFile);
				getLog().debug("Copying deployment POM '" + outputFile.getAbsolutePath() + "' to: " + destinationFile.getAbsolutePath());
			} else if (child.getPackaging().equals(POM_TYPE)) {
				// recursively add children found in POMs with "pom" packaging
				copyToModules(outputFile, basedir + module + File.separator, child, activeProfiles, activeProjects);
			}
		}
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
	//

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

	/**
	 * @param parent, the parent as a "groupId:artifactId" string
	 * @return
	 */
	protected boolean isParentIgnored(MavenProject parent) {
		if (ignoredParent == null) {
			return false;
		}
		
		return ignoredParent.equals(parent.getGroupId() + ":" + parent.getArtifactId());
	}

	private Model addParent(Model model, String parentGroupId, String parentArtifactId, String parentVersion) {
		if (model == null) return null;

		Parent p = new Parent();
		p.setGroupId(parentGroupId);
		p.setArtifactId(parentArtifactId);
		p.setVersion(parentVersion);

		if (!(model.getGroupId()+":"+model.getArtifactId()).equals(parentGroupId+":"+parentArtifactId)) {
			model.setParent(p);
		}

		return model;
	}
	
	@Override
	protected File getOutputFile() {
		return new File(packageDirectory + File.separator + pomDeployFilename);
	}

	private void updateBuild(Model model) {
		// BWMaven plugin
		Plugin bwMavenPlugin = new Plugin();
		bwMavenPlugin.setGroupId(pluginDescriptor.getGroupId());
		bwMavenPlugin.setArtifactId(pluginDescriptor.getArtifactId());
		bwMavenPlugin.setVersion(pluginDescriptor.getVersion());
		bwMavenPlugin.setExtensions(true);
		
		// Maven clean plugin
		Plugin cleanPlugin = new Plugin();
		cleanPlugin.setArtifactId("maven-clean-plugin");
		cleanPlugin.setVersion("2.5");
		Xpp3Dom config = MojoExecutor.configuration(element("skip", "true"));
		cleanPlugin.setConfiguration(config);
		
		Build build = model.getBuild();
		if (build == null) {
			build = new Build();
			model.setBuild(build);
		}
		model.getBuild().setOutputDirectory(".");
		model.getBuild().addPlugin(bwMavenPlugin);
		model.getBuild().addPlugin(cleanPlugin);
	}

	private void generateDeployPOM(MavenProject project) throws MojoExecutionException {
		File outputFile = getOutputFile();
		
		getLog().info(GENERATING_POM_DEPLOY + "'" + outputFile.getAbsolutePath() + "'");
		
		try {
			outputFile.createNewFile();
			if (pomDeployTemplate != null && pomDeployTemplate.exists() && !pomDeployTemplateMerge) {
				FileUtils.copyFile(pomDeployTemplate, outputFile); // if a template deploy POM exists and we don't want to merge with built-in one: use it
			} else {
				// otherwise : use the one included in the plugin
				InputStream in;
				if (project.getPackaging().equals(POM_TYPE)) {
					in = this.getClass().getClassLoader().getResourceAsStream("deploy/pom/pom.xml");
				} else {
					in = this.getClass().getClassLoader().getResourceAsStream("deploy/ear/pom.xml");					
				}
				FileOutputStream fos = new FileOutputStream(outputFile);
				IOUtils.copy(in, fos);
			}
		} catch (IOException e) {
			throw new MojoExecutionException(FAILURE_POM_DEPLOY);
		}

		try {
			Model model = POMManager.getModelFromPOM(outputFile, this.getLog());
			if (pomDeployTemplate != null && pomDeployTemplate.exists() && pomDeployTemplateMerge) {
				model = POMManager.mergeModelFromPOM(pomDeployTemplate, model, this.getLog()); // if a template deploy POM exists and we want to merge with built-in one: merge it
			}

			model.setGroupId(project.getGroupId());
			model.setArtifactId(project.getArtifactId());
			model.setVersion(project.getVersion());

			if (project.getPackaging().equals(POM_TYPE)) {
				model.setArtifactId(project.getArtifactId() + getArtifactSuffix());

//				model.setPackaging(POM_TYPE);
//				model.getProperties().setProperty("generate.pom.skip", "true");
//				model.getProperties().setProperty("maven.deploy.skip", "true");
//				model.getProperties().setProperty("maven.install.skip", "true");

//				model = addDefaultProperties(model, project);

//				updateBuildForPOM(model);

				model = addModules(model, project);
			} else if (project.getPackaging().equals(BWEAR_TYPE)) {
//				model.setPackaging("bw-ear-deploy");

				File rootDeployPOM = updateProperties(model, project);

				if (rootDeployPOM != null) {
					File destinationFile = new File(project.getBuild().getDirectory() + File.separator + pomDeployFilename);
					FileUtils.copyFile(rootDeployPOM, destinationFile);
					getLog().debug("Copying deployment POM '" + rootDeployPOM.getAbsolutePath() + "' to: " + destinationFile.getAbsolutePath());
				}

				updateBuild(model);

				model = addParent(model, parentGroupId, parentArtifactId + getArtifactSuffix(), parentVersion);
			}

			POMManager.writeModelToPOM(model, outputFile, getLog());

			if (project.getPackaging().equals(POM_TYPE)) {
				if (model.getGroupId().equals(parentGroupId) &&
					model.getArtifactId().equals(parentArtifactId + getArtifactSuffix())) {
					copyToModules(outputFile, model, project);
				}
			}

			attachFile(outputFile, POM_TYPE, "deploy");
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (XmlPullParserException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

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
		Boolean skipParent = super.skip();
		if (skipParent || skipGeneratePom) {
			if (!skipParent) {
				getLog().info(SKIPPING);
			}

			File outputFile = getOutputFile();
           	if (outputFile != null && !outputFile.exists() && touchPOMDeployIfSkipped) {
           		// deployment POM was not created because packaging is skipped
           		// however we "touch" the deployment POM file so there is an empty deployment POM file created
           		try {
           			outputFile.getParentFile().mkdirs();
					outputFile.createNewFile();
				} catch (IOException e) {
					throw new MojoExecutionException(e.getLocalizedMessage(), e);
				}
           	}
			if (outputFile != null && outputFile.exists()) {
				attachFile(outputFile, POM_TYPE, "deploy");
			} else {
            	getLog().warn(WARN_NO_ARTIFACT_ATTACHED);
			}
			return;
		}

		if (!packageDirectory.exists()) {
			packageDirectory.mkdirs();
		}

		if (excludedModules == null) {
			excludedModules = new ArrayList<String>();
		}
		if (includedModules == null) {
			includedModules = new ArrayList<String>();
		}
		if (deployProperties == null) {
			deployProperties = new ArrayList<String>();
		}

		MavenProject project = getProject();
		if (useParent) {
			MavenProject parent = getProject().getParent();
			
			if (parent != null) {
				project = parent;
			} else {
				getLog().warn(WARN_NO_PARENT);
			}
		}
		
		generateDeployPOM(project);
	}

}
