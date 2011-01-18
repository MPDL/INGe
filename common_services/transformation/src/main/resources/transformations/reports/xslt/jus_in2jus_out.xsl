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
	xmlns:s="http://sorting">

	<xsl:output method="xml" indent="yes" 
		encoding="UTF-8" />

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
		
		<xsl:element name="html">
			<xsl:element name="head">
				<xsl:element name="meta">
					<xsl:attribute name="http-equiv" select="'Content-Type'"/>
					<xsl:attribute name="content" select="'text/html; charset=utf-8'"/>
				</xsl:element>
			</xsl:element>
			<xsl:element name="body">
				<xsl:element name="div">
				<xsl:attribute name="class" select="'Pubman'"/>
					<xsl:element name="div">
					<xsl:attribute name="class" select="'Authorship'"/>
					<xsl:text>BERICHT InstitutsId: </xsl:text> <xsl:value-of select="$institutsId"></xsl:value-of> <xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text><xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
					<xsl:text>AUTORENSCHAFTEN </xsl:text><xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
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
									<xsl:element name="p">
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
									</xsl:element>
								</xsl:when>
							</xsl:choose>
						</xsl:for-each>
						</xsl:element>
						
						<xsl:element name="div">
							<xsl:attribute name="class" select="'Editorship'"/>
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
							<xsl:choose>
								<xsl:when test="($firstEditorListCount &gt; 0) or ($secondEditorListCount &gt; 0) ">
									<xsl:text>HERAUSGEBERSCHAFTEN</xsl:text> <xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
								</xsl:when>
							</xsl:choose>	
							<xsl:element name="div">
								<xsl:attribute name="class" select="'Editorship_1'"/>
								<xsl:choose>
									<xsl:when test="$firstEditorListCount &gt; 0">
										<xsl:text>SAMMEL- UND TAGUNGSBÄNDE/HERAUSGEBER- UND VERFASSUNGSWERKE</xsl:text><xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
									</xsl:when>
								</xsl:choose>		
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
											and publication:publication/eterms:creator/@role=$editorRole]) &gt; 0">
												<xsl:element name="p">
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
											</xsl:element>
										</xsl:when>
									</xsl:choose>
								</xsl:for-each>
						</xsl:element>	
						<xsl:element name="div">
							<xsl:attribute name="class" select="'Editorship_2'"/>
							<xsl:choose>
								<xsl:when test="$secondEditorListCount &gt; 0">
									<xsl:text>ZEITSCHRIFTEN, SCHRIFTENREIHEN, MATERIAL- UND GESETZESSAMMLUNGEN</xsl:text><xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
								</xsl:when>
							</xsl:choose>			
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
										<xsl:element name="p">
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
										</xsl:element>
									</xsl:when>
								</xsl:choose>
							</xsl:for-each>
							</xsl:element>	 <!-- end of editor_2 div tag -->
						</xsl:element> <!-- end of editor div tag -->
				</xsl:element>  <!-- end of div tag -->
			</xsl:element>  <!-- end of body tag -->
		</xsl:element> <!-- end of html tag -->
		
			
	</xsl:template>

	<xsl:template match="escidocItem:item" mode="sortedAuthorList">
		<xsl:param name="authorName" />
		<xsl:param name="authorCitationName"/>
	
		<xsl:variable name="authorString"
			select="string-join((escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/eterms:creator/person:person/eterms:concat-complete-name[. != $authorName]), '; ')" />
		
		<xsl:variable name="pos" select="position()" />
		
		<xsl:variable name="is-first"
			select="not(exists(../escidocItem:item[position() &lt; $pos][(string-join((escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/eterms:creator/person:person/eterms:concat-complete-name[. != $authorName]), '; ') = $authorString)]))" />
		
		<xsl:choose>
			<xsl:when test="$is-first">
				<xsl:choose>
					<!-- when single author write the citation style name of the author in italic style -->
					<xsl:when test="string-length($authorString) = 0">
						<xsl:text disable-output-escaping="yes">&lt;span class="Italic"&gt;&lt;i&gt;</xsl:text>
							<xsl:value-of select="$authorCitationName" />
						<xsl:text disable-output-escaping="yes">&lt;/i&gt;&lt;/span&gt;</xsl:text>
					</xsl:when>
					<!--
						when more than one author write the citation style name of the
						first author and the other author separated with semicolon. When this is the first
						publication in the list, write the name of the author in italic style.
					-->
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$pos = 1">
								<xsl:text disable-output-escaping="yes">&lt;span class="Italic"&gt;&lt;i&gt;</xsl:text>
								<xsl:value-of select="$authorCitationName" />
								<xsl:text disable-output-escaping="yes">&lt;/i&gt;&lt;/span&gt;</xsl:text>; <xsl:value-of select="$authorString" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$authorCitationName" />; <xsl:value-of select="$authorString" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of>-</xsl:value-of>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:variable name="bibCitation" select="escidocItem:properties/prop:content-model-specific/dcterms:bibliographicCitation"/>
		<xsl:choose>
			<xsl:when test="contains($bibCitation, 'span class')">
				<xsl:variable name="afterOccurance" select="substring-after($bibCitation, '&lt;/span&gt;')"/>
				<xsl:variable name="beforeOccurance" select="substring-before($bibCitation, '&lt;span class')"/>
				<xsl:variable name="occurance" select="substring-after(substring-before($bibCitation, '&lt;/'), '&gt;')"/>		
				<xsl:value-of select="$beforeOccurance"/> 
				<xsl:text disable-output-escaping="yes">&lt;span class="Italic"&gt;&lt;i&gt;</xsl:text>
				<xsl:value-of select="$occurance"/>
				<xsl:text disable-output-escaping="yes">&lt;/i&gt;&lt;/span&gt;</xsl:text>
				<xsl:value-of select="$afterOccurance"/>			
			</xsl:when>
			<xsl:otherwise>		
				<xsl:value-of select="$bibCitation"/>			
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
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