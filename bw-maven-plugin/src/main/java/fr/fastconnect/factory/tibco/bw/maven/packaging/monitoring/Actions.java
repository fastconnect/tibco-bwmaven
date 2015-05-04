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
package fr.fastconnect.factory.tibco.bw.maven.packaging.monitoring;

import java.util.ArrayList;
import java.util.List;

public class Actions {
	private List<AlertAction> alerts;
	private List<CustomAction> customs;
	private List<EmailAction> emails;

	public List<AlertAction> getAlerts() {
		if (alerts == null) {
			alerts = new ArrayList<AlertAction>();
		}
		return alerts;
	}

	public void setAlerts(List<AlertAction> alerts) {
		this.alerts = alerts;
	}

	public List<CustomAction> getCustoms() {
		if (customs == null) {
			customs = new ArrayList<CustomAction>();
		}
		return customs;
	}

	public void setCustoms(List<CustomAction> customs) {
		this.customs = customs;
	}

	public List<EmailAction> getEmails() {
		if (emails == null) {
			emails = new ArrayList<EmailAction>();
		}
		return emails;
	}

	public void setEmails(List<EmailAction> emails) {
		this.emails = emails;
	}
}
