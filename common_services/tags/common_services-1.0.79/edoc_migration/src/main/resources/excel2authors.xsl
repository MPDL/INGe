<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
	
	<xsl:output encoding="UTF-8" method="xml"/>

	<xsl:template match="/">
		<authors>
			<xsl:for-each select="/root/author">
				<xsl:variable name="pos" select="position()"/>
				<xsl:variable name="login" select="login"/>
				<xsl:choose>
					<xsl:when test="familyname != ''">
						<xsl:call-template name="process">
							<xsl:with-param name="pos" select="$pos"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
		</authors>
	</xsl:template>
	
	<xsl:template name="process">
		<xsl:param name="pos"/>

		<xsl:variable name="ou" select="ou"/>
		
		<author id="urn:cone:persons{$pos}">
			<familyname><xsl:value-of select="familyname"/></familyname>
			<givenname><xsl:value-of select="givenname"/></givenname>
			<aliases>
				<alias>
					<familyname><xsl:value-of select="familyname"/></familyname>
					<givenname><xsl:value-of select="givenname"/></givenname>
				</alias>
			</aliases>
			<departments>
				<department><xsl:value-of select="$ou"/></department>
				<xsl:call-template name="next-department">
					<xsl:with-param name="pos" select="$pos + 1"/>
				</xsl:call-template>
			</departments>
			<cone display="{cone = 'y'}"/>
		</author>
	</xsl:template>
	
	<xsl:template name="next-department">
		<xsl:param name="pos"/>
		<xsl:if test="/root/author[position() = $pos and familyname = '']">
			<department><xsl:value-of select="/root/author[position() = $pos]/ou"/></department>
			<xsl:call-template name="next-department">
				<xsl:with-param name="pos" select="$pos + 1"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
