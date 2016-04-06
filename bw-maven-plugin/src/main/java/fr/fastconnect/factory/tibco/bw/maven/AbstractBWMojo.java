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
package fr.fastconnect.factory.tibco.bw.maven;

import static org.apache.commons.io.FileUtils.copyFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.apache.commons.exec.launcher.CommandLauncher;
import org.apache.commons.exec.launcher.CommandLauncherFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;
import org.apache.tools.ant.taskdefs.optional.ReplaceRegExp;

import fr.fastconnect.factory.tibco.bw.maven.exception.BinaryMissingException;

/**
 * 
 * <p>
 * This abstract class is aimed at preparing a TIBCO build environment in order
 * to build the project specified in the POM project on a clean environment
 * which is absolutely independent of the current system.
 * 
 * It implies :
 * <ul>
 * <li>checking the BW project exists (TODO : and is correct)</li>
 * <li>creating a Designer5.prefs with actual file aliases referencing other
 * artifacts handled by Maven</li>
 * <li>creating a .designtimelibs referencing the dependencies declared in the
 * POM project</li>
 * </ul>
 * 
 * The concrete children classes will call the launchTIBCOBinary with specific
 * arguments (use 'buildear' or 'designer' or 'buildlibrary' or ...)
 * </p>
 * 
 * @author Mathieu Debove
 * 
 */
public class AbstractBWMojo extends AbstractMojo {

	protected final static String BWPROJECT_NOTFOUND = "The BusinessWorks project can't be found.";
	protected final static String BWPROJECT_PREF_LIB_ERROR_MSG = "The BusinessWorks project Designer5.prefs/.designtimelibs can't be copied.";
	protected final static String SKIPPING = "Skipping.";

	public final static String BWEAR_EXTENSION = ".ear";
	public final static String BWEAR_TYPE = "bw-ear";
	public final static String BWEAR_DEPLOY_TYPE = "bw-ear-deploy";
	protected final static String PROJLIB_EXTENSION = ".projlib";
	public final static String PROJLIB_TYPE = "projlib";
	protected final static String JAR_EXTENSION = ".jar";
	public final static String JAR_TYPE = "jar";

	protected final static String PROPERTIES_EXTENSION = ".properties";

	public final static String POM_TYPE = "pom";
	protected final static String POM_EXTENSION = ".xml";
	public final static String XML_TYPE = "xml";
	protected final static String XML_EXTENSION = ".xml";
	
	protected final static String TIBCO_HOME_DIR = ".TIBCO";
	protected final static String TIBCO_ALIAS_PREFIX = "tibco.alias.";
	protected final static String FILE_ALIAS_PREFIX = "filealias.pref.";
	protected final static String FILE_ALIAS_PREFIX_ESCAPED = "filealias\\.pref\\.";
	protected final static String ALIASES_FILE = "aliases.properties";
	protected final static String DTL_FILE_NAME = ".designtimelibs";
	protected final static String DESIGNER5_PREFS = "Designer5.prefs";

	/**
	 * Timeout for the execution of TIBCO commands to build artifacts.
	 * This time is given in seconds.
	 */
	@Parameter( property = "timeOut", defaultValue="180" )
	protected int timeOut;

	/**
	 * Path to the TIBCO home directory.
	 */
	@Parameter ( property = "tibco.home" )
	protected File tibcoHome;

	@Parameter (property="tibco.hawk.version")
	protected String hawkVersion;

	/*
	 * TRA configuration files
	 */

	/**
	 * Path to the TIBCO "AppManage" TRA configuration file.
	 */
	@Parameter ( property = "appmanage.tra.path" )
	protected File tibcoAppManageTRAPath;

	/**
	 * Path to the TIBCO "buildear" TRA configuration file.
	 */
	@Parameter( property = "buildear.tra.path" )
	protected File tibcoBuildEARTRAPath;
	
	/**
	 * Path to the BusinessWorks Engine TRA configuration file.
	 */
	@Parameter (property = "bwengine.tra.path", required = true)
	protected File tibcoBWEngineTRAPath;

	/**
	 * Path to the TIBCO Designer "buildlibrary" TRA configuration file.
	 */
	@Parameter( property = "buildlibrary.tra.path" )
	protected File tibcoBuildLibraryTRAPath;

	/**
	 * Path to the TIBCO Designer TRA configuration file.
	 */
	@Parameter( property = "designer.tra.path" )
	protected File tibcoDesignerTRAPath;

	/**
	 * Sometimes a TRA can reference another TRA, for instance 'buildear.tra'
	 * will reference 'designer.tra'.
	 * This field specifies whether we will use the default referenced TRA or
	 * override with a provided TRA. 
	 */
	@Parameter( property = "tra.buildear.uses.designer.tra.path" )
	protected boolean tibcoBuildEARUseDesignerTRA;

