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

import static fr.fastconnect.factory.tibco.bw.maven.hawk.HawkFormatter.formatData;

import java.util.List;

import COM.TIBCO.hawk.talon.DataElement;
import COM.TIBCO.hawk.talon.MethodInvocation;
import COM.TIBCO.hawk.talon.MicroAgentData;
import COM.TIBCO.hawk.talon.MicroAgentException;

public class MethodInvocator extends AbstractMethodAction {
    private MethodInvocation methodInvocation;

	public MethodInvocator(MicroAgent microAgent) throws Exception {
		super(microAgent);
	}

    public MethodInvocator(String hawkDomain, String rvService, String rvNetwork, String rvDaemon) throws Exception {
    	super(hawkDomain, rvService, rvNetwork, rvDaemon);
    }

    public MethodInvocator(String hawkDomain, String rvService, String rvNetwork, String rvDaemon, String microAgentName) throws Exception {
    	super(hawkDomain, rvService, rvNetwork, rvDaemon, microAgentName);
    }

    @Override
    public void setMethodName(String methodName) {
    	super.setMethodName(methodName);
		methodInvocation = new MethodInvocation(methodName, null);    	
    }

    @Override
    public void setMethodName(String methodName, List<DataElement> arguments) {
    	super.setMethodName(methodName);
    	methodInvocation = new MethodInvocation(methodName, arguments.toArray(new DataElement[0]));    	
    }

	public void invocate() throws Exception {
		try {
			MicroAgentData m = getAgentManager().invoke(getMicroAgentID(), methodInvocation);
			Object maData = m.getData();
			if (maData != null) {
				formatData(maData);
			}
		} catch (MicroAgentException me) {
			System.out.println("ERROR while performing a method invocation: " + me);
			throw new Exception(me.getMessage());
		}
	}

	// Main - Usage: java ConsoleApp <hawkDomain> <rvService> <rvNetwork> <rvDaemon>
    public static void main(String[] args) {
    	MethodInvocator ca = null;

        if (args.length != 4 ) {
            System.err.println("Usage: java ConsoleApp <hawkDomain> <rvService> <rvNetwork> <rvDaemon>");
            return;
        }

        if (args.length == 4) {
			try {
				ca = new MethodInvocator(args[0],args[1],args[2],args[3],"COM.TIBCO.admin.TRA");
				ca.setMethodName("getComponentInstanceStatus");
				ca.setArguments("TEC-JMS", "TEC-JMS-IN");
//				ca.setMethodName("getFileStatusForComponentFile");
//				ca.setArguments("TEC-JMS", "TEC-JMS-IN", "C:/tibco/tra/domain/LOCAL/application/logs/TEC-JMS-TEC-JMS-IN.log");
				ca.invocate();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.exit(0);
			}
        }
    }
}
