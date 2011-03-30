<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions" 
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/" 
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:ei="${xsd.soap.item.item}" 
	xmlns:mdr="${xsd.soap.common.mdrecords}"
	xmlns:mdp="${xsd.metadata.escidocprofile}" 
	xmlns:ec="${xsd.soap.item.components}"
	xmlns:srel="${xsd.soap.common.srel}" 
	xmlns:version="${xsd.soap.common.version}"
	xmlns:release="${xsd.soap.common.release}" 
	xmlns:file="${xsd.metadata.file}"
	xmlns:pub="${xsd.metadata.publication}" 
	xmlns:person="${xsd.metadata.person}"
	xmlns:prop="${xsd.core.properties}" 
	xmlns:escidocItem="${xsd.soap.item.item}"
	xmlns:publication="${xsd.metadata.publication}" 
	xmlns:escidocItemList="${xsd.soap.item.itemlist}"
	xmlns:escidocMetadataRecords="${xsd.soap.common.metadatarecords}"
	xmlns:source="${xsd.metadata.source}" 
	xmlns:eterms="${xsd.metadata.terms}"
	xmlns:event="${xsd.metadata.event}" 
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:escidocFunctions="urn:escidoc:functions" 
	xmlns:escidoc="${xsd.metadata.terms}"
	xmlns:Util="java:de.mpg.escidoc.services.transformation.Util"
	xmlns:reportFunctions="urn:escidoc:functions" 
	xmlns:itemlist="${xsd.soap.item.itemlist}"
	xmlns:eprints="http://purl.org/eprint/terms/" 
	xmlns:foaf="http://xmlns.com/foaf/0.1/"
	xmlns:s="http://sorting"
	xmlns:aid="http://ns.adobe.com/AdobeInDesign/4.0/">

	<xsl:output method="xml" indent="no" 
		encoding="UTF-8" />
		
	<xsl:param name="indesign-namespace" select="'http://ns.adobe.com/AdobeInDesign/4.0/'"/>

	<xsl:variable name="authorRole"
		select="'http://www.loc.gov/loc.terms/relators/AUT'" />
	<xsl:variable name="editorRole"
		select="'http://www.loc.gov/loc.terms/relators/EDT'" />
	
	<xsl:variable name="genreArticle"
		select="'http://purl.org/escidoc/metadata/ves/publication-types/article'" />
	<xsl:variable name="genreMonograph"
		select="'http://purl.org/escidoc/metadata/ves/publication-types/monograph'" />
	<xsl:variable name="genreCollectedEdition"
		select="'http://purl.org/escidoc/metadata/ves/publication-types/collected-edition'" />
	<xsl:variable name="genreCommentary"
		select="'http://purl.org/escidoc/metadata/ves/publication-types/commentary'" />
	<xsl:variable name="genreHandbook"
		select="'http://purl.org/escidoc/metadata/ves/publication-types/handbook'" />
	<xsl:variable name="genreProceedings"
		select="'http://purl.org/escidoc/metadata/ves/publication-types/proceedings'" />

	<xsl:variable name="genreJournal"
		select="'http://purl.org/escidoc/metadata/ves/publication-types/journal'" />
	<xsl:variable name="genreSeries"
		select="'http://purl.org/escidoc/metadata/ves/publication-types/series'" />

	<xsl:param name="institutsId"/>
	
	<xsl:key name="authorshipGenreOrder" match="s:sortorder/s:authorship/s:genre/@priority"
		use="../s:name" />
	<xsl:key name="firstListEditorshipGenreOrder" match="s:sortorder/s:first_editorship/s:genre/@priority"
		use="../s:name" />
	<xsl:key name="secondListEditorshipGenreOrder" match="s:sortorder/s:second_editorship/s:genre/@priority"
		use="../s:name" />
		
	<xsl:template match="escidocItemList:item-list">
		
		<xsl:variable name="coneResult"
			select="Util:queryReportPersonCone('persons',$institutsId)/cone" />

		<xsl:variable name="item-list">
			<xsl:apply-templates mode="sort-creators" />
		</xsl:variable>
		
		<xsl:element name="report">
			<xsl:namespace name="aid" select="$indesign-namespace" />
			<xsl:element name="authorship">
			<xsl:element name="h1"><xsl:attribute name="cstyle" namespace="http://ns.adobe.com/AdobeInDesign/4.0/" select="'h1'"/>Autorenschaften</xsl:element><xsl:text>
			
 </xsl:text>
			
			<xsl:for-each select="$coneResult/rdf:RDF/rdf:Description">
					<xsl:sort select="foaf:family_name" />
					<xsl:sort select="foaf:givenname" />
					<xsl:variable name="currentAuthorId" select="@rdf:about" />
					<xsl:variable name="currentAuthorName"
						select="concat(foaf:givenname, ' ', foaf:family_name)" />
					<xsl:variable name="currentAuthorCitationStyleName"
						select="concat(foaf:family_name,', ',foaf:givenname)" />
					<xsl:choose>
						<xsl:when
							test="count($item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentAuthorId 
							and publication:publication/eterms:creator/@role=$authorRole]) &gt; 0">
							<xsl:element name="p"><xsl:attribute name="aid:pstyle" select="'p'"/>
								<xsl:variable name="currentAuthorList">
									<xsl:for-each
										select="$item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentAuthorId 
							and publication:publication/eterms:creator/@role=$authorRole]">
										<xsl:sort
											select="string-join((publication:publication/eterms:creator[person:person/eterms:concat-complete-name != $currentAuthorName]/person:person/eterms:sort-name), '; ')" />
										<xsl:sort
											select="key('authorshipGenreOrder', publication:publication/@type)"
											data-type="number" />
										<xsl:copy-of select="../.."></xsl:copy-of>
									</xsl:for-each>
								</xsl:variable>
								
								<xsl:apply-templates select="$currentAuthorList"
									mode="sortedAuthorList">
									<xsl:with-param name="authorName" select="$currentAuthorName" />
									<xsl:with-param name="authorCitationName"
										select="$currentAuthorCitationStyleName" />
								</xsl:apply-templates>
								<xsl:text>
