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
package fr.fastconnect.tibco.businessworks.fcunit.xml;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.AssertionFailedError;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Mathieu Debove
 *
 */
public class FCDiff {

	//private Diff diff;
	private DetailedDiff detailedDiff;
	
	private Document expectedDoc;
	private Document actualDoc;
	
	private ArrayList<String> ignoredXPaths;
	private ArrayList<String> ignoredAttributesXPaths;
	private ArrayList<String> ignoredBranches;
	
	/**
	 * Cette méthode permet de reformater le contenu d'une chaîne XML
	 * et donc de s'affranchir des problèmes liés au formatage (tabulations, espaces, retours chariot, ...).
	 * 
     * @param document
     * @return une chaîne de caractères "normalisée" créé à partir d'un document Document Object Model
     */
    public String formatDocumentForTesting(Document document) {
        try {
            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);

            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * @param fromString
     * @return Un document {@link Document} au "format" Document Object Model créé à partir de la chaîne {@link fromString}
     */
    public Document getDocumentFromString(String fromString) {
    	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace(); // FIXME : remove printStackTrace()
		}
	
		Document document = null;
		try {
			document = documentBuilder.parse(new InputSource(new StringReader(fromString)));
		} catch (SAXException e) {
			e.printStackTrace(); // FIXME : remove printStackTrace()
		} catch (IOException e) {
			e.printStackTrace(); // FIXME : remove printStackTrace()
		}
		
		return document;
    }

