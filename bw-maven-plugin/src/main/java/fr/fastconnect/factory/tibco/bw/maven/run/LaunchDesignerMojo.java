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
package fr.fastconnect.factory.tibco.bw.maven.run;

import static org.apache.commons.io.FileUtils.copyFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import fr.fastconnect.factory.tibco.bw.maven.AbstractBWMojo;
import fr.fastconnect.factory.tibco.bw.maven.packaging.ApplicationManagement.SortedProperties;

/**
 * <p>
 * This goal launches a TIBCO Designer with exactly the same environment that
 * will be used to compile the TIBCO BusinessWorks EAR from the TIBCO
 * BusinessWorks project sources (with the
 * <a href="./compile-bw-ear-mojo.html">compile-bw-ear</a> goal in the
 * <a href="./bwmaven-lifecycles.html#EAR">compile phase</a>).
 * </p>
 * <p>
 * This is made possible by inheriting from {@link AbstractBWMojo} class.
 * </p>
 * 
 * @author Mathieu Debove
 * 
 */
@Mojo( name="launch-designer", defaultPhase = LifecyclePhase.PROCESS_TEST_RESOURCES )
@Execute ( phase=LifecyclePhase.PROCESS_TEST_RESOURCES )
public class LaunchDesignerMojo extends AbstractBWMojo {

	protected final static String LAUNCH_DESIGNER_FAILED = "The launch of the TIBCO Designer has failed.";
	protected final static String LAUNCHING_DESIGNER = "Launching TIBCO Designer...";
	protected final static String PROJECT_LOCATION = "Project location : ";

	/**
	 * If true, the project copied in "target/src" is used by TIBCO Designer.
	 */
	@Parameter(property="bw.project.use.copy", required=false, defaultValue="false")
	protected Boolean useBuildSrcDirectory;
	
	/**
	 * If true, ".aliaslib" can use parameters such as ${project.version}.
	 */
	@Parameter(property="bw.project.adapt.aliases", required=false, defaultValue="true")
	protected Boolean adaptAliases;

	/**
	 * This method copies the '.designtimelibs' of the "target/src" directory
	 * to the actual project's source folder
	 * 
	 * @throws IOException
	 */
	private void copyDesignTimeLibs() throws IOException {
		copyFile(new File(testSrcDirectory + "/" + DTL_FILE_NAME),
				 new File(projectDirectory + "/" + DTL_FILE_NAME));
	}

	/**
	 * 
	 * 
	 * @throws MojoExecutionException
	 * @throws IOException
	 */
	private void launchDesigner() throws MojoExecutionException, IOException {
		checkDesigner();

		copyDesignTimeLibs();
		
		ArrayList<String> arguments = new ArrayList<String>();
		if (useBuildSrcDirectory) {
			arguments.add(buildSrcDirectory.getAbsolutePath()); // using the one in "target/src"
			getLog().info(PROJECT_LOCATION + buildSrcDirectory.getAbsolutePath());
		} else {			
			arguments.add(projectDirectory.getAbsolutePath()); // real BW project path (not the one in "target/src")
			getLog().info(PROJECT_LOCATION + projectDirectory.getAbsolutePath());
		}

		getLog().info(LAUNCHING_DESIGNER);
		
		ArrayList<File> tras = new ArrayList<File>();
		tras.add(tibcoDesignerTRAPath);

		launchTIBCOBinary(tibcoDesignerPath, tras, arguments, directory, LAUNCH_DESIGNER_FAILED, true, false);
	}

	public void execute() throws MojoExecutionException {
		enableTestScope();

		super.execute();

		if (adaptAliases) {
			try {
				updateAliasesFile();
			} catch (IOException e) {
				throw new MojoExecutionException(e.getLocalizedMessage(), e);
			}
		}

		try {
			launchDesigner();
		} catch (IOException e) {
			throw new MojoExecutionException(LAUNCH_DESIGNER_FAILED, e);
		}
	}

	private void updateAliasesFile() throws IOException {
		File aliasesFile = getAliasesFile();
		File designer5Prefs = getDesigner5Prefs();

		Properties prefs = new SortedProperties();
		FileInputStream fisPrefs = new FileInputStream(designer5Prefs);
		prefs.load(fisPrefs);
		fisPrefs.close();
		
		Integer maxFileAliasPref = 0;
		for (Object k : prefs.keySet()) {
			String key = (String) k;

			if (key.startsWith(FILE_ALIAS_PREFIX)) {
				maxFileAliasPref++;
			}
		}

		Properties aliases = new Properties();
		FileInputStream fis = new FileInputStream(aliasesFile);
		aliases.load(fis);
		fis.close();

		String projectVersion = getProject().getVersion();
		Properties duplicates = new Properties();

		for (Object k : aliases.keySet()) {
			String key = (String) k;
			String value = aliases.getProperty(key);
			if (key.contains(projectVersion) && key.endsWith(":jar")) {
				getLog().debug(key);
				key = key.replace(projectVersion, "${project.version}");
				duplicates.put(key, value);
			}
		}
		
		if (!duplicates.isEmpty()) {
			for (Object k : duplicates.keySet()) {
				String key = (String) k;
				String value = duplicates.getProperty(key);
				key = key.replace(TIBCO_ALIAS_PREFIX, "");
				
				prefs.put(FILE_ALIAS_PREFIX + maxFileAliasPref.toString(), key + "=" + value);
				maxFileAliasPref++;
			}

			FileOutputStream fosPrefs = new FileOutputStream(designer5Prefs);
			prefs.store(fosPrefs, "");
			fis.close();

			aliases.putAll(duplicates);

			FileOutputStream fos = new FileOutputStream(aliasesFile);
			aliases.store(fos, "");
			fis.close();
		}
	}

}
