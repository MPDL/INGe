<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:ec="${xsd.soap.item.components}"
	xmlns:ei="${xsd.soap.item.item}"
	xmlns:eprints="http://purl.org/eprint/terms/"
	xmlns:escidoc="${xsd.metadata.terms}"
	xmlns:escidocFunctions="urn:escidoc:functions"
	xmlns:escidocItem="${xsd.soap.item.item}"
	xmlns:escidocItemList="${xsd.soap.item.itemlist}"
	xmlns:escidocMetadataRecords="${xsd.soap.common.metadatarecords}"
	xmlns:eterms="${xsd.metadata.terms}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:file="${xsd.metadata.file}"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:foaf="http://xmlns.com/foaf/0.1/"
	xmlns:itemlist="${xsd.soap.item.itemlist}"
	xmlns:mdp="${xsd.metadata.escidocprofile}"
	xmlns:mdr="${xsd.soap.common.mdrecords}"
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:prop="${xsd.core.properties}"
	xmlns:pub="${xsd.metadata.publication}"
	xmlns:publication="${xsd.metadata.publication}"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:release="${xsd.soap.common.release}"
	xmlns:reportFunctions="urn:escidoc:functions"
	xmlns:s="http://sorting"
	xmlns:source="${xsd.metadata.source}"
	xmlns:srel="${xsd.soap.common.srel}"
 xmlns:Util="https://pubman.mpdl.mpg.de/util-functions"
	xmlns:version="${xsd.soap.common.version}"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output encoding="UTF-8" indent="yes" method="xml"></xsl:output>
	<xsl:variable name="authorRole" select="'http://www.loc.gov/loc.terms/relators/AUT'"></xsl:variable>
	<xsl:variable name="editorRole" select="'http://www.loc.gov/loc.terms/relators/EDT'"></xsl:variable>
	<xsl:variable name="genreArticle" select="'http://purl.org/escidoc/metadata/ves/publication-types/article'"></xsl:variable>
	<xsl:variable name="genreMonograph" select="'http://purl.org/escidoc/metadata/ves/publication-types/monograph'"></xsl:variable>
	<xsl:variable name="genreCollectedEdition" select="'http://purl.org/escidoc/metadata/ves/publication-types/collected-edition'"></xsl:variable>
	<xsl:variable name="genreCommentary" select="'http://purl.org/escidoc/metadata/ves/publication-types/commentary'"></xsl:variable>
	<xsl:variable name="genreHandbook" select="'http://purl.org/escidoc/metadata/ves/publication-types/handbook'"></xsl:variable>
	<xsl:variable name="genreProceedings" select="'http://purl.org/escidoc/metadata/ves/publication-types/proceedings'"></xsl:variable>
	<xsl:variable name="genreJournal" select="'http://purl.org/escidoc/metadata/ves/publication-types/journal'"></xsl:variable>
	<xsl:variable name="genreSeries" select="'http://purl.org/escidoc/metadata/ves/publication-types/series'"></xsl:variable>
	<xsl:variable name="genreFestschrift" select="'http://purl.org/escidoc/metadata/ves/publication-types/festschrift'"></xsl:variable>
	<xsl:param name="institutsId"></xsl:param>
	<xsl:param name="conePersonsIdIdentifier"></xsl:param>
	<xsl:key match="transformations/reports/conf/reportSortOrder.xml/s:sortorder/s:authorship/s:genre/@priority" name="authorshipGenreOrder" use="../s:name"></xsl:key>
	<xsl:key match="transformations/reports/conf/reportSortOrder.xml/s:sortorder/s:first_editorship/s:genre/@priority" name="firstListEditorshipGenreOrder" use="../s:name"></xsl:key>
	<xsl:key match="transformations/reports/conf/reportSortOrder.xml/s:sortorder/s:second_editorship/s:genre/@priority" name="secondListEditorshipGenreOrder" use="../s:name"></xsl:key>
	<xsl:template match="escidocItemList:item-list">
		<xsl:variable name="coneResult" select="Util:queryReportPersonCone('persons', $institutsId)/cone"></xsl:variable>
		<xsl:variable name="item-list">
			<xsl:apply-templates mode="sort-creators"></xsl:apply-templates>
		</xsl:variable>
		<xsl:element name="html">
			<xsl:element name="head">
				<xsl:element name="meta">
					<xsl:attribute name="http-equiv" select="'Content-Type'"></xsl:attribute>
					<xsl:attribute name="content" select="'text/html; charset=utf-8'"></xsl:attribute>
				</xsl:element>
			</xsl:element>
			<xsl:element name="body">
				<xsl:element name="div">
					<xsl:attribute name="class" select="'Pubman'"></xsl:attribute>
					<xsl:element name="div">
						<xsl:attribute name="class" select="'Authorship'"></xsl:attribute>
						<xsl:text>BERICHT InstitutsId: </xsl:text>
						<xsl:value-of select="$institutsId"></xsl:value-of>
						<xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
						<xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
						<xsl:text>AUTORENSCHAFTEN </xsl:text>
						<xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
						<xsl:for-each select="$coneResult/rdf:RDF/rdf:Description">
							<xsl:sort select="foaf:family_name"></xsl:sort>
							<xsl:sort select="foaf:givenname"></xsl:sort>
							<xsl:variable name="currentAuthorId" select="concat($conePersonsIdIdentifier, substring-after(@rdf:about, $conePersonsIdIdentifier))" />
							<xsl:variable name="currentAuthorName" select="concat(foaf:givenname, ' ', foaf:family_name)"></xsl:variable>
							<xsl:variable name="currentAuthorCitationStyleName" select="concat(foaf:family_name, ', ', foaf:givenname)"></xsl:variable>
							<xsl:choose>
								<xsl:when test="count($item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentAuthorId and publication:publication/eterms:creator/@role = $authorRole]) &gt; 0">
									<xsl:element name="p">
										<xsl:variable name="currentAuthorList">
											<xsl:for-each select="$item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentAuthorId and publication:publication/eterms:creator/@role = $authorRole]">
												<xsl:sort select="string-join((publication:publication/eterms:creator[person:person/eterms:concat-complete-name != $currentAuthorName]/person:person/eterms:sort-name), '; ')"></xsl:sort>
												<xsl:sort data-type="number" select="key('authorshipGenreOrder', publication:publication/@type)"></xsl:sort>
												<xsl:copy-of select="../.."></xsl:copy-of>
											</xsl:for-each>
										</xsl:variable>
										<xsl:apply-templates mode="sortedAuthorList" select="$currentAuthorList">
											<xsl:with-param name="authorName" select="$currentAuthorName"></xsl:with-param>
											<xsl:with-param name="authorCitationName" select="$currentAuthorCitationStyleName"></xsl:with-param>
										</xsl:apply-templates>
									</xsl:element>
								</xsl:when>
							</xsl:choose>
						</xsl:for-each>
					</xsl:element>
					<xsl:element name="div">
						<xsl:attribute name="class" select="'Editorship'"></xsl:attribute>
						<xsl:variable name="firstEditorListCount"  select="count($item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/@role = $editorRole and (publication:publication/@type = $genreCollectedEdition or publication:publication/@type = $genreMonograph or publication:publication/@type = $genreCommentary or publication:publication/@type = $genreHandbook or publication:publication/@type = $genreProceedings or publication:publication/@type = $genreFestschrift)])"></xsl:variable>
						<xsl:variable name="secondEditorListCount" select="count($item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/@role = $editorRole and (publication:publication/@type = $genreJournal or publication:publication/@type = $genreSeries)])"></xsl:variable>
						<xsl:choose>
							<xsl:when test="($firstEditorListCount &gt; 0) or ($secondEditorListCount &gt; 0)">
								<xsl:text>HERAUSGEBERSCHAFTEN</xsl:text>
								<xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
							</xsl:when>
						</xsl:choose>
						<xsl:element name="div">
							<xsl:attribute name="class" select="'Editorship_1'"></xsl:attribute>
							<xsl:choose>
								<xsl:when test="$firstEditorListCount &gt; 0">
									<xsl:text>SAMMEL- UND TAGUNGSBÄNDE/HERAUSGEBER- UND VERFASSUNGSWERKE/FESTSCHRIFTEN</xsl:text>
									<xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
								</xsl:when>
							</xsl:choose>
							<xsl:for-each select="$coneResult/rdf:RDF/rdf:Description">
								<xsl:sort select="foaf:family_name"></xsl:sort>
								<xsl:sort select="foaf:givenname"></xsl:sort>
								<xsl:variable name="currentFirstEditorId" select="concat($conePersonsIdIdentifier, substring-after(@rdf:about, $conePersonsIdIdentifier))"></xsl:variable>
								<xsl:variable name="currentFirstEditorName" select="concat(foaf:givenname, ' ', foaf:family_name)"></xsl:variable>
								<xsl:variable name="currentFirstEditorCitationStyleName" select="concat(foaf:family_name, ', ', foaf:givenname)"></xsl:variable>
								<xsl:choose>
									<xsl:when test="count($item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentFirstEditorId and publication:publication/eterms:creator/@role = $editorRole]) &gt; 0">
										<xsl:element name="p">
											<xsl:variable name="currentFirstEditorList">
												<xsl:for-each select="$item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentFirstEditorId and publication:publication/eterms:creator/@role = $editorRole and (publication:publication/@type = $genreCollectedEdition or publication:publication/@type = $genreMonograph or publication:publication/@type = $genreCommentary or publication:publication/@type = $genreHandbook or publication:publication/@type = $genreProceedings or publication:publication/@type = $genreFestschrift)]">
													<xsl:sort select="string-join((publication:publication/eterms:creator[person:person/eterms:concat-complete-name != $currentFirstEditorName]/person:person/eterms:sort-name), '; ')"></xsl:sort>
													<xsl:sort data-type="number" select="key('firstListEditorshipGenreOrder', publication:publication/@type)"></xsl:sort>
													<xsl:copy-of select="../.."></xsl:copy-of>
												</xsl:for-each>
											</xsl:variable>
											<xsl:apply-templates mode="sortedAuthorList" select="$currentFirstEditorList">
												<xsl:with-param name="authorName" select="$currentFirstEditorName"></xsl:with-param>
												<xsl:with-param name="authorCitationName" select="$currentFirstEditorCitationStyleName"  ></xsl:with-param>
											</xsl:apply-templates>
										</xsl:element>
									</xsl:when>
								</xsl:choose>
							</xsl:for-each>
						</xsl:element>
						<xsl:element name="div">
							<xsl:attribute name="class" select="'Editorship_2'"></xsl:attribute>
							<xsl:choose>
								<xsl:when test="$secondEditorListCount &gt; 0">
									<xsl:text>ZEITSCHRIFTEN, SCHRIFTENREIHEN, MATERIAL- UND GESETZESSAMMLUNGEN</xsl:text>
									<xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
								</xsl:when>
							</xsl:choose>
							<xsl:for-each select="$coneResult/rdf:RDF/rdf:Description">
								<xsl:sort select="foaf:family_name"></xsl:sort>
								<xsl:sort select="foaf:givenname"></xsl:sort>
								<xsl:variable name="currentSecondEditorId" select="concat($conePersonsIdIdentifier, substring-after(@rdf:about, $conePersonsIdIdentifier))"></xsl:variable>
								<xsl:variable name="currentSecondEditorName" select="concat(foaf:givenname, ' ', foaf:family_name)"></xsl:variable>
								<xsl:variable name="currentSecondEditorCitationStyleName" select="concat(foaf:family_name, ', ', foaf:givenname)"></xsl:variable>
								<xsl:choose>
									<xsl:when test="count($item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentSecondEditorId and publication:publication/eterms:creator/@role = $editorRole and (publication:publication/@type = $genreJournal or publication:publication/@type = $genreSeries)]) &gt; 0">
										<xsl:element name="p">
											<xsl:variable name="currentSecondEditorList">
												<xsl:for-each select="$item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentSecondEditorId and publication:publication/eterms:creator/@role = $editorRole and (publication:publication/@type = $genreJournal or publication:publication/@type = $genreSeries)]">
													<xsl:sort select="string-join((publication:publication/eterms:creator[person:person/eterms:concat-complete-name != $currentSecondEditorName]/person:person/eterms:sort-name), '; ')"></xsl:sort>
													<xsl:sort data-type="number" select="key('secondListEditorshipGenreOrder', publication:publication/@type)"></xsl:sort>
													<xsl:copy-of select="../.."></xsl:copy-of>
												</xsl:for-each>
											</xsl:variable>
											<xsl:apply-templates mode="sortedAuthorList" select="$currentSecondEditorList">
												<xsl:with-param name="authorName" select="$currentSecondEditorName"></xsl:with-param>
												<xsl:with-param name="authorCitationName" select="$currentSecondEditorCitationStyleName"  ></xsl:with-param>
											</xsl:apply-templates>
										</xsl:element>
									</xsl:when>
								</xsl:choose>
							</xsl:for-each>
						</xsl:element>
						<!-- end of editor_2 div tag -->
					</xsl:element>
					<!-- end of editor div tag -->
				</xsl:element>
				<!-- end of div tag -->
			</xsl:element>
			<!-- end of body tag -->
		</xsl:element>
		<!-- end of html tag -->
	</xsl:template>
	<xsl:template match="escidocItem:item" mode="sortedAuthorList">
		<xsl:param name="authorName"></xsl:param>
		<xsl:param name="authorCitationName"></xsl:param>
		<xsl:variable name="authorString" select="string-join((escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/eterms:creator/person:person/eterms:concat-complete-name[. != $authorName]), '; ')"></xsl:variable>
		<xsl:variable name="pos" select="position()"></xsl:variable>
		<xsl:variable name="is-first" select="not(exists(../escidocItem:item[position() &lt; $pos][(string-join((escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/eterms:creator/person:person/eterms:concat-complete-name[. != $authorName]), '; ') = $authorString)]))"></xsl:variable>
		<xsl:choose>
			<xsl:when test="$is-first">
				<xsl:choose>
					<!-- when single author write the citation style name of the author in italic style -->
					<xsl:when test="string-length($authorString) = 0">
						<xsl:text disable-output-escaping="yes">&lt;span class="Italic"&gt;&lt;i&gt;</xsl:text>
						<xsl:value-of select="$authorCitationName"></xsl:value-of>
						<xsl:text disable-output-escaping="yes">&lt;/i&gt;&lt;/span&gt;</xsl:text>, 
					</xsl:when>
					<!--  when more than one author write the citation style name of the  first author and the other author separated with semicolon. When this is the first  publication in the list, write the name of the author in italic style.  -->
					<xsl:otherwise>
						<xsl:text disable-output-escaping="yes">&lt;span class="Italic"&gt;&lt;i&gt;</xsl:text>
						<xsl:value-of select="$authorCitationName"></xsl:value-of>; 
						<xsl:value-of select="$authorString"></xsl:value-of>,
						<xsl:text disable-output-escaping="yes">&lt;/i&gt;&lt;/span&gt;</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of>- </xsl:value-of>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:variable name="bibCitation" select="escidocItem:properties/prop:content-model-specific/dcterms:bibliographicCitation"></xsl:variable>
		<xsl:choose>
			<xsl:when test="contains($bibCitation, 'span class')">
				<xsl:variable name="afterOccurance" select="substring-after($bibCitation, '&lt;/span&gt;')"></xsl:variable>
				<xsl:variable name="beforeOccurance" select="substring-before($bibCitation, '&lt;span class')"></xsl:variable>
				<xsl:variable name="occurance" select="substring-after(substring-before($bibCitation, '&lt;/'), '&gt;')"></xsl:variable>
				<xsl:value-of disable-output-escaping="yes" select="$beforeOccurance"></xsl:value-of>
				<xsl:text disable-output-escaping="yes">&lt;span class="Italic"&gt;&lt;i&gt;</xsl:text>
				<xsl:value-of disable-output-escaping="yes" select="$occurance"></xsl:value-of>
				<xsl:text disable-output-escaping="yes">&lt;/i&gt;&lt;/span&gt;</xsl:text>
				<xsl:value-of disable-output-escaping="yes" select="$afterOccurance"></xsl:value-of>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of disable-output-escaping="yes" select="$bibCitation"></xsl:value-of>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
	</xsl:template>
	<xsl:template match="*" mode="sort-creators">
		<xsl:copy>
			<xsl:copy-of select="@*"></xsl:copy-of>
			<xsl:apply-templates mode="sort-creators"></xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="publication:publication" mode="sort-creators">
		<xsl:copy>
			<xsl:copy-of select="@*"></xsl:copy-of>
			<xsl:variable name="creators">
				<xsl:perform-sort select="eterms:creator">
					<xsl:sort select="person:person/eterms:family-name"></xsl:sort>
					<xsl:sort select="person:person/eterms:given-name"></xsl:sort>
				</xsl:perform-sort>
			</xsl:variable>
			<xsl:apply-templates mode="sort-creators" select="$creators"></xsl:apply-templates>
			<xsl:apply-templates mode="sort-creators" select="*[name() != 'eterms:creator']"></xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="person:person" mode="sort-creators">
		<xsl:copy>
			<xsl:copy-of select="@*"></xsl:copy-of>
			<eterms:sort-name>
				<xsl:value-of select="eterms:family-name"></xsl:value-of>
				<xsl:text>, </xsl:text>
				<xsl:value-of select="eterms:given-name"></xsl:value-of>
			</eterms:sort-name>
			<eterms:concat-complete-name>
				<xsl:value-of select="eterms:given-name"></xsl:value-of>
				<xsl:text> </xsl:text>
				<xsl:value-of select="eterms:family-name"></xsl:value-of>
			</eterms:concat-complete-name>
			<xsl:apply-templates mode="sort-creators"></xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>