ABSTRACT
	This Archetype will create an empty TIBCO BusinessWorks project ready to be
	used with Maven.

COMMAND
	mvn archetype:generate -DarchetypeGroupId=fr.fastconnect.factory.tibco.bw.maven \
						   -DarchetypeArtifactId=archetype-bw-default-project \
						   -DgroupId=com.company.project \
						   -DartifactId=project-root \
						   -Dversion=1.0.0 \
						   [-DinteractiveMode=false] 