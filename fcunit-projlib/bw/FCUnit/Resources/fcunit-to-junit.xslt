<?xml version = "1.0" encoding = "UTF-8"?>
<!--

        Copyright (C) 2011 FastConnect SAS http://www.fastconnect.fr/

        This file is part of FCUnit

        FCUnit is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        FCUnit is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.

-->
<xsl:stylesheet exclude-result-prefixes="xsl fcu SOAP-ENV"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:xs="http://www.w3.org/TR/xmlschema-2/"
xmlns:fcu="http://fastconnect.fr/fcunit.xsd"
xmlns:fcue="http://fastconnect.fr/fcunit-errors.xsd"
xmlns:fcfn="http://fastconnect.fr/fcunit_helpers"
xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
  version="2.0">
    <xsl:output omit-xml-declaration="no" indent="yes" cdata-section-elements="message"/>
    <xsl:param name="properties"/>
    <xsl:param name="hostname"/>
    
    <xsl:template match="fcu:test-suite-results" >
        <testsuites>
            <xsl:apply-templates select="fcu:test-suite-result//fcu:test-case-result"/>
        </testsuites>
    </xsl:template>
    
    <xsl:template match="fcu:test-case-result">
        <testsuite>
            <xsl:attribute name="name">
                <xsl:value-of select="@name"/>
            </xsl:attribute>
            <xsl:attribute name="timestamp">
                <xsl:value-of select="fcfn:transform-dateTime(@datetime)"/>
            </xsl:attribute>
            <xsl:attribute name="hostname"><xsl:value-of select="$hostname"/></xsl:attribute>
            <xsl:attribute name="tests">
                <xsl:value-of select="count(fcu:test-result)"/>
            </xsl:attribute>
            <xsl:attribute name="failures">
                <xsl:value-of select="count(fcu:test-result/fcu:test-failure)"/>
            </xsl:attribute>
            <xsl:attribute name="errors">
                <xsl:value-of select="0"/>
            </xsl:attribute>
            <xsl:attribute name="time">
                <xsl:value-of select="(sum(fcu:test-result/fcu:test-success/fcu:duration/fcu:test) + sum(fcu:test-result/fcu:test-success/fcu:duration/fcu:fixture)) * 0.001"/>
            </xsl:attribute>
            <xsl:attribute name="package">
                <xsl:value-of select="replace(@path, '(.*)/[^/]+', '$1')"/>
            </xsl:attribute>
            <xsl:attribute name="id">
                <xsl:value-of select="position()-1"/>
            </xsl:attribute>
            <!--<properties/>-->
            <xsl:apply-templates select="fcu:test-result"/>
            <system-out/>
            <system-err/>
        </testsuite>
    </xsl:template>

    <xsl:template match="fcu:test-result">
        <testcase>
            <xsl:attribute name="classname">
                <xsl:value-of select="replace(../@path, '/', '.')"/>
            </xsl:attribute>
            <xsl:attribute name="name">
                <xsl:value-of select="@name"/>
            </xsl:attribute>
            <xsl:attribute name="time">
                <xsl:choose>
                    <xsl:when test="fcu:test-success">
                        <xsl:value-of select="(fcu:test-success/fcu:duration/fcu:test + fcu:test-success/fcu:duration/fcu:fixture) * 0.001"/>
                    </xsl:when>
                    <xsl:otherwise>0</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:if test="fcu:test-failure">
                <error>
                    <xsl:attribute name="message">
                        <xsl:value-of select="fcu:test-failure/fcu:error-report/fcu:Msg"/>
                    </xsl:attribute>
                    <xsl:attribute name="type">
                        <xsl:value-of select="fcu:test-failure/fcu:error-report/fcu:MsgCode"/>
                    </xsl:attribute>
                    
                    <xsl:if test="fcu:test-failure/fcu:error-report/fcu:Data/fcue:assert-xml-equal-error/fcue:error-fields/fcue:field">
                    <xsl:text>
ERROR FIELDS:</xsl:text>
					</xsl:if>
                    <xsl:for-each select="fcu:test-failure/fcu:error-report/fcu:Data/fcue:assert-xml-equal-error/fcue:error-fields/fcue:field">
                        <xsl:text>
</xsl:text>
                        <xsl:text> -> Field #</xsl:text><xsl:value-of select="position()"/><xsl:text>
</xsl:text>
                        <xsl:text>    * XPath location : </xsl:text><xsl:value-of select="./fcue:xpath"/><xsl:text>
</xsl:text>
                        <xsl:text>    * Expected value : </xsl:text><xsl:value-of select="./fcue:expected"/><xsl:text>
</xsl:text>
                        <xsl:text>    * Actual value : </xsl:text><xsl:value-of select="./fcue:actual"/><xsl:text>
</xsl:text>                        
                    </xsl:for-each>
                    
                    <xsl:if test="fcu:test-failure/fcu:error-report/fcu:Data/fcue:assert-xml-equal-error/fcue:ignored-fields/fcue:field">
                    <xsl:text>
IGNORED FIELDS:</xsl:text>
					</xsl:if>
                    <xsl:for-each select="fcu:test-failure/fcu:error-report/fcu:Data/fcue:assert-xml-equal-error/fcue:ignored-fields/fcue:field">
                        <xsl:text>
</xsl:text>
                        <xsl:text> -> Field #</xsl:text><xsl:value-of select="position()"/><xsl:text>
</xsl:text>
                        <xsl:text>    * XPath location : </xsl:text><xsl:value-of select="./fcue:xpath"/><xsl:text>
</xsl:text>                  
                    </xsl:for-each>
<!--
ERROR DATA:</xsl:text>
                        <xsl:call-template name="render">
                            <xsl:with-param name="element" select="fcu:test-failure/fcu:error-report/fcu:Data/*"/>
                            <xsl:with-param name="indent" select="'    '"/>
                        </xsl:call-template>
-->
                </error>
            </xsl:if>   
        </testcase>
    </xsl:template>

    <xsl:template name="render">
        <xsl:param name="element"/>
        <xsl:param name="indent"/>
        <xsl:text>
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>&lt;</xsl:text>
        <xsl:value-of select="name($element)"/>
        <xsl:for-each select="$element/@*">
            <xsl:text> </xsl:text>
            <xsl:value-of select="name()"/>
            <xsl:text>="</xsl:text>
            <xsl:value-of select="."/>
            <xsl:text> "</xsl:text>
        </xsl:for-each>
        <xsl:text>&gt;</xsl:text>
        <xsl:for-each select="$element/node()|$element/text()">
            <xsl:choose>
                <xsl:when test="node()">
                    <xsl:call-template name="render">
                        <xsl:with-param name="element" select="."/>
                        <xsl:with-param name="indent" select="concat($indent,'    ')"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
        <xsl:if test="not($element/text())">
            <xsl:text>
</xsl:text>
            <xsl:value-of select="$indent"/>
        </xsl:if>
        <xsl:text>&lt;/</xsl:text>
            <xsl:value-of select="name($element)"/>
        <xsl:text>&gt;</xsl:text>
    </xsl:template>
    
    <xsl:function name="fcfn:transform-dateTime">
        <!-- transforms dateTime into Apache Ant's JUnit dateTime format -->
        <xsl:param name="dateTime" />
        <xsl:variable name="no-time-zone" select="adjust-dateTime-to-timezone($dateTime, ())"/>
        <xsl:value-of select="format-dateTime($no-time-zone, '[Y0001]-[M01]-[D01]T[H01]:[m01]:[s01]')"/>
    </xsl:function>
    
</xsl:stylesheet>
