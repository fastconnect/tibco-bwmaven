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
package fr.fastconnect.tibco.businessworks.fcunit;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import fr.fastconnect.tibco.businessworks.fcunit.processes.AbstractProcess;
import fr.fastconnect.tibco.businessworks.fcunit.processes.AbstractProcessList;

/**
 * 
 * @author Mathieu Debove
 *
 */
public class TestSuite extends BWResource implements Serializable {

	private static final long serialVersionUID = 2914984144832167792L;
	private Set<TestSuite> suites = new LinkedHashSet<TestSuite>();
	private Set<TestCase> cases = new LinkedHashSet<TestCase>();
    
    public TestSuite(String path) {
		super(path);
    }

	public TestSuite getChild(String name, String path) {
        for (TestSuite child : suites) {
            if (child.getName().equals(name)) {
                return child;
            }
        }

        return getChild(new TestSuite(path));
    }

	private TestSuite getChild(TestSuite child) {
		this.suites.add(child);
        return child;
    }
    
	public TestCase getCase(String name, String path) {
        for (TestCase child : cases) {
            if (child.getName().equals(name)) {
                return child;
            }
        }
        
        TestCase testCase = new TestCase(path);
        this.cases.add(testCase);

		return testCase;		
	}
	
	public TestSuite[] getSuites() {
    	return suites.toArray(new TestSuite[0]);
    }
	
	public TestCase[] getCases() {
    	return cases.toArray(new TestCase[0]);
    }
	
	public static TestSuite createTreeFromList(AbstractProcessList<AbstractProcess> processList) {
		TestSuite tree = new TestSuite("");
	    
	    TestSuite current = tree;
	    TestCase currentCase = null;
		
	    for (AbstractProcess process : processList.getProcessesList()) {
	        TestSuite root = current;

	        String path = "";
	        for (String data : process.getPath().split("/")) {
	        	path += "/" + data;
	        	if (Helpers.matchCase(path.substring(1))) {
	        		currentCase = current.getCase(data, path.substring(1));
	        		continue;
	        	}
	        	if (path.toLowerCase().endsWith(".process")) {
	        		currentCase.addProcess(path.substring(1));
	        		continue;
	        	}
        		current = current.getChild(data, path.substring(1));
	        }

	        current = root;
	    }
	    
		return tree;
	}
    
}