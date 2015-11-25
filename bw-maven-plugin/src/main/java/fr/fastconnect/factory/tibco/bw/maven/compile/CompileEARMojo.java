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
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProjectHelper;

import fr.fastconnect.factory.tibco.bw.maven.AbstractBWArtifactMojo;
import fr.fastconnect.factory.tibco.bw.maven.AbstractBWMojo;
import fr.fastconnect.factory.tibco.bw.maven.exception.BinaryMissingException;

/**
 * <p>
 * This goal creates a TIBCO BusinessWorks EAR based on the configuration given in the project's POM.
 * </p>
 * <p>
 * The most important parameters are:
 * </p>
 * <ul>
 * <li><b>bw.project.archive.builder</b> : the location of the Enterprise Archive file inside the TIBCO BusinessWorks project</li>
 * <li><b>bw.project.location</b> : the TIBCO BusinessWorks sources location relatively to the POM</li>
 * </ul>
 * <p>
 * Other parameters have default values set with the global values of "<a href="./documentation/configuration.html#Maven_settingsxml_file">settings.xml</a>"
 * file.
 * </p>
 * 
 * @author Mathieu Debove
 * 
 */
@Mojo(name = "compile-bw-ear",
        defaultPhase = LifecyclePhase.COMPILE)
public class CompileEARMojo extends AbstractBWArtifactMojo {

    protected final static String BUILDEAR_BINARY_NOTFOUND = "The TIBCO 'buildear' binary can't be found.";
    protected final static String BUILD_EAR_FAILED = "The build of the EAR file has failed.";
    protected final static String COPY_EAR_FAILED = "Unable to copy the EAR to the package directory.";
    protected final static String BUILDING_EAR = "Building the EAR...";
    protected final static String EAR_LOCATION = "Output EAR location: ";

    /**
     * Path to the Archive descriptor relatively to the BusinessWorks project
     * path.
     */
    @Parameter(property = "bw.project.archive.builder", required = true)
    protected String archiveFile;

    /**
     * Path to the TIBCO "buildear" binary.
     */
    @Parameter(property = "buildear.path", required = true)
    protected File tibcoBuildEARPath;

    /**
     * <p>
     * Whether to validate or not the EAR before building
     * (-v switch of <i>buildear</i>).
     * </p>
     */
    @Parameter(property = "buildear.validate", defaultValue = "true")
    protected Boolean tibcoBuildEARValidation;

    /**
     * Allow to delete override variables from projlib
     */
    @Parameter(property = "bw.clean.projlib.defaultVars")
    private boolean cleanDefaultVars;

    @Component
    private MavenProjectHelper projectHelper;

    private void checkBuildEAR() throws MojoExecutionException {
        if (tibcoBuildEARPath == null ||
                !tibcoBuildEARPath.exists() ||
                !tibcoBuildEARPath.isFile()) {
            tibcoBuildEARPathNotFound();
        }
    }

    private void tibcoBuildEARPathNotFound() throws MojoExecutionException {
        throw new BinaryMissingException(BUILDEAR_BINARY_NOTFOUND);
    }

    protected String getArtifactFileExtension() {
        return BWEAR_EXTENSION;
    }

    /**
     * This calls the "buildear" binary found in TIBCO TRA to build an EAR for
     * the {@link AbstractBWMojo#project}, defined by the {@link CompileEARMojo#archiveFile}.
     * 
     * @param outputFile
     *            , the path where the EAR output will be created
     * @throws MojoExecutionException
     * @throws IOException
     */
    private void buildEAR(File outputFile) throws MojoExecutionException, IOException {
        checkBuildEAR();
        // assert (outputFile != null);

        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add("-ear"); // path of the Enterprise Archive "builder" relative to the BW project path
        arguments.add(archiveFile);
        arguments.add("-p"); // BW project path
        arguments.add(buildSrcDirectory.getAbsolutePath());
        arguments.add("-o"); // output file
        arguments.add(outputFile.getAbsolutePath());
        arguments.add("-x"); // overwrite the output
        if (tibcoBuildEARValidation) {
            arguments.add("-v"); // validate the project
        }
        File aliasesFile = new File(directory, ALIASES_FILE);
        if (aliasesFile.exists()) {
            arguments.add("-a");
            arguments.add(aliasesFile.getAbsolutePath());
        }
        getLog().info(BUILDING_EAR);

        ArrayList<File> tras = new ArrayList<File>();
        tras.add(tibcoBuildEARTRAPath);
        if (tibcoBuildEARUseDesignerTRA) {
            tras.add(tibcoDesignerTRAPath);
        }
        launchTIBCOBinary(tibcoBuildEARPath, tras, arguments, directory, BUILD_EAR_FAILED);
    }

    /**
     * Delete "default vars" directory of override projlib's variables
     * 
     * @throws IOException
     */
    private void cleanDefaultVars() throws IOException {
        File varsDirectory = new File(buildSrcDirectory, "defaultVars");
        cleanVarsDirectory(varsDirectory, getProjlibsDependencies());
    }

    protected void cleanVarsDirectory(File varsDirectory, List<Dependency> projlibList) {
        File[] listFiles = varsDirectory.listFiles();
        ArrayList<String> projlibNames = new ArrayList<String>();
        for (Dependency dependency : projlibList) {
            projlibNames.add(dependency.getArtifactId());
        }
        for (File dir : listFiles) {
            if (projlibNames.contains(dir.getName()) && dir.isDirectory()) {
                for (File defaultVarsFile : dir.listFiles()) {
                    cleanDirectory(defaultVarsFile);
                }
                dir.delete();
            }
        }
    }

    /**
     * Delete all file of a directory and then delete this directory
     * 
     * @param directory
     * @return
     */
    protected boolean cleanDirectory(File directory) {
        if (directory.isDirectory() && directory.listFiles().length != 0) {
            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
        return directory.delete();
    }

    private void doCleanDefaultVars() throws MojoExecutionException {
        if (cleanDefaultVars) {
            try {
                cleanDefaultVars();
            } catch (IOException e) {
                throw new MojoExecutionException(BUILD_EAR_FAILED, e);
            }
        }
    }

    public void execute() throws MojoExecutionException {
    	if (skipCompile || skipEARCompile) {
    		getLog().info(SKIPPING);
    		
            File outputFile = getOutputFile();
           	if (outputFile != null && !outputFile.exists() && touchEARIfSkipped) {
           		// EAR was not created because compilation is skipped
           		// however we "touch" the EAR file so there is an empty EAR file created
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

        doCleanDefaultVars();

        super.execute();
        checkOutputDirectory();
        File outputFile = getOutputFile();
        getLog().debug(EAR_LOCATION + outputFile.getAbsolutePath());

        try {
            buildEAR(outputFile);
        } catch (IOException e) {
            throw new MojoExecutionException(BUILD_EAR_FAILED, e);
        }

        try {
    		if (!packageDirectory.exists()) {
    			packageDirectory.mkdirs();
    		}
			FileUtils.copyFile(outputFile, new File(packageDirectory + File.separator + outputFile.getName()));
		} catch (IOException e) {
			throw new MojoExecutionException(COPY_EAR_FAILED, e);
		}
        
        attachArtifact(outputFile);
    }
    
    private void attachArtifact(File outputFile) {
        if (classifier != null) {
            projectHelper.attachArtifact(getProject(), BWEAR_TYPE, classifier, outputFile);
        } else {
            getProject().getArtifact().setFile(outputFile);
        }
    }

}
