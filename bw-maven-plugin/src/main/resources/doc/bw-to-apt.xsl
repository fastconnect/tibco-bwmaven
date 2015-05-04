<?xml version="1.0" encoding="UTF-8"?>
<!--

    (C) Copyright 2011-2015 FastConnect SAS
    (http://www.fastconnect.fr/) and others.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
							  xmlns:bwdoc="http://fastconnect.org/bw/doc/2014">
	<xsl:output method="text"/>
	
	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="bwdoc:Label">
		<xsl:value-of select="./bwdoc:description"/>
		<xsl:call-template name="newLines">
			<xsl:with-param name="numberOfLines" select="2"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="newLines">
		<xsl:param name="numberOfLines" select="1"/>
		<xsl:text>&#xa;</xsl:text>
		
		<xsl:if test="$numberOfLines &gt; 1">
			<xsl:call-template name="newLines">
				<xsl:with-param name="numberOfLines" select="$numberOfLines - 1"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- override default XSLT template -->
	<xsl:template match="text()|@*"/>
</xsl:stylesheet>