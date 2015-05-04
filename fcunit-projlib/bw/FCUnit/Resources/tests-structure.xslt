<?xml version="1.0" encoding="UTF-8"?>
<!--
 |     Copyright (C) 2011 FastConnect SAS http://www.fastconnect.fr/
 |
 |     This file is part of FCUnit
 |
 |     FCUnit is free software: you can redistribute it and/or modify
 |     it under the terms of the GNU General Public License as published by
 |     the Free Software Foundation, either version 3 of the License, or
 |     (at your option) any later version.
 |
 |     FCUnit is distributed in the hope that it will be useful,
 |     but WITHOUT ANY WARRANTY; without even the implied warranty of
 |     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |     GNU General Public License for more details.
 |
 |     You should have received a copy of the GNU General Public License
 |     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 -->
<xsl:stylesheet exclude-result-prefixes="ns_0 xsl"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns="http://fastconnect.fr/fcunit.xsd"
xmlns:ns_0 = "www.tibco.com/plugin/java/xmlSchema/fr.fastconnect.tibco.businessworks.fcunit.TestSuite"
  version="2.0">
	<xsl:output omit-xml-declaration="no"/>
	<xsl:template match="/ns_0:TestSuite">
		<test-suites>
			<xsl:apply-templates/>
		</test-suites>
	</xsl:template>

	<xsl:template match="ns_0:Suites">
		<test-suite name="{ns_0:Name}" path="{ns_0:Path}">
			<xsl:apply-templates select="ns_0:Suites"/>
			<xsl:apply-templates select="ns_0:Cases"/>
		</test-suite>
	</xsl:template>

	<xsl:template match="ns_0:Cases">
		<test-case name="{ns_0:Name}" path="{ns_0:Path}">
			<xsl:apply-templates select="ns_0:Tests"/>
		</test-case>
	</xsl:template>

	<xsl:template match="ns_0:Tests">
		<test name="{ns_0:Name}" path="{ns_0:Path}">
		</test>
	</xsl:template>

</xsl:stylesheet>