	/**
	 * Sometimes a TRA can reference another TRA, for instance
	 * 'buildlibrary.tra' will reference 'designer.tra'.
	 * This field specifies whether we will use the default referenced TRA or
	 * override with a provided TRA. 
	 */
	@Parameter( property = "tra.buildlibrary.uses.designer.tra.path" )
	protected boolean tibcoBuildLibraryUseDesignerTRA;

	/**
	 * Path to the TIBCO RendezVous folder.
	 */
	@Parameter( property = "tibrv.home.path" )
	protected File tibcoRvHomePath;

	/*
	 * TIBCO binaries 
	 */
	
	protected final static String APPMANAGE_BINARY_NOTFOUND = "The TIBCO AppManage binary can't be found.";
	protected final static String DESIGNER_BINARY_NOTFOUND = "The TIBCO Designer binary can't be found.";
	protected final static String HAWK_BINARY_NOTFOUND = "The TIBCO Hawk binary folder can't be found.";
	
	/**
	 * Path to the TIBCO "AppManage" binary.
	 */
	@Parameter( property = "appmanage.path" )
	protected File tibcoAppManagePath;

	protected void checkAppManage() throws MojoExecutionException {
		if (tibcoAppManagePath == null ||
		   !tibcoAppManagePath.exists() ||
		   !tibcoAppManagePath.isFile()) {
			tibcoAppManagePathNotFound();
		}
	}

	private void tibcoAppManagePathNotFound() throws MojoExecutionException {
		throw new BinaryMissingException(APPMANAGE_BINARY_NOTFOUND);
	}

	/**
	 * Path to the TIBCO Designer binary.
	 */
	@Parameter( property = "designer.path" )
	protected File tibcoDesignerPath;
	
	protected void checkDesigner() throws MojoExecutionException {
		if (tibcoDesignerPath == null ||
		   !tibcoDesignerPath.exists() ||
		   !tibcoDesignerPath.isFile()) {
			tibcoDesignerPathNotFound();
		}
	}

	private void tibcoDesignerPathNotFound() throws MojoExecutionException {
		throw new BinaryMissingException(DESIGNER_BINARY_NOTFOUND);
	}

	/**
	 * Allows to ignore any alias when building EAR or Projlib.
	 * 
	 * This will mimic the "Hide Library Resources" behaviour of the TIBCO
	 * Designer.
	 */
	@Parameter( property = "bw.hide.library.resources" )
	protected boolean hideLibraryResources;

	/**
	 * List of "groupId:artifactId" to ignore when building
	 */
	@Parameter
	protected ArrayList<String> dependenciesIgnored;

	/*
	 * Paths
	 */
	
	/**
	 * Path to the output folder.
	 * 
	 * Default is "target/"
	 */
	@Parameter( property = "project.build.directory",
				required=true,
				readonly=true )
	protected File directory;

	/**
	 * Path to the test folder.
	 * 
	 * Default is "target/test"
	 */
	@Parameter( property = "project.build.test.directory",
			    defaultValue="${project.build.directory}/test",
			    required=true )
	protected File testDirectory;
	
	/**
	 * Path to the dependencies (Projlibs, JARs...) for the test.
	 * 
	 * Default is "target/test/lib"
	 */
	@Parameter( property = "project.build.test.directory.lib",
			    defaultValue = "${project.build.test.directory}/lib",
			    required=true )
	protected File testLibDirectory;
	
	/**
	 * Path to the BusinessWorks sources for the test.
	 * 
	 * Default is "target/test/src"
	 */
	@Parameter( property = "project.build.test.directory.src",
		    	defaultValue = "${project.build.test.directory}/src",
		    	required = true )
	protected File testSrcDirectory;

	/**
	 * Path to the package folder.
	 * 
	 * Default is "target/package"
	 */
	@Parameter( property = "project.package.directory",
				defaultValue="${project.build.directory}/package",
				required=true)
	protected File packageDirectory;

	/**
	 * Directory containing the generated artifact.
	 */
	@Parameter( property = "project.build.outputDirectory", required=true )
	protected File outputDirectory;

	/**
	 * Path to the BusinessWorks project.
	 */
	@Parameter( property = "bw.project.location")
	protected File projectDirectory;

	/**
	 * Path to the dependencies (Projlibs, JARs...) for the build.
	 * 
	 * Default is "target/lib"
	 */
	@Parameter( property = "project.build.directory.lib",
			    defaultValue = "${project.build.directory}/lib",
			    required=true )
	protected File buildLibDirectory;

	/**
	 * Path to the BusinessWorks sources for the build.
	 * 
	 * Default is "target/src"
	 */
	@Parameter( property = "project.build.directory.src",
		    	defaultValue = "${project.build.directory}/src",
		    	required=true )
	protected File buildSrcDirectory;

	/*
	 * Hawk configuration (optional)
	 */

	@Parameter( property = "hawk.domain",
				defaultValue = "${tibco.domain.name}")
	protected String hawkDomain;

	@Parameter( property = "hawk.subscribe.interval",
				defaultValue = "10")
	protected Integer hawkSubscribeInterval; // in seconds

