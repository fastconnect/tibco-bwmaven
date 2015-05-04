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

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * <p>
 * This goal "unmavenizes" a list of mavenized TIBCO BusinessWorks projects by 
 * simply removing the POMs of the projects.
 * </p>
 * 
 * <p>
 * Refer to <a href="./list-maven-projects-mojo.html">bw:list-maven-projects</a>
 * goal for an explanation about projects lists.
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
@Mojo ( name="unmavenize-bw-projects", requiresProject = false )
public class MassUnMavenizerMojo extends MavenProjectsListMojo {

	@Override
	protected String getActionFailure() { return " project failed the unmavenization (POM was not removed)."; }
	@Override
	protected String getActionFailures() { return " projects failed the unmavenization (POM was not removed)."; }
	@Override
	protected String getActionSuccess() { return " project was successfully unmavenized (POM was removed)."; }
	@Override
	protected String getActionSuccesses() { return " projects were successfully unmavenized (POM was removed)."; }

	@Override
	protected boolean performAction(AbstractProject p) {
		getLog().info("Removing : " + p.getMandatoryFilePath());
		return FileUtils.deleteQuietly(new File(p.getMandatoryFilePath()));
	}
	
}
