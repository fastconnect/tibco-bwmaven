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
package fr.fastconnect.factory.tibco.bw.maven.packaging.pom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import fr.fastconnect.factory.tibco.bw.maven.packaging.AbstractPackagingMojo;
import fr.fastconnect.factory.tibco.bw.maven.source.POMManager;

/**
 * <p>
 * There are several deployment POM:
 *  <ul>
 *   <li>POM packaging</li>
 *   <li><i>bw-ear-deploy</i> packaging</li>
 *   <li>standalone <i>bw-ear-deploy</i> packaging</li>
 *  </ul>
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
public abstract class AbstractPOMGenerator extends AbstractPackagingMojo {

	protected final static String MODULE_SEPARATOR = "/";

	protected final static String GENERATING_POM_DEPLOY = "Generating deployment POM in ";
	protected final static String FAILURE_POM_DEPLOY = "Unable to create deployment POM";

	@Override
	protected String getArtifactFileExtension() {
		return POM_EXTENSION;
	}

	@Component
	protected PluginDescriptor pluginDescriptor;

	protected abstract File getOutputFile();
	protected abstract File getTemplateFile();
	protected abstract Boolean getTemplateMerge();
	protected abstract Boolean getSkipGeneratePOM();
	protected abstract InputStream getBuiltinTemplateFile();
	protected abstract Model updateModel(Model model, MavenProject project) throws MojoExecutionException;
	protected abstract void attachFile(File pom);
	protected abstract void postGeneration(File outputFile, Model model, MavenProject project) throws MojoExecutionException;

	protected void updatePluginVersion(Model model) {
		for (Plugin plugin : model.getBuild().getPluginManagement().getPlugins()) {
			if (plugin.getKey().equals(pluginDescriptor.getPluginLookupKey())) {
				plugin.setVersion(pluginDescriptor.getVersion());
			}
		}		
	}

	protected void generateDeployPOM(MavenProject project) throws MojoExecutionException {
		File outputFile = getOutputFile();
		File templateFile = getTemplateFile();
		InputStream builtinTemplateFile = getBuiltinTemplateFile();

		getLog().info(GENERATING_POM_DEPLOY + "'" + outputFile.getAbsolutePath() + "'");
		
		try {
   			outputFile.getParentFile().mkdirs();
			outputFile.createNewFile();
			if (templateFile != null && templateFile.exists() && !getTemplateMerge()) {
				FileUtils.copyFile(templateFile, outputFile); // if a template deploy POM exists and we don't want to merge with built-in one: use it
			} else {
				// otherwise : use the one included in the plugin
				FileOutputStream fos = new FileOutputStream(outputFile);
				IOUtils.copy(builtinTemplateFile, fos);
			}
		} catch (IOException e) {
			throw new MojoExecutionException(FAILURE_POM_DEPLOY);
		}

		try {
			Model model = POMManager.getModelFromPOM(outputFile, this.getLog());
			if (templateFile != null && templateFile.exists() && getTemplateMerge()) {
				model = POMManager.mergeModelFromPOM(templateFile, model, this.getLog()); // if a template deploy POM exists and we want to merge with built-in one: merge it
			}

			model.setGroupId(project.getGroupId());
			model.setArtifactId(project.getArtifactId());
			model.setVersion(project.getVersion());

			model = updateModel(model, project); 

			POMManager.writeModelToPOM(model, outputFile, getLog());

			postGeneration(outputFile, model, project);

			attachFile(outputFile); 
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (XmlPullParserException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	@Override
	protected MavenProject getProject() {
		return super.getProject();
	}

	public void execute() throws MojoExecutionException {
		Boolean skipParent = super.skip();
		if (skipParent || getSkipGeneratePOM()) {
			if (!skipParent) {
				getLog().info(SKIPPING);
			}

			// TODO : rendre générique
			File outputFile = getOutputFile();
           	if (outputFile != null && !outputFile.exists() && touchPOMDeployIfSkipped) {
           		// deployment POM was not created because packaging is skipped
           		// however we "touch" the deployment POM file so there is an empty deployment POM file created
           		try {
           			outputFile.getParentFile().mkdirs();
					outputFile.createNewFile();
				} catch (IOException e) {
					throw new MojoExecutionException(e.getLocalizedMessage(), e);
				}
           	}
			if (outputFile != null && outputFile.exists()) {
				attachFile(outputFile, POM_TYPE, "deploy");
			} else {
            	getLog().warn(WARN_NO_ARTIFACT_ATTACHED);
			}
			return;
		}

		if (!packageDirectory.exists()) {
			packageDirectory.mkdirs();
		}

		generateDeployPOM(getProject());
	}

}