</xsl:text>
							</xsl:element>
						</xsl:when>
					</xsl:choose>
				</xsl:for-each>
			</xsl:element>
						
			<xsl:variable name="firstEditorListCount"
				select="count($item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/@role=$editorRole and
				(publication:publication/@type = $genreCollectedEdition or 
				publication:publication/@type = $genreMonograph or
				publication:publication/@type = $genreCommentary or
				publication:publication/@type = $genreHandbook or
				publication:publication/@type = $genreProceedings)])"/>
			<xsl:variable name="secondEditorListCount" 
				select="count($item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/@role=$editorRole and
				(publication:publication/@type = $genreJournal or
				publication:publication/@type = $genreSeries)])" />
			
			<xsl:element name="editorship_1">
				<xsl:element name="h1"><xsl:attribute name="aid:cstyle" select="'h1'"/>Herausgeberschaften</xsl:element><xsl:text>&#x0D;</xsl:text>
				<xsl:element name="h2"><xsl:attribute name="aid:cstyle" select="'h2'"/>Sammel- und Tagungsbände/Herausgeber- und Verfassungswerke</xsl:element><xsl:text>
					
</xsl:text>
						<xsl:for-each select="$coneResult/rdf:RDF/rdf:Description">
							<xsl:sort select="foaf:family_name" />
							<xsl:sort select="foaf:givenname" />
							<xsl:variable name="currentFirstEditorId" select="@rdf:about" />
							<xsl:variable name="currentFirstEditorName"
								select="concat(foaf:givenname, ' ', foaf:family_name)" />
							<xsl:variable name="currentFirstEditorCitationStyleName"
								select="concat(foaf:family_name,', ',foaf:givenname)" />
							<xsl:choose>
								<xsl:when
									test="count($item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentFirstEditorId 
									and publication:publication/eterms:creator/@role=$editorRole and
									(publication:publication/@type = $genreCollectedEdition or 
									publication:publication/@type = $genreMonograph or
									publication:publication/@type = $genreCommentary or
									publication:publication/@type = $genreHandbook or
									publication:publication/@type = $genreProceedings)]) &gt; 0">
										<xsl:element name="p"><xsl:attribute name="aid:pstyle" select="'p'"/>
										<xsl:variable name="currentFirstEditorList">
											<xsl:for-each
												select="$item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentFirstEditorId and 
											publication:publication/eterms:creator/@role=$editorRole and
											(publication:publication/@type = $genreCollectedEdition or 
											publication:publication/@type = $genreMonograph or
											publication:publication/@type = $genreCommentary or
											publication:publication/@type = $genreHandbook or
											publication:publication/@type = $genreProceedings)]">
												<xsl:sort
													select="string-join((publication:publication/eterms:creator[person:person/eterms:concat-complete-name != $currentFirstEditorName]/person:person/eterms:sort-name), '; ')" />
												<xsl:sort
													select="key('firstListEditorshipGenreOrder', publication:publication/@type)"
													data-type="number" />
												<xsl:copy-of select="../.."></xsl:copy-of>
											</xsl:for-each>
										</xsl:variable>
			
										<xsl:apply-templates select="$currentFirstEditorList"
											mode="sortedAuthorList">
											<xsl:with-param name="authorName" select="$currentFirstEditorName" />
											<xsl:with-param name="authorCitationName"
												select="$currentFirstEditorCitationStyleName" />
										</xsl:apply-templates>
										<xsl:text>
