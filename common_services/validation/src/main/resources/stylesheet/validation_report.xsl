<?xml version="1.0" encoding="UTF-8"?>
<!--
 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.de/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<?xar XSLT?>
<xsl:stylesheet xml:base="stylesheet" version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:axsl="http://www.w3.org/1999/XSL/TransformAlias" xmlns:sch="http://www.ascc.net/xml/schematron" xmlns:iso="http://purl.oclc.org/dsdl/schematron" xmlns:exsl="http://exslt.org/common" xmlns:msxsl="urn:schemas-microsoft-com:xslt" extension-element-prefixes="exsl msxsl">

	<xsl:import href="iso-skeleton.xsl"/>
	<xsl:import href="iso_schematron_skeleton_for_saxon.xsl"/>
	
	<xsl:namespace-alias stylesheet-prefix="axsl" result-prefix="xsl"/>
	
	<xsl:output method="xml" omit-xml-declaration="no" standalone="yes" indent="yes"/>
	
	<xsl:param name="diagnose">yes</xsl:param>
	
	<xsl:param name="phase">
		<xsl:choose>
			<xsl:when test="//sch:schema/@defaultPhase">
				<xsl:value-of select="//sch:schema/@defaultPhase"/>
			</xsl:when>
			<xsl:when test="//iso:schema/@defaultPhase">
				<xsl:value-of select="//iso:schema/@defaultPhase"/>
			</xsl:when>
			<xsl:otherwise>#ALL</xsl:otherwise>
		</xsl:choose>
	</xsl:param>

	<xsl:param name="allow-foreign">false</xsl:param>

	<xsl:param name="message-newline">true</xsl:param>

	<xsl:param name="optimize"/>

	<!-- e.g. saxon file.xml file.xsl "sch.exslt.imports=.../string.xsl;.../math.xsl" -->
	<xsl:param name="sch.exslt.imports"/>

	<!-- Simple namespace check -->
	<xsl:template match="/">
		<xsl:if test="//sch:* and //iso:*">
			<xsl:message>Schema error: Schematron elements in old and new namespaces found</xsl:message>
		</xsl:if>

		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template name="process-diagnostic">
		<xsl:param name="id"/>

		<!-- "Rich" parameters -->
		<xsl:param name="fpi"/>
		<xsl:param name="icon"/>
		<xsl:param name="lang"/>
		<xsl:param name="see"/>
		<xsl:param name="space"/>
		
	    <!-- We generate too much whitespace rather than risking concatenation -->
		<axsl:text></axsl:text>
		<xsl:apply-templates mode="text"/>
		<axsl:text></axsl:text>
	</xsl:template>
	
	<xsl:template name="process-dir">
		<xsl:param name="value"/>

	    <!-- We generate too much whitespace rather than risking concatenation -->
		<axsl:text></axsl:text>
		<xsl:apply-templates mode="inline-text"/>
		<axsl:text></axsl:text>
	</xsl:template>
	
	<xsl:template name="process-emph"> 
	    <!-- We generate too much whitespace rather than risking concatenation -->
		<axsl:text></axsl:text>
		<xsl:apply-templates mode="inline-text"/>
		<axsl:text></axsl:text>
	</xsl:template>
	
	<xsl:template name="process-name">
		<xsl:param name="name"/>
		
		<!-- We generate too much whitespace rather than risking concatenation -->
		<axsl:text></axsl:text>
		<axsl:value-of select="{$name}"/>
		<axsl:text></axsl:text>

	</xsl:template>
	
	<xsl:template name="process-ns">
	<!-- Note that process-ns is for reporting. The sch:ns elements are 
	     independently used in the sch:schema template to provide namespace bindings -->
		<xsl:param name="prefix"/>
		<xsl:param name="uri"/>
	</xsl:template>
	
	<xsl:template name="process-p">
		<xsl:param name="id"/>
		<xsl:param name="class"/>
		<xsl:param name="icon"/>
		<xsl:param name="lang"/>
	</xsl:template>

	<!-- <xsl:template name="process-pattern">
		<xsl:param name="id" />
		<xsl:param name="name" />
		<xsl:param name="is-a" />

		<xsl:param name="fpi" />
		<xsl:param name="icon" />
		<xsl:param name="lang" />
		<xsl:param name="see" />
		<xsl:param name="space" />
      </xsl:template> -->

	
	<xsl:template name="process-rule">
		<xsl:param name="context"/>
		
		<xsl:param name="id"/>
		<xsl:param name="flag"/>

           	<!-- "Linkable" parameters -->
		<xsl:param name="role"/>
		<xsl:param name="subject"/>
  
		<!-- "Rich" parameters -->
		<xsl:param name="fpi"/>
		<xsl:param name="icon"/>
		<xsl:param name="lang"/>
		<xsl:param name="see"/>
		<xsl:param name="space"/>
	</xsl:template>
	
	<xsl:template name="process-span">
		<xsl:param name="class"/>

	    <!-- We generate too much whitespace rather than risking concatenation -->
		<axsl:text></axsl:text>
		<xsl:apply-templates mode="inline-text"/>
		<axsl:text></axsl:text>
	</xsl:template>
	
	<xsl:template name="process-title">
		<xsl:param name="class"/>
		<xsl:call-template name="process-p">
			<xsl:with-param name="class">title</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="process-value-of">
		<xsl:param name="select"/>
		
	    <!-- We generate too much whitespace rather than risking concatenation -->
		<axsl:text></axsl:text>
		<axsl:value-of select="{$select}"/>
		<axsl:text></axsl:text>
	</xsl:template>

	<!-- default output action: the simplest customization is to just override this -->
	<!-- <xsl:template name="process-message">
		<xsl:param name="pattern" />
            <xsl:param name="role" />

		<xsl:apply-templates mode="text"/>	
		 <xsl:if test=" $message-newline = 'true'" >
			<axsl:value-of  select="string('&#10;')"/>
		</xsl:if>
		
	</xsl:template> -->
	<!-- iso-skeleton end -->

	<!-- Root node -->
	<xsl:template name="process-root">
		<xsl:param name="title"/>
		<xsl:param name="contents"/>
		<xsl:param name="id"/>
		<xsl:param name="version"/>
		<xsl:param name="schemaVersion"/>
		<xsl:param name="queryBinding"/>
		<xsl:param name="fpi"/>
		<xsl:param name="icon"/>
		<xsl:param name="lang"/>
		<xsl:param name="see"/>
		<xsl:param name="space"/>
		
		<validation-report>
			<xsl:attribute name="phase">
				<xsl:value-of select="$phase"/>
			</xsl:attribute>
			<xsl:value-of select="$title"/>
			<xsl:text>&#10;</xsl:text>
			<xsl:copy-of select="$contents"/>
		</validation-report>
	</xsl:template>
	
	<xsl:template name="process-pattern">
		<xsl:param name="id"/>
		<xsl:param name="name"/>
		<xsl:param name="is-a"/>
		<xsl:param name="fpi"/>
		<xsl:param name="icon"/>
		<xsl:param name="lang"/>
		<xsl:param name="see"/>
		<xsl:param name="space"/>
		
		<xsl:text>&#10;</xsl:text>
		<pattern>
			<xsl:value-of select="$name"/>
		</pattern>
	</xsl:template>
	
	<xsl:template name="process-assert">
		<xsl:param name="test"/>
		<xsl:param name="diagnostics"/>
		<xsl:param name="id"/>
		<xsl:param name="flag"/>
		<xsl:param name="role"/>
		<xsl:param name="subject"/>
		<xsl:param name="fpi"/>
		<xsl:param name="icon"/>
		<xsl:param name="lang"/>
		<xsl:param name="see"/>
		<xsl:param name="space"/>
		
		<xsl:text>&#10;</xsl:text>
		<failure infolevel="restrictive">
			<message>
				<xsl:call-template name="process-message">
					<xsl:with-param name="pattern" select="$test"/>
					<xsl:with-param name="role" select="$role"/>
				</xsl:call-template>
			</message>
			<xsl:if test="$diagnose = 'yes'">
				<diagnostics>
					<xsl:call-template name="diagnosticsSplit">
						<xsl:with-param name="str" select="$diagnostics"/>
					</xsl:call-template>
				</diagnostics>
			</xsl:if>
		</failure>
	</xsl:template>
	
	<xsl:template name="process-report">
		<xsl:param name="test"/>
		<xsl:param name="diagnostics"/>
		<xsl:param name="id"/>
		<xsl:param name="flag"/>
		<xsl:param name="role"/>
		<xsl:param name="subject"/>
		<xsl:param name="fpi"/>
		<xsl:param name="icon"/>
		<xsl:param name="lang"/>
		<xsl:param name="see"/>
		<xsl:param name="space"/>
		
		<xsl:text>&#10;</xsl:text>
		<report infolevel="informative">
			<message>
				<xsl:call-template name="process-message">
					<xsl:with-param name="pattern" select="$test"/>
					<xsl:with-param name="role" select="$role"/>
				</xsl:call-template>
			</message>
			<xsl:if test="$diagnose = 'yes'">
				<xsl:call-template name="diagnosticsSplit">
					<xsl:with-param name="str" select="$diagnostics"/>
				</xsl:call-template>
			</xsl:if>
		</report>
	</xsl:template>

	
	<xsl:template name="process-message">
		<xsl:param name="pattern"/>
		<xsl:param name="role"/>
		<xsl:if test="$role">
			<xsl:text> (</xsl:text>
			<xsl:value-of select="$role"/>
			<xsl:text>)</xsl:text>
		</xsl:if>
		<name>
			<xsl:apply-templates mode="text"/>
		</name>
		<path>
			<axsl:apply-templates mode="schematron-get-full-path" select="."/>
		</path>
		<element>
			<axsl:value-of select="name()"/>
			<!-- Removed attributes -->
			<!-- <axsl:for-each select="@*">
				<attribute>
					<axsl:value-of select="name()"/>="<axsl:value-of select="."/>"</attribute>
			</axsl:for-each> -->
		</element>
	</xsl:template>
</xsl:stylesheet>