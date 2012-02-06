<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:oai="http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd"
	xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:imeji="http://imeji.mpdl.mpg.de/"
	xmlns:imeji-metadata="http://imeji.mpdl.mpg.de/metadata/"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
>

	<xsl:output omit-xml-declaration="yes" />
	
	<xsl:param name="type" select="'image'"/>
	
	<xsl:param name="metadataProfile" />
	
	<xsl:template match="/">		
		<xsl:if test="$type='image'">
			<xsl:call-template name="image" />			
		</xsl:if>
		<xsl:if test="$type='album'">
			<xsl:call-template name="album" />			
		</xsl:if>
		<xsl:if test="$type='collection'">
			<xsl:call-template name="collection" />			
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="image">		
			<xsl:for-each select="rdf:RDF/imeji:image">
				<xsl:call-template name="imageRecord" />
			</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="imageRecord">
		<record>
			<oai:header>
				<oai:identifier> <xsl:value-of select="@rdf:about"/> </oai:identifier> 
				<oai:datestamp><xsl:value-of select="current-dateTime()"/></oai:datestamp>
				<oai:setSpec> <xsl:value-of select="imeji:collection/@rdf:resource"/> </oai:setSpec>		
			</oai:header>
			
			<xsl:variable name="metadataProfile">
				<xsl:value-of select="imeji:metadataSet/imeji:profile/@rdf:resource"/>
			</xsl:variable>
			
			<xsl:variable name="title">
				<xsl:value-of select="imeji:metadataSet/imeji:profile/@rdf:resource"/>
			</xsl:variable>
			
			<oai:metadata>
				<oai_dc:dc>
					<dc:title>
						<xsl:if test="imeji:metadataSet/imeji:metadata/imeji-metadata:ns[@rdf:resource=concat($metadataProfile,'/Title')]">
							<xsl:value-of select="imeji:metadataSet/imeji:metadata/imeji-metadata:ns[@rdf:resource=concat($metadataProfile,'/Title')]/../imeji-metadata:text"/>
						</xsl:if>					
					</dc:title>
					<dc:creator><xsl:value-of select="imeji:properties/imeji:createdBy/@rdf:resource"/></dc:creator>
					<!--<dc:subject></dc:subject> -->
					<dc:description>
						<xsl:if test="imeji:metadataSet/imeji:metadata/imeji-metadata:ns/@rdf:resource = concat($metadataProfile,'/Description')">
							<xsl:value-of select="imeji:metadataSet/imeji:metadata/imeji-metadata:ns[@rdf:resource=concat($metadataProfile,'/Description')]/../imeji-metadata:text"/>
						</xsl:if>	
					</dc:description>
					<dc:date><xsl:value-of select="imeji:properties/imeji:creationDate"/></dc:date>
					<dc:type>image</dc:type>
					<dc:identifier><xsl:value-of select="@rdf:about"/></dc:identifier>
					<dc:rights>
						<xsl:if test="imeji:metadataSet/imeji:metadata/imeji-metadata:license != ''">
							<xsl:value-of select="imeji:metadataSet/imeji:metadata/imeji-metadata:license"/>
						</xsl:if>					
					</dc:rights>
					<dc:coverage>
						<xsl:if test="imeji:metadataSet/imeji:metadata/imeji-metadata:latitude != ''">
							<xsl:value-of select="imeji:metadataSet/imeji:metadata/imeji-metadata:name"/>
						</xsl:if>					
					</dc:coverage>
					<dc:source>
						<xsl:value-of select="@rdf:about"/>					
					</dc:source>
				</oai_dc:dc>
			</oai:metadata>
		</record>
	</xsl:template>
	
	<xsl:template name="collection">
			<record>
				<oai:header>
					<oai:identifier> <xsl:value-of select="rdf:RDF/imeji:collection/@rdf:about"/> </oai:identifier> 
					<oai:datestamp><xsl:value-of select="current-dateTime()"/></oai:datestamp>	
				</oai:header>			
				
				<oai:metadata>
					<oai_dc:dc>
						<dc:title><xsl:value-of select="rdf:RDF/imeji:collection/imeji-metadata:metadata/dcterms:title"/></dc:title>
						<dc:creator><xsl:value-of select="rdf:RDF/imeji:collection/imeji:properties/imeji:createdBy/@rdf:resource"/></dc:creator>
						<!--<dc:subject></dc:subject> -->
						<dc:description><xsl:value-of select="rdf:RDF/imeji:collection/imeji-metadata:metadata/dcterms:description"/></dc:description>
						<dc:date><xsl:value-of select="rdf:RDF/imeji:collection/imeji:properties/imeji:creationDate"/></dc:date>
						<dc:type>collection</dc:type>
						<dc:identifier><xsl:value-of select="rdf:RDF/imeji:collection/@rdf:about"/></dc:identifier>
					</oai_dc:dc>
				</oai:metadata>
			</record>
	</xsl:template>
	
	<xsl:template name="album">
			<record>
				<oai:header>
					<oai:identifier> <xsl:value-of select="rdf:RDF/imeji:album/@rdf:about"/> </oai:identifier> 
					<oai:datestamp><xsl:value-of select="current-dateTime()"/></oai:datestamp>	
				</oai:header>			
				
				<oai:metadata>
					<oai_dc:dc>
						<dc:title><xsl:value-of select="rdf:RDF/imeji:album/imeji-metadata:metadata/dcterms:title"/></dc:title>
						<dc:creator><xsl:value-of select="rdf:RDF/imeji:album/imeji:properties/imeji:createdBy/@rdf:resource"/></dc:creator>
						<!--<dc:subject></dc:subject> -->
						<dc:description><xsl:value-of select="rdf:RDF/imeji:album/imeji-metadata:metadata/dcterms:description"/></dc:description>
						<dc:date><xsl:value-of select="rdf:RDF/imeji:album/imeji:properties/imeji:creationDate"/></dc:date>
						<dc:type>album</dc:type>
						<dc:identifier><xsl:value-of select="rdf:RDF/imeji:album/@rdf:about"/></dc:identifier>
					</oai_dc:dc>
				</oai:metadata>
			</record>
	</xsl:template>

</xsl:stylesheet>