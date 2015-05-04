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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import org.custommonkey.xmlunit.Difference;
import org.xml.sax.SAXException;

/**
 * 
 * Cette classe permet de comparer les résultats d'un process passés en paramètre :<br/>
 * - les résultats au format XML d'un process BusinessWorks ("actual result").<br/>
 * - les résultats attendus ("expected result").<br/>
 * Cette comparaison peut ignorer la présence de champs supplémentaires dans le "actual result" si cette option est activée. Le constructeur prend en argument
 * les données XML des deux résultats nécessaires à la comparaison. Puis différentes méthodes permettent de savoir s'il y a des différences, de récupérer les
 * informations sur ces différences.
 * 
 * @author Mathieu Debove
 * 
 */
public class AssertXMLEqual implements Serializable {

	private static final int FIELD_INFO_XPATH = 0;
	private static final int FIELD_INFO_EXPECTED_VALUE = 1;
	private static final int FIELD_INFO_ACTUAL_VALUE = 2;
	private static final int FIELD_INFO_DESCRIPTIONS = 3;
	private static final int FIELD_INFO_IGNORED_XPATH = 4;
	private static final int FIELD_INFO_IGNORED_DESCRIPTION = 5;
	private static final int FIELD_INFO_ADDITIONAL_XPATH = 6;

	private static final long serialVersionUID = 3309936469070970788L;

	private FCDiff diff;

	/**
	 * 
	 * @param expectedXML
	 *            : le résultat attendu du process testé sous forme d'XML
	 * @param actualXML
	 *            : le résultat réel du process testé sous forme d'XML
	 * @throws SAXException
	 * @throws IOException
	 */
	public AssertXMLEqual(String expectedXML, String actualXML) throws SAXException, IOException {
		this.diff = new FCDiff(expectedXML, actualXML);
	}

	/**
	 * Dans cette méthode, nous considérons que les deux résultats XML doivent être identiques mais les différences liées au formatage du fichier (tabulations,
	 * espaces, retours chariot...) ne seront pas prises en compte. Enfin, les champs ignorés définis par {@link AssertXMLEqual#setIgnoredXPaths(String[])} ne
	 * seront pas considérés comme des différences.
	 * 
	 * @return true si le résultat (XML) du process est identique au résultat attendu
	 * @throws SAXException
	 * @throws IOException
	 */
	public boolean assertXMLEqual() throws SAXException, IOException {
		return diff.identical();
	}

	/**
	 * Dans cette méthode, nous considérons que les deux résultats XML doivent être identiques mais les différences liées au formatage du fichier (tabulations,
	 * espaces, retours chariot...) ne seront pas prises en compte. De plus, si le résultat du process contient des champs supplémentaires par rapport au
	 * résultat attendu, ces différences ne seront pas prises en compte. Enfin, les champs ignorés définis par {@link AssertXMLEqual#setIgnoredXPaths(String[])}
	 * ne seront pas considérés comme des différences.
	 * 
	 * @return true si le résultat (XML) du process est identique au résultat attendu (modulo les champs supplémentaires dans le "actual result")
	 * @throws SAXException
	 * @throws IOException
	 */
	public boolean assertXMLAtLeastEqual() throws SAXException, IOException {
		return diff.identicalExceptAdditionalFieldsInActual();
	}

	/**
	 * 
	 * @param acceptAdditionFieldsInActual
	 * @return La liste des chemins XPath des champs différents (entre le "actual" et le "expected" result).
	 * @throws SAXException
	 * @throws IOException
	 * 
	 * @see AssertXMLEqual#getDifferentFields
	 */
	public String[] getDifferentFieldsPaths(boolean acceptAdditionFieldsInActual) throws SAXException, IOException {
		return getDifferentFields(FIELD_INFO_XPATH, acceptAdditionFieldsInActual);
	}

	/**
	 * 
	 * @param acceptAdditionFieldsInActual
	 * @return La liste des valeurs attendues des champs différents (entre le "actual" et le "expected" result).
	 * @throws SAXException
	 * @throws IOException
	 * 
	 * @see AssertXMLEqual#getDifferentFields
	 */
	public String[] getDifferentFieldsExpectedValues(boolean acceptAdditionFieldsInActual) throws SAXException, IOException {
		return getDifferentFields(FIELD_INFO_EXPECTED_VALUE, acceptAdditionFieldsInActual);
	}

	/**
	 * 
	 * @param acceptAdditionFieldsInActual
	 * @return La liste des valeurs réelles des champs différents (entre le "actual" et le "expected" result).
	 * @throws SAXException
	 * @throws IOException
	 * 
	 * @see AssertXMLEqual#getDifferentFields
	 */
	public String[] getDifferentFieldsActualValues(boolean acceptAdditionFieldsInActual) throws SAXException, IOException {
		return getDifferentFields(FIELD_INFO_ACTUAL_VALUE, acceptAdditionFieldsInActual);
	}

	/**
	 * 
	 * @param acceptAdditionFieldsInActual
	 * @return La liste des descriptions des champs différents (entre le "actual" et le "expected" result).
	 * @throws SAXException
	 * @throws IOException
	 * 
	 * @see AssertXMLEqual#getDifferentFields
	 */
	public String[] getDifferentFieldsDescriptions(boolean acceptAdditionFieldsInActual) throws SAXException, IOException {
		return getDifferentFields(FIELD_INFO_DESCRIPTIONS, acceptAdditionFieldsInActual);
	}

	/**
	 * 
	 * @param acceptAdditionFieldsInActual
	 * @return La liste des chemins XPath des champs ignorés (définis par {@link AssertXMLEqual#setIgnoredXPaths(String[])}) .
	 * @throws SAXException
	 * @throws IOException
	 * 
	 * @see AssertXMLEqual#getDifferentFields
	 */
	public String[] getDifferentFieldsIgnoredPaths(boolean acceptAdditionFieldsInActual) throws SAXException, IOException {
		return getDifferentFields(FIELD_INFO_IGNORED_XPATH, acceptAdditionFieldsInActual);
	}

