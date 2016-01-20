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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
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

	@Override
	protected String getArtifactFileExtension() {
		return POM_EXTENSION;
	}

	@Component
	protected PluginDescriptor pluginDescriptor;

	/**
	 * <p>
	 * All properties in original model with this prefix will be
	 * copied to the deployment POM.
	 * </p>
	 */
	@Parameter (property="deploy.pom.forwardPropertyPrefix", defaultValue="deploymentProperty_")
	protected String deploymentPropertyPrefix;

	/**
	 * <p>
	 * All properties listed in the plugin configuration will be copied to
	 * the deployment POM.
	 * </p>
	 * <p>For instance:<br /><pre>
	 *   &lt;deploymentProperties>
	 *     &lt;deploymentProperty>someProperty&lt;/deploymentProperty>
	 *   &lt;/deploymentProperties></pre>
	 * </p>
	 */
	@Parameter
	protected List<String> deploymentProperties; 

	protected abstract File getOutputFile();
	protected abstract File getTemplateFile();
	protected abstract Boolean getTemplateMerge();
	protected abstract String getClassifier();
	protected abstract Boolean getSkipGeneratePOM();
	protected abstract Boolean getTouchWhenSkipped();
	protected abstract InputStream getBuiltinTemplateFile();
	protected abstract Model updateModel(Model model, MavenProject project) throws MojoExecutionException;
	protected abstract String getGenerationMessage();
	protected abstract String getFailureMessage();

	protected void updatePluginVersion(Model model) {
		for (Plugin plugin : model.getBuild().getPluginManagement().getPlugins()) {
			if (plugin.getKey().equals(pluginDescriptor.getPluginLookupKey())) {
				plugin.setVersion(pluginDescriptor.getVersion());
			}
		}
	}

	protected void addPlugin(Model model, boolean setVersion) {
		for (Plugin plugin : model.getBuild().getPlugins()) {
			if (plugin.getKey().equals(pluginDescriptor.getPluginLookupKey())) {
				return; // already exists
			}
		}

		Plugin plugin = new Plugin();
		plugin.setGroupId(pluginDescriptor.getGroupId());
		plugin.setArtifactId(pluginDescriptor.getArtifactId());
		if (setVersion) {
			plugin.setVersion(pluginDescriptor.getVersion());
		}
		model.getBuild().getPlugins().add(plugin);
	}

	protected void attachFile(File pom) {
		attachFile(pom, POM_TYPE, getClassifier());
	}

	protected void generateDeployPOM(MavenProject project) throws MojoExecutionException {
		File outputFile = getOutputFile();
		File templateFile = getTemplateFile();
		getLog().info(templateFile.getAbsolutePath());
		InputStream builtinTemplateFile = getBuiltinTemplateFile();

		getLog().info(getGenerationMessage() + "'" + outputFile.getAbsolutePath() + "'");
		
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
			throw new MojoExecutionException(getFailureMessage());
		}

		try {
			Model model = POMManager.getModelFromPOM(outputFile, this.getLog());
			if (templateFile != null && templateFile.exists() && getTemplateMerge()) {
				model = POMManager.mergeModelFromPOM(templateFile, model, this.getLog()); // if a template deploy POM exists and we want to merge with built-in one: merge it
			}

			model.setGroupId(project.getGroupId());
			model.setArtifactId(project.getArtifactId());
			model.setVersion(project.getVersion());

			Properties originalProperties = getProject().getProperties();
			for (String property : originalProperties.stringPropertyNames()) {
				if (property != null && property.startsWith(deploymentPropertyPrefix)) {
					model.getProperties().put(property.substring(deploymentPropertyPrefix.length()), originalProperties.getProperty(property));
				}
				if (property != null && deploymentProperties.contains(property)) {
					model.getProperties().put(property, originalProperties.getProperty(property));
				}
			}

			model = updateModel(model, project); 

			POMManager.writeModelToPOM(model, outputFile, getLog());

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
		if (deploymentProperties == null) {
			deploymentProperties = new ArrayList<String>();
		}

		Boolean skipParent = super.skip();
		if (skipParent || getSkipGeneratePOM()) {
			if (!skipParent) {
				getLog().info(SKIPPING);
			}

			File outputFile = getOutputFile();
			if (outputFile != null && !outputFile.exists() && getTouchWhenSkipped()) {
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
				attachFile(outputFile, POM_TYPE, getClassifier());
			} else if (getTouchWhenSkipped()) {
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
