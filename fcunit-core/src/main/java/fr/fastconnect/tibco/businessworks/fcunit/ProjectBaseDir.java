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

import java.io.Serializable;

import com.tibco.bw.store.RepoAgent;
import com.tibco.pe.core.Engine;

/**
 * Cette classe permet de récupérer le chemin du répertoire de base
 * du projet dont les tests sont exécutés.
 * 
 * @author Mathieu Debove
 *
 */
public class ProjectBaseDir implements Serializable {

	private static final long serialVersionUID = 693667351865956165L;

	/**
	 * Les tests d'un projet contenant FCUnit sont exécutés via un Web Service.
	 * Durant l'exécution du service, les appels à cette méthode, la méthode statique {@link Engine#getRepoAgent()}
	 * renvoit le RepoAgent correspondant au projet appelant.
	 * 
	 * @return Le répertoire de base du projet qui a appelé la méthode (via une activité "Java Method" dans Business Works)
	 */
	public static String getProjectBaseDir() {
		RepoAgent ra = Engine.getRepoAgent();
		String uri = ra.getAbsoluteURIFromProjectRelativeURI("");
		
		return uri;
	}
}