	/**
	 * 
	 * @param acceptAdditionFieldsInActual
	 * @return La liste des descriptions des champs ignorés (définis par {@link AssertXMLEqual#setIgnoredXPaths(String[])}) .
	 * @throws SAXException
	 * @throws IOException
	 * 
	 * @see AssertXMLEqual#getDifferentFields
	 */
	public String[] getDifferentFieldsIgnoredDescriptions(boolean acceptAdditionFieldsInActual) throws SAXException, IOException {
		return getDifferentFields(FIELD_INFO_IGNORED_DESCRIPTION, acceptAdditionFieldsInActual);
	}

	/**
	 * 
	 * @return La liste des chemins XPath qui sont ignorés car étant des champs supplémentaires présents dans le résultat du process testé ("actual result").
	 * @throws SAXException
	 * @throws IOException
	 * 
	 * @see AssertXMLEqual#getDifferentFields
	 */
	public String[] getAdditionalFields() throws SAXException, IOException {
		return getDifferentFields(FIELD_INFO_ADDITIONAL_XPATH, true);
	}

	/**
	 * Cette fonction génère la liste des informations correspondant au code infoType.
	 * 
	 * Le champ acceptAdditionFieldsInActual conditionne la prise en compte ou non des champs supplémentaires présents dans le résultat d'un process
	 * ("actual result"). Ce comportement est utilisé par l'activité BW "AssertAtLeastEqualXML".
	 * 
	 * @param infoType
	 * @param acceptAdditionFieldsInActual
	 *            , si ce paramètre est vrai, la présence de champs supplémentaires n'est pas considérée comme une erreur.
	 * @return Une array de String contenant les informations correspondant au code infoType donné
	 * @throws SAXException
	 * @throws IOException
	 */
	public String[] getDifferentFields(int infoType, boolean acceptAdditionFieldsInActual) throws SAXException, IOException {
		ArrayList<String> differences = new ArrayList<String>();

		if (!diff.identical()) {
			java.util.List<Difference> listOfDifferences = null;
			if (infoType == FIELD_INFO_ADDITIONAL_XPATH) {
				listOfDifferences = diff.getAdditionalFieldsInActual();
			} else {
				listOfDifferences = diff.getAllDifferences();
			}

			for (Difference difference : listOfDifferences) {

				if (difference.getControlNodeDetail().getXpathLocation() == null)
					continue;
				if (difference.getTestNodeDetail().getXpathLocation() == null)
					continue;
				if (acceptAdditionFieldsInActual && diff.getAdditionalFieldsInActual().contains(difference)) {
					continue; // do not add additional fields in actual as error fields
				}

				if (diff.isPathIgnored(difference.getTestNodeDetail().getXpathLocation())) {
					switch (infoType) {
					case FIELD_INFO_IGNORED_XPATH:
						differences.add(difference.getControlNodeDetail().getXpathLocation());
						break;

					case FIELD_INFO_IGNORED_DESCRIPTION:
						differences.add(difference.getDescription());
						break;

					default:
						break;
					}
				} else {
					switch (infoType) {
					case FIELD_INFO_XPATH:
						differences.add(difference.getControlNodeDetail().getXpathLocation());
						break;

					case FIELD_INFO_EXPECTED_VALUE:
						differences.add(difference.getControlNodeDetail().getValue());
						break;

					case FIELD_INFO_ACTUAL_VALUE:
						differences.add(difference.getTestNodeDetail().getValue());
						break;

					case FIELD_INFO_DESCRIPTIONS:
						differences.add(difference.getDescription());
						break;

					case FIELD_INFO_ADDITIONAL_XPATH:
						differences.add(difference.getTestNodeDetail().getXpathLocation() + "(" + difference.getDescription() + ") : "
								+ difference.getTestNodeDetail().getValue());
						break;

					default:
						break;
					}
				}
			}
		}

		return differences.toArray(new String[differences.size()]);
	}

	/**
	 * Définit la liste des chemins XPath ignorés au moment de la comparaison entre le résultat attendu ("expected") et le résultat "actual" du process
	 * 
	 * @param ignoredXPaths
	 *            , une array de String contenant les chemins définis de façon absolue en XPath
	 */
	public void setIgnoredXPaths(String[] ignoredXPaths) {
		if (ignoredXPaths != null && ignoredXPaths.length>0) diff.setIgnoredXPaths(new ArrayList<String>(Arrays.asList(ignoredXPaths)));
	}

	/**
	 * 
	 * @return La liste des chemins XPath ignorés
	 */
	public ArrayList<String> getIgnoredXPaths() {
		return diff.getIgnoredXPaths();
	}

	/**
	 * Définit la liste des chemins XPath ignorés au moment de la comparaison entre le résultat attendu ("expected") et le résultat "actual" du process
	 * 
	 * @param ignoredXPaths
	 *            , une array de String contenant les chemins définis de façon absolue en XPath
	 */
	public void setIgnoredBranches(String[] ignoredBranches) {
		if (ignoredBranches != null && ignoredBranches.length >0) diff.setIgnoredBranches(new ArrayList<String>(Arrays.asList(ignoredBranches)));
	}

	/**
	 * 
	 * @return La liste des chemins XPath ignorés
	 */
	public ArrayList<String> getIgnoredBranches() {
		return diff.getIgnoredBranches();
	}

	public FCDiff getDiff() {
		return diff;
	}

	public void setDiff(FCDiff diff) {
		this.diff = diff;
	}

}
