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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * <p>
 * This abstract class is aimed at preparing the build of a TIBCO artefact, 
 * either EAR, Projlib, deployment POM, XML configuration file...<br />
 * 
 * Because this class inherits from {@link AbstractBWMojo}, a TIBCO environment
 * will be initialized before building the artefact. The created artefact will
 * hence be <b>the same on every platform</b>.
 * </p>
 * 
 * @author Mathieu Debove
 * 
 */
public abstract class AbstractBWArtifactMojo extends AbstractBWMojo {

    protected final static String WARN_NO_ARTIFACT_ATTACHED  = "Could not attach artifact.";

    /**
     * Whether to skip the compilation of EAR <b>and</b> Projlib.<br /><br />
     * 
     * NB: shall be used with 'bw.package.skip', 'maven.install.skip' and
     * 'maven.deploy.skip' set to true.
     */
    @Parameter(property = "bw.compile.skip", required=false, defaultValue="false")
    protected Boolean skipCompile;

    /**
     * Whether to skip the compilation of EAR.<br /><br />
     * 
     * NB: shall be used with 'bw.package.skip', 'maven.install.skip' and
     * 'maven.deploy.skip' set to true.
     */
    @Parameter(property = "bw.compile.ear.skip", required=false, defaultValue="false")
    protected Boolean skipEARCompile;

    /**
     * Whether to "touch" the EAR file when EAR compilation is skipped.<br />
     * <br />
     * 
     * NB: must be used with 'bw.compile.skip' or 'bw.compile.ear.skip' set to
     * true.
     */
    @Parameter(property = "bw.compile.ear.skip.touch", required=false, defaultValue="false")
    protected Boolean touchEARIfSkipped;

    /**
     * Whether to skip the compilation of Projlib.<br /><br />
     * 
     * NB: shall be used with 'bw.package.skip', 'maven.install.skip' and
     * 'maven.deploy.skip' set to true
     */
    @Parameter(property = "bw.compile.projlib.skip", required=false, defaultValue="false")
    protected Boolean skipProjlibCompile;

    /**
     * Whether to "touch" the Projlib file when Projlib compilation is skipped.
     * <br /><br />
     * 
     * NB: must be used with 'bw.compile.skip' or 'bw.compile.projlib.skip' set
     * to true.
     */
    @Parameter(property = "bw.compile.projlib.skip.touch", required=false, defaultValue="false")
    protected Boolean touchProjlibIfSkipped;

    /**
     * Whether to skip the package goals.
     * 
     * NB: shall be used with 'maven.install.skip' and 'maven.deploy.skip' set
     * to true.
     */
    @Parameter(property = "bw.package.skip", required=false, defaultValue="false")
    protected Boolean skipPackage;

    /**
     * Whether to "touch" the final deployment descriptor file when final
     * deployment descriptor generation is skipped.<br /><br />
     * 
     * NB: must be used with 'bw.package.skip'  set to true.
     */
    @Parameter(property = "bw.package.skip.deploy.descriptor.final.touch", required=false, defaultValue="false")
    protected Boolean touchFinalDeploymentDescriptorIfSkipped;

    /**
     * Name of the generated artifact (without file extension).
     */
    @Parameter(property = "project.build.finalName", required = true)
    protected String finalName;

    protected abstract String getArtifactFileExtension(); // abstract because it can be ".ear", ".projlib", ".xml", ".pom" ...

    @Parameter(property = "project.build.classifier")
    protected String classifier;

    /**
     * This returns a {@link DefaultArtifact} object with the same groupId,
     * artifactId, version and scope as the main artifact of the project
     * (for instance 'bw-ear' or 'projlib').
     * 
     * This {@link DefaultArtifact} will have its own {@link type} and
     * {@link classifier}.
     * 
     * @param a
     * @param type
     * @param classifier
     * @return
     */
	private Artifact extractArtifact(Artifact a, String type, String classifier) {
		if (a == null) {
			return a;
		}

		Artifact result = new DefaultArtifact(a.getGroupId(),
				                              a.getArtifactId(),
				                              a.getVersionRange(),
				                              a.getScope(),
				                              type,
				                              classifier,
				                              new DefaultArtifactHandler(type));
		
		return result;
	}

	
	protected void attachFile(File f, String type, String classifier) {
		Artifact artifact = extractArtifact(getProject().getArtifact(), type, classifier);
		artifact.setFile(f);
		getProject().addAttachedArtifact(artifact);
	}

    /**
     * Retrieves the full path of the artifact that will be created.
     * 
     * @param basedir, the directory where the artifact will be created
     * @param finalName, the name of the artifact, without file extension
     * @param classifier
     * @return a {@link File} object with the path of the artifact
     */
    protected File getArtifactFile(File basedir, String finalName, String classifier) {
        if (classifier == null) {
            classifier = "";
        } else if (classifier.trim().length() > 0 && !classifier.startsWith("-")) {
            classifier = "-" + classifier;
        }

        return new File(basedir, finalName + classifier + getArtifactFileExtension());
    }

    /**
     * @return the Maven artefact as a {@link File}
     */
    protected File getOutputFile() {
        return getArtifactFile(outputDirectory, finalName, classifier);
    }

    /**
     * This will check whether {@code outputDirectory} exists and is a
     * directory. Otherwise it will be created.
     */
    protected void checkOutputDirectory() {
        if (!outputDirectory.exists() || !outputDirectory.isDirectory()) {
            outputDirectory.mkdirs();
        }
    }

    public void execute() throws MojoExecutionException {
        super.execute();

        getOutputFile().getParentFile().mkdir(); // create output directory
    }

}
