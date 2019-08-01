<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:escidocItemList="http://www.escidoc.de/schemas/itemlist/0.10"
   xmlns:mdp="http://purl.org/escidoc/metadata/profiles/0.1/publication"
   xmlns:publ="http://purl.org/escidoc/metadata/terms/0.1/"
   xmlns:person="http://purl.org/escidoc/metadata/profiles/0.1/person"
   xmlns:ec="http://www.escidoc.de/schemas/components/0.9"
   xmlns:ei="http://www.escidoc.de/schemas/item/0.10"
   xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.5" 
   xmlns:ou="http://purl.org/escidoc/metadata/profiles/0.1/organization"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   xmlns:fn="http://www.w3.org/2005/xpath-functions"
   xmlns:xlink="http://www.w3.org/1999/xlink"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:dcterms="http://purl.org/dc/terms/"
   xmlns:srel="http://escidoc.de/core/01/structural-relations/"
   xmlns:prop="http://escidoc.de/core/01/properties/"
   xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/"
   xmlns:file="http://purl.org/escidoc/metadata/profiles/0.1/file"
   xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source"
   xmlns:event="http://purl.org/escidoc/metadata/profiles/0.1/event"
   xmlns:gfz="http://www.gfz-potsdam.de/ns"
   xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"   
   xmlns:escidoc="urn:escidoc:functions">

	<xsl:import href="../../vocabulary-mappings.xsl"/>   
   
	<xsl:param name="external_organization_id" />
	<xsl:param name="context" select="'dummy-context'"/>	
	<xsl:param name="content-model" select="'dummy-content-model'"/>
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	
	<xsl:template match="/">
		<xsl:apply-templates select="doi_records"/>
	</xsl:template>
	
	<xsl:template match="doi_records">
		<xsl:choose>
			<xsl:when test="count(doi_records/doi_record)>1">
				<xsl:element name="escidocItemList:itemList">
					<xsl:apply-templates select="doi_record"/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="doi_record"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>
	
	<xsl:template match="doi_record">
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<srel:context xlink:href="{$context}"/>
				<srel:content-model xlink:href="{$content-model}"/>
				<xsl:element name="prop:content-model-specific"/>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:element name="mdp:publication">
						<xsl:choose>
							<xsl:when test="crossref/journal"> 
								<xsl:attribute name="type" select="$genre-ves/enum[.='article']/@uri"/>
								<xsl:call-template name="createJournal"/>

							</xsl:when>
							<xsl:when test="crossref/book and crossref/book/content_item">
								<xsl:attribute name="type" select="$genre-ves/enum[.='book-item']/@uri"/>
								<xsl:call-template name="createBookItem"/>
							</xsl:when>
							<xsl:when test="crossref/book and not (crossref/book/content_item)">
								<xsl:attribute name="type" select="$genre-ves/enum[.='book']/@uri"/>
								<xsl:call-template name="createBook"/>
							</xsl:when>
						</xsl:choose>
						
					</xsl:element>
				</mdr:md-record>
			</xsl:element>	
			<xsl:element name="ec:components"/>
		</xsl:element>

	</xsl:template>	
	
	
	<xsl:template name="createJournal">
		
		<xsl:apply-templates select="crossref/journal/journal_article/titles/title"/>
		<xsl:apply-templates select="crossref/journal/journal_article/publication_date"/>
		<xsl:apply-templates select="crossref/journal/journal_article/doi_data/doi"/>
		
		<xsl:apply-templates select="crossref/journal/journal_article/contributors/person_name"/>
		
		<xsl:element name="source:source">
			<xsl:attribute name="type" select="$genre-ves/enum[.='journal']/@uri"/>
			
			<xsl:apply-templates select="crossref/journal/journal_metadata/full_title"/>
			<xsl:apply-templates select="crossref/journal/journal_metadata/issn"/>
			<xsl:apply-templates select="crossref/journal/journal_article/pages"/>
			<xsl:apply-templates select="crossref/journal/journal_issue/journal_volume/volume"/>
		</xsl:element>	
							
	</xsl:template>
	
	<xsl:template name="createBookItem">
		
		<xsl:apply-templates select="crossref/book/content_item/titles/title"/>
		<xsl:apply-templates select="crossref/book/content_item/publication_date"/>
		<xsl:apply-templates select="crossref/book/content_item/doi_data/doi"/>
		
		<xsl:apply-templates select="crossref/book/content_item/contributors/person_name"/>
		
		<xsl:element name="source:source">
			<xsl:attribute name="type" select="$genre-ves/enum[.='book']/@uri"/>
			<xsl:choose>
				<xsl:when test="crossref/book/book_metadata">
					<xsl:apply-templates select="crossref/book/book_metadata/titles/title"/>
					<xsl:apply-templates select="crossref/book/book_metadata/issn"/>
					<xsl:apply-templates select="crossref/book/book_metadata/isbn"/>
					<xsl:apply-templates select="crossref/book/book_metadata/contributors/person_name"/>
					<xsl:apply-templates select="crossref/book/book_metadata/publication_date"/>
					<xsl:apply-templates select="crossref/book/book_metadata/publisher"/>
					<xsl:apply-templates select="crossref/book/book_metadata/volume"/>
				</xsl:when>
				<xsl:when test="crossref/book/book_series_metadata">
					<xsl:element name="dc:title">
						<xsl:value-of select="concat(crossref/book/book_series_metadata/titles/title,': ',crossref/book/book_series_metadata/series_metadata/titles/title)"/>
					</xsl:element>
					<xsl:apply-templates select="crossref/book/book_series_metadata/series_metadata/issn"/>
					<xsl:apply-templates select="crossref/book/book_series_metadata/isbn"/>
					<xsl:apply-templates select="crossref/book/book_series_metadata/contributors/person_name"/>
					<xsl:apply-templates select="crossref/book/book_series_metadata/publication_date"/>
					<xsl:apply-templates select="crossref/book/book_series_metadata/publisher"/>
					<xsl:apply-templates select="crossref/book/book_series_metadata/volume"/>
				</xsl:when>
			
			</xsl:choose>
			<xsl:apply-templates select="crossref/book/content_item/pages"/>
		</xsl:element>	
							
	</xsl:template>
	
	<xsl:template name="createBook">
		<xsl:apply-templates select="crossref/book/book_series_metadata/series_metadata/titles/title"/>
		<xsl:apply-templates select="crossref/book/book_series_metadata/series_metadata/issn"/>
		<xsl:apply-templates select="crossref/book/book_series_metadata/isbn"/>
		<xsl:apply-templates select="crossref/book/book_series_metadata/contributors/person_name"/>
		<xsl:apply-templates select="crossref/book/book_series_metadata/publication_date"/>
		<xsl:apply-templates select="crossref/book/book_series_metadata/publisher"/>
		<xsl:apply-templates select="crossref/book/content_item/pages"/>
		<xsl:apply-templates select="crossref/book/book_series_metadata/volume"/>
	</xsl:template>
	
	<xsl:template match="full_title">
		<xsl:element name="dc:title">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="issn">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:ISSN</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="isbn">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:ISBN</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="pages">
		<xsl:if test="first_page">
			<publ:start-page>
				<xsl:value-of select="first_page"/>
			</publ:start-page>
		</xsl:if>
		<xsl:if test="last_page">
			<publ:end-page>
				<xsl:value-of select="last_page"/>
			</publ:end-page>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="volume">
		<publ:volume>
			<xsl:value-of select="."/>
		</publ:volume>
	</xsl:template>	
	
	<xsl:template match="title">
		<xsl:element name="dc:title">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
		
	<xsl:template match="publication_date">
		<xsl:element name="{escidoc:getYearElementName(@media_type)}">
			<xsl:choose>
				<xsl:when test="year and month and day and not(@media_type='print')">
					<xsl:value-of select="concat(year,'-',escidoc:completeDigit(month),'-',escidoc:completeDigit(day))"/>			
				</xsl:when>
				<xsl:when test="year and month and not(@media_type='print')">
					<xsl:value-of select="concat(year,'-',escidoc:completeDigit(month))"/>			
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="year"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="person_name">
		<xsl:element name="publ:creator">
			<xsl:choose>
				<xsl:when test="@contributor_role='editor'">
					<xsl:attribute name="role" select="$creator-ves/enum[.='editor']/@uri"/>
				</xsl:when>
				<xsl:when test="@contributor_role='author'">
					<xsl:attribute name="role" select="$creator-ves/enum[.='author']/@uri"/>
				</xsl:when>
			</xsl:choose>

			<xsl:element name="person:person">
				<xsl:if test="surname and given_name">
					<publ:complete-name>
						<xsl:value-of select="given_name"/>
						<xsl:text> </xsl:text>
						<xsl:value-of select="surname"/>
					</publ:complete-name>
				</xsl:if>
				<publ:family-name>
					<xsl:value-of select="surname"/>
				</publ:family-name>
				<publ:given-name>
					<xsl:value-of select="given_name"/>
				</publ:given-name>
				<xsl:element name="ou:organization">
					<dc:title>
						<xsl:value-of select="'External Organizations'"/>
					</dc:title>
					<dc:identifier>
						<xsl:value-of select="$external_organization_id" />
					</dc:identifier>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="doi">
	   	<xsl:if test="not(. = '')">
			<xsl:element name="dc:identifier">
				<xsl:attribute name="xsi:type">eterms:DOI</xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="publisher">
		<xsl:element name="eterms:publishing-info">
			<xsl:element name="dc:publisher">
				<xsl:value-of select="publisher_name"/>
			</xsl:element>
			<xsl:if test="publisher_place">
				<xsl:element name="eterms:place">
					<xsl:value-of select="publisher_place"/>
				</xsl:element>			
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
		<xsl:function name="escidoc:completeDigit">
		<xsl:param name="param"/>
		<xsl:choose>
			<xsl:when test="fn:string-length($param) = 1">
				<xsl:value-of select="concat('0',$param)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$param"/>
			</xsl:otherwise>
		
		</xsl:choose>
	</xsl:function>
	
	<xsl:function name="escidoc:getYearElementName">
		<xsl:param name="param"/>
		<xsl:choose>
			<xsl:when test="$param='print'">
				<xsl:value-of select="'dcterms:issued'"/>				
			</xsl:when>
			<xsl:when test="$param='online'">
				<xsl:value-of select="'eterms:published-online'"/>				
			</xsl:when>
		</xsl:choose>
	
	</xsl:function>
	

</xsl:stylesheet>
