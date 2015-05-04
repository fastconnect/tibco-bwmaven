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

import java.util.List;

import fr.fastconnect.factory.tibco.bw.maven.packaging.monitoring.FailureEvent;
import fr.fastconnect.factory.tibco.bw.maven.packaging.monitoring.LogEvent;


public class Events {

	private List<FailureEvent> failures;
	private List<LogEvent> events;

	public List<FailureEvent> getFailures() {
		return failures;
	}

	public void setFailures(List<FailureEvent> failures) {
		this.failures = failures;
	}

	public List<LogEvent> getLogs() {
		return events;
	}

	public void setEvents(List<LogEvent> events) {
		this.events = events;
	}
}
