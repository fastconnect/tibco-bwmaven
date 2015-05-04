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

import COM.TIBCO.hawk.talon.CompositeData;
import COM.TIBCO.hawk.talon.DataElement;
import COM.TIBCO.hawk.talon.MicroAgentException;
import COM.TIBCO.hawk.talon.TabularData;

public class HawkFormatter {

	public static String formatData(Object madata) {
		if (madata instanceof CompositeData) {
			CompositeData compData = (CompositeData) madata;
			DataElement[] data = compData.getDataElements();

			StringBuffer sb = new StringBuffer("composite{");

			for (int i = 0; i < data.length; i++) {
				sb.append(data[i] + ((i == (data.length - 1)) ? "}" : ", "));
			}

			return sb.toString();
		} else if (madata instanceof TabularData) {
			TabularData tabData = (TabularData) madata;

			String[] columnNames = tabData.getColumnNames();
			String[] indexNames = tabData.getIndexNames();
			// alternatively you can use getAllDataElements() as well
			Object[][] table = tabData.getAllData();

			StringBuffer sb = new StringBuffer();
			sb.append("table{");
			sb.append("columns={");

			for (int i = 0; i < columnNames.length; i++) {
				sb.append(columnNames[i] + ((i == (columnNames.length - 1)) ? "} " : ", "));
			}

			sb.append("indexColumns={");

			for (int i = 0; i < indexNames.length; i++) {
				sb.append(indexNames[i]	+ ((i == (indexNames.length - 1)) ? "} " : ", "));
			}

			sb.append("values={");
			if (table == null) {
				sb.append("null");
			} else {
				for (int i = 0; i < table.length; i++) {
					sb.append("row" + i + "={");
					for (int j = 0; j < table[i].length; j++) {
						sb.append(table[i][j] + ((j == (table[i].length - 1)) ? "} " : ", "));
					}
				}
			}
			sb.append("}");
			sb.append("}");

			return sb.toString();
		} else if (madata instanceof MicroAgentException) {
			MicroAgentException exc = (MicroAgentException) madata;
			return "EXCEPTION: " + exc;
		} else if (madata == null) {
			return "Method Invocation returned no data.";
		} else {
			return "Method Invocation returned data of unknown type.";
		}
	}

}
