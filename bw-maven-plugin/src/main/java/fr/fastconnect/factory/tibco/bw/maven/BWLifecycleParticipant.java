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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

/**
 *
 * @author Mathieu Debove
 *
 */
@Component(role = AbstractMavenLifecycleParticipant.class, hint = "BWLifecycleParticipant")
public class BWLifecycleParticipant extends AbstractMavenLifecycleParticipant {

	@Requirement
	private Logger logger;

	@Override
	public void afterProjectsRead(MavenSession session)	throws MavenExecutionException {
		List<MavenProject> projects = prepareProjects(session.getProjects(), session.getProjectBuildingRequest());
		session.setProjects(projects);
	}

	private File getHawkLibDirectory(MavenProject mavenProject) {
		File result = null;
		if (mavenProject == null) {
			return result;
		}

		String hawkLib = mavenProject.getModel().getProperties().getProperty("hawk.lib");
		if (hawkLib != null) {
			result = new File(hawkLib);
		}
		if (result != null && result.exists() && result.isDirectory()) {
			return result;
		}

		String tibcoHome = mavenProject.getModel().getProperties().getProperty("tibco.home");
		logger.debug("tibcoHome : " + tibcoHome);
		String hawkVersion = mavenProject.getModel().getProperties().getProperty("tibco.hawk.version");
		logger.debug("hawkVersion : " + hawkVersion);

		hawkLib = tibcoHome + File.separator + "hawk" + File.separator + hawkVersion + File.separator + "lib";
		if (hawkLib != null) {
			result = new File(hawkLib);
		}

		if (result != null && result.exists() && result.isDirectory()) {
			return result;
		}

		return null;
	}

	private File getRvLibDirectory(MavenProject mavenProject) {
		File result = null;
		if (mavenProject == null) {
			return result;
		}

		String rvLib = mavenProject.getModel().getProperties().getProperty("rv.lib");
		if (rvLib != null) {
			result = new File(rvLib);
		}
		if (result != null && result.exists() && result.isDirectory()) {
			return result;
		}

		String tibcoHome = mavenProject.getModel().getProperties().getProperty("tibco.home");
		logger.debug("tibcoHome : " + tibcoHome);
		String rvVersion = mavenProject.getModel().getProperties().getProperty("tibco.rv.version");
		logger.debug("rvVersion : " + rvVersion);

		rvLib = tibcoHome + File.separator + "tibrv" + File.separator + rvVersion + File.separator + "lib";
		if (rvLib != null) {
			result = new File(rvLib);
		}

		if (result != null && result.exists() && result.isDirectory()) {
			return result;
		}

		return null;
	}

	private List<MavenProject> prepareProjects(List<MavenProject> projects, ProjectBuildingRequest projectBuildingRequest) throws MavenExecutionException {
		List<MavenProject> result = new ArrayList<MavenProject>();

		for (MavenProject mavenProject : projects) {
			if (mavenProject.getPackaging().startsWith(AbstractBWMojo.BWEAR_TYPE)) {
				File hawkLib = getHawkLibDirectory(mavenProject);
				File rvLib = getRvLibDirectory(mavenProject);
				String hawkVersion = mavenProject.getModel().getProperties().getProperty("tibco.hawk.version");
				String rvVersion = mavenProject.getModel().getProperties().getProperty("tibco.rv.version");
				logger.debug("hawkVersion : " + hawkVersion);
				logger.debug("rvVersion : " + rvVersion);

				if (hawkLib != null && hawkLib.exists() && hawkLib.isDirectory()) {
					File console = new File(hawkLib, "console.jar");
					File talon = new File(hawkLib, "talon.jar");
					File util = new File(hawkLib, "util.jar");

					logger.debug(console.getAbsolutePath());
					logger.debug(talon.getAbsolutePath());
					logger.debug(util.getAbsolutePath());

					for (Plugin plugin : mavenProject.getModel().getBuild().getPlugins()) {
						if ("bw-maven-plugin".equals(plugin.getArtifactId())) {
							Dependency dependency = new Dependency();
							dependency.setGroupId("com.tibco.hawk");
							dependency.setVersion(hawkVersion);
							dependency.setScope("system");
							dependency.setArtifactId("console");
							dependency.setSystemPath(console.getAbsolutePath());
							plugin.addDependency(dependency);

							dependency = new Dependency();
							dependency.setGroupId("com.tibco.hawk");
							dependency.setVersion(hawkVersion);
							dependency.setScope("system");
							dependency.setArtifactId("talon");
							dependency.setSystemPath(talon.getAbsolutePath());
							plugin.addDependency(dependency);

							dependency = new Dependency();
							dependency.setGroupId("com.tibco.hawk");
							dependency.setVersion(hawkVersion);
							dependency.setScope("system");
							dependency.setArtifactId("util");
							dependency.setSystemPath(util.getAbsolutePath());
							plugin.addDependency(dependency);
						}
					}
				}

				if (rvLib != null && rvLib.exists() && rvLib.isDirectory()) {
					File tibrv = new File(rvLib, "tibrvj.jar");

					logger.debug(tibrv.getAbsolutePath());

					for (Plugin plugin : mavenProject.getModel().getBuild().getPlugins()) {
						if ("bw-maven-plugin".equals(plugin.getArtifactId())) {
							Dependency dependency = new Dependency();
							dependency.setGroupId("com.tibco.rv");
							dependency.setVersion(rvVersion);
							dependency.setScope("system");
							dependency.setArtifactId("tibrvj");
							dependency.setSystemPath(tibrv.getAbsolutePath());
							plugin.addDependency(dependency);
						}
					}
				}
			}
			result.add(mavenProject);
		}

		return result;
	}

}
