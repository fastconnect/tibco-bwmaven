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
package fr.fastconnect.factory.tibco.bw.javaxpath;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Goal which updates .javaxpath resources in the BW project.
 * 
 * @author Emilien THOMAS
 */
@Mojo(defaultPhase = LifecyclePhase.GENERATE_SOURCES, name = "generate", requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class XPathResourceBuilderMojo extends AbstractMojo {

	@Parameter(required = true)
	private List<XPathResource> resources;

	protected void classBytesToBWJavaXPath(final XPathResource r)
			throws IOException {
		// Extract byte64 encoding of the .class content
		byte[] bytesForClass = getBytesForClass(r.getClassName());
		String base64 = DatatypeConverter.printBase64Binary(bytesForClass);
		String fileContent = extractFileContent(r.getResourceName());

		String newFileContent;
		// Replace in the extracted file content
		if (fileContent.contains("bytecode>")) {
			newFileContent = fileContent.replaceFirst(
					"(bytecode>)([^<]*)([<])", "$1" + base64 + "$3");
		} else {
			newFileContent = fileContent.replaceFirst("(</)([^:]+):(java-xpath)", 
					"<$2:bytecode>" + base64 + "</$2:bytecode></$2:java-xpath");
		}
		// Overwrite the file
		final FileWriter fileWriter = new FileWriter(r.getResourceName(), false);
		try {
			fileWriter.write(newFileContent);
		} finally {
			fileWriter.close();
		}
	}

	/**
	 * MOJO execution method.
	 */
	public void execute() throws MojoExecutionException {

		if (this.resources.size() == 0) {
			throw new MojoExecutionException("No resources configured");
		}

		// Prefered to count error instead of failing on the first one.
		int errCount = 0;

		// Iterate on all configured resources.
		for (final XPathResource r : this.resources) {
			try {
				classBytesToBWJavaXPath(r);
			} catch (Exception e) {
				super.getLog().error(e);
				errCount++;
			}
		}
		if (errCount > 0) {
			throw new MojoExecutionException("There was " + errCount
					+ " error(s) during goal execution, check above.");
		}
	}

	/**
	 * Reads the complete content of the javaxpath file.
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	protected String extractFileContent(final String filename)
			throws IOException {
		final StringBuilder sb = new StringBuilder();
		String temp = "";
		final BufferedReader bufferedReader = new BufferedReader(
				new FileReader(filename));
		try {
			while (temp != null) {
				temp = bufferedReader.readLine();
				if (temp != null) {
					sb.append(temp);
				}
			}
			return sb.toString();
		} finally {
			bufferedReader.close();
		}
	}

	/**
	 * Reads a class byte code from current {@link ClassLoader}
	 * 
	 * @param className
	 * @return
	 * @throws RuntimeException
	 */
	protected byte[] getBytesForClass(final String className)
			throws RuntimeException {
		try {
			ClassLoader classLoader = this.getClass().getClassLoader();
			InputStream stream = classLoader.getResourceAsStream(className
					.replace(".", "/") + ".class");
			if (stream == null) {
				throw new NullPointerException("Class " + className
						+ " could not be loaded. Check plugin dependencies.");
			}
			byte[] byteArray = new byte[1024000];
			int readBytes = 0;
			int currentOffset = 0;
			while ((readBytes = stream.read(byteArray, currentOffset, 1024)) >= 0) {
				currentOffset += readBytes;
			}
			return Arrays.copyOf(byteArray, currentOffset);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
