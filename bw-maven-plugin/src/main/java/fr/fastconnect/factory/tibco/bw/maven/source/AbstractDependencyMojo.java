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

import org.apache.maven.model.Dependency;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * <p>
 * This class only defines the parameters to define a Maven dependency.
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
public class AbstractDependencyMojo extends MavenProjectsListMojo {

	@Parameter ( property = "dependencyGroupId", required = true )
	protected String dependencyGroupId;
	@Parameter ( property = "dependencyArtifactId", required = true )
	protected String dependencyArtifactId;
	@Parameter ( property = "dependencyVersion", required = false )
	protected String dependencyVersion;
	@Parameter ( property = "dependencyType", defaultValue = "jar", required = false )
	protected String dependencyType;
	@Parameter ( property = "dependencyClassifier", defaultValue = "", required = false )
	protected String dependencyClassifier;

	@Parameter ( property = "dependencyManagement", defaultValue = "false", required = false )
	protected boolean dependencyManagement;

	protected Dependency createDependency() {
		Dependency d = new Dependency();
		d.setGroupId(dependencyGroupId);
		d.setArtifactId(dependencyArtifactId);
		d.setVersion(dependencyVersion);
		d.setType(dependencyType);
		d.setClassifier(dependencyClassifier);
		
		return d;
	}

}
