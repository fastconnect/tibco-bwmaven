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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import fr.fastconnect.factory.tibco.bw.maven.AbstractBWArtifactMojo;
import fr.fastconnect.factory.tibco.bw.maven.AbstractBWMojo;
import fr.fastconnect.factory.tibco.bw.maven.source.AbstractProjectsListMojo;
import fr.fastconnect.factory.tibco.bw.maven.source.alias.RepositoryModel;

/**
 * <p>
 * This goal modifies the ".aliaslib" files of the TIBCO BusinessWorks project
 * sources to include transitive JARs dependencies.
 * </p>
 * <p>
 * This step can be ignored by setting <i>includeTransitiveJARsInEAR</i> to
 * <i>false</i>.
 * </p>
 * 
 * @see IncludeDependenciesInEARMojo
 * @author Mathieu Debove
 * 
 */
@Mojo( name="update-alias-lib",
defaultPhase=LifecyclePhase.COMPILE )
public class UpdateAliasesLibsMojo extends AbstractBWArtifactMojo {

    protected final static String SRC_NOT_SET = "Source directory is not set. Modification of '.aliaslib' file might fail.";

	@Parameter (property="includeTransitiveJARsInEAR", defaultValue="true")
	public Boolean includeTransitiveJARsInEAR;

	/**
	 * A list of directories where this goal will look for '.aliaslib' files to
	 * update.
	 */
	@Parameter(required=false)
	public List<File> customAliasLibDirectories;

	/**
	 * Whether to keep original entries in '.aliaslib' files when updating
	 */
	@Parameter(required=false, defaultValue="true")
	public Boolean keepOriginalAliasLib;

	private List<File> aliaslibFiles; // list of ".aliaslib" files to modify
	private List<Dependency> jarDependencies; // JAR dependencies retrieved from parent class AbstractBWMojo

	@Override
	protected String getArtifactFileExtension() {
		return BWEAR_EXTENSION;
	}

	/**
	 * This method retrieves a list of ".aliaslib" files to process in the
	 * source directory (usually "target/src") which is a copy of the actual
	 * source directory used to compile the TIBCO BusinessWorks EAR.
	 */
	private List<File> initFiles() throws IOException {
		FileSet restriction = new FileSet();
		File directory = buildSrcDirectory;
		if (directory == null) {
			directory = new File(".");
			getLog().warn(SRC_NOT_SET);
		}

		getLog().debug(directory.getAbsolutePath());
		restriction.setDirectory(directory.getAbsolutePath());

		restriction.addInclude("**/*.aliaslib");

		List<File> result = AbstractProjectsListMojo.toFileList(restriction);

		if (customAliasLibDirectories != null && !customAliasLibDirectories.isEmpty()) {
			for (File customDirectory : customAliasLibDirectories) {
				getLog().debug("Looking for '.aliaslib' files in custom directory: " + customDirectory);
				FileSet customRestriction = new FileSet();
				customRestriction.setDirectory(customDirectory.getAbsolutePath());
				customRestriction.addInclude("**/*.aliaslib");
				result.addAll(AbstractProjectsListMojo.toFileList(customRestriction));
			}
		}

		getLog().debug("List of '.aliaslib' files to update: " + result);
		return result;
	}

	/**
	 * This method add an alias in the object used internally by TIBCO
	 * BusinessWorks.
	 * 
	 * @param list, an object used internally by TIBCO BusinessWorks.
	 * @param aliasName, the name of the alias as normalized b 
	 * {@link AbstractBWMojo}.
	 */
	private void addAlias(ArrayList<HashMap<String,Object>> list, String aliasName) {
		for (HashMap<String, Object> h : list) {
			String name = (String) h.get("name");
			if (name != null && name.equals(aliasName)) {
				return; // avoid duplicates
			}
		}
		HashMap<String, Object> h = new HashMap<String, Object>();
		h.put("isClasspathFile", Boolean.TRUE);
		h.put("name", aliasName);
		h.put("includeInDeployment", Boolean.TRUE);

		list.add(h);
	}

	@SuppressWarnings("unchecked") // unchecked because we know it's the type used by TIBCO BusinessWorks
	private ArrayList<HashMap<String, Object>> readXMLBean(RepositoryModel repositoryModel, File f) throws JAXBException, UnsupportedEncodingException {
		// retrieve the content of the XML Bean in the ".aliaslib" file
		String xmlBean = repositoryModel.getRepository().getName().getFILEALIASESLIST();

		// this XML bean is decoded to a Java object with the XMLDecoder
		XMLDecoder d = new XMLDecoder(new ByteArrayInputStream(xmlBean.getBytes(this.sourceEncoding)));
		ArrayList<HashMap<String, Object>> result = null;
		try {
			result = (ArrayList<HashMap<String, Object>>) d.readObject();
		} finally {
			d.close();
		}

		return result;
	}

	private void writeXMLBean(RepositoryModel repositoryModel, File f, ArrayList<HashMap<String, Object>> aliases) throws JAXBException, IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		XMLEncoder e = new XMLEncoder(os);
		e.writeObject(aliases);
		e.close();

		String xmlBean = os.toString(this.sourceEncoding);
		os.close();

		// put back the XML Bean in the ".aliaslib" file
		repositoryModel.getRepository().getName().setFILEALIASESLIST(xmlBean);
		repositoryModel.save();
	}

	/**
	 * This method adds the JAR aliases to a ".aliaslib" file
	 * 
	 * @param f, the ".aliaslib" file to update
	 * @throws MojoExecutionException
	 */
	public void processFile(File f) throws MojoExecutionException {
		try {
			RepositoryModel repositoryModel = new RepositoryModel(f);

			ArrayList<HashMap<String, Object>> aliases = readXMLBean(repositoryModel, f);

			// reset old references
			if (!keepOriginalAliasLib) {
				aliases.clear();
			}

			// adding the JAR dependencies
			for (Dependency dependency : jarDependencies) {
				addAlias(aliases, getJarAlias(dependency, false));
			}

			writeXMLBean(repositoryModel, f, aliases);
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	public void execute() throws MojoExecutionException {
		if (
			skipCompile  || skipEARCompile || 
			(
			isCurrentGoal("bw:launch-designer") || 
		   !includeTransitiveJARsInEAR
		    )
		   &&
		    (
		    customAliasLibDirectories == null ||
		    customAliasLibDirectories.isEmpty()
		    ) 
		   ) {
			getLog().info(SKIPPING);
			return; // ignore
		}
		
		super.execute();

		try {
			jarDependencies = this.getJarDependencies();
			aliaslibFiles = initFiles(); // look for ".aliaslib" files in "target/src" folder and optional directories in 'customAliasLibDirectories' 
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}

		for (File f : aliaslibFiles) {
			processFile(f);
		}
	}

}
