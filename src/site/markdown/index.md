
## Modules

The BWMaven plugin is composed of several modules.

### Maven Plugin

The core of BWMaven plugin is the [Maven Plugin for TIBCO BusinessWorks][bwmaven-link]

[bwmaven-link]: ./bw-maven-plugin/index.html

---

### Archetypes

Several default archetypes are provided to easily create new projects or import
existing ones.

---

#### BusinessWorks project

#### BusinessWorks Projlib

#### BusinessWorks mavenizer

#### Root pom

---

### FCUnit

FCUnit is the implementation of unit testing in the BWMaven plugin.
It is composed of two Maven artifacts:
* one BusinessWorks Projlib
* one Java ARchive (JAR)

---

#### FCUnit Projlib
This artifact must be a dependency in the test scope of any project where unit
testing must be enabled.

[FCUnit Projlib][fcunit-projlib-link]

[fcunit-projlib-link]: ./fcunit-projlib/index.html

#### FCUnit Java
This artifact is a dependency of the FCUnit Projlib. It will be a transitive
dependency of projects with unit testing enabled.

[FCUnit core Java classes][fcunit-core-link]

[fcunit-core-link]: ./fcunit-core/index.html
