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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * <p>
 * This is the base class to define a list of projects. The list will be
 * populating according to several criteria such as {@code workspaceRoot}
 * (directory where to look for the projects) or the type of project defined by
 * concrete children classes of inner-class {@link AbstractProject}.
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
public abstract class AbstractProjectsListMojo extends AbstractMojo {

	/**
	 * <p>
	 * A project can be either:
	 * <ul>
	 * <li>an existing Maven project defined by a POM</li>
	 * <li>an existing BW project that is to be mavenized (Maven properties such
	 * as groupId, artifactId & version will be guessed from provided values)
	 * </li>
	 * </ul>
	 * Concrete inner-classes will define the properties according to these
	 * profiles.
	 * </p>
	 */
	public static abstract class AbstractProject {
		protected Log logger; // Maven logger (because the inner-class must be static)
		private String mandatoryFilename; // the mandatory filename will trigger the add of a project to the projects list
		private File path; // original path of the file that triggered the creation of an AbstractProject
		protected File root; // root path where the projects are searched

		/**
		 * The concrete inner-classes must be instantiated with the default
		 * constructor (with no arguments).
		 * The instantiator is responsible for calling the initialize method
		 * right after the instantiation of concrete inner-class.
		 * @throws XmlPullParserException 
		 * @throws IOException 
		 */
		public void initialize(File path, File root, HashMap<String, String> m, String mandatoryFilename, Log logger) throws IOException, XmlPullParserException {
			this.logger = logger;
			this.mandatoryFilename = mandatoryFilename;
			this.path = path;
			this.root = root;			
		}

		public String getAbsolutePath() {
			return root + File.separator + getRelativePath();
//			return root + File.separator + getOriginalRelativePath();
		}

		public String getOriginalAbsolutePath() {
//			return root + File.separator + getRelativePath();
			return root + File.separator + getOriginalRelativePath();
		}

		public String getMandatoryFilePath() {
			return getOriginalAbsolutePath() + File.separator + mandatoryFilename;
		}
		
		public String getMandatoryFilePath(String customMandatoryFilename) {
			return getOriginalAbsolutePath() + File.separator + customMandatoryFilename;
		}
		
		private String _getRelativePath() {
			String s = path.getAbsolutePath();
			s = s.replace(root.getAbsolutePath()+File.separator, "");

			s = s.replace(mandatoryFilename, "");
			if (s.endsWith(File.separator)) {
				s = s.substring(0, s.length()-1);
			}
			
			return s;
		}
		public String getOriginalRelativePath() {
			return _getRelativePath();
		}
		
		public String getRelativePath() {
			return _getRelativePath();
		}

		// default is not to ignored (concrete inner-classes can override)
		public boolean isIgnored() {
			return false;
		}
		
		// information of the project from Maven point-of-view
		public abstract String getProjectName();
		public abstract String getGroupId();
		public abstract String getArtifactId();
		public abstract String getVersion();

		public Boolean getPutPOMInParentDirectory() {
			return false;
		}
	}

	protected final static String PROJECT_FOUND = " project was found.";
	protected final static String PROJECTS_FOUND = " projects were found.";
	protected final static String NOTHING_WAS_PERFORMED = "No action was performed. Add '-DdoIt=true' to perform.";

	/**
	 *  Whether to trigger the action of children classes or not
	 */
	@Parameter ( property = "doIt", required = true, defaultValue = "false" )
	protected boolean doIt;

	/**
	 *  Whether to search recursively in subdirectories or not
	 */
	@Parameter ( property = "recursive", required = false, defaultValue = "true" )
	protected boolean recursive;

	/**
	 *  A list of comma-separated patterns where to search.
	 *  Must be use with 'recursive' switch. 
	 *  
	 */
	@Parameter ( property = "patterns" )
	protected List<String> patterns;

	/**
	 *  Whether to display the list of projects or not.
	 */
	@Parameter ( property = "display.list", defaultValue = "true" )
	protected boolean displayList;

	/**
	 * This is the main directory where to look for projects.
	 * The projects can be in subfolders.
	 */
	@Parameter ( required = false, property = "workspaceRoot", defaultValue="${basedir}" )
	protected File workspaceRoot;

	protected List<AbstractProject> projects;
	protected FileSet restriction;

    static class PathResolutionException extends RuntimeException {
		private static final long serialVersionUID = 2723212952556555691L;

		PathResolutionException(String msg) {
            super(msg);
        }
    }
    
