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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.ElementNSImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.fastconnect.factory.tibco.bw.maven.compile.repository.ObjectFactory;
import fr.fastconnect.factory.tibco.bw.maven.compile.repository.Repository;

/**
 * <p>
 * This class is use to manage ".archive" files known as Archive Builder in
 * TIBCO Designer.
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
public class ArchiveBuilder {

	private Repository repository;

	public ArchiveBuilder() {
		ObjectFactory of = new ObjectFactory();
		this.repository = of.createRepository();
	}

	public ArchiveBuilder(File f) {
		this();

		this.repository = this.load(f);
	}

	private ElementNSImpl getElement(ElementNSImpl parent, String elementName) {
		NodeList elements = parent.getElementsByTagName(elementName);
		ElementNSImpl element;

		if (elements != null) {
			element = (ElementNSImpl) elements.item(0);
		} else {
			element = (ElementNSImpl) new ElementImpl((CoreDocumentImpl) parent.getOwnerDocument(), elementName);
			parent.appendChild(element);
		}

		return element;
	}

	@SuppressWarnings("unchecked") // TODO: switch to Apache Commons 4.0+
	public void removeDuplicateProcesses() {
		ElementNSImpl mergedEnterpriseArchive = (ElementNSImpl) this.repository.getAny().get(0);
		ElementNSImpl mergedProcessArchive = getElement(mergedEnterpriseArchive, "processArchive");
		ElementNSImpl mergedProcessProperty = getElement(mergedProcessArchive, "processProperty");

		String content = mergedProcessProperty.getTextContent();
		if (content != null && !content.isEmpty()) {
			List<String> processes = SetUniqueList.decorate(new ArrayList<String>());
			processes.addAll(Arrays.asList(content.split(",")));
			String contentWithoutDuplicate = StringUtils.join(processes, ",");
			mergedProcessProperty.setTextContent(contentWithoutDuplicate);
		}

	}

	private Repository load(File f) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Object o =  jaxbUnmarshaller.unmarshal(f);
			return (Repository) o;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void save(File f) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Marshaller m = jaxbContext.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);		
			m.marshal(this.repository, f);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public ElementNSImpl getEnterpriseArchive() {
		List<Object> any = this.repository.getAny();
		if (any != null && any.size() > 0) {
			ElementNSImpl e = (ElementNSImpl) any.get(0);
			if (e.getLocalName().equals("enterpriseArchive")) {
				return e;
			}
		}

		return null;
	}

	private ElementNSImpl getFirstProcessArchive() {
		ElementNSImpl enterpriseArchive = this.getEnterpriseArchive();

		if (enterpriseArchive != null) {
			NodeList processArchives = enterpriseArchive.getElementsByTagName("processArchive");
			if (processArchives != null && processArchives.getLength() > 0) {
				return (ElementNSImpl) enterpriseArchive.getElementsByTagName("processArchive").item(0); // first one
			}		
		}

		return null;
	}

	public ElementNSImpl getSharedArchive() {
		ElementNSImpl enterpriseArchive = this.getEnterpriseArchive();

		if (enterpriseArchive != null) {
			NodeList sharedArchive = enterpriseArchive.getElementsByTagName("sharedArchive");
			if (sharedArchive != null && sharedArchive.getLength() > 0) {
				return (ElementNSImpl) enterpriseArchive.getElementsByTagName("sharedArchive").item(0); // only one sharedArchive is allowed
			}
		}

		return null;
	}

	public void setSharedArchiveAuthor(String textContent) {
		setProperty(getSharedArchive(), "authorProperty", textContent);
	}

	public void setEnterpriseArchiveAuthor(String textContent) {
		setEnterpriseArchiveProperty("authorProperty", textContent);
	}

	public void setEnterpriseArchiveName(String textContent) {
		setEnterpriseArchiveProperty("name", textContent);
	}

	public void setEnterpriseArchiveFileLocationProperty(String textContent) {
		setEnterpriseArchiveProperty("fileLocationProperty", textContent);
	}

	public void setEnterpriseArchiveVersionProperty(String textContent) {
		setEnterpriseArchiveProperty("versionProperty", textContent);
	}

	protected void setEnterpriseArchiveProperty(String propertyName, String textContent) {
		setProperty(getEnterpriseArchive(), propertyName, textContent);
	}

	public void setFirstProcessArchiveName(String textContent) {
		setAttribute(getFirstProcessArchive(), "name", textContent);		
	}

	protected void setAttribute(ElementNSImpl element, String attributeName, String textContent) {
		if (element != null) {
			Attr attribute = element.getAttributeNode(attributeName);

			if (attribute != null) {
				attribute.setTextContent(textContent);
			}
		}
	}

	protected void setProperty(ElementNSImpl element, String propertyName, String textContent) {
		if (element != null) {
			NodeList properties = element.getElementsByTagName(propertyName);
			ElementNSImpl property = null;
			for (int i = 0; i < properties.getLength(); i++) {
				property = (ElementNSImpl) properties.item(i);
				Node parentNode = property.getParentNode();
				if (parentNode == element) {
					break;
				}
			}
			
			if (property != null) {
				property.setTextContent(textContent);
			}
		}
	}

	// merge two ArchiveBuilder
	public void merge(File archiveFile) {
		ArchiveBuilder archiveBuilder = new ArchiveBuilder(archiveFile);
		this.merge(archiveBuilder);
	}

	public void merge(ArchiveBuilder archiveBuilder) {
		ElementNSImpl mergedEnterpriseArchive = this.getEnterpriseArchive();
		ElementNSImpl enterpriseArchive = archiveBuilder.getEnterpriseArchive();
		
		if (mergedEnterpriseArchive == null) {
			this.repository.getAny().add(enterpriseArchive);
			return;
		}

		@SuppressWarnings("unchecked")
		List<ElementNSImpl> elements = (List<ElementNSImpl>)(Object) archiveBuilder.repository.getAny(); // dirty
		
		for (ElementNSImpl e : elements) {
			if (e.getLocalName().equals("enterpriseArchive")) {
				// merged elements
				ElementNSImpl mergedProcessArchive = getElement(mergedEnterpriseArchive, "processArchive");
				ElementNSImpl mergedProcessProperty = getElement(mergedProcessArchive, "processProperty");

				// look for processArchive elements to merge
				ElementNSImpl nodes = (ElementNSImpl) e.getChildNodes();
				for (int i = 0; i < nodes.getLength(); i++) {
					Node o = nodes.item(i);
					if (!o.getClass().getName().equals("org.apache.xerces.dom.ElementNSImpl")) continue;
					ElementNSImpl n = (ElementNSImpl) o;
				    if (n != null && "processArchive".equals(n.getLocalName())) {
				    	// current project elements
				    	ElementNSImpl processArchive = (ElementNSImpl) n;
				    	ElementNSImpl processProperty = (ElementNSImpl) processArchive.getElementsByTagName("processProperty").item(0);
						
						String newContent = processProperty.getTextContent();
						String oldContent = mergedProcessProperty.getTextContent();
						if (newContent != null && !newContent.isEmpty()) {
							if (oldContent != null && !oldContent.isEmpty()) {
								newContent = oldContent + "," + newContent;
							}
							mergedProcessProperty.setTextContent(newContent);
						}
				    }
				}
			}
		}		
	}

}
