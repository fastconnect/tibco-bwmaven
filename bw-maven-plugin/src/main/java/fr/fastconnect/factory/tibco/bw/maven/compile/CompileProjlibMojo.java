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
package fr.fastconnect.factory.tibco.bw.maven.compile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import fr.fastconnect.factory.tibco.bw.maven.AbstractBWArtifactMojo;
import fr.fastconnect.factory.tibco.bw.maven.AbstractBWMojo;
import fr.fastconnect.factory.tibco.bw.maven.exception.BinaryMissingException;

/**
 * <p>
 * This goal creates a TIBCO BusinessWorks Projib based on the configuration
 * given in the project's POM.
 * </p>
 * <p>
 * The most important parameters are:
 * </p>
 * <ul>
 * 	<li><b>bw.project.library.builder</b> : the location of the Library
 * Builder file inside the TIBCO BusinessWorks project</li>
 * 	<li><b>bw.project.location</b> : the TIBCO BusinessWorks sources
 * location relatively to the POM</li>
 * </ul>
 * <p>
 * Other parameters have default values set with the global values of
 * "<a href="./documentation/configuration.html#Maven_settingsxml_file">settings.xml</a>"
 * file.
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
@Mojo( name="compile-projlib",
defaultPhase=LifecyclePhase.COMPILE )
public class CompileProjlibMojo extends AbstractBWArtifactMojo {

	protected final static String TIBCO_DESIGNER_BINARY_NOTFOUND = "The TIBCO Designer binary can't be found.";
	protected final static String BUILD_PROJLIB_FAILED = "The build of the Projlib file has failed.";
	protected final static String BUILDING_PROJLIB = "Building the Projlib...";

	/**
	 * Path to the LibBuilder relatively to the BusinessWorks project path.
	 */
	@Parameter ( property="bw.project.library.builder", required = true)
	private String libBuilder;

	/**
	 * Path to the TIBCO Designer "buildlibrary" binary.
	 * 
	 */
	@Parameter ( property="buildlibrary.path", required = true)
	private File tibcoBuildLibraryPath;
	    
	private void checkDesignerBuildLibrary() throws MojoExecutionException {
		if (tibcoBuildLibraryPath == null ||
		   !tibcoBuildLibraryPath.exists() ||
		   !tibcoBuildLibraryPath.isFile()) {
			designerBuildLibraryPathNotFound();
		}		
	}

	private void designerBuildLibraryPathNotFound() throws MojoExecutionException {
		throw new BinaryMissingException(TIBCO_DESIGNER_BINARY_NOTFOUND);		
	}

	protected String getArtifactFileExtension() {
		return PROJLIB_EXTENSION;
	}
	
	/**
	 *  This calls the "buildlibrary" binary found in TIBCO Designer to build
	 * a Projlib for the {@link AbstractBWMojo#project}, defined by the {@link CompileProjlibMojo#libBuilder}.
	 * 
	 * @param outputFile, the path where the Projlib output will be created
	 * @throws MojoExecutionException
	 * @throws IOException
	 */
	private void buildProjlib(File outputFile) throws MojoExecutionException, IOException {
		checkDesignerBuildLibrary();
		assert(outputFile != null);

		ArrayList<String> arguments = new ArrayList<String>();		
		arguments.add("-lib"); // path of the libbuilder relative to the BW project path
		arguments.add(libBuilder);
		arguments.add("-p"); // BW project path
		arguments.add(buildSrcDirectory.getAbsolutePath());
		arguments.add("-o"); // output file
		arguments.add(outputFile.getAbsolutePath());
		arguments.add("-x"); // overwrite the output
		if (!hideLibraryResources) {
			arguments.add("-v"); // validate the project
		}

		getLog().info(BUILDING_PROJLIB);

		ArrayList<File> tras = new ArrayList<File>();
		tras.add(tibcoBuildLibraryTRAPath);
		if (tibcoBuildLibraryUseDesignerTRA) {
			tras.add(tibcoDesignerTRAPath);
		}
		launchTIBCOBinary(tibcoBuildLibraryPath, tras, arguments, directory, BUILD_PROJLIB_FAILED);
	}

	public void execute() throws MojoExecutionException {
    	if (skipCompile || skipProjlibCompile) {
    		getLog().info(SKIPPING);
    		
            File outputFile = getOutputFile();
           	if (outputFile != null && !outputFile.exists() && touchProjlibIfSkipped) {
           		// Projlib was not created because compilation is skipped
           		// however we "touch" the Projlib file so there is an empty Projlib file created
           		try {
           			outputFile.getParentFile().mkdirs();
					outputFile.createNewFile();
				} catch (IOException e) {
					throw new MojoExecutionException(e.getLocalizedMessage(), e);
				}
           	}
            if (outputFile != null && outputFile.exists()) {
            	attachArtifact(outputFile);
            } else {
            	getLog().warn(WARN_NO_ARTIFACT_ATTACHED);
            }
    		return;
    	}

		if (isCurrentGoal("bw:launch-designer")) {
			return; // ignore
		}

		super.execute();

		File outputFile = getOutputFile();
		
		try {
			buildProjlib(outputFile);
		} catch (IOException e) {
			throw new MojoExecutionException(BUILD_PROJLIB_FAILED, e);
		}
		
		attachArtifact(outputFile);
	}

    private void attachArtifact(File outputFile) {
        getProject().getArtifact().setFile(outputFile);
    }
}
