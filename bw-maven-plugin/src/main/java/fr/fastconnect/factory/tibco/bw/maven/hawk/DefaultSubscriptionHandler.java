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
package fr.fastconnect.factory.tibco.bw.maven.hawk;

import COM.TIBCO.hawk.console.hawkeye.AgentManager;
import COM.TIBCO.hawk.talon.MethodSubscription;
import COM.TIBCO.hawk.talon.MicroAgentData;
import COM.TIBCO.hawk.talon.MicroAgentException;
import COM.TIBCO.hawk.talon.MicroAgentID;
import COM.TIBCO.hawk.talon.Subscription;

public class DefaultSubscriptionHandler implements AdvancedSubscriptionHandler {
	public static Integer numberOfSubscriptions = 0;
	
	private Integer currentRetry = 0;
	private Integer numberOfRetry;
	
	protected Object result;

	@Override
	public Object getResult() {
		return result;
	}

	@Override
	public void subscribe(AgentManager agentManager, MicroAgentID microAgentID,	MethodSubscription methodSubscription, Integer numberOfRetry, boolean displayOuptutData) throws MicroAgentException {
		this.setSubscription(agentManager.subscribe(microAgentID, methodSubscription, this, this.getResult()), numberOfRetry, displayOuptutData);
	}

	private void setSubscription(Subscription subscription, Integer numberOfRetry, boolean displayOuptutData) {		
		increaseReferenceCount();

		if (numberOfRetry <= 0) {
			numberOfRetry = 1;
		}
		this.numberOfRetry = numberOfRetry;
		
		this.onSubscribe();
	}

	@Override
	public void onData(Subscription s, MicroAgentData mad) {
		currentRetry++;

		if (currentRetry == numberOfRetry) {
			s.cancel();
			decreaseReferenceCount();
		}
	}

	public void onError(Subscription s, MicroAgentException e) {
		decreaseReferenceCount();
	}

	public void onErrorCleared(Subscription s) {
		decreaseReferenceCount();
	}

	public void onTermination(Subscription s, MicroAgentException e) {
		decreaseReferenceCount();
	}

	@Override
	public String formatData(Object data) {
		return fr.fastconnect.factory.tibco.bw.maven.hawk.HawkFormatter.formatData(data);
	}

	@Override
	public void decreaseReferenceCount() {
		numberOfSubscriptions--;
	}

	@Override
	public void increaseReferenceCount() {
		numberOfSubscriptions++;
	}

	@Override
	public void onSubscribe() {
		
	}

}
