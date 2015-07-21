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

import java.util.List;

import COM.TIBCO.hawk.console.hawkeye.ConsoleInitializationException;
import COM.TIBCO.hawk.talon.DataElement;
import COM.TIBCO.hawk.talon.MethodSubscription;
import COM.TIBCO.hawk.talon.MicroAgentData;
import COM.TIBCO.hawk.talon.MicroAgentException;
import COM.TIBCO.hawk.talon.Subscription;

public class MethodSubscriber extends AbstractMethodAction {	
    private MethodSubscription methodSubscription;
    private Integer interval = 15000; // default is 15 seconds
	private Integer numberOfRetry = 4;
	private boolean displayOutputData = true;

	private AdvancedSubscriptionHandler subscriptionHandler;

	public AdvancedSubscriptionHandler getSubscriptionHandler() {
		return subscriptionHandler;
	}

	public MethodSubscriber(MicroAgent microAgent) {
		super(microAgent);
	}

    public MethodSubscriber(String hawkDomain, String rvService, String rvNetwork, String rvDaemon) throws ConsoleInitializationException {
    	super(hawkDomain, rvService, rvNetwork, rvDaemon);
    }

    public MethodSubscriber(String hawkDomain, String rvService, String rvNetwork, String rvDaemon, String microAgentName) throws ConsoleInitializationException, MicroAgentException {
    	super(hawkDomain, rvService, rvNetwork, rvDaemon, microAgentName);
    }

    @Override
    public void setMethodName(String methodName) {
    	super.setMethodName(methodName);
		methodSubscription = new MethodSubscription(methodName, null, getInterval());    	
    }

    @Override
    public void setMethodName(String methodName, List<DataElement> arguments) {
    	super.setMethodName(methodName);
    	methodSubscription = new MethodSubscription(methodName, arguments.toArray(new DataElement[0]), getInterval());    	
    }

	/**
	 * @param interval, a number of seconds
	 */
	public void setInterval(int interval) {
		this.interval = interval * 1000;
		setMethodName(this.getMethodName(), this.getArguments());
	}

	public void setNumberOfRetry(int numberOfRetry) {
		this.numberOfRetry = numberOfRetry;
	}

	public void subscribe(AdvancedSubscriptionHandler subscriptionHandler) throws MicroAgentException {
		this.subscriptionHandler = subscriptionHandler;
       	subscriptionHandler.subscribe(getAgentManager(), getMicroAgentID(), methodSubscription, this.getNumberOfRetry(), this.displayOutputData);
	}

	public void subscribe() throws MicroAgentException {
       	subscribe(new DefaultSubscriptionHandler());
	}

	public Object getResult() {
		if (subscriptionHandler == null) {
			return null;
		}
		return subscriptionHandler.getResult();
	}

	public Integer getInterval() {
		return interval;
	}

	public Integer getNumberOfRetry() {
		return numberOfRetry;
	}

	protected class RunningInstanceSubscriptionHandler extends DefaultSubscriptionHandler {
		@Override
		public void onData(Subscription s, MicroAgentData mad) {
			super.onData(s, mad);
			
			String data = fr.fastconnect.factory.tibco.bw.maven.hawk.HawkFormatter.formatData(mad.getData());
			
			if (data != null && data.contains("RUNNING")) {
				s.cancel();
				result = "RUNNING";
				numberOfSubscriptions--;
			}
		}
	}

}
