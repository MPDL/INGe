<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:sch="http://www.ascc.net/xml/schematron" version="1.0" xmlns:cs="http://www.escidoc.de/citationstyle" cs:dummy-for-xmlns="">
	<xsl:output method="text"/>
	<xsl:template match="*|@*" mode="schematron-get-full-path">
		<xsl:apply-templates select="parent::*" mode="schematron-get-full-path"/>
		<xsl:text>/</xsl:text>
		<xsl:if test="count(. | ../@*) = count(../@*)">@</xsl:if>
		<xsl:choose>
			<xsl:when test="not(namespace-uri(.)='') and          not(contains(name(.), ':'))">
				<xsl:variable name="sq">'</xsl:variable>
				<xsl:value-of select="concat('*:', local-name(.),             '[namespace-uri(.)=', $sq, namespace-uri(.), $sq, ']')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="name()"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>[</xsl:text>
		<xsl:value-of select="1+count(preceding-sibling::*[name()=name(current())])"/>
		<xsl:text>]</xsl:text>
	</xsl:template>
	<xsl:template match="/" mode="generate-id-from-path"/>
	<xsl:template match="text()" mode="generate-id-from-path">
		<xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
		<xsl:value-of select="concat('.text-', 1+count(preceding-sibling::text()), '-')"/>
	</xsl:template>
	<xsl:template match="comment()" mode="generate-id-from-path">
		<xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
		<xsl:value-of select="concat('.comment-', 1+count(preceding-sibling::comment()), '-')"/>
	</xsl:template>
	<xsl:template match="processing-instruction()" mode="generate-id-from-path">
		<xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
		<xsl:value-of select="concat('.processing-instruction-',       1+count(preceding-sibling::processing-instruction()), '-')"/>
	</xsl:template>
	<xsl:template match="@*" mode="generate-id-from-path">
		<xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
		<xsl:value-of select="concat('.@', name())"/>
	</xsl:template>
	<xsl:template match="*" mode="generate-id-from-path" priority="-0.5">
		<xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
		<xsl:text>.</xsl:text>
		<xsl:choose>
			<xsl:when test="count(. | ../namespace::*) = count(../namespace::*)">
				<xsl:value-of select="concat('.namespace::-',1+count(namespace::*),'-')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat('.',name(),'-',             1+count(preceding-sibling::*[name()=name(current())]),'-')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="/">


