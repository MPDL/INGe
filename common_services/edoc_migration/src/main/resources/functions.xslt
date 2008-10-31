<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:escidoc="urn:escidoc:functions" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">

	<xsl:function name="escidoc:ou-name">
		<xsl:param name="name"/>

		<xsl:value-of select="$name"/>

		<xsl:if test="$organizational-units//ou[@name = $name]/../@name != $name">
			<xsl:text>, </xsl:text>
			<xsl:value-of select="escidoc:ou-name($organizational-units//ou[@name = $name]/../@name)"/>
		</xsl:if>
		
	</xsl:function>
	
	<xsl:function name="escidoc:ou-id">
		<xsl:param name="name"/>
		
		<xsl:choose>
			<xsl:when test="$organizational-units//ou[@name = $name]">
				<xsl:value-of select="$organizational-units//ou[@name = $name]/@id"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$organizational-units//ou[@name = 'external']/@id"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:function>
	
</xsl:stylesheet>