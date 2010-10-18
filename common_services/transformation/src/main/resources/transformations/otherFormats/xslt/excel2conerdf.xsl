<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:srw="http://www.loc.gov/zing/srw/" xmlns:search-result="http://www.escidoc.de/schemas/searchresult/0.8" xmlns:organizational-unit="http://www.escidoc.de/schemas/organizationalunit/0.8" xmlns:excel="urn:schemas-microsoft-com:office:spreadsheet" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"  xmlns:mdou="http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:ei="http://www.escidoc.de/schemas/item/0.8" xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.5" xmlns:mdp="http://escidoc.mpg.de/metadataprofile/schema/0.1/" xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"  xmlns:prop="http://escidoc.de/core/01/properties/" xmlns:srel="http://escidoc.de/core/01/structural-relations/" xmlns:version="http://escidoc.de/core/01/properties/version/" xmlns:release="http://escidoc.de/core/01/properties/release/" xmlns:file="http://escidoc.mpg.de/metadataprofile/schema/0.1/file" xmlns:publ="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication" xmlns:escidocFunctions="urn:escidoc:functions" xmlns:escidoc="http://purl.org/escidoc/metadata/terms/0.1/" xmlns:eprint="http://purl.org/eprint/terms/">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="ou-url" select="'http://migration-coreservice.mpdl.mpg.de:8080'"/>
	<xsl:param name="cone-url" select="'http://migration-pubman.mpdl.mpg.de:8080/cone'"/>
	
	<xsl:variable name="ou-list" select="document(concat($ou-url, '/srw/search/escidocou_all?query=(escidoc.objid=e*)&amp;maximumRecords=10000'))"/>
	<xsl:variable name="cone-list" select="document(concat($cone-url, '/persons/all?format=rdf'))"/>
	
	

	<xsl:template match="/">
		<rdf:RDF>
			
			<xsl:for-each select="//excel:Row">
				<xsl:variable name="pos" select="position()"/>
				
				<xsl:if test="excel:Cell[1]/excel:Data != '' and excel:Cell[1]/excel:Data != 'Nachname' and string-length(//excel:Row[$pos - 1]/excel:Cell[1]/excel:Data) = 0">
					
					<xsl:variable name="main" select="//excel:Row[position() &gt;= $pos and excel:Cell[3] != ''][1]"/>

					<xsl:variable name="familyname" select="normalize-space(string($main/excel:Cell[1]/excel:Data[1]))"/>
					<xsl:variable name="givenname" select="normalize-space($main/excel:Cell[2]/excel:Data)"/>
					<xsl:variable name="ouname" select="normalize-space($main/excel:Cell[4]/excel:Data)"/>
					
					<xsl:variable name="old-entry" select="$cone-list/rdf:RDF/rdf:Description[contains(./dc:title, concat($familyname, ', ', $givenname)) and contains(./dc:title , $ouname)]"/>

					<rdf:Description>
						<xsl:if test="exists($old-entry)">
							<xsl:attribute name="rdf:about" select="$old-entry/@rdf:about"/>
						</xsl:if>
						<dc:title>
							<xsl:value-of select="normalize-space($main/excel:Cell[1]/excel:Data)"/>
							<xsl:text>, </xsl:text>
							<xsl:value-of select="normalize-space($main/excel:Cell[2]/excel:Data)"/>
						</dc:title>
						<foaf:family_name>
							<xsl:value-of select="$familyname"/>
						</foaf:family_name>
						<foaf:givenname>
							<xsl:value-of select="$givenname"/>
						</foaf:givenname>
						<xsl:call-template name="alternative-name">
							<xsl:with-param name="pos" select="$pos"/>
							<xsl:with-param name="main" select="$main"/>
						</xsl:call-template>
						<escidoc:position>
							<rdf:Description>

								<xsl:variable name="escidoc-ou">
									<xsl:value-of select="$ou-list/srw:searchRetrieveResponse/srw:records/srw:record[normalize-space(srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title) = $ouname]/srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/@objid"/>
								</xsl:variable>
							
								<xsl:variable name="ou-path">
									<xsl:call-template name="get-ou-path">
										<xsl:with-param name="id" select="$escidoc-ou"/>
									</xsl:call-template>
								</xsl:variable>
							
								<eprint:affiliatedInstitution>
									<xsl:value-of select="$ou-path"/>
								</eprint:affiliatedInstitution>
								
								
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
	
	<xsl:template name="get-ou-path">
		<xsl:param name="id"/>
		
		<xsl:variable name="ou" select="$ou-list/srw:searchRetrieveResponse/srw:records/srw:record/srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit[@objid = $id]"/>
	
		<xsl:value-of select="normalize-space($ou/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title)"/>
		
		<xsl:if test="normalize-space($ou/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title) = ''">
			ERROR
		</xsl:if>
		
		<xsl:choose>
			<xsl:when test="exists($ou/organizational-unit:parents/srel:parent)">
				<xsl:text>, </xsl:text>
				<xsl:call-template name="get-ou-path">
					<xsl:with-param name="id" select="$ou/organizational-unit:parents/srel:parent[1]/@objid"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
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
