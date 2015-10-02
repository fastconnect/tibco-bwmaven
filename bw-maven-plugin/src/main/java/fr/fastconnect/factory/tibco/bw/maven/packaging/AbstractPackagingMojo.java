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
package fr.fastconnect.factory.tibco.bw.maven.packaging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

import fr.fastconnect.factory.tibco.bw.maven.AbstractBWArtifactMojo;

/**
 * <p>
 * This class defines locations of files produced by packaging phase which will
 * be used by the deploy phase.
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
public abstract class AbstractPackagingMojo extends AbstractBWArtifactMojo {

	protected final static String deployedProjectNameProperty = "deploy.project.name";
	protected final static String domainNameProperty = "tibco.domain.name";
	protected final static String domainUsernameProperty = "tibco.domain.username";
	protected final static String domainPasswordProperty = "tibco.domain.password";

	protected static final String deploymentDescriptorProperty = "deploy.descriptor";
	protected static final String deploymentDescriptorFinalProperty = "deploy.descriptor.final";
	protected static final String deploymentGlobalVariablesProperty = "deploy.properties.globalVariables";
	protected static final String deploymentServicesProperty = "deploy.properties.services";
	protected static final String deploymentGlobalVariablesReferenceProperty = "deploy.properties.globalVariables.reference";
	protected static final String deploymentServicesReferenceProperty = "deploy.properties.services.reference";
	protected static final String deploymentGlobalVariablesCommonProperty = "deploy.properties.globalVariables.common";
	protected static final String deploymentServicesCommonProperty = "deploy.properties.services.common";
	
	@Parameter ( property=deploymentDescriptorProperty, defaultValue = "${project.package.directory}/${project.build.finalName}.xml")
	protected File deploymentDescriptor;

	@Parameter ( property=deploymentDescriptorFinalProperty, defaultValue = "${project.package.directory}/${project.build.finalName}-final.xml")
	protected File deploymentDescriptorFinal;

	@Parameter ( property=deploymentGlobalVariablesProperty, defaultValue = "${project.package.directory}/${project.build.finalName}.gv.properties")
	protected File deploymentGlobalVariables;

	@Parameter ( property=deploymentServicesProperty, defaultValue = "${project.package.directory}/${project.build.finalName}.services.properties")
	protected File deploymentServices;

	@Parameter ( property=deploymentGlobalVariablesReferenceProperty, defaultValue = "${project.package.directory}/${project.artifactId}.gv.properties")
	protected File deploymentGlobalVariablesReference;
	
	@Parameter ( property=deploymentServicesReferenceProperty, defaultValue = "${project.package.directory}/${project.artifactId}.services.properties")
	protected File deploymentServicesReference;

	@Parameter ( property=deploymentGlobalVariablesCommonProperty, defaultValue = "${project.package.directory}/${project.artifactId}.gv.properties")
	protected File deploymentGlobalVariablesCommon;
	
	@Parameter ( property=deploymentServicesCommonProperty, defaultValue = "${project.package.directory}/${project.artifactId}.services.properties")
	protected File deploymentServicesCommon;
	
	@Parameter ( property="ignoreReferenceFiles", defaultValue = "false" )
	protected boolean ignoreReferenceFiles;

	@Parameter ( property="ignoreCommonFiles", defaultValue = "false" )
	protected boolean ignoreCommonFiles;

	@Parameter ( property="deploy.properties.filterProperties", defaultValue = "false" , alias="filterProperties" )
	protected boolean filterProperties; 

	// repoInstance
	@Parameter (property="repoSelectInstance", defaultValue="local")
	protected String repoSelectInstance;

	@Parameter (property="repoHttpTimeout", defaultValue="600")
	protected Integer repoHttpTimeout;
	@Parameter (property="repoHttpUrl", defaultValue="")
	protected String repoHttpUrl;
	@Parameter (property="repoHttpServer", defaultValue="")
	protected String repoHttpServer;
	@Parameter (property="repoHttpUser", defaultValue="")
	protected String repoHttpUser;
	@Parameter (property="repoHttpPassword", defaultValue="")
	protected String repoHttpPassword;
	@Parameter (property="repoHttpExtraPropertyFile", defaultValue="")
	protected String repoHttpExtraPropertyFile;
	
	@Parameter (property="repoRvTimeout", defaultValue="600")
	protected Integer repoRvTimeout;
	@Parameter (property="repoRvDiscoveryTimeout", defaultValue="10")
	protected Integer repoRvDiscoveryTimeout;
	@Parameter (property="repoRvDaemon", defaultValue="tcp:7500")
	protected String repoRvDaemon;
	@Parameter (property="repoRvService", defaultValue="7500")
	protected String repoRvService;
	@Parameter (property="repoRvNetwork", defaultValue="")
	protected String repoRvNetwork;
	@Parameter (property="repoRvRegionalSubject", defaultValue="")
	protected String repoRvRegionalSubject;
	@Parameter (property="repoRvOperationRetry", defaultValue="0")
	protected Integer repoRvOperationRetry;
	@Parameter (property="repoRvServer", defaultValue="")
	protected String repoRvServer;
	@Parameter (property="repoRvUser", defaultValue="")
	protected String repoRvUser;
	@Parameter (property="repoRvPassword", defaultValue="")
	protected String repoRvPassword;
	@Parameter (property="repoRvExtraPropertyFile", defaultValue="")
	protected String repoRvExtraPropertyFile;

	@Parameter (property="repoLocalEncoding", defaultValue="UTF-8")
	protected String repoLocalEncoding;
	//

	// monitor (applied to all <bw> services)
	@Parameter (property = "events", alias="events")
	protected Events events;

	@Parameter (property = "deploy.maxDeploymentRevision", defaultValue="-1")
	protected String maxDeploymentRevision;

	@Parameter (property = "deploy.contact", defaultValue="")
	protected String contact;

	@Parameter (property = "deploy.description", defaultValue="")
	protected String description;

    /**
     * <p>
     * This loads a properties file into a java.util.Properties object with
     * sorted keys.<br/><br/>
     * 
     *  <b>NB</b>: always use keys() method to browse the properties
     * </p>
     * 
     * @param propertiesFile
     * @return
     * @throws ConfigurationException
     * @throws IOException
     */
	protected Properties loadPropertiesFile(File propertiesFile) throws ConfigurationException, IOException {
		Properties properties = new Properties() { // sorted properties
			private static final long serialVersionUID = 7793482336210629858L;
			
			@Override
			public synchronized Enumeration<Object> keys() {
				return Collections.enumeration(new TreeSet<Object>(super.keySet()));
			}
		};
		
		FileInputStream fileInputStream = new FileInputStream(propertiesFile);
		properties.load(fileInputStream);

		return properties;
	}

	@Component( role=org.apache.maven.shared.filtering.MavenResourcesFiltering.class, hint="default")
	protected MavenResourcesFiltering mavenResourcesFiltering;

	/**
	 * <p>
	 * This saves a java.util.Properties to a file.<br />
	 * 
	 * It is possible to add a comment at the beginning of the file.
	 * </p>
	 * 
	 * @param outputFile, the File where to output the Properties
	 * @param properties, the Properties to save
	 * @param propertiesComment, the comment to add at the beginning of the file
	 * @param success, the success message
	 * @param failure, the failure message
	 * @throws MojoExecutionException
	 */
	protected void savePropertiesToFile(File outputFile, Properties properties, String propertiesComment, String success, String failure) throws MojoExecutionException {
		OutputStream outputStream = null;
		
		try {
			outputFile.getParentFile().mkdirs();
			outputStream = new FileOutputStream(outputFile);
			properties.store(outputStream, propertiesComment);
			
			if (filterProperties) {
				getLog().debug("Filtering properties files");

				File tmpDir = new File(outputFile.getParentFile(), "tmp");
				tmpDir.mkdir();
				List<Resource> resources = new ArrayList<Resource>();
				Resource r = new Resource();
				r.setDirectory(outputFile.getParentFile().getAbsolutePath());
				r.addInclude("*.properties");
				r.setFiltering(true);
				resources.add(r);
				
				ArrayList<Object> filters = new ArrayList<Object>();
				List<String> nonFilteredFileExtensions = new ArrayList<String>();
				
				MavenResourcesExecution mre = new MavenResourcesExecution(resources, tmpDir, this.getProject(), this.sourceEncoding, filters, nonFilteredFileExtensions, session);
				mavenResourcesFiltering.filterResources(mre);
				
				FileUtils.copyDirectory(tmpDir, outputFile.getParentFile());
				FileUtils.deleteDirectory(tmpDir);
			}

			getLog().info(success + " '" + outputFile + "'");
		} catch (Exception e) {
			throw new MojoExecutionException(failure + " '" + outputFile + "'", e);
		} finally {
			try {
				outputStream.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Convert an expression with wildcards to a regex.
	 * 
	 * source = http://www.rgagnon.com/javadetails/java-0515.html
	 * 
	 * @param wildcard
	 * @return
	 */
    protected static String wildcardToRegex(String wildcard) {
        StringBuffer s = new StringBuffer(wildcard.length());
        s.append('^');
        for (int i = 0, is = wildcard.length(); i < is; i++) {
            char c = wildcard.charAt(i);
            switch(c) {
                case '*':
                    s.append(".*");
                    break;
                case '?':
                    s.append(".");
                    break;
                    // escape special regexp-characters
                case '(': case ')': case '[': case ']': case '$':
                case '^': case '.': case '{': case '}': case '|':
                case '\\':
                    s.append("\\");
                    s.append(c);
                    break;
                default:
                    s.append(c);
                    break;
            }
        }
        s.append('$');
        return(s.toString());
    }

	public boolean skip() {
		if (skipPackage) {
			getLog().info(SKIPPING);
		}
		return skipPackage;
	}

}
