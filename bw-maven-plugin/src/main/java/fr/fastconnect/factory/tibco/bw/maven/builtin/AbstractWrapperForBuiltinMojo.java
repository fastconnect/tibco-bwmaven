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

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

/**
 * <p>
 * This goal is just a wrapper to launch a goal from a builtin Maven plugin
 * (such as org.apache.maven.plugins:maven-dependency-plugin) with a
 * <b>configuration bound to the lifecycle but without triggering a parallel
 * lifecycle</b>. <br />
 * This configuration will be injected from the Plexus 'components.xml' file.
 * </p>
 * 
 * <p>
 * <b>NB</b>: The Maven 2 doclet annotations must be used to inject properly the
 * configuration.
 * </p>
 * 
 * <p>
 * Duplicate this template code to create a new wrapped goal (all << >> must
 * be replaced).
 * </p>
 * 
 * <p>
 * <u>Original goal</u> :
 * <b>&lt;&lt;groupId>>:&lt;&lt;artifactId>>:&lt;&lt;goal-name>></b>
 * </p>
 * 
 * <p>
 * Associated Plexus 'components.xml' sections (first section is shared) : 
 * <pre>
 * &lt;component>
 *  &lt;role>org.apache.maven.lifecycle.mapping.LifecycleMapping&lt;/role>
 *    &lt;role-hint>&lt;&lt;packaging-name>>&lt;/role-hint>
 *    &lt;implementation>org.apache.maven.lifecycle.mapping.DefaultLifecycleMapping
 *    &lt;/implementation>
 *   &lt;configuration>
 *    &lt;lifecycles>
 *     &lt;lifecycle>
 *      &lt;id>default&lt;/id>
 *      &lt;phases>
 *       &lt;generate-sources>
 *        &lt;&lt; fr.fastconnect.factory.tibco.bw.maven:bw-maven-plugin:resolve-bw-dependencies >>
 *       &lt;/generate-sources>
 *      &lt;/phases>
 *     &lt;/lifecycle>
 *    &lt;/lifecycles>
 *   &lt;/configuration>
 *	&lt;/component>
 *
 *	&lt;component>
 *		&lt;role>org.apache.maven.plugin.Mojo&lt;/role>
 *		&lt;role-hint>&lt;&lt;default-new-goal-name>>&lt;/role-hint>
 *		&lt;implementation>&lt;&lt;fr.fastconnect.factory.tibco.bw.maven.builtin.AbstractWrapperForBuiltinMojo>>&lt;/implementation>
 *		&lt;configuration>
 *			&lt;groupId>&lt;&lt;org.apache.maven.plugins>>&lt;/groupId>
 *			&lt;artifactId>&lt;&lt;maven-dependency-plugin>>&lt;/artifactId>
 *			&lt;version>&lt;&lt;2.8>>&lt;/version>
 *			&lt;goal>&lt;&lt;resolve>>&lt;/goal>
 *			&lt;&lt; &lt;outputFile>${project.build.directory}/resolved&lt;/outputFile> >>
 *			&lt;&lt; &lt;includeTypes>projlib,jar&lt;/includeTypes>      >>
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
 * </p>
 * 
 * @_goal <<new-goal-name>>
 * @inheritByDefault true
 * @requiresProject true
 * @aggregator true
 * @requiresDependencyResolution test
 *
 * @author Mathieu Debove
 * 
 */
public abstract class AbstractWrapperForBuiltinMojo<CustomResource extends Resource> extends AbstractMojo {
	// Mojo configuration
	/**
	 *  @parameter property="groupId"
	 */
	protected String groupId;
	protected abstract String getGroupId();

	/**
	 *  @parameter property="artifactId"
	 */
	protected String artifactId;
	protected abstract String getArtifactId();

	/**
	 *  @parameter property="version"
	 */
	protected String version;
	protected abstract String getVersion();

	/**
	 *  @parameter property="goal}
	 */
	protected String goal;
	protected abstract String getGoal();

	// Environment configuration
	/**
	 * The project currently being build.
	 *
	 * @parameter property="project"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;
	protected abstract MavenProject getProject();

	/**
	 * The current Maven session.
	 *
	 * @parameter property="session"
	 * @required
	 * @readonly
	 */
	protected MavenSession session;
	protected abstract MavenSession getSession();

	/**
	 * The Build Plugin Manager (this one is Java5 annotation style).
	 */
	@Component (role = BuildPluginManager.class)
	protected BuildPluginManager pluginManager;
	protected abstract BuildPluginManager getPluginManager();