	@Parameter( property = "hawk.subscribe.retry.count",
				defaultValue = "30")
	protected Integer hawkSubscribeNumberOfRetry;
	
	@Parameter ( property = "hawk.rv.service",
				 defaultValue = "7474")
	protected String hawkRvService;

	@Parameter ( property = "hawk.rv.network",
				 defaultValue = ";")
	protected String hawkRvNetwork;
	
	@Parameter ( property = "hawk.rv.daemon",
			 	 defaultValue = "tcp:7474")
	protected String hawkRvDaemon;

	protected boolean initHawk(boolean failIfNotFound) throws BinaryMissingException {
		if (tibcoRvHomePath == null || !tibcoRvHomePath.exists()) {
			if (failIfNotFound) {
				throw new BinaryMissingException(HAWK_BINARY_NOTFOUND);
			} else {
				getLog().info("Unable to init Hawk.");
				return false;
			}
		}

		File tibrvj;
		if (SystemUtils.IS_OS_WINDOWS) {
			getLog().debug("Windows OS");
			tibcoRvHomePath = new File(tibcoRvHomePath, "bin/");
			tibrvj = new File(tibcoRvHomePath, "tibrvj.dll");
			System.load(tibrvj.getAbsolutePath());
		} else {
			getLog().debug("Not Windows OS");
			tibcoRvHomePath = new File(tibcoRvHomePath, "lib/");
			String osArch = System.getProperty("os.arch");
			tibrvj = null;
			if (osArch.equals("x86")) {
				getLog().debug("x86");
				tibrvj = new File(tibcoRvHomePath, "libtibrvj.so");
				System.loadLibrary("tibrvj");
			} else if (osArch.contains("64")) {
				getLog().debug("64");
				tibrvj = new File(tibcoRvHomePath, "libtibrvj64.so");
				System.loadLibrary("tibrvj64");
			}
		}
		getLog().debug("Loading system library : " + tibrvj.getAbsolutePath());

		return true;
	}

	/*
	 * Properties
	 */
	public static final Pattern mavenPropertyPattern = Pattern.compile("\\$\\{([^}]*)\\}"); // ${prop} regex pattern

	/**
	 * <p>
	 * Instantiate a minimalistic {@link AbstractCommonMojo} to use properties
	 * management as a standalone object.
	 * </p>
	 *
	 * @param session
	 * @param mavenProject
	 * @return
	 */
	public static AbstractBWMojo propertiesManager(MavenSession session, MavenProject mavenProject) {
		AbstractBWMojo mojo = new AbstractBWMojo();
		mojo.setProject(mavenProject);
		mojo.setSession(session);
		mojo.setSettings(session.getSettings());

		return mojo;
	}

	@SuppressWarnings("unchecked") // because of Maven poor typing
	public String getPropertyValueInSettings(String propertyName, Settings settings) {
		if (settings == null) {
			return null;
		}

		List<String> activeProfiles = settings.getActiveProfiles();

		for (Object _profileWithId : settings.getProfilesAsMap().entrySet()) {
			Entry<String, Profile> profileWithId = (Entry<String, Profile>) _profileWithId;
			if (activeProfiles.contains(profileWithId.getKey())) {
				Profile profile = profileWithId.getValue();

				String value = profile.getProperties().getProperty(propertyName);
				if (value != null) {
					return value;
				}
			}
		}

		return null;
	}

