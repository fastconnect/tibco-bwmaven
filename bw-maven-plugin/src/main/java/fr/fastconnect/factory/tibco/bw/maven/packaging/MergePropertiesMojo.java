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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import fr.fastconnect.factory.tibco.bw.maven.packaging.ApplicationManagement.SortedProperties;

/**
 * <p>
 * This goal will merge the reference and common properties into the working
 * properties files (usually the ones in "target/package").
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
@Mojo( name="merge-properties",
defaultPhase=LifecyclePhase.PREPARE_PACKAGE )
public class MergePropertiesMojo extends AbstractPackagingMojo {

	/**
	 * By default, the empty binding ("bw[EXAMPLE.par]/bindings/binding[]) is
	 * removed if other named bindings exists
	 * ("bw[EXAMPLE.par]/bindings/binding[named]").
	 * It is possible to keep this empty binding by setting
     * <i>alwaysKeepEmptyBindings</i> to <i>true</i>. Default is <i>false</i>.
	 */
	@Parameter (property = "alwaysKeepEmptyBindings", defaultValue = "false")
	boolean alwaysKeepEmptyBindings;
	
	/**
	 * It is possible to ignore any merging of properties by setting
	 * <i>ignorePropertiesMerge</i> to <i>true</i>. Default is <i>false</i>
	 * (do merge).
	 */
	@Parameter (property = "ignorePropertiesMerge", defaultValue = "false")
	boolean ignorePropertiesMerge;

	/**
	 * By default the properties from common properties are merged first and the
	 * properties from reference specific to the project last. By setting 
	 * <i>mergeCommonLast</i> to true, the common properties are mergerd last.
	 */
	@Parameter (property = "mergeCommonLast", defaultValue = "false")
	boolean mergeCommonLast; 
	
	private enum MergedFiles {
		NONE, GV, SERVICES, BOTH
	}
	
	protected final static String MERGING_PROPERTIES = "Merging properties...";
	protected final static String NOTHING_TO_MERGE = "Nothing to merge.";
	protected final static String MERGE_FAILURE = "Failed to merge properties";
	protected final static String PROPERTIES_LOAD_FAILURE = "Failed to load properties";

	protected final static String PROPERTIES_SAVE_GVS_SUCCESS = "Successfully saved merged properties to file";
	protected final static String PROPERTIES_SAVE_GVS_FAILURE = "Failed to save merged properties to XML file";
	protected final static String PROPERTIES_SAVE_SERVICES_SUCCESS = "Successfully saved merged properties to file";
	protected final static String PROPERTIES_SAVE_SERVICES_FAILURE = "Failed to save merged properties to XML file";

	protected final static String USING_GVS_COMMON_FILE = "Using Global Variables common properties file";
	protected final static String USING_GVS_REFERENCE_FILE = "Using Global Variables reference properties file";
	protected final static String USING_SERVICES_COMMON_FILE = "Using Services common properties file";
	protected final static String USING_SERVICES_REFERENCE_FILE = "Using Services reference properties file";

	private List<ImmutablePair<String, String>> pairParInstance = new ArrayList<ImmutablePair<String, String>>(); // keep trace of dynamically created bindings (to generate them only once)

	private final static String regexNotEmptyBinding = "^bw\\[(.*)\\]/bindings/binding\\[(.+)\\]/(.*)$";
	private final static String regexEmptyBinding = "^bw\\[(.*)\\]/bindings/binding(\\[\\])/(.*)$";

	@Override
	protected String getArtifactFileExtension() {
		return PROPERTIES_EXTENSION;
	}

	public MergedFiles mergeFiles(Properties propertiesGlobalVariables,
								  Properties propertiesServices, 
								  File deploymentGlobalVariables,
								  File deploymentServices,
								  String messageGlobalVariables,
								  String messageServices) throws MojoExecutionException, IOException {

		MergedFiles result = MergedFiles.NONE;
		
		// Merge with files if they exist
		if (deploymentGlobalVariables != null &&
			deploymentGlobalVariables.exists() &&
			!deploymentGlobalVariables.getCanonicalPath().equals(this.deploymentGlobalVariables.getCanonicalPath())) { 
			// FIXME: check the checksum of files instead of canonical path ?
			getLog().info(messageGlobalVariables + " : " + deploymentGlobalVariables.getAbsolutePath());
			
			Properties propertiesGlobalVariablesReference;
			try {
				propertiesGlobalVariablesReference = loadPropertiesFile(deploymentGlobalVariables); 
			} catch (Exception e) {
				throw new MojoExecutionException(PROPERTIES_LOAD_FAILURE, e);
			}
			
			Enumeration<Object> e = propertiesGlobalVariablesReference.keys();
	   		while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = propertiesGlobalVariablesReference.getProperty(key);
				
				propertiesGlobalVariables.setProperty(key, value); // add or override value
			}
			
			result = MergedFiles.GV;
		}
		
		Pattern pNotEmptyBinding = Pattern.compile(regexNotEmptyBinding);
		
		if (deploymentServices != null &&
			deploymentServices.exists() &&
			!deploymentServices.getCanonicalPath().equals(this.deploymentServices.getCanonicalPath())) {
			// FIXME: check the checksum of files instead of canonical path ?
			
			getLog().info(messageServices + " : " + deploymentServices.getAbsolutePath());
			Properties propertiesServicesReference;
			try {
				propertiesServicesReference = loadPropertiesFile(deploymentServices);
			} catch (Exception e) {
				throw new MojoExecutionException(PROPERTIES_LOAD_FAILURE, e);
			}
			
			String par, binding;
			Enumeration<Object> e = propertiesServicesReference.keys();
	   		while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				
				Matcher mNotEmptyBinding = pNotEmptyBinding.matcher(key);
				
				if (mNotEmptyBinding.matches() && !isAWildCard(key)) {
					par = mNotEmptyBinding.group(1);
					binding = mNotEmptyBinding.group(2);
					ImmutablePair<String, String> pair = new ImmutablePair<String, String>(par, binding);
					if (!pairParInstance.contains(pair)) {
						propertiesServices = duplicateEmptyBinding(propertiesServices, par, binding);
						pairParInstance.add(pair);
					}
				}
				
				String value = propertiesServicesReference.getProperty(key);
				
				propertiesServices.setProperty(key, value); // add or override value
			}
			
			propertiesServices = expandWildCards(propertiesServices);
			
			if (result == MergedFiles.GV) {
				result = MergedFiles.BOTH;
			} else {
				result = MergedFiles.SERVICES;
			}
		}
		
		return result;
	}

	private Properties duplicateEmptyBinding(Properties properties, String par, String binding) {
		String key, value;
		Pattern pEmptyBinding = Pattern.compile(regexEmptyBinding);

		Enumeration<Object> e = properties.keys();
		while (e.hasMoreElements()) {
			key = (String) e.nextElement();
			value = properties.getProperty(key);
			Matcher mEmptyBinding = pEmptyBinding.matcher(key);
			if (mEmptyBinding.matches() && mEmptyBinding.group(1).equals(par)) {
				key = "bw[" + mEmptyBinding.group(1) + "]/bindings/binding[" + binding + "]/" + mEmptyBinding.group(3);
			}
			properties.setProperty(key, value);
		}
		return properties;
	}

	private Properties sortProperties(Properties properties) throws ConfigurationException, IOException {
		Properties sp = new SortedProperties();
		sp.putAll(properties);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		sp.store(baos, null);
		
		properties.clear();
		properties.load(new ByteArrayInputStream(baos.toByteArray()));

		return properties;
	}

    protected boolean isAWildCard(String key) {
		String regexVariable = "bw\\[[^\\**]*\\]/variables\\[.*\\]/variable\\[.*\\]"; // is a wildcard only if it has a '*' in bw[] for variables (because a variable can have '*' in their name)

		return key.contains("*") && !Pattern.matches(regexVariable, key);
    }

    /**
     * <p>
     * This expands wild cards properties.<br /><br />
     * Both wildcard expressions and expressions to expand are present in the
     * same properties object.<br /><br />
     * <i>Example</i>
     *  <ul>
     * 	 <li><b>property with wildcard</b>: /root/element[*]/key=new_value</li>
     * 	 <li><b>property matching</b>: /root/element[my_name]/key=old_value</li>
     *  </ul>
     *  will expand to:<br />
     *  <ul>
     * 	 <li><b>property after expansion</b>:
     * /root/element[my_name]/key=new_value</li>
     *  </ul>
     * </p>
     * 
     * @param properties, the properties object with wildcard expressions and
     * expressions to expand
     * @return properties with expanded expressions, but without wildcard
     * expressions
     */
    protected Properties expandWildCards(Properties properties) {
    	Properties propertiesWithWildCards = new Properties() { // sorted properties
			private static final long serialVersionUID = 7793482336210629858L;

			@Override
		    public synchronized Enumeration<Object> keys() {
		        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
		    }
		};
    	String key;
    	
    	// retrieve the keys with WildCards
		Enumeration<Object> e = properties.keys();
   		while (e.hasMoreElements()) {
    		key = (String) e.nextElement();
    		if (isAWildCard(key)) {
    			propertiesWithWildCards.setProperty(key, properties.getProperty(key));
    			properties.remove(key);
    		}
    	}
    	
    	// try to replace the values of other keys matching the keys with WildCards
		Enumeration<Object> w = propertiesWithWildCards.keys();
   		while (w.hasMoreElements()) {
    		String keyWithWildCards = (String) w.nextElement();
    		String regex = wildcardToRegex(keyWithWildCards);

			String ignoreWildcardInVariablesPattern = "(.*)variables\\\\\\[(.*)\\\\\\]\\/variable\\\\\\[(.*)\\\\\\](.*)";
			Pattern p = Pattern.compile(ignoreWildcardInVariablesPattern);
			Matcher m = p.matcher(regex);
			if (m.matches()) {
				String variables = m.group(2);
				String variable = m.group(3);
				variables = variables.replace(".*", "\\*");
				variable = variable.replace(".*", "\\*");
				regex = m.group(1) + "variables\\[" + variables + "\\]/variable\\[" + variable + "\\]" + m.group(4);
			}

    		Boolean found = false;

    		e = properties.keys();
    	   	while (e.hasMoreElements()) {
    			key = (String) e.nextElement();
    			
    			if (Pattern.matches(regex, key)) {
    				found = true;
    				String value = (String) propertiesWithWildCards.getProperty(keyWithWildCards);
    				properties.setProperty(key, value);
    			}
    		}

    	   	// not found, we put back the expression with wild cards in the original list (false positive)
    	   	// this way the wildcard can still be used in a next pass and will be removed at the end by AbstractPackagingMojo.removeWildCards 
    		if (!found) {
    			properties.setProperty(keyWithWildCards, propertiesWithWildCards.getProperty(keyWithWildCards));
    		}
    	}
    	
    	return properties;
    }

    protected Properties removeWildCards(Properties properties) {
    	String key;
    	
		Enumeration<Object> e = properties.keys();
    	while (e.hasMoreElements()) {
    		key = (String) e.nextElement();
    		
    		if (isAWildCard(key)) {
    			properties.remove(key);
    		}
    	}
    	
    	return properties;
    }

	public void execute() throws MojoExecutionException {
		if (super.skip()) {
			return;
		}

		if (ignorePropertiesMerge) return; // ignore all properties merging

		getLog().info(MERGING_PROPERTIES);
		try {
			Properties earGlobalVariables = loadPropertiesFile(deploymentGlobalVariables);
			Properties earServices = loadPropertiesFile(deploymentServices);

			MergedFiles mergedCommonFiles = MergedFiles.NONE, mergedReferenceFiles = MergedFiles.NONE;

			if (mergeCommonLast) {
				if (!ignoreReferenceFiles) {
					mergedReferenceFiles = mergeFiles(earGlobalVariables,
													  earServices,
													  deploymentGlobalVariablesReference,
													  deploymentServicesReference,
													  USING_GVS_REFERENCE_FILE,
													  USING_SERVICES_REFERENCE_FILE);
				}
				if (!ignoreCommonFiles) {
					mergedCommonFiles = mergeFiles(earGlobalVariables,
							   					   earServices,
							   					   deploymentGlobalVariablesCommon,
							   					   deploymentServicesCommon,
							   					   USING_GVS_COMMON_FILE,
							   					   USING_SERVICES_COMMON_FILE);
				}
			} else {
				if (!ignoreCommonFiles) {
					mergedCommonFiles = mergeFiles(earGlobalVariables,
												   earServices,
												   deploymentGlobalVariablesCommon,
												   deploymentServicesCommon,
												   USING_GVS_COMMON_FILE,
												   USING_SERVICES_COMMON_FILE);
				}
				if (!ignoreReferenceFiles) {
					mergedReferenceFiles = mergeFiles(earGlobalVariables,
							  						  earServices,
							  						  deploymentGlobalVariablesReference,
							  						  deploymentServicesReference,
							  						  USING_GVS_REFERENCE_FILE,
							  						  USING_SERVICES_REFERENCE_FILE);
				}
			}

		    earGlobalVariables = sortProperties(earGlobalVariables);
		    earServices = sortProperties(earServices);

		    earServices = removeWildCards(earServices);
		    if (!alwaysKeepEmptyBindings) {
		    	earServices = removeEmptyBindings(earServices);
		    }


			if (mergedCommonFiles == MergedFiles.GV ||
			    mergedCommonFiles == MergedFiles.BOTH ||
			    mergedReferenceFiles == MergedFiles.GV ||
			    mergedReferenceFiles == MergedFiles.BOTH ||
			    mergedCommonFiles == MergedFiles.SERVICES ||
			    mergedCommonFiles == MergedFiles.BOTH ||
			    mergedReferenceFiles == MergedFiles.SERVICES ||
			    mergedReferenceFiles == MergedFiles.BOTH) {

				getLog().info("");
			} else {
				getLog().debug("GV ref: " + deploymentGlobalVariablesReference.getAbsolutePath());
				getLog().debug("SVC ref: " + deploymentServicesReference.getAbsolutePath());
				getLog().debug("GV common: " + deploymentGlobalVariablesCommon.getAbsolutePath());
				getLog().debug("SVC common: " + deploymentServicesCommon.getAbsolutePath());
				getLog().info(NOTHING_TO_MERGE);
			}

			// Export Properties in a properties file
			//  Global Variables
			if (mergedCommonFiles == MergedFiles.GV ||
			    mergedCommonFiles == MergedFiles.BOTH ||
			    mergedReferenceFiles == MergedFiles.GV ||
			    mergedReferenceFiles == MergedFiles.BOTH) {
				savePropertiesToFile(deploymentGlobalVariables,
									 earGlobalVariables,
									 "Global Variables",
									 PROPERTIES_SAVE_GVS_SUCCESS,
									 PROPERTIES_SAVE_GVS_FAILURE);
			}
			//  Services (=~ Process Archives)
			if (mergedCommonFiles == MergedFiles.SERVICES ||
			    mergedCommonFiles == MergedFiles.BOTH ||
			    mergedReferenceFiles == MergedFiles.SERVICES ||
			    mergedReferenceFiles == MergedFiles.BOTH) {
				savePropertiesToFile(deploymentServices,
									 earServices,
									 "Services (Bindings, Processes)",
									 PROPERTIES_SAVE_SERVICES_SUCCESS,
									 PROPERTIES_SAVE_SERVICES_FAILURE);
			}
		} catch (Exception e) {
			throw new MojoExecutionException(MERGE_FAILURE, e);
		}
	}

	private Properties removeEmptyBindings(Properties properties) {
		ArrayList<String> pars = new ArrayList<String>();
		
		Pattern pNotEmptyBinding = Pattern.compile(regexNotEmptyBinding);
		Pattern pEmptyBinding = Pattern.compile(regexEmptyBinding);
		String parName;
		
		Enumeration<Object> e = properties.keys();
		// first check if there is at least one non empty binding (non default)
	   	while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			Matcher mNotEmptyBinding = pNotEmptyBinding.matcher(key);
			if (mNotEmptyBinding.matches()) {
				parName = mNotEmptyBinding.group(1);
				if (pars.contains(parName)) continue;
				pars.add(parName);
			}
		}
	   	// then delete
		e = properties.keys();
	   	while (e.hasMoreElements()) {
	   		String key = (String) e.nextElement();
	   		Matcher mEmptyBinding = pEmptyBinding.matcher(key);
	   		if (mEmptyBinding.matches()) {
	   			parName = mEmptyBinding.group(1);
	   			if (pars.contains(parName)) {
	   				properties.remove(key);
	   			}
	   		}
	   	}
		
		return properties;
	}

}
