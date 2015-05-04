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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import fr.fastconnect.factory.tibco.bw.maven.AbstractBWMojo;
import fr.fastconnect.factory.tibco.bw.maven.source.AbstractProjectsListMojo;
import fr.fastconnect.factory.tibco.bw.maven.source.ProcessModel;

/**
 * <p>
 * This goal generates an XML documentation file from TIBCO BusinessWorks source
 * files.
 * </p>
 * 
 * @author Mathieu Debove
 * 
 */
public abstract class AbstractDocMojo extends AbstractBWMojo {

	/**
	 * Path to the output folder.
	 * 
	 * Default is "target/doc"
	 */
	@Parameter( property = "project.doc.directory",
				required=true,
				defaultValue = "target/doc" )
	protected File docDirectory;

	/**
	 * The list of files to process for documentation.
	 */
	private List<File> files;
	
	public abstract void processFile(File f) throws MojoExecutionException;
	
	/**
	 * @throws JAXBException 
	 * 
	 */
	protected String getDescription(File f) throws JAXBException {
		ProcessModel pm = new ProcessModel(f);
		
		String description = pm.getProcess().getDescription();
		
		return description;
	}
	
	@Override
	public void execute() throws MojoExecutionException {
		super.execute();
		
		if (!docDirectory.exists()) {
			docDirectory.mkdirs();
		}

		getLog().info("Generating project documentation in '" + docDirectory + '"');
		
		try {
			files = initFiles(); // look for ".process" files in "target/src" folder
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		
		for (File f : this.getFiles()) {
			processFile(f);
		}
	}

	public List<File> getFiles() {
		return files;
	}

	protected File getDirectoryToProcess() {
		return buildSrcDirectory;
	}

	protected List<File> initFiles() throws IOException {
		getLog().debug("Looking for files to process...");

		FileSet restriction = new FileSet();
		File directory = getDirectoryToProcess();
		if (directory == null) {
			directory = new File(".");
		}
		restriction.setDirectory(directory.getAbsolutePath());
		
		restriction.addInclude("**/*.process");
		
		return AbstractProjectsListMojo.toFileList(restriction);
	}

    public void applyXSL(File in, File out, InputStream inputStream) throws MojoExecutionException {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();

            Templates template = factory.newTemplates(new StreamSource(inputStream));

            Transformer xformer = template.newTransformer();

            Source source = new StreamSource(new FileInputStream(in));
            Result result = new StreamResult(new FileOutputStream(out));

            xformer.transform(source, result);
        } catch (Exception e) {
        	throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