	private List<String> getActiveProfiles(Settings settings) {
		if (settings == null) return null;

		List<String> result = settings.getActiveProfiles();

		for (org.apache.maven.settings.Profile profile : settings.getProfiles()) {
			if (profile.getActivation().isActiveByDefault() && !result.contains(profile.getId())) {
				result.add(profile.getId());
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked") // because of Maven poor typing
	public boolean propertyExistsInSettings(String propertyName, Settings settings) {
		if (settings == null) {
			return false;
		}

		List<String> activeProfiles = getActiveProfiles(settings);

		for (Object _profileWithId : settings.getProfilesAsMap().entrySet()) {
			Entry<String, Profile> profileWithId = (Entry<String, Profile>) _profileWithId;
			if (activeProfiles.contains(profileWithId.getKey())) {
				Profile profile = profileWithId.getValue();

				boolean result = profile.getProperties().containsKey(propertyName);
				if (result) {
					return result;
				}
			}
		}

		return false;
	}

	private String getPropertyValueInCommandLine(String propertyName, MavenSession session) {
		if (session == null) {
			return null;
		}

		return session.getRequest().getUserProperties().getProperty(propertyName);
	}

	public boolean propertyExistsInSettings(String propertyName) {
		return propertyExistsInSettings(propertyName, session.getSettings());
	}

	public boolean propertyExists(String propertyName) {
		return propertyExists(project, propertyName);
	}

	public boolean propertyExists(MavenProject mavenProject, String propertyName) {
		return mavenProject.getOriginalModel().getProperties().containsKey(propertyName) ||
			   mavenProject.getModel().getProperties().containsKey(propertyName) ||
			   session.getRequest().getUserProperties().containsKey(propertyName) ||
			   propertyExistsInSettings(propertyName, session.getSettings());
	}

	public String getPropertyValue(MavenProject mavenProject, String propertyName, boolean lookInSettingsProperties, boolean lookInCommandLine, boolean onlyInOriginalModel) {
		if (mavenProject == null) return null;

		String result = null;

		if (onlyInOriginalModel) {
			result = mavenProject.getOriginalModel().getProperties().getProperty(propertyName);
		} else {
			result = mavenProject.getModel().getProperties().getProperty(propertyName);
		}
		if (lookInCommandLine && (result == null || result.isEmpty())) {
			result = getPropertyValueInCommandLine(propertyName, session);
		}
		if (lookInSettingsProperties && (result == null || result.isEmpty())) {
			result = getPropertyValueInSettings(propertyName, settings);
		}

		return result;
	}

	public String getPropertyValue(String propertyName, boolean onlyInOriginalModel) {
		return getPropertyValue(project, propertyName, true, true, onlyInOriginalModel);
	}

	public String getPropertyValue(String propertyName) {
		return getPropertyValue(propertyName, false);
	}

	public String getRootProjectProperty(MavenProject mavenProject, String propertyName) {
		return mavenProject == null ? "" : (mavenProject.getParent() == null ? getPropertyValue(mavenProject, propertyName, false, false, false) : getRootProjectProperty(mavenProject.getParent(), propertyName));
	}

	public String getRootProjectProperty(MavenProject mavenProject, String propertyName, boolean onlyInOriginalModel) {
		return mavenProject == null ? "" : (mavenProject.getParent() == null ? getPropertyValue(mavenProject, propertyName, false, false, onlyInOriginalModel) : getRootProjectProperty(mavenProject.getParent(), propertyName, onlyInOriginalModel));
	}

	public String getPropertyValue(String modelPropertyName, boolean propertyInRootProject, boolean onlyInOriginalModel, boolean lookInSettings) {
		String value = null;
		if (lookInSettings) {
			value = getPropertyValueInSettings(modelPropertyName, settings);
		}
		if (value == null) {
			if (propertyInRootProject) {
				value = getRootProjectProperty(project, modelPropertyName, onlyInOriginalModel);
			} else {
				value = getPropertyValue(modelPropertyName, onlyInOriginalModel);
			}
		}
		return value;
	}

	public String replaceProperties(String string) {
		if (string == null) return null;

		Matcher m = mavenPropertyPattern.matcher(string);

		StringBuffer sb = new StringBuffer();

		while (m.find()) {
			String propertyName = m.group(1);
			String propertyValue = getPropertyValue(propertyName);
			if (propertyValue != null) {
			    m.appendReplacement(sb, Matcher.quoteReplacement(propertyValue));
			}
		}
		m.appendTail(sb);
		string = sb.toString();

		return string;
	}
	//

	/*
	 * Maven
	 */

	/**
	 * The Maven project.
	 */
	@Parameter ( property="project", 
			     required=true, 
			     readonly=true)
	private MavenProject project;

	protected MavenProject getProject() {
		return project;
	}

	public void setProject(MavenProject project) {
		this.project = project;
	}

	/**
	 * The current Maven session.
	 *
	 */
	@Parameter ( property="session", 
		     	required=true, 
		     	readonly=true)
	protected MavenSession session;
	protected final MavenSession getSession() {
		return session;
	}
	public void setSession(MavenSession session) {
		this.session = session;
	}

	@Parameter (defaultValue = "${settings}", readonly = true)
	private Settings settings;

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	/**
	 * The source enconding.
	 */
	@Parameter ( property="project.build.sourceEncoding", 
			     required=true, 
			     readonly=true)
	protected String sourceEncoding;

	protected boolean isCurrentGoal(String goal) {
		return getSession().getRequest().getGoals().contains(goal);
	}


	/*
	 * BW project
	 */

	/**
	 * This will check that the BW project specified in the POM project exists.
	 * 
	 * TODO : add additional checks about BW project integrity
	 * 
	 * @throws MojoExecutionException
	 */
	private void checkBWProject() throws MojoExecutionException {
		if (projectDirectory == null) {
			projectNotFound();
		} else if (!projectDirectory.exists() || !projectDirectory.isDirectory()) {
			projectNotFound();
		}
	}

	/**
	 * This will throw an error if the BW project is not found.
	 * 
	 * @throws MojoExecutionException
	 */
	private void projectNotFound() throws MojoExecutionException {
		throw new MojoExecutionException(BWPROJECT_NOTFOUND, new FileNotFoundException());
	}

	/*
	 * Dependencies
	 */
	
	/**
	 * 
	 * @param bwEARDependency, a BW EAR dependency from Maven point-of-view
	 * @return the name of the BW EAR artifact (without path but with
	 * extension) : artifactId-version.ear
	 */
	protected String getBWEARName(Dependency bwEARDependency) {
		assert (bwEARDependency != null);

		return bwEARDependency.getArtifactId() + "-" + bwEARDependency.getVersion() + BWEAR_EXTENSION;
	}

	/**
	 * 
	 * @param bwEARDependency, a BW EAR dependency from Maven point-of-view
	 * @return groupId:artifactId:version:bw-ear of the BW EAR dependency
	 */
	protected String getBWEARAlias(Dependency bwEARDependency) {
		assert (bwEARDependency != null);

		return bwEARDependency.getGroupId() + ":" + bwEARDependency.getArtifactId() + ":" + bwEARDependency.getVersion() + ":" + BWEAR_TYPE;
	}

	/**
	 * 
	 * @param jarDependency, a JAR dependency from Maven point-of-view
	 * @param replaceDot, allows to replace dots in the version of the artifact
	 * by underscores. This is because Maven will do so on the generated
	 * artifact.
	 * 
	 * @return the name of the Jar artifact (without path but with extension) :
	 * artifactId-version.jar
	 */
	protected String getJarName(Dependency jarDependency, boolean replaceDot) {
		assert (jarDependency != null);
		String version = jarDependency.getVersion();
		if (replaceDot) {
			version = version.replace('.', '_');
		}

		return jarDependency.getArtifactId() + "-" + version + JAR_EXTENSION;
	}

	/**
	 * 
	 * @param jarDependency, a JAR dependency from Maven point-of-view
	 * @param replaceDot, allows to replace dots in the version of the artifact
	 * by underscores. This is because Maven will do so on the generated
	 * artifact.
	 * 
	 * @return groupId:artifactId:version:jar of the JAR dependency
	 */
	protected String getJarAlias(Dependency jarDependency, boolean replaceDot) {
		assert (jarDependency != null);
		String version = jarDependency.getVersion();
		if (replaceDot) {
			version = version.replace('.', '_');
		}

		return jarDependency.getGroupId() + ":" + jarDependency.getArtifactId() + ":" + version + ":" + JAR_TYPE;
	}

	/**
	 * 
	 * @param projlibDependency, a Projlib dependency from Maven point-of-view
	 * @return the name of the Projlib artifact (without path but with
	 * extension) : artifactId-version.projlib
	 */
	protected String getProjlibName(Dependency projlibDependency) {
		assert (projlibDependency != null);

		return projlibDependency.getArtifactId() + "-" + projlibDependency.getVersion() + PROJLIB_EXTENSION;
	}

	/**
	 * 
	 * @param projlibDependency, a Projlib dependency from Maven point-of-view
	 * @return groupId:artifactId:version:projlib of the Projlib dependency
	 */
	protected String getProjlibAlias(Dependency projlibDependency) {
		assert (projlibDependency != null);

		return projlibDependency.getGroupId() + ":" + projlibDependency.getArtifactId() + ":" + projlibDependency.getVersion() + ":" + PROJLIB_TYPE;
	}

	/**
	 * 
	 * @param dependency, a dependency from Maven point-of-view, retrieved with
	 * getJarName or getProjlibName
	 * @return the absolute path of the dependency file (usually in "target/lib")
	 */
	protected String getDependencyPath(String dependencyName) {
		return buildLibDirectory.getAbsolutePath().replace('\\', '/') + "/" + dependencyName;
	}

	/**
	 * This will read the dependencies from the 'resolved' file found in the
	 * build directory. This file was created earlier in the build by the
	 * 'resolve-bw-dependencies' execution of the 'process-resources' phase.
	 * 
	 * @return The list of dependencies of type {@link dependencyType}
	 * @throws IOException
	 */
	protected List<Dependency> readDependenciesFromFile(String resolvedFileName, String dependencyType) throws IOException {
		List<Dependency> dependencies = new ArrayList<Dependency>();

		File resolvedFile = new File(resolvedFileName);
		if (!resolvedFile.exists()) {
			return dependencies;
		}

		FileInputStream fstream = new FileInputStream(resolvedFile);
		DataInputStream ds = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(ds));
		Pattern p = Pattern.compile("   (.*):(.*):" + dependencyType + ":(.*):(.*)"); // keep only selected type (projlib or jar or bw-ear) dependencies
		String strLine;
		while ((strLine = br.readLine()) != null) {
			Matcher m = p.matcher(strLine);
			if (m.matches()) {
				getLog().debug(m.group(0));

				String groupId = m.group(1);
				String artifactId = m.group(2);
				String version = m.group(3);
				String scope = m.group(4);

				// create the dependency
				Dependency dependency = new Dependency();
				dependency.setGroupId(groupId);
				dependency.setArtifactId(artifactId);
				dependency.setVersion(version);
				dependency.setType(dependencyType);
				dependency.setScope(scope);

				dependencies.add(dependency);
			}
		}
		br.close();

		return dependencies;
	}

	/**
	 * This will retrieve only the dependencies of type dependencyType defined
	 * in POM project as /dependencies/dependency/type="dependencyType".
	 * 
	 * The list is retrieved from an external file generated by the
	 * 'resolve-bw-dependencies' execution of the 'process-resources' phase.
	 * 
	 * @param dependencyType, the type of dependencies to retrieve
	 * @param doIgnoreDependencies, specifies if the dependencies in the 
	 * {@link dependenciesIgnored} must be ignored
	 * @return
	 * @throws IOException
	 */
	protected List<Dependency> getDependencies(String dependencyType, boolean doIgnoreDependencies) throws IOException {
		ArrayList<Dependency> result = new ArrayList<Dependency>();
		List<Dependency> dependencies = readDependenciesFromFile(directory + "/resolved", dependencyType);
		
		for (Dependency dependency : dependencies) {
			if (doIgnoreDependencies) {
				if (dependenciesIgnored.indexOf(dependency.getGroupId() + ':' + dependency.getArtifactId()) >= 0) {
					continue;
				}
			}
			if (dependency.getType().equals(dependencyType)) {
				result.add(dependency);
			}
		}

		return result;
	}

	/**
	 * @return the list of BW EARs Dependencies from the POM project
	 * @throws IOException 
	 */
	protected List<Dependency> getBWEARsDependencies() throws IOException {
		return getDependencies(BWEAR_TYPE, false);
	}

	/**
	 * @return the list of Projlib Dependencies from the POM project
	 * @throws IOException 
	 */
	protected List<Dependency> getProjlibsDependencies() throws IOException {
		return getDependencies(PROJLIB_TYPE, false);
	}

	/**
	 * @return the list of Jar Dependencies from the POM project
	 * @throws IOException 
	 */
	protected List<Dependency> getJarDependencies() throws IOException {
		return getDependencies(JAR_TYPE, true);
	}

	/**
	 * Some configuration file must escape the ':' character of Maven
	 * dependency coordinates syntax.
	 * 
	 * @param alias
	 * @return alias but with '\\:' replaced by ':'
	 */
	private String formatAlias(String alias) {
		if (alias != null) {
			return alias.replace(":", "\\:");
		}
		return null;
	}
	
	public File getAliasesFile() {
		return new File(directory, ALIASES_FILE);
	}
	
	/**
	 * This will create an aliases file ('aliases.properties') that can be
	 * provided to 'buildear' for instance to specify the JAR aliases.
	 * It seems that the JAR aliases are not recognized by 'buildear' from
	 * 'Designer5.prefs' whereas they are by TIBCO Designer.
	 * 
	 * @throws IOException
	 */
	private void copyAliasesFile() throws IOException {
		// Create the aliases.properties in the target folder
		File aliasesFile = getAliasesFile();
		FileWriter file = null;
		BufferedWriter buffer = null;
		PrintWriter aliasesFileOut = null;
		try {
			file = new FileWriter(aliasesFile);
			buffer = new BufferedWriter(file);
			aliasesFileOut = new PrintWriter(buffer);
	
			// Copy all of the required aliases for the project, and map them with their path in Designer5.prefs
			for (Dependency dependency : getJarDependencies()) {
				String jarName = getJarName(dependency, false);
				String jarAlias = formatAlias(getJarAlias(dependency, false));
				String jarPath = getDependencyPath(jarName); 
	
				if (new File(jarPath).exists()) {
					aliasesFileOut.println(TIBCO_ALIAS_PREFIX + jarAlias + "=" + jarPath);
				}
			}
	
			for (Dependency dependency : getProjlibsDependencies()) {
				String projlibName = getProjlibName(dependency);
				String projlibAlias = formatAlias(getProjlibAlias(dependency));
				String projlibPath = getDependencyPath(projlibName); 
	
				aliasesFileOut.println(TIBCO_ALIAS_PREFIX + projlibAlias + "=" + projlibPath);
			}
		} finally {
			aliasesFileOut.close();
			buffer.close();
			file.close();
		}
	}

	protected File getDesigner5Prefs() throws IOException {
		// create a ".TIBCO" user_dir-like in build directory
		File homeTIBCO = new File(directory, TIBCO_HOME_DIR);
		if (!homeTIBCO.exists()) {
			homeTIBCO.mkdir();
		}

		File designer5Prefs = new File(homeTIBCO, DESIGNER5_PREFS);

		return designer5Prefs;
	}

	/**
	 * This will create the 'Designer5.prefs' file in 'target/.TIBCO' which will
	 * override the platform TIBCO_HOME directory.
	 * 
	 * The content of the 'Designer5.prefs' will be a copy of the original
	 * 'Designer5.prefs' file found on the current system (in the user home dir)
	 * However all the references to alias files will be removed and replaced by
	 * the actual alias files referencing Maven artifacts
	 * 
	 * @throws IOException
	 */
	private void copyDesigner5Prefs() throws IOException {
		File designer5Prefs = getDesigner5Prefs();

		// copy system 'Designer5.prefs' to this ".TIBCO" directory
		File systemDesigner5Prefs = new File(System.getProperty("user.home") + "/" + TIBCO_HOME_DIR + "/" + DESIGNER5_PREFS);
		getLog().debug(DESIGNER5_PREFS + " : " + systemDesigner5Prefs.getAbsolutePath());
		if (systemDesigner5Prefs.exists()) {
			copyFile(systemDesigner5Prefs, designer5Prefs);
		} else {
			designer5Prefs.createNewFile();
		}

		// remove file aliases
		ReplaceRegExp replaceRegExp = new ReplaceRegExp();
		replaceRegExp.setFile(designer5Prefs);
		replaceRegExp.setMatch(FILE_ALIAS_PREFIX_ESCAPED + "([0-9]*)=(.*)");
		replaceRegExp.setReplace("");
		replaceRegExp.setByLine(true);
		replaceRegExp.execute();

		// replace with actual file aliases (which are Maven artifacts)
		FileWriter file = null;
		BufferedWriter buffer = null;
		PrintWriter designer5PrefsOut = null;
		try {
			file = new FileWriter(designer5Prefs, true);
			buffer = new BufferedWriter(file);
			designer5PrefsOut = new PrintWriter(buffer);
			designer5PrefsOut.println("");

			// first the Projlibs aliases
			int i = 0;
			if (!hideLibraryResources) { // implements the "Hide Library Resources" of TIBCO Designer

				for (Dependency dependency : getProjlibsDependencies()) {
					String projlibName = getProjlibName(dependency);
					String projlibAlias = getProjlibAlias(dependency);
					String projlibPath = getDependencyPath(projlibName);
					designer5PrefsOut.println(FILE_ALIAS_PREFIX + (i++) + "=" + projlibAlias + "\\=" + projlibPath);
				}
			}

			// then the Jar aliases
			for (Dependency dependency : getJarDependencies()) {
				String jarName = getJarName(dependency, false);
				String jarAlias = getJarAlias(dependency, false);
				String jarPath = getDependencyPath(jarName);
				// String jarNameUnderscored = getJarName(dependency, true);
				designer5PrefsOut.println(FILE_ALIAS_PREFIX + (i++) + "=" + jarAlias + "\\=" + jarPath);
			}
		} finally {
			designer5PrefsOut.close();
			buffer.close();
			file.close();
		}
	}

	/**
	 * This will create the '.designtimelibs' file in {@link buildSrcDirectory}
	 * which is basically the path to the temporary BusinessWorks project being
	 * built.
	 * 
	 * The content of the '.designtimelibs' will be the Projlib dependencies of
	 * the project, defined in the POM project and resolved with classic Maven
	 * mechanism.
	 * 
	 * @throws IOException
	 */
	private void copyDesignTimeLibs() throws IOException {
		File designTimeLibs = new File(buildSrcDirectory + "/" + DTL_FILE_NAME);
		getLog().debug(DTL_FILE_NAME + " : " + buildSrcDirectory + "/" + DTL_FILE_NAME);
		
		if (!designTimeLibs.exists()) {
			if (buildSrcDirectory.exists()) {
				designTimeLibs.createNewFile();
			} else {
				return;
			}
		}

		FileWriter file = null;
		BufferedWriter buffer = null;
		PrintWriter designTimeLibsOut = null;
		try {
			file = new FileWriter(designTimeLibs, false);
			buffer = new BufferedWriter(file);
			designTimeLibsOut = new PrintWriter(buffer);
			
			int i = getProjlibsDependencies().size();
			if (!hideLibraryResources) {
				for (Dependency dependency : getProjlibsDependencies()) {
					//String projlibName = getProjlibName(dependency);
					String projlibAlias = getProjlibAlias(dependency);
					designTimeLibsOut.println((--i) + "=" + projlibAlias + "\\=");
				}
			}
		} finally {
			designTimeLibsOut.close();
			buffer.close();
			file.close();
		}
	}

	/*
	 * Execution
	 */
	
	/**
	 * This calls a TIBCO binary.
	 * 
	 * @param binary, the TIBCO binary file to execute
	 * @param tras, the TRA files associated with the TIBCO binary
	 * @param arguments, command-line arguments
	 * @param workingDir, working directory from where the binary is launched
	 * @param errorMsg, error message to display in case of a failure
	 * @param fork, if true the chiild process will be detached from the caller
	 * 
	 * @throws IOException
	 * @throws MojoExecutionException
	 */
	protected int launchTIBCOBinary(File binary, List<File> tras, ArrayList<String> arguments, File workingDir, String errorMsg, boolean fork, boolean synchronous) throws IOException, MojoExecutionException {
		Integer result = 0;
		
		if (tras == null) { // no value specified as Mojo parameter, we use the .tra in the same directory as the binary
			String traPathFileName = binary.getAbsolutePath();
			traPathFileName = FilenameUtils.removeExtension(traPathFileName);
			traPathFileName += ".tra";
			tras = new ArrayList<File>();
			tras.add(new File(traPathFileName));
		}

		HashMap<File, File> trasMap = new HashMap<File, File>();
		for (File tra : tras) {
			// copy of ".tra" file in the working directory
			File tmpTRAFile = new File(directory, tra.getName());
			trasMap.put(tra, tmpTRAFile);
			copyFile(tra, tmpTRAFile);
		}

		for (File tra : trasMap.keySet()) {
			if (trasMap.containsKey(tibcoDesignerTRAPath)
					&& ((tibcoBuildEARUseDesignerTRA && tra == tibcoBuildEARTRAPath) || (tibcoBuildLibraryUseDesignerTRA && tra == tibcoBuildLibraryTRAPath))) {
				if (tras.size() > 1) {
					ReplaceRegExp replaceRegExp = new ReplaceRegExp();
					replaceRegExp.setFile(trasMap.get(tra));
					replaceRegExp.setMatch("tibco.include.tra (.*/designer.tra)");
					replaceRegExp.setReplace("tibco.include.tra " + trasMap.get(tibcoDesignerTRAPath).toString().replace('\\', '/'));
					replaceRegExp.setByLine(true);

					replaceRegExp.execute();
				}
			}

			if (tra == tibcoBuildEARTRAPath ||
				tra == tibcoDesignerTRAPath ||
				tra == tibcoBWEngineTRAPath) { // FIXME: should check more properly
				// append user.home at the end to force the use of custom Designer5.prefs
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(trasMap.get(tra), true)));
				out.println("");
				out.println("java.property.user.home=" + directory.getAbsolutePath().replace("\\", "/"));
				out.close();
			}
		}

		CommandLine cmdLine = new CommandLine(binary);

		for (String argument : arguments) {
			cmdLine.addArgument(argument);
		}
		getLog().debug("launchTIBCOBinary command line : " + cmdLine.toString());
		getLog().debug("working dir : " + workingDir);
		
		DefaultExecutor executor = new DefaultExecutor();
		executor.setWorkingDirectory(workingDir);
		
		if (timeOut > 0) {
			ExecuteWatchdog watchdog = new ExecuteWatchdog(timeOut * 1000);
			executor.setWatchdog(watchdog);
		}

		executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
		
		ByteArrayOutputStream stdOutAndErr = new ByteArrayOutputStream();
		executor.setStreamHandler(new PumpStreamHandler(stdOutAndErr));

		if (fork) {
			CommandLauncher commandLauncher = CommandLauncherFactory.createVMLauncher();
			commandLauncher.exec(cmdLine, null, workingDir);
		} else {
			try {
				if (synchronous) {
					result = executor.execute(cmdLine);
				} else {
					executor.execute(cmdLine, new DefaultExecuteResultHandler());					
				}
			} catch (ExecuteException e) {
				// TODO : gérer erreurs des exécutables (éventuellement parser les erreurs classiques)
				getLog().info(cmdLine.toString());
				getLog().info(stdOutAndErr.toString());
				getLog().info(result.toString());
				throw new MojoExecutionException(errorMsg, e);
			} catch (IOException e) {
				throw new MojoExecutionException(e.getMessage(), e);
			}
		}

		return result;
	}

