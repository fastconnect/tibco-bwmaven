ABSTRACT
	This Archetype is used to mavenize an existing BW project.

COMMAND
	mvn archetype:generate -DarchetypeGroupId=fr.fastconnect.factory.tibco.bw.maven \
						   -DarchetypeArtifactId=archetype-bw-mavenizer \
						   -DgroupId=com.company.project \
						   -DartifactId=project-root \
						   -Dversion=1.0.0 \
						   [-DinteractiveMode=false] 