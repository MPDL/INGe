<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:saxon="http://saxon.sf.net/"
	xmlns:misc="http://www.editura.de/ns/2012/misc"
	xmlns:misc-marc="http://www.editura.de/ns/2012/misc-marc"
	xmlns:local="http://www.editura.de/ns/2012/local"
	xmlns:tools="http://www.editura.de/ns/2012/tools"
	xmlns:dc="${xsd.metadata.dc}"
	xmlns:dcterms="${xsd.metadata.dcterms}"
	xmlns:escidocComponents="${xsd.soap.item.components}"
	xmlns:escidocItem="${xsd.soap.item.item}"
	xmlns:escidocItemList="${xsd.soap.item.itemlist}"
	xmlns:escidocMetadataRecords="${xsd.soap.common.metadatarecords}"
	xmlns:eterms="${xsd.metadata.escidocprofile.types}"
	xmlns:eves="http://purl.org/escidoc/metadata/ves/0.1/"
	xmlns:file="${xsd.metadata.file}"
	xmlns:item-list="${xsd.soap.item.itemlist}"
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:prop="${xsd.soap.common.prop}"
	xmlns:publication="${xsd.metadata.publication}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:srel="${xsd.soap.common.srel}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:marc="http://www.loc.gov/MARC21/slim" exclude-result-prefixes="xs xd misc local tools" version="2.0">
	<xsl:import href="mapping_commons.xsl"/>
	<xsl:import href="mapping_commons_marc.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	<xsl:param name="local:librarys-identifying-code" as="xs:string">ZDB-97-MPR</xsl:param>
	<xsl:variable name="local:addition-for-alternative-name" as="xs:string?" select="' (alternative name)'"/>
	<xsl:variable name="local:addition-for-pseudonym" as="xs:string?" select="' (pseudonym)'"/>
	<xsl:variable name="local:form-description-for-online-resources" as="xs:string?" select="'[Online]'"/>
	<xsl:variable name="local:pages-marker" as="xs:string">p.</xsl:variable>
	<xsl:param name="pubmanUrl" />
	<xsl:template match="/|escidocItem:item|escidocMetadataRecords:md-records|escidocMetadataRecords:md-record| eterms:creator" xml:id="match-and-apply-templates">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="escidocItem:properties|escidocComponents:components" xml:id="match-discard"/>
	<xsl:template match="publication:publication[normalize-space(dc:title)]" xml:id="match-publication_publication" as="element(marc:record)">
		<marc:record>
			<xsl:variable name="date-entered-on-file" as="xs:string" select="if ($misc:run-in-testmode) then '991231' else format-date(current-date(), '[Y01,2-2][M01][D01]')"/>
			<xsl:variable name="year-of-publication" as="xs:string">
				<xsl:variable name="temp" as="xs:string" select="substring(local:publication-date(.), 1, 4)"/>
				<xsl:choose>
					<xsl:when test="normalize-space($temp)">
						<xsl:sequence select="$temp"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:sequence select="'uuuu'"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="form-of-item" as="xs:string" select="local:form-of-item(.)"/>
			<xsl:variable name="bibliographic-level" as="xs:string" select="local:bibliographic-level(.)"/>
			<xsl:variable name="languages" as="xs:string*" select="dc:language/local:lang(.)[normalize-space(.)]"/>
			<xsl:variable name="lang" as="xs:string?" select="$languages[1]"/>
			<xsl:sequence select="local:leader(misc-marc:pubman_publication-type-to-marc_type-of-record(@type), $bibliographic-level, misc-marc:pubman_publication-type-to-marc_multipart-resource(@type))"/>
			<xsl:sequence select="local:controlfield('001', local:controlfield-001(ancestor::escidocItem:item/@objid) )"/>
			<xsl:sequence select="local:controlfield('003', $local:librarys-identifying-code)"/>
			<xsl:sequence select="local:controlfield-005()"/>
			<xsl:if test="exists(eterms:published-online)">
				<xsl:sequence select="local:controlfield('007','cr|||||||||||')"/>
			</xsl:if>
			<xsl:sequence select="local:controlfield-008-wrapper( $date-entered-on-file, if ($bibliographic-level eq 's') then 'c' else if ($year-of-publication ne 'uuuu') then 's' else 'n', if (($year-of-publication eq 'uuuu') and ($bibliographic-level eq 's')) then ' ' else $year-of-publication, if ($bibliographic-level = ('s')) then local:cf008-type-specific-continuing-resources( local:type-of-continuing-resource(@type), $form-of-item, local:nature-of-contents(@type, 3), misc-marc:pubman_publication-type-to-marc_conference(@type) ) else local:cf008-type-specific-books( $form-of-item, local:nature-of-contents(@type, 4), misc-marc:pubman_publication-type-to-marc_conference(@type), misc-marc:pubman_publication-type-to-marc_festschrift(@type) ), if ($lang) then $lang else '|||' )"/>
			<xsl:apply-templates select="dc:identifier[@xsi:type eq 'eterms:ISBN'][normalize-space(.)]"/>
			<xsl:apply-templates select="dc:identifier[@xsi:type eq 'eterms:ISSN'][normalize-space(.)]"/>
			<xsl:apply-templates select="dc:identifier[not(@xsi:type = ('eterms:ISBN', 'eterms:ISSN'))]" mode="local:helper-phase"/>
			<xsl:apply-templates select="event:event[normalize-space(.)]" mode="local:helper-phase"/>
			<xsl:variable name="subfields-041" as="element(marc:subfield)*">
				<xsl:variable name="subfield-a-languages" as="xs:string*" select="distinct-values($languages)[normalize-space(.)]"/>
				<xsl:variable name="subfield-b-languages" as="xs:string*">
					<xsl:variable name="every-lang-code" as="xs:string*">
						<xsl:for-each select="dcterms:abstract">
							<xsl:variable name="current-lang" as="xs:string?">
								<xsl:variable name="lang-code" as="xs:string?" select="normalize-space( if (contains(@xml:lang, '-')) then substring-before(@xml:lang, '-') else @xml:lang )"/>
								<xsl:choose>
									<xsl:when test="not($lang-code)"/>
									<xsl:when test="misc:is-iso-639-2-b($lang-code)">
										<xsl:sequence select="$lang-code"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:sequence select="misc:iso-639-3_to_iso-639-2($lang-code)[normalize-space(.)]"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="$current-lang">
									<xsl:sequence select="$current-lang"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:sequence select="$lang[normalize-space(.)]"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</xsl:variable>
					<xsl:sequence select="distinct-values($every-lang-code)"/>
				</xsl:variable>
				<xsl:if test="$subfield-a-languages[1] or $subfield-b-languages[1]">
					<xsl:for-each select="$subfield-a-languages">
						<xsl:sequence select="local:subfield('a', .)"/>
					</xsl:for-each>
					<xsl:for-each select="$subfield-b-languages">
						<xsl:sequence select="local:subfield('b', .)"/>
					</xsl:for-each>
				</xsl:if>
			</xsl:variable>
			<xsl:if test="$subfields-041">
				<xsl:sequence select="local:datafield('041', ' ', '7', ( $subfields-041, local:subfield('2', 'iso639-2b') ) )"/>
			</xsl:if>
			<xsl:apply-templates select="dc:subject[normalize-space(.)]"/>
			<xsl:apply-templates select="eterms:creator[local:eterms_creator-is-1xx(.)]"/>
			<xsl:variable name="subfields-245" as="element(marc:subfield)+">
				<xsl:sequence select="local:subfield('a', normalize-space(dc:title))"/>
				<xsl:if test="$form-of-item eq 'o'">
					<xsl:sequence select="local:subfield('h', $local:form-description-for-online-resources)"/>
				</xsl:if>
			</xsl:variable>
			<xsl:variable name="df245-ind1" as="xs:string">
				<xsl:choose>
					<xsl:when test="eterms:creator[local:eterms_creator-is-1xx(.)]">1</xsl:when>
					<xsl:otherwise>0</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:sequence select="local:datafield('245', $df245-ind1, '0', $subfields-245 )"/>
			<xsl:apply-templates select="dcterms:alternative[normalize-space(.)]"/>
			<xsl:apply-templates select="eterms:publishing-info/eterms:edition[normalize-space(.)]"/>
			<xsl:choose>
				<xsl:when test="eterms:publishing-info">
					<xsl:apply-templates select="eterms:publishing-info"/>
				</xsl:when>
				<xsl:when test="normalize-space(local:publication-date(.) )">
					<xsl:sequence select="local:datafield('260', local:subfield('c', normalize-space(local:publication-date(.) ) ) )"/>
				</xsl:when>
			</xsl:choose>
			<xsl:variable name="total-number-of-pages" as="xs:string?" select="(misc:total-number-of-pages(.), (source:source/misc:total-number-of-pages(.))[1] )[1]"/>
			<xsl:if test="$total-number-of-pages">
				<xsl:sequence select="local:datafield( '300', local:subfield( 'a', concat( $total-number-of-pages, if (matches($total-number-of-pages, 'S\.|Seiten|p\.|pages')) then () else concat(' ', $local:pages-marker) ) ) )"/>
			</xsl:if>
			<xsl:apply-templates select="source:source[local:source_source-is-series(.)]"/>
			<xsl:apply-templates select="dcterms:tableOfContents[normalize-space(.)]"/>
			<!-- Degree type in field 502 -->
			<xsl:apply-templates select="eterms:degree[normalize-space(.)]"/>
			<xsl:call-template name="local:make-506"/>
			<xsl:apply-templates select="event:event[normalize-space(.)]"/>
			<xsl:apply-templates select="dcterms:abstract[normalize-space(.)]"/>
			<xsl:if test="$form-of-item eq 'o'">
				<xsl:sequence select="local:datafield('533', local:subfield('n', $local:form-description-for-online-resources))"/>
			</xsl:if>
			<xsl:apply-templates select="../../../escidocComponents:components/escidocComponents:component/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dcterms:license[normalize-space(.)]"/>
			<xsl:sequence select="local:make-542(.)"/>
			<xsl:apply-templates select="dcterms:subject[normalize-space(.)]"/>
			<xsl:apply-templates select="eterms:creator[local:eterms_creator-is-7xx(.)]"/>
			<xsl:apply-templates select="eterms:creator[local:eterms_creator-is-7xx-other(.)]"/>
			<xsl:if test="@type = ( 'http://purl.org/eprint/type/ConferencePaper', 'http://purl.org/eprint/type/ConferencePoster', 'http://purl.org/escidoc/metadata/ves/publication-types/proceedings' )">
				<xsl:for-each select="event:event">
					<xsl:call-template name="local:event_event-to-711"/>
				</xsl:for-each>
			</xsl:if>
			<xsl:apply-templates select="source:source[not(local:source_source-is-series(.))]"/>
			<xsl:apply-templates select="source:source/eterms:creator[not(local:eterms_creator-is-1xx(.))]/person:person" mode="local:make-8xx"/>
			<xsl:apply-templates select="source:source/eterms:creator[not(local:eterms_creator-is-1xx(.))]/organization:organization" mode="local:make-8xx"/>
			<xsl:variable name="collected-856" as="element(marc:datafield)*">
				<xsl:apply-templates select="dc:identifier[misc:dc_identifier-is-online-resource(.)]"/>
				<xsl:apply-templates select="../../../escidocComponents:components/escidocComponents:component"/>
			</xsl:variable>
			<xsl:for-each select="distinct-values($collected-856/marc:subfield[@code eq 'u']/text())">
				<xsl:copy-of select="$collected-856[marc:subfield[@code eq 'u']/text() eq current()][1]"/>
			</xsl:for-each>
			<!-- Context Id in field 887 -->
			<xsl:sequence select="local:datafield('887', (local:subfield('a', ../../../escidocItem:properties/srel:context/@objid), local:subfield('2', 'mpg.pure.context.id')))"/>
			<xsl:apply-templates select="source:source" mode="local:make-952"/>
			<xsl:variable name="local-tags" as="xs:string*" select="../../../escidocItem:properties/prop:content-model-specific/local-tags/local-tag[normalize-space(.)]/string()"/>
			<xsl:if test="$local-tags[1]">
				<xsl:sequence select="local:datafield('995', (for $i in distinct-values($local-tags) return local:subfield('a', $i)))"/>
			</xsl:if>
			<xsl:variable name="f996a" as="xs:string?" select="misc-marc:pubman_publication_type-to-marc_996_a(@type)"/>
			<xsl:if test="$f996a">
				<xsl:sequence select="local:datafield('996', local:subfield('a', $f996a))"/>
			</xsl:if>
			<xsl:variable name="f997a" as="xs:string?" select="misc-marc:pubman_publication_type-to-marc_997_a(@type)"/>
			<xsl:if test="$f997a">
				<xsl:sequence select="local:datafield('997', local:subfield('a', $f997a))"/>
			</xsl:if>
			<xsl:if test="$misc:run-in-testmode">
				<xsl:apply-templates select="*[normalize-space(.)] except ( dc:identifier| dc:language| dc:subject| dc:title| dcterms:abstract| dcterms:alternative| dcterms:created| dcterms:dateAccepted| dcterms:dateSubmitted| dcterms:issued| dcterms:modified| dcterms:subject| dcterms:tableOfContents| eterms:creator| eterms:degree| eterms:location| eterms:published-online| eterms:publishing-info| eterms:total-number-of-pages| event:event| source:source )"/>
			</xsl:if>
		</marc:record>
	</xsl:template>
	<xsl:template match="publication:publication[not(normalize-space(dc:title) )]" xml:id="match-publication_publication_empty-title">
		<xsl:variable name="currentID" as="xs:string?" select="dc:identifier[normalize-space(.)][1]"/>
		<xsl:call-template name="misc:message">
			<xsl:with-param name="level">WARN</xsl:with-param>
			<xsl:with-param name="message">[pubman_to__marc.xsl#match-publication_publication_empty-title]Publication without dc:title 
				<xsl:value-of select="if ($currentID) then concat('with ID ', $currentID, ' ') else ()"/>not converted.
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="eterms:creator[local:eterms_creator-is-1xx(.)]/person:person" as="element(marc:datafield)" xml:id="eterms_creator-is-1xx-person_person">
		<xsl:call-template name="local:make-x00">
			<xsl:with-param name="tag">100</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="eterms:creator[local:eterms_creator-is-1xx(.)]/organization:organization" as="element(marc:datafield)" xml:id="eterms_creator-is-1xx-organization_organization">
		<xsl:call-template name="local:make-x10">
			<xsl:with-param name="tag">110</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="eterms:creator[local:eterms_creator-is-7xx(.) or local:eterms_creator-is-7xx-other(.)]/person:person" as="element(marc:datafield)" xml:id="eterms_creator-is-7xx-person_person">
		<xsl:call-template name="local:make-x00">
			<xsl:with-param name="tag">700</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="eterms:creator[local:eterms_creator-is-7xx(.) or local:eterms_creator-is-7xx-other(.)]/organization:organization" as="element(marc:datafield)" xml:id="eterms_creator-is-7xx-organization_organization">
		<xsl:call-template name="local:make-x10">
			<xsl:with-param name="tag">710</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="publication:publication/eterms:publishing-info" as="element(marc:datafield)?" xml:id="publication_publication-eterms_publishing-info">
		<xsl:variable name="subfields" as="element(marc:subfield)*">
			<xsl:apply-templates select="eterms:place, dc:publisher"/>
			<xsl:variable name="publication-date" as="xs:string?" select="local:publication-date(..)"/>
			<xsl:if test="$publication-date">
				<xsl:sequence select="local:subfield('c', $publication-date)"/>
			</xsl:if>
		</xsl:variable>
		<xsl:if test="$subfields">
			<xsl:sequence select="local:datafield('260', $subfields)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="publication:publication/source:source[local:source_source-is-series(.)]" xml:id="publication_publication-source_source-is-series" as="element(marc:datafield)">
		<xsl:variable name="ind1" as="xs:string">
			<xsl:choose>
				<xsl:when test="normalize-space(eterms:volume)">1</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="subfields" as="element(marc:subfield)+">
			<xsl:variable name="main_entry_heading" as="xs:string?" select=" ( eterms:creator[local:eterms_creator-is-1xx(.)]/person:person/misc:person_person-to-name(., false())[normalize-space(.)], eterms:creator[local:eterms_creator-is-1xx(.)]/organization:organization/dc:title[normalize-space(.)]/normalize-space(.) )[normalize-space(.)][1] "/>
			<xsl:sequence select="local:subfield('a', concat( normalize-space(dc:title), if($main_entry_heading) then (' / ') else (), $main_entry_heading ) )"/>
			<xsl:variable name="formatted-volume-issue" as="xs:string?" select="misc:format-volume-issue(eterms:volume, eterms:issue)"/>
			<xsl:if test="$formatted-volume-issue">
				<xsl:sequence select="local:subfield('v', $formatted-volume-issue)"/>
			</xsl:if>
			<xsl:for-each select="dc:identifier[@xsi:type eq 'eterms:ISSN'][normalize-space(.)]">
				<xsl:sequence select="local:subfield('x', normalize-space(.))"/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:sequence select="local:datafield('490', $ind1, $subfields)"/>
	</xsl:template>
	<xsl:template match="event:event" mode="local:helper-phase" xml:id="match-event_event-helper_phase" as="element(marc:datafield)?">
		<xsl:variable name="start" as="xs:string?" select="normalize-space(translate(eterms:start-date, '-', '') )"/>
		<xsl:variable name="end" as="xs:string?" select="normalize-space(translate(eterms:end-date, '-', '') )"/>
		<xsl:variable name="ind1" as="xs:string">
			<xsl:choose>
				<xsl:when test="not($start or $end)">
					<xsl:sequence select=" ' ' "/>
				</xsl:when>
				<xsl:when test="$start eq $end">
					<xsl:sequence select=" '0' "/>
				</xsl:when>
				<xsl:when test="$start and $end">
					<xsl:sequence select=" '2' "/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:sequence select=" '0' "/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="subfields" as="element(marc:subfield)*">
			<xsl:if test="$start">
				<xsl:sequence select="local:subfield('a', tools:fill-right($start, '-', 8) )"/>
			</xsl:if>
			<xsl:if test="$end">
				<xsl:sequence select="local:subfield('a', tools:fill-right($end, '-', 8) )"/>
			</xsl:if>
			<xsl:if test="normalize-space(eterms:place)">
				<xsl:sequence select="local:subfield('p', normalize-space(eterms:place) )"/>
			</xsl:if>
		</xsl:variable>
		<xsl:if test="$subfields">
			<xsl:sequence select="local:datafield('033', $ind1, $subfields)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="event:event" xml:id="match-event_event" as="element(marc:datafield)">
		<xsl:variable name="subfields" as="element(marc:subfield)+">
			<xsl:sequence select=" local:subfield( 'a', concat( normalize-space(dc:title), if (normalize-space(dc:title) and eterms:place[normalize-space(.)]) then (', ') else (), normalize-space(eterms:place) ) )"/>
			<xsl:variable name="date" as="xs:string" select=" normalize-space(concat( normalize-space(eterms:start-date), if (normalize-space(eterms:start-date) and normalize-space(eterms:end-date)) then ('/') else (), normalize-space(eterms:end-date) ) )"/>
			<xsl:if test="$date">
				<xsl:sequence select="local:subfield('d', $date)"/>
			</xsl:if>
			<xsl:for-each select="dcterms:alternative[normalize-space(.)]">
				<xsl:sequence select="local:subfield('o', normalize-space(concat(., $local:addition-for-alternative-name) ) )"/>
			</xsl:for-each>
			<xsl:if test="normalize-space(eterms:place)">
				<xsl:sequence select="local:subfield('p', normalize-space(eterms:place) )"/>
			</xsl:if>
		</xsl:variable>
		<xsl:sequence select="local:datafield('518', $subfields)"/>
	</xsl:template>
	<xsl:template match="publication:publication/source:source[not(local:source_source-is-series(.))]" xml:id="publication_publication-source_source-is-not-series" as="element(marc:datafield)">
		<xsl:variable name="target-data-field" as="xs:string">
			<xsl:choose>
				<xsl:when test="../@type eq 'http://purl.org/escidoc/metadata/ves/publication-types/issue'">770</xsl:when>
				<xsl:otherwise>773</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="subfields" as="element(marc:subfield)+">
			<xsl:if test="(local:bibliographic-level(..) = ('a', 'b') ) or (local:bibliographic-level(.) = ('m', 's') )">
				<xsl:sequence select="local:subfield('i', 'in:')"/>
			</xsl:if>
			<xsl:sequence select="local:subfield('t', normalize-space(dc:title) )"/>
			<xsl:variable name="main_entry_headings" as="xs:string*">
				<xsl:for-each select="eterms:creator[local:eterms_creator-is-1xx(.)]">
					<xsl:sequence select="person:person/misc:person_person-to-name(., false()), normalize-space(organization:organization/dc:title)"/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="$main_entry_headings[normalize-space(.)][1]">
				<xsl:sequence select="local:subfield('a', $main_entry_headings[normalize-space(.)][1])"/>
			</xsl:if>
			<xsl:variable name="date" as="xs:string?">
				<xsl:choose>
					<xsl:when test="normalize-space(dcterms:issued)">
						<xsl:sequence select="normalize-space(dcterms:issued)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:sequence select="../local:publication-date(.)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="place-publisher-date" as="xs:string?" select="local:render-place-publisher-date(eterms:publishing-info/eterms:place, eterms:publishing-info/dc:publisher, $date)"/>
			<xsl:if test="$place-publisher-date">
				<xsl:sequence select="local:subfield('d', $place-publisher-date)"/>
			</xsl:if>
			<xsl:variable name="subfield-g" as="xs:string?">
				<xsl:variable name="volume-issue-formatted" as="xs:string?" select="misc:format-volume-issue(eterms:volume, eterms:issue)"/>
				<xsl:variable name="pages" as="xs:string?" select="local:pages(.)"/>
				<xsl:variable name="sequence-number" as="xs:string?" select="eterms:sequence-number"/>
				<xsl:sequence select="concat($volume-issue-formatted, if ($volume-issue-formatted and $pages) then ', ' else (), if ($pages) then concat($local:pages-marker, ' ') else (), $pages, if($sequence-number) then (if($volume-issue-formatted or $pages) then concat(', art. ', $sequence-number) else concat('art. ', $sequence-number) ) else ()) "/>
			</xsl:variable>
			<xsl:if test="$subfield-g">
				<xsl:sequence select="local:subfield('g', $subfield-g)"/>
			</xsl:if>
			<xsl:if test="dcterms:alternative[@xsi:type eq 'eterms:ABBREVIATION'][normalize-space(.)]">
				<xsl:sequence select="local:subfield('p', dcterms:alternative[@xsi:type eq 'eterms:ABBREVIATION'][normalize-space(.)][1])"/>
			</xsl:if>
			<xsl:variable name="ISSN" as="xs:string?" select="(dc:identifier[@xsi:type eq 'eterms:ISSN'][normalize-space(.)]/normalize-space(.))[1]"/>
			<xsl:if test="($target-data-field eq '773') and misc:publication-publication-is-journal-article(..)">
				<xsl:variable name="SICI" as="xs:string" select="misc:SICI-for-773-q( misc:clean-volume-issue(eterms:volume), misc:clean-volume-issue(eterms:issue), '', misc:clean-pages(eterms:start-page), misc:clean-pages(eterms:end-page) )"/>
				<xsl:if test="$SICI">
					<xsl:sequence select="local:subfield('q', $SICI)"/>
				</xsl:if>
			</xsl:if>
			<xsl:if test="normalize-space($ISSN)">
				<xsl:sequence select="local:subfield('x', $ISSN)"/>
			</xsl:if>
			<xsl:for-each select="dc:identifier[@xsi:type eq 'eterms:ISBN'][normalize-space(.)]/normalize-space(.)">
				<xsl:sequence select="local:subfield('z', normalize-space(.))"/>
			</xsl:for-each>
			<xsl:sequence select="local:subfield('7', concat('nn', misc-marc:pubman_publication-type-to-marc_type-of-record(@type), local:bibliographic-level(.) ) )"/>
		</xsl:variable>
		<xsl:sequence select="local:datafield($target-data-field, '0', '8', $subfields)"/>
		<xsl:if test="$misc:run-in-testmode">
			<xsl:apply-templates select="*[normalize-space(.)] except ( dc:title| eterms:creator| dcterms:issued| eterms:publishing-info| eterms:start-page| eterms:end-page| eterms:volume| eterms:issue| dc:identifier| dcterms:alternative[@xsi:type eq 'eterms:ABBREVIATION'] )"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="escidocComponents:component[normalize-space(escidocComponents:content/@xlink:href)]" as="element(marc:datafield)?" xml:id="match_escidocComponents_component">
		<xsl:variable name="href" as="xs:string?">
			<xsl:variable name="href-temp" as="xs:string" select="normalize-space(escidocComponents:content/@xlink:href)"/>
			<xsl:choose>
				<xsl:when test="escidocComponents:content/@storage eq 'internal-managed'">
					<xsl:sequence select="local:href(.)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:sequence select="$href-temp"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="subfields" as="element(marc:subfield)+">
			<xsl:apply-templates select="escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dc:format[@xsi:type eq 'dcterms:IMT'][normalize-space(.)]"/>
			<xsl:apply-templates select="escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dcterms:extent[normalize-space(.)]"/>
			<xsl:sequence select="local:subfield('u', $href)"/>
			<xsl:apply-templates select="escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dc:description[normalize-space(.)]"/>
			<xsl:apply-templates select="escidocComponents:properties/prop:visibility[normalize-space(.)]"/>
			<xsl:apply-templates select="escidocComponents:properties/prop:content-category[normalize-space(.)]"/>
		</xsl:variable>
		<xsl:if test="$href">
			<xsl:sequence select="local:datafield('856', '4', $subfields)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="escidocComponents:component[not(normalize-space(escidocComponents:content/@xlink:href))]" xml:id="match_escidocComponents_component_2"/>
	<xsl:template match="item-list:item-list" xml:id="match-item_list-item_list" as="element(marc:collection)">
		<marc:collection>
			<xsl:apply-templates/>
		</marc:collection>
	</xsl:template>
	<xsl:template match="publication:publication/dc:identifier[@xsi:type eq 'eterms:ISBN']" as="element(marc:datafield)" xml:id="match-publication_publication-dc_identifier-ISBN">
		<xsl:sequence select="local:datafield('020', local:subfield('a', .) )"/>
	</xsl:template>
	<xsl:template match="publication:publication/dc:identifier[@xsi:type eq 'eterms:ISSN']" as="element(marc:datafield)" xml:id="match-publication_publication-dc_identifier-ISSN">
		<xsl:sequence select="local:datafield('022', local:subfield('a', .) )"/>
	</xsl:template>
	<xsl:template match="eterms:person-title" as="element(marc:subfield)" xml:id="match-eterms_person-title">
		<xsl:sequence select="local:subfield('c', normalize-space(.))"/>
	</xsl:template>
	<xsl:template match="person:person/organization:organization[not(preceding-sibling::organization:organization)]" as="element(marc:subfield)" xml:id="match-person_person-organization_organization">
		<xsl:variable name="affiliation" as="xs:string">
			<xsl:sequence select="string-join(../organization:organization/misc:organization_organization-to-string(.), '; ')"/>
		</xsl:variable>
		<xsl:sequence select="local:subfield('u', $affiliation)"/>
	</xsl:template>
	<xsl:template match="person:person/organization:organization[preceding-sibling::organization:organization]" xml:id="match-person_person-organization_organization_2"/>
	<xsl:template match="eterms:publishing-info/eterms:place" as="element(marc:subfield)" xml:id="match-eterms_publishing-info-eterms_place">
		<xsl:sequence select="local:subfield('a', normalize-space(.))"/>
	</xsl:template>
	<xsl:template match="eterms:publishing-info/dc:publisher" as="element(marc:subfield)" xml:id="match-dc_publisher">
		<xsl:sequence select="local:subfield('b', normalize-space(.))"/>
	</xsl:template>
	<xsl:template match="dcterms:abstract" as="element(marc:datafield)" xml:id="match-dcterms_abstract">
		<xsl:sequence select="local:datafield('520', '3', local:subfield('a', normalize-space(.) ) )"/>
	</xsl:template>
	<xsl:template match="person:person/dc:identifier|organization:organization/dc:identifier" as="element(marc:subfield)?" xml:id="match-person_person-dc_identifier">
		<xsl:if test="normalize-space(.)">
			<xsl:variable name="type-prefix" as="xs:string?" select="if (normalize-space(@xsi:type) ) then concat('(', @xsi:type, ')') else ''"/>
			<xsl:sequence select="local:subfield('0', concat($type-prefix, normalize-space(.) ) )"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="publication:publication/eterms:publishing-info/eterms:edition" as="element(marc:datafield)" xml:id="match-publication-publishing_info-edition">
		<xsl:sequence select="local:datafield('250', local:subfield('a', normalize-space(.) ) )"/>
	</xsl:template>
	<xsl:template match="publication:publication/dc:identifier[misc:dc_identifier-is-online-resource(.)]| source:source/dc:identifier[misc:dc_identifier-is-online-resource(.)]" as="element(marc:datafield)" xml:id="match-dc_identifier-is-online-resource">
		<xsl:variable name="url" as="xs:string" select="misc:dc_identifier-to-url(.)"/>
		<xsl:variable name="ind1" as="xs:string">
			<xsl:choose>
				<xsl:when test="starts-with($url, 'http://')">4</xsl:when>
				<xsl:when test="starts-with($url, 'https://')">4</xsl:when>
				<xsl:otherwise>
					<xsl:sequence select="' '"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="mediatype" as="xs:string?" select="tools:mediatype-from-url(normalize-space(.))"/>
		<xsl:variable name="subfield-q" as="element(marc:subfield)?">
			<xsl:if test="$mediatype">
				<xsl:sequence select="local:subfield('q', $mediatype)"/>
			</xsl:if>
		</xsl:variable>
		<xsl:sequence select="local:datafield('856', $ind1, (local:subfield('u', $url), $subfield-q) )"/>
	</xsl:template>
	<xsl:template match="publication:publication/dc:identifier[not(@xsi:type = ('eterms:ISBN', 'eterms:ISSN'))]| source:source/dc:identifier[not(@xsi:type = ('eterms:ISBN', 'eterms:ISSN'))]" mode="local:helper-phase" as="element(marc:datafield)?" xml:id="match-all-dc_identifier-but-isbn-issn-helper-phase">
		<xsl:variable name="source" as="xs:string?" select="lower-case(normalize-space(substring-after(@xsi:type, ':') ) )"/>
		<xsl:if test="normalize-space(.)">
			<xsl:sequence select="local:datafield( '024', '7', ( local:subfield('a', normalize-space(.) ), if (normalize-space($source) ) then local:subfield('2', $source) else () ) )"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="file:file/dc:format[@xsi:type eq 'dcterms:IMT']" as="element(marc:subfield)" xml:id="match-file_file-dc_format">
		<xsl:sequence select="local:subfield('q', normalize-space(.) )"/>
	</xsl:template>
	<xsl:template match="file:file/dcterms:extent" as="element(marc:subfield)" xml:id="match-file_file-dcterms_extent">
		<xsl:sequence select="local:subfield('s', concat(normalize-space(.), ' bytes') )"/>
	</xsl:template>
	<xsl:template match="file:file/dc:description" as="element(marc:subfield)" xml:id="match-file_file-dc_description">
		<xsl:sequence select="local:subfield('z', .)"/>
	</xsl:template>
	<xsl:template match="escidocComponents:properties/prop:visibility" as="element(marc:subfield)" xml:id="match-escidocComponents_properties-prop_visibility">
		<xsl:sequence select="local:subfield('2', .)"/>
	</xsl:template>
	<xsl:template match="escidocComponents:properties/prop:content-category" as="element(marc:subfield)" xml:id="match-escidocComponents_properties-prop_content-category">
		<xsl:variable name="content" as="xs:string" select="if (contains(., '/')) then tokenize(., '/')[last()] else string(.)"/>
		<xsl:sequence select="local:subfield('3', $content)"/>
	</xsl:template>
	<xsl:template match="publication:publication/dcterms:subject" as="element(marc:datafield)?" xml:id="match-publication_publication-dcterms_subject">
		<xsl:variable name="subfields" as="element(marc:subfield)*">
			<xsl:for-each select="local:tokenize-subject(.)">
				<xsl:sequence select="local:subfield('a', .)"/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="$subfields">
			<xsl:sequence select="local:datafield('653', '0', '0', $subfields)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="publication:publication/dc:subject" as="element(marc:datafield)" xml:id="match-publication_publication-dc_subject">
		<xsl:sequence select="local:datafield('082', local:subfield('a', .))"/>
	</xsl:template>
	<xsl:template match="eterms:degree" as="element(marc:datafield)" xml:id="match-eterms_degree">
		<xsl:variable name="local:degree_type" select="."/>
		<xsl:sequence select=" local:datafield('502', '', local:subfield('b', $local:mapping_marc_degrees/misc:mapping[./misc:source=$local:degree_type]/misc:target/text()) )"/>
	</xsl:template>
	<xsl:template match="dcterms:tableOfContents" as="element(marc:datafield)" xml:id="match-dcterms_tableOfContents">
		<xsl:sequence select=" local:datafield('505', '8', local:subfield('a', string-join(for $i in tokenize(., '\n', 'm') return normalize-space($i), '; ') ) )"/>
	</xsl:template>
	<xsl:template match="publication:publication/dcterms:alternative" as="element(marc:datafield)" xml:id="match-dcterms_alternative">
		<xsl:variable name="ind2" as="xs:string">
			<xsl:choose>
				<xsl:when test="@xsi:type eq 'eterms:ABBREVIATION'">0</xsl:when>
				<xsl:when test="@xsi:type eq 'eterms:OTHER'">3</xsl:when>
				<xsl:otherwise>
					<xsl:sequence select="' '"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:sequence select="local:datafield('246', ' ', $ind2, local:subfield('a', normalize-space(.)))"/>
	</xsl:template>
	<xsl:template match="file:file/dcterms:license" as="element(marc:datafield)?" xml:id="match-file_file-dcterms_license">
		<xsl:choose>
			<xsl:when test="ancestor::escidocComponents:component/preceding-sibling::escidocComponents:component/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dcterms:license[normalize-space(.) eq normalize-space(current())]"/>
			<xsl:otherwise>
				<xsl:sequence select="local:datafield('540', local:subfield('a', normalize-space(.)))"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="*" mode="#all" as="node()?" xml:id="match-all">
		<xsl:call-template name="misc:message">
			<xsl:with-param name="level">INFO</xsl:with-param>
			<xsl:with-param name="message">[pubman_to_marc.xsl#match-all]
				<xsl:if test="function-available('saxon:current-mode-name')">[Mode: 
					<xsl:sequence select="saxon:current-mode-name()" use-when="function-available('saxon:current-mode-name')"/>]
				</xsl:if> no matching template found
			</xsl:with-param>
			<xsl:with-param name="show-context" select="true()"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="eterms:alternative-name" mode="local:TextOnly" as="xs:string?" xml:id="match_eterms_alternative-name">
		<xsl:if test="normalize-space(.)">
			<xsl:sequence select="concat(normalize-space(.), $local:addition-for-alternative-name)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="eterms:pseudonym" mode="local:TextOnly" as="xs:string?" xml:id="match_eterms_pseudonym">
		<xsl:if test="normalize-space(.)">
			<xsl:sequence select="concat(normalize-space(.), $local:addition-for-pseudonym)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="source:source" as="element(marc:datafield)?" mode="local:make-952" xml:id="match_source-source-952">
		<xsl:variable name="volume" as="xs:string" select="normalize-space(eterms:volume)"/>
		<xsl:variable name="issue" as="xs:string" select="normalize-space(eterms:issue)"/>
		<xsl:variable name="pages" as="xs:string?" select="local:pages(.)"/>
		<xsl:variable name="total-number-of-pages" as="xs:string?" select="(misc:total-number-of-pages(.), (source:source/misc:total-number-of-pages(.))[1] )[1]"/>
		<xsl:variable name="year-of-publication" as="xs:string?" select="substring(local:publication-date(..), 1, 4)[normalize-space(.)]"/>
		<xsl:if test="(local:bibliographic-level(..) = ('a', 'b') ) and (some $i in ($volume, $issue, $pages, $total-number-of-pages, $year-of-publication) satisfies $i)">
			<xsl:variable name="subfields-952" as="element(marc:subfield)+">
				<xsl:if test="$volume">
					<xsl:sequence select="local:subfield('d', $volume)"/>
				</xsl:if>
				<xsl:if test="$issue">
					<xsl:sequence select="local:subfield('e', $issue)"/>
				</xsl:if>
				<xsl:if test="$total-number-of-pages">
					<xsl:sequence select="local:subfield('g', $total-number-of-pages)"/>
				</xsl:if>
				<xsl:if test="$pages">
					<xsl:sequence select="local:subfield('h', $pages)"/>
				</xsl:if>
				<xsl:if test="$year-of-publication">
					<xsl:sequence select="local:subfield('j', $year-of-publication)"/>
				</xsl:if>
			</xsl:variable>
			<xsl:sequence select="local:datafield('952', $subfields-952)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="person:person" xml:id="match-source_source-person_person-8xx" as="element(marc:datafield)?" mode="local:make-8xx">
		<xsl:if test="local:write-8XX(../..)">
			<xsl:call-template name="local:make-x00">
				<xsl:with-param name="tag">800</xsl:with-param>
				<xsl:with-param name="subfield-t-content" select="string(../../dc:title[1])"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="organization:organization" mode="local:make-8xx" as="element(marc:datafield)?" xml:id="match-source_source-organization_organization-8xx">
		<xsl:if test="local:write-8XX(../..)">
			<xsl:call-template name="local:make-x10">
				<xsl:with-param name="tag">810</xsl:with-param>
				<xsl:with-param name="subfield-t-content" select="string(../../dc:title[1])"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template name="local:make-x00" as="element(marc:datafield)" xml:id="named_local_make-x00">
		<xsl:param name="tag" as="xs:string" required="yes"/>
		<xsl:param name="subfield-t-content" as="xs:string?" required="no"/>
		<xsl:variable name="complete-name" as="xs:string" select="normalize-space(eterms:complete-name)"/>
		<xsl:variable name="given-name" as="xs:string" select="normalize-space(eterms:given-name)"/>
		<xsl:variable name="family-name" as="xs:string" select="normalize-space(eterms:family-name)"/>
		<xsl:variable name="ind1" as="xs:string">
			<xsl:choose>
				<xsl:when test="$given-name and $family-name">1</xsl:when>
				<xsl:when test="$given-name and (($given-name eq $complete-name) or not($complete-name))">0</xsl:when>
				<xsl:when test="$family-name and (($family-name eq $complete-name) or not($complete-name))">3</xsl:when>
				<xsl:otherwise>
					<xsl:sequence select="' '"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="subfields" as="element(marc:subfield)+">
			<xsl:variable name="name" as="xs:string" select="misc:person_person-to-name(., true())"/>
			<xsl:sequence select="local:subfield('a', $name)"/>
			<xsl:apply-templates select="eterms:person-title"/>
			<xsl:variable name="role" as="xs:string" select="normalize-space(ancestor::eterms:creator/@role)"/>
			<xsl:variable name="relator-term" as="xs:string?" select="misc-marc:eterms_creator_role-to-marc_relator_term($role)"/>
			<xsl:if test="$relator-term">
				<xsl:sequence select="local:subfield('e', $relator-term)"/>
			</xsl:if>
			<xsl:variable name="miscellaneous_information" as="xs:string*">
				<xsl:apply-templates select="eterms:alternative-name|eterms:pseudonym" mode="local:TextOnly"/>
			</xsl:variable>
			<xsl:if test="$miscellaneous_information[1]">
				<xsl:sequence select="local:subfield('g', string-join($miscellaneous_information, '; ') )"/>
			</xsl:if>
			<xsl:if test="normalize-space($subfield-t-content)">
				<xsl:sequence select="local:subfield('t', $subfield-t-content)"/>
			</xsl:if>
			<xsl:apply-templates select="organization:organization"/>
			<xsl:apply-templates select="dc:identifier"/>
			<xsl:if test="$role">
				<xsl:sequence select="local:subfield('4', misc-marc:eterms_creator_role-to-marc_relator_code($role))"/>
			</xsl:if>
		</xsl:variable>
		<xsl:sequence select="local:datafield($tag, $ind1, $subfields)"/>
	</xsl:template>
	<xsl:template name="local:make-x10" as="element(marc:datafield)" xml:id="named_local_make-x10">
		<xsl:param name="tag" as="xs:string" required="yes"/>
		<xsl:param name="subfield-t-content" as="xs:string?" required="no"/>
		<xsl:variable name="subfields" as="element(marc:subfield)+">
			<xsl:variable name="name" as="xs:string" select="normalize-space(dc:title)"/>
			<xsl:sequence select="local:subfield('a', if ($name) then $name else $misc:anonymous-organization-name)"/>
			<xsl:variable name="role" as="xs:string" select="normalize-space(ancestor::eterms:creator/@role)"/>
			<xsl:variable name="relator-term" as="xs:string?" select="misc-marc:eterms_creator_role-to-marc_relator_term($role)"/>
			<xsl:if test="$relator-term">
				<xsl:sequence select="local:subfield('e', $relator-term)"/>
			</xsl:if>
			<xsl:variable name="adress" as="xs:string" select="normalize-space(eterms:address)"/>
			<xsl:if test="$adress">
				<xsl:sequence select="local:subfield('g', $adress)"/>
			</xsl:if>
			<xsl:if test="normalize-space($subfield-t-content)">
				<xsl:sequence select="local:subfield('t', $subfield-t-content)"/>
			</xsl:if>
			<xsl:if test="$role">
				<xsl:sequence select="local:subfield('4', misc-marc:eterms_creator_role-to-marc_relator_code($role))"/>
			</xsl:if>
			<xsl:apply-templates select="dc:identifier"/>
		</xsl:variable>
		<xsl:sequence select="local:datafield($tag, $subfields)"/>
	</xsl:template>
	<xsl:template name="local:event_event-to-711" as="element(marc:datafield)" xml:id="named-event_event-to-711">
		<xsl:variable name="subfields" as="element(marc:subfield)+">
			<xsl:sequence select="local:subfield('a', normalize-space(dc:title))"/>
			<xsl:variable name="dates" as="xs:string?">
				<xsl:variable name="start" as="xs:string?" select="substring(normalize-space(eterms:start-date), 1, 4)"/>
				<xsl:variable name="end" as="xs:string?" select="substring(normalize-space(eterms:end-date), 1, 4)"/>
				<xsl:choose>
					<xsl:when test="$start and ($start eq $end)">
						<xsl:sequence select="$start"/>
					</xsl:when>
					<xsl:when test="$start and $end and ($start ne $end)">
						<xsl:sequence select="concat($start, '/', $end)"/>
					</xsl:when>
					<xsl:when test="$start">
						<xsl:sequence select="$start"/>
					</xsl:when>
					<xsl:when test="$end">
						<xsl:sequence select="$end"/>
					</xsl:when>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="misc" as="xs:string?" select="string-join(dcterms:alternative[normalize-space(.)]/normalize-space(.), '; ')"/>
			<xsl:if test="$misc">
				<xsl:sequence select="local:subfield('g', concat($misc, $local:addition-for-alternative-name) )"/>
			</xsl:if>
			<xsl:if test="$dates">
				<xsl:sequence select="local:subfield('d', $dates)"/>
			</xsl:if>
			<xsl:variable name="place" as="xs:string?" select="normalize-space(eterms:place)"/>
			<xsl:if test="$place">
				<xsl:sequence select="local:subfield('c', $place)"/>
			</xsl:if>
		</xsl:variable>
		<xsl:sequence select="local:datafield('711', $subfields)"/>
	</xsl:template>
	<xsl:template name="local:make-506" as="element(marc:datafield)?" xml:id="named-local_make-506">
		<xsl:variable name="visibility" as="xs:string*" select="distinct-values(ancestor::escidocItem:item/escidocComponents:components/escidocComponents:component/escidocComponents:properties/prop:visibility/normalize-space() )[normalize-space(.)]"/>
		<xsl:choose>
			<xsl:when test="count($visibility) ne 1"/>
			<xsl:when test="$visibility eq 'public'">
				<xsl:sequence select="local:datafield('506', '0', ( local:subfield('f', 'Unrestricted online access'), local:subfield('2', 'star') ) )"/>
			</xsl:when>
			<xsl:when test="$visibility eq 'private'">
				<xsl:sequence select="local:datafield('506', '1', ( local:subfield('a', 'No public access'), local:subfield('f', 'No online access'), local:subfield('2', 'star') ) )"/>
			</xsl:when>
			<xsl:when test="$visibility eq 'audience'">
				<xsl:sequence select="local:datafield('506', '1', ( local:subfield('a', 'Access only to restricted audience'), local:subfield('f', 'Online access with authorization'), local:subfield('2', 'star') ) )"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">INFO</xsl:with-param>
					<xsl:with-param name="message">[pubman_to__marc.xsl#named-local_make-506] no rule for converting prop:visibility="
						<xsl:value-of select="$visibility"/>" to MARC field 506
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:function name="local:leader-template" as="element(marc:leader)">
		<xsl:param name="record-length" as="xs:string"/>
		<xsl:param name="record-status" as="xs:string"/>
		<xsl:param name="type-of-record" as="xs:string"/>
		<xsl:param name="bibliographic-level" as="xs:string"/>
		<xsl:param name="type-of-control" as="xs:string"/>
		<xsl:param name="character-coding-scheme" as="xs:string"/>
		<xsl:param name="indicator-count" as="xs:string"/>
		<xsl:param name="subfield-code-count" as="xs:string"/>
		<xsl:param name="base-address-of-data" as="xs:string"/>
		<xsl:param name="encoding-level" as="xs:string"/>
		<xsl:param name="descriptive-cataloging-form" as="xs:string"/>
		<xsl:param name="multipart-resource-record-level" as="xs:string"/>
		<xsl:param name="length-of-the-length-of-field-portion" as="xs:string"/>
		<xsl:param name="length-of-the-starting-character-position-portion" as="xs:string"/>
		<xsl:param name="length-of-the-implementation-defined-portion" as="xs:string"/>
		<xsl:param name="undefined" as="xs:string"/>
		<marc:leader>
			<xsl:value-of select="concat( $record-length, $record-status, $type-of-record, $bibliographic-level, $type-of-control, $character-coding-scheme, $indicator-count, $subfield-code-count, $base-address-of-data, $encoding-level, $descriptive-cataloging-form, $multipart-resource-record-level, $length-of-the-length-of-field-portion, $length-of-the-starting-character-position-portion, $length-of-the-implementation-defined-portion, $undefined )"/>
		</marc:leader>
	</xsl:function>
	<xsl:function name="local:leader" as="element(marc:leader)">
		<xsl:param name="type-of-record" as="xs:string"/>
		<xsl:param name="bibliographic-level" as="xs:string"/>
		<xsl:param name="multipart-resource-record-level" as="xs:string"/>
		<xsl:sequence select="local:leader-template( '     ', 'n', $type-of-record, $bibliographic-level, ' ', 'a', '2', '2', '     ', 'u', 'u', $multipart-resource-record-level, '4', '5', '0', '0' )"/>
	</xsl:function>
	<xsl:function name="local:controlfield" as="element(marc:controlfield)">
		<xsl:param name="tag" as="xs:string"/>
		<xsl:param name="content" as="xs:string"/>
		<marc:controlfield tag="{$tag}">
			<xsl:sequence select="$content"/>
		</marc:controlfield>
	</xsl:function>
	<xsl:function name="local:datafield" as="element(marc:datafield)">
		<xsl:param name="tag" as="xs:string"/>
		<xsl:param name="ind1" as="xs:string"/>
		<xsl:param name="ind2" as="xs:string"/>
		<xsl:param name="subfields" as="element(marc:subfield)+"/>
		<marc:datafield tag="{$tag}" ind1="{$ind1}" ind2="{$ind2}">
			<xsl:sequence select="$subfields"/>
		</marc:datafield>
	</xsl:function>
	<xsl:function name="local:datafield" as="element(marc:datafield)">
		<xsl:param name="tag" as="xs:string"/>
		<xsl:param name="ind1" as="xs:string"/>
		<xsl:param name="subfields" as="element(marc:subfield)+"/>
		<xsl:sequence select="local:datafield($tag, $ind1, ' ', $subfields)"/>
	</xsl:function>
	<xsl:function name="local:datafield" as="element(marc:datafield)">
		<xsl:param name="tag" as="xs:string"/>
		<xsl:param name="subfields" as="element(marc:subfield)+"/>
		<xsl:sequence select="local:datafield($tag, ' ', ' ', $subfields)"/>
	</xsl:function>
	<xsl:function name="local:subfield" as="element(marc:subfield)">
		<xsl:param name="code" as="xs:string"/>
		<xsl:param name="content" as="xs:string"/>
		<marc:subfield code="{$code}">
			<xsl:value-of select="$content"/>
		</marc:subfield>
	</xsl:function>
	<xsl:function name="local:controlfield-001" as="element(marc:controlfield)">
		<xsl:param name="objid" as="xs:string?"/>
		<xsl:variable name="id" as="xs:string">
			<xsl:choose>
				<xsl:when test="matches($objid, '^item_[0-9]+$')">
					<xsl:sequence select="translate($objid, ':', '')"/>
				</xsl:when>
				<xsl:when test="normalize-space($objid)">
					<xsl:sequence select="$objid"/>
				</xsl:when>
				<xsl:when test="$misc:run-in-testmode">
					<xsl:sequence select="'run-in-testmode:fixed'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:sequence select="concat('genericid', format-dateTime(current-dateTime(), '[Y0001][M01][D01][H01][m01][s01][f01]'))"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:sequence select="local:controlfield('001', $id)"/>
	</xsl:function>
	<xsl:function name="local:controlfield-005" as="element(marc:controlfield)">
		<xsl:choose>
			<xsl:when test="$misc:run-in-testmode">
				<xsl:sequence select="local:controlfield('005', '20120907201307.78')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="local:controlfield('005', format-dateTime(current-dateTime(), '[Y0004][M02][D02][H02][m02][s02].[f02]') )"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:controlfield-008-template" as="element(marc:controlfield)">
		<xsl:param name="date-entered-on-file" as="xs:string"/>
		<xsl:param name="type-of-date_publication-status" as="xs:string"/>
		<xsl:param name="date-1" as="xs:string"/>
		<xsl:param name="date-2" as="xs:string"/>
		<xsl:param name="place-of-publication_production_execution" as="xs:string"/>
		<xsl:param name="type-dependent" as="xs:string"/>
		<xsl:param name="language" as="xs:string"/>
		<xsl:param name="modified-record" as="xs:string"/>
		<xsl:param name="cataloging-source" as="xs:string"/>
		<xsl:sequence select="local:controlfield('008', concat( $date-entered-on-file, $type-of-date_publication-status, $date-1, $date-2, $place-of-publication_production_execution, $type-dependent, $language, $modified-record, $cataloging-source ))"/>
	</xsl:function>
	<xsl:function name="local:controlfield-008-wrapper" as="element(marc:controlfield)">
		<xsl:param name="date-entered-on-file" as="xs:string"/>
		<xsl:param name="type-of-date_publication-status" as="xs:string"/>
		<xsl:param name="date-1" as="xs:string"/>
		<xsl:param name="type-specific" as="xs:string"/>
		<xsl:param name="language" as="xs:string"/>
		<xsl:sequence select="local:controlfield-008-template( $date-entered-on-file, $type-of-date_publication-status, $date-1, ' ', 'xx ', $type-specific, $language, ' ', '|' )"/>
	</xsl:function>
	<xsl:function name="local:cf008-type-specific-books" as="xs:string">
		<xsl:param name="form-of-item" as="xs:string"/>
		<xsl:param name="nature-of-contents" as="xs:string"/>
		<xsl:param name="conference-publication" as="xs:string"/>
		<xsl:param name="festschrift" as="xs:string"/>
		<xsl:sequence select="concat('||||', '|', $form-of-item, $nature-of-contents, '|', $conference-publication, $festschrift, '|', ' ', '|', '|')"/>
	</xsl:function>
	<xsl:function name="local:cf008-type-specific-continuing-resources" as="xs:string">
		<xsl:param name="type-of-continuing-resource" as="xs:string"/>
		<xsl:param name="form-of-item" as="xs:string"/>
		<xsl:param name="nature-of-contents" as="xs:string"/>
		<xsl:param name="conference-publication" as="xs:string"/>
		<xsl:sequence select="concat('|| ', $type-of-continuing-resource, '|', $form-of-item, '|', $nature-of-contents, '|', $conference-publication, ' ||')"/>
	</xsl:function>
	<xsl:function name="local:type-of-continuing-resource" as="xs:string">
		<xsl:param name="pubman_publication_type" as="xs:string?"/>
		<xsl:choose>
			<xsl:when test="$pubman_publication_type eq 'http://purl.org/escidoc/metadata/ves/publication-types/series' ">m</xsl:when>
			<xsl:when test="$pubman_publication_type eq 'http://purl.org/escidoc/metadata/ves/publication-types/newspaper' ">n</xsl:when>
			<xsl:when test="$pubman_publication_type eq 'http://purl.org/escidoc/metadata/ves/publication-types/journal' ">p</xsl:when>
			<xsl:otherwise>|</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:form-of-item" as="xs:string">
		<xsl:param name="publication:publication" as="element(publication:publication)"/>
		<xsl:choose>
			<xsl:when test="some $i in $publication:publication/dc:identifier[misc:dc_identifier-is-online-resource(.)] satisfies normalize-space($i)">
				<xsl:sequence select="'o'"/>
			</xsl:when>
			<xsl:when test="some $i in $publication:publication/../../../escidocComponents:components/escidocComponents:component satisfies normalize-space(local:href($i) )">
				<xsl:sequence select="'o'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="'r'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:nature-of-contents" as="xs:string">
		<xsl:param name="pubman_publication_type" as="xs:string?"/>
		<xsl:param name="length" as="xs:integer"/>
		<xsl:sequence select="tools:fill-right(misc-marc:pubman_publication-type-to-marc_nature-of-contents($pubman_publication_type), ' ', $length)"/>
	</xsl:function>
	<xsl:function name="local:lang" as="xs:string" xml:id="local:lang">
		<xsl:param name="dc:language" as="element(dc:language)?"/>
		<xsl:variable name="cleaned" as="xs:string" select="normalize-space($dc:language)"/>
		<xsl:choose>
			<xsl:when test="$dc:language/@xsi:type eq 'dcterms:ISO639-3' and normalize-space(misc:iso-639-3_to_iso-639-2($cleaned))">
				<xsl:sequence select="misc:iso-639-3_to_iso-639-2($cleaned)"/>
			</xsl:when>
			<xsl:when test="$dc:language/@xsi:type eq 'dcterms:ISO639-2' and $cleaned">
				<xsl:sequence select="$cleaned"/>
			</xsl:when>
			<xsl:when test="misc:is-iso-639-2-b($cleaned)">
				<xsl:sequence select="$cleaned"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="''"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:eterms_creator-is-1xx" as="xs:boolean">
		<xsl:param name="eterms_creator" as="element(eterms:creator)"/>
		<xsl:choose>
			<xsl:when test="$eterms_creator/self::eterms:creator[misc:eterms_creator-is-author(.)] and not(local:eterms_creator-is-7xx($eterms_creator) )">
				<xsl:sequence select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:eterms_creator-is-7xx" as="xs:boolean">
		<xsl:param name="eterms_creator" as="element(eterms:creator)"/>
		<xsl:choose>
			<xsl:when test="$eterms_creator/self::eterms:creator[misc:eterms_creator-is-author(.)][preceding-sibling::eterms:creator[misc:eterms_creator-is-author(.)]]| $eterms_creator/self::eterms:creator[misc:eterms_creator-is-editor(.)]">
				<xsl:sequence select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:eterms_creator-is-7xx-other" as="xs:boolean">
		<xsl:param name="eterms_creator" as="element(eterms:creator)"/>
		<xsl:choose>
			<xsl:when test="local:eterms_creator-is-1xx($eterms_creator) or local:eterms_creator-is-7xx($eterms_creator)">
				<xsl:sequence select="false()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="true()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:publication-date" as="xs:string?" xml:id="local:publication-date">
		<xsl:param name="publication:publication" as="element(publication:publication)"/>
		<xsl:choose>
			<xsl:when test="normalize-space($publication:publication/dcterms:issued)">
				<xsl:sequence select="normalize-space($publication:publication/dcterms:issued)"/>
			</xsl:when>
			<xsl:when test="normalize-space($publication:publication/eterms:published-online)">
				<xsl:sequence select="normalize-space($publication:publication/eterms:published-online)"/>
			</xsl:when>
			<xsl:when test="normalize-space($publication:publication/dcterms:dateAccepted)">
				<xsl:sequence select="normalize-space($publication:publication/dcterms:dateAccepted)"/>
			</xsl:when>
			<xsl:when test="normalize-space($publication:publication/dcterms:dateSubmitted)">
				<xsl:sequence select="normalize-space($publication:publication/dcterms:dateSubmitted)"/>
			</xsl:when>
			<xsl:when test="normalize-space($publication:publication/dcterms:modified)">
				<xsl:sequence select="normalize-space($publication:publication/dcterms:modified)"/>
			</xsl:when>
			<xsl:when test="normalize-space($publication:publication/dcterms:created)">
				<xsl:sequence select="normalize-space($publication:publication/dcterms:created)"/>
			</xsl:when>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:render-place-publisher-date" as="xs:string?">
		<xsl:param name="place" as="xs:string?"/>
		<xsl:param name="publisher" as="xs:string?"/>
		<xsl:param name="date" as="xs:string?"/>
		<xsl:variable name="_place" as="xs:string" select="normalize-space($place)"/>
		<xsl:variable name="_publisher" as="xs:string" select="normalize-space($publisher)"/>
		<xsl:variable name="_date" as="xs:string" select="normalize-space($date)"/>
		<xsl:choose>
			<xsl:when test="$_place and $_publisher and $_date">
				<xsl:sequence select="concat($_place, ': ', $_publisher, ', ', $_date)"/>
			</xsl:when>
			<xsl:when test="$_place and $_publisher">
				<xsl:sequence select="concat($_place, ': ', $_publisher)"/>
			</xsl:when>
			<xsl:when test="$_place and $_date">
				<xsl:sequence select="concat($_place, ', ', $_date)"/>
			</xsl:when>
			<xsl:when test="$_place">
				<xsl:sequence select="$_place"/>
			</xsl:when>
			<xsl:when test="$_publisher and $_date">
				<xsl:sequence select="concat($_publisher, ', ', $_date)"/>
			</xsl:when>
			<xsl:when test="$_publisher">
				<xsl:sequence select="$_publisher"/>
			</xsl:when>
			<xsl:when test="$_date">
				<xsl:sequence select="$_date"/>
			</xsl:when>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:pages" as="xs:string?">
		<xsl:param name="source:source" as="element(source:source)?"/>
		<xsl:choose>
			<xsl:when test="normalize-space($source:source/eterms:start-page) and normalize-space($source:source/eterms:end-page)">
				<xsl:sequence select="concat(normalize-space($source:source/eterms:start-page), '-', normalize-space($source:source/eterms:end-page))"/>
			</xsl:when>
			<xsl:when test="normalize-space($source:source/eterms:start-page)">
				<xsl:sequence select="normalize-space($source:source/eterms:start-page)"/>
			</xsl:when>
			<xsl:when test="normalize-space($source:source/source:source/eterms:start-page) and normalize-space($source:source/source:source/eterms:end-page)">
				<xsl:sequence select="concat(normalize-space($source:source/source:source/eterms:start-page), '-', normalize-space($source:source/source:source/eterms:end-page))"/>
			</xsl:when>
			<xsl:when test="normalize-space($source:source/source:source/eterms:start-page)">
				<xsl:sequence select="normalize-space($source:source/source:source/eterms:start-page)"/>
			</xsl:when>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:source_source-is-series" as="xs:boolean">
		<xsl:param name="source:source" as="element(source:source)"/>
		<xsl:sequence select="boolean($source:source[@type eq 'http://purl.org/escidoc/metadata/ves/publication-types/series'])"/>
	</xsl:function>
	<xsl:function name="local:bibliographic-level" as="xs:string">
		<xsl:param name="publication_publication-or-source_source" as="element()"/>
		<xsl:variable name="level-temp" as="xs:string?" select="misc-marc:pubman_publication-type-to-marc_bibliographic-level($publication_publication-or-source_source/@type)[normalize-space(.)]"/>
		<xsl:variable name="level-source" as="xs:string?" select="if ($publication_publication-or-source_source/source:source) then local:bibliographic-level($publication_publication-or-source_source/source:source[1])[normalize-space(.)] else ()"/>
		<xsl:variable name="level-default" as="xs:string?" select="tokenize($level-temp, '/')[1]"/>
		<xsl:choose>
			<xsl:when test="string-length($level-temp) eq 1">
				<xsl:sequence select="$level-temp"/>
			</xsl:when>
			<xsl:when test="normalize-space( string-join( ( $publication_publication-or-source_source/source:source/eterms:start-page| $publication_publication-or-source_source/source:source/eterms:end-page ) , '' ) )">
				<xsl:sequence select=" 'a' "/>
			</xsl:when>
			<xsl:when test="$level-source = ('a', 'b', 'm', 's')">
				<xsl:sequence select=" 'a' "/>
			</xsl:when>
			<xsl:when test="$level-default">
				<xsl:sequence select="$level-default"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select=" ' ' "/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:href" as="xs:string?" xml:id="fn-local-href">
		<xsl:param name="escidocComponents:component" as="element(escidocComponents:component)"/>
		<xsl:variable name="version-objid" as="xs:string?" select="normalize-space($escidocComponents:component/../../escidocItem:properties[1]/prop:version[1]/@objid)"/>
		<xsl:variable name="component-objid" as="xs:string?" select="normalize-space($escidocComponents:component/@objid)"/>
		<xsl:variable name="file-name" as="xs:string?" select="normalize-space($escidocComponents:component[1]/escidocMetadataRecords:md-records[1]/escidocMetadataRecords:md-record[1]/file:file[1]/dc:title[1])"/>
		<xsl:choose>
			<xsl:when test="$version-objid and $component-objid and $file-name">
				<xsl:sequence select="concat($pubmanUrl, '/item/', $version-objid, '/component/', $component-objid, '/', $file-name)"/>
			</xsl:when>
			<xsl:when test="not(normalize-space($component-objid))">
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">INFO</xsl:with-param>
					<xsl:with-param name="message">[pubman_to__marc.xsl#fn-local-href] escidocComponents:component without objid at escidocItem:item with objid»
						<xsl:value-of select="$escidocComponents:component/ancestor::escidocItem:item/@objid"/>«
					</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">INFO</xsl:with-param>
					<xsl:with-param name="message">[pubman_to__marc.xsl#fn-local-href] could not retrieve the PubMan-URI of component with objid »
						<xsl:value-of select="$component-objid"/>«
					</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:tokenize-subject" as="xs:string*">
		<xsl:param name="text" as="xs:string?"/>
		<xsl:variable name="normalized" as="xs:string" select="normalize-space($text)"/>
		<xsl:choose>
			<xsl:when test="matches($normalized, '; ')">
				<xsl:sequence select="for $i in tokenize($normalized, '; ')[normalize-space(.)] return normalize-space($i)"/>
			</xsl:when>
			<xsl:when test="matches($normalized, ', ')">
				<xsl:sequence select="for $i in tokenize($normalized, ', ')[normalize-space(.)] return normalize-space($i)"/>
			</xsl:when>
			<xsl:when test="matches($normalized, '–')">
				<xsl:sequence select="for $i in tokenize($normalized, '–')[normalize-space(.)] return normalize-space($i)"/>
			</xsl:when>
			<xsl:when test="matches($normalized, ' - ')">
				<xsl:sequence select="for $i in tokenize($normalized, ' - ')[normalize-space(.)] return normalize-space($i)"/>
			</xsl:when>
			<xsl:when test="matches($text, '^', 'm')">
				<xsl:sequence select="for $i in tokenize($text, '^', 'm')[normalize-space(.)] return normalize-space($i)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="$text[normalize-space(.)]"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:make-542" as="element(marc:datafield)*" xml:id="fct_local-make-542">
		<xsl:param name="publication:publication" as="element(publication:publication)?"/>
		<xsl:variable name="datafields" as="element(root)">
			<root>
				<xsl:for-each select="$publication:publication/../../../escidocComponents:components/escidocComponents:component/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file[normalize-space(concat(dc:rights, dcterms:dateCopyrighted))]">
					<xsl:variable name="subfields" as="element(marc:subfield)*">
						<xsl:if test="normalize-space(dc:rights)">
							<xsl:sequence select="local:subfield('f', normalize-space(dc:rights))"/>
						</xsl:if>
						<xsl:if test="normalize-space(dcterms:dateCopyrighted)">
							<xsl:sequence select="local:subfield('g', normalize-space(dcterms:dateCopyrighted))"/>
						</xsl:if>
					</xsl:variable>
					<xsl:sequence select="local:datafield('542', $subfields)"/>
				</xsl:for-each>
			</root>
		</xsl:variable>
		<xsl:for-each-group select="$datafields/*" group-by="concat(marc:subfield[@code eq 'f'], marc:subfield[@code eq 'g'])">
			<xsl:sequence select="current-group()[1]"/>
		</xsl:for-each-group>
	</xsl:function>
	<xsl:function name="local:write-8XX" as="xs:boolean">
		<xsl:param name="source:source" as="element(source:source)"/>
		<xsl:choose>
			<xsl:when test="$source:source/@type = ( ('http://purl.org/escidoc/metadata/ves/publication-types/series'), ('http://purl.org/escidoc/metadata/ves/publication-types/journal'), ('http://purl.org/escidoc/metadata/ves/publication-types/newspaper') )">
				<xsl:sequence select="false()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="true()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
</xsl:stylesheet>