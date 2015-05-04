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
import java.util.HashSet;

import fr.fastconnect.tibco.businessworks.fcunit.processes.TestProcess;

/**
 * Une classe qui modélise un TestCase, donc une liste de {@link TestProcess} contenus dans le TestCase.
 * 
 * @author Mathieu Debove
 *
 */
public class TestCase extends BWResource implements Serializable {

	private static final long serialVersionUID = -5129492488357372228L;
	private HashSet<TestProcess> tests = new HashSet<TestProcess>();

	/**
	 * Création via {@link BWResource}
	 * 
	 * @param path, le chemin du TestCase
	 */
	public TestCase(String path) {
		super(path);
	}

	/**
	 * Cette méthode crée le test corresondant au process passé en paramètre
	 * et l'ajoute à la liste des tests du {@link TestCase}.
	 * 
	 * @param process
	 */
	public void addProcess(String process) {
		this.tests.add(new TestProcess(process));
	}

	/**
	 * 
	 * @return la liste des {@link TestProcess} sous forme d'array
	 */
	public TestProcess[] getTests() {
		return ((TestProcess[]) this.tests.toArray(new TestProcess[0]));
	}
}