</xsl:text>
									</xsl:element>
								</xsl:when>
							</xsl:choose>
						</xsl:for-each>
			</xsl:element>	
			<xsl:element name="editorship_2">
				<xsl:element name="h2"><xsl:attribute name="aid:cstyle" select="'h2'"/>Zeitschriften, Schriftenreihen, Material- und Gesetzessamlungen</xsl:element><xsl:text>
</xsl:text>	
				<xsl:for-each select="$coneResult/rdf:RDF/rdf:Description">
					<xsl:sort select="foaf:family_name" />
					<xsl:sort select="foaf:givenname" />
					<xsl:variable name="currentSecondEditorId" select="@rdf:about" />
					<xsl:variable name="currentSecondEditorName"
						select="concat(foaf:givenname, ' ', foaf:family_name)" />
					<xsl:variable name="currentSecondEditorCitationStyleName"
						select="concat(foaf:family_name,', ',foaf:givenname)" />
					<xsl:choose>
						<xsl:when
							test="count($item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentSecondEditorId
							and publication:publication/eterms:creator/@role=$editorRole 
							and (publication:publication/@type = $genreJournal 
							or publication:publication/@type = $genreSeries)]) &gt; 0">
							<xsl:element name="p"><xsl:attribute name="aid:pstyle" select="'p'"/>
								<xsl:variable name="currentSecondEditorList">
									<xsl:for-each
										select="$item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentSecondEditorId and 
									publication:publication/eterms:creator/@role=$editorRole and
									(publication:publication/@type = $genreJournal or
									publication:publication/@type = $genreSeries)]">
										<xsl:sort
											select="string-join((publication:publication/eterms:creator[person:person/eterms:concat-complete-name != $currentSecondEditorName]/person:person/eterms:sort-name), '; ')" />
										<xsl:sort
											select="key('secondListEditorshipGenreOrder', publication:publication/@type)"
											data-type="number" />
										<xsl:copy-of select="../.."></xsl:copy-of>
									</xsl:for-each>
								</xsl:variable>
	
								<xsl:apply-templates select="$currentSecondEditorList"
									mode="sortedAuthorList">
									<xsl:with-param name="authorName" select="$currentSecondEditorName" />
									<xsl:with-param name="authorCitationName"
										select="$currentSecondEditorCitationStyleName" />
								</xsl:apply-templates>
								<xsl:text>
