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
package fr.fastconnect.tibco.businessworks.fcunit.processes;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import fr.fastconnect.tibco.businessworks.fcunit.ProjectBaseDir;

/**
 * 
 * 
 * @author Mathieu Debove
 *
 */
public abstract class AbstractProcessList<ProcessType extends AbstractProcess> implements Serializable {
	
	private static final long serialVersionUID = 7827257804215752755L;
	
	protected HashMap<String, ProcessType> processes = new HashMap<String, ProcessType>();

	private FileFilter processFilter;
	private String projectUri;

	@SuppressWarnings("unchecked")
	public AbstractProcessList(String scopeDir, AbstractProcessFactory abstractProcessFactory) {
		this.processFilter = getProcessFilter();
		this.projectUri = ProjectBaseDir.getProjectBaseDir();
		
		HashSet<String> processes = getProcesses();

		for (String processPath : processes) {
			ProcessType process = ((ProcessType) abstractProcessFactory.createProcess(processPath));
			if (process.addProcessToList((HashMap<String, AbstractProcess>) this.processes)) {
				this.processes.put(processPath, ((ProcessType) abstractProcessFactory.createProcess(processPath)));
			}
		}
	}

	private FileFilter getProcessFilter() {
		FileFilter filter = new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory() ||
					   pathname.getName().toLowerCase().endsWith(".process");
			}
		};
		
		return filter;
	}
	
	public void listProcessFiles(File file, HashSet<String> files) {
		if (file.isDirectory()) {
			File[] filesInDirectory = file.listFiles(processFilter);
			for (File innerFile : filesInDirectory) {
				listProcessFiles(innerFile, files);
			}
		}
		else if (file.isFile() && processFilter.accept(file)) {
			files.add(
				file.getAbsolutePath()
				.substring(projectUri.length())
				.replaceAll("\\\\", "/")
			);
		}
	}
	
	private HashSet<String> getProcesses() {
		HashSet<String> processes = new HashSet<String>();

		listProcessFiles(new File(ProjectBaseDir.getProjectBaseDir()), processes);
		
		return processes;
	}

	public int getProcessesCount() {
		return getProcessesList() == null ? 0 : getProcessesList().length; 
	}
	
	public abstract ProcessType[] getProcessesList();
}