    /**
     * <p>
     * Get the relative path from one file to another, specifying the directory
     * separator. 
     * If one of the provided resources does not exist, it is assumed to be a
     * file unless it ends with '/' or '\'.
     * </p>
     * 
     * @param targetPath targetPath is calculated to this file
     * @param basePath basePath is calculated from this file
     * @param pathSeparator directory separator. The platform default is not assumed so that we can test Unix behaviour when running on Windows (for example)
     * @return
     */
    public static String getRelativePath(String targetPath, String basePath, String pathSeparator) {
        // Normalize the paths
        String normalizedTargetPath = FilenameUtils.normalizeNoEndSeparator(targetPath);
        String normalizedBasePath = FilenameUtils.normalizeNoEndSeparator(basePath);

        // Undo the changes to the separators made by normalization
        if (pathSeparator.equals("/")) {
            normalizedTargetPath = FilenameUtils.separatorsToUnix(normalizedTargetPath);
            normalizedBasePath = FilenameUtils.separatorsToUnix(normalizedBasePath);

        } else if (pathSeparator.equals("\\")) {
            normalizedTargetPath = FilenameUtils.separatorsToWindows(normalizedTargetPath);
            normalizedBasePath = FilenameUtils.separatorsToWindows(normalizedBasePath);

        } else {
            throw new IllegalArgumentException("Unrecognised dir separator '" + pathSeparator + "'");
        }

        String[] base = normalizedBasePath.split(Pattern.quote(pathSeparator));
        String[] target = normalizedTargetPath.split(Pattern.quote(pathSeparator));

        // First get all the common elements. Store them as a string,
        // and also count how many of them there are.
        StringBuffer common = new StringBuffer();

        int commonIndex = 0;
        while (commonIndex < target.length && commonIndex < base.length
                && target[commonIndex].equals(base[commonIndex])) {
            common.append(target[commonIndex] + pathSeparator);
            commonIndex++;
        }

        if (commonIndex == 0) {
            // No single common path element. This most
            // likely indicates differing drive letters, like C: and D:.
            // These paths cannot be relativized.
            throw new PathResolutionException("No common path element found for '" + normalizedTargetPath + "' and '" + normalizedBasePath + "'");
        }   

        // The number of directories we have to backtrack depends on whether the base is a file or a dir
        // For example, the relative path from
        //
        // /foo/bar/baz/gg/ff to /foo/bar/baz
        // 
        // ".." if ff is a file
        // "../.." if ff is a directory
        //
        // The following is a heuristic to figure out if the base refers to a file or dir. It's not perfect, because
        // the resource referred to by this path may not actually exist, but it's the best I can do
        boolean baseIsFile = true;

        File baseResource = new File(normalizedBasePath);

        if (baseResource.exists()) {
            baseIsFile = baseResource.isFile();

        } else if (basePath.endsWith(pathSeparator)) {
            baseIsFile = false;
        }

        StringBuffer relative = new StringBuffer();

        if (base.length != commonIndex) {
            int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;

            for (int i = 0; i < numDirsUp; i++) {
                relative.append(".." + pathSeparator);
            }
        }
        relative.append(normalizedTargetPath.substring(common.length()));
        return relative.toString();
    }

    private static String toCommaSeparatedString(List<String> strings) {
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            if (sb.length() > 0) {
            	sb.append(", ");
            }
            sb.append(string);
        }
        return sb.toString();
    }
    
	public static List<File> toFileList(FileSet fileSet) throws IOException {
        File directory = new File(fileSet.getDirectory());
        String includes = toCommaSeparatedString(fileSet.getIncludes());
        String excludes = toCommaSeparatedString(fileSet.getExcludes());
        return FileUtils.getFiles(directory, includes, excludes);
    }

    protected int successfullyPerformedAction;
    protected abstract void displayProject(AbstractProject p);
    protected boolean performAction(AbstractProject p) {
		return true;
	}

	protected abstract void initProjects() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException, XmlPullParserException;
	
	protected final void initProjects(String mandatoryFilename, HashMap<String, String> m, Class<? extends AbstractProject> classAbstractProject) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchMethodException, XmlPullParserException {
		projects = new ArrayList<AbstractProject>();
		
		restriction= new FileSet();
		restriction.setDirectory(workspaceRoot.getAbsolutePath());
		
		if (recursive) {
			for (String p : patterns) {
				restriction.addInclude(p + mandatoryFilename);
			}
		} else {
			restriction.addInclude(mandatoryFilename); // we are only looking in the "workspaceRoot" directory
		}
		restriction.addExclude("**/target/**/" + mandatoryFilename);
		restriction.addExclude("**/bin/" + mandatoryFilename);
		
		List<File> files = toFileList(restriction);
		
		for (File f : files) {
			Constructor<? extends AbstractProject> ctor = classAbstractProject.getConstructor();
			AbstractProject ap = ctor.newInstance();
			
			ap.initialize(f, workspaceRoot, m, mandatoryFilename, getLog());
			if (!ap.isIgnored()) {
				projects.add(ap);
			} else {
				getLog().debug("Ignoring project '" + ap.getProjectName() + "'");
			}
		}
	}
	
	protected void browseList(){
		for (AbstractProject p : projects) {
			// display
			if (displayList) {
				getLog().info("---");
				displayProject(p);
				getLog().info("");
			}
			
			// perform action (if exists)
			if (doIt && performAction(p)) {
				successfullyPerformedAction++;
			}
		}
		
		getLog().info("");
		if (projects.size()>1) {
			getLog().info(projects.size() + PROJECTS_FOUND);
		} else {
			getLog().info(projects.size() + PROJECT_FOUND);
		}
		
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (patterns == null || patterns.isEmpty()) {
			patterns = new ArrayList<String>();
			patterns.add("**/");
		}
		
		if (recursive) {
			getLog().info("Scanning recursively '" + workspaceRoot + "' with patterns '" + patterns.toString() +"'");
		} else {
			getLog().info("Scanning '" + workspaceRoot + "'");
		}
		
		successfullyPerformedAction = 0;
		try {
			initProjects();
		} catch (Exception e) {
			throw new MojoExecutionException(e.getLocalizedMessage(), e);
		}
		
		browseList();
	}

}
