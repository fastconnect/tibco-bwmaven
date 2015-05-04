ABSTRACT
	This Archetype can be used to create a "root project". This project will be
the parent POM for all TIBCO BW mavenized children projects.

COMMAND
	mvn archetype:generate -DarchetypeGroupId=fr.fastconnect.factory.tibco.bw.maven \
						   -DarchetypeArtifactId=archetype-client-root-project \
						   -DgroupId=com.company.project \
						   -DartifactId=project-root \
						   -Dversion=1.0.0 \
						   -DclientProjectName=ProjectName \
						   -DfcFactoryParentVersion=1.0.0 \
						   [-DinteractiveMode=false] 