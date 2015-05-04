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
package fr.fastconnect.tibco.businessworks.fcunit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helpers {
	
	/**
	 * 
	 * @param path
	 * @return true si path se termine par TestCase
	 */
	public static boolean matchCase(String path) {
		Pattern p = Pattern.compile(".*TestCase$");
		Matcher m = p.matcher(path);

		return m.matches();
	}

	/**
	 * 
	 * @param path
	 * @return true si path se termine par TestSuite
	 */
	public static boolean matchSuite(String path) {
		Pattern p = Pattern.compile(".*TestSuite$");
		Matcher m = p.matcher(path);

		return m.matches();
	}
	
}
