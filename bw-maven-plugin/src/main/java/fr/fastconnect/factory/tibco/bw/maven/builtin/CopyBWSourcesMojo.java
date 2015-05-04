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
package fr.fastconnect.factory.tibco.bw.maven.builtin;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;

import fr.fastconnect.factory.tibco.bw.maven.AbstractBWMojo;
import fr.fastconnect.factory.tibco.bw.maven.compile.ArchiveBuilder;
import fr.fastconnect.factory.tibco.bw.maven.source.POMManager;

/**
 * <p>
 * This goal copies the TIBCO BusinessWorks project sources to a temporary
 * folder.<br />
 * These sources will be used to compile a TIBCO BusinessWorks EAR or Projlib
 * from a fresh copy and with potential machine-generated code (for instance
 * for Java Custom Functions).
 * </p>
 * 
 * <p>
 * A builtin goal from Maven is called by this goal with a custom configuration
 * defined in the 'components.xml' file from Plexus. This allows to use the
 * builtin goal bound to a lifecycle phase without adding configuration in POMs.
 * <br />
 * Please refer to {@link AbstractWrapperForBuiltinMojo} for a full explanation
 * of the lifecycle binding of a builtin Maven plugin.
 * </p>
 * 
 * <p>
 * <u>Original goal</u> :
 * <b>org.apache.maven.plugins:maven-resources-plugin:copy-resources</b>
 * </p>
 * 
 * @goal copy-bw-sources
 * @inheritByDefault true
 * @requiresProject true
 * @aggregator true
 * @requiresDependencyResolution test
 *
 * @author Mathieu Debove
 * 
 */
public class CopyBWSourcesMojo extends AbstractWrapperForBuiltinMojo<Resource> {
	// Mojo configuration
	/**
	 *  @parameter property="groupId"
	 */
	protected String groupId;
	
	@Override
	protected String getGroupId() {
		return groupId;
	}

	/**
	 *  @parameter property="artifactId"
	 */
	protected String artifactId;

	@Override
	protected String getArtifactId() {
		return artifactId;
	}

	/**
	 *  @parameter property="version"
	 */
	protected String version;

	@Override
	protected String getVersion() {
		return version;
	}

	/**
	 *  @parameter property="goal"
	 */
	protected String goal;
	
	@Override
	protected String getGoal() {
		return goal;
	}

	// Environment configuration
	/**
	 * The project currently being build.
	 *
	 * @parameter property="project"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	@Override
	protected MavenProject getProject() {
		return project;
	}

	/**
	 * The current Maven session.
	 *
	 * @parameter property="session"
	 * @required
	 * @readonly
	 */
	protected MavenSession session;

	@Override
	protected MavenSession getSession() {
		return session;
	}

	/**
	 * The Build Plugin Manager (this one is Java5 annotation style).
	 */
	@Component (role = BuildPluginManager.class)
	protected BuildPluginManager pluginManager;
	
	@Override
	protected BuildPluginManager getPluginManager() {
		return pluginManager;
	}

	// Configuration
    /**
     * The actual Mojo configuration found in the Plexus 'components.xml' file.
     * <pre>  		
     *	&lt;component>
 	 *		&lt;role>org.apache.maven.plugin.Mojo&lt;/role>
 	 *		&lt;role-hint>default-copy-bw-sources&lt;/role-hint>
	 *		&lt;implementation>fr.fastconnect.factory.tibco.bw.maven.builtin.CopyBWSourcesMojo&lt;/implementation>
	 *		&lt;isolated-realm>false&lt;/isolated-realm>
	 *		&lt;configuration>
	 *			&lt;groupId>org.apache.maven.plugins&lt;/groupId>
 	 *			&lt;artifactId>maven-resources-plugin&lt;/artifactId>
	 *			&lt;version>2.6&lt;/version>
	 *			&lt;goal>copy-resources&lt;/goal>
	 *			&lt;resources>
	 *					&lt;resource>
	 *						&lt;directory>${bw.project.location}&lt;/directory>
	 *						&lt;filtering>true&lt;/filtering>
	 *						&lt;excludes>
	 *							&lt;exclude>**&#47;*TestSuite/&lt;/exclude> &lt;!&ndash;&ndash; exclude FCUnit TestSuites  	&ndash;&ndash;&gt:
	 *						&lt;/excludes>
	 *					&lt;/resource>
	 *				&lt;/resources>
	 *				&lt;configuration>
	 * 					&lt;property>
	 * 						&lt;name>outputDirectory&lt;/name>
	 *  						&lt;value>${project.build.directory}/src&lt;/value>
	 * 					&lt;/property>
	 * 				&lt;/configuration>
	 *		&lt;/configuration>
 	 *		&lt;requirements>
     *			&lt;requirement>
	 *				&lt;role>org.apache.maven.plugin.BuildPluginManager&lt;/role>
     *				&lt;role-hint />
     *				&lt;field-name>pluginManager&lt;/field-name>
     *			&lt;/requirement>
     *		&lt;/requirements>
	 *	&lt;/component>
	 * </pre>
     * @parameter
     */
    protected Properties configuration;

	@Override
	protected Properties getConfiguration() {
		return configuration;
	}

