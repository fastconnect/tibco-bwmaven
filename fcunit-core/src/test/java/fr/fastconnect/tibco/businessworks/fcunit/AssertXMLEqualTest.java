package fr.fastconnect.tibco.businessworks.fcunit;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.fastconnect.tibco.businessworks.fcunit.xml.AssertXMLEqual;
import fr.fastconnect.tibco.businessworks.fcunit.xml.FCDiff;

public class AssertXMLEqualTest {
	
	private String getExpected() throws IOException {
		return FileUtils.readFileToString(new File("src/test/resources/expected.xml"));
		
	}
	private String getActual() throws IOException {
		return FileUtils.readFileToString(new File("src/test/resources/actual.xml"));
	}
	
	@Test
	public void getDifferentFields_test() throws SAXException, IOException{
		AssertXMLEqual assertXML = new AssertXMLEqual(getExpected(),getActual());

		String[] ignoreBranches = new String[1];
		ignoreBranches[0] = "/timestamp";
		assertXML.setIgnoredBranches(ignoreBranches);
		
		//String [] fields = assertXML.getDifferentFields(4, true); // FIELD_INFO_IGNORED_XPATH
		//FIXME : do the test!
	}
	
	@Test
	public void removeBranches_test() throws SAXException, IOException{
		AssertXMLEqual assertXML = new AssertXMLEqual(getExpected(),getActual());
		FCDiff diff = assertXML.getDiff();
		
		String[] ignoreBranches = new String[1];
		ignoreBranches[0] = "/timestamp";
		assertXML.setIgnoredBranches(ignoreBranches);
		
		Document expectedDoc = diff.getDocumentFromString(getExpected());
		diff.removeIgnoredBranches(expectedDoc);
		
	}
	
	
	@Test
	public void test_AssertAtLeastEqualXML () throws SAXException, IOException{
		
		String expectedXML= getExpected();
		String actualXML = getActual();
		String[] ignoredXPaths= new String []{"/dimensions/pyramide[1]/timestamp[1]/text()[1]"};
		String[] ignoredBranches = new String []{"/dimensions/square"};
		
		AssertXMLEqual assertAssertXMLEqual = new AssertXMLEqual(expectedXML, actualXML);
		
		assertAssertXMLEqual.setIgnoredXPaths(ignoredXPaths);
		
		assertAssertXMLEqual.setIgnoredBranches(ignoredBranches);
		
		boolean result = assertAssertXMLEqual.assertXMLAtLeastEqual();
		
		if (!result){
			String [] additiionalFields = assertAssertXMLEqual.getAdditionalFields();
			printArray(additiionalFields);
			String [] differentFieldsIgnorePaths = assertAssertXMLEqual.getDifferentFieldsIgnoredPaths(true);
			printArray(differentFieldsIgnorePaths);
			String [] differentFieldsIgnoredDescription = assertAssertXMLEqual.getDifferentFieldsDescriptions(true);
			printArray(differentFieldsIgnoredDescription);
			String [] differentFieldsPaths = assertAssertXMLEqual.getDifferentFieldsPaths(true);
			printArray(differentFieldsPaths);
			String [] differentFieldsExpectedValues = assertAssertXMLEqual.getDifferentFieldsActualValues(true);
			printArray(differentFieldsExpectedValues);
			String [] differentFieldsActualValues = assertAssertXMLEqual.getDifferentFieldsActualValues(true);
			printArray(differentFieldsActualValues);
			String [] differentFieldsDescription = assertAssertXMLEqual.getDifferentFieldsDescriptions(true);
			printArray(differentFieldsDescription);
			
		}
		
		Assert.assertTrue(result);
		
		
	}
	
	private void printArray(String [] array){
		for (String s : array){
			System.out.println(s);
		}
	}

}
