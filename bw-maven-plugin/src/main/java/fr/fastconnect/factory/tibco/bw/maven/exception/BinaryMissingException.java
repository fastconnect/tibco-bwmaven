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
package fr.fastconnect.factory.tibco.bw.maven.exception;

import java.io.FileNotFoundException;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * <p>
 * This class is the default exception thrown when a binary is not found.
 * </p>
 * <p>
 * As a consequence this exception could display a help message in order to fix
 * the configuration of the project.
 * </p>
 * 
 * @author Mathieu Debove
 *
 */
public class BinaryMissingException extends MojoExecutionException {
	private static final long serialVersionUID = 8885220376998182560L;

	public BinaryMissingException(String message) {
		super(message, new FileNotFoundException());
	}

}
