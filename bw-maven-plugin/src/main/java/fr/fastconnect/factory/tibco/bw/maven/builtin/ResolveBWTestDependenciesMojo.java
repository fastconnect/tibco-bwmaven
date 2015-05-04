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

import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;

/**
 * <p>
 * This goal resolves TIBCO BusinessWorks dependencies (especially Projlibs)
 * defined in the POM of the project.<br/>
 * The resolution is transitive and follows the Maven standard and uses the
 * 'test' scope.
 * </p>
 * 
 * <p>
 * These dependencies are then copied by
 * <a href="./copy-bw-test-dependencies-mojo.html">copy-bw-test-dependencies</a>.
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
 * <b>org.apache.maven.plugins:maven-dependency-plugin:list</b>
 * </p>
 * 
 * @goal resolve-bw-test-dependencies
 * @inheritByDefault true
 * @requiresProject true
 * @aggregator true
 * @requiresDependencyResolution test
 *
 * @author Mathieu Debove
 * 
 */
public class ResolveBWTestDependenciesMojo extends AbstractWrapperForBuiltinMojo<Resource> {
	// Mojo configuration
	/**
	 *  @parameter property="groupId"
	 */
	protected String groupId;

	@Override
	protected String getGroupId() {
		// TODO Auto-generated method stub
		return groupId;
	}

	/**
	 *  @parameter property="artifactId"
	 */
	protected String artifactId;

	@Override
	protected String getArtifactId() {
		// TODO Auto-generated method stub
		return artifactId;
	}

	/**
	 *  @parameter property="version"
	 */
	protected String version;

	@Override
	protected String getVersion() {
		// TODO Auto-generated method stub
		return version;
	}
	
	/**
	 *  @parameter property="goal"
	 */
	protected String goal;

	@Override
	protected String getGoal() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return session;
	}

	/**
	 * The Build Plugin Manager (this one is Java5 annotation style).
	 */
	@Component (role = BuildPluginManager.class)
	protected BuildPluginManager pluginManager;
	
	@Override
	protected BuildPluginManager getPluginManager() {
		// TODO Auto-generated method stub
		return pluginManager;
	}

	// Configuration
    /**
     * The actual Mojo configuration found in the Plexus 'components.xml' file.
     * <pre>  		
 	 *	&lt;component>
 	 *		&lt;role>org.apache.maven.plugin.Mojo&lt;/role>
 	 *		&lt;role-hint>default-resolve-bw-dependencies&lt;/role-hint>
	 *		&lt;implementation>fr.fastconnect.factory.tibco.bw.maven.builtin.ResolveBWTestDependenciesMojo&lt;/implementation>
	 *		&lt;isolated-realm>false&lt;/isolated-realm>
	 *		&lt;configuration>
	 *			&lt;groupId>org.apache.maven.plugins&lt;/groupId>
 	 *			&lt;artifactId>maven-dependency-plugin&lt;/artifactId>
	 *			&lt;version>2.8&lt;/version>
	 *			&lt;goal>list&lt;/goal>
 	 *			&lt;configuration>
 	 *				&lt;property>
 	 *					&lt;name>outputFile&lt;/name>
 	 *					&lt;value>${project.build.test.directory}/resolved&lt;/value>
 	 *				&lt;/property>
 	 *				&lt;property>
 	 *					&lt;name>includeTypes&lt;/name>
 	 *					&lt;value>projlib,jar&lt;/value>
 	 *				&lt;/property>
 	 *				&lt;property>
 	 *					&lt;name>includeScope&lt;/name>
 	 *					&lt;value>test&lt;/value>
 	 *				&lt;/property>
 	 *			&lt;/configuration>
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
		// TODO Auto-generated method stub
		return configuration;
	}

    /**
    * Optional resources parameter do define includes/excludes filesets
    * 
    * @parameter
    */
    protected List<Resource> resources;

	@Override
	protected List<Resource> getResources() {
		// TODO Auto-generated method stub
		return resources;
	}

}
