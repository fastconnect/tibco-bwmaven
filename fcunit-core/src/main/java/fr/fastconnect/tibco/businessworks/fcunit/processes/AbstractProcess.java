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
package fr.fastconnect.tibco.businessworks.fcunit.processes;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.fastconnect.tibco.businessworks.fcunit.BWResource;
import fr.fastconnect.tibco.businessworks.fcunit.ProjectBaseDir;

public abstract class AbstractProcess extends BWResource {
		
	public AbstractProcess(String path) {
		super(path);
	}

	/**
	 * 
	 * @return le noeud dont le chemin est xPathExpression
	 */
	public Node getNodeFromXPath(String xPathExpression) {
		String baseDirectory = ProjectBaseDir.getProjectBaseDir();
		
		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		
		File documentFile = new File(baseDirectory + getPath());
		if (!documentFile.exists() || documentFile.isDirectory()) {
			return null;
		}
		Document document = null;
		try {
			document = builder.parse(documentFile);
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		XPath xpath = XPathFactory.newInstance().newXPath();
		NamespaceContext pdNamespace = new NamespaceContext() {
			
			public Iterator<?> getPrefixes(String namespaceURI) {
				return null;
			}
			
			public String getPrefix(String namespaceURI) {
				if("http://xmlns.tibco.com/bw/process/2003".equals(namespaceURI)){
					return "pd";
				}else{
					return null;
				}
			}
			
			public String getNamespaceURI(String prefix) {
				if("pd".equals(prefix)){
					return "http://xmlns.tibco.com/bw/process/2003";
				}else{
					return null;
				}
			}
		};
		
		xpath.setNamespaceContext(pdNamespace);
		String expression = xPathExpression;
		try {
			Node widgetNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
			return widgetNode;
		} catch (XPathExpressionException e) {
			e.printStackTrace(); // FIXME : remove printStackTrace()
		}

		return null;
	}

	public abstract boolean addProcessToList(HashMap<String, ? extends AbstractProcess> processes);

}