</xsl:text>
							</xsl:element>
						</xsl:when>
					</xsl:choose>
				</xsl:for-each>
				</xsl:element>	 <!-- end of editor_2 div tag -->
		</xsl:element> <!-- end of report tag -->
		
			
	</xsl:template>
	
		<xsl:template match="escidocItem:item" mode="sortedAuthorList">
			<xsl:param name="authorName" />
			<xsl:param name="authorCitationName"/>
		
			<xsl:variable name="authorString"
				select="string-join((escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/eterms:creator/person:person/eterms:concat-complete-name[. != $authorName]), '; ')" />
			
			<xsl:variable name="pos" select="position()" />
			
			<xsl:variable name="is-first"
				select="not(exists(../escidocItem:item[position() &lt; $pos][(string-join((escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/eterms:creator/person:person/eterms:concat-complete-name[. != $authorName]), '; ') = $authorString)]))" />
			
			<xsl:element name="publication"><xsl:attribute name="aid:pstyle" select="'publication'"/>
				<xsl:choose>
					<xsl:when test="$is-first">
						<xsl:choose>
							<!-- when single author write the citation style name of the author in italic style -->
							<xsl:when test="string-length($authorString) = 0">
								<xsl:element name="author-name">
									<xsl:attribute name="aid:cstyle" select="'italics'"/>
										<xsl:value-of select="$authorCitationName" /></xsl:element>, </xsl:when>
							<!--
								when more than one author write the citation style name of the
								first author and the other author separated with semicolon. When this is the first
								publication in the list, write the name of the author in italic style.
							-->
							<xsl:otherwise>
								<xsl:element name="author-name">
									<xsl:attribute name="aid:cstyle" select="'italics'"/>
										<xsl:value-of select="$authorCitationName" />; <xsl:value-of select="$authorString" /></xsl:element>, </xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>&#8211;	</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:variable name="bibCitation" select="escidocItem:properties/prop:content-model-specific/dcterms:bibliographicCitation"/>
					<xsl:choose>
						<xsl:when test="contains($bibCitation, 'span class')">
							<xsl:variable name="afterOccurance" select="substring-after($bibCitation, '&lt;/span&gt;')"/>
							<xsl:variable name="beforeOccurance" select="substring-before($bibCitation, '&lt;span class')"/>
							<xsl:variable name="occurance" select="substring-after(substring-before($bibCitation, '&lt;/'), '&gt;')"/>		
							<xsl:value-of select="$beforeOccurance"/> 
							<xsl:element name="review-of">
								<xsl:attribute name="aid:cstyle" select="'italics'"/>
								<xsl:value-of select="$occurance"/>
							</xsl:element>
							<xsl:value-of select="$afterOccurance"/>		
						</xsl:when>
						<xsl:otherwise>		
							<xsl:value-of select="$bibCitation"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:text>&#x0D;</xsl:text>
			</xsl:element>
		</xsl:template>
	

	<xsl:template match="*" mode="sort-creators">
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates mode="sort-creators" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="publication:publication" mode="sort-creators">
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<xsl:variable name="creators">
				<xsl:perform-sort select="eterms:creator">
					<xsl:sort select="person:person/eterms:family-name" />
					<xsl:sort select="person:person/eterms:given-name" />
				</xsl:perform-sort>
			</xsl:variable>
			<xsl:apply-templates select="$creators" mode="sort-creators" />
			<xsl:apply-templates select="*[name() != 'eterms:creator']"
				mode="sort-creators" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="person:person" mode="sort-creators">
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<eterms:sort-name>
				<xsl:value-of select="eterms:family-name" />
				<xsl:text>, </xsl:text>
				<xsl:value-of select="eterms:given-name" />
			</eterms:sort-name>
			<eterms:concat-complete-name>
				<xsl:value-of select="eterms:given-name" />
				<xsl:text> </xsl:text>
				<xsl:value-of select="eterms:family-name" />
			</eterms:concat-complete-name>
			<xsl:apply-templates mode="sort-creators" />
		</xsl:copy>
	</xsl:template>
	
</xsl:stylesheet>