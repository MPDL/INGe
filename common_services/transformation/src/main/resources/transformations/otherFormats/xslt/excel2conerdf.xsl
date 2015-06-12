<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
		xmlns:dc="http://purl.org/dc/elements/1.1/" 
		xmlns:dcterms="http://purl.org/dc/terms/" 
		xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types" 
		xmlns:ei="${xsd.soap.item.item}" 
		xmlns:eprint="http://purl.org/eprint/terms/"
		xmlns:escidocFunctions="urn:escidoc:functions" 
		xmlns:escidoc="${xsd.metadata.terms}" 
		xmlns:excel="urn:schemas-microsoft-com:office:spreadsheet"
		xmlns:file="${xsd.metadata.file}"
		xmlns:fn="http://www.w3.org/2005/xpath-functions"
		xmlns:foaf="http://xmlns.com/foaf/0.1/" 
		xmlns:mdou="${xsd.metadata.organizationalunit}"  
		xmlns:mdp="${xsd.metadata.escidocprofile}" 
		xmlns:mdr="${xsd.soap.common.mdrecords}" 
		xmlns:organizational-unit="${xsd.soap.ou.ou}"
		xmlns:prop="${xsd.soap.common.prop}" 
		xmlns:publ="${xsd.metadata.publication}"
		xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
		xmlns:release="${xsd.soap.common.release}"  
		xmlns:search-result="${xsd.soap.searchresult.searchresult}"
		xmlns:srel="${xsd.soap.common.srel}"
		xmlns:srw="http://www.loc.gov/zing/srw/"
		xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet" 
		xmlns:version="${xsd.soap.common.version}"
		xmlns:xlink="http://www.w3.org/1999/xlink"
		xmlns:xs="http://www.w3.org/2001/XMLSchema" 
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="ou-url" select="'http://migration-coreservice.mpdl.mpg.de:8080'"/>
	<xsl:param name="cone-url" select="'http://migration-pubman.mpdl.mpg.de:8080/cone'"/>
	<xsl:param name="check-existence" select="false"/>
	<xsl:param name="check-compare" select="'false'"/>
	
	<xsl:param name="import-name"/>
	<xsl:param name="external-ou-id" select="'persistent:22'"/>

	<xsl:param name="user" select="'administrator'"/>
	
	<xsl:param name="compare-query" select="'%22*Max%20Planck%20Institute%20for%20Research%20on%20Collective%20Goods*%22'"/>
	
	<xsl:variable name="ou-list" select="document(concat($ou-url, '/srw/search/escidocou_all?query=(escidoc.objid=e*)&amp;maximumRecords=10000'))"/>
	<xsl:variable name="cone-list" select="document(concat($cone-url, '/persons/all?format=rdf'))"/>
	
	<xsl:variable name="comparation-list" select="document(concat($cone-url, '/persons/query?format=rdf&amp;q=', $compare-query, '&amp;l=*&amp;n=0&amp;m=full'))"/>

	<xsl:template match="/">
		<rdf:RDF>
		
			<xsl:comment>Found <xsl:value-of select="count(//excel:Row)"/> rows.</xsl:comment>
			
			<xsl:comment>
				<xsl:value-of select="concat($ou-url, '/srw/search/escidocou_all?query=(escidoc.objid=e*)&amp;maximumRecords=10000')"/>
				<xsl:text> - </xsl:text>
				<xsl:value-of select="count($ou-list//srw:record)"/>
			</xsl:comment>
			
			<!-- Check if there are more than one worksheets -->
			<xsl:if test="count(//excel:Worksheet) != 1">
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:WrongNumberOfWorksheets' ), 'There must be only one Worksheet.')"/>
			</xsl:if>
		
			<xsl:for-each select="//excel:Row">

				<xsl:variable name="pos" select="position()"/>

				<!--  <xsl:if test="excel:Cell[1]/excel:Data != '' and excel:Cell[1]/excel:Data != 'Nachname' and (string-length(//excel:Row[$pos - 1]/excel:Cell[1]/excel:Data) = 0 or //excel:Row[$pos - 1]/@ss:Index != '')">-->
				<xsl:if test="excel:Cell[1]/excel:Data != '' and excel:Cell[1]/excel:Data != 'Nachname' and lower-case(excel:Cell[3]/excel:Data) = 'x'">
					
					<xsl:variable name="main" select="."/>

					<xsl:variable name="familyname" select="normalize-space(string($main/excel:Cell[1]/excel:Data[1]))"/>
					<xsl:variable name="givenname" select="normalize-space($main/excel:Cell[2]/excel:Data)"/>
					
					<rdf:Description>
						<xsl:if test="$check-compare = 'true' and exists($comparation-list/rdf:RDF/rdf:Description[dc:title = concat($familyname, ', ', $givenname)])">
							<xsl:attribute name="rdf:about" select="$comparation-list/rdf:RDF/rdf:Description[dc:title = concat($familyname, ', ', $givenname)]/@rdf:about"/>
							<xsl:comment>
								Match found with name <xsl:value-of select="concat($familyname, ', ', $givenname)"/>
							</xsl:comment>
						</xsl:if>
						<dc:title>
							<xsl:value-of select="normalize-space($main/excel:Cell[1]/excel:Data)"/>
							<xsl:if	test="$givenname != '#'"> <!-- Änderung Erndt -->	
								<xsl:text>, </xsl:text>
								<xsl:value-of select="normalize-space($main/excel:Cell[2]/excel:Data)"/>
							</xsl:if>
						</dc:title>
						<foaf:family_name>
							<xsl:value-of select="$familyname"/>
						</foaf:family_name>
						<xsl:if	test="$givenname != '#'"> <!-- Änderung Erndt -->
							<foaf:givenname>
								<xsl:value-of select="$givenname"/>
							</foaf:givenname>
						</xsl:if>
						<xsl:call-template name="alternative-name">
							<xsl:with-param name="pos" select="$pos"/>
							<xsl:with-param name="main" select="$main"/>
						</xsl:call-template>
						
						<xsl:call-template name="start-affiliations">
							<xsl:with-param name="pos" select="$pos"/>
							<xsl:with-param name="familyname" select="$familyname"/>
							<xsl:with-param name="givenname" select="$givenname"/>
						</xsl:call-template>
						
						<xsl:call-template name="start-degree">
						    <xsl:with-param name="pos" select="$pos"/>
						</xsl:call-template>
						
						<dcterms:created><xsl:value-of select="current-dateTime()"/></dcterms:created>
						<dcterms:contributor><xsl:value-of select="$user"/></dcterms:contributor>
						
						<!-- <dc:identifier>
							<rdf:Description>
								<xsi:type>MPIKYB</xsi:type>
								<rdf:value>
									<xsl:value-of select="$main/excel:Cell[5]/excel:Data"/>
									<xsl:if test="$main/excel:Cell[3]/@ss:Index = 4 and normalize-space($main/excel:Cell[3]/excel:Data) != ''">
										<xsl:value-of select="$main/excel:Cell[4]/excel:Data"/>
									</xsl:if>
									<xsl:if test="$main/excel:Cell[4]/@ss:Index = 5">
										<xsl:value-of select="$main/excel:Cell[4]/excel:Data"/>
									</xsl:if>
								</rdf:value>
							</rdf:Description>
						</dc:identifier> -->
					</rdf:Description>
				</xsl:if>
			</xsl:for-each>
		</rdf:RDF>
	</xsl:template>
	
	<xsl:template name="start-degree">
	    <xsl:param name="pos"/>
	    <xsl:choose>
	        <!-- GOING BACK TO THE FIRST ROW OF THE ENTRY -->
	        <xsl:when test="//excel:Row[$pos - 1]/excel:Cell[1]/excel:Data != '' and not(exists(//excel:Row[$pos]/@ss:Index))">
	            <xsl:call-template name="start-degree">
	                <xsl:with-param name="pos" select="$pos - 1"/>
	            </xsl:call-template>
	        </xsl:when>
	        <xsl:otherwise>
	            <xsl:call-template name="get-degree">
	                <xsl:with-param name="pos" select="$pos"/>
	            </xsl:call-template>
	        </xsl:otherwise>
	    </xsl:choose>
	</xsl:template>
	
	<xsl:template name="get-degree">
	    <xsl:param name="pos"/>
	    <xsl:choose>
	        <!-- CHECK IF THERE IS ANY DEGREE IN THIS ROW -->
	        <xsl:when test="//excel:Row[$pos]/excel:Cell[3]/@ss:Index = 4 and normalize-space(//excel:Row[$pos]/excel:Cell[3]/excel:Data) != ''">
	            <escidoc:degree>
	                <xsl:comment>DEGREE CASE 1</xsl:comment>
	                <xsl:value-of select="//excel:Row[$pos]/excel:Cell[4]/excel:Data"/>
	            </escidoc:degree>
	        </xsl:when>
	        <xsl:when test="//excel:Row[$pos]/excel:Cell[4]/@ss:Index = 5">
	            <xsl:comment>DEGREE CASE 2</xsl:comment>
	            <escidoc:degree>
	                <xsl:value-of select="//excel:Row[$pos]/excel:Cell[4]/excel:Data"/>
	            </escidoc:degree>
	        </xsl:when>
	        <xsl:when test="//excel:Row[$pos]/excel:Cell[5]/excel:Data and not(//excel:Row[$pos]/excel:Cell[5]/@ss:Index > 5)">
	            <escidoc:degree>
	                <xsl:comment>DEGREE CASE 3</xsl:comment>
	                <xsl:value-of select="//excel:Row[$pos]/excel:Cell[5]/excel:Data"/>
	            </escidoc:degree>
	        </xsl:when>
	        <xsl:otherwise>
	            <!-- CALLS THE TEMPLATE WITH THE NEXT ROW -->
	            <xsl:if test="//excel:Row[$pos + 1]/excel:Cell[1]/excel:Data != '' and not(exists(//excel:Row[$pos]/@ss:Index))">
	                <xsl:call-template name="get-degree">
	                    <xsl:with-param name="pos" select="$pos + 1"/>
	                </xsl:call-template>
	            </xsl:if>
	        </xsl:otherwise>
	    </xsl:choose>
	</xsl:template>
	
	<xsl:template name="start-affiliations">
		<xsl:param name="pos"/>
		<xsl:param name="familyname"/>
		<xsl:param name="givenname"/>
	
		<xsl:choose>
			<xsl:when test="//excel:Row[$pos - 1]/excel:Cell[1]/excel:Data != '' and not(exists(//excel:Row[$pos]/@ss:Index))">
				<xsl:comment>up to <xsl:value-of select="$pos - 1"/></xsl:comment>
				<xsl:call-template name="start-affiliations">
					<xsl:with-param name="pos" select="$pos - 1"/>
					<xsl:with-param name="familyname" select="$familyname"/>
					<xsl:with-param name="givenname" select="$givenname"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="get-affiliations">
					<xsl:with-param name="col" select="1"/>
					<xsl:with-param name="pos" select="$pos"/>
					<xsl:with-param name="familyname" select="$familyname"/>
					<xsl:with-param name="givenname" select="$givenname"/>
					<xsl:with-param name="direction" select="'both'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>

	<xsl:template name="get-affiliations">
		<xsl:param name="pos"/>
		<xsl:param name="col"/>
		<xsl:param name="familyname"/>
		<xsl:param name="givenname"/>
		<xsl:param name="direction"/>
		
		<xsl:variable name="this" select="//excel:Row[$pos]/excel:Cell[$col]"/>
		
		<xsl:comment>direction <xsl:value-of select="$direction"/></xsl:comment>
		
		<xsl:choose>
			<xsl:when test="not(exists($this))">
				<!-- Too far, giving up --><xsl:comment>111</xsl:comment>
			</xsl:when>
			<xsl:when test="$col &gt; 4">
				<!-- Too far, giving up --><xsl:comment>222</xsl:comment>
			</xsl:when>
			<xsl:when test="$this/@ss:Index &gt; 4">
				<!-- Too far, giving up --><xsl:comment>333</xsl:comment>
			</xsl:when>
			<xsl:when test="not(exists($this/@ss:Index)) and //excel:Row[$pos]/excel:Cell[$col - 1]/@ss:Index &gt; 3">
				<!-- Too far, giving up --><xsl:comment>444</xsl:comment>
			</xsl:when>
			<xsl:when test="not(exists($this/@ss:Index)) and not(exists(//excel:Row[$pos]/excel:Cell[$col - 1]/@ss:Index)) and //excel:Row[$pos]/excel:Cell[$col - 2]/@ss:Index &gt; 2">
				<!-- Too far, giving up --><xsl:comment>555</xsl:comment>
			</xsl:when>
			<xsl:when test="not(exists($this/@ss:Index)) and not(exists(//excel:Row[$pos]/excel:Cell[$col - 1]/@ss:Index)) and not(exists(//excel:Row[$pos]/excel:Cell[$col - 2]/@ss:Index)) and //excel:Row[$pos]/excel:Cell[$col - 2]/@ss:Index &gt; 1">
				<!-- Too far, giving up --><xsl:comment>666</xsl:comment>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="is-column-4">
					<xsl:choose>
						<xsl:when test="$col = 4">
							<xsl:text>true</xsl:text>
						</xsl:when>
						<xsl:when test="$this/@ss:Index = '4'">
							<xsl:text>true</xsl:text>
						</xsl:when>
						<xsl:when test="not(exists($this/@ss:Index)) and //excel:Row[$pos]/excel:Cell[$col - 1]/@ss:Index = '3'">
							<xsl:text>true</xsl:text>
						</xsl:when>
						<xsl:when test="not(exists($this/@ss:Index)) and not(exists(//excel:Row[$pos]/excel:Cell[$col - 1]/@ss:Index)) and //excel:Row[$pos]/excel:Cell[$col - 2]/@ss:Index = '2'">
							<xsl:text>true</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>false</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:comment>ic4: <xsl:value-of select="$is-column-4"/></xsl:comment>
				<xsl:choose>
					<xsl:when test="$is-column-4 = 'true' and normalize-space($this/excel:Data) != '' and normalize-space($this/excel:Data) != 'Abteilung'">
						
						<xsl:call-template name="write-affiliation">
							<xsl:with-param name="ouname" select="normalize-space(//excel:Row[$pos]/excel:Cell[$col]/excel:Data)"/>
							<xsl:with-param name="familyname" select="$familyname"/>
							<xsl:with-param name="givenname" select="$givenname"/>
						</xsl:call-template>
						
						<xsl:if test="($direction = 'both' or $direction = 'backward') and not(exists(//excel:Row[$pos]/@ss:Index))">
							<xsl:comment>calling backward</xsl:comment>
							<xsl:call-template name="get-affiliations">
								<xsl:with-param name="col" select="1"/>
								<xsl:with-param name="pos" select="$pos - 1"/>
								<xsl:with-param name="familyname" select="$familyname"/>
								<xsl:with-param name="givenname" select="$givenname"/>
								<xsl:with-param name="direction" select="'backward'"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="($direction = 'both' or $direction = 'forward') and not(exists(//excel:Row[$pos + 1]/@ss:Index))">
							<xsl:comment>calling forward</xsl:comment>
							<xsl:call-template name="get-affiliations">
								<xsl:with-param name="col" select="1"/>
								<xsl:with-param name="pos" select="$pos + 1"/>
								<xsl:with-param name="familyname" select="$familyname"/>
								<xsl:with-param name="givenname" select="$givenname"/>
								<xsl:with-param name="direction" select="'forward'"/>
							</xsl:call-template>
						</xsl:if>
						
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="get-affiliations">
							<xsl:with-param name="col" select="$col + 1"/>
							<xsl:with-param name="pos" select="$pos"/>
							<xsl:with-param name="familyname" select="$familyname"/>
							<xsl:with-param name="givenname" select="$givenname"/>
							<xsl:with-param name="direction" select="$direction"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>
	
	<xsl:template name="write-affiliation">
		<xsl:param name="ouname"/>
		<xsl:param name="familyname"/>
		<xsl:param name="givenname"/>
		<escidoc:position>
			<rdf:Description>
				<xsl:variable name="escidoc-ou">
					<xsl:value-of select="$ou-list/srw:searchRetrieveResponse/srw:records/srw:record[normalize-space(srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title) = $ouname or normalize-space(srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/mdr:md-records/mdr:md-record/mdou:organizational-unit/dcterms:alternative[1]) = $ouname]/srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/substring-after(substring-after(substring-after(@xlink:href, '/'), '/'), '/')"/>
				</xsl:variable>
				<xsl:text>&#xa;</xsl:text>
				<xsl:comment>Found ID: "<xsl:value-of select="$escidoc-ou"/>" for Name: "<xsl:value-of select="$ouname"/>"</xsl:comment>
				<xsl:text>&#xa;</xsl:text>
				<xsl:variable name="ou-path">
					<xsl:call-template name="get-ou-path">
						<xsl:with-param name="id" select="$escidoc-ou"/>
						<xsl:with-param name="familyname" select="$familyname"/>
						<xsl:with-param name="givenname" select="$givenname"/>
						<xsl:with-param name="ouname" select="$ouname"/>
					</xsl:call-template>
				</xsl:variable>
			
				<eprint:affiliatedInstitution>
					<xsl:value-of select="$ou-path"/>
				</eprint:affiliatedInstitution>
				
				<xsl:choose>
				    <xsl:when test="$escidoc-ou != ''" >
				        <dc:identifier>
					    <xsl:value-of select="$escidoc-ou"/>
					</dc:identifier>
				    </xsl:when>
				    <xsl:when test="$escidoc-ou = '' and $import-name = 'MPIDynamics'" >
				        <dc:identifier>
					    <xsl:value-of select="$external-ou-id"/>
					</dc:identifier>
				    </xsl:when>
				</xsl:choose>
			</rdf:Description>
		</escidoc:position>
	</xsl:template>
	
	<xsl:template name="get-ou-path">
		<xsl:param name="id"/>
		<xsl:param name="familyname"/>
		<xsl:param name="givenname"/>
		<xsl:param name="ouname"/>
		
		<xsl:variable name="ou" select="$ou-list/srw:searchRetrieveResponse/srw:records/srw:record/srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit[substring-after(substring-after(substring-after(@xlink:href, '/'), '/'), '/') = $id]"/>
	
		<xsl:choose>
			<xsl:when test="$ouname != ''">
				<xsl:value-of select="$ouname"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="normalize-space($ou/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title)"/>
			</xsl:otherwise>
		</xsl:choose>
		
		<xsl:if test="normalize-space($ou/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title) = '' and not($import-name = 'MPIDynamics')">
			<xsl:message>ERROR with "<xsl:value-of select="$ouname"/>" for <xsl:value-of select="$familyname"/>,  <xsl:value-of select="$givenname"/> at <xsl:value-of select="$id"/></xsl:message>
			ERROR with "<xsl:value-of select="$ouname"/>" for <xsl:value-of select="$familyname"/>,  <xsl:value-of select="$givenname"/> at <xsl:value-of select="$id"/>
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
		<xsl:param name="direction"/>
		<xsl:choose>
			<xsl:when test="//excel:Row[$pos] = $main">
				<xsl:call-template name="alternative-name">
					<xsl:with-param name="pos" select="$pos + 1"/>
					<xsl:with-param name="main" select="$main"/>
					<xsl:with-param name="direction">forward</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="alternative-name">
					<xsl:with-param name="pos" select="$pos - 1"/>
					<xsl:with-param name="main" select="$main"/>
					<xsl:with-param name="direction">backward</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="string-length(//excel:Row[$pos]/excel:Cell[1]/excel:Data) = 0"/>
			<xsl:when test="exists(//excel:Row[$pos]/excel:Cell[1]/@ss:Index)"/>
			<xsl:when test="$direction = 'forward' and //excel:Row[$pos]/@ss:Index != ''"/>
			<xsl:when test="$direction = 'backward' and //excel:Row[$pos + 1]/@ss:Index != ''"/>
			<xsl:when test="normalize-space(//excel:Row[$pos]/excel:Cell[1]/excel:Data) != 'Nachname'">
				<dcterms:alternative>
					<xsl:value-of select="normalize-space(//excel:Row[$pos]/excel:Cell[1]/excel:Data)"/>
					<xsl:text>, </xsl:text>
					<xsl:value-of select="normalize-space(//excel:Row[$pos]/excel:Cell[2]/excel:Data)"/>
				</dcterms:alternative>
				<xsl:if test="$direction = 'forward'">
					<xsl:call-template name="alternative-name">
						<xsl:with-param name="pos" select="$pos + 1"/>
						<xsl:with-param name="main" select="$main"/>
						<xsl:with-param name="direction">forward</xsl:with-param>
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="$direction = 'backward'">
					<xsl:call-template name="alternative-name">
						<xsl:with-param name="pos" select="$pos - 1"/>
						<xsl:with-param name="main" select="$main"/>
						<xsl:with-param name="direction">backward</xsl:with-param>
					</xsl:call-template>
				</xsl:if>
			</xsl:when>
		</xsl:choose>
	
	</xsl:template>


</xsl:stylesheet>