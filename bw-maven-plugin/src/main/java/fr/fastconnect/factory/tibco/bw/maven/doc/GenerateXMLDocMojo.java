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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tibco.xmlns.bw.process._2003.Activity;
import com.tibco.xmlns.bw.process._2003.Label;
import com.tibco.xmlns.bw.process._2003.ProcessDefinition;

import fr.fastconnect.factory.tibco.bw.maven.doc.jaxb.Bwdoc;
import fr.fastconnect.factory.tibco.bw.maven.doc.jaxb.Bwdoc.CalledProcesses;
import fr.fastconnect.factory.tibco.bw.maven.doc.jaxb.Bwdoc.Labels;
import fr.fastconnect.factory.tibco.bw.maven.doc.jaxb.ObjectFactory;
import fr.fastconnect.factory.tibco.bw.maven.doc.jaxb.Process;
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
@Mojo( name="generate-doc-xml",
defaultPhase=LifecyclePhase.PROCESS_SOURCES )
@Execute( goal="generate-doc-xml", phase=LifecyclePhase.PROCESS_SOURCES)
public class GenerateXMLDocMojo extends AbstractDocMojo {

	private File xmlDirectory;
	
	private ProcessDefinition getProcessModel(File f) throws MojoExecutionException {
		ProcessDefinition result = null;
		try {
			ProcessModel pm = new ProcessModel(f);
			result = pm.getProcess();
		} catch (JAXBException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return result;
	}
	
	@Override
	public void processFile(File f) throws MojoExecutionException {
		ProcessDefinition pd = getProcessModel(f);
		if (pd == null) return;

		String name = AbstractProjectsListMojo.getRelativePath(f.getAbsolutePath(),
				                                              getDirectoryToProcess().getAbsolutePath(),
				                                              File.separator);

		//File out = new File(xmlDirectory + File.separator + f.getName() + ".xml");
		File out = new File(xmlDirectory + File.separator + name + ".xml");
		out.getParentFile().mkdirs();
		
		getLog().debug("Processing '" + f.getAbsolutePath() + "' to '" + out.getAbsolutePath());

		// description of the process
		Bwdoc doc = new Bwdoc();
		doc.setDescription(pd.getDescription());

		if (doc.getLabels() == null) {
			doc.setLabels(new Labels());
		}

		for (Label l : pd.getLabel()) {
			fr.fastconnect.factory.tibco.bw.maven.doc.jaxb.Label label = new fr.fastconnect.factory.tibco.bw.maven.doc.jaxb.Label();
			label.setName(l.getName());
			label.setDescription(l.getDescription());
			doc.getLabels().getLabel().add(label);
		}

		if (doc.getCalledProcesses() == null) {
			doc.setCalledProcesses(new CalledProcesses());
		}

		// retrieve "Call Process" activities
		for (Activity a : pd.getActivity()) {
			if (a.getType().equals("com.tibco.pe.core.CallProcessActivity")) {
				String pathValue = "";
				Element configElement = (Element) a.getConfig();
				NodeList nodes = configElement.getElementsByTagName("processName");
				if (nodes.getLength() > 0) {
					Node node = nodes.item(0).getFirstChild();
					if (node != null) {
						pathValue = node.getNodeValue();
					}
				}
				Process p = new Process();
				p.setName(a.getName());
				p.setPath(pathValue);
				doc.getCalledProcesses().getCalledProcess().add(p);
			}
		} 
		//
		
		try {
			save(doc, out);
		} catch (JAXBException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	@Override
	public void execute() throws MojoExecutionException {
		initXMLDirectory();
		
		super.execute();
	}
	
	private void initXMLDirectory() {
		xmlDirectory = new File(docDirectory + File.separator + "xml");

		getLog().debug(xmlDirectory.getAbsolutePath());
		if (!xmlDirectory.exists()) {
			xmlDirectory.mkdirs();
		}		
	}

	private void save(Bwdoc doc, File f) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		Marshaller m = jaxbContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);		
		m.marshal(doc, f);
	}
	
}
