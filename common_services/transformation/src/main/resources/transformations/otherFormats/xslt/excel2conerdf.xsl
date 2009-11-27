<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:excel="urn:schemas-microsoft-com:office:spreadsheet" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:ei="http://www.escidoc.de/schemas/item/0.8" xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.4" xmlns:mdp="http://escidoc.mpg.de/metadataprofile/schema/0.1/" xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types" xmlns:ec="http://www.escidoc.de/schemas/components/0.8" xmlns:prop="http://escidoc.de/core/01/properties/" xmlns:srel="http://escidoc.de/core/01/structural-relations/" xmlns:version="http://escidoc.de/core/01/properties/version/" xmlns:release="http://escidoc.de/core/01/properties/release/" xmlns:file="http://escidoc.mpg.de/metadataprofile/schema/0.1/file" xmlns:publ="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication" xmlns:escidocFunctions="urn:escidoc:functions" xmlns:escidoc="http://escidoc.mpg.de/" xmlns:Util="java:de.mpg.escidoc.services.transformation.Util">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="ou-url" select="'http://migration-coreservice.mpdl.mpg.de:8080'"/>
	
	<xsl:variable name="ou-list" select="document(concat($ou-url, '/srw/search/escidocou_all?query=(escidoc.objid=e*)&amp;maximumRecords=10000'))"/>

	<xsl:template match="/">
		<rdf:RDF>
			
			<xsl:for-each select="//excel:Row">
				<xsl:variable name="pos" select="position()"/>
				
				<xsl:if test="excel:Cell[1]/excel:Data != '' and excel:Cell[1]/excel:Data != 'Nachname' and string-length(//excel:Row[$pos - 1]/excel:Cell[1]/excel:Data) = 0">
					<rdf:Description>
						<xsl:variable name="main" select="//excel:Row[position() &gt;= $pos and excel:Cell[3] != ''][1]"/>
						<dc:title>
							<xsl:value-of select="normalize-space($main/excel:Cell[1]/excel:Data)"/>
							<xsl:text>, </xsl:text>
							<xsl:value-of select="normalize-space($main/excel:Cell[2]/excel:Data)"/>
						</dc:title>
						<foaf:familyname>
							<xsl:value-of select="normalize-space($main/excel:Cell[1]/excel:Data)"/>
						</foaf:familyname>
						<foaf:givenname>
							<xsl:value-of select="normalize-space($main/excel:Cell[2]/excel:Data)"/>
						</foaf:givenname>
						<xsl:call-template name="alternative-name">
							<xsl:with-param name="pos" select="$pos"/>
							<xsl:with-param name="main" select="$main"/>
						</xsl:call-template>
						<escidoc:position>
							<rdf:Description>
								<escidoc:organization>
									<xsl:value-of select="$main/excel:Cell[4]/excel:Data"/>
								</escidoc:organization>
								<xsl:variable name="ou" select="$main/excel:Cell[4]/excel:Data"/>
								<xsl:variable name="escidoc-ou">
									<xsl:value-of select="$ou-list/srw:searchRetrieveResponse/srw:records/srw:record[srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/organizational-unit:properties/prop:name = $ou]/srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/@objid" xmlns:srw="http://www.loc.gov/zing/srw/" xmlns:search-result="http://www.escidoc.de/schemas/searchresult/0.7" xmlns:organizational-unit="http://www.escidoc.de/schemas/organizationalunit/0.7" xmlns:prop="http://escidoc.de/core/01/properties/"/>
								</xsl:variable>
								<xsl:if test="$escidoc-ou != ''">
									<dc:identifier>
										<xsl:value-of select="$escidoc-ou"/>
									</dc:identifier>
								</xsl:if>
							</rdf:Description>
						</escidoc:position>
						<escidoc:degree>
							<xsl:value-of select="$main/excel:Cell[5]/excel:Data"/>
						</escidoc:degree>
					</rdf:Description>
				</xsl:if>
			</xsl:for-each>
		</rdf:RDF>
	</xsl:template>
	
	<xsl:template name="alternative-name">
		<xsl:param name="pos"/>
		<xsl:param name="main"/>
		<xsl:choose>
			<xsl:when test="//excel:Row[$pos] = $main">
				<xsl:call-template name="alternative-name">
					<xsl:with-param name="pos" select="$pos + 1"/>
					<xsl:with-param name="main" select="$main"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="string-length(//excel:Row[$pos]/excel:Cell[1]/excel:Data) = 0"/>
			<xsl:otherwise>
				<dcterms:alternative>
					<xsl:value-of select="normalize-space(//excel:Row[$pos]/excel:Cell[1]/excel:Data)"/>
					<xsl:text>, </xsl:text>
					<xsl:value-of select="normalize-space(//excel:Row[$pos]/excel:Cell[2]/excel:Data)"/>
				</dcterms:alternative>
				<xsl:call-template name="alternative-name">
					<xsl:with-param name="pos" select="$pos + 1"/>
					<xsl:with-param name="main" select="$main"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	
	</xsl:template>

</xsl:stylesheet>
