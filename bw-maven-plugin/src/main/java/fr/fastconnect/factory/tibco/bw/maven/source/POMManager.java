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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.inheritance.DefaultInheritanceAssembler;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * This class handles POM file manipulation using the Maven API.
 * 
 * @author Mathieu Debove
 *
 */
public class POMManager {
	
	/**
	 * Load a Maven {@link Model} object from a POM file.
	 * 
	 * @param pom
	 * @param logger
	 * @return the model parsed from the POM file
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	public static Model getModelFromPOM(File pom, Log logger) throws IOException, XmlPullParserException {
		Model model = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		try {
			fis = new FileInputStream(pom);
			isr = new InputStreamReader(fis, "utf-8"); // FIXME
			MavenXpp3Reader reader = new MavenXpp3Reader();
			model = reader.read(isr);
		} finally {
			try {
				isr.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return model;
	}

	/**
	 * Merge a Maven {@link Model} object from a POM file to an existing Maven
	 * {@link Model} object.
	 *
	 * @param pom
	 * @param existingModel
	 * @param logger
	 * @return the existing model merged with the parsed model from the POM file
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static Model mergeModelFromPOM(File pom, Model existingModel, Log logger) throws IOException, XmlPullParserException {
		if (pom == null || !pom.exists() || existingModel == null || logger == null) return null;

		Model model = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		try {
			fis = new FileInputStream(pom);
			isr = new InputStreamReader(fis, "utf-8"); // FIXME
			MavenXpp3Reader reader = new MavenXpp3Reader();
			model = reader.read(isr);
			DefaultInheritanceAssembler assembler = new DefaultInheritanceAssembler();
			assembler.assembleModelInheritance(model, existingModel, null, null);
		} finally {
			try {
				isr.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return model;
	}

	/**
	 * Write a Maven {@link Model} object to a POM file.
	 * 
	 * @param model
	 * @param pom
	 * @param logger
	 * @throws IOException
	 */
	public static void writeModelToPOM(Model model, File pom, Log logger) throws IOException {
		FileOutputStream fos = new FileOutputStream(pom);
		
		new MavenXpp3Writer().write(fos, model);

		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add the Maven dependency to a POM file.
	 * 
	 * @param pom
	 * @param dependency
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static void addDependency(File pom, Dependency dependency, Log logger) throws IOException, XmlPullParserException {
		Model model = getModelFromPOM(pom, logger);

		model.addDependency(dependency);
		
		writeModelToPOM(model, pom, logger);
	}

	/**
	 * Add the Maven dependency to a POM file (in management section).
	 * 
	 * @param pom
	 * @param dependency
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static void addDependencyManagement(File pom, Dependency dependency, Log logger) throws IOException, XmlPullParserException {
		Model model = getModelFromPOM(pom, logger);
		
		DependencyManagement dMgmt = model.getDependencyManagement();
		if (dMgmt == null) {
			model.setDependencyManagement(new DependencyManagement());
			dMgmt = model.getDependencyManagement();
		}
		dMgmt.addDependency(dependency);
		
		writeModelToPOM(model, pom, logger);
	}

	/**
	 * Remove the Maven dependency from a POM file.
	 * 
	 * @param pom
	 * @param dependency
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static void removeDependency(File pom, Dependency dependency, Log logger) throws IOException, XmlPullParserException {
		Model model = getModelFromPOM(pom, logger);

		for (Iterator<Dependency> it = model.getDependencies().iterator(); it.hasNext();){
			if (dependenciesEqual(it.next(), dependency)) {
				it.remove();
			}
		}
		
		writeModelToPOM(model, pom, logger);
	}

	/**
	 * Remove the Maven dependency from a POM file (in management section).
	 * 
	 * @param pom
	 * @param dependency
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static void removeDependencyManagement(File pom, Dependency dependency, Log logger) throws IOException, XmlPullParserException {
		Model model = getModelFromPOM(pom, logger);
		
		DependencyManagement dMgmt = model.getDependencyManagement();
		if (dMgmt == null) {
			model.setDependencyManagement(new DependencyManagement());
			dMgmt = model.getDependencyManagement();
		}
		
		for (Iterator<Dependency> it = dMgmt.getDependencies().iterator(); it.hasNext();){
			if (dependenciesEqual(it.next(), dependency)) {
				it.remove();
			}
		}
		
		writeModelToPOM(model, pom, logger);
	}

	private static boolean dependenciesEqual(Dependency d1, Dependency d2) {
		boolean result = true;
		
		if (d1 == null || d2 == null) {
			return d1 == d2;
		}
		
		result = result && d1.getGroupId().equals(d2.getGroupId());
		result = result && d1.getArtifactId().equals(d2.getArtifactId());
		if (d1.getVersion() != null) {
			result = result && d1.getVersion().equals(d2.getVersion());
		} else {
			result = result && (d2.getVersion() == null);
		}
		if (d1.getType() != null) {
			result = result && d1.getType().equals(d2.getType());
		} else {
			result = result && (d2.getType() == null);
		}
		if (d1.getClassifier() != null) {
			result = result && d1.getClassifier().equals(d2.getClassifier());
		} else {
			result = result && (d2.getClassifier() == null);
		}
		
		return result;		
	}

	/**
	 * Check whether a dependency exists in a list of dependencies.
	 * 
	 * @param dependency
	 * @param dependencies
	 * @return true if the dependency exists in dependencies list
	 */
	private static boolean dependencyExists(Dependency dependency, List<Dependency> dependencies) {
		for (Dependency d : dependencies) {
			if (dependenciesEqual(dependency, d)) {
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * Check whether a dependency exists in a POM.
	 * 
	 * @param pom
	 * @param dependency
	 * @param logger
	 * @return true if the dependency exists in the POM
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static boolean dependencyExists(File pom, Dependency dependency, Log logger) throws IOException, XmlPullParserException {
		Model model = getModelFromPOM(pom, logger);
		
		return dependencyExists(dependency, model.getDependencies());
	}

	/**
	 * Check whether a dependency exists in a POM (in management section).
	 * 
	 * @param pom
	 * @param dependency
	 * @param logger
	 * @return true if the dependency exists in the management section of the
	 * POM
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static boolean dependencyExistsManagement(File pom, Dependency dependency, Log logger) throws IOException, XmlPullParserException {
		Model model = getModelFromPOM(pom, logger);
		
		DependencyManagement dMgmt = model.getDependencyManagement();
		if (dMgmt == null) {
			model.setDependencyManagement(new DependencyManagement());
			dMgmt = model.getDependencyManagement();
		}

		return dependencyExists(dependency, dMgmt.getDependencies());
	}

	public static Profile getProfile(Model model, String profileId) {
		if (model == null || profileId == null || profileId.isEmpty()) return null;
		for (Profile profile : model.getProfiles()) {
			if (profileId.equals(profile.getId())) {
				return profile;
			}
		}

		Profile result = new Profile();
		result.setId(profileId);
		model.addProfile(result);
		return result;
	}

	/**
	 * Add a project as a module.
	 * 
	 * @param pom
	 * @param relativePath
	 * @param logger
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static void addProjectAsModule(File pom, String relativePath, String profileId, Log logger) throws IOException, XmlPullParserException {
		if (relativePath == null) return;

		Model model = getModelFromPOM(pom, logger);

		relativePath = relativePath.replace("\\", "/");

		if (profileId != null && !profileId.isEmpty()) {
			Profile p = getProfile(model, profileId);
			if (p != null) {
				p.addModule(relativePath);
			}
		} else {
			model.addModule(relativePath);
		}
		
		writeModelToPOM(model, pom, logger);
	}

	public static void removeProjectAsModule(File pom, String relativePath,	String profileId, Log logger) throws IOException, XmlPullParserException {
		if (relativePath == null) return;
		
		Model model = getModelFromPOM(pom, logger);
		
		relativePath = relativePath.replace("\\", "/");
		if (profileId != null && !profileId.isEmpty()) {
			Profile p = getProfile(model, profileId);
			if (p != null) {
				p.removeModule(relativePath);
			}
		} else {
			model.removeModule(relativePath);
		}
		
		writeModelToPOM(model, pom, logger);
	}

	/**
	 * Check whether a module exists in a POM.
	 * 
	 * @param rootPOM
	 * @param relative
	 * @param log
	 * @return 
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	public static boolean moduleExists(File pom, String relativePath, String profileId, Log logger) throws IOException, XmlPullParserException {
		if (relativePath == null) return false;

		Model model = getModelFromPOM(pom, logger);
		
		relativePath = relativePath.replace("\\", "/");

		if (profileId != null && !profileId.isEmpty()) {
			Profile p = getProfile(model, profileId);
			if (p != null) {
				return p.getModules().indexOf(relativePath) >= 0;
			}
		} else {
			return model.getModules().indexOf(relativePath) >= 0;
		}

		return false;
	}

}
