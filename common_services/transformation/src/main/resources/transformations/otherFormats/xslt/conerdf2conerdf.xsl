<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:eprints="http://purl.org/eprint/terms/" xmlns:srw="http://www.loc.gov/zing/srw/" xmlns:search-result="http://www.escidoc.de/schemas/searchresult/0.8" xmlns:organizational-unit="http://www.escidoc.de/schemas/organizationalunit/0.8" xmlns:excel="urn:schemas-microsoft-com:office:spreadsheet" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"  xmlns:mdou="http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:ei="http://www.escidoc.de/schemas/item/0.8" xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.5" xmlns:mdp="http://escidoc.mpg.de/metadataprofile/schema/0.1/" xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"  xmlns:prop="http://escidoc.de/core/01/properties/" xmlns:srel="http://escidoc.de/core/01/structural-relations/" xmlns:version="http://escidoc.de/core/01/properties/version/" xmlns:release="http://escidoc.de/core/01/properties/release/" xmlns:file="http://escidoc.mpg.de/metadataprofile/schema/0.1/file" xmlns:publ="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication" xmlns:escidocFunctions="urn:escidoc:functions" xmlns:escidoc="http://purl.org/escidoc/metadata/terms/0.1/" xmlns:eprint="http://purl.org/eprint/terms/">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="ou-url" select="'http://coreservice.mpdl.mpg.de:8080'"/>
	<xsl:param name="cone-url" select="'http://pubman.mpdl.mpg.de:8080/cone'"/>
	<!-- URL of the server, where the RDF has been exported (needed, if commas are in the OU-Names) -->
	<xsl:param name="old-ou-url" select="''"/> 
	
	<xsl:variable name="ou-list" select="document(concat($ou-url, '/srw/search/escidocou_all?query=(escidoc.objid=e*)&amp;maximumRecords=10000'))"/>
	<xsl:variable name="cone-list" select="document(concat($cone-url, '/persons/all?format=rdf'))"/>
	<!-- OU-List of the server, where the RDF has been exported (needed, if commas are in the OU-Names)-->
	<xsl:variable name="old-ou-list" select="document(concat($old-ou-url, '/srw/search/escidocou_all?query=(escidoc.objid=e*)&amp;maximumRecords=10000'))"/>
	
	
	

	<xsl:template match="/">
		<rdf:RDF>
			<xsl:for-each select="rdf:RDF/rdf:Description">
				<xsl:variable name="about" select="@rdf:about"/>
				<xsl:if test="not(exists(preceding-sibling::*[@rdf:about = $about]))">
					<xsl:copy>
						<xsl:copy-of select="@*[name() != 'rdf:about']"/>
						<xsl:apply-templates/>
					</xsl:copy>
				</xsl:if>
			</xsl:for-each>
		</rdf:RDF>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	
	
	<xsl:template match="escidoc:position/rdf:Description/dc:identifier">
	    <xsl:variable name="idToCompare"><xsl:value-of select="."/></xsl:variable>
		<xsl:variable name="ou">
			<xsl:choose>
			    <xsl:when test="$old-ou-url != '' and exists(normalize-space($old-ou-list/srw:searchRetrieveResponse/srw:records/srw:record[srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/substring-after(substring-after(substring-after(@xlink:href, '/'), '/'), '/') = $idToCompare]/srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title))">
			        <!-- ADJUST to @objid, when Pointing to CoreService < 1.3.X -->
			        <xsl:value-of select="normalize-space($old-ou-list/srw:searchRetrieveResponse/srw:records/srw:record[srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/substring-after(substring-after(substring-after(@xlink:href, '/'), '/'), '/') = $idToCompare]/srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title)"/>
			    </xsl:when>
				<xsl:when test="contains(../eprints:affiliatedInstitution, ',')">
					<xsl:value-of select="substring-before(../eprints:affiliatedInstitution, ',')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="../eprints:affiliatedInstitution"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="$old-ou-url != '' and exists(normalize-space($old-ou-list/srw:searchRetrieveResponse/srw:records/srw:record[srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/substring-after(substring-after(substring-after(@xlink:href, '/'), '/'), '/') = $idToCompare]/srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title))">
			<!-- ADJUST to @objid, when Pointing to CoreService < 1.3.X -->
		    <xsl:comment>Looking for OU: '<xsl:value-of select="$ou"/>' and found <xsl:value-of select="count($ou-list/srw:searchRetrieveResponse/srw:records/srw:record[normalize-space(srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title) = $ou])"/> result</xsl:comment>
		</xsl:if>
		
		<xsl:if test="count($ou-list/srw:searchRetrieveResponse/srw:records/srw:record[normalize-space(srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title) = $ou]) &gt; 1">
			<xsl:comment>ERROR with "<xsl:value-of select="$ou"/>" (found more than one entry)</xsl:comment>
		</xsl:if>
		<xsl:if test="$ou-list/srw:searchRetrieveResponse/srw:records/srw:record[normalize-space(srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title) = $ou]/srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/substring-after(substring-after(substring-after(@xlink:href, '/'), '/'), '/') = ''">
			<xsl:comment>ERROR with "<xsl:value-of select="$ou"/>" (ID empty)</xsl:comment>
		</xsl:if>
		<xsl:element name="dc:identifier">
			<xsl:value-of select="$ou-list/srw:searchRetrieveResponse/srw:records/srw:record[normalize-space(srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title) = $ou]/srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/substring-after(substring-after(substring-after(@xlink:href, '/'), '/'), '/')"/>
		</xsl:element>
		<xsl:comment>ID set for OU '<xsl:value-of select="$ou"/>'</xsl:comment>
	</xsl:template>
	
	<xsl:template name="get-ou-path">
		<xsl:param name="id"/>
		
		<xsl:variable name="ou" select="$ou-list/srw:searchRetrieveResponse/srw:records/srw:record/srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit[substring-after(substring-after(substring-after(@xlink:href, '/'), '/'), '/') = $id]"/>
	
		<xsl:value-of select="normalize-space($ou/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title)"/>
		
		<xsl:if test="normalize-space($ou/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title) = ''">
			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:OuNotFound' ), 'Ou not found')"/>
		</xsl:if>
		
		<xsl:choose>
			<xsl:when test="exists($ou/organizational-unit:parents/srel:parent)">
				<xsl:text>, </xsl:text>
				<xsl:call-template name="get-ou-path">
					<xsl:with-param name="id" select="$ou/organizational-unit:parents/srel:parent[1]/substring-after(substring-after(substring-after(@xlink:href, '/'), '/'), '/')"/>
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
