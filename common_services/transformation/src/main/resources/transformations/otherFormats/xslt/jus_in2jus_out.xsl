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

	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes"
		encoding="UTF-8" />

	<xsl:variable name="authorRole"
		select="'http://www.loc.gov/loc.terms/relators/AUT'" />
	<xsl:variable name="editorRole"
		select="'http://www.loc.gov/loc.terms/relators/EDT'" />
	<xsl:param name="selectedRole" select="$authorRole" />

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

	<xsl:param name="institutsName" />

	<xsl:key name="autorshipGenreOrder" match="s:authorship/s:genre/@priority"
		use="../s:name" />
	<xsl:key name="firstListEditorshipGenreOrder" match="s:first_editorship/s:genre/@priority"
		use="../s:name" />
	<xsl:key name="secondListEditorshipGenreOrder" match="s:second_editorship/s:genre/@priority"
		use="../s:name" />

	<xsl:template match="escidocItemList:item-list">
		<xsl:variable name="coneResult"
			select="Util:queryCone('persons',$institutsName)/cone" />

		<xsl:variable name="item-list">
			<xsl:apply-templates mode="sort-creators" />
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$selectedRole = $authorRole">
				<xsl:element name="AUTHORS">
					<xsl:attribute name="selRole" select="$selectedRole" />
					<xsl:attribute name="instName" select="$institutsName" />
					<xsl:attribute name="totalItemsCount"
						select="count($item-list/escidocItem:item)" />

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
								<xsl:element name="AUTOR">
									<xsl:attribute name="id" select="$currentAuthorId" />
									<xsl:attribute name="name" select="$currentAuthorName" />
									<xsl:attribute name="authorListCount"
										select="count($item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentAuthorId 
								and publication:publication/eterms:creator/@role=$authorRole])" />
									<xsl:variable name="currentAuthorList">
										<xsl:for-each
											select="$item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentAuthorId 
								and publication:publication/eterms:creator/@role=$authorRole]">
											<xsl:sort
												select="string-join((publication:publication/eterms:creator[person:person/eterms:complete-name != $currentAuthorName]/person:person/eterms:sort-name), '; ')" />
											<xsl:sort
												select="key('autorshipGenreOrder', publication:publication/@type)"
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
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="EDITORS">
					<xsl:attribute name="selRole" select="$selectedRole" />
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
								<xsl:element name="EDITOR_1">
									<xsl:attribute name="id" select="$currentFirstEditorId" />
									<xsl:attribute name="name" select="$currentFirstEditorName" />
									<xsl:attribute name="firstEditorListCount"
										select="count($item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentFirstEditorId 
								and publication:publication/eterms:creator/@role=$editorRole])" />
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
												select="string-join((publication:publication/eterms:creator[person:person/eterms:complete-name != $currentFirstEditorName]/person:person/eterms:sort-name), '; ')" />
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
								<xsl:element name="EDITOR_2">
									<xsl:attribute name="id" select="$currentSecondEditorId" />
									<xsl:attribute name="name" select="$currentSecondEditorName" />
									<xsl:attribute name="editorListCount"
										select="count($item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentSecondEditorId and 
								publication:publication/eterms:creator/@role=$editorRole and
								(publication:publication/@type = $genreJournal or
								publication:publication/@type = $genreSeries)])" />
									<xsl:variable name="currentSecondEditorList">
										<xsl:for-each
											select="$item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentSecondEditorId and 
										publication:publication/eterms:creator/@role=$editorRole and
										(publication:publication/@type = $genreJournal or
										publication:publication/@type = $genreSeries)]">
											<xsl:sort
												select="string-join((publication:publication/eterms:creator[person:person/eterms:complete-name != $currentSecondEditorName]/person:person/eterms:sort-name), '; ')" />
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

				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="escidocItem:item" mode="sortedAuthorList">
		<xsl:param name="authorName" />
		<xsl:param name="authorCitationName" />
		<xsl:variable name="authorString"
			select="string-join((escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/eterms:creator/person:person/eterms:complete-name[. != $authorName]), '; ')" />
		<xsl:variable name="pos" select="position()" />
		<xsl:variable name="is-first"
			select="not(exists(../escidocItem:item[position() &lt; $pos][(string-join((escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/eterms:creator/person:person/eterms:complete-name[. != $authorName]), '; ') = $authorString)]))" />
		<xsl:choose>
			<xsl:when test="$is-first">
				<xsl:choose>
					<!-- when single author write the citation style name of the author -->
					<xsl:when test="string-length($authorString) = 0">
						<xsl:value-of select="$authorCitationName" />
					</xsl:when>
					<!--
						when more than one author write the citation style name of the
						first author and the other author separated with semicolon
					-->
					<xsl:otherwise>
						<xsl:value-of select="$authorCitationName" />
						;
						<xsl:value-of select="$authorString" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of>
					--
				</xsl:value-of>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:value-of
			select="escidocItem:properties/prop:content-model-specific/dcterms:bibliographicCitation" />
		<xsl:text> (</xsl:text>
		<xsl:value-of select="@objid"></xsl:value-of>
		<xsl:text>)</xsl:text>
		<xsl:text>
	</xsl:text>
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
			<xsl:apply-templates mode="sort-creators" />
		</xsl:copy>
	</xsl:template>


</xsl:stylesheet>