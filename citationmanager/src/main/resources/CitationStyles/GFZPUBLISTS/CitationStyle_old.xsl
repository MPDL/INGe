<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:cit="http://www.escidoc.de/citationstyle" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:jfunc="java:de.mpg.mpdl.inge.citationmanager.utils.XsltHelper" xmlns:func="http://www.escidoc.de/citationstyle/functions" xmlns:functx="http://www.functx.com" xmlns:ei="http://www.escidoc.de/schemas/item/0.10" xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.5" xmlns:mdp="http://escidoc.mpg.de/metadataprofile/schema/0.1/" xmlns:pub="http://purl.org/escidoc/metadata/profiles/0.1/publication" xmlns:e="http://purl.org/escidoc/metadata/terms/0.1/" xmlns:prop="http://escidoc.de/core/01/properties/" xmlns:escidocComponents="http://www.escidoc.de/schemas/components/0.9" xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source" xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/" xmlns:event="http://purl.org/escidoc/metadata/profiles/0.1/event"
    xmlns:organization="http://purl.org/escidoc/metadata/profiles/0.1/organization" xmlns:person="http://purl.org/escidoc/metadata/profiles/0.1/person" xmlns:legalCase="http://purl.org/escidoc/metadata/profiles/0.1/legal-case" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
    <xsl:output method="xml" encoding="UTF-8" indent="yes" cdata-section-elements="dcterms:bibliographicCitation dcterms:abstract"></xsl:output>
    <xsl:param name="pubmanUrl"></xsl:param>
    <xsl:param name="instanceUrl"></xsl:param>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"></xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="prop:content-model-specific">
        <xsl:element name="{name(.)}">
            <xsl:copy-of select="child::node()"></xsl:copy-of>
            <xsl:element name="dcterms:bibliographicCitation">
                <xsl:variable name="citation">
                    <xsl:for-each select="../../mdr:md-records/mdr:md-record">
                        <xsl:variable name="l_journal">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/journal'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_article">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/article'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_issue">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/issue'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_book">
                            <xsl:value-of select="'http://purl.org/eprint/type/Book'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_book-item">
                            <xsl:value-of select="'http://purl.org/eprint/type/BookItem'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_proceedings">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/proceedings'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_conference-paper">
                            <xsl:value-of select="'http://purl.org/eprint/type/ConferencePaper'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_conference-report">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/conference-report'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_poster">
                            <xsl:value-of select="'http://purl.org/eprint/type/ConferencePoster'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_report">
                            <xsl:value-of select="'http://purl.org/eprint/type/Report'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_paper">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/paper'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_talk-at-event">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_courseware-lecture">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/courseware-lecture'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_thesis">
                            <xsl:value-of select="'http://purl.org/eprint/type/Thesis'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_series">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/series'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_manuscript">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/manuscript'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_other">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/other'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_monograph">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/monograph'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_contr-to-collect-ed">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-collected-edition'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_case-note">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/case-note'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_opinion">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/opinion'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_case-study">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/case-study'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_book-review">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/book-review'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_contr-to-commentary">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-commentary'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_contr-to-festschrift">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-festschrift'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_contr-to-handbook">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-handbook'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_contr-to-encyclopedia">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-encyclopedia'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_newspaper-article">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/newspaper-article'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_collected-edition">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/collected-edition'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_commentary">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/commentary'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_festschrift">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/festschrift'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_handbook">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/handbook'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_editorial">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/editorial'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_patent">
                            <xsl:value-of select="'http://purl.org/eprint/type/Patent'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_author">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/AUT'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_artist">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/ART'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_editor">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/EDT'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_painter">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/creator-roles/painter'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_photographer">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/PHT'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_illustrator">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/ILL'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_commentator">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/CMM'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_transcriber">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/TRC'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_translator">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/TRL'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_advisor">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/SAD'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_advisor">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/THS'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_contributor">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/CTB'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_publisher">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/creator-roles/publisher'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_honoree">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/HNR'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_contributor">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/creator-roles/founder'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_contributor">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/creator-roles/referee'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_master">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/academic-degrees/master'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_diploma">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/academic-degrees/diploma'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_magister">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/academic-degrees/magister'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_staatsexamen">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/academic-degrees/staatsexamen'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_phd">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/academic-degrees/phd'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_habilitation">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/academic-degrees/habilitation'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_bachelor">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/academic-degrees/bachelor'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="v_degree">
                            <xsl:value-of select="pub:publication/eterms:degree/text()"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="l_degree">
                            <xsl:value-of select="&#xA;&#x9;&#x9;if ($v_degree=$l_master) then 'Master' else&#xA;&#x9;&#x9;if ($v_degree=$l_diploma) then 'Diploma' else&#xA;&#x9;&#x9;if ($v_degree=$l_magister) then 'Magister' else&#xA;&#x9;&#x9;if ($v_degree=$l_staatsexamen) then 'Staatsexamen' else&#xA;&#x9;&#x9;if ($v_degree=$l_phd) then 'PhD' else&#xA;&#x9;&#x9;if ($v_degree=$l_habilitation) then 'Habilitation' else&#xA;&#x9;&#x9;if ($v_degree=$l_bachelor) then 'Bachelor' else ''&#xA;&#x9;"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="apa_default_variable">
                            <xsl:value-of select="'APA specific default variable'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="objid">
                            <xsl:value-of select="../../@objid"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="genre">
                            <xsl:value-of select="pub:publication/@type"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="source-type">
                            <xsl:value-of select="pub:publication/source:source[1]/@type"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="hasPublication" as="xs:boolean">
                            <xsl:value-of select="exists(pub:publication)"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="authorsCount">
                            <xsl:value-of select="count(pub:publication/eterms:creator[@role = $l_author and boolean(person:person)])"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="editorsCount">
                            <xsl:value-of select="count(pub:publication/eterms:creator[@role = $l_editor and boolean(person:person)])"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="ed-postfix">
                            <xsl:value-of select="
                                    if ($editorsCount = 1) then
                                        'Ed.'
                                    else
                                        'Eds.'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="sourceEditorsCount">
                            <xsl:value-of select="count(pub:publication/source:source[1]/eterms:creator[@role = $l_editor])"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="source-ed-postfix">
                            <xsl:value-of select="
                                    if ($sourceEditorsCount = 1) then
                                        'Ed.'
                                    else
                                        'Eds.'"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="genre-exception" as="xs:boolean">
                            <xsl:value-of select="$genre = ($l_poster, $l_talk-at-event, $l_courseware-lecture)"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="date">
                            <xsl:value-of
                                select="
                                    if ($genre = $l_manuscript and pub:publication/dcterms:created) then
                                        func:get_year(pub:publication/dcterms:created)
                                    else
                                        if ($genre-exception and pub:publication/event:event/eterms:start-date) then
                                            func:get_year(pub:publication/event:event/eterms:start-date)
                                        else
                                            if (pub:publication/dcterms:issued) then
                                                func:get_year(pub:publication/dcterms:issued)
                                            else
                                                if (pub:publication/eterms:published-online and $genre = $l_patent) then
                                                    func:get_year(pub:publication/eterms:published-online)
                                                else
                                                    if (pub:publication/eterms:published-online and not($genre = $l_patent)) then
                                                        concat(func:get_year(pub:publication/eterms:published-online), ' online')
                                                    else
                                                        if (($genre-exception and $genre = $l_manuscript) and pub:publication/dcterms:dateAccepted) then
                                                            func:get_year(pub:publication/dcterms:dateAccepted)
                                                        else
                                                            if (pub:publication/dcterms:dateAccepted) then
                                                                'in press'
                                                            else
                                                                if (($genre-exception or $genre = $l_manuscript) and pub:publication/dcterms:dateSubmitted) then
                                                                    func:get_year(pub:publication/dcterms:dateSubmitted)
                                                                else
                                                                    if (pub:publication/dcterms:dateSubmitted) then
                                                                        'submitted'
                                                                    else
                                                                        if (($genre-exception or $genre = $l_manuscript) and pub:publication/dcterms:modified) then
                                                                            func:get_year(pub:publication/dcterms:modified)
                                                                        else
                                                                            if (($genre-exception or $genre = $l_manuscript) and pub:publication/dcterms:created) then
                                                                                func:get_year(pub:publication/dcterms:created)
                                                                            else
                                                                                if (pub:publication/dcterms:modified or pub:publication/dcterms:created) then
                                                                                    'in preparation'
                                                                                else
                                                                                    if (not($genre = ($l_journal, $l_series))) then
                                                                                        'n.d.'
                                                                                    else
                                                                                        ''"
                            ></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="start-date-or-date">
                            <xsl:value-of select="&#xA;&#x9;&#x9;&#x9;if (pub:publication/event:event/eterms:start-date) &#xA;&#x9;&#x9;&#x9;then pub:publication/event:event/eterms:start-date&#xA;&#x9;&#x9;&#x9;else $date&#xA;&#x9;&#x9;"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="date-for-thesis">
                            <xsl:value-of select="&#xA;&#x9;&#x9;&#x9;if (pub:publication/dcterms:issued)&#xA;&#x9;&#x9;&#x9;then func:get_year(pub:publication/dcterms:issued) &#xA;&#x9;&#x9;&#x9;else if (pub:publication/dcterms:dateAccepted)&#xA;&#x9;&#x9;&#x9;then func:get_year(pub:publication/dcterms:dateAccepted) &#xA;&#x9;&#x9;&#x9;else if (pub:publication/eterms:published-online)&#xA;&#x9;&#x9;&#x9;then func:get_year(pub:publication/eterms:published-online) &#xA;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="notPublishedRule" as="xs:boolean">
                            <xsl:value-of select="&#xA;&#x9;&#x9;&#x9;$date = ( 'submitted', 'in preparation') and&#xA;&#x9;&#x9;&#x9;not( $genre = ($l_manuscript, $l_courseware-lecture, $l_talk-at-event, $l_poster) )&#xA;&#x9;&#x9;"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="degree">
                            <xsl:value-of select="&#xA;&#x9;&#x9;&#x9;concat (&#xA;&#x9;&#x9;&#x9;&#x9;(if (not($v_degree))&#xA;&#x9;&#x9;&#x9;&#x9;then ''&#xA;&#x9;&#x9;&#x9;&#x9;else if ($v_degree=$l_phd)&#xA;&#x9;&#x9;&#x9;&#x9;then 'PhD '&#xA;&#x9;&#x9;&#x9;&#x9;else concat ($l_degree, ' ')),&#xA;&#x9;&#x9;&#x9;&#x9;'Thesis'&#xA;&#x9;&#x9;&#x9;)&#x9;&#x9;&#xA;&#x9;&#x9;"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="doi">
                            <xsl:value-of select="(pub:publication/dc:identifier[@xsi:type = 'eterms:DOI'])[1]/text()"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="urn">
                            <xsl:value-of select="(pub:publication/dc:identifier[@xsi:type = 'eterms:URN'])[1]/text()"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="doiLink">
                            <xsl:if test="($doi != '')">
                                <xsl:text>&lt;br/&gt;&lt;a href="https://doi.org/</xsl:text>
                                <xsl:value-of select="$doi"></xsl:value-of>
                                <xsl:text>" target="_blank"&gt;https://doi.org/</xsl:text>
                                <xsl:value-of select="$doi"></xsl:value-of>
                                <xsl:text>&lt;/a&gt;</xsl:text>
                            </xsl:if>
                        </xsl:variable>
                        <xsl:variable name="doi">
                            <xsl:value-of select="$doiLink"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="urn">
                            <xsl:if test="($urn != '')">
                                <xsl:text>&lt;br/&gt;URN: &lt;a href="http://nbn-resolving.de/urn/resolver.pl?urn=</xsl:text>
                                <xsl:value-of select="$urn"></xsl:value-of>
                                <xsl:text>" target="_blank"&gt;http://nbn-resolving.de/urn/resolver.pl?urn=</xsl:text>
                                <xsl:value-of select="$urn"></xsl:value-of>
                                <xsl:text>&lt;/a&gt;</xsl:text>
                            </xsl:if>
                        </xsl:variable>
                        <xsl:variable name="uri">
                            <xsl:value-of select="(pub:publication/dc:identifier[@xsi:type = 'eterms:URI'])[1]/text()"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="sourceSeriesEnumeration">
                            <xsl:for-each select="pub:publication/source:source[@type = 'http://purl.org/escidoc/metadata/ves/publication-types/series']">
                                <xsl:variable name="var" select="dc:title"></xsl:variable>
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:text>(</xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"></xsl:copy-of>
                                <xsl:if test="eterms:volume and eterms:volume != ''">
                                    <xsl:text>&#160;;&#160;</xsl:text>
                                    <xsl:value-of select="eterms:volume"></xsl:value-of>
                                </xsl:if>
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:text>)</xsl:text>
                                </xsl:if>
                                <xsl:if test="position() &lt; last()">
                                    <xsl:text></xsl:text>
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:variable>
                        <xsl:variable name="eventData">
                            <xsl:for-each select="pub:publication/event:event">
                                <xsl:value-of select="dc:title"></xsl:value-of>
                                <xsl:if test="eterms:place != '' or eterms:start-date != '' or eterms:end-date != ''">
                                    <xsl:text> (</xsl:text>
                                    <xsl:call-template name="applyDelimiter">
                                        <xsl:with-param name="les">
                                            <le>
                                                <xsl:value-of select="eterms:place"></xsl:value-of>
                                            </le>
                                            <le>
                                                <xsl:value-of select="substring(eterms:start-date, 1, 4)"></xsl:value-of>
                                            </le>
                                        </xsl:with-param>
                                        <xsl:with-param name="delimiter" select="' '"></xsl:with-param>
                                    </xsl:call-template>
                                    <xsl:text>)</xsl:text>
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:variable>
                        <xsl:variable name="publisherData">
                            <xsl:value-of select="pub:publication/eterms:publishing-info/eterms:place/text()"></xsl:value-of>
                            <xsl:if test="pub:publication/eterms:publishing-info/eterms:place and pub:publication/eterms:publishing-info/dc:publisher">
                                <xsl:text>&#160;:&#160;</xsl:text>
                            </xsl:if>
                            <xsl:value-of select="pub:publication/eterms:publishing-info/dc:publisher/text()"></xsl:value-of>
                        </xsl:variable>
                        <xsl:variable name="pageCount">
                            <xsl:if test="pub:publication/eterms:total-number-of-pages != ''">
                                <xsl:value-of select="pub:publication/eterms:total-number-of-pages"></xsl:value-of>
                                <xsl:text>&#160;p.</xsl:text>
                            </xsl:if>
                        </xsl:variable>
                        <xsl:variable name="ed-postfix-i18n">
                            <xsl:variable name="var" select="''"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="$ed-postfix"></xsl:variable>
                                                <xsl:variable name="var">
                                                    <xsl:if test="$editorsCount = 1">
                                                        <xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text>&lt;localized class="editor"&gt;</xsl:text>
                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                <xsl:text>&lt;/localized&gt;</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="$ed-postfix"></xsl:variable>
                                                <xsl:variable name="var">
                                                    <xsl:if test="$editorsCount &gt; 1">
                                                        <xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text>&lt;localized class="editors"&gt;</xsl:text>
                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                <xsl:text>&lt;/localized&gt;</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"></xsl:with-param>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="source-ed-postfix-i18n">
                            <xsl:variable name="var" select="''"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="$source-ed-postfix"></xsl:variable>
                                                <xsl:variable name="var">
                                                    <xsl:if test="$sourceEditorsCount = 1">
                                                        <xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text>&lt;localized class="editor"&gt;</xsl:text>
                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                <xsl:text>&lt;/localized&gt;</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="$source-ed-postfix"></xsl:variable>
                                                <xsl:variable name="var">
                                                    <xsl:if test="$sourceEditorsCount &gt; 1">
                                                        <xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text>&lt;localized class="editors"&gt;</xsl:text>
                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                <xsl:text>&lt;/localized&gt;</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"></xsl:with-param>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="year">
                            <xsl:variable name="var" select="$date"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:text>(</xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"></xsl:copy-of>
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:text>): </xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="year-for-thesis">
                            <xsl:variable name="var" select="$date-for-thesis"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:text>(</xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"></xsl:copy-of>
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:text>).</xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="year-with-event-start-date">
                            <xsl:variable name="var" select="''"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="$start-date-or-date"></xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"></xsl:with-param>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:text>(</xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"></xsl:copy-of>
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:text>).</xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="year-authors-or-editors-are-presented">
                            <xsl:variable name="var" select="$year"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="$authorsCount &gt; 0 or $editorsCount &gt; 0">
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="year-authors-or-editors-are-not-presented">
                            <xsl:variable name="var" select="$year"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="$authorsCount = 0 and $editorsCount = 0">
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="year-and-month-name">
                            <xsl:variable name="var" select="''"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="''"></xsl:variable>
                                                <xsl:variable name="var">
                                                    <xsl:if test="pub:publication/dcterms:issued">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="func:get_year(pub:publication/dcterms:issued/text())"></xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="func:get_month_name(pub:publication/dcterms:issued/text())"></xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="$date"></xsl:variable>
                                                <xsl:variable name="var">
                                                    <xsl:if test="not(pub:publication/dcterms:issued)">
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"></xsl:with-param>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:text>(</xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"></xsl:copy-of>
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:text>).</xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="year-and-month-authors-are-presented">
                            <xsl:variable name="var" select="$year-and-month-name"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="$authorsCount &gt; 0">
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="year-and-month-authors-are-not-presented">
                            <xsl:variable name="var" select="$year-and-month-name"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="$authorsCount = 0">
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="year-editors-are-presented">
                            <xsl:variable name="var" select="$year"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="$editorsCount &gt; 0">
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="year-editors-are-not-presented">
                            <xsl:variable name="var" select="$year"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="$editorsCount = 0">
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="title-with-dot">
                            <xsl:variable name="var" select="func:escapeMarkupTags(pub:publication/dc:title/text())"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:text></xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"></xsl:copy-of>
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:text>.</xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="title-italic">
                            <xsl:variable name="var" select="func:escapeMarkupTags(pub:publication/dc:title/text())"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="title-with-dot-italic">
                            <xsl:variable name="var" select="$title-italic"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:text></xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"></xsl:copy-of>
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:text>.</xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="start-page-end-page">
                            <xsl:variable name="var" select="''"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="pub:publication/source:source[1]/eterms:start-page/text()"></xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="pub:publication/source:source[1]/eterms:end-page/text()"></xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="'-'"></xsl:with-param>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:choose>
                                <xsl:when test="$var != ''">
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:variable name="var" select="pub:publication/source:source[1]/eterms:sequence-number/text()"></xsl:variable>
                                    <xsl:if test="$var != ''">
                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                    </xsl:if>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="source-edition-start-page-end-page">
                            <xsl:variable name="var" select="''"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="pub:publication/source:source[1]/eterms:publishing-info/eterms:edition/text()"></xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="$start-page-end-page"></xsl:variable>
                                                <xsl:variable name="var">
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="volume-issue">
                            <xsl:variable name="var" select="''"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="pub:publication/source:source[1]/eterms:volume/text()"></xsl:variable>
                                                <xsl:variable name="var">
                                                    <xsl:if test="exists($var) and $var != ''">
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="pub:publication/source:source[1]/eterms:issue/text()"></xsl:variable>
                                                <xsl:variable name="var">
                                                    <xsl:if test="exists($var) and $var != ''">
                                                        <xsl:text>, </xsl:text>
                                                    </xsl:if>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="''"></xsl:with-param>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="source-title">
                            <xsl:variable name="var" select="func:escapeMarkupTags(pub:publication/source:source[1]/dc:title/text())"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:text> - </xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"></xsl:copy-of>
                            </xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="place-publisher">
                            <xsl:variable name="var" select="''"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="pub:publication/eterms:publishing-info/eterms:place/text()"></xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="pub:publication/eterms:publishing-info/dc:publisher/text()"></xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="': '"></xsl:with-param>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="exists($var) and $var != ''">
                                    <xsl:text></xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"></xsl:copy-of>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="editors-base">
                            <xsl:variable name="var" select="''"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="$editorsCount &gt; 0">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="editors-Equal1">
                                                        <xsl:variable name="var" select="''"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:if test="$editorsCount = 1">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <xsl:for-each select="pub:publication/eterms:creator[@role = $l_editor]">
                                                                                <le position-delimiter=", ">
                                                                                    <xsl:choose>
                                                                                        <xsl:when test="person:person/dc:identifier[@xsi:type = 'eterms:CONE'] != ''">
                                                                                            <xsl:text>&lt;a href="</xsl:text>
                                                                                            <xsl:value-of select="concat($instanceUrl, '/cone', person:person/dc:identifier[@xsi:type = 'eterms:CONE'])"></xsl:value-of>
                                                                                            <xsl:text>" target="_blank"&gt;</xsl:text>
                                                                                            <xsl:call-template name="applyDelimiter">
                                                                                                <xsl:with-param name="les">
                                                                                                    <le>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:variable name="var" select="person:person/eterms:family-name/text()"></xsl:variable>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </le>
                                                                                                    <le>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"></xsl:variable>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </le>
                                                                                                </xsl:with-param>
                                                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                            </xsl:call-template>
                                                                                            <xsl:text>&lt;/a&gt;</xsl:text>
                                                                                        </xsl:when>
                                                                                        <xsl:otherwise>
                                                                                            <xsl:call-template name="applyDelimiter">
                                                                                                <xsl:with-param name="les">
                                                                                                    <le>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:variable name="var" select="person:person/eterms:family-name/text()"></xsl:variable>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </le>
                                                                                                    <le>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"></xsl:variable>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </le>
                                                                                                </xsl:with-param>
                                                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                            </xsl:call-template>
                                                                                        </xsl:otherwise>
                                                                                    </xsl:choose>
                                                                                </le>
                                                                            </xsl:for-each>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$editors-Equal1"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="editors-MoreThan1">
                                                        <xsl:variable name="var" select="''"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:if test="$editorsCount &gt; 1">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <xsl:for-each select="pub:publication/eterms:creator[@role = $l_editor]">
                                                                                <le position-delimiter=", ">
                                                                                    <xsl:choose>
                                                                                        <xsl:when test="person:person/dc:identifier[@xsi:type = 'eterms:CONE'] != ''">
                                                                                            <xsl:text>&lt;a href="</xsl:text>
                                                                                            <xsl:value-of select="concat($instanceUrl, '/cone', person:person/dc:identifier[@xsi:type = 'eterms:CONE'])"></xsl:value-of>
                                                                                            <xsl:text>" target="_blank"&gt;</xsl:text>
                                                                                            <xsl:call-template name="applyDelimiter">
                                                                                                <xsl:with-param name="les">
                                                                                                    <le>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:variable name="var" select="person:person/eterms:family-name/text()"></xsl:variable>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </le>
                                                                                                    <le>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"></xsl:variable>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </le>
                                                                                                </xsl:with-param>
                                                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                            </xsl:call-template>
                                                                                            <xsl:text>&lt;/a&gt;</xsl:text>
                                                                                        </xsl:when>
                                                                                        <xsl:otherwise>
                                                                                            <xsl:call-template name="applyDelimiter">
                                                                                                <xsl:with-param name="les">
                                                                                                    <le>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:variable name="var" select="person:person/eterms:family-name/text()"></xsl:variable>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </le>
                                                                                                    <le>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"></xsl:variable>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </le>
                                                                                                </xsl:with-param>
                                                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                            </xsl:call-template>
                                                                                        </xsl:otherwise>
                                                                                    </xsl:choose>
                                                                                </le>
                                                                            </xsl:for-each>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$editors-MoreThan1"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="editors">
                            <xsl:variable name="var" select="''"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="$editorsCount &gt; 0">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$editors-base"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$ed-postfix-i18n"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text> (</xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text>)</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="editors-book">
                            <xsl:variable name="var" select="''"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="$authorsCount &gt; 0 and $editorsCount &gt; 0 and $genre = $l_book">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="''"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:if test="$editorsCount &gt; 0">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="editors-Equal1">
                                                                                    <xsl:variable name="var" select="''"></xsl:variable>
                                                                                    <xsl:variable name="var">
                                                                                        <xsl:if test="$editorsCount = 1">
                                                                                            <xsl:variable name="var">
                                                                                                <xsl:call-template name="applyDelimiter">
                                                                                                    <xsl:with-param name="les">
                                                                                                        <xsl:for-each select="pub:publication/eterms:creator[@role = $l_editor]">
                                                                                                            <le position-delimiter=", ">
                                                                                                                <xsl:choose>
                                                                                                                    <xsl:when test="person:person/dc:identifier[@xsi:type = 'eterms:CONE'] != ''">
                                                                                                                        <xsl:text>&lt;a href="</xsl:text>
                                                                                                                        <xsl:value-of select="concat($instanceUrl, '/cone', person:person/dc:identifier[@xsi:type = 'eterms:CONE'])"></xsl:value-of>
                                                                                                                        <xsl:text>" target="_blank"&gt;</xsl:text>
                                                                                                                        <xsl:call-template name="applyDelimiter">
                                                                                                                            <xsl:with-param name="les">
                                                                                                                                <le>
                                                                                                                                    <xsl:variable name="var">
                                                                                                                                        <xsl:variable name="var" select="person:person/eterms:family-name/text()"></xsl:variable>
                                                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                    </xsl:variable>
                                                                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                </le>
                                                                                                                                <le>
                                                                                                                                    <xsl:variable name="var">
                                                                                                                                        <xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"></xsl:variable>
                                                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                    </xsl:variable>
                                                                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                </le>
                                                                                                                            </xsl:with-param>
                                                                                                                            <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                                                        </xsl:call-template>
                                                                                                                        <xsl:text>&lt;/a&gt;</xsl:text>
                                                                                                                    </xsl:when>
                                                                                                                    <xsl:otherwise>
                                                                                                                        <xsl:call-template name="applyDelimiter">
                                                                                                                            <xsl:with-param name="les">
                                                                                                                                <le>
                                                                                                                                    <xsl:variable name="var">
                                                                                                                                        <xsl:variable name="var" select="person:person/eterms:family-name/text()"></xsl:variable>
                                                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                    </xsl:variable>
                                                                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                </le>
                                                                                                                                <le>
                                                                                                                                    <xsl:variable name="var">
                                                                                                                                        <xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"></xsl:variable>
                                                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                    </xsl:variable>
                                                                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                </le>
                                                                                                                            </xsl:with-param>
                                                                                                                            <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                                                        </xsl:call-template>
                                                                                                                    </xsl:otherwise>
                                                                                                                </xsl:choose>
                                                                                                            </le>
                                                                                                        </xsl:for-each>
                                                                                                    </xsl:with-param>
                                                                                                    <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                                </xsl:call-template>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                        </xsl:if>
                                                                                    </xsl:variable>
                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                </xsl:variable>
                                                                                <xsl:copy-of select="$editors-Equal1"></xsl:copy-of>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="editors-MoreThan1">
                                                                                    <xsl:variable name="var" select="''"></xsl:variable>
                                                                                    <xsl:variable name="var">
                                                                                        <xsl:if test="$editorsCount &gt; 1">
                                                                                            <xsl:variable name="var">
                                                                                                <xsl:call-template name="applyDelimiter">
                                                                                                    <xsl:with-param name="les">
                                                                                                        <xsl:for-each select="pub:publication/eterms:creator[@role = $l_editor]">
                                                                                                            <le position-delimiter=", ">
                                                                                                                <xsl:choose>
                                                                                                                    <xsl:when test="person:person/dc:identifier[@xsi:type = 'eterms:CONE'] != ''">
                                                                                                                        <xsl:text>&lt;a href="</xsl:text>
                                                                                                                        <xsl:value-of select="concat($instanceUrl, '/cone', person:person/dc:identifier[@xsi:type = 'eterms:CONE'])"></xsl:value-of>
                                                                                                                        <xsl:text>" target="_blank"&gt;</xsl:text>
                                                                                                                        <xsl:call-template name="applyDelimiter">
                                                                                                                            <xsl:with-param name="les">
                                                                                                                                <le>
                                                                                                                                    <xsl:variable name="var">
                                                                                                                                        <xsl:variable name="var" select="person:person/eterms:family-name/text()"></xsl:variable>
                                                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                    </xsl:variable>
                                                                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                </le>
                                                                                                                                <le>
                                                                                                                                    <xsl:variable name="var">
                                                                                                                                        <xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"></xsl:variable>
                                                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                    </xsl:variable>
                                                                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                </le>
                                                                                                                            </xsl:with-param>
                                                                                                                            <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                                                        </xsl:call-template>
                                                                                                                        <xsl:text>&lt;/a&gt;</xsl:text>
                                                                                                                    </xsl:when>
                                                                                                                    <xsl:otherwise>
                                                                                                                        <xsl:call-template name="applyDelimiter">
                                                                                                                            <xsl:with-param name="les">
                                                                                                                                <le>
                                                                                                                                    <xsl:variable name="var">
                                                                                                                                        <xsl:variable name="var" select="person:person/eterms:family-name/text()"></xsl:variable>
                                                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                    </xsl:variable>
                                                                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                </le>
                                                                                                                                <le>
                                                                                                                                    <xsl:variable name="var">
                                                                                                                                        <xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"></xsl:variable>
                                                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                    </xsl:variable>
                                                                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                </le>
                                                                                                                            </xsl:with-param>
                                                                                                                            <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                                                        </xsl:call-template>
                                                                                                                    </xsl:otherwise>
                                                                                                                </xsl:choose>
                                                                                                            </le>
                                                                                                        </xsl:for-each>
                                                                                                    </xsl:with-param>
                                                                                                    <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                                </xsl:call-template>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                        </xsl:if>
                                                                                    </xsl:variable>
                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                </xsl:variable>
                                                                                <xsl:copy-of select="$editors-MoreThan1"></xsl:copy-of>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="''"></xsl:with-param>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$ed-postfix-i18n"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:if test="exists($var) and $var != ''">
                                            <xsl:text> (</xsl:text>
                                        </xsl:if>
                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                        <xsl:if test="exists($var) and $var != ''">
                                            <xsl:text>)</xsl:text>
                                        </xsl:if>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="authors-or-editors">
                            <xsl:variable name="var" select="''"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="''"></xsl:variable>
                                                <xsl:variable name="var">
                                                    <xsl:if test="$authorsCount &gt; 0">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="authors-Equal1">
                                                                            <xsl:variable name="var" select="''"></xsl:variable>
                                                                            <xsl:variable name="var">
                                                                                <xsl:if test="$authorsCount = 1">
                                                                                    <xsl:variable name="var">
                                                                                        <xsl:call-template name="applyDelimiter">
                                                                                            <xsl:with-param name="les">
                                                                                                <xsl:for-each select="pub:publication/eterms:creator[@role = $l_author]">
                                                                                                    <le position-delimiter=", ">
                                                                                                        <xsl:choose>
                                                                                                            <xsl:when test="person:person/dc:identifier[@xsi:type = 'eterms:CONE'] != ''">
                                                                                                                <xsl:text>&lt;a href="</xsl:text>
                                                                                                                <xsl:value-of select="concat($instanceUrl, '/cone', person:person/dc:identifier[@xsi:type = 'eterms:CONE'])"></xsl:value-of>
                                                                                                                <xsl:text>" target="_blank"&gt;</xsl:text>
                                                                                                                <xsl:call-template name="applyDelimiter">
                                                                                                                    <xsl:with-param name="les">
                                                                                                                        <le>
                                                                                                                            <xsl:variable name="var">
                                                                                                                                <xsl:variable name="var" select="person:person/eterms:family-name/text()"></xsl:variable>
                                                                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                            </xsl:variable>
                                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                        </le>
                                                                                                                        <le>
                                                                                                                            <xsl:variable name="var">
                                                                                                                                <xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"></xsl:variable>
                                                                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                            </xsl:variable>
                                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                        </le>
                                                                                                                    </xsl:with-param>
                                                                                                                    <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                                                </xsl:call-template>
                                                                                                                <xsl:text>&lt;/a&gt;</xsl:text>
                                                                                                            </xsl:when>
                                                                                                            <xsl:otherwise>
                                                                                                                <xsl:call-template name="applyDelimiter">
                                                                                                                    <xsl:with-param name="les">
                                                                                                                        <le>
                                                                                                                            <xsl:variable name="var">
                                                                                                                                <xsl:variable name="var" select="person:person/eterms:family-name/text()"></xsl:variable>
                                                                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                            </xsl:variable>
                                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                        </le>
                                                                                                                        <le>
                                                                                                                            <xsl:variable name="var">
                                                                                                                                <xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"></xsl:variable>
                                                                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                            </xsl:variable>
                                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                        </le>
                                                                                                                    </xsl:with-param>
                                                                                                                    <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                                                </xsl:call-template>
                                                                                                            </xsl:otherwise>
                                                                                                        </xsl:choose>
                                                                                                    </le>
                                                                                                </xsl:for-each>
                                                                                            </xsl:with-param>
                                                                                            <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                        </xsl:call-template>
                                                                                    </xsl:variable>
                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                </xsl:if>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$authors-Equal1"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="authors-MoreThan1">
                                                                            <xsl:variable name="var" select="''"></xsl:variable>
                                                                            <xsl:variable name="var">
                                                                                <xsl:if test="$authorsCount &gt; 1">
                                                                                    <xsl:variable name="var">
                                                                                        <xsl:call-template name="applyDelimiter">
                                                                                            <xsl:with-param name="les">
                                                                                                <xsl:for-each select="pub:publication/eterms:creator[@role = $l_author]">
                                                                                                    <le position-delimiter=", ">
                                                                                                        <xsl:choose>
                                                                                                            <xsl:when test="person:person/dc:identifier[@xsi:type = 'eterms:CONE'] != ''">
                                                                                                                <xsl:text>&lt;a href="</xsl:text>
                                                                                                                <xsl:value-of select="concat($instanceUrl, '/cone', person:person/dc:identifier[@xsi:type = 'eterms:CONE'])"></xsl:value-of>
                                                                                                                <xsl:text>" target="_blank"&gt;</xsl:text>
                                                                                                                <xsl:call-template name="applyDelimiter">
                                                                                                                    <xsl:with-param name="les">
                                                                                                                        <le>
                                                                                                                            <xsl:variable name="var">
                                                                                                                                <xsl:variable name="var" select="person:person/eterms:family-name/text()"></xsl:variable>
                                                                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                            </xsl:variable>
                                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                        </le>
                                                                                                                        <le>
                                                                                                                            <xsl:variable name="var">
                                                                                                                                <xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"></xsl:variable>
                                                                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                            </xsl:variable>
                                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                        </le>
                                                                                                                    </xsl:with-param>
                                                                                                                    <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                                                </xsl:call-template>
                                                                                                                <xsl:text>&lt;/a&gt;</xsl:text>
                                                                                                            </xsl:when>
                                                                                                            <xsl:otherwise>
                                                                                                                <xsl:call-template name="applyDelimiter">
                                                                                                                    <xsl:with-param name="les">
                                                                                                                        <le>
                                                                                                                            <xsl:variable name="var">
                                                                                                                                <xsl:variable name="var" select="person:person/eterms:family-name/text()"></xsl:variable>
                                                                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                            </xsl:variable>
                                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                        </le>
                                                                                                                        <le>
                                                                                                                            <xsl:variable name="var">
                                                                                                                                <xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"></xsl:variable>
                                                                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                            </xsl:variable>
                                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                        </le>
                                                                                                                    </xsl:with-param>
                                                                                                                    <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                                                </xsl:call-template>
                                                                                                            </xsl:otherwise>
                                                                                                        </xsl:choose>
                                                                                                    </le>
                                                                                                </xsl:for-each>
                                                                                            </xsl:with-param>
                                                                                            <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                        </xsl:call-template>
                                                                                    </xsl:variable>
                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                </xsl:if>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$authors-MoreThan1"></xsl:copy-of>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"></xsl:with-param>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="''"></xsl:variable>
                                                <xsl:variable name="var">
                                                    <xsl:if test="$authorsCount = 0">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="$editors"></xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"></xsl:with-param>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="''"></xsl:with-param>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="source-editors">
                            <xsl:variable name="var" select="''"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:if test="$sourceEditorsCount &gt; 0">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="source-editors-Equal1">
                                                        <xsl:variable name="var" select="''"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:if test="$sourceEditorsCount = 1">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <xsl:for-each select="pub:publication/source:source[1]/eterms:creator[@role = $l_editor]">
                                                                                <le position-delimiter="">
                                                                                    <xsl:choose>
                                                                                        <xsl:when test="person:person/dc:identifier[@xsi:type = 'eterms:CONE'] != ''">
                                                                                            <xsl:text>&lt;a href="</xsl:text>
                                                                                            <xsl:value-of select="concat($instanceUrl, '/cone', person:person/dc:identifier[@xsi:type = 'eterms:CONE'])"></xsl:value-of>
                                                                                            <xsl:text>" target="_blank"&gt;</xsl:text>
                                                                                            <xsl:call-template name="applyDelimiter">
                                                                                                <xsl:with-param name="les">
                                                                                                    <le>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:variable name="var" select="person:person/eterms:family-name/text()"></xsl:variable>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </le>
                                                                                                    <le>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"></xsl:variable>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </le>
                                                                                                </xsl:with-param>
                                                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                            </xsl:call-template>
                                                                                            <xsl:text>&lt;/a&gt;</xsl:text>
                                                                                        </xsl:when>
                                                                                        <xsl:otherwise>
                                                                                            <xsl:call-template name="applyDelimiter">
                                                                                                <xsl:with-param name="les">
                                                                                                    <le>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:variable name="var" select="person:person/eterms:family-name/text()"></xsl:variable>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </le>
                                                                                                    <le>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"></xsl:variable>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </le>
                                                                                                </xsl:with-param>
                                                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                            </xsl:call-template>
                                                                                        </xsl:otherwise>
                                                                                    </xsl:choose>
                                                                                </le>
                                                                            </xsl:for-each>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="''"></xsl:with-param>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$source-editors-Equal1"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="source-editors-MoreThan1">
                                                        <xsl:variable name="var" select="''"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:if test="$sourceEditorsCount &gt; 1">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <xsl:for-each select="pub:publication/source:source[1]/eterms:creator[@role = $l_editor]">
                                                                                <le position-delimiter=", ">
                                                                                    <xsl:choose>
                                                                                        <xsl:when test="person:person/dc:identifier[@xsi:type = 'eterms:CONE'] != ''">
                                                                                            <xsl:text>&lt;a href="</xsl:text>
                                                                                            <xsl:value-of select="concat($instanceUrl, '/cone', person:person/dc:identifier[@xsi:type = 'eterms:CONE'])"></xsl:value-of>
                                                                                            <xsl:text>" target="_blank"&gt;</xsl:text>
                                                                                            <xsl:call-template name="applyDelimiter">
                                                                                                <xsl:with-param name="les">
                                                                                                    <le>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:variable name="var" select="person:person/eterms:family-name/text()"></xsl:variable>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </le>
                                                                                                    <le>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"></xsl:variable>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </le>
                                                                                                </xsl:with-param>
                                                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                            </xsl:call-template>
                                                                                            <xsl:text>&lt;/a&gt;</xsl:text>
                                                                                        </xsl:when>
                                                                                        <xsl:otherwise>
                                                                                            <xsl:call-template name="applyDelimiter">
                                                                                                <xsl:with-param name="les">
                                                                                                    <le>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:variable name="var" select="person:person/eterms:family-name/text()"></xsl:variable>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </le>
                                                                                                    <le>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"></xsl:variable>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </le>
                                                                                                </xsl:with-param>
                                                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                            </xsl:call-template>
                                                                                        </xsl:otherwise>
                                                                                    </xsl:choose>
                                                                                </le>
                                                                            </xsl:for-each>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$source-editors-MoreThan1"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$source-ed-postfix-i18n"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text> (</xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text>)</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="authors-or-editors-and-year-and-title-with-dot">
                            <xsl:variable name="var" select="''"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="$authors-or-editors"></xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="$year-authors-or-editors-are-presented"></xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="$title-with-dot"></xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="$year-authors-or-editors-are-not-presented"></xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"></xsl:with-param>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:variable name="authors-or-editors-and-year-and-title-with-dot-italic">
                            <xsl:variable name="var" select="''"></xsl:variable>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="$authors-or-editors"></xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="$year-authors-or-editors-are-presented"></xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="$title-with-dot-italic"></xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                        <le>
                                            <xsl:variable name="var">
                                                <xsl:variable name="var" select="$year-authors-or-editors-are-not-presented"></xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"></xsl:with-param>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:copy-of select="$var"></xsl:copy-of>
                        </xsl:variable>
                        <xsl:choose>
                            <xsl:when test="not($hasPublication)">
                                <xsl:variable name="isNotPublication">
                                    <xsl:variable name="var" select="''"></xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$objid"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:if test="exists($var) and $var != ''">
                                            <xsl:text>## Citaion style layout is not defined for metadata record: </xsl:text>
                                        </xsl:if>
                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:variable>
                                <xsl:copy-of select="$isNotPublication"></xsl:copy-of>
                            </xsl:when>
                            <xsl:when test="$notPublishedRule">
                                <xsl:variable name="submitted-or-in-preparation">
                                    <xsl:variable name="var" select="''"></xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$authors-or-editors-and-year-and-title-with-dot"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:variable>
                                <xsl:copy-of select="$submitted-or-in-preparation"></xsl:copy-of>
                            </xsl:when>
                            <xsl:when test="$genre = $l_patent">
                                <xsl:variable name="patent">
                                    <xsl:call-template name="applyDelimiter">
                                        <xsl:with-param name="les">
                                            <le>
                                                <xsl:variable name="var">
                                                    <xsl:variable name="var" select="$authors-or-editors"></xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </le>
                                            <le>
                                                <xsl:variable name="var">
                                                    <xsl:variable name="var" select="$year-authors-or-editors-are-presented"></xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                            </le>
                                            <le>
                                                <xsl:call-template name="applyDelimiter">
                                                    <xsl:with-param name="les">
                                                        <le>
                                                            <xsl:value-of select="func:escapeMarkupTags(pub:publication/dc:title/text())"></xsl:value-of>
                                                        </le>
                                                        <le>
                                                            <xsl:variable name="var">
                                                                <xsl:value-of select="$sourceSeriesEnumeration"></xsl:value-of>
                                                            </xsl:variable>
                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                        </le>
                                                        <le>
                                                            <xsl:value-of select="$place-publisher"></xsl:value-of>
                                                        </le>
                                                        <le>
                                                            <xsl:value-of select="$pageCount"></xsl:value-of>
                                                        </le>
                                                    </xsl:with-param>
                                                    <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                </xsl:call-template>
                                            </le>
                                        </xsl:with-param>
                                        <xsl:with-param name="delimiter" select="' '"></xsl:with-param>
                                    </xsl:call-template>
                                    <xsl:text>.</xsl:text>
                                </xsl:variable>
                                <xsl:copy-of select="$patent"></xsl:copy-of>
                                <xsl:if test="pub:publication/dc:identifier[@xsi:type = 'eterms:PATENT_NR']">
                                    <xsl:text>&lt;br/&gt;Patentnr: </xsl:text>
                                    <xsl:value-of select="pub:publication/dc:identifier[@xsi:type = 'eterms:PATENT_NR']"></xsl:value-of>
                                </xsl:if>
                                <xsl:if test="$doi">
                                    <xsl:value-of select="$doiLink"></xsl:value-of>
                                </xsl:if>
                            </xsl:when>
                            <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;$genre = ( $l_article, $l_paper, $l_case-note, $l_book-review, $l_case-study, $l_editorial, $l_newspaper-article)">
                                <xsl:variable name="journal-article-etc">
                                    <xsl:variable name="var" select="''"></xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$authors-or-editors-and-year-and-title-with-dot"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="volume-issue-start-page-end-page">
                                                        <xsl:variable name="var" select="''"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="$source-title"></xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="$volume-issue"></xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="$start-page-end-page"></xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$volume-issue-start-page-end-page"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:variable>
                                <xsl:copy-of select="$journal-article-etc"></xsl:copy-of>
                                <xsl:if test="$doi">
                                    <xsl:value-of select="$doiLink"></xsl:value-of>
                                </xsl:if>
                            </xsl:when>
                            <xsl:when test="$genre = ($l_book, $l_proceedings, $l_monograph, $l_commentary, $l_collected-edition, $l_handbook, $l_festschrift)">
                                <xsl:variable name="book-etc">
                                    <xsl:variable name="var" select="''"></xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$authors-or-editors"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$year-authors-or-editors-are-presented"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="''"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="func:escapeMarkupTags(pub:publication/dc:title/text())"></xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:value-of select="$sourceSeriesEnumeration"></xsl:value-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:value-of select="$eventData"></xsl:value-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:value-of select="$publisherData"></xsl:value-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:if test="$genre = $l_proceedings">
                                                                            <xsl:value-of select="$pageCount"></xsl:value-of>
                                                                        </xsl:if>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="''"></xsl:variable>
                                                                            <xsl:variable name="var">
                                                                                <xsl:if test="$genre = $l_book">
                                                                                    <xsl:variable name="var">
                                                                                        <xsl:call-template name="applyDelimiter">
                                                                                            <xsl:with-param name="les">
                                                                                                <le>
                                                                                                    <xsl:variable name="var">
                                                                                                        <xsl:variable name="var" select="func:escapeMarkupTags(pub:publication/dcterms:alternative[1]/text())"></xsl:variable>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:if test="exists($var) and $var != ''">
                                                                                                                <xsl:text>: </xsl:text>
                                                                                                            </xsl:if>
                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:if test="exists($var) and $var != ''">
                                                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                            </xsl:if>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </xsl:variable>
                                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                </le>
                                                                                                <le>
                                                                                                    <xsl:variable name="var">
                                                                                                        <xsl:variable name="var" select="''"></xsl:variable>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:call-template name="applyDelimiter">
                                                                                                                <xsl:with-param name="les">
                                                                                                                    <le>
                                                                                                                        <xsl:variable name="var">
                                                                                                                            <xsl:variable name="var" select="$editors-book"></xsl:variable>
                                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                        </xsl:variable>
                                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                    </le>
                                                                                                                    <le>
                                                                                                                        <xsl:variable name="var">
                                                                                                                            <xsl:variable name="var" select="pub:publication/eterms:publishing-info/eterms:edition/text()"></xsl:variable>
                                                                                                                            <xsl:variable name="var">
                                                                                                                                <xsl:if test="exists($var) and $var != ''">
                                                                                                                                    <xsl:text>(</xsl:text>
                                                                                                                                </xsl:if>
                                                                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                                <xsl:if test="exists($var) and $var != ''">
                                                                                                                                    <xsl:text>)</xsl:text>
                                                                                                                                </xsl:if>
                                                                                                                            </xsl:variable>
                                                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                        </xsl:variable>
                                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                                    </le>
                                                                                                                </xsl:with-param>
                                                                                                                <xsl:with-param name="delimiter" select="'. '"></xsl:with-param>
                                                                                                            </xsl:call-template>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                    </xsl:variable>
                                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                                </le>
                                                                                                <le>
                                                                                                    <xsl:value-of select="$pageCount"></xsl:value-of>
                                                                                                </le>
                                                                                            </xsl:with-param>
                                                                                            <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                                                        </xsl:call-template>
                                                                                    </xsl:variable>
                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                </xsl:if>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"></xsl:with-param>
                                        </xsl:call-template>
                                        <xsl:text>.</xsl:text>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:variable>
                                <xsl:copy-of select="$book-etc"></xsl:copy-of>
                                <xsl:if test="$doi">
                                    <xsl:value-of select="$doiLink"></xsl:value-of>
                                </xsl:if>
                            </xsl:when>
                            <xsl:when test="$genre = $l_thesis">
                                <xsl:variable name="thesis">
                                    <xsl:variable name="var" select="''"></xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$authors-or-editors"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$year"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="title-degree-place-publisher">
                                                        <xsl:variable name="var" select="''"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="func:escapeMarkupTags(pub:publication/dc:title/text())"></xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="$degree"></xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:for-each select="pub:publication/source:source[@type != 'http://purl.org/escidoc/metadata/ves/publication-types/series']">
                                                                                <xsl:variable name="var" select="func:escapeMarkupTags(dc:title/text())"></xsl:variable>
                                                                                <xsl:variable name="var">
                                                                                    <xsl:if test="exists($var) and $var != ''">
                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                        <xsl:if test="position() &lt; last()">
                                                                                            <xsl:text>, </xsl:text>
                                                                                        </xsl:if>
                                                                                    </xsl:if>
                                                                                </xsl:variable>
                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                            </xsl:for-each>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:value-of select="$sourceSeriesEnumeration"></xsl:value-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:value-of select="$publisherData"></xsl:value-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:value-of select="$pageCount"></xsl:value-of>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$title-degree-place-publisher"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:variable>
                                <xsl:copy-of select="$thesis"></xsl:copy-of>
                                <xsl:text>.</xsl:text>
                                <xsl:choose>
                                    <xsl:when test="$doi != ''">
                                        <xsl:value-of select="$doiLink"></xsl:value-of>
                                    </xsl:when>
                                    <xsl:when test="$urn">
                                        <xsl:value-of select="$urn"></xsl:value-of>
                                    </xsl:when>
                                </xsl:choose>
                            </xsl:when>
                            <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;$genre = ($l_book-item, $l_contr-to-collect-ed, $l_contr-to-handbook, $l_contr-to-encyclopedia, $l_contr-to-festschrift, $l_contr-to-commentary )&#xA;&#x9;&#x9;&#x9;&#x9;">
                                <xsl:variable name="book-item-etc">
                                    <xsl:variable name="var" select="''"></xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$authors-or-editors-and-year-and-title-with-dot"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                        <xsl:copy-of select="' - '"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="''"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="$source-editors"></xsl:variable>
                                                                            <xsl:variable name="var">
                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:for-each select="pub:publication/source:source[@type != 'http://purl.org/escidoc/metadata/ves/publication-types/series']">
                                                                                <xsl:variable name="var" select="func:escapeMarkupTags(dc:title/text())"></xsl:variable>
                                                                                <xsl:variable name="var">
                                                                                    <xsl:if test="exists($var) and $var != ''">
                                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                        <xsl:if test="position() &lt; last()">
                                                                                            <xsl:text>, </xsl:text>
                                                                                        </xsl:if>
                                                                                    </xsl:if>
                                                                                </xsl:variable>
                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                            </xsl:for-each>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:value-of select="$sourceSeriesEnumeration"></xsl:value-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="pub:publication/event:event/dc:title"></xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="source-place-publisher">
                                                                            <xsl:variable name="var" select="''"></xsl:variable>
                                                                            <xsl:variable name="var">
                                                                                <xsl:call-template name="applyDelimiter">
                                                                                    <xsl:with-param name="les">
                                                                                        <le>
                                                                                            <xsl:variable name="var">
                                                                                                <xsl:variable name="var" select="pub:publication/source:source[1]/eterms:publishing-info/eterms:place/text()"></xsl:variable>
                                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                        </le>
                                                                                        <le>
                                                                                            <xsl:variable name="var">
                                                                                                <xsl:variable name="var" select="pub:publication/source:source[1]/eterms:publishing-info/dc:publisher/text()"></xsl:variable>
                                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                        </le>
                                                                                    </xsl:with-param>
                                                                                    <xsl:with-param name="delimiter" select="'&#160;:&#160;'"></xsl:with-param>
                                                                                </xsl:call-template>
                                                                            </xsl:variable>
                                                                            <xsl:variable name="var">
                                                                                <xsl:if test="exists($var) and $var != ''">
                                                                                    <xsl:text></xsl:text>
                                                                                </xsl:if>
                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$source-place-publisher"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="$source-edition-start-page-end-page"></xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text> In: </xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:variable>
                                <xsl:copy-of select="$book-item-etc"></xsl:copy-of>
                                <xsl:if test="$doi">
                                    <xsl:value-of select="$doiLink"></xsl:value-of>
                                </xsl:if>
                            </xsl:when>
                            <xsl:when test="$genre = $l_issue">
                                <xsl:variable name="issue">
                                    <xsl:variable name="var" select="''"></xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$authors-or-editors"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$year-authors-or-editors-are-presented"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="func:escapeMarkupTags(pub:publication/dc:title/text())"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text></xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text> [Special Issue]. </xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="''"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="$source-title"></xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="$volume-issue"></xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:value-of select="$pageCount"></xsl:value-of>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:variable>
                                <xsl:copy-of select="$issue"></xsl:copy-of>
                            </xsl:when>
                            <xsl:when test="$genre = $l_journal">
                                <xsl:variable name="journal">
                                    <xsl:variable name="var" select="''"></xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$editors"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$year-editors-are-presented"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:call-template name="applyDelimiter">
                                                        <xsl:with-param name="les">
                                                            <le>
                                                                <xsl:variable name="var">
                                                                    <xsl:variable name="var" select="$title-italic"></xsl:variable>
                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                </xsl:variable>
                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                            </le>
                                                            <le>
                                                                <xsl:value-of select="$publisherData"></xsl:value-of>
                                                            </le>
                                                            <le>
                                                                <xsl:value-of select="$pageCount"></xsl:value-of>
                                                            </le>
                                                        </xsl:with-param>
                                                        <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                    </xsl:call-template>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:variable>
                                <xsl:copy-of select="$journal"></xsl:copy-of>
                            </xsl:when>
                            <xsl:when test="$genre = ($l_manuscript, $l_opinion)">
                                <xsl:variable name="manuscript">
                                    <xsl:variable name="var" select="''"></xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$authors-or-editors-and-year-and-title-with-dot-italic"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                        <xsl:if test="exists($var) and $var != ''">
                                            <xsl:text> Unpublished Manuscript.</xsl:text>
                                        </xsl:if>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:variable>
                                <xsl:copy-of select="$manuscript"></xsl:copy-of>
                            </xsl:when>
                            <xsl:when test="$genre = $l_series">
                                <xsl:variable name="series">
                                    <xsl:variable name="var" select="''"></xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$editors"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$title-with-dot-italic"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$place-publisher"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:variable>
                                <xsl:copy-of select="$series"></xsl:copy-of>
                            </xsl:when>
                            <xsl:when test="$genre = $l_talk-at-event">
                                <xsl:variable name="talk-at-event">
                                    <xsl:variable name="var" select="''"></xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$authors-or-editors-and-year-and-title-with-dot-italic"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$eventData"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text> - Talk presented at the </xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:variable>
                                <xsl:copy-of select="$talk-at-event"></xsl:copy-of>
                            </xsl:when>
                            <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;$genre = $l_courseware-lecture&#xA;&#x9;&#x9;&#x9;&#x9;">
                                <xsl:variable name="courseware-lecture">
                                    <xsl:variable name="var" select="''"></xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$authors-or-editors-and-year-and-title-with-dot"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="func:escapeMarkupTags(pub:publication/event:event/dc:title/text())"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text></xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="''"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="pub:publication/event:event/eterms:place/text()"></xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="''"></xsl:variable>
                                                                            <xsl:variable name="var">
                                                                                <xsl:call-template name="applyDelimiter">
                                                                                    <xsl:with-param name="les">
                                                                                        <le>
                                                                                            <xsl:variable name="var">
                                                                                                <xsl:variable name="var" select="pub:publication/event:event/eterms:start-date/text()"></xsl:variable>
                                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                        </le>
                                                                                        <le>
                                                                                            <xsl:variable name="var">
                                                                                                <xsl:variable name="var" select="pub:publication/event:event/eterms:end-date/text()"></xsl:variable>
                                                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                        </le>
                                                                                    </xsl:with-param>
                                                                                    <xsl:with-param name="delimiter" select="' - '"></xsl:with-param>
                                                                                </xsl:call-template>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text></xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:variable>
                                <xsl:copy-of select="$courseware-lecture"></xsl:copy-of>
                            </xsl:when>
                            <xsl:when test="$genre = $l_poster">
                                <xsl:variable name="poster">
                                    <xsl:variable name="var" select="''"></xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$authors-or-editors-and-year-and-title-with-dot-italic"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$eventData"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text> - Poster presented at the </xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:variable>
                                <xsl:copy-of select="$poster"></xsl:copy-of>
                            </xsl:when>
                            <xsl:when test="$genre = $l_report">
                                <xsl:variable name="report">
                                    <xsl:variable name="var" select="''"></xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$authors-or-editors"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$year-authors-or-editors-are-presented"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="title-report-identifier">
                                                        <xsl:variable name="var" select="''"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:value-of select="func:escapeMarkupTags(pub:publication/dc:title/text())"></xsl:value-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:value-of select="$sourceSeriesEnumeration"></xsl:value-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:if test="count(pub:publication/source:source[@type != 'http://purl.org/escidoc/metadata/ves/publication-types/series']) > 0">
                                                                            <xsl:variable name="var">
                                                                                <xsl:for-each select="pub:publication/source:source[@type != 'http://purl.org/escidoc/metadata/ves/publication-types/series']">
                                                                                    <xsl:variable name="var" select="func:escapeMarkupTags(dc:title)"></xsl:variable>
                                                                                    <xsl:variable name="var">
                                                                                        <xsl:if test="exists($var) and $var != ''">
                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                        </xsl:if>
                                                                                    </xsl:variable>
                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                    <xsl:if test="position() &lt; last()">
                                                                                        <xsl:text>, </xsl:text>
                                                                                    </xsl:if>
                                                                                </xsl:for-each>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:if>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:value-of select="$eventData"></xsl:value-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:value-of select="$year-authors-or-editors-are-not-presented"></xsl:value-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:value-of select="$publisherData"></xsl:value-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:value-of select="$pageCount"></xsl:value-of>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$title-report-identifier"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:variable>
                                <xsl:copy-of select="$report"></xsl:copy-of>
                                <xsl:text>.</xsl:text>
                                <xsl:if test="$doi">
                                    <xsl:value-of select="$doiLink"></xsl:value-of>
                                </xsl:if>
                            </xsl:when>
                            <xsl:when test="$genre = ($l_conference-report, $l_conference-paper, $l_other)">
                                <xsl:variable name="confpaper">
                                    <xsl:variable name="var" select="''"></xsl:variable>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="fn:replace($authors-or-editors-and-year-and-title-with-dot, '.$', '')"></xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="$source-editors"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var != ''">
                                                                <xsl:text>. - In: </xsl:text>
                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                <xsl:text>, </xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                                <le>
                                                    <xsl:if test="count(pub:publication/source:source[@type = 'http://purl.org/escidoc/metadata/ves/publication-types/proceedings' and (not(../event:event/dc:title) or dc:title/text() != ../event:event/dc:title/text())]) > 0">
                                                        <xsl:variable name="var">
                                                            <xsl:for-each select="pub:publication/source:source[@type = 'http://purl.org/escidoc/metadata/ves/publication-types/proceedings' and (not(../event:event/dc:title) or dc:title/text() != ../event:event/dc:title/text())]">
                                                                <xsl:variable name="var" select="func:escapeMarkupTags(dc:title/text())"></xsl:variable>
                                                                <xsl:if test="position() = 1">
                                                                    <xsl:text> - </xsl:text>
                                                                </xsl:if>
                                                                <xsl:variable name="var">
                                                                    <xsl:if test="exists($var) and $var != ''">
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </xsl:if>
                                                                </xsl:variable>
                                                                <xsl:copy-of select="$var"></xsl:copy-of>
                                                                <xsl:if test="position() &lt; last()">
                                                                    <xsl:text>, </xsl:text>
                                                                </xsl:if>
                                                            </xsl:for-each>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:if>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var">
                                                        <xsl:variable name="var" select="''"></xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:value-of select="$sourceSeriesEnumeration"></xsl:value-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:if test="count(pub:publication/source:source[@type != 'http://purl.org/escidoc/metadata/ves/publication-types/proceedings' and @type != 'http://purl.org/escidoc/metadata/ves/publication-types/series']) > 0">
                                                                            <xsl:variable name="var">
                                                                                <xsl:for-each select="pub:publication/source:source[@type != 'http://purl.org/escidoc/metadata/ves/publication-types/proceedings' and @type != 'http://purl.org/escidoc/metadata/ves/publication-types/series']">
                                                                                    <xsl:variable name="var" select="func:escapeMarkupTags(dc:title)"></xsl:variable>
                                                                                    <xsl:variable name="var">
                                                                                        <xsl:if test="exists($var) and $var != ''">
                                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                        </xsl:if>
                                                                                    </xsl:variable>
                                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                                    <xsl:if test="position() &lt; last()">
                                                                                        <xsl:text>, </xsl:text>
                                                                                    </xsl:if>
                                                                                </xsl:for-each>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:if>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:value-of select="$eventData"></xsl:value-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:value-of select="$publisherData"></xsl:value-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var">
                                                                            <xsl:variable name="var" select="$source-edition-start-page-end-page"></xsl:variable>
                                                                            <xsl:copy-of select="$var"></xsl:copy-of>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:value-of select="$pageCount"></xsl:value-of>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"></xsl:with-param>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:variable name="var">
                                                            <xsl:choose>
                                                                <xsl:when test="exists($var) and $var != ''">
                                                                    <xsl:text>, </xsl:text>
                                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                                    <xsl:text>.</xsl:text>
                                                                </xsl:when>
                                                                <xsl:otherwise>
                                                                    <xsl:text>.</xsl:text>
                                                                </xsl:otherwise>
                                                            </xsl:choose>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"></xsl:copy-of>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"></xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"></xsl:copy-of>
                                </xsl:variable>
                                <xsl:copy-of select="$confpaper"></xsl:copy-of>
                                <xsl:if test="$doi">
                                    <xsl:value-of select="$doiLink"></xsl:value-of>
                                </xsl:if>
                            </xsl:when>
                        </xsl:choose>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:value-of select="func:cleanCitation($citation)"></xsl:value-of>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="escidocComponents:content[@storage = 'internal-managed']">
        <xsl:element name="{name(.)}">
            <xsl:copy-of select="@*[name(.) != 'xlink:href']"></xsl:copy-of>
            <xsl:attribute name="xlink:href" select="concat($pubmanUrl, '/item/', ../../../ei:properties/prop:version/@objid, '/component/', ../@objid, '/', ../escidocComponents:properties/prop:file-name)"></xsl:attribute>
        </xsl:element>
    </xsl:template>
    <xsl:template name="applyDelimiter">
        <xsl:param name="les"></xsl:param>
        <xsl:param name="delimiter"></xsl:param>
        <xsl:variable name="les_filled" select="$les/le[exists(text()) and text() != '']"></xsl:variable>
        <xsl:for-each select="$les_filled">
            <xsl:value-of select="
                    if (position() &gt; 1) then
                        @position-delimiter
                    else
                        ''"></xsl:value-of>
            <xsl:copy-of select="child::node()" copy-namespaces="no"></xsl:copy-of>
            <xsl:if test="position() != last() and not(@position-delimiter)">
                <xsl:value-of select="$delimiter"></xsl:value-of>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="func:get_year">
        <xsl:param name="date"></xsl:param>
        <xsl:value-of select="substring($date, 1, 4)"></xsl:value-of>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="func:get_month">
        <xsl:param name="date"></xsl:param>
        <xsl:value-of select="substring($date, 6, 2)"></xsl:value-of>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="func:get_month_name">
        <xsl:param name="date"></xsl:param>
        <xsl:variable name="months">
            <m n="0?1">January</m>
            <m n="0?2">February</m>
            <m n="0?3">March</m>
            <m n="0?4">April</m>
            <m n="0?5">May</m>
            <m n="0?6">June</m>
            <m n="0?7">July</m>
            <m n="0?8">August</m>
            <m n="0?9">September</m>
            <m n="10">October</m>
            <m n="11">November</m>
            <m n="12">December</m>
        </xsl:variable>
        <xsl:value-of select="$months/m[matches(tokenize($date, '-')[2], @n)]"></xsl:value-of>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="func:get_initials">
        <xsl:param name="str"></xsl:param>
        <xsl:variable name="delim" select="
                if (contains($str, '-')) then
                    '-'
                else
                    ' '"></xsl:variable>
        <xsl:for-each select="tokenize(normalize-space($str), '\s+|\.\s+|\-\s*')">
            <xsl:value-of select="
                    concat(substring(., 1, 1), if (position() != last()) then
                        concat('.', $delim)
                    else
                        '.')"></xsl:value-of>
        </xsl:for-each>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="func:fname_initials">
        <xsl:param name="fname"></xsl:param>
        <xsl:param name="gname"></xsl:param>
        <xsl:param name="delim"></xsl:param>
        <xsl:value-of select="
                if (jfunc:isCJK(concat($fname, $gname))) then
                    string-join(($fname, $gname), $delim)
                else
                    string-join(($fname, func:get_initials($gname)), $delim)"></xsl:value-of>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="func:initials_fname">
        <xsl:param name="gname"></xsl:param>
        <xsl:param name="fname"></xsl:param>
        <xsl:param name="delim"></xsl:param>
        <xsl:value-of select="
                if (jfunc:isCJK(concat($fname, $gname))) then
                    string-join(($fname, $gname), $delim)
                else
                    string-join((func:get_initials($gname), $fname), $delim)"></xsl:value-of>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="func:escapeMarkupTags">
        <xsl:param name="str"></xsl:param>
        <xsl:value-of select="jfunc:escapeMarkupTags($str)"></xsl:value-of>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="func:cleanCitation">
        <xsl:param name="str"></xsl:param>
        <xsl:value-of select="normalize-space(functx:replace-multi($str, ('([.,?!:;])\s*(&lt;[/]span&gt;)\s*\1', '([.,?!:;])\s*\1', '\.&#34;\.', '\s+([.,?!:;])', '\s*(&lt;[/]?span&gt;)\s*([.,?!:;])', '([?!])+\.'), ('$1$2', '$1', '.&#34;', '$1', '$1$2', '$1')))"></xsl:value-of>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="functx:replace-multi" as="xs:string?">
        <xsl:param name="arg" as="xs:string?"></xsl:param>
        <xsl:param name="changeFrom" as="xs:string*"></xsl:param>
        <xsl:param name="changeTo" as="xs:string*"></xsl:param>
        <xsl:sequence select="
                if (count($changeFrom) &gt; 0) then
                    functx:replace-multi(replace($arg, $changeFrom[1], functx:if-absent($changeTo[1], '')), $changeFrom[position() &gt; 1], $changeTo[position() &gt; 1])
                else
                    $arg"></xsl:sequence>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="functx:if-absent" as="item()*">
        <xsl:param name="arg" as="item()*"></xsl:param>
        <xsl:param name="value" as="item()*"></xsl:param>
        <xsl:sequence select="
                if (exists($arg)) then
                    $arg
                else
                    $value"></xsl:sequence>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="func:get_reverse_date">
        <xsl:param name="input_date"></xsl:param>
        <xsl:if test="$input_date[. != '']">
            <xsl:value-of select="concat(substring($input_date, 9, 2), '.', substring($input_date, 6, 2), '.', substring($input_date, 1, 4))"></xsl:value-of>
        </xsl:if>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="func:getCitationStyleForJournal">
        <xsl:param name="idType"></xsl:param>
        <xsl:param name="idValue"></xsl:param>
        <xsl:value-of select="jfunc:getCitationStyleForJournal($idType, $idValue)"></xsl:value-of>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="func:substringAfterEdition">
        <xsl:param name="inputWithSpaceComma"></xsl:param>
        <xsl:value-of select="substring-before(substring-after($inputWithSpaceComma, ', '), ' ')"></xsl:value-of>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="func:substringBeforeEdition">
        <xsl:param name="inputWithSpaceComma"></xsl:param>
        <xsl:value-of select="substring-before(substring-before($inputWithSpaceComma, ', '), ' ')"></xsl:value-of>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="func:substringBeforeInstalment">
        <xsl:param name="inputWithInstalment"></xsl:param>
        <xsl:value-of select="
                if (contains($inputWithInstalment, 'instl')) then
                    substring-before($inputWithInstalment, 'instl')
                else
                    if (contains($inputWithInstalment, 'Lf')) then
                        substring-before($inputWithInstalment, 'Lf')
                    else
                        ''"></xsl:value-of>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="func:substringBeforeSince">
        <xsl:param name="inputWithSince"></xsl:param>
        <xsl:value-of select="
                if (contains($inputWithSince, 'since')) then
                    substring-before($inputWithSince, 'since')
                else
                    if (contains($inputWithSince, 'seit')) then
                        substring-before($inputWithSince, 'seit')
                    else
                        ''"></xsl:value-of>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="func:substringAfterSince">
        <xsl:param name="inputWithSince"></xsl:param>
        <xsl:value-of select="
                if (contains($inputWithSince, 'since')) then
                    substring-after($inputWithSince, 'since')
                else
                    if (contains($inputWithSince, 'seit')) then
                        substring-after($inputWithSince, 'seit')
                    else
                        ''"></xsl:value-of>
    </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common" name="func:substringAfterReviewOf">
        <xsl:param name="inputReviewTitle"></xsl:param>
        <xsl:value-of select="substring-after($inputReviewTitle, 'Review of:')"></xsl:value-of>
    </xsl:function>
</xsl:stylesheet>
