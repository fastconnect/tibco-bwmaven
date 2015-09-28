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
import java.io.IOException;

import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import fr.fastconnect.factory.tibco.bw.maven.source.POMManager;

/**
 * <p>
 * This goal will initialize additional properties to use in next goals.
 * It is the first goal called by the <b>bw-ear</b> lifecycle in the
 * <b>initialize</b> phase.
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
@Mojo (name="initialize", defaultPhase=LifecyclePhase.INITIALIZE)
public class InitializeMojo extends AbstractBWMojo {

	protected static final String parentBasedirProperty = "parentProject.basedir";

	public static boolean hasSameGAV(Model m1, Model m2) {
		if (m1 == null || m2 == null) {
			return false;
		} else {
			return m1.getGroupId().equals(m2.getGroupId()) &&
				   m1.getArtifactId().equals(m2.getArtifactId()) &&
				   (m1.getVersion().equals(m2.getVersion()) || m1.getVersion() == null || m2.getVersion() == null) && // tolerance if version is inherited
				   m1.getPackaging().equals(m2.getPackaging());				   
		}
	}

	private static boolean propertyExistsInModel(Model model, String propertyKey) {
		for (Object v : model.getProperties().values()) {
			String value = (String) v;
			
			if (value.contains("${" + propertyKey +"}")) {
				return true;
			}
		}

		return false;
	}

	private static boolean findParentPath(File path, Log logger) throws IOException, XmlPullParserException {
		File pomPath = new File(path + File.separator + "pom.xml");
		if (pomPath != null && pomPath.exists()) {
			logger.debug("pomPath: " + pomPath.getAbsolutePath());
			Model m = POMManager.getModelFromPOM(pomPath, logger);
			return propertyExistsInModel(m, parentBasedirProperty);
		}
		logger.debug("find false");
		return false;
	}

	protected static File getParent(MavenProject project, Log logger) throws IOException, XmlPullParserException {
		File result = null;
		
		MavenProject parent = project.getParent();
		if (parent == null) {
			return result; // no parent: return null
		}
		
		File parentPOM = parent.getFile();
		File parentBasedir = null;
		if (parentPOM != null &&
			parentPOM.getParentFile() != null &&
			parentPOM.getParentFile().exists() &&
			parentPOM.getParentFile().isDirectory()
			) {
			parentBasedir = parentPOM.getParentFile();
		}

		if (parentPOM != null) {
			logger.debug("parentPOM: " + parentPOM.getAbsolutePath());
		}

		while (parentBasedir != null && parentBasedir.exists()) {
			logger.debug("parentBasedir: " + parentBasedir.getAbsolutePath());
			if (findParentPath(parentBasedir, logger)) {
				logger.debug("parentFound");
				result = parentBasedir;
				
				break;
			}
			logger.debug("parentNotFound");
			if (parent != null) {
				logger.debug(parent.getArtifactId());
				parentBasedir = parent.getParentFile(); // use <relativePath> to retrieve real parent file
				if (parentBasedir == null && parent.getParent() != null) {
					parentBasedir = parent.getParent().getFile();
				}
				if (parentBasedir != null) {
					logger.debug(parentBasedir.getAbsolutePath());
				}
				if (parentBasedir != null &&
					parentBasedir.exists() &&
					parentBasedir.isFile()) {
					parentBasedir = parentBasedir.getParentFile();
				}
				parent = parent.getParent();
			}
		}

		return result;
	}

	private void setParentPath() throws IOException, XmlPullParserException {
		File parentPath = getParent(getProject(), getLog());
		if (parentPath != null) {
			getLog().debug(parentBasedirProperty + ": " + parentPath.getAbsolutePath());
			getProject().getProperties().setProperty(parentBasedirProperty, parentPath.getAbsolutePath());
		}
	}

	@Override
	public void execute() throws MojoExecutionException {
		try {
			setParentPath();
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

}
