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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:fcun="http://fastconnect.fr/fcunit.xsd" xmlns:ns0="http://fastconnect.fr/fcunit.xsd" exclude-result-prefixes="ns0">
	
	<xsl:output method="xml" indent="yes" />
	
	<xsl:param name="ExportDirectory" /> 
	
	<!-- ce template s'applique à tous les éléments du namespace par défaut (simple recopie) -->
	<xsl:template match="node()|text()">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="node() | text()"/>
		</xsl:copy>
	</xsl:template>
	
	<!-- ce template s'applique à tous les éléments dont le namespace est "http://fastconnect.fr/fcunit.xsd"
	et leur donne le préfixe "fcun" (les éléments du fichier source ne sont pas forcément préfixés "ns0") -->
	
	<xsl:template match="ns0:*">
		<xsl:element name="fcun:{local-name()}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="node() | text()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="/fcun:test-suites-results">
		<xsl:variable name="result_filename" select="concat($ExportDirectory, '/results.xml')" />
		<!--<xsl:result-document href="{$result_filename}">-->
		<fcun:test-suite-results>
		<xsl:for-each select="/fcun:test-suites-results/fcun:test-suite-result">
			<xsl:variable name="test_index" select="position()"/>

			<xsl:variable name="filename" select="concat($ExportDirectory, '/', 'test', $test_index, '.xml')" />
			
			<!--
			<xsl:result-document href="{$filename}">
				<fcun:test-suite-results>
			-->
					<fcun:test-suite-result>
						<xsl:attribute name="name">
							<xsl:value-of select="./@name"/>
						</xsl:attribute>
						<xsl:attribute name="path">
							<xsl:value-of select="./@path"/>
						</xsl:attribute>
						<xsl:attribute name="datetime">
							<xsl:value-of select="./@datetime"/>
						</xsl:attribute>
					<xsl:apply-templates select="./*"/>
					</fcun:test-suite-result>
			<!--
				</fcun:test-suite-results>
			</xsl:result-document>
			-->
			
		</xsl:for-each>
		</fcun:test-suite-results>
		<!--</xsl:result-document>-->

	</xsl:template>

	<xsl:template match="fcun:test-suite-result/ns0:*">
		<xsl:element name="fcun:{local-name()}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="node() | text()"/>
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>