	/**
    * Optional resources parameter do define includes/excludes filesets
    * 
    * @parameter
    */
    protected List<Resource> resources;

//	@Component(role = PluginDescriptor.class)
	/**
	 * The plugin descriptor.
	 *
	 * @parameter property="pluginDescriptor"
	 * @component role="org.apache.maven.plugin.descriptor.PluginDescriptor"
	 * @required
	 * @readonly
	 */
	private PluginDescriptor pluginDescriptor;

	/**
	 * @parameter property="bw.container.merged.enterprise.archive.name" default-value="${project.artifactId}"
	 */
	private String enterpriseArchiveName;

	/**
	 * @parameter property="bw.container.merged.process.archive.name" default-value="${project.artifactId}"
	 */
	private String processArchiveName;

	@Override
	protected List<Resource> getResources() {
		List<Resource> result = new ArrayList<Resource>();
		if (resources != null) {
			result.addAll(resources);
		}
		
		if (isContainerEnabled(getProject())) {
			result.clear(); // ignore configuration from Plexus 'components.xml'

			getLog().debug(getProject().getProperties().toString());
			getLog().debug(getProject().getProperties().getProperty("project.build.directory.src"));
			File buildSrcDirectory = new File(getProject().getProperties().getProperty("project.build.directory.src"));
			buildSrcDirectory.mkdirs(); // create "target/src" directory

			// define a ".archive" file to merge all ".archive" found in other projects
			String bwProjectArchiveBuilder = getProject().getProperties().getProperty("bw.project.archive.builder");
			File bwProjectArchiveMerged = new File(buildSrcDirectory.getAbsolutePath() + File.separator + bwProjectArchiveBuilder);
			getLog().debug(".archive: " + bwProjectArchiveMerged.getAbsolutePath());

			// create an empty Archive Builder (".archive" file)
			ArchiveBuilder mergedArchiveBuilder = new ArchiveBuilder();
			
			List<MavenProject> projectsToAggregate = new ArrayList<MavenProject>();
			
			MavenProject aggregator = getProject().getParent();
			@SuppressWarnings("unchecked")
			List<String> modules = aggregator.getModules();

			for (String module : modules) {
				getLog().debug(module);
				String pom = aggregator.getBasedir() + File.separator + module + File.separator + "pom.xml";
				File pomFile = new File(pom);
				
				try {
					projectsToAggregate.add(new MavenProject(POMManager.getModelFromPOM(pomFile, getLog())));
				} catch (Exception e) {
					getLog().debug("Unable to add project from module: " + module);
				}
			}

			List<MavenProject> projects = new ArrayList<MavenProject>();
			projects.addAll(getSession().getProjects());
			
			for (Iterator<MavenProject> it = projects.iterator(); it.hasNext();) {
				MavenProject p = (MavenProject) it.next();
				if (!isProjectToAggregate(p, projectsToAggregate)) {
					it.remove();
				}
			}

			if (projects.size() > 0) {
				for (MavenProject p : projects) {
					if (p.getPackaging().equals(AbstractBWMojo.BWEAR_TYPE) && !isContainerEnabled(p)) {
						// initialize project information
						String basedir = p.getBasedir().getAbsolutePath();
						String bwProjectLocation = p.getProperties().getProperty("bw.project.location");
						bwProjectArchiveBuilder = p.getProperties().getProperty("bw.project.archive.builder"); // the ".archive" of the project
						getLog().debug(basedir);
						getLog().debug(bwProjectLocation);
						
						File bwProjectArchive = new File(basedir + File.separator + bwProjectLocation + File.separator + bwProjectArchiveBuilder);
						getLog().debug(bwProjectArchive.getAbsolutePath());
						//
						
						mergedArchiveBuilder.merge(bwProjectArchive);
	
						// add sources from the project to the container sources
						File srcDirectory = new File(basedir + File.separator + bwProjectLocation);
						result.add(addResource(srcDirectory));
					}
				}
	
				mergedArchiveBuilder.setSharedArchiveAuthor(pluginDescriptor.getArtifactId());
				mergedArchiveBuilder.setEnterpriseArchiveAuthor(pluginDescriptor.getArtifactId());
				mergedArchiveBuilder.setEnterpriseArchiveFileLocationProperty(this.getProject().getArtifactId() + AbstractBWMojo.BWEAR_EXTENSION);
				mergedArchiveBuilder.setEnterpriseArchiveName(enterpriseArchiveName);
				mergedArchiveBuilder.setFirstProcessArchiveName(processArchiveName);
				mergedArchiveBuilder.removeDuplicateProcesses();
				mergedArchiveBuilder.save(bwProjectArchiveMerged);
			}
		}

		return result;
	}

	private boolean isProjectToAggregate(MavenProject project,	List<MavenProject> projectsToAggregate) {
		if (project == null) {
			return false;
		}
		for (MavenProject p : projectsToAggregate) {
			if (project.equals(p)) {
				return true;
			}
		}
		return false;
	}

	private Resource addResource(File srcDirectory) {
		Resource r = new Resource();

		r.setDirectory(srcDirectory.getAbsolutePath());
		r.setFiltering(true);
		r.addExclude("**/*TestSuite/"); // exclude FCUnit TestSuites

		return r;
	}

	private boolean isContainerEnabled(MavenProject p) {
		if (p == null) {
			return false;
		}

		String isContainer = p.getProperties().getProperty("bw.container");
		return (isContainer != null && isContainer.equals("true"));
	}

}
