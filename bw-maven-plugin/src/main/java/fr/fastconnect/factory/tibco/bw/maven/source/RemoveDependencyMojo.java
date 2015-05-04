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
package fr.fastconnect.factory.tibco.bw.maven.source;

import java.io.File;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * <p>
 * This goal removes a dependency from the POMs of the defined projects list.
 * </p>
 * <p>
 * Refer to <a href="./list-maven-projects-mojo.html">bw:list-maven-projects</a>
 * goal for an explanation about projects lists.
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
@Mojo ( name = "remove-dependency", requiresProject = false )
public class RemoveDependencyMojo extends AbstractDependencyMojo {
	
	@Override
	protected String getActionFailure() { return " project failed to remove the dependency."; }
	@Override
	protected String getActionFailures() { return " projects failed to remove the dependency."; }
	@Override
	protected String getActionSuccess() { return " project successfully remove the dependency."; }
	@Override
	protected String getActionSuccesses() { return " projects successfully remove the dependency."; }
	
	@Override
	protected void displayProject(AbstractProject p) {
		super.displayProject(p);
		
		Dependency d = createDependency();

		getLog().info("");
		getLog().info("Removing dependency '" + d.getManagementKey() + "' from '" + p.getProjectName() + "'");
	};
	
	@Override
	protected boolean performAction(AbstractProject p) {
		File pom = new File(p.getMandatoryFilePath());
		
		Dependency d = createDependency();
		
		try {
			if (!dependencyManagement) {
				if (POMManager.dependencyExists(pom, d, getLog())) {
					POMManager.removeDependency(pom, d, getLog());
					return true;
				} else {
					getLog().info("Skipping : '" + d.getManagementKey() + "' is not a dependency in '" + pom + "'");
				}
			} else {
				if (POMManager.dependencyExists(pom, d, getLog())) {
					POMManager.removeDependencyManagement(pom, d, getLog());
					return true;
				} else {
					getLog().info("Skipping : '" + d.getManagementKey() + "' is not a managed dependency in '" + pom + "'");
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return false;
	}

}
