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


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<xsl:stylesheet xml:base="stylesheet" version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:func="cone:functions" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">

	<xsl:output encoding="UTF-8" indent="yes" method="xml"/>
	
	<xsl:param name="model"/>
	
	<xsl:template match="/">
	
			<xsl:comment>RDF-Schema for cone models</xsl:comment>
			<rdf:RDF>
	
				<xsl:apply-templates select="/models/model[$model = '' or name = $model or aliases/alias = model]" mode="class">
					<xsl:with-param name="model-name" select="$model"/>
				</xsl:apply-templates>

				<xsl:apply-templates select="/models/model[$model = '' or name = $model or aliases/alias = model]/predicates//predicate[exists(predicate)]" mode="class"/>

			</rdf:RDF>

	</xsl:template>
	
	<xsl:template match="model" mode="class">
		<xsl:param name="model-name"/>
		
		<xsl:comment>Class definition <xsl:value-of select="concat('cone:', name)"/></xsl:comment>
		<rdfs:Class rdf:about="cone:{name}" rdfs:label="{name}" rdfs:comment="{description}"/>
				
		<xsl:apply-templates select="predicates/predicate">
			<xsl:with-param name="class-name" select="concat('cone:', name)"/>
		</xsl:apply-templates>
		
	</xsl:template>
	
	<xsl:template match="predicate" mode="class">
	
		<xsl:variable name="model-name" select="ancestor::node()[name() = 'model']/name"/>
		
		<xsl:variable name="class-name">
			<xsl:text>cone:</xsl:text>
			<xsl:value-of select="$model-name"/>
			<xsl:text>:</xsl:text>
			<xsl:if test="../../../name() = 'predicate'">
				<xsl:value-of select="func:extract-name(../../../@value)"/>
				<xsl:text>:</xsl:text>
			</xsl:if>
			<xsl:if test="../../name() = 'predicate'">
				<xsl:value-of select="func:extract-name(../../@value)"/>
				<xsl:text>:</xsl:text>
			</xsl:if>
			<xsl:if test="../name() = 'predicate'">
				<xsl:value-of select="func:extract-name(../@value)"/>
				<xsl:text>:</xsl:text>
			</xsl:if>
			<xsl:value-of select="func:extract-name(@value)"/>
		</xsl:variable>
	
		<xsl:comment>Class definition <xsl:value-of select="$class-name"/></xsl:comment>
		<rdfs:Class rdf:about="{$class-name}" rdfs:label="Enclosed class {$class-name}" rdfs:comment="{@description}"/>

		<xsl:apply-templates select="predicate">
			<xsl:with-param name="class-name" select="$class-name"/>
		</xsl:apply-templates>
		
	</xsl:template>
	
	<xsl:template match="predicate">
		<xsl:param name="class-name"/>
		
		<rdf:Property rdf:about="{@value}" rdfs:label="{@name}" rdfs:comment="{@description}">
			<rdfs:domain rdf:resource="{$class-name}"/>
			<xsl:choose>
				<xsl:when test="exists(@resourceModel)">
					<rdfs:range rdf:resource="cone:{@resourceModel}"/>
				</xsl:when>
				<xsl:when test="exists(predicate)">
					<rdfs:range rdf:resource="{$class-name}:{func:extract-name(@value)}"/>
				</xsl:when>
				<xsl:otherwise>
					<rdfs:range rdf:resource="http://www.w3.org/2000/01/rdf-schema#Literal"/>
				</xsl:otherwise>
			</xsl:choose>

		</rdf:Property>
	
	</xsl:template>

	<xsl:template match="*" mode="class"/>
	
	<xsl:function name="func:extract-name">
		<xsl:param name="url"/>
		
		<xsl:choose>
			<xsl:when test="contains($url, '/')">
				<xsl:value-of select="func:extract-name(substring-after($url, '/'))"></xsl:value-of>
			</xsl:when>
			<xsl:when test="contains($url, '#')">
				<xsl:value-of select="func:extract-name(substring-after($url, '#'))"></xsl:value-of>
			</xsl:when>
			<xsl:when test="contains($url, ' ')">
				<xsl:value-of select="func:extract-name(substring-after($url, ' '))"></xsl:value-of>
			</xsl:when>
			<xsl:when test="contains($url, ':')">
				<xsl:value-of select="func:extract-name(substring-after($url, ':'))"></xsl:value-of>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="upper-case($url)"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:function>
</xsl:stylesheet>