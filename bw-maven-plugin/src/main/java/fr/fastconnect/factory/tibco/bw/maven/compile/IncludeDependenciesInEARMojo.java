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
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.mojo.truezip.Fileset;
import org.codehaus.mojo.truezip.TrueZipFileSet;
import org.codehaus.mojo.truezip.internal.DefaultTrueZip;
import org.jaxen.JaxenException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.fs.archive.zip.ZipDriver;
import de.schlichtherle.truezip.socket.sl.IOPoolLocator;
import fr.fastconnect.factory.tibco.bw.maven.AbstractBWArtifactMojo;

/**
 * <p>
 * This goal includes the JAR dependencies of the runtime scope inside the
 * "lib.zip" of the TIBCO BusinessWorks EAR.<br/>
 * It allows to load seamlessly the transitive dependencies of JARs of the
 * TIBCO BusinessWorks project and avoid the ClassNotFoundException issues when
 * the application is deployed on a TIBCO domain.
 * </p>
 * <p>
 * This step can be ignored by setting <i>includeTransitiveJARsInEAR</i> to
 * <i>false</i>.
 * </p>
 * 
 * @see UpdateAliasesLibsMojo
 * @author Mathieu Debove
 * 
 */
@Mojo( name="include-dependencies-in-bw-ear",
defaultPhase=LifecyclePhase.COMPILE )
public class IncludeDependenciesInEARMojo extends AbstractBWArtifactMojo {

	/**
	 * Whether to add JARs files inside EAR.
	 */
	@Parameter (property="includeTransitiveJARsInEAR", defaultValue="true")
	public Boolean includeTransitiveJARsInEAR;

	/**
	 * Whether to rename JARs files inside EAR without their version.
	 */
	@Parameter (property="removeVersionFromFileNames", defaultValue="false")
	public Boolean removeVersionFromFileNames;
	
	@Override
	protected String getArtifactFileExtension() {
		return BWEAR_EXTENSION;
	}

	private DefaultTrueZip truezip;
	
	/**
	 * <p>
	 * This methods copies the transitive JAR dependencies of the project inside
	 * the "WEB-INF/lib" folder of the "lib.zip" subarchive of the TIBCO
	 * BusinessWorks EAR archive.
	 * </p>
	 * 
	 * @param ear, the TIBCO BusinessWorks EAR archive file
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws JaxenException 
	 */
	private void copyRuntimeJARsInEAR(File ear) throws IOException, JDOMException {
		Fileset fileSet = new Fileset();
		fileSet.setDirectory(buildLibDirectory.getAbsolutePath());

		for (Dependency dependency : this.getJarDependencies()) {
			String jarName = getJarName(dependency, false);
			
			fileSet.addInclude(jarName); // using jarName because files are all in buildLibDirectory
		}
		
		String ouptutDirectory = ear.getAbsolutePath() + File.separator + "lib.zip" + File.separator + "WEB-INF" + File.separator + "lib";
        fileSet.setOutputDirectory(ouptutDirectory);        
        
        if (fileSet.getIncludes() != null && !fileSet.getIncludes().isEmpty()) {
        	truezip.copy(fileSet);
        }
		truezip.sync();
		
		if (removeVersionFromFileNames) {
			removeVersionFromFileNames(ouptutDirectory, ear);

			truezip.sync();
		}
	}

	private void removeVersionFromFileNames(String ouptutDirectory, File ear) throws IOException, JDOMException {
		for (Dependency dependency : this.getJarDependencies()) {
			Pattern p = Pattern.compile("(.*)-" + dependency.getVersion() + JAR_EXTENSION);

			String includeOrigin = getJarName(dependency, false);
			String includeDestination;

			Matcher m = p.matcher(includeOrigin);
			if (m.matches()) {
				includeDestination = m.group(1)+JAR_EXTENSION;
				
				truezip.moveFile(new TFile(ouptutDirectory + File.separator + includeOrigin), new TFile(ouptutDirectory + File.separator + includeDestination));
				
				updateAlias(includeOrigin, includeDestination, ear);
			}
		}

		truezip.sync();
	}

	private void updateAlias(String includeOrigin, String includeDestination, File ear) throws JDOMException, IOException, JDOMException {
		TFile xmlTIBCO = new TFile(ear.getAbsolutePath() + File.separator + "TIBCO.xml");
		String tempPath = ear.getParentFile().getAbsolutePath() + File.separator + "TIBCO.xml";
		TFile xmlTIBCOTemp = new TFile(tempPath);

		truezip.copyFile(xmlTIBCO, xmlTIBCOTemp);

		File xmlTIBCOFile = new File(tempPath);

		SAXBuilder sxb = new SAXBuilder();
		Document document = sxb.build(xmlTIBCOFile);

		XPath xpa = XPath.newInstance("//dd:NameValuePairs/dd:NameValuePair[starts-with(dd:name, 'tibco.alias') and dd:value='" + includeOrigin + "']/dd:value");
		xpa.addNamespace("dd", "http://www.tibco.com/xmlns/dd");

		Element singleNode = (Element) xpa.selectSingleNode(document);
		if (singleNode != null) {
			singleNode.setText(includeDestination);
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat().setIndent("    "));
			xmlOutput.output(document, new FileWriter(xmlTIBCOFile));

			truezip.copyFile(xmlTIBCOTemp, xmlTIBCO);
		}

		updateAliasInPARs(includeOrigin, includeDestination, ear);
	}

	private void updateAliasInPARs(String includeOrigin, String includeDestination, File ear) throws IOException, JDOMException {
		TrueZipFileSet pars = new TrueZipFileSet();
		pars.setDirectory(ear.getAbsolutePath());
		pars.addInclude("*.par");
		List<TFile> parsXML = truezip.list(pars);
		for (TFile parXML : parsXML) {
			TFile xmlTIBCO = new TFile(parXML, "TIBCO.xml");

			String tempPath = ear.getParentFile().getAbsolutePath() + File.separator + "TIBCO.xml";
			TFile xmlTIBCOTemp = new TFile(tempPath);

			truezip.copyFile(xmlTIBCO, xmlTIBCOTemp);

			File xmlTIBCOFile = new File(tempPath);

			SAXBuilder sxb = new SAXBuilder();
			Document document = sxb.build(xmlTIBCOFile);

			XPath xpa = XPath.newInstance("//dd:NameValuePairs/dd:NameValuePair[dd:name='EXTERNAL_JAR_DEPENDENCY']/dd:value");
			xpa.addNamespace("dd", "http://www.tibco.com/xmlns/dd");

			Element singleNode = (Element) xpa.selectSingleNode(document);
			if (singleNode != null) {
				String value = singleNode.getText().replace(includeOrigin, includeDestination);
				singleNode.setText(value);
				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat().setIndent("    "));
				xmlOutput.output(document, new FileWriter(xmlTIBCOFile));

				truezip.copyFile(xmlTIBCOTemp, xmlTIBCO);
			}
		}
	}

	public void execute() throws MojoExecutionException {
    	if (skipCompile || skipEARCompile) {
    		getLog().info(SKIPPING);
    		return;
    	}

		if (isCurrentGoal("bw:launch-designer") || !includeTransitiveJARsInEAR) {
			return; // ignore
		}

		super.execute();

		File ear = getProject().getArtifact().getFile(); // EAR generated by "compile-bw-ear" goal
		if (ear == null) {
			ear = getOutputFile();
		}
		getLog().debug("Using EAR : " + ear.getAbsolutePath());

		TConfig.get().setArchiveDetector( new TArchiveDetector( TArchiveDetector.NULL, new Object[][] {
				{ "zip|kar|par|ear", new ZipDriver( IOPoolLocator.SINGLETON ) },
		} ) );

		truezip = new DefaultTrueZip();

		try {
			this.copyRuntimeJARsInEAR(ear);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (JDOMException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

}