	/**
	 * Same as launchTIBCOBinary with 'fork=false' and 'synchronous=true'
	 */
	protected void launchTIBCOBinary(File binary, List<File> tras, ArrayList<String> arguments, File workingDir, String errorMsg) throws IOException, MojoExecutionException {
		launchTIBCOBinary(binary, tras, arguments, workingDir, errorMsg, false, true);
	}

	protected void enableTestScope() {
		directory = testDirectory; // set directory to "target/test" instead of "target"
		buildSrcDirectory = testSrcDirectory; // set buildSrcDirectory to "target/test/src" instead of "target/src"
		buildLibDirectory = testLibDirectory; // set buildLibDirectory to "target/test/lib" instead of "target/lib"
	}

	/**
	 * <p>
	 * The execute method of this Mojo will :
	 * <ul>
	 * <li>check that the BusinessWorks project exists</li>
	 * <li>copy the Designer5.prefs file</li>
	 * <li>copy the .designtimelibs file useful for setting a TIBCO environment
	 * for the project being built.</li>
	 * </ul>
	 * </p>
	 */
	public void execute() throws MojoExecutionException {
		if (dependenciesIgnored == null) {
			dependenciesIgnored = new ArrayList<String>();
		}

		if (!directory.exists()) {
			directory.mkdirs();
		}

		checkBWProject();
		try {
			copyAliasesFile();
			copyDesigner5Prefs();
			copyDesignTimeLibs();
		} catch (IOException e) {
			e.printStackTrace(); // FIXME : remove printStackTrace()
		}
	}

}