>From pattern "Check structure": <xsl:apply-templates select="/" mode="M1"/></xsl:template>
	<xsl:template match="cs:cs-layout-definition/cs:elements/cs:layout-element" priority="4000" mode="M1">
		<xsl:if test="not(@repeatable) and cs:position/cs:parameters/cs:internal-delimiter">
      Report: "The internal-delimiter parameter must not be set in case of : layout-element/parameters."  at 
         <xsl:apply-templates mode="schematron-get-full-path" select="."/> 
        &lt;<xsl:value-of select="name()"/><xsl:for-each select="@*"><xsl:value-of select="' '"/><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:for-each>&gt;...&lt;/&gt;</xsl:if>
		<xsl:if test="not(@repeatable) and  cs:position/cs:parameters/cs:max-count">
      Report: "The max-count parameter must not be set in case of : layout-element/parameters."  at 
         <xsl:apply-templates mode="schematron-get-full-path" select="."/> 
        &lt;<xsl:value-of select="name()"/><xsl:for-each select="@*"><xsl:value-of select="' '"/><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:for-each>&gt;...&lt;/&gt;</xsl:if>
		<xsl:if test="not(@repeatable) and cs:position/cs:parameters/cs:max-count-ends-with">
      Report: "The max-count-ends-with parameter must not be set in case of : layout-element/parameters."  at 
         <xsl:apply-templates mode="schematron-get-full-path" select="."/> 
        &lt;<xsl:value-of select="name()"/><xsl:for-each select="@*"><xsl:value-of select="' '"/><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:for-each>&gt;...&lt;/&gt;</xsl:if>
		<xsl:apply-templates mode="M1"/>
	</xsl:template>
	<xsl:template match="cs:layout-element/cs:elements/cs:layout-element" priority="3999" mode="M1">
		<xsl:if test="not(@repeatable) and       cs:position/cs:parameters/cs:internal-delimiter">
      Report: "The internal-delimiter parameter must not be set in case of : layout/element/element/layout-element/parameters."  at 
         <xsl:apply-templates mode="schematron-get-full-path" select="."/> 
        &lt;<xsl:value-of select="name()"/><xsl:for-each select="@*"><xsl:value-of select="' '"/><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:for-each>&gt;...&lt;/&gt;</xsl:if>
		<xsl:if test="not(@repeatable) and  cs:position/cs:parameters/cs:max-count">
      Report: "The max-count parameter must not be set in case of : layout-element/parameters."  at 
         <xsl:apply-templates mode="schematron-get-full-path" select="."/> 
        &lt;<xsl:value-of select="name()"/><xsl:for-each select="@*"><xsl:value-of select="' '"/><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:for-each>&gt;...&lt;/&gt;</xsl:if>
		<xsl:if test="not(@repeatable) and cs:position/cs:parameters/cs:max-count-ends-with">
      Report: "The max-count-ends-with parameter must not be set in case of : layout-element/parameters."  at 
         <xsl:apply-templates mode="schematron-get-full-path" select="."/> 
        &lt;<xsl:value-of select="name()"/><xsl:for-each select="@*"><xsl:value-of select="' '"/><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:for-each>&gt;...&lt;/&gt;</xsl:if>
		<xsl:if test="(@repeatable = 'yes') and cs:position/cs:parameters/cs:validIf">
      Report: "The validIf parameter must not be set in case of: layout-element/parameters/layout-element/parameters Repeatable. NO SENCE IN SCRIPLET!"  at 
         <xsl:apply-templates mode="schematron-get-full-path" select="."/> 
        &lt;<xsl:value-of select="name()"/><xsl:for-each select="@*"><xsl:value-of select="' '"/><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:for-each>&gt;...&lt;/&gt;</xsl:if>
		<xsl:if test="(@repeatable = 'yes') and cs:position/cs:parameters/cs:delimiter">
      Report: "The delimiter parameter must not be set in case of :layout/element/element/layout-element/parameters Repeatable. NO SENCE IN SCRIPLET!"  at 
         <xsl:apply-templates mode="schematron-get-full-path" select="."/> 
        &lt;<xsl:value-of select="name()"/><xsl:for-each select="@*"><xsl:value-of select="' '"/><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:for-each>&gt;...&lt;/&gt;</xsl:if>
		<xsl:if test="(@repeatable = 'yes') and cs:position/cs:parameters/cs:internal-delimiter">
      Report: "The internal-delimiter parameter must not be set in case of :layout/element/element/layout-element/parameters Repeatable."  at 
         <xsl:apply-templates mode="schematron-get-full-path" select="."/> 
        &lt;<xsl:value-of select="name()"/><xsl:for-each select="@*"><xsl:value-of select="' '"/><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:for-each>&gt;...&lt;/&gt;</xsl:if>
		<xsl:if test="(@repeatable = 'yes') and cs:position/cs:parameters/cs:font-style">
      Report: "The font-style parameter must not be set in case of :layout/element/element/layout-element/parameters Repeatable. NO SENCE IN SCRIPLET!"  at 
         <xsl:apply-templates mode="schematron-get-full-path" select="."/> 
        &lt;<xsl:value-of select="name()"/><xsl:for-each select="@*"><xsl:value-of select="' '"/><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:for-each>&gt;...&lt;/&gt;</xsl:if>
		<xsl:if test="(@repeatable = 'yes') and cs:position/cs:parameters/cs:max-count">
      Report: "The max-count parameter must not be set in case of :layout/element/element/layout-element/parameters Repeatable. NO SENCE IN SCRIPLET!"  at 
         <xsl:apply-templates mode="schematron-get-full-path" select="."/> 
        &lt;<xsl:value-of select="name()"/><xsl:for-each select="@*"><xsl:value-of select="' '"/><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:for-each>&gt;...&lt;/&gt;</xsl:if>
		<xsl:if test="(@repeatable = 'yes') and cs:position/cs:parameters/cs:max-count-ends-with">
      Report: "The max-count-ends-with parameter must not be set in case of :layout/element/element/layout-element/parameters Repeatable. NO SENCE IN SCRIPLET!"  at 
         <xsl:apply-templates mode="schematron-get-full-path" select="."/> 
        &lt;<xsl:value-of select="name()"/><xsl:for-each select="@*"><xsl:value-of select="' '"/><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:for-each>&gt;...&lt;/&gt;</xsl:if>
		<xsl:apply-templates mode="M1"/>
	</xsl:template>
	<xsl:template match="cs:cs-layout-definition" priority="3998" mode="M1">
		<xsl:if test="not(@repeatable) and cs:parameters/cs:internal-delimiter">
      Report: "The internal-delimiter parameter must not be set in case of :layout/element/element/layout-element/parameters."  at 
         <xsl:apply-templates mode="schematron-get-full-path" select="."/> 
        &lt;<xsl:value-of select="name()"/><xsl:for-each select="@*"><xsl:value-of select="' '"/><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:for-each>&gt;...&lt;/&gt;</xsl:if>
		<xsl:if test="not(@repeatable) and  cs:parameters/cs:max-count">
      Report: "The max-count parameter must not be set in case of : layout-element/parameters."  at 
         <xsl:apply-templates mode="schematron-get-full-path" select="."/> 
        &lt;<xsl:value-of select="name()"/><xsl:for-each select="@*"><xsl:value-of select="' '"/><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:for-each>&gt;...&lt;/&gt;</xsl:if>
		<xsl:if test="not(@repeatable) and cs:parameters/cs:max-count-ends-with">
      Report: "The max-count-ends-with parameter must not be set in case of : layout-element/parameters."  at 
         <xsl:apply-templates mode="schematron-get-full-path" select="."/> 
        &lt;<xsl:value-of select="name()"/><xsl:for-each select="@*"><xsl:value-of select="' '"/><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:for-each>&gt;...&lt;/&gt;</xsl:if>
		<xsl:apply-templates mode="M1"/>
	</xsl:template>
	<xsl:template match="text()" priority="-1" mode="M1"/>
	<xsl:template match="text()" priority="-1"/>
</xsl:stylesheet>