    public Document removeIgnoredBranches(Document document) {
		for (String branch : getIgnoredBranches()) {
	        XpathEngine simpleXpathEngine = XMLUnit.newXpathEngine();
	        
	        NodeList nodeList;
			try {
				nodeList = simpleXpathEngine.getMatchingNodes(branch, document);
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node parentNode = nodeList.item(i).getParentNode();
					parentNode.removeChild(nodeList.item(i));
				}
			} catch (XpathException e) {
				e.printStackTrace(); // FIXME : remove printStackTrace()
			}
		}
		return document;
	}

	/**
     * 
	 * @param expectedXML : le résultat attendu du process testé sous forme d'XML
	 * @param actualXML   : le résultat réel du process testé sous forme d'XML
     * @throws SAXException
     * @throws IOException
     */
    public FCDiff(String expectedXML, String actualXML) throws SAXException, IOException {
    	expectedDoc = getDocumentFromString(expectedXML);
    	actualDoc = getDocumentFromString(actualXML);
		expectedXML = formatDocumentForTesting(expectedDoc);
		actualXML = formatDocumentForTesting(actualDoc);
		
		this.detailedDiff = new DetailedDiff(new Diff(expectedXML, actualXML));
		this.ignoredXPaths = new ArrayList<String>();
		this.ignoredAttributesXPaths = new ArrayList<String>();
		this.ignoredBranches = new ArrayList<String>();
	}

    /**
     * 
	 * @return true si les deux résultats XML sont identiques (en ignorant les noeuds définis dans {@link FCDiff#ignoredXPaths}, false sinon.
     */
	public boolean identical() {
	
		expectedDoc = removeIgnoredBranches(expectedDoc);
		actualDoc = removeIgnoredBranches(actualDoc);
		
        DifferenceListener myDifferenceListener = new DifferenceListener() {
			public void skippedComparison(Node arg0, Node arg1) {}
			
			public int differenceFound(Difference difference) {
				if (difference.getId() == DifferenceConstants.SCHEMA_LOCATION_ID) {
					//System.out.println("SCHEMA_LOCATION_ID");
					return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
				}
				else if (isPathIgnored(difference.getTestNodeDetail().getXpathLocation())
				 || isPathIgnored(difference.getControlNodeDetail().getXpathLocation())) {
					return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
		        } else {
		            return RETURN_ACCEPT_DIFFERENCE;
			    }
			}
		};
		
		String expectedXML = formatDocumentForTesting(expectedDoc);
		String actualXML = formatDocumentForTesting(actualDoc);
		Diff diff;
		try {
			diff = new Diff(expectedXML, actualXML);
			diff.overrideDifferenceListener(myDifferenceListener);
			return diff.identical();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
		//this.diff.overrideDifferenceListener(myDifferenceListener);
		//boolean isIdentical = this.diff.identical();
		//return isIdentical;
	}

	/**
	 * 
	 * @return true si les deux résultats XML sont identiques en ignorant les champs supplémentaires dans le résultat du process ("actual result")
	 * qui ne sont pas présents dans le résultat attendu ("expected result").
	 */
	public boolean identicalExceptAdditionalFieldsInActual() {
		boolean notIdentical = false;
		
		for (Difference difference : this.getAllDifferences()) {
			if (difference.getId() == DifferenceConstants.CHILD_NODELIST_LENGTH_ID) {
				// possibilité d'accepter des champs en plus dans le "actual"
				int expectedCount = Integer.parseInt(difference.getControlNodeDetail().getValue());
				int actualCount = Integer.parseInt(difference.getTestNodeDetail().getValue());
				
				if (expectedCount > actualCount) { // on accepte les différences s'il y a plus dans le actual que dans l'expected mais pas l'inverse
					notIdentical = true;
				}
			}
		}
		
		return (this.identical() && !notIdentical);
	}

	/**
	 * 
	 * @return Toutes les différences ({@link org.custommonkey.xmlunit.Difference}) entre les deux résultats XML ("actual" et "expected").
	 */
	@SuppressWarnings("unchecked")
	public List<Difference> getAllDifferences() {
		return detailedDiff.getAllDifferences();
	}
	
	/**
	 * 
	 * @return Toutes les différences ({@link org.custommonkey.xmlunit.Difference}) entre les deux résultats XML ("actual" et "expected").
	 */
	public List<Difference> getDifferencesWithoutAdditionalFieldsInActual() {
		List<Difference> differences = new ArrayList<Difference>(this.getAllDifferences());
		
		differences.removeAll(getAdditionalFieldsInActual());
		
		return differences;
	}
	
	/**
	 * 
	 * @return La liste des champs supplémentaires présents dans le résultat d'un process ("actual result")
	 * et non présent dans le résultat attendu ("expected result") sous forme de différences ({@link org.custommonkey.xmlunit.Difference}).
	 */
	public List<Difference> getAdditionalFieldsInActual() {
		List<Difference> differences = new ArrayList<Difference>(this.getAllDifferences()); // deep-copy of the differences
		
	    for (Iterator<Difference> it = differences.iterator(); it.hasNext(); ) {
	    	Difference difference = it.next();
	        if (difference.getId() == DifferenceConstants.CHILD_NODELIST_LENGTH_ID) {
				int expectedCount = Integer.parseInt(difference.getControlNodeDetail().getValue());
				int actualCount = Integer.parseInt(difference.getTestNodeDetail().getValue());
				
				if (expectedCount > actualCount) { // on accepte les différences s'il y a plus dans le actual que dans l'expected mais pas l'inverse
		            it.remove();
				}
	        }
	        else {
	            it.remove();
	        }
	    }
	    
		return differences;
	}

	/**
	 * Définit la liste des chemins XPath ignorés au moment de la comparaison entre le résultat attendu ("expected") et le résultat "actual" du process
	 * 
	 * @param ignoredXPaths, une array de String contenant les chemins définis de façon absolue en XPath
	 */
	public void setIgnoredXPaths(ArrayList<String> ignoredXPaths) {
		this.ignoredXPaths = ignoredXPaths;		
	}

	/**
	 * 
	 * @return La liste des chemins XPath ignorés
	 */
	public ArrayList<String> getIgnoredXPaths() {
		return ignoredXPaths;
	}
	
	/**
	 * Définit la liste des chemins XPath dont les attributs sont ignorés au moment de la comparaison entre le résultat attendu ("expected") et le résultat "actual" du process
	 * 
	 * @param ignoredAttributesXPaths, une array de String contenant les chemins définis de façon absolue en XPath
	 */
	public void setIgnoredAttributesXPaths(ArrayList<String> ignoredAttributesXPaths) {
		this.ignoredAttributesXPaths = ignoredAttributesXPaths;		
	}
	
	/**
	 * 
	 * @return La liste des chemins XPath ignorés
	 */
	public ArrayList<String> getIgnoredAttributesXPaths() {
		return ignoredAttributesXPaths;
	}

	/**
	 * Définit la liste des branches ignorées au moment de la comparaison entre le résultat attendu ("expected") et le résultat "actual" du process
	 * 
	 * @param ignoredBranches, une array de String contenant les branches définies de façon absolue en XPath
	 */
	public void setIgnoredBranches(ArrayList<String> ignoredBranches) {
		this.ignoredBranches = ignoredBranches;				
	}

	/**
	 * 
	 * @return La liste des branches XML ignorées
	 */
	public ArrayList<String> getIgnoredBranches() {
		return ignoredBranches;
	}
	
	/**
	 * 
	 * @param path1
	 * @param path2
	 * @return true si les deux chemins path1 et path2 représente le même noeud dans le document "actual" (résultat réel du process testé), false sinon
	 */
	public boolean pathsAreEqual(String path1, String path2) {
		if (path1 == null || path2 == null) return false;
		
		// check that both nodes from these XPaths exist
        XpathEngine simpleXpathEngine = XMLUnit.newXpathEngine();
        
        NodeList nodeList1;
		try {
			nodeList1 = simpleXpathEngine.getMatchingNodes(path1, actualDoc);
	        if (nodeList1.getLength() <= 0) {
	        	return false;
	        }
		} catch (XpathException e1) {
        	return false;
		}
		NodeList nodeList2;
		try {
			nodeList2 = simpleXpathEngine.getMatchingNodes(path2, actualDoc);
			if (nodeList2.getLength() <= 0) {
				return false;
			}
		} catch (XpathException e1) {
			return false;
		}
		
		try {
			XMLAssert.assertXpathsEqual(path1, path2, actualDoc);
		} catch (AssertionFailedError e) {
			return false;
		} catch (XpathException e) {
		}

		return true;
	}

	/**
	 * 
	 * @param xPathLocation
	 * @return true si le noeud correspondant au chemin xPathLocation est ignoré, false sinon
	 */
	public boolean isPathIgnored(String xPathLocation) {
		for (String path : getIgnoredXPaths()) {
			if (xPathLocation.equals(path)) {
				return true;
			}
			if (pathsAreEqual(xPathLocation, path)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * 
	 * @param xPathLocation
	 * @return true si les attributs du noeud correspondant au chemin xPathLocation sont ignorés, false sinon
	 */
	public boolean isPathIgnoredForAttributes(String xPathLocation) {
//		for (String path : getIgnoredAttributesXPaths()) {
//			if (xPathLocation.equals(path)) {
//				return true;
//			}
//			if (pathsAreEqual(xPathLocation, path)) {
//				return true;
//			}
//		}
		
		return true;
	}

}
