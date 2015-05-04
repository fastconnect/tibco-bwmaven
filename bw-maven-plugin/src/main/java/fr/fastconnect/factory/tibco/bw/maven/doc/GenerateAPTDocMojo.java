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
package fr.fastconnect.factory.tibco.bw.maven.doc;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import fr.fastconnect.factory.tibco.bw.maven.source.AbstractProjectsListMojo;

/**
 * <p>
 * This goal generates an APT documentation file from TIBCO BusinessWorks source
 * files.
 * </p>
 * <p>
 * The "generate-doc-xml" goal is used to generate XML files.
 * </p>
 * 
 * @author Mathieu Debove
 * 
 */
@Mojo( name="generate-doc-apt",
defaultPhase=LifecyclePhase.PROCESS_SOURCES )
@Execute( goal="generate-doc-apt", phase=LifecyclePhase.PROCESS_SOURCES)
public class GenerateAPTDocMojo extends AbstractDocMojo {

	private File aptDirectory;
	
	@Override
	public void processFile(File f) throws MojoExecutionException {
		String name = AbstractProjectsListMojo.getRelativePath(f.getAbsolutePath(),
                                                               getDirectoryToProcess().getAbsolutePath(),
                                                               File.separator);

		//File out = new File(aptDirectory + File.separator + f.getName() + ".xml");
		File out = new File(aptDirectory + File.separator + name + ".apt");
		out.getParentFile().mkdirs();

		getLog().debug("Processing '" + f.getAbsolutePath() + "' to '" + out.getAbsolutePath());
		
		applyXSL(f, out, GenerateAPTDocMojo.class.getResourceAsStream("/doc/bw-to-apt.xsl"));
	}

	@Override
	public void execute() throws MojoExecutionException {
		initAPTDirectory();
		
		super.execute();
	}

	private void initAPTDirectory() {
		aptDirectory = new File(docDirectory + File.separator + "apt");

		getLog().debug(aptDirectory.getAbsolutePath());
		if (!aptDirectory.exists()) {
			aptDirectory.mkdirs();
		}		
	}

	@Override
	protected File getDirectoryToProcess() {
		return new File(docDirectory + File.separator + "xml"); // warning: must be the same as GenerateXMLDocMojo one
	}

	@Override
	protected List<File> initFiles() throws IOException {
		getLog().debug("Looking for files to process...");

		FileSet restriction = new FileSet();
		restriction.setDirectory(getDirectoryToProcess().getAbsolutePath());
		
		restriction.addInclude("**/*.xml");
		
		return AbstractProjectsListMojo.toFileList(restriction);
	}

}
