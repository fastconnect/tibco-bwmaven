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
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import fr.fastconnect.factory.tibco.bw.maven.InitializeMojo;

/**
 * <p>
 * This goal lists all the existing TIBCO BusinessWorks projects found in
 * a specified folder and nested subfolders.<br />
 * The mandatory 'vcrepo.dat' file will be used as a search criteria.
 * </p>
 * 
 * <p>
 * Once the list of projects is ready, it is possible to use children goals such
 * as <a href="./mavenize-bw-projects-mojo.html">bw:mavenize-bw-projects</a>
 * with the same parameters.
 * </p>
 * 
 * <p class="mojo-sample">
 * 
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
@Mojo ( name = "list-bw-projects", requiresProject = false )
public class ExistingBWProjectsListMojo extends AbstractProjectsListMojo {

	/**
	 * This concrete inner-class inheriting from AbstractProject will guess
	 * project informations from the project location and provided properties.
	 * These informations will be used if the project is mavenized.
	 *
	 */
	public static class BWProject extends AbstractProject {
		private Boolean putPOMInParentDirectory;
		private String bwProjectLocation;
		private String groupId;
		private String version;
		private Boolean ignoreAlreadyMavenizedProjects;
		
		@Override
		public void initialize(File path, File root, HashMap<String, String> m, String mandatoryFilename, Log logger) throws IOException, XmlPullParserException {
			super.initialize(path, root, m, mandatoryFilename, logger);
			
			putPOMInParentDirectory = m.get("putPOMInParentDirectory").equals("true");
			if (putPOMInParentDirectory) {
				bwProjectLocation = null;
			} else {
				bwProjectLocation = m.get("bwProjectLocation");
			}
			groupId = m.get("groupId");
			version = m.get("version");
			ignoreAlreadyMavenizedProjects = m.get("ignoreAlreadyMavenizedProjects").equals("true");
		}

		@Override
		public Boolean getPutPOMInParentDirectory() {
			return putPOMInParentDirectory;
		}

		@Override
		public String getProjectName() {
			return getProjectName(getRelativePath());
		}
		
		@Override
		public String getRelativePath() {
			String s = super.getRelativePath();
			s = root + File.separator + s;

			File f = getRelativeProjectPath(new File(s));
			if (f != null) {
				s = f.getAbsolutePath();
			}
			
			s = s.replace(root.getAbsolutePath()+File.separator, "");

			return s;
		}
		
		@Override
		public String getGroupId() {
			return groupId;
		}
		
		@Override
		public String getArtifactId() {
			return getProjectName().replaceAll("[ _]", "-").toLowerCase(); // convert to Maven convention for ArtifactId
		}

		@Override
		public String getVersion() {
			return version;
		}

		/**
		 * Ignore the found project if it contains a 'pom.xml' file or if
		 * 'vcrepo.dat' is not found (the latter can happen if the relative
		 * source directory specified by bwProjectLocation is not found).
		 */
		@Override
		public boolean isIgnored() {
			File f1 = new File(this.getAbsolutePath() + File.separator + MavenProjectsListMojo.MANDATORY_MAVEN_FILENAME);
			File f2;
			if (this.bwProjectLocation == null) {
				f2 = new File(this.getOriginalAbsolutePath() + File.separator + MANDATORY_BW_FILENAME);
			} else {
				f2 = new File(this.getOriginalAbsolutePath() + File.separator + this.bwProjectLocation + File.separator + MANDATORY_BW_FILENAME);
			}
			
			return ignoreAlreadyMavenizedProjects && f1.exists() || !f2.exists();
		}
		/**
		 * The project name will be the name of the directory containing the
		 * project. 
		 * 
		 * @param path, the relative path of the project, without the source
		 * directory
		 * @return the last part of path after '/' or '\' (File.separator) if
		 * this character exists in path, path otherwise
		 */
		private String getProjectName(String path) {
			return 
				path.lastIndexOf(File.separator) > 1
			? 
				path.substring(path.lastIndexOf(File.separator)+1)
			:
				path
			;
		}

		/**
		 * Recursively removes the source directory to get the actual project
		 * path. 
		 * 
		 * Example : 
		 *   the BW project is in       : C:\test\MyProject\src\bw\vcrepo.dat
		 *   the source directory is    : ./src/bw
		 *   the actual project will be : C:\test\MyProject
		 * 
		 * @param f
		 * @return
		 */
	    private File getRelativeProjectPath(File f) {
	    	if (f == null) {
	    		return null;
	    	}
	    	
	    	File tmp;
	    	if (bwProjectLocation != null && !bwProjectLocation.trim().isEmpty()) {
	    		tmp = new File(f.getAbsolutePath() + bwProjectLocation);
	    	} else {
	    		tmp = new File(f.getAbsolutePath());
	    	}

			if (tmp.isDirectory() && tmp.exists()) {
				if (putPOMInParentDirectory) {
					return f.getParentFile();
				} else {
					return f;
				}
			} else {
				return getRelativeProjectPath(f.getParentFile());
			}
		}
	}

	protected final static String MANDATORY_BW_FILENAME = "vcrepo.dat";

	/**
	 * True means that the "pom.xml" file must be put in parent folder of
	 * BusinessWorks project
	 */
	@Parameter ( required = false, property = "putPOMInParentDirectory", defaultValue="true" )
	protected Boolean putPOMInParentDirectory;

	/**
	 * The source directory of BusinessWorks project (relatively to the actual
	 * project).
	 */
	@Parameter ( required = false, property = "bwProjectLocation" )
	protected String bwProjectLocation;

	/**
	 * Whether to ignore BW projects already mavenized (those having a POM) in
	 * the search results.
	 */
	@Parameter ( required = false, property = "ignore.mavenized.projects", defaultValue = "true" )
	protected Boolean ignoreAlreadyMavenizedProjects;

	protected HashMap<String, String> updateProjects(HashMap<String, String> m) {
		return m;
	}
	
	@Override
	protected void initProjects() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException, XmlPullParserException {
		HashMap<String, String> m = new HashMap<String, String>();
		
		// information to forward to the static inner-class
		m = updateProjects(m);
		m.put("putPOMInParentDirectory", putPOMInParentDirectory.toString());
		m.put("bwProjectLocation", bwProjectLocation);
		m.put("ignoreAlreadyMavenizedProjects", ignoreAlreadyMavenizedProjects.toString());
		
		super.initProjects(MANDATORY_BW_FILENAME, m, BWProject.class);
	}

	protected void displayRootProject(AbstractProject p) {
		// nothing to display
	}

	private static boolean findParentPath(File path, Model model, Log logger) throws IOException, XmlPullParserException {
		File pomPath = new File(path + File.separator + "pom.xml");
		if (pomPath != null && pomPath.exists()) {
			Model m = POMManager.getModelFromPOM(pomPath, logger);
			return InitializeMojo.hasSameGAV(model, m);
		}
		return false;
	}

	protected static File getParent(Model parentModel, File basedir, Log logger) throws IOException, XmlPullParserException {
		File result = null;

		while (basedir.getParentFile() != null && 
			   basedir.getParentFile().exists()) {

			if (findParentPath(basedir.getParentFile(), parentModel, logger)) {
				result = basedir.getParentFile();
				break;
			}
			
			basedir = basedir.getParentFile();
		}
		
		return result;
	}

	@Override
	protected void displayProject(AbstractProject p) {
		getLog().info(p.getProjectName());
		getLog().info("  Location of the BW project    : " +
		              p.getOriginalRelativePath()
		             );
		getLog().info("  Location of the project (POM) : " +
				p.getRelativePath()
				);
		this.displayRootProject(p);

		if (!this.getClass().getName().equals(ExistingBWProjectsListMojo.class.getName())) { // display this only in children
			getLog().info("  Mavenized project will be     : " +
					p.getGroupId() + ":" +
					p.getArtifactId() + ":" +
					p.getVersion()
					);
		}
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
	}

}
