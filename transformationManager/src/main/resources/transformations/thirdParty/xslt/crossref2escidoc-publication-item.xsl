<?xml version="1.0" encoding="UTF-8"?>
<!-- Beispielaufruf für CrossRef Quelle: http://doi.crossref.org/servlet/query?pid=bib@gfz-potsdam.de&format=unixref&id=10.1002/esp.4823 -->
<xsl:stylesheet version="2.0" 
	xmlns:dc="http://purl.org/dc/elements/1.1/" 
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:ec="http://www.escidoc.de/schemas/components/0.9"
	xmlns:ei="http://www.escidoc.de/schemas/item/0.10"
	xmlns:escidoc="urn:escidoc:functions"
	xmlns:escidocItemList="http://www.escidoc.de/schemas/itemlist/0.10"
	xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
	xmlns:event="http://purl.org/escidoc/metadata/profiles/0.1/event"
	xmlns:file="http://purl.org/escidoc/metadata/profiles/0.1/file"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:mdp="http://purl.org/escidoc/metadata/profiles/0.1/publication"
	xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.5"
	xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/"
	xmlns:ou="http://purl.org/escidoc/metadata/profiles/0.1/organization"
	xmlns:person="http://purl.org/escidoc/metadata/profiles/0.1/person"
	xmlns:prop="http://escidoc.de/core/01/properties/"
	xmlns:publ="http://purl.org/escidoc/metadata/terms/0.1/"
	xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source"
	xmlns:srel="http://escidoc.de/core/01/structural-relations/"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:import href="../../vocabulary-mappings.xsl"></xsl:import>
	
	<xsl:param name="external_organization_id" select="'dummy-id'"></xsl:param>
	<xsl:param name="context" select="'dummy-context'"></xsl:param>
	<xsl:param name="content-model" select="'dummy-content-model'"></xsl:param>
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"></xsl:output>
	
	<xsl:template match="/">
		<xsl:apply-templates select="doi_records"></xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="doi_records">
		<xsl:choose>
			<xsl:when test="count(doi_records/doi_record) > 1">
				<xsl:element name="escidocItemList:itemList">
					<xsl:apply-templates select="doi_record"></xsl:apply-templates>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="doi_record"></xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="doi_record">
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<srel:context xlink:href="{$context}"></srel:context>
				<srel:content-model xlink:href="{$content-model}"></srel:content-model>
				<xsl:element name="prop:content-model-specific"></xsl:element>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:element name="mdp:publication">
						<xsl:choose>
							<xsl:when test="crossref/journal">
								<xsl:attribute name="type"
									select="$genre-ves/enum[. = 'article']/@uri"></xsl:attribute>
								<xsl:call-template name="createJournal"></xsl:call-template>
							</xsl:when>
							<xsl:when test="crossref/book and crossref/book/content_item">
								<xsl:attribute name="type"
									select="$genre-ves/enum[. = 'book-item']/@uri"></xsl:attribute>
								<xsl:call-template name="createBookItem"></xsl:call-template>
							</xsl:when>
							<xsl:when test="crossref/book and not(crossref/book/content_item)">
								<xsl:attribute name="type" select="$genre-ves/enum[. = 'book']/@uri"></xsl:attribute>
								<xsl:call-template name="createBook"></xsl:call-template>
							</xsl:when>
						</xsl:choose>
					</xsl:element>
				</mdr:md-record>
			</xsl:element>
			<xsl:element name="ec:components"></xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="createJournal">
		<xsl:apply-templates select="crossref/journal/journal_article/titles/title"></xsl:apply-templates>
		<xsl:apply-templates select="crossref/journal/journal_article/publication_date"></xsl:apply-templates>
		<xsl:apply-templates select="crossref/journal/journal_article/doi_data/doi"></xsl:apply-templates>
		<xsl:apply-templates select="crossref/journal/journal_article/contributors/person_name"></xsl:apply-templates>
		<xsl:element name="source:source">
			<xsl:attribute name="type" select="$genre-ves/enum[. = 'journal']/@uri"></xsl:attribute>
			<xsl:apply-templates select="crossref/journal/journal_metadata/full_title"></xsl:apply-templates>
			<xsl:apply-templates select="crossref/journal/journal_metadata/issn"></xsl:apply-templates>
			<xsl:apply-templates select="crossref/journal/journal_article/pages"></xsl:apply-templates>
			<xsl:apply-templates select="crossref/journal/journal_issue/journal_volume/volume"></xsl:apply-templates>
			<xsl:apply-templates select="crossref/journal/journal_article/publisher_item/item_number"></xsl:apply-templates>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="createBookItem">
		<xsl:apply-templates select="crossref/book/content_item/titles/title"></xsl:apply-templates>
		<xsl:apply-templates select="crossref/book/content_item/publication_date"></xsl:apply-templates>
		<xsl:apply-templates select="crossref/book/content_item/doi_data/doi"></xsl:apply-templates>
		<xsl:apply-templates select="crossref/book/content_item/contributors/person_name"></xsl:apply-templates>
		<xsl:element name="source:source">
			<xsl:attribute name="type" select="$genre-ves/enum[. = 'book']/@uri"></xsl:attribute>
			<xsl:choose>
				<xsl:when test="crossref/book/book_metadata">
					<xsl:apply-templates select="crossref/book/book_metadata/titles/title"></xsl:apply-templates>
					<xsl:apply-templates select="crossref/book/book_metadata/issn"></xsl:apply-templates>
					<xsl:apply-templates select="crossref/book/book_metadata/isbn"></xsl:apply-templates>
					<xsl:apply-templates select="crossref/book/book_metadata/contributors/person_name"></xsl:apply-templates>
					<xsl:apply-templates select="crossref/book/book_metadata/publication_date"></xsl:apply-templates>
					<xsl:apply-templates select="crossref/book/book_metadata/publisher"></xsl:apply-templates>
					<xsl:apply-templates select="crossref/book/book_metadata/volume"></xsl:apply-templates>
				</xsl:when>
				<xsl:when test="crossref/book/book_series_metadata">
					<xsl:element name="dc:title">
						<xsl:value-of
							select="concat(crossref/book/book_series_metadata/titles/title, ': ', crossref/book/book_series_metadata/series_metadata/titles/title)"
						></xsl:value-of>
					</xsl:element>
					<xsl:apply-templates select="crossref/book/book_series_metadata/series_metadata/issn"></xsl:apply-templates>
					<xsl:apply-templates select="crossref/book/book_series_metadata/isbn"></xsl:apply-templates>
					<xsl:apply-templates select="crossref/book/book_series_metadata/contributors/person_name"></xsl:apply-templates>
					<xsl:apply-templates select="crossref/book/book_series_metadata/publication_date"></xsl:apply-templates>
					<xsl:apply-templates select="crossref/book/book_series_metadata/publisher"></xsl:apply-templates>
					<xsl:apply-templates select="crossref/book/book_series_metadata/volume"></xsl:apply-templates>
				</xsl:when>
			</xsl:choose>
			<xsl:apply-templates select="crossref/book/content_item/pages"></xsl:apply-templates>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="createBook">
		<xsl:apply-templates select="crossref/book/book_metadata/titles/title"></xsl:apply-templates>
		<xsl:apply-templates select="crossref/book/book_metadata/issn"></xsl:apply-templates>
		<xsl:apply-templates select="crossref/book/book_metadata/isbn"></xsl:apply-templates>
		<xsl:apply-templates select="crossref/book/book_metadata/contributors/person_name"></xsl:apply-templates>
		<xsl:apply-templates select="crossref/book/book_metadata/publication_date"></xsl:apply-templates>
		<xsl:apply-templates select="crossref/book/book_metadata/publisher"></xsl:apply-templates>
		<xsl:apply-templates select="crossref/book/book_metadata/content_item/pages"></xsl:apply-templates>
		<xsl:apply-templates select="crossref/book/book_series_metadata/volume"></xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="full_title">
		<xsl:element name="dc:title">
            <xsl:value-of select="replace(replace(.,'\n',''),'\s{2,}','')"></xsl:value-of>
		</xsl:element>
	</xsl:template>
	
    <xsl:template match="title">
        <xsl:element name="dc:title">
            <xsl:value-of select="replace(replace(.,'\n',''),'\s{2,}','')"></xsl:value-of>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="item_number">
        <xsl:element name="publ:sequence-number">
            <xsl:choose>
                <xsl:when test="@item_number_type = 'article-number'">
                    <xsl:value-of select="."></xsl:value-of>
                </xsl:when>
            </xsl:choose>
        </xsl:element>
    </xsl:template>
    
	<xsl:template match="issn">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:ISSN</xsl:attribute>
			<xsl:value-of select="."></xsl:value-of>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="isbn">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:ISBN</xsl:attribute>
			<xsl:value-of select="."></xsl:value-of>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="pages">
		<xsl:if test="first_page">
			<publ:start-page>
				<xsl:value-of select="first_page"></xsl:value-of>
			</publ:start-page>
		</xsl:if>
		<xsl:if test="last_page">
			<publ:end-page>
				<xsl:value-of select="last_page"></xsl:value-of>
			</publ:end-page>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="volume">
		<publ:volume>
			<xsl:value-of select="."></xsl:value-of>
		</publ:volume>
	</xsl:template>
	
	<xsl:template match="publication_date">
		<xsl:element name="{escidoc:getYearElementName(@media_type)}">
			<xsl:choose>
				<xsl:when test="year and month and day and not(@media_type = 'print')">
					<xsl:value-of
						select="concat(year, '-', escidoc:completeDigit(month), '-', escidoc:completeDigit(day))"
					></xsl:value-of>
				</xsl:when>
				<xsl:when test="year and month and not(@media_type = 'print')">
					<xsl:value-of select="concat(year, '-', escidoc:completeDigit(month))"
					></xsl:value-of>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="year"></xsl:value-of>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="person_name">
		<xsl:element name="publ:creator">
			<xsl:choose>
				<xsl:when test="@contributor_role = 'editor'">
					<xsl:attribute name="role" select="$creator-ves/enum[. = 'editor']/@uri"
					></xsl:attribute>
				</xsl:when>
				<xsl:when test="@contributor_role = 'author'">
					<xsl:attribute name="role" select="$creator-ves/enum[. = 'author']/@uri"
					></xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:element name="person:person">
				<xsl:if test="surname and given_name">
					<publ:complete-name>
						<xsl:value-of select="given_name"></xsl:value-of>
						<xsl:text> </xsl:text>
						<xsl:value-of select="surname"></xsl:value-of>
					</publ:complete-name>
				</xsl:if>
				<publ:family-name>
					<xsl:value-of select="surname"></xsl:value-of>
				</publ:family-name>
				<publ:given-name>
					<xsl:value-of select="given_name"></xsl:value-of>
				</publ:given-name>
				<xsl:element name="ou:organization">
					<dc:title>
						<xsl:value-of select="'External Organizations'"></xsl:value-of>
					</dc:title>
					<dc:identifier>
						<xsl:value-of select="$external_organization_id"></xsl:value-of>
					</dc:identifier>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="doi">
		<xsl:if test="not(. = '')">
			<xsl:element name="dc:identifier">
				<xsl:attribute name="xsi:type">eterms:DOI</xsl:attribute>
				<xsl:value-of select="."></xsl:value-of>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="publisher">
		<xsl:element name="eterms:publishing-info">
			<xsl:element name="dc:publisher">
				<xsl:value-of select="publisher_name"></xsl:value-of>
			</xsl:element>
			<xsl:if test="publisher_place">
				<xsl:element name="eterms:place">
					<xsl:value-of select="publisher_place"></xsl:value-of>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
	<xsl:function name="escidoc:completeDigit">
		<xsl:param name="param"></xsl:param>
		<xsl:choose>
			<xsl:when test="fn:string-length($param) = 1">
				<xsl:value-of select="concat('0', $param)"></xsl:value-of>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$param"></xsl:value-of>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<xsl:function name="escidoc:getYearElementName">
		<xsl:param name="param"></xsl:param>
		<xsl:choose>
			<xsl:when test="$param = 'print'">
				<xsl:value-of select="'dcterms:issued'"></xsl:value-of>
			</xsl:when>
			<xsl:when test="$param = 'online'">
				<xsl:value-of select="'eterms:published-online'"></xsl:value-of>
			</xsl:when>
		</xsl:choose>
	</xsl:function>
</xsl:stylesheet>
