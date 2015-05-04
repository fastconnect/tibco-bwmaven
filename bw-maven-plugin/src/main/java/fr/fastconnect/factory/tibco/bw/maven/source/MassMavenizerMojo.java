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
package fr.fastconnect.factory.tibco.bw.maven.source;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import fr.fastconnect.factory.tibco.bw.maven.AbstractBWMojo;

/**
 * <p>
 * This goal "mavenizes" a list of existing TIBCO BusinessWorks projects.<br />
 * The Mavenizer is the default "Mavenizer Archetype" (archetype-bw-mavenizer).
 * <br/>
 * </p>
 * 
 * <p>
 * Refer to <a href="./list-bw-projects-mojo.html">bw:list-bw-projects</a> goal
 * for an explanation about projects lists.
 * </p>
 * 
 * @author Mathieu Debove
 * 
 */
@Mojo(name = "mavenize-bw-projects", requiresProject = false)
public class MassMavenizerMojo extends ExistingBWProjectsListMojo {

	@Parameter ( property = "archetypeGroupId", defaultValue = "fr.fastconnect.factory.tibco.bw.maven", required = false)
	protected String archetypeGroupId;

	@Parameter ( property = "archetypeArtifactId", defaultValue = "archetype-bw-mavenizer", required = false)
	protected String archetypeArtifactId;

	@Parameter ( property = "archetypeVersion", required = false)
	protected String archetypeVersion;

	@Parameter ( property = "archetypeCatalog", required = false)
	protected String archetypeCatalog;

	@Parameter ( property = "archetypeRepository", required = false)
	protected String archetypeRepository;

	@Parameter ( property = "archetypeGoals", defaultValue = "antrun:run", required = false)
	protected String archetypeGoals;

	@Parameter
	protected Properties archetypeProperties;

	/**
	 * Whether the project to create is a Projlib or not.
	 */
	@Parameter ( required = false, property = "isProjlib", defaultValue="false" )
	protected boolean isProjlib;

	/**
	 * The groupId of the projects to mavenize
	 */
	@Parameter ( required = false, property = "projectGroupId", defaultValue = "${rootProjectGroupId}" )
	protected String projectGroupId;
	
	/**
	 * The version of the projects to mavenize
	 */
	@Parameter ( required = false, property = "projectVersion", defaultValue = "1.0.0")
	protected String projectVersion;

	/**
	 * The parent.groupId of the project to mavenize
	 */
	@Parameter ( required = true, property = "rootProjectGroupId" )
	protected String rootProjectGroupId;

	/**
	 * The parent.artifactId of the project to mavenize
	 */
	@Parameter ( required = true, property = "rootProjectArtifactId" )
	protected String rootProjectArtifactId;
	
	/**
	 * The parent.version of the project to mavenize
	 */
	@Parameter ( required = true, property = "rootProjectVersion" )
	protected String rootProjectVersion;

	protected final static String MAVENIZATION_FAILURE = " project failed the mavenization.";
	protected final static String MAVENIZATION_FAILURES = " projects failed the mavenization.";
	protected final static String MAVENIZATION_SUCCESS = " project was successfully mavenized.";
	protected final static String MAVENIZATION_SUCCESSES = " projects were successfully mavenized.";

	// Environment configuration
	/**
	 * The project currently being build.
	 *
	 */
	@Parameter(property="project", required=true, readonly=true)
	protected MavenProject project;

	/**
	 * The current Maven session.
	 *
	 */
	@Parameter(property="session", required=true, readonly=true)
	protected MavenSession session;

	/**
	 * The Build Plugin Manager
	 */
	@Component (role = BuildPluginManager.class)
	protected BuildPluginManager pluginManager;

	/**
	 * 
	 * @param configuration, a list of "-D" properties
	 * @return the configuration as a List<Element> used by MojoExecutor
	 */
	private List<Element> generateConfiguration(Properties configuration) {
		List<Element> configurations = new ArrayList<Element>();
				
		if (configuration != null) {
			for (Object key : configuration.keySet()) {
				String value = configuration.get(key).toString();
				getLog().debug(value);
				configurations.add(element(key.toString(), value));
			}
		}

		return configurations;
	}

	protected ExecutionEnvironment getEnvironment() {
		return executionEnvironment(project, session, pluginManager);
	}

	@SuppressWarnings("deprecation") // this can't be avoided because 'org.apache.maven.plugins:maven-archetype-plugin:2.2:generate' uses this method
	@Override
	protected boolean performAction(AbstractProject p) {
		try {
			Properties configuration = new Properties();

			configuration.put("basedir", p.root + File.separator + p.getRelativePath());
			configuration.put("archetypeGroupId", archetypeGroupId);
			configuration.put("archetypeArtifactId", archetypeArtifactId);

			if (archetypeVersion != null) {
				configuration.put("archetypeVersion", archetypeVersion);
			}
			if (archetypeCatalog != null) {
				configuration.put("archetypeCatalog", archetypeCatalog);
			}
			if (archetypeRepository != null) {
				configuration.put("archetypeRepository", archetypeRepository);
			}

			configuration.put("goals", archetypeGoals);
			configuration.put("interactiveMode", false);

			archetypeProperties = initProperties(p);
			
			for (Object s : archetypeProperties.keySet()) {
				String key = (String) s;
				String value = archetypeProperties.getProperty(key);
				
				session.getExecutionProperties().put(key, value);				
			}
			
			executeMojo(
	                plugin(
	                    groupId("org.apache.maven.plugins"),
	                    artifactId("maven-archetype-plugin"),
	                    version("2.2")
	                ),
	                goal("generate"),
	                configuration(
	                	generateConfiguration(configuration).toArray(new Element[0])
	                ),
	                getEnvironment()
	            );		

		} catch (Exception e) {
			getLog().warn("Mavenization failed : " + e.getLocalizedMessage());
			return false;
		}

		return true;
	}

	@Override
	protected HashMap<String, String> updateProjects(HashMap<String, String> m) {
		assert(m != null);
		m.put("bwProjectLocation", bwProjectLocation);
		m.put("groupId", projectGroupId);
		m.put("version", projectVersion);
		return m;
	}

	private String getRootProjectPath(AbstractProject p) {
		String result = "";

		Model parentModel = new Model();
		parentModel.setGroupId(rootProjectGroupId);
		parentModel.setArtifactId(rootProjectArtifactId);
		parentModel.setVersion(rootProjectVersion);
		parentModel.setPackaging("pom");
		
		try {
			File parent = getParent(parentModel, new File(p.getOriginalAbsolutePath()).getParentFile(), getLog());
			if (parent != null) {
				result = parent.getAbsolutePath();
			}
		} catch (Exception e) {
		}
		
		return result;
	}

	@Override
	protected void displayRootProject(AbstractProject p) {
		super.displayRootProject(p); 
		getLog().info("  The root project is           : " +
				rootProjectGroupId + ":" +
				rootProjectArtifactId + ":" +
				rootProjectVersion
				);
		getLog().info("  The root project location is  : " +
				getRootProjectPath(p)
				);
	}

	private Properties initProperties(AbstractProject p) {
		if (archetypeProperties == null) {
			archetypeProperties = new Properties();			
		}

		archetypeProperties.put("rootProjectGroupId", rootProjectGroupId);
		archetypeProperties.put("rootProjectArtifactId", rootProjectArtifactId);
		archetypeProperties.put("rootProjectVersion", rootProjectVersion);

		String s = AbstractProjectsListMojo.getRelativePath(p.getAbsolutePath(), getRootProjectPath(p), File.separator);
		String rootRelativePath = "../..";
		for (int i = StringUtils.countMatches(s, File.separator); i > 0; i--) {
			rootRelativePath += "/..";
		}
		archetypeProperties.put("rootRelativePath", rootRelativePath);
		
		if (isProjlib) {
			archetypeProperties.put("projectPackaging", AbstractBWMojo.PROJLIB_TYPE);
		} else {
			archetypeProperties.put("projectPackaging", AbstractBWMojo.BWEAR_TYPE);
		}
		
		archetypeProperties.put("bwProjectName", p.getProjectName());
		archetypeProperties.put("groupId", projectGroupId);                 
		archetypeProperties.put("artifactId", p.getArtifactId());
		if (projectVersion == null || projectVersion.isEmpty()) {
			projectVersion = "[inherited]";
			archetypeProperties.put("versionIsInherited", "true");
		}
		archetypeProperties.put("version", projectVersion);                 
		if (bwProjectLocation == null) {
			bwProjectLocation = "";
			archetypeProperties.put("bwProjectLocationIsInherited", "true");
		}
		archetypeProperties.put("bwProjectLocation", bwProjectLocation);

		archetypeProperties.put("putPOMInParentDirectory", p.getPutPOMInParentDirectory().toString());

		return archetypeProperties;
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		if (!doIt) {
			getLog().info(NOTHING_WAS_PERFORMED);
		} else if (projects.size() > 0) {
			int failed = projects.size() - successfullyPerformedAction;

			if (failed > 1) {
				getLog().info(failed + MAVENIZATION_FAILURES);
			} else {
				getLog().info(failed + MAVENIZATION_FAILURE);
			}
			if (successfullyPerformedAction > 1) {
				getLog().info(successfullyPerformedAction + MAVENIZATION_SUCCESSES);
			} else {
				getLog().info(successfullyPerformedAction + MAVENIZATION_SUCCESS);
			}
		}
	}

}
