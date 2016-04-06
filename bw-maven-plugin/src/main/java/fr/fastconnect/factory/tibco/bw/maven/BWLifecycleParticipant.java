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
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
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

    @Requirement
    protected ProjectBuilder projectBuilder;

	private AbstractBWMojo propertiesManager;

	@Override
	public void afterProjectsRead(MavenSession session)	throws MavenExecutionException {
		logger.debug("BW lifecycle participant");

		propertiesManager = AbstractBWMojo.propertiesManager(session, session.getCurrentProject());

		List<MavenProject> projects = prepareProjects(session.getProjects(), session);
		session.setProjects(projects);
	}

	private List<String> activateProfilesWithProperties(MavenProject mavenProject, List<String> activeProfileIds) {
		if (mavenProject == null) return activeProfileIds;
		List<String> result = new ArrayList<String>();
		if (activeProfileIds != null) {
			result.addAll(activeProfileIds);
		}

		for (Profile profile : mavenProject.getModel().getProfiles()) {
			Activation activation = profile.getActivation();
			if (activation != null) {
				ActivationProperty property = activation.getProperty();
				if (property != null) {
					String name = property.getName();
					if (name != null) {
						String value;
						if (name.startsWith("!")) {
							value = propertiesManager.getPropertyValue(name.substring(1));
						} else {
							value = propertiesManager.getPropertyValue(name);
						}
						if (value != null) {
							if (!name.startsWith("!") && value.equals(property.getValue()) || name.startsWith("!") && !value.equals(property.getValue())) {
								result.add(profile.getId());
							}
						}
					}
				}
			}
		}

		return result;
	}

	private static File getHawkLibDirectory(MavenProject mavenProject, Logger logger) {
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

	private static File getRvLibDirectory(MavenProject mavenProject, Logger logger) {
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

	private List<MavenProject> prepareProjects(List<MavenProject> projects, MavenSession session) throws MavenExecutionException {
		List<MavenProject> result = new ArrayList<MavenProject>();

		ProjectBuildingRequest projectBuildingRequest = session.getProjectBuildingRequest();

		for (MavenProject mavenProject : projects) {
			logger.debug("project: " + mavenProject.getGroupId()+":"+mavenProject.getArtifactId());

			List<String> oldActiveProfileIds = projectBuildingRequest.getActiveProfileIds();
			try {
				List<String> activeProfileIds = activateProfilesWithProperties(mavenProject, oldActiveProfileIds);
				if (activeProfileIds.size() != oldActiveProfileIds.size()) {
					projectBuildingRequest.setActiveProfileIds(activeProfileIds);
					if (mavenProject.getFile() != null) {
						List<File> files = new ArrayList<File>();
						files.add(mavenProject.getFile());
						List<ProjectBuildingResult> results = null;
						try {
							results = projectBuilder.build(files, true, projectBuildingRequest);
						} catch (ProjectBuildingException e) {
						}

						for (ProjectBuildingResult projectBuildingResult : results) {
							mavenProject = projectBuildingResult.getProject();
						}
					}
				}
			} finally {
				projectBuildingRequest.setActiveProfileIds(oldActiveProfileIds);
			}

			if (mavenProject.getPackaging().startsWith(AbstractBWMojo.BWEAR_TYPE) || "true".equals(propertiesManager.getPropertyValue("enableBWLifecycle"))) {
				addTIBCODependenciesToPlugin(mavenProject, logger);
			}
			result.add(mavenProject);
		}

		return result;
	}

	public static void addTIBCODependenciesToPlugin(MavenProject mavenProject, Logger logger) {
		File hawkLib = getHawkLibDirectory(mavenProject, logger);
		File rvLib = getRvLibDirectory(mavenProject, logger);
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

			logger.debug("looking for plugins");
			for (Plugin plugin : mavenProject.getModel().getBuild().getPlugins()) {
				logger.debug(plugin.getArtifactId());
				if ("bw-maven-plugin".equals(plugin.getArtifactId())) {
					logger.debug("found ! " + plugin.getArtifactId());
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
					logger.debug(plugin.getDependencies().toString());

				}
			}
		}

		logger.debug(mavenProject.getModel().getBuild().getPlugins().toString());
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
}
