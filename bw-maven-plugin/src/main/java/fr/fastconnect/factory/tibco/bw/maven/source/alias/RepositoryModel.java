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
package fr.fastconnect.factory.tibco.bw.maven.source.alias;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import fr.fastconnect.factory.tibco.bw.maven.source.alias.jaxb.ObjectFactory;
import fr.fastconnect.factory.tibco.bw.maven.source.alias.jaxb.Repository;

/**
 * <p>
 * This class will<ul>
 * <li>unmarshall XML file of the schema with
 * "http://www.tibco.com/xmlns/repo/types/2002" namespace to JAXB objects.
 * </li>
 * <li>
 * marshall the {@link Repository} object back to XML file with the same
 * schema.
 * </li>
 * </ul>
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
public class RepositoryModel {
//	private static final String REPOSITORY_NAMESPACE = "http://www.tibco.com/xmlns/repo/types/2002";
	
	private Repository repository;
	public Repository getRepository() {
		return repository;
	}

	private JAXBContext jaxbContext;
	private File xmlFile;

	public RepositoryModel(File xmlFile) throws JAXBException {
		this.xmlFile = xmlFile;
		initRepositoryModel();
	}
	
	/**
	 * <p>
	 * This will initialize the {@link Repository} object which is a JAXB
	 * representation of the "Repository" root-element of TIBCO ".aliaslib"
	 * files using the schema with "http://www.tibco.com/xmlns/repo/types/2002"
	 * namespace.
	 * </p>
	 *
	 * @throws JAXBException
	 */
	private void initRepositoryModel() throws JAXBException {
		jaxbContext = JAXBContext.newInstance(ObjectFactory.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Object o =  jaxbUnmarshaller.unmarshal(xmlFile);
		this.repository = (Repository) o;
	}
	
	/**
	 * <p>
	 * This will marshall the object back to the XML file.
	 * </p>
	 * 
	 * @throws JAXBException
	 */
	public void save() throws JAXBException {
		Marshaller m = jaxbContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);		
		m.marshal(repository, xmlFile);
	}
}
