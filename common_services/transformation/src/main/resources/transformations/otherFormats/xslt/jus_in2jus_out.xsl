<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:s="http://sorting"
	xmlns:t="http://testing"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:escidocContext="http://www.escidoc.de/schemas/context/0.7" 
	xmlns:escidocContextList="http://www.escidoc.de/schemas/contextlist/0.7" 
	xmlns:escidocComponents="http://www.escidoc.de/schemas/components/0.9" 
	xmlns:escidocItem="http://www.escidoc.de/schemas/item/0.9" 
	xmlns:escidocItemList="http://www.escidoc.de/schemas/itemlist/0.9" 
	xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.5" 
	xmlns:escidocRelations="http://www.escidoc.de/schemas/relations/0.3" 
	xmlns:xlink="http://www.w3.org/1999/xlink" 
	xmlns:publication="http://purl.org/escidoc/metadata/profiles/0.1/publication" 
	xmlns:jhove="http://hul.harvard.edu/ois/xml/ns/jhove"
	xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
	xmlns:person="http://purl.org/escidoc/metadata/profiles/0.1/person"
	xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source"
	xmlns:organization="http://purl.org/escidoc/metadata/profiles/0.1/organization"
	xmlns:legalCase="http://purl.org/escidoc/metadata/profiles/0.1/legal-case"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:reportFunctions="urn:escidoc:functions"
	xmlns:escidoc="http://purl.org/escidoc/metadata/terms/0.1/"
	xmlns:eprints="http://purl.org/eprint/terms/"
	xmlns:foaf="http://xmlns.com/foaf/0.1/"
	xmlns:prop="http://escidoc.de/core/01/properties/"
	xmlns:Util="java:de.mpg.escidoc.services.transformation.Util">
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:variable name="authorRole" select="'http://www.loc.gov/loc.terms/relators/AUT'"/>
	<xsl:variable name="editorRole" select="'http://www.loc.gov/loc.terms/relators/EDT'"/>
	<!-- <xsl:variable name="selectedRole" select="$authorRole"/> -->
	<xsl:param name="selectedRole" select="$authorRole"/>
	
	<xsl:variable name="genreMonograph" select="'http://purl.org/escidoc/metadata/ves/publication-types/monograph'"/>
	<xsl:variable name="genreCollectedEdition" select="'http://purl.org/escidoc/metadata/ves/publication-types/collected-edition'"/>
	<xsl:variable name="genreCommentary" select="'http://purl.org/escidoc/metadata/ves/publication-types/commentary'"/>
	<xsl:variable name="genreHandbook" select="'http://purl.org/escidoc/metadata/ves/publication-types/handbook'"/>
	<xsl:variable name="genreProceedings" select="'http://purl.org/escidoc/metadata/ves/publication-types/proceedings'"/>
	
	<xsl:variable name="genreJournal" select="'http://purl.org/escidoc/metadata/ves/publication-types/journal'"/>
	<xsl:variable name="genreSeries" select="'http://purl.org/escidoc/metadata/ves/publication-types/series'"/>

	<xsl:variable name="item" select="item-list/item"/>
	<xsl:variable name="publication" select="item-list/item/publication"/>
	
	<xsl:variable name="instName"
		select="'MPI for Comparative and International Private Law'"></xsl:variable>
	<xsl:param name="institutsName" select="'MPI for Comparative and International Private Law'"/>
	
	<xsl:key name="autorshipGenreOrder" match="s:autorship/s:genre/@priority"
		use="../s:name" />
	<xsl:key name="firstListEditorshipGenreOrder" match="s:first_editorship/s:genre/@priority" 
		use="../s:name" />
	<xsl:key name="secondListEditorshipGenreOrder" match="s:second_editorship/s:genre/@priority" 
		use="../s:name" />
	
	<xsl:template match="escidocItemList:item-list">
		<xsl:variable name="coneResult"
		select="Util:queryCone('persons',$instName)/cone"/>

	<xsl:variable name="item-list">
		<xsl:apply-templates mode="sort-creators" />
	</xsl:variable>
	
	<xsl:choose>
		<xsl:when test="$selectedRole = $authorRole">
		<xsl:element name="AUTHORS">
			<xsl:attribute name="itemsCount" select="count($item-list/escidocItem:item)"/>
			<xsl:for-each select="$coneResult/rdf:RDF/rdf:Description">
				
				<xsl:sort select="foaf:family_name" />
				<xsl:sort select="foaf:givenname" />
				<xsl:variable name="currentAuthorId" select="@rdf:about" />
				<xsl:variable name="currentAuthorName"
					select="concat(foaf:givenname, ' ', foaf:family_name)" />
				<xsl:variable name="currentAuthorCitationStyleName"
					select="concat(foaf:family_name,', ',foaf:givenname)" />
				<xsl:choose>
					<xsl:when test="count($item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentAuthorId 
								and publication:publication/eterms:creator/@role=$authorRole]) &gt; 0">
				<xsl:element name="AUTOR">
					<xsl:attribute name="id" select="$currentAuthorId" />
					<xsl:attribute name="name" select="$currentAuthorName" />
					<xsl:attribute name="authorList" select="count($item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentAuthorId 
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
				<xsl:attribute name="test" select="$item-list/escidocItem:item/test"/>
			<xsl:for-each select="$coneResult/rdf:RDF/rdf:Description">
				<xsl:sort select="foaf:family_name" />
				<xsl:sort select="foaf:givenname" />
				<xsl:variable name="currentAuthorId" select="@rdf:about" />
				<xsl:variable name="currentAuthorName"
					select="concat(foaf:givenname, ' ', foaf:family_name)" />
				<xsl:variable name="currentAuthorCitationStyleName"
					select="concat(foaf:family_name,', ',foaf:givenname)" />

				<xsl:element name="EDITOR_1">
					<xsl:attribute name="id" select="$currentAuthorId" />
					<xsl:attribute name="name" select="$currentAuthorName" />

					<xsl:variable name="currentAuthorList">
						<xsl:for-each
							select="$item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentAuthorId and 
								publication:publication/eterms:creator/@role=$editorRole and
								(publication:publication/@type = $genreCollectedEdition or 
								publication:publication/@type = $genreMonograph or
								publication:publication/@type = $genreCommentary or
								publication:publication/@type = $genreHandbook or
								publication:publication/@type = $genreProceedings)]">
							<xsl:sort
								select="string-join((publication:publication/eterms:creator[person:person/eterms:complete-name != $currentAuthorName]/person:person/eterms:sort-name), '; ')" />
							<xsl:sort
								select="key('firstListEditorshipGenreOrder', publication:publication/@type)"
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
			</xsl:for-each>
			
			<xsl:for-each select="$coneResult/rdf:RDF/rdf:Description">
				<xsl:sort select="foaf:family_name" />
				<xsl:sort select="foaf:givenname" />
				<xsl:variable name="currentAuthorId" select="@rdf:about" />
				<xsl:variable name="currentAuthorName"
					select="concat(foaf:givenname, ' ', foaf:family_name)" />
				<xsl:variable name="currentAuthorCitationStyleName"
					select="concat(foaf:family_name,', ',foaf:givenname)" />

				<xsl:element name="EDITOR_2">
					<xsl:attribute name="id" select="$currentAuthorId" />
					<xsl:attribute name="name" select="$currentAuthorName" />

					<xsl:variable name="currentAuthorList">
						<xsl:for-each
							select="$item-list/escidocItem:item/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[publication:publication/eterms:creator/person:person/dc:identifier = $currentAuthorId and 
								publication:publication/eterms:creator/@role=$editorRole and
								(publication:publication/@type = $genreJournal or
								publication:publication/@type = $genreSeries)]">
							<xsl:sort
								select="string-join((publication:publication/eterms:creator[person:person/eterms:complete-name != $currentAuthorName]/person:person/eterms:sort-name), '; ')" />
							<xsl:sort
								select="key('firstListEditorshipGenreOrder', publication:publication/@type)"
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
			</xsl:for-each>
			
			</xsl:element>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>
	
	<xsl:template match="escidocItem:item"  mode="sortedAuthorList">
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
						<xsl:value-of select="$authorCitationName" />; <xsl:value-of select="$authorString" />
					</xsl:otherwise>
				</xsl:choose>
				<!--
					<xsl:value-of select="$authorName"/>; <xsl:value-of
					select="$authorString"/>
				-->
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of>--</xsl:value-of>
			</xsl:otherwise>
		</xsl:choose>
<!--			<xsl:text>, objid: </xsl:text>-->
<!--			<xsl:value-of select="@objid"></xsl:value-of>-->
<!--			<xsl:text>, type: </xsl:text>-->
<!--			<xsl:value-of select="key('autorshipGenreOrder', escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/@type)"/>-->
<!--			<xsl:value-of select="escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/@type"></xsl:value-of>-->
<!--		<xsl:text>-->
<!--	</xsl:text>-->
<!--		<xsl:value-of select="$authorString"></xsl:value-of>-->
		<xsl:value-of select="escidocItem:properties/prop:content-model-specific/dcterms:bibliographicCitation"/>
		<xsl:text> </xsl:text>
		<xsl:value-of select="@objid"></xsl:value-of>
			<xsl:text>
	</xsl:text>
	</xsl:template>
	
	
	<xsl:template match="*" mode="sort-creators">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates mode="sort-creators"/>
		</xsl:copy>
	</xsl:template>
	
	
	
	<!--<xsl:template match="escidocItem:item" mode="sort-creators">
		<xsl:copy>
			<xsl:element name="test">XXX</xsl:element>
			<xsl:apply-templates  mode="sort-creators"/>
		</xsl:copy>
	</xsl:template> -->
	

	<xsl:template match="publication:publication" mode="sort-creators">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:variable name="creators">
				<xsl:perform-sort select="eterms:creator">
					<xsl:sort select="person:person/eterms:family-name"/>
					<xsl:sort select="person:person/eterms:given-name"/>
				</xsl:perform-sort>
			</xsl:variable>
			<xsl:apply-templates select="$creators" mode="sort-creators"/>
			<xsl:apply-templates select="*[name() != 'eterms:creator']" mode="sort-creators"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="person:person" mode="sort-creators">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<eterms:sort-name>
				<xsl:value-of select="eterms:family-name"/>
				<xsl:text>, </xsl:text>
				<xsl:value-of select="eterms:given-name"/>
			</eterms:sort-name>
			<xsl:apply-templates mode="sort-creators"/>
		</xsl:copy>
	</xsl:template>	


</xsl:stylesheet>