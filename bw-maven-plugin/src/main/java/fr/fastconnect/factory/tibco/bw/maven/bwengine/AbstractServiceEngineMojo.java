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
package fr.fastconnect.factory.tibco.bw.maven.bwengine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import fr.fastconnect.factory.tibco.bw.maven.AbstractBWMojo;
import fr.fastconnect.factory.tibco.bw.maven.exception.BinaryMissingException;

/**
 * <p>
 * This abstract class defines the core features for the execution of Service
 * inside a BusinessWorks engine.
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
public abstract class AbstractServiceEngineMojo extends AbstractBWMojo {

	protected final static String BWENGINE_MISSING = "The BusinessWorks engine can't be found.";
	protected final static String BWENGINE_STARTING = "Starting BusinessWorks Engine";
	protected final static String BWENGINE_STOPPING = "Stopping BusinessWorks Engine";
	protected final static String BWENGINE_ERROR_STARTING = "Error while starting the BusinessWorks Engine";
	protected final static String BWENGINE_TIMEOUT_STARTING = "Timeout while starting the BusinessWorks Engine";
	
	/**
	 * Path to the BusinessWorks Engine binary.
	 */
	@Parameter (property = "bwengine.path", required = true)
	protected File tibcoBWEnginePath;

	/**
	 * <p>
	 * The minimum value for the randomly chosen port.
	 * </p>
	 */
	@Parameter (property = "bw.service.minPort", defaultValue="49152")
	protected int minPort;

	/**
	 * <p>
	 * The maximum value for the randomly chosen port.
	 * </p>
	 */
	@Parameter (property = "bw.service.maxPort", defaultValue="65535")
	protected int maxPort;

	/**
	 * A Service Agent is a Web Service representation in TIBCO BusinessWorks.
	 * This Service Agent will be started when the BusinessWorks engine starts.
	 */
	protected ServiceAgentInEngine<?> serviceAgent;

	private void bwEngineNotFound() throws MojoExecutionException {
		throw new BinaryMissingException(BWENGINE_MISSING);
	}

	private void checkBWEngine() throws MojoExecutionException {
		if (tibcoBWEnginePath == null ||
		   !tibcoBWEnginePath.exists() ||
		   !tibcoBWEnginePath.isFile()) {
			bwEngineNotFound();
		}
	}

	/**
	 * 
	 * @return true si le Service Agent répond à l'invocation de la méthode
	 * isStarted qui agit comme une méthode de type "ping" pour savoir si le
	 * service est bien lancé, false sinon.
	 */
	public boolean isStarted() {
		return (serviceAgent == null) ? false : serviceAgent.isStarted();
	}

	/**
	 * This will call the stopEngine() method of the Service Agent. The Service
	 * Agent will then stop the BusinessWorks engine.
	 */
	public void stopEngine() {
		if (serviceAgent != null) {
			serviceAgent.stopEngine();
		}
	}

	public abstract void initServiceAgent() throws MojoExecutionException;
	
	public abstract void executeServiceMethods() throws MojoExecutionException;

	public abstract String getBWEnginePort();
	public abstract int getRetryInterval();
	public abstract String getServiceName();
	public abstract String getServiceFailureMessage();
	public abstract String getServiceTimeoutMessage();
	
	public int getTimeOut() {
		return timeOut; // default TimeOut, concrete children will override
	}
	
	private int getMaxRetry() {
		return getTimeOut() / getRetryInterval();
	}

	public static boolean available(int port, int minPort, int maxPort) {
	    if (port < minPort || port > maxPort || port > 65535) {
	        throw new IllegalArgumentException("Invalid start port: " + port);
	    }

	    ServerSocket ss = null;
	    DatagramSocket ds = null;
	    try {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        ds = new DatagramSocket(port);
	        ds.setReuseAddress(true);
	        return true;
	    } catch (IOException e) {
	    } finally {
	        if (ds != null) {
	            ds.close();
	        }

	        if (ss != null) {
	            try {
	                ss.close();
	            } catch (IOException e) {
	                /* should not be thrown */
	            }
	        }
	    }

	    return false;
	}

	public Integer getFreePort(int minPort, int maxPort) {
		Random rand = new Random();
		int port = -1;
		do {
			port = rand.nextInt((maxPort - minPort) + 1) + minPort;
		} while (!available(port, minPort, maxPort));

	    return port;
	}

	public Integer getFreePort() {
		return getFreePort(minPort, maxPort);
	}

	public void execute() throws MojoExecutionException {		
		super.execute();

		checkBWEngine();
		tibcoBWEnginePath = new File(tibcoBWEnginePath.getAbsolutePath());

		initServiceAgent();

		try {
			startEngine();
		} catch (IOException e) {
			throw new MojoExecutionException(getServiceFailureMessage(), e);
		}

		int maxRetry = getMaxRetry();
		
		while (!isStarted() && (maxRetry > 0)) {
			try {
				Thread.sleep(getRetryInterval() * 1000);
			} catch (InterruptedException iee) {
				iee.printStackTrace(); // FIXME : remove printStackTrace()
			}
			maxRetry--;
		}

		if (!isStarted()) {
			throw new MojoExecutionException(getServiceTimeoutMessage());
		}

		getLog().debug("Engine started");

		// now that the Service is started, concrete children can call the ops
		executeServiceMethods();

		// stopping BusinessWorks engine
		getLog().info(BWENGINE_STOPPING);
		stopEngine();
	}

	private void startEngine() throws IOException, MojoExecutionException {
		Properties engineProperties = new Properties();
		engineProperties.setProperty("tibco.clientVar." + getServiceName() + "/HTTP-service-port", getBWEnginePort());
		engineProperties.setProperty("bw.plugin.jms.recoverOnStartupError", "true");
		
//		engineProperties.setProperty("Hawk.Enabled", "true");
//		engineProperties.setProperty("ServiceAgent.builtinResource.serviceagent.Class", "com.tibco.plugin.brp.BRPServiceAgent");
//		engineProperties.setProperty("bw.platform.services.retreiveresources.Enabled", "true");

		// création du fichier de propriétés (chargé dans le BWEngine avec l'option "-p")
		File propertyFile = new File(directory, "bwengine.properties");
		propertyFile.createNewFile();

		FileOutputStream fos = new FileOutputStream(propertyFile);
		engineProperties.store(fos, null);
		fos.close();
		
		// merge aliases file into the properties file of the bwengine
		File aliasesFile = new File(directory, ALIASES_FILE);
		if (aliasesFile.exists()) {
			FileUtils.write(propertyFile, FileUtils.readFileToString(aliasesFile), true);
		}
		
		ArrayList<String> arguments = new ArrayList<String>();
		arguments.add("-name"); 
		arguments.add(getServiceName() + " Web Service");
		arguments.add("-p"); // properties file
		//arguments.add("-a"); // alias file
		arguments.add(propertyFile.getAbsolutePath());
		arguments.add(getProjectToRunPath().getAbsolutePath());
		getLog().info(BWENGINE_STARTING);

		ArrayList<File> tras = new ArrayList<File>();
		tras.add(tibcoBWEngineTRAPath);
		
		launchTIBCOBinary(tibcoBWEnginePath, tras, arguments, directory, getServiceFailureMessage(), false, false);
	}

	protected abstract File getProjectToRunPath();

}
