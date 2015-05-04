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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * <p>
 * This goal lists all the existing projects (with a POM.xml file) found in a
 * specified folder and nested subfolders.<br />
 * The mandatory 'pom.xml' file will be used as a search criteria.
 * </p>
 * 
 * <p>
 * Once the list of projects is ready, it is possible to use children goals such
 * as <a href="./add-dependency-mojo.html">bw:add-dependency</a> with the same
 * parameters.
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
@Mojo ( name = "list-maven-projects", requiresProject = false )
public class MavenProjectsListMojo extends AbstractProjectsListMojo {

	// these methods must be overriden in children classes
	protected String getActionFailure() { return " action failed."; }
	protected String getActionFailures() { return " actions failed."; }
	protected String getActionSuccess() { return " action succeeded."; }
	protected String getActionSuccesses() { return " actions succeeded."; }
	
	/**
	 * This concrete inner-class inheriting from AbstractProject will guess
	 * project information from the Maven model. This model is built from the
	 * POM of the project.
	 *
	 */
	public static class MavenProject extends AbstractProject {
		private Model model;
		
		@Override
		public void initialize(File path, File root, HashMap<String, String> m, String mandatoryFilename, Log logger) throws IOException, XmlPullParserException {
			super.initialize(path, root, m, mandatoryFilename, logger);
			
			initModel();
		}

		private void initModel() throws IOException, XmlPullParserException {
			File pom = new File(getMandatoryFilePath());
			
			model = POMManager.getModelFromPOM(pom, logger);
		}

		@Override
		public String getProjectName() {
			return model.getName();
		}

		@Override
		public String getGroupId() {
			return model.getGroupId();
		}
		
		@Override
		public String getArtifactId() {
			return model.getArtifactId();
		}

		@Override
		public String getVersion() {
			return model.getVersion() != null ? model.getVersion() : "[inherited]";
		}
	}

	protected final static String MANDATORY_MAVEN_FILENAME = "pom.xml";
 
	@Override
	protected void initProjects() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException, XmlPullParserException {
		super.initProjects(MANDATORY_MAVEN_FILENAME, new HashMap<String, String>(), MavenProject.class);
	}

	@Override
	protected void displayProject(AbstractProject p) {
		getLog().info(p.getProjectName());
		getLog().info("  Location of the project : " +
		              p.getRelativePath()
		             );
		getLog().info("  Maven project is        : " +
				p.getGroupId() + ":" +
				p.getArtifactId() + ":" +
				p.getVersion()
				);
	}
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		if (!doIt) {
			if (!this.getClass().getName().equals(MavenProjectsListMojo.class.getName())) {
				getLog().info(NOTHING_WAS_PERFORMED);
			}
		} else if (projects.size() > 0) {
			int failed = projects.size() - successfullyPerformedAction;
			
			if (failed > 1) {
				getLog().info(failed + getActionFailures());
			} else {
				getLog().info(failed + getActionFailure());
			}
			if (successfullyPerformedAction > 1) {
				getLog().info(successfullyPerformedAction + getActionSuccesses());
			} else {
				getLog().info(successfullyPerformedAction + getActionSuccess());
			}
		}
	}

}
