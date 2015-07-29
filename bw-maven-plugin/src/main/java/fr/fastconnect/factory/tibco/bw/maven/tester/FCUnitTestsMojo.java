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
package fr.fastconnect.factory.tibco.bw.maven.tester;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.ZipScanner;
import org.apache.tools.ant.types.selectors.ContainsSelector;
import org.apache.tools.ant.types.selectors.FileSelector;

import fr.fastconnect.factory.tibco.bw.maven.bwengine.AbstractServiceEngineMojo;
import fr.fastconnect.factory.tibco.bw.maven.jaxws.Settings;

/**
 * <p>
 * This goal runs the unit tests of a FCUnit-enabled TIBCO BusinessWorks
 * project.
 * </p>
 * 
 */
@Mojo( name="bw-test",
defaultPhase=LifecyclePhase.TEST )
public class FCUnitTestsMojo extends AbstractServiceEngineMojo {

	/**
	 * Port of the FCUnit WebService in the TIBCO BWEngine running the tests.
	 */
	@Parameter (property = "fcunit.engine.port", required = true, defaultValue = "9099")
	protected String bwEnginePort;
	
	@Parameter (property = "fcunit.timeout", defaultValue = "100")
	private int timeOut; // in seconds

	@Parameter (property = "fcunit.retryInterval", defaultValue = "1")
	private int retryInterval; // interval between FCUnit WebService call, in seconds
	
	protected final static String FCUNIT_SERVICE_NAME = "FCUnit";
	protected final static String FCUNIT_FAILURE = "The execution of the tests failed";
	protected final static String FCUNIT_TIMEOUT = "The execution of the tests timed out";
	protected final static String FCUNIT_SUCCESSFUL = "All the tests were executed successfully";
	protected final static String FCUNIT_RESULTS = "The results are found in : ";

	protected static final String JUNIT_JAR_ALIAS = "junit:junit:jar:4.8.2:compile";
	protected static final String XMLUNIT_JAR_ALIAS = "xmlunit:xmlunit:jar:1.3:compile";
	
	/**
	 * Whether the FCUnit tests on the project should be skipped or not.
	 */
	@Parameter (property = "bw.tests.skip")
	private boolean skipTests;

	public void setBwEngineTRAPath(File bwEngineTRAPath) {
		this.tibcoBWEngineTRAPath = bwEngineTRAPath;
	}

	private boolean skipTests() {
		if (skipTests) {
			getLog().info("Skipping FCUnit tests.");
			return true;
		} else {
			return false;
		}
	}

	private void checkWorkingDirectory() throws MojoExecutionException {
		if (!directory.exists()) {
			directory.mkdir();
		}

		if (!directory.isDirectory()) {
			throw new MojoExecutionException("Working directory for Maven build can't be found or created", new FileNotFoundException());
		}
	}

	private void checkOutputDirectory() throws MojoExecutionException {
		if (!testDirectory.exists()) {
			testDirectory.mkdir();
		}

		if (!testDirectory.isDirectory()) {
			throw new MojoExecutionException("Output directory for tests can't be found or created", new FileNotFoundException());
		}
	}

	@Override
	public void executeServiceMethods() throws MojoExecutionException {
		Settings settings = new Settings();
		settings.setScope(null);
		settings.setSuitePattern(null);
		settings.setCasePattern(null);
		settings.setExportToFiles(true);
		settings.setExportDirectory(testDirectory.getAbsolutePath());

		// exécution des tests en appelant la méthode "runAllTests" du Service Agent de FCUnit
		if (((FCUnitService) serviceAgent).runAllTests(settings)) {
			getLog().info(FCUNIT_SUCCESSFUL);
			getLog().info(FCUNIT_RESULTS + "\"" + testDirectory.getAbsolutePath() + "\"");
		}		
	}

	@Override
	public String getBWEnginePort() {
		return bwEnginePort;
	}

	@Override
	public int getRetryInterval() {
		return retryInterval;
	}
	
	@Override
	public int getTimeOut() {
		return timeOut;
	}

	@Override
	public String getServiceName() {
		return FCUNIT_SERVICE_NAME;
	}

	@Override
	public String getServiceFailureMessage() {
		return FCUNIT_FAILURE;
	}

	@Override
	public String getServiceTimeoutMessage() {
		return FCUNIT_TIMEOUT;
	}

	@Override
	public void initServiceAgent() throws MojoExecutionException {
		try {
			serviceAgent = new FCUnitService(bwEnginePort);
		} catch (Exception e) {
			throw new MojoExecutionException(FCUNIT_FAILURE, e);
		}
	}

	// si on ne trouve pas "fcunit-projlib" on skippe
	// FIXME : peut-on déléguer l'exécution d'un goal à un plugin
	// externe? (FCUnit is back?)
	private boolean foundFCUnitDependency() {
		try {
			List<Dependency> dependencies = getDependencies(PROJLIB_TYPE, false);
			
			for (Dependency dependency : dependencies) {
				if ("fcunit-projlib".equals(dependency.getArtifactId())) {
					return true;
				}
			}
		} catch (IOException e) {
			return false;
		}
		return false;
	}
	
	public void execute() throws MojoExecutionException {
		if (skipTests()) {
			return;
		}
		
		enableTestScope();

		if (!foundFCUnitDependency()) {
			getLog().info("FCUnit tests require the \"fcunit-projlib:projlib\" dependency.");
			return;
		}

		try {
			cleanStarters();
		} catch (ZipException e) {
			throw new MojoExecutionException(FCUNIT_FAILURE, e);
		} catch (IOException e) {
			throw new MojoExecutionException(FCUNIT_FAILURE, e);
		}
		
		checkWorkingDirectory();
		checkOutputDirectory();

		super.execute();
	}

	private void removeFileContaining(String content) {
		DirectoryScanner ds = new DirectoryScanner();
		String[] includes = {"**/*.process"};
		ds.setIncludes(includes);
		ds.setBasedir(testSrcDirectory);
		//ds.setCaseSensitive(true);
		
		ContainsSelector contentToRemove = new ContainsSelector();
		contentToRemove.setText(content);
		
		FileSelector[] selectors = {contentToRemove};
		ds.setSelectors(selectors);
		ds.scan();
		
		String[] files = ds.getIncludedFiles();
		for (int i = 0; i < files.length; i++) {
			String file = testSrcDirectory + File.separator + files[i];
			getLog().debug("Deleting file with starter : '" + file + "'");
			getLog().info("Deleting file with starter : '" + file + "'");
			FileUtils.deleteQuietly(new File(file));
		}

	}
	
	private void removeFileInZipContaining(List<String> contentFilter, File zipFile) throws ZipException, IOException {
		ZipScanner zs = new ZipScanner();
		zs.setSrc(zipFile);
		String[] includes = {"**/*.process"};
		zs.setIncludes(includes);
		//zs.setCaseSensitive(true);
		zs.init();
		zs.scan();

		File originalProjlib = zipFile; // to be overwritten
		File tmpProjlib = new File(zipFile.getAbsolutePath() + ".tmp"); // to read
		FileUtils.copyFile(originalProjlib, tmpProjlib);
		
		ZipFile listZipFile = new ZipFile(tmpProjlib);
		ZipInputStream readZipFile = new ZipInputStream(new FileInputStream(tmpProjlib));
		ZipOutputStream writeZipFile = new ZipOutputStream(new FileOutputStream(originalProjlib));
		
		ZipEntry zipEntry;
		boolean keep;
		while ((zipEntry = readZipFile.getNextEntry()) != null) {
			keep = true;
			for (String filter : contentFilter) {
				keep = keep && !containsString(filter, listZipFile.getInputStream(zipEntry));
			}
//			if (!containsString("<pd:type>com.tibco.pe.core.OnStartupEventSource</pd:type>", listZipFile.getInputStream(zipEntry))
//			 && !containsString("<pd:type>com.tibco.plugin.jms.JMSTopicEventSource</pd:type>", listZipFile.getInputStream(zipEntry))) {
			if (keep) {
				writeZipFile.putNextEntry(zipEntry);
		        int len = 0;
		        byte[] buf = new byte[1024];
		        while ((len = readZipFile.read(buf)) >= 0) {
		        	writeZipFile.write(buf, 0, len);
		        }
		        writeZipFile.closeEntry();
		        //getLog().info("written");
			} else {
				getLog().info("removed " + zipEntry.getName());
			}

		}

		writeZipFile.close();
		readZipFile.close();
		listZipFile.close();
		
		originalProjlib.setLastModified(originalProjlib.lastModified()-100000);
	}
	
	private void cleanStarters() throws ZipException, IOException {
		getLog().info("Cleaning the starters from " + testSrcDirectory);
		
		List<String> ignore = new ArrayList<String>();
		ignore.add("<pd:type>com.tibco.plugin.jms.JMSQueueEventSource</pd:type>");
		ignore.add("<pd:type>com.tibco.pe.core.OnStartupEventSource</pd:type>");
		ignore.add("<pd:type>com.tibco.plugin.jms.JMSQueueSendActivity</pd:type>");
		ignore.add("<pd:type>com.tibco.plugin.jms.JMSQueueRequestReplyActivity</pd:type>");
		
		for (String i : ignore) {
			removeFileContaining(i);
		}
		
		// look inside Projlibs too !
		if (testLibDirectory.exists()) {
			DirectoryScanner ds = new DirectoryScanner();
			String[] includes = {"**/*.projlib"};
			ds.setIncludes(includes);
			String[] excludes = {"**/fcunit*.projlib"};
			ds.setExcludes(excludes);
			ds.setBasedir(testLibDirectory);
			//ds.setCaseSensitive(true);
			ds.scan();
		
			String[] files = ds.getIncludedFiles();
			List<String> projlibsFiles = Arrays.asList(files);
			for (String projlibFile : projlibsFiles) {
				removeFileInZipContaining(ignore, new File(testLibDirectory + File.separator + projlibFile));
			}
		}
	}

	private boolean containsString(String string, InputStream fileInZip) throws IOException {
		boolean result = false;
		BufferedInputStream lzf = new BufferedInputStream(fileInZip);
		FindStringInInputStream ris = new FindStringInInputStream(lzf);
		
		result = ris.containsString(string);
		
		ris.close();
		lzf.close();
		
		return result;
	}

	class FindStringInInputStream extends FilterInputStream {
	    LinkedList<Integer> inQueue;
	    LinkedList<Integer> outQueue;
	    private byte[] search;
		private boolean found;

	    protected FindStringInInputStream(InputStream in) {
	        super(in);
	    }

	    private void init() throws IOException {
	    	inQueue = new LinkedList<Integer>();
		    outQueue = new LinkedList<Integer>();
	    }
	    
	    private boolean isMatchFound() {
	        Iterator<Integer> inIter = inQueue.iterator();
	        for (int i = 0; i < search.length; i++)
	            if (!inIter.hasNext() || search[i] != inIter.next())
	                return false;
	        return true;
	    }

	    private void readAhead() throws IOException {
	        // Work up some look-ahead.
	        while (inQueue.size() < search.length) {
	            int next = super.in.read();
	            inQueue.offer(next);
	            if (next == -1)
	                break;
	        }
	    }


	    @Override
	    public int read() throws IOException {
	    	//this.in.read();
	        // Next byte already determined.
	        if (outQueue.isEmpty()) {

	            readAhead();

	            if (isMatchFound()) {
	            	found = true;
	            	return -1;
	            } else
	                outQueue.add(inQueue.remove());
	        }

	        return outQueue.remove();
	    }

	    public boolean containsString(String str) throws IOException {
	    	search = str.getBytes("UTF-8");
	    	found = false;
	    	init();
	    	
	    	while (this.read() != -1) {}
	    	
	    	return found;
	    }
	    // TODO: Override the other read methods.
	}

	@Override
	protected File getProjectToRunPath() {
		return testSrcDirectory;
	}

}