	// Configuration
    /**
     * The actual Mojo configuration found in the Plexus 'components.xml' file.
     * <!--
	 *		<configuration>
	 *			...
 	 *		<<	<properties>
 	 *				<property>
 	 *					<name><<outputFile>></name>
 	 *					<value><<${project.build.directory}/resolved>></value>
 	 *				</property>
 	 *				<property>
 	 *					<name><<includeTypes>></name>
 	 *					<value><<projlib,jar>></value>
 	 *				</property>
 	 *			</properties> >>
	 *		</configuration>
     * -->
     * @parameter
     */
	protected Properties configuration;
    protected abstract Properties getConfiguration();
	
    /**
    * Optional resources parameter do define includes/excludes filesets
    * 
    * @parameter
    */
	protected Properties resources;
    protected abstract List<CustomResource> getResources();

	// Execution
	/**
	 * @return the Execution Environment for the actual Mojo being called
	 */
	protected ExecutionEnvironment getEnvironment() {
		return executionEnvironment(getProject(), getSession(), getPluginManager());
	}

	/**
	 * Children classes can use a descendant of {@link Resource} (because of the
	 * <? extends Resource> declaration).
	 * If so, the children classes are responsible for coding the additional 
	 * mappings from Java objects to {@link Element}.
	 * @param resource 
	 *
	 *@return null, children classes need to override this method to parse
	 * their custom elements
	 */
	protected List<Element> generateCustomElements(CustomResource resource) {
		return null;
	}

	private List<Element> generateResources() {
		List<CustomResource> resources = getResources();
		List<Element> resourcesElement = new ArrayList<Element>();

		if (resources != null) {
			getLog().debug(resources.toString());
			
			for (CustomResource resource : resources) {
				List<Element> children = new ArrayList<Element>();
				children.add(element("directory", resource.getDirectory()));
				children.add(element("filtering", resource.getFiltering()));
				children.add(element("targetPath", resource.getTargetPath()));
				
				List<Element> excludes = new ArrayList<Element>();
				for (String exclude : resource.getExcludes()) {
					excludes.add(element("exclude", exclude));
				}
				children.add(element("excludes", excludes.toArray(new Element[0])));

				List<Element> includes = new ArrayList<Element>();
				for (String include : resource.getIncludes()) {
					excludes.add(element("include", include));
				}
				children.add(element("includes", includes.toArray(new Element[0])));

				List<Element> customElements = generateCustomElements(resource);
				if (customElements != null) {
					for (Element element : customElements) {
						children.add(element);
					}
				}

				resourcesElement.add(element("resource", children.toArray(new Element[0])));
			}			
		}

		return resourcesElement;
	}

	/**
	 * 
	 * @param configuration, the configuration found in Plexus 'components.xml'
	 * file
	 * @return the configuration as a List<Element> used by MojoExecutor
	 * @throws IOException 
	 */
	private List<Element> generateConfiguration(Properties configuration) {
		ArrayList<Element> configurations = new ArrayList<Element>();

		if (configuration != null) {
			for (Object key : configuration.keySet()) {
				String value = configuration.get(key).toString();
				getLog().debug(value);
				if ("properties".equals(key) && value != null) {
					getLog().debug("key='properties'");
					Pattern p = Pattern.compile("^\\$\\{(.*)\\}$");
					Matcher matcherElement = p.matcher(value);
					if (matcherElement.matches()) {
						getLog().debug("matching properties to expand");
						value = matcherElement.group(1);
						String replacedValue = this.getProject().getProperties().getProperty(value);
						if (replacedValue != null && !replacedValue.isEmpty()) {
							value = replacedValue;
							getLog().debug("properties_value: " + value);
							Properties properties = new Properties();
							try {
								properties.load(new StringReader(value));
								getLog().debug(properties.toString());
							} catch (IOException e) {
								continue;
							}

							List<Element> children = new ArrayList<Element>();
							for (Object o : properties.keySet()) {
								String childKey = (String) o;
								String childValue = properties.getProperty(childKey);
								children.add(element(childKey, childValue));
							}

							configurations.add(element(key.toString(), children.toArray(new Element[0])));
						}
					}

				} else {
					configurations.add(element(key.toString(), value));
				}
			}
		}

		List<Element> resources = generateResources();
		if (resources != null && !resources.isEmpty()) {
			configurations.add(element("resources", resources.toArray(new Element[0])));
		}

		return configurations;
	}

	/**
	 * This method will call the actual builtin goal from the Mojo specified in
	 * Plexus 'components.xml'.<br/>
	 * 
	 * The {@link MojoExecutor} is used to perform the call natively.
	 */
	public void execute() throws MojoExecutionException {
		executeMojo(
                plugin(
                    groupId(getGroupId()),
                    artifactId(getArtifactId()),
                    version(getVersion())
                ),
                goal(getGoal()),
                configuration(
                	generateConfiguration(getConfiguration()).toArray(new Element[0])
                ),
                getEnvironment()
            );		
	}

}
