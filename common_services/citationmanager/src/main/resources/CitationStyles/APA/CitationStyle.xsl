<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:cit="http://www.escidoc.de/citationstyle"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:jfunc="java:de.mpg.escidoc.services.citationmanager.utils.XsltHelper"
                xmlns:func="http://www.escidoc.de/citationstyle/functions"
                xmlns:functx="http://www.functx.com"
                xmlns:ei="http://www.escidoc.de/schemas/item/0.10"
                xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.5"
                xmlns:mdp="http://escidoc.mpg.de/metadataprofile/schema/0.1/"
                xmlns:pub="http://purl.org/escidoc/metadata/profiles/0.1/publication"
                xmlns:e="http://purl.org/escidoc/metadata/terms/0.1/"
                xmlns:prop="http://escidoc.de/core/01/properties/"
                xmlns:escidocComponents="http://www.escidoc.de/schemas/components/0.9"
                xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source"
                xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
                xmlns:event="http://purl.org/escidoc/metadata/profiles/0.1/event"
                xmlns:organization="http://purl.org/escidoc/metadata/profiles/0.1/organization"
                xmlns:person="http://purl.org/escidoc/metadata/profiles/0.1/person"
                xmlns:legalCase="http://purl.org/escidoc/metadata/profiles/0.1/legal-case"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                version="2.0">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"
                cdata-section-elements="dcterms:bibliographicCitation dcterms:abstract"/>
    <xsl:param name="pubman_instance"/>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="@* | node ()"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="prop:content-model-specific">
        <xsl:element name="{name(.)}">
            <xsl:copy-of select="child::node()"/>
            <xsl:element name="dcterms:bibliographicCitation">
                <xsl:variable name="citation">
                    <xsl:for-each select="../../mdr:md-records/mdr:md-record"><!--### Global Default Variables ###-->
	<xsl:variable name="l_journal">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/journal'"/>
                        </xsl:variable>
                        <xsl:variable name="l_article">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/article'"/>
                        </xsl:variable>
                        <xsl:variable name="l_issue">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/issue'"/>
                        </xsl:variable>
                        <xsl:variable name="l_book">
                            <xsl:value-of select="'http://purl.org/eprint/type/Book'"/>
                        </xsl:variable>
                        <xsl:variable name="l_book-item">
                            <xsl:value-of select="'http://purl.org/eprint/type/BookItem'"/>
                        </xsl:variable>
                        <xsl:variable name="l_proceedings">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/proceedings'"/>
                        </xsl:variable>
                        <xsl:variable name="l_conference-paper">
                            <xsl:value-of select="'http://purl.org/eprint/type/ConferencePaper'"/>
                        </xsl:variable>
                        <xsl:variable name="l_meeting-abstract">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/meeting-abstract'"/>
                        </xsl:variable>
                        <xsl:variable name="l_conference-report">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/conference-report'"/>
                        </xsl:variable>
                        <xsl:variable name="l_poster">
                            <xsl:value-of select="'http://purl.org/eprint/type/ConferencePoster'"/>
                        </xsl:variable>
                        <xsl:variable name="l_report">
                            <xsl:value-of select="'http://purl.org/eprint/type/Report'"/>
                        </xsl:variable>
                        <xsl:variable name="l_paper">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/paper'"/>
                        </xsl:variable>
                        <xsl:variable name="l_talk-at-event">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event'"/>
                        </xsl:variable>
                        <xsl:variable name="l_courseware-lecture">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/courseware-lecture'"/>
                        </xsl:variable>
                        <xsl:variable name="l_thesis">
                            <xsl:value-of select="'http://purl.org/eprint/type/Thesis'"/>
                        </xsl:variable>
                        <xsl:variable name="l_series">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/series'"/>
                        </xsl:variable>
                        <xsl:variable name="l_manuscript">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/manuscript'"/>
                        </xsl:variable>
                        <xsl:variable name="l_other">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/other'"/>
                        </xsl:variable>
                        <xsl:variable name="l_monograph">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/monograph'"/>
                        </xsl:variable>
                        <xsl:variable name="l_contr-to-collect-ed">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-collected-edition'"/>
                        </xsl:variable>
                        <xsl:variable name="l_case-note">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/case-note'"/>
                        </xsl:variable>
                        <xsl:variable name="l_opinion">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/opinion'"/>
                        </xsl:variable>
                        <xsl:variable name="l_case-study">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/case-study'"/>
                        </xsl:variable>
                        <xsl:variable name="l_book-review">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/book-review'"/>
                        </xsl:variable>
                        <xsl:variable name="l_contr-to-commentary">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-commentary'"/>
                        </xsl:variable>
                        <xsl:variable name="l_contr-to-festschrift">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-festschrift'"/>
                        </xsl:variable>
                        <xsl:variable name="l_contr-to-handbook">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-handbook'"/>
                        </xsl:variable>
                        <xsl:variable name="l_contr-to-encyclopedia">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-encyclopedia'"/>
                        </xsl:variable>
                        <xsl:variable name="l_newspaper-article">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/newspaper-article'"/>
                        </xsl:variable>
                        <xsl:variable name="l_collected-edition">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/collected-edition'"/>
                        </xsl:variable>
                        <xsl:variable name="l_commentary">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/commentary'"/>
                        </xsl:variable>
                        <xsl:variable name="l_festschrift">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/festschrift'"/>
                        </xsl:variable>
                        <xsl:variable name="l_handbook">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/handbook'"/>
                        </xsl:variable>
                        <xsl:variable name="l_editorial">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/publication-types/editorial'"/>
                        </xsl:variable>
                        <xsl:variable name="l_author">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/AUT'"/>
                        </xsl:variable>
                        <xsl:variable name="l_artist">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/ART'"/>
                        </xsl:variable>
                        <xsl:variable name="l_editor">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/EDT'"/>
                        </xsl:variable>
                        <xsl:variable name="l_painter">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/creator-roles/painter'"/>
                        </xsl:variable>
                        <xsl:variable name="l_photographer">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/PHT'"/>
                        </xsl:variable>
                        <xsl:variable name="l_illustrator">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/ILL'"/>
                        </xsl:variable>
                        <xsl:variable name="l_commentator">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/CMM'"/>
                        </xsl:variable>
                        <xsl:variable name="l_transcriber">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/TRC'"/>
                        </xsl:variable>
                        <xsl:variable name="l_translator">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/TRL'"/>
                        </xsl:variable>
                        <xsl:variable name="l_advisor">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/SAD'"/>
                        </xsl:variable>
                        <xsl:variable name="l_advisor">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/THS'"/>
                        </xsl:variable>
                        <xsl:variable name="l_contributor">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/CTB'"/>
                        </xsl:variable>
                        <xsl:variable name="l_publisher">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/creator-roles/publisher'"/>
                        </xsl:variable>
                        <xsl:variable name="l_honoree">
                            <xsl:value-of select="'http://www.loc.gov/loc.terms/relators/HNR'"/>
                        </xsl:variable>
                        <xsl:variable name="l_contributor">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/creator-roles/founder'"/>
                        </xsl:variable>
                        <xsl:variable name="l_contributor">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/creator-roles/referee'"/>
                        </xsl:variable>
                        <xsl:variable name="l_master">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/academic-degrees/master'"/>
                        </xsl:variable>
                        <xsl:variable name="l_diploma">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/academic-degrees/diploma'"/>
                        </xsl:variable>
                        <xsl:variable name="l_magister">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/academic-degrees/magister'"/>
                        </xsl:variable>
                        <xsl:variable name="l_staatsexamen">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/academic-degrees/staatsexamen'"/>
                        </xsl:variable>
                        <xsl:variable name="l_phd">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/academic-degrees/phd'"/>
                        </xsl:variable>
                        <xsl:variable name="l_habilitation">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/academic-degrees/habilitation'"/>
                        </xsl:variable>
                        <xsl:variable name="l_bachelor">
                            <xsl:value-of select="'http://purl.org/escidoc/metadata/ves/academic-degrees/bachelor'"/>
                        </xsl:variable>
                        <xsl:variable name="v_degree">
                            <xsl:value-of select="pub:publication/eterms:degree/text()"/>
                        </xsl:variable>
                        <xsl:variable name="l_degree">
                            <xsl:value-of select="&#xA;&#x9;&#x9;if ($v_degree=$l_master) then 'Master' else&#xA;&#x9;&#x9;if ($v_degree=$l_diploma) then 'Diploma' else&#xA;&#x9;&#x9;if ($v_degree=$l_magister) then 'Magister' else&#xA;&#x9;&#x9;if ($v_degree=$l_staatsexamen) then 'Staatsexamen' else&#xA;&#x9;&#x9;if ($v_degree=$l_phd) then 'PhD' else&#xA;&#x9;&#x9;if ($v_degree=$l_habilitation) then 'Habilitation' else&#xA;&#x9;&#x9;if ($v_degree=$l_bachelor) then 'Bachelor' else ''&#xA;&#x9;"/>
                        </xsl:variable>
                        <!--### APA specific Default Variables ###-->
	<xsl:variable name="apa_default_variable">
                            <xsl:value-of select="'APA specific default variable'"/>
                        </xsl:variable>
                        <xsl:variable name="objid">
                            <xsl:value-of select="../../@objid"/>
                        </xsl:variable>
                        <xsl:variable name="genre">
                            <xsl:value-of select="pub:publication/@type"/>
                        </xsl:variable>
                        <xsl:variable name="source-type">
                            <xsl:value-of select="pub:publication/source:source[1]/@type"/>
                        </xsl:variable>
                        <xsl:variable name="hasPublication" as="xs:boolean">
                            <xsl:value-of select="exists(pub:publication)"/>
                        </xsl:variable>
                        <xsl:variable name="authorsCount">
                            <xsl:value-of select="count(pub:publication/eterms:creator[@role=$l_author])"/>
                        </xsl:variable>
                        <xsl:variable name="editorsCount">
                            <xsl:value-of select="count(pub:publication/eterms:creator[@role=$l_editor])"/>
                        </xsl:variable>
                        <xsl:variable name="ed-postfix">
                            <xsl:value-of select="if ($editorsCount=1) then 'Ed.' else 'Eds.'"/>
                        </xsl:variable>
                        <xsl:variable name="sourceEditorsCount">
                            <xsl:value-of select="count(pub:publication/source:source[1]/eterms:creator[@role=$l_editor])"/>
                        </xsl:variable>
                        <xsl:variable name="source-ed-postfix">
                            <xsl:value-of select="if ($sourceEditorsCount=1) then 'Ed.' else 'Eds.'"/>
                        </xsl:variable>
                        <xsl:variable name="genre-exception" as="xs:boolean">
                            <xsl:value-of select="$genre=( $l_poster, $l_talk-at-event, $l_courseware-lecture )"/>
                        </xsl:variable>
                        <xsl:variable name="date">
                            <xsl:value-of select=" &#xA;&#x9;&#x9;&#x9;if ($genre=$l_manuscript and pub:publication/dcterms:created) &#xA;&#x9;&#x9;&#x9;then func:get_year(pub:publication/dcterms:created)  &#xA;&#x9;&#x9;&#x9;else if ($genre-exception and pub:publication/event:event/eterms:start-date)&#xA;&#x9;&#x9;&#x9;then func:get_year(pub:publication/event:event/eterms:start-date) &#xA;&#x9;&#x9;&#x9;else if (pub:publication/dcterms:issued)&#xA;&#x9;&#x9;&#x9;then func:get_year(pub:publication/dcterms:issued) &#xA;&#x9;&#x9;&#x9;else if (pub:publication/eterms:published-online) &#xA;&#x9;&#x9;&#x9;then func:get_year(pub:publication/eterms:published-online) &#xA;&#x9;&#x9;&#x9;else if (( $genre-exception and $genre=$l_manuscript) and pub:publication/dcterms:dateAccepted)&#xA;&#x9;&#x9;&#x9;then func:get_year(pub:publication/dcterms:dateAccepted)&#xA;&#x9;&#x9;&#x9;else if (pub:publication/dcterms:dateAccepted) &#xA;&#x9;&#x9;&#x9;then 'in press' &#xA;&#x9;&#x9;&#x9;else if&#x9;(( $genre-exception or $genre=$l_manuscript) and pub:publication/dcterms:dateSubmitted)&#xA;&#x9;&#x9;&#x9;then func:get_year(pub:publication/dcterms:dateSubmitted)&#xA;&#x9;&#x9;&#x9;else if (pub:publication/dcterms:dateSubmitted) &#xA;&#x9;&#x9;&#x9;then 'submitted' &#xA;&#x9;&#x9;&#x9;else if&#x9;(( $genre-exception or $genre=$l_manuscript) and pub:publication/dcterms:modified)&#xA;&#x9;&#x9;&#x9;then func:get_year(pub:publication/dcterms:modified)&#xA;&#x9;&#x9;&#x9;else if (( $genre-exception or $genre=$l_manuscript) and pub:publication/dcterms:created)&#xA;&#x9;&#x9;&#x9;then func:get_year(pub:publication/dcterms:created)&#xA;&#x9;&#x9;&#x9;else if (pub:publication/dcterms:modified or pub:publication/dcterms:created) &#xA;&#x9;&#x9;&#x9;then 'in preparation' &#xA;&#x9;&#x9;&#x9;else if (not($genre = ( $l_journal, $l_series)))&#xA;&#x9;&#x9;&#x9;then 'n.d.'&#xA;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;"/>
                        </xsl:variable>
                        <xsl:variable name="start-date-or-date">
                            <xsl:value-of select="&#xA;&#x9;&#x9;&#x9;if (pub:publication/event:event/eterms:start-date) &#xA;&#x9;&#x9;&#x9;then pub:publication/event:event/eterms:start-date&#xA;&#x9;&#x9;&#x9;else $date&#xA;&#x9;&#x9;"/>
                        </xsl:variable>
                        <xsl:variable name="date-for-thesis">
                            <xsl:value-of select="&#xA;&#x9;&#x9;&#x9;if (pub:publication/dcterms:issued)&#xA;&#x9;&#x9;&#x9;then func:get_year(pub:publication/dcterms:issued) &#xA;&#x9;&#x9;&#x9;else if (pub:publication/dcterms:dateAccepted)&#xA;&#x9;&#x9;&#x9;then func:get_year(pub:publication/dcterms:dateAccepted) &#xA;&#x9;&#x9;&#x9;else if (pub:publication/eterms:published-online)&#xA;&#x9;&#x9;&#x9;then func:get_year(pub:publication/eterms:published-online) &#xA;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;"/>
                        </xsl:variable>
                        <xsl:variable name="notPublishedRule" as="xs:boolean">
                            <xsl:value-of select="&#xA;&#x9;&#x9;&#x9;$date = ( 'submitted', 'in preparation') and&#xA;&#x9;&#x9;&#x9;not( $genre = ($l_manuscript, $l_courseware-lecture, $l_talk-at-event, $l_poster) )&#xA;&#x9;&#x9;"/>
                        </xsl:variable>
                        <xsl:variable name="degree">
                            <xsl:value-of select="&#xA;&#x9;&#x9;&#x9;concat (&#xA;&#x9;&#x9;&#x9;&#x9;(if (not($v_degree))&#xA;&#x9;&#x9;&#x9;&#x9;then ''&#xA;&#x9;&#x9;&#x9;&#x9;else if ($v_degree=$l_phd)&#xA;&#x9;&#x9;&#x9;&#x9;then 'PhD '&#xA;&#x9;&#x9;&#x9;&#x9;else concat ($l_degree, ' ')),&#xA;&#x9;&#x9;&#x9;&#x9;'Thesis'&#xA;&#x9;&#x9;&#x9;)&#x9;&#x9;&#xA;&#x9;&#x9;"/>
                        </xsl:variable>
                        <xsl:variable name="doi">
                            <xsl:value-of select="(pub:publication/dc:identifier[@xsi:type='eterms:DOI'])[1]/text()"/>
                        </xsl:variable>
                        <xsl:variable name="uri">
                            <xsl:value-of select="(pub:publication/dc:identifier[@xsi:type='eterms:URI'])[1]/text()"/>
                        </xsl:variable>
                        <xsl:variable name="doi-or-uri">
                            <xsl:value-of select="&#xA;&#x9;&#x9;&#x9;if ($doi!='') then concat('doi:', $doi) &#xA;&#x9;&#x9;&#x9;else if ($uri!='') then concat('Retrieved from ', $uri)&#xA;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;"/>
                        </xsl:variable>
                        <xsl:variable name="published-online-and-external-locator">
                            <xsl:value-of select="&#xA;&#x9;&#x9;&#x9;if ( ($genre=$l_article) or (not(pub:publication/dcterms:issued) and pub:publication/eterms:published-online) ) &#xA;&#x9;&#x9;&#x9;then $doi-or-uri&#xA;&#x9;&#x9;&#x9;else ''&#x9;&#xA;&#x9;&#x9;"/>
                        </xsl:variable>
                        <!--### APA specific Default Layout Elements ###-->
	<xsl:variable name="ed-postfix-i18n"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$ed-postfix"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$editorsCount=1"><!--i18n--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">&lt;localized class="editor"&gt;<xsl:copy-of select="$var"/>&lt;/localized&gt;</xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$ed-postfix"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$editorsCount&gt;1"><!--i18n--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">&lt;localized class="editors"&gt;<xsl:copy-of select="$var"/>&lt;/localized&gt;</xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="source-ed-postfix-i18n"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-ed-postfix"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$sourceEditorsCount=1"><!--i18n--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">&lt;localized class="editor"&gt;<xsl:copy-of select="$var"/>&lt;/localized&gt;</xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-ed-postfix"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$sourceEditorsCount&gt;1"><!--i18n--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">&lt;localized class="editors"&gt;<xsl:copy-of select="$var"/>&lt;/localized&gt;</xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="year"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$date"/>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>(</xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"/>
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>).</xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <!--font-style--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">&lt;span class="DisplayDateStatus"&gt;<xsl:copy-of select="$var"/>&lt;/span&gt;</xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="year-for-thesis"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$date-for-thesis"/>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>(</xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"/>
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>).</xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <!--font-style--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">&lt;span class="DisplayDateStatus"&gt;<xsl:copy-of select="$var"/>&lt;/span&gt;</xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="year-with-event-start-date"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$start-date-or-date"/>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>(</xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"/>
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>).</xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <!--font-style--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">&lt;span class="DisplayDateStatus"&gt;<xsl:copy-of select="$var"/>&lt;/span&gt;</xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="published-online-and-external-locator"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$published-online-and-external-locator"/>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text> </xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"/>
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>.</xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="year-authors-or-editors-are-presented"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="$authorsCount&gt;0 or $editorsCount&gt;0">
                                    <xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="year-authors-or-editors-are-not-presented"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="$authorsCount=0 and $editorsCount=0">
                                    <xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="year-and-month-name"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="pub:publication/dcterms:issued">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_year(pub:publication/dcterms:issued/text())"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_month_name(pub:publication/dcterms:issued/text())"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$date"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="not(pub:publication/dcterms:issued)">
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>(</xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"/>
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>).</xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <!--font-style--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">&lt;span class="DisplayDateStatus"&gt;<xsl:copy-of select="$var"/>&lt;/span&gt;</xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="year-and-month-authors-are-presented"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-and-month-name"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="$authorsCount&gt;0">
                                    <xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="year-and-month-authors-are-not-presented"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-and-month-name"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="$authorsCount=0">
                                    <xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="year-editors-are-presented"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="$editorsCount&gt;0">
                                    <xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="year-editors-are-not-presented"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="$editorsCount=0">
                                    <xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="title-with-dot"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:escapeMarkupTags(pub:publication/dc:title/text())"/>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text> </xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"/>
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>.</xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="title-italic"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:escapeMarkupTags(pub:publication/dc:title/text())"/>
                            <!--font-style--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">&lt;span class="Italic"&gt;<xsl:copy-of select="$var"/>&lt;/span&gt;</xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="title-with-dot-italic"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title-italic"/>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text> </xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"/>
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>.</xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="e-number"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="e-number-only"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="(pub:publication/source:source[1]/eterms:start-page=null or pub:publication/source:source[1]/eterms:start-page='') and (pub:publication/source:source[1]/eterms:sequence-number!=null or pub:publication/source:source[1]/eterms:sequence-number!='')">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                          select="pub:publication/source:source[1]/eterms:sequence-number/text()"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>. </xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$e-number-only"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="e-number-and-pages"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="(pub:publication/source:source[1]/eterms:start-page!=null or pub:publication/source:source[1]/eterms:start-page!='') and (pub:publication/source:source[1]/eterms:sequence-number!=null or pub:publication/source:source[1]/eterms:sequence-number!='')">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                          select="pub:publication/source:source[1]/eterms:sequence-number/text()"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>, </xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$e-number-and-pages"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="start-page-end-page"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="start-page-end-page-without-pp"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="pub:publication/source:source[1]/eterms:sequence-number=null or pub:publication/source:source[1]/eterms:sequence-number=''">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/source:source[1]/eterms:start-page/text()"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/source:source[1]/eterms:end-page/text()"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="'-'"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>. </xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$start-page-end-page-without-pp"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="start-page-end-page-with-pp"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="pub:publication/source:source[1]/eterms:sequence-number!=null or pub:publication/source:source[1]/eterms:sequence-number!=''">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/source:source[1]/eterms:start-page/text()"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/source:source[1]/eterms:end-page/text()"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="'-'"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>pp. </xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>. </xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$start-page-end-page-with-pp"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="doi"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="pub:publication/source:source[1]/dc:identifier/@xsi:type=&#34;eterms:DOI&#34;">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/source:source[1]/dc:identifier/text()"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>doi:</xsl:text>
                                        </xsl:if>
                                        <xsl:copy-of select="$var"/>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="source-edition-start-page-end-page"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                              select="pub:publication/source:source[1]/eterms:publishing-info/eterms:edition/text()"/>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$start-page-end-page"/>
                                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                    <xsl:if test="exists($var) and $var!=''">
                                                        <xsl:text>pp. </xsl:text>
                                                    </xsl:if>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="', '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>(</xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"/>
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>)</xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="volume-issue"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="pub:publication/source:source[1]/eterms:sequence-number!=null or pub:publication/source:source[1]/eterms:sequence-number!=''">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/source:source[1]/eterms:volume/text()"/>
                                                                            <!--font-style--><xsl:variable name="var">
                                                                                <xsl:if test="exists($var) and $var!=''">&lt;span class="Italic"&gt;<xsl:copy-of select="$var"/>&lt;/span&gt;</xsl:if>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/source:source[1]/eterms:issue/text()"/>
                                                                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                                <xsl:if test="exists($var) and $var!=''">
                                                                                    <xsl:text>(</xsl:text>
                                                                                </xsl:if>
                                                                                <xsl:copy-of select="$var"/>
                                                                                <xsl:if test="exists($var) and $var!=''">
                                                                                    <xsl:text>)</xsl:text>
                                                                                </xsl:if>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="''"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>: </xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="(pub:publication/source:source[1]/eterms:sequence-number=null or pub:publication/source:source[1]/eterms:sequence-number='') and (pub:publication/source:source[1]/eterms:start-page!=null or pub:publication/source:source[1]/eterms:start-page!='') ">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/source:source[1]/eterms:volume/text()"/>
                                                                            <!--font-style--><xsl:variable name="var">
                                                                                <xsl:if test="exists($var) and $var!=''">&lt;span class="Italic"&gt;<xsl:copy-of select="$var"/>&lt;/span&gt;</xsl:if>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/source:source[1]/eterms:issue/text()"/>
                                                                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                                <xsl:if test="exists($var) and $var!=''">
                                                                                    <xsl:text>(</xsl:text>
                                                                                </xsl:if>
                                                                                <xsl:copy-of select="$var"/>
                                                                                <xsl:if test="exists($var) and $var!=''">
                                                                                    <xsl:text>)</xsl:text>
                                                                                </xsl:if>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="''"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>, </xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="(pub:publication/source:source[1]/eterms:sequence-number=null or pub:publication/source:source[1]/eterms:sequence-number='') and (pub:publication/source:source[1]/eterms:start-page=null or pub:publication/source:source[1]/eterms:start-page='') ">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/source:source[1]/eterms:volume/text()"/>
                                                                            <!--font-style--><xsl:variable name="var">
                                                                                <xsl:if test="exists($var) and $var!=''">&lt;span class="Italic"&gt;<xsl:copy-of select="$var"/>&lt;/span&gt;</xsl:if>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/source:source[1]/eterms:issue/text()"/>
                                                                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                                <xsl:if test="exists($var) and $var!=''">
                                                                                    <xsl:text>(</xsl:text>
                                                                                </xsl:if>
                                                                                <xsl:copy-of select="$var"/>
                                                                                <xsl:if test="exists($var) and $var!=''">
                                                                                    <xsl:text>)</xsl:text>
                                                                                </xsl:if>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="''"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>. </xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="source-title"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                          select="func:escapeMarkupTags(pub:publication/source:source[1]/dc:title/text())"/>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text> </xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"/>
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>,</xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <!--font-style--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">&lt;span class="Italic"&gt;<xsl:copy-of select="$var"/>&lt;/span&gt;</xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="place-publisher"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/eterms:publishing-info/eterms:place/text()"/>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/eterms:publishing-info/dc:publisher/text()"/>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="': '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text> </xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <!--### Predefined Layout Elements ###-->
	<xsl:variable name="editors-base"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="editors-base-impl"><!--### Repeatable Layout Element ###-->
	<xsl:variable name="var" select="''"/>
                                                <xsl:variable name="var">
                                                    <xsl:call-template name="applyDelimiter">
                                                        <xsl:with-param name="les">
                                                            <xsl:for-each select="pub:publication/eterms:creator[@role=$l_editor]">
                                                                <xsl:choose>
                                                                    <xsl:when test="position()=last()">
                                                                        <le position-delimiter=", &amp;amp; ">
                                                                            <xsl:call-template name="applyDelimiter">
                                                                                <xsl:with-param name="les">
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="organization:organization/dc:title/text()"/>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="person:person/eterms:family-name/text()"/>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"/>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                </xsl:with-param>
                                                                                <xsl:with-param name="delimiter" select="', '"/>
                                                                            </xsl:call-template>
                                                                        </le>
                                                                    </xsl:when>
                                                                    <xsl:otherwise>
                                                                        <le position-delimiter=", ">
                                                                            <xsl:call-template name="applyDelimiter">
                                                                                <xsl:with-param name="les">
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="organization:organization/dc:title/text()"/>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="person:person/eterms:family-name/text()"/>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"/>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                </xsl:with-param>
                                                                                <xsl:with-param name="delimiter" select="', '"/>
                                                                            </xsl:call-template>
                                                                        </le>
                                                                    </xsl:otherwise>
                                                                </xsl:choose>
                                                            </xsl:for-each>
                                                        </xsl:with-param>
                                                        <xsl:with-param name="delimiter" select="', '"/>
                                                    </xsl:call-template>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$editors-base-impl"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="''"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="editors"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="$editorsCount&gt;0">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$editors-base"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$ed-postfix-i18n"/>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text> (</xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>)</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                        <xsl:copy-of select="$var"/>
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>.</xsl:text>
                                        </xsl:if>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="editors-book"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="$authorsCount&gt;0 and $editorsCount&gt;0 and $genre=$l_book">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$editors-base"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$ed-postfix-i18n"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="', '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text> (</xsl:text>
                                        </xsl:if>
                                        <xsl:copy-of select="$var"/>
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>)</xsl:text>
                                        </xsl:if>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="authors-or-editors"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$authorsCount&gt;0">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="authors-base"><!--### Repeatable Layout Element ###-->
	<xsl:variable name="var" select="''"/>
                                                                            <xsl:variable name="var">
                                                                                <xsl:call-template name="applyDelimiter">
                                                                                    <xsl:with-param name="les">
                                                                                        <xsl:for-each select="pub:publication/eterms:creator[@role=$l_author]">
                                                                                            <xsl:choose>
                                                                                                <xsl:when test="position()=last()">
                                                                                                    <le position-delimiter=", &amp;amp; ">
                                                                                                        <xsl:call-template name="applyDelimiter">
                                                                                                            <xsl:with-param name="les">
                                                                                                                <le>
                                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="organization:organization/dc:title/text()"/>
                                                                                                                        <xsl:copy-of select="$var"/>
                                                                                                                    </xsl:variable>
                                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                                </le>
                                                                                                                <le>
                                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="person:person/eterms:family-name/text()"/>
                                                                                                                        <xsl:copy-of select="$var"/>
                                                                                                                    </xsl:variable>
                                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                                </le>
                                                                                                                <le>
                                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"/>
                                                                                                                        <xsl:copy-of select="$var"/>
                                                                                                                    </xsl:variable>
                                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                                </le>
                                                                                                            </xsl:with-param>
                                                                                                            <xsl:with-param name="delimiter" select="', '"/>
                                                                                                        </xsl:call-template>
                                                                                                    </le>
                                                                                                </xsl:when>
                                                                                                <xsl:otherwise>
                                                                                                    <le position-delimiter=", ">
                                                                                                        <xsl:call-template name="applyDelimiter">
                                                                                                            <xsl:with-param name="les">
                                                                                                                <le>
                                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="organization:organization/dc:title/text()"/>
                                                                                                                        <xsl:copy-of select="$var"/>
                                                                                                                    </xsl:variable>
                                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                                </le>
                                                                                                                <le>
                                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="person:person/eterms:family-name/text()"/>
                                                                                                                        <xsl:copy-of select="$var"/>
                                                                                                                    </xsl:variable>
                                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                                </le>
                                                                                                                <le>
                                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"/>
                                                                                                                        <xsl:copy-of select="$var"/>
                                                                                                                    </xsl:variable>
                                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                                </le>
                                                                                                            </xsl:with-param>
                                                                                                            <xsl:with-param name="delimiter" select="', '"/>
                                                                                                        </xsl:call-template>
                                                                                                    </le>
                                                                                                </xsl:otherwise>
                                                                                            </xsl:choose>
                                                                                        </xsl:for-each>
                                                                                    </xsl:with-param>
                                                                                    <xsl:with-param name="delimiter" select="', '"/>
                                                                                </xsl:call-template>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$authors-base"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$authorsCount=0">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$editors"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="''"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="source-editors"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="$sourceEditorsCount&gt;0">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="source-editors-MoreThan1"><!--### Repeatable Layout Element ###-->
	<xsl:variable name="var" select="''"/>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <xsl:for-each select="pub:publication/source:source[1]/eterms:creator[@role=$l_editor]">
                                                                        <xsl:choose>
                                                                            <xsl:when test="position()=last()">
                                                                                <le position-delimiter=", &amp;amp; ">
                                                                                    <xsl:call-template name="applyDelimiter">
                                                                                        <xsl:with-param name="les">
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="organization:organization/dc:title/text()"/>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </xsl:variable>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </le>
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"/>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </xsl:variable>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </le>
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="person:person/eterms:family-name/text()"/>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </xsl:variable>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </le>
                                                                                        </xsl:with-param>
                                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                                    </xsl:call-template>
                                                                                </le>
                                                                            </xsl:when>
                                                                            <xsl:otherwise>
                                                                                <le position-delimiter=", ">
                                                                                    <xsl:call-template name="applyDelimiter">
                                                                                        <xsl:with-param name="les">
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="organization:organization/dc:title/text()"/>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </xsl:variable>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </le>
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(person:person/eterms:given-name/text())"/>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </xsl:variable>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </le>
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="person:person/eterms:family-name/text()"/>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </xsl:variable>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </le>
                                                                                        </xsl:with-param>
                                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                                    </xsl:call-template>
                                                                                </le>
                                                                            </xsl:otherwise>
                                                                        </xsl:choose>
                                                                    </xsl:for-each>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$source-editors-MoreThan1"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-ed-postfix-i18n"/>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text> (</xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>)</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="authors-or-editors-and-year-and-title-with-dot"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$authors-or-editors"/>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-authors-or-editors-are-presented"/>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title-with-dot"/>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-authors-or-editors-are-not-presented"/>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="authors-or-editors-and-year-and-title-with-dot-italic"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$authors-or-editors"/>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-authors-or-editors-are-presented"/>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title-with-dot-italic"/>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-authors-or-editors-are-not-presented"/>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="debugBlock"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$objid"/>
                                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                    <xsl:if test="exists($var) and $var!=''">
                                                        <xsl:text>OBJID:</xsl:text>
                                                    </xsl:if>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$genre"/>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/dc:title/text()"/>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/dcterms:abstract[contains(.,'APA')]/text()"/>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/dc:subject/text()"/>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/dcterms:issued/text()"/>
                                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                    <xsl:if test="exists($var) and $var!=''">
                                                        <xsl:text>issued:</xsl:text>
                                                    </xsl:if>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/eterms:published-online/text()"/>
                                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                    <xsl:if test="exists($var) and $var!=''">
                                                        <xsl:text>published-online:</xsl:text>
                                                    </xsl:if>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$doi"/>
                                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                    <xsl:if test="exists($var) and $var!=''">
                                                        <xsl:text>doi:</xsl:text>
                                                    </xsl:if>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/dcterms:dateAccepted/text()"/>
                                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                    <xsl:if test="exists($var) and $var!=''">
                                                        <xsl:text>dateAccepted:</xsl:text>
                                                    </xsl:if>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/dcterms:dateSubmitted/text()"/>
                                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                    <xsl:if test="exists($var) and $var!=''">
                                                        <xsl:text>dateSubmitted:</xsl:text>
                                                    </xsl:if>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/dcterms:modified/text()"/>
                                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                    <xsl:if test="exists($var) and $var!=''">
                                                        <xsl:text>modified:</xsl:text>
                                                    </xsl:if>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/dcterms:created/text()"/>
                                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                    <xsl:if test="exists($var) and $var!=''">
                                                        <xsl:text>created:</xsl:text>
                                                    </xsl:if>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="','"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>debug:</xsl:text>
                                </xsl:if>
                                <xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <!--font-style--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">&lt;span class="Blue"&gt;<xsl:copy-of select="$var"/>&lt;/span&gt;</xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <!--### End of Predefined Layout Elements ###-->
	<!--### Citation Style Layout Definitions ###-->
	<xsl:choose>
                            <xsl:when test="not($hasPublication)">
                                <xsl:variable name="isNotPublication"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$objid"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>## Citaion style layout is not defined for metadata record: </xsl:text>
                                        </xsl:if>
                                        <xsl:copy-of select="$var"/>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <xsl:copy-of select="$isNotPublication"/>
                            </xsl:when>
                            <xsl:when test="$notPublishedRule">
                                <xsl:variable name="submitted-or-in-preparation"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$authors-or-editors-and-year-and-title-with-dot"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <xsl:copy-of select="$submitted-or-in-preparation"/>
                            </xsl:when>
                            <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;$genre = ( $l_article, $l_paper, $l_case-note, $l_book-review, $l_case-study, $l_editorial, $l_newspaper-article ) &#xA;&#x9;&#x9;&#x9;&#x9;or ( $genre = ( $l_other, $l_paper, $l_conference-report, $l_conference-paper, $l_meeting-abstract) and $source-type = $l_journal )  &#xA;&#x9;&#x9;&#x9;">
                                <xsl:variable name="journal-article-etc"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$authors-or-editors-and-year-and-title-with-dot"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="volume-issue-start-page-end-page"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-title"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$volume-issue"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$e-number"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$start-page-end-page"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>. </xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$volume-issue-start-page-end-page"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$doi"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$published-online-and-external-locator"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <xsl:copy-of select="$journal-article-etc"/>
                            </xsl:when>
                            <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;$genre = ( $l_book, $l_proceedings, $l_monograph, $l_commentary, $l_collected-edition, $l_handbook, $l_festschrift ) &#xA;&#x9;&#x9;&#x9;&#x9;or ( $genre = ( $l_conference-report, $l_conference-paper, $l_meeting-abstract, $l_other ) and &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;not($source-type = ($l_journal, $l_proceedings, $l_book ))  &#xA;&#x9;&#x9;&#x9;&#x9;)  &#xA;&#x9;&#x9;&#x9;&#x9;or ( $genre = $l_other and not($source-type) )&#xA;&#x9;&#x9;&#x9;">
                                <xsl:variable name="book-etc"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$authors-or-editors"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-authors-or-editors-are-presented"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title-italic"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                                            <!--valid-if--><xsl:variable name="var">
                                                                                <xsl:if test="$genre = $l_book">
                                                                                    <xsl:variable name="var">
                                                                                        <xsl:call-template name="applyDelimiter">
                                                                                            <xsl:with-param name="les">
                                                                                                <le>
                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                                      select="func:escapeMarkupTags(pub:publication/dcterms:alternative[1]/text())"/>
                                                                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                                                            <xsl:if test="exists($var) and $var!=''">
                                                                                                                <xsl:text>: </xsl:text>
                                                                                                            </xsl:if>
                                                                                                            <xsl:copy-of select="$var"/>
                                                                                                        </xsl:variable>
                                                                                                        <!--font-style--><xsl:variable name="var">
                                                                                                            <xsl:if test="exists($var) and $var!=''">&lt;span class="Italic"&gt;<xsl:copy-of select="$var"/>&lt;/span&gt;</xsl:if>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"/>
                                                                                                    </xsl:variable>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </le>
                                                                                                <le>
                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                                                                        <xsl:variable name="var">
                                                                                                            <xsl:call-template name="applyDelimiter">
                                                                                                                <xsl:with-param name="les">
                                                                                                                    <le>
                                                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$editors-book"/>
                                                                                                                            <xsl:copy-of select="$var"/>
                                                                                                                        </xsl:variable>
                                                                                                                        <xsl:copy-of select="$var"/>
                                                                                                                    </le>
                                                                                                                    <le>
                                                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                                                          select="pub:publication/eterms:publishing-info/eterms:edition/text()"/>
                                                                                                                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                                                                                <xsl:if test="exists($var) and $var!=''">
                                                                                                                                    <xsl:text> (</xsl:text>
                                                                                                                                </xsl:if>
                                                                                                                                <xsl:copy-of select="$var"/>
                                                                                                                                <xsl:if test="exists($var) and $var!=''">
                                                                                                                                    <xsl:text>)</xsl:text>
                                                                                                                                </xsl:if>
                                                                                                                            </xsl:variable>
                                                                                                                            <xsl:copy-of select="$var"/>
                                                                                                                        </xsl:variable>
                                                                                                                        <xsl:copy-of select="$var"/>
                                                                                                                    </le>
                                                                                                                </xsl:with-param>
                                                                                                                <xsl:with-param name="delimiter" select="'. '"/>
                                                                                                            </xsl:call-template>
                                                                                                        </xsl:variable>
                                                                                                        <xsl:copy-of select="$var"/>
                                                                                                    </xsl:variable>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </le>
                                                                                            </xsl:with-param>
                                                                                            <xsl:with-param name="delimiter" select="' '"/>
                                                                                        </xsl:call-template>
                                                                                    </xsl:variable>
                                                                                    <xsl:copy-of select="$var"/>
                                                                                </xsl:if>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="''"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-authors-or-editors-are-not-presented"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$place-publisher"/>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$published-online-and-external-locator"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <xsl:copy-of select="$book-etc"/>
                            </xsl:when>
                            <xsl:when test=" $genre = $l_thesis and (pub:publication/dcterms:issued or pub:publication/dcterms:dateAccepted)">
                                <xsl:variable name="thesis-in-print-or-accepted"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$authors-or-editors"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-for-thesis"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title-with-dot"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="degree-place-publisher"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$degree"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/eterms:publishing-info/dc:publisher/text()"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/eterms:publishing-info/eterms:place/text()"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text> </xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$degree-place-publisher"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <xsl:copy-of select="$thesis-in-print-or-accepted"/>
                            </xsl:when>
                            <xsl:when test=" $genre = $l_thesis and pub:publication/eterms:published-online ">
                                <xsl:variable name="thesis-published-online"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$authors-or-editors"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-for-thesis"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="degree-place-publisher-year"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title-italic"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="degree-place-publisher-year"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                                            <xsl:variable name="var">
                                                                                <xsl:call-template name="applyDelimiter">
                                                                                    <xsl:with-param name="les">
                                                                                        <le>
                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$degree"/>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </le>
                                                                                        <le>
                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/eterms:publishing-info/dc:publisher/text()"/>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </le>
                                                                                        <le>
                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/eterms:publishing-info/eterms:place/text()"/>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </le>
                                                                                        <le>
                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$date-for-thesis"/>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </le>
                                                                                    </xsl:with-param>
                                                                                    <xsl:with-param name="delimiter" select="', '"/>
                                                                                </xsl:call-template>
                                                                            </xsl:variable>
                                                                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                                <xsl:if test="exists($var) and $var!=''">
                                                                                    <xsl:text>(</xsl:text>
                                                                                </xsl:if>
                                                                                <xsl:copy-of select="$var"/>
                                                                                <xsl:if test="exists($var) and $var!=''">
                                                                                    <xsl:text>)</xsl:text>
                                                                                </xsl:if>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$degree-place-publisher-year"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$degree-place-publisher-year"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$uri"/>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text> Retrieved from </xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"/>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <xsl:copy-of select="$thesis-published-online"/>
                            </xsl:when>
                            <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;$genre = ($l_book-item, $l_contr-to-collect-ed, $l_contr-to-handbook, $l_contr-to-encyclopedia, $l_contr-to-festschrift, $l_contr-to-commentary )&#xA;&#x9;&#x9;&#x9;&#x9;or ( $genre = $l_other and $source-type = $l_book )  &#xA;&#x9;&#x9;&#x9;&#x9;or ( &#xA;&#x9;&#x9;&#x9;&#x9;&#x9; $genre = ( $l_conference-report, $l_conference-paper, $l_meeting-abstract )   &#xA;&#x9;&#x9;&#x9;&#x9;&#x9; and  $source-type = ( $l_book, $l_proceedings )  &#xA;&#x9;&#x9;&#x9;&#x9;)  &#xA;&#x9;&#x9;&#x9;">
                                <xsl:variable name="book-item-etc"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$authors-or-editors-and-year-and-title-with-dot"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-editors"/>
                                                                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                                <xsl:copy-of select="$var"/>
                                                                                <xsl:if test="exists($var) and $var!=''">
                                                                                    <xsl:text>, </xsl:text>
                                                                                </xsl:if>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                          select="func:escapeMarkupTags(pub:publication/source:source[1]/dc:title/text())"/>
                                                                            <!--font-style--><xsl:variable name="var">
                                                                                <xsl:if test="exists($var) and $var!=''">&lt;span class="Italic"&gt;<xsl:copy-of select="$var"/>&lt;/span&gt;</xsl:if>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-edition-start-page-end-page"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text> In </xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="source-place-publisher"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                          select="pub:publication/source:source[1]/eterms:publishing-info/eterms:place/text()"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                          select="pub:publication/source:source[1]/eterms:publishing-info/dc:publisher/text()"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="': '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text> </xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$source-place-publisher"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$published-online-and-external-locator"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <xsl:copy-of select="$book-item-etc"/>
                            </xsl:when>
                            <xsl:when test="$genre = $l_issue">
                                <xsl:variable name="issue"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$editors"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-editors-are-presented"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:escapeMarkupTags(pub:publication/dc:title/text())"/>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text> </xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text> [Special Issue]. </xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-editors-are-not-presented"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-title"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$volume-issue"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <xsl:copy-of select="$issue"/>
                            </xsl:when>
                            <xsl:when test="$genre = $l_journal">
                                <xsl:variable name="journal"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$editors"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title-with-dot-italic"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$place-publisher"/>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <xsl:copy-of select="$journal"/>
                            </xsl:when>
                            <xsl:when test="$genre = ($l_manuscript, $l_opinion)">
                                <xsl:variable name="manuscript"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$authors-or-editors-and-year-and-title-with-dot-italic"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                        <xsl:copy-of select="$var"/>
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text> Unpublished Manuscript.</xsl:text>
                                        </xsl:if>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <xsl:copy-of select="$manuscript"/>
                            </xsl:when>
                            <xsl:when test="$genre = $l_series">
                                <xsl:variable name="series"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$editors"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title-with-dot-italic"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$place-publisher"/>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <xsl:copy-of select="$series"/>
                            </xsl:when>
                            <xsl:when test="$genre = $l_talk-at-event">
                                <xsl:variable name="talk-at-event"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$authors-or-editors-and-year-and-title-with-dot-italic"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                          select="func:escapeMarkupTags(pub:publication/event:event/dc:title/text())"/>
                                                                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                                <xsl:if test="exists($var) and $var!=''">
                                                                                    <xsl:text>Talk presented at </xsl:text>
                                                                                </xsl:if>
                                                                                <xsl:copy-of select="$var"/>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/event:event/eterms:place/text()"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                                            <xsl:variable name="var">
                                                                                <xsl:call-template name="applyDelimiter">
                                                                                    <xsl:with-param name="les">
                                                                                        <le>
                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/event:event/eterms:start-date/text()"/>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </le>
                                                                                        <le>
                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/event:event/eterms:end-date/text()"/>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </le>
                                                                                    </xsl:with-param>
                                                                                    <xsl:with-param name="delimiter" select="' - '"/>
                                                                                </xsl:call-template>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="'. '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text> </xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <xsl:copy-of select="$talk-at-event"/>
                            </xsl:when>
                            <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;$genre = $l_courseware-lecture&#xA;&#x9;&#x9;&#x9;&#x9;">
                                <xsl:variable name="courseware-lecture"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$authors-or-editors-and-year-and-title-with-dot"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                      select="func:escapeMarkupTags(pub:publication/event:event/dc:title/text())"/>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text> </xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/event:event/eterms:place/text()"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                                            <xsl:variable name="var">
                                                                                <xsl:call-template name="applyDelimiter">
                                                                                    <xsl:with-param name="les">
                                                                                        <le>
                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/event:event/eterms:start-date/text()"/>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </le>
                                                                                        <le>
                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/event:event/eterms:end-date/text()"/>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </le>
                                                                                    </xsl:with-param>
                                                                                    <xsl:with-param name="delimiter" select="' - '"/>
                                                                                </xsl:call-template>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text> </xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <xsl:copy-of select="$courseware-lecture"/>
                            </xsl:when>
                            <xsl:when test="$genre = $l_poster">
                                <xsl:variable name="poster"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$authors-or-editors-and-year-and-title-with-dot-italic"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="event-title-and-place"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="exists(pub:publication/event:event/dc:title/text()) and pub:publication/event:event/dc:title/text()!=''">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                  select="func:escapeMarkupTags(pub:publication/event:event/dc:title/text())"/>
                                                                                    <xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="pub:publication/event:event/eterms:place/text()"/>
                                                                                    <xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="', '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                    <xsl:if test="exists($var) and $var!=''">
                                                                        <xsl:text> Poster presented at </xsl:text>
                                                                    </xsl:if>
                                                                    <xsl:copy-of select="$var"/>
                                                                    <xsl:if test="exists($var) and $var!=''">
                                                                        <xsl:text>.</xsl:text>
                                                                    </xsl:if>
                                                                </xsl:variable>
                                                                <xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$event-title-and-place"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <xsl:copy-of select="$poster"/>
                            </xsl:when>
                            <xsl:when test="$genre = $l_report">
                                <xsl:variable name="report"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$authors-or-editors"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-authors-or-editors-are-presented"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="title-report-identifier"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title-italic"/>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                          select="(pub:publication/dc:identifier[@xsi:type='eterms:OTHER'])[1]/text()"/>
                                                                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                                <xsl:if test="exists($var) and $var!=''">
                                                                                    <xsl:text>(</xsl:text>
                                                                                </xsl:if>
                                                                                <xsl:copy-of select="$var"/>
                                                                                <xsl:if test="exists($var) and $var!=''">
                                                                                    <xsl:text>)</xsl:text>
                                                                                </xsl:if>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text> </xsl:text>
                                                            </xsl:if>
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$title-report-identifier"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-authors-or-editors-are-not-presented"/>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$place-publisher"/>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="''"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <xsl:copy-of select="$report"/>
                            </xsl:when>
                        </xsl:choose>
                        <!--### End of Citation Style Layout Definitions ###-->
	</xsl:for-each>
                </xsl:variable>
                <xsl:value-of select="func:cleanCitation($citation)"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!--### Includes ###-->
	<xsl:template match="escidocComponents:content[@storage='internal-managed']">
    	   <xsl:element name="{name(.)}">
    		      <xsl:copy-of select="@*[name(.)!='xlink:href']"/>
    		      <xsl:attribute name="xlink:href"
                           select="concat(         $pubman_instance,         '/item/',          ../../../ei:properties/prop:version/@objid,         '/component/',         ../@objid,         '/',         ../escidocComponents:properties/prop:file-name        )"/>
    	   </xsl:element>
    </xsl:template>
    <xsl:template name="applyDelimiter">
		      <xsl:param name="les"/>
		      <xsl:param name="delimiter"/>
		      <xsl:variable name="les_filled" select="$les/le[exists(text()) and text()!='']"/>
		      <xsl:for-each select="$les_filled">
			         <xsl:value-of select="if (position()&gt;1) then @position-delimiter else ''"/>
			         <xsl:copy-of select="child::node()" copy-namespaces="no"/>
			         <xsl:if test="position()!=last() and not(@position-delimiter)">
				            <xsl:value-of select="$delimiter"/>
			         </xsl:if>
		      </xsl:for-each>
	   </xsl:template>
    <!--### Runtime Functions ###-->
	<xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:get_year">
		      <xsl:param name="date"/>
		      <xsl:value-of select="substring($date,1,4)"/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:get_month">
		      <xsl:param name="date"/>
		      <xsl:value-of select="substring($date,6,2)"/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:get_month_name">
		      <xsl:param name="date"/>
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
		      <xsl:value-of select="     $months/m[     matches(      tokenize($date, '-')[2], @n     )     ]   "/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:get_initials">
		      <xsl:param name="str"/>
		      <xsl:variable name="delim" select="if (contains ($str, '-')) then '-' else ' '"/>
		      <xsl:for-each select="tokenize(normalize-space ($str), '\s+|\.\s+|\-\s*')">
			         <xsl:value-of select="concat(substring (., 1, 1), if (position()!=last())then concat ('.', $delim) else '.')"/>
		      </xsl:for-each>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:fname_initials">
		      <xsl:param name="fname"/>
		      <xsl:param name="gname"/>
		      <xsl:param name="delim"/>
		
		      <xsl:value-of select="    if ( jfunc:isCJK(concat($fname, $gname) ) )     then string-join( ($fname, $gname), $delim )    else string-join( ($fname, func:get_initials($gname)), $delim )   "/>
		
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:initials_fname">
		      <xsl:param name="gname"/>
		      <xsl:param name="fname"/>
		      <xsl:param name="delim"/>
		
		      <xsl:value-of select="    if ( jfunc:isCJK(concat($fname, $gname) ) )     then string-join( ($fname, $gname), $delim )    else string-join( (func:get_initials($gname), $fname), $delim )   "/>
		
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:escapeMarkupTags">
		      <xsl:param name="str"/>
		      <xsl:value-of select="jfunc:escapeMarkupTags($str)"/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:cleanCitation">
		      <xsl:param name="str"/>
			     <xsl:value-of select="     normalize-space (     functx:replace-multi (      $str,      ( '([.,?!:;])\s*(&lt;[/]span&gt;)\s*\1', '([.,?!:;])\s*\1', '\.&#34;\.', '\s+([.,?!:;])', '\s*(&lt;[/]?span&gt;)\s*([.,?!:;])', '([?!])+\.' ),      ( '$1$2',         '$1',    '.&#34;',  '$1',     '$1$2',         '$1' )     )     )    "/>
			     <!-- 																	.".=>." ??? -->
	</xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="functx:replace-multi"
                  as="xs:string?">
	       <xsl:param name="arg" as="xs:string?"/> 
	       <xsl:param name="changeFrom" as="xs:string*"/> 
	       <xsl:param name="changeTo" as="xs:string*"/> 
	 
	       <xsl:sequence select="      if (count($changeFrom) &gt; 0)     then functx:replace-multi(            replace($arg, $changeFrom[1],                       functx:if-absent($changeTo[1],'')),            $changeFrom[position() &gt; 1],            $changeTo[position() &gt; 1])     else $arg   "/>
	   
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="functx:if-absent"
                  as="item()*">
	       <xsl:param name="arg" as="item()*"/> 
	       <xsl:param name="value" as="item()*"/> 
	 
	       <xsl:sequence select="       if (exists($arg))      then $arg      else $value   "/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:get_reverse_date">
		      <xsl:param name="input_date"/>
		      <xsl:if test="$input_date[.!=''] ">
			         <xsl:value-of select="concat(substring($input_date,9,2),'.',substring($input_date,6,2),'.',substring($input_date,1,4))"/>
		      </xsl:if>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:getCitationStyleForJournal">
		      <xsl:param name="idType"/>
		      <xsl:param name="idValue"/>
		      <xsl:value-of select="jfunc:getCitationStyleForJournal($idType,$idValue)"/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:substringAfterEdition">
		      <xsl:param name="inputWithSpaceComma"/>
		      <xsl:value-of select="substring-before(substring-after($inputWithSpaceComma,', '), ' ')"/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:substringBeforeEdition">
		      <xsl:param name="inputWithSpaceComma"/>
		      <xsl:value-of select="substring-before(substring-before($inputWithSpaceComma,', '), ' ')"/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:substringBeforeInstalment">
		      <xsl:param name="inputWithInstalment"/>
		      <xsl:value-of select="      if (contains($inputWithInstalment, 'instl'))      then substring-before($inputWithInstalment, 'instl')        else if (contains($inputWithInstalment, 'Lf'))      then substring-before($inputWithInstalment, 'Lf')        else ''      "/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:substringBeforeSince">
		      <xsl:param name="inputWithSince"/>
	       <xsl:value-of select="       if (contains($inputWithSince, 'since'))       then substring-before($inputWithSince, 'since')         else if (contains($inputWithSince, 'seit'))       then substring-before($inputWithSince, 'seit')         else ''       "/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:substringAfterSince">
	       <xsl:param name="inputWithSince"/>
		      <xsl:value-of select="      if (contains($inputWithSince, 'since'))      then substring-after($inputWithSince, 'since')        else if (contains($inputWithSince, 'seit'))      then substring-after($inputWithSince, 'seit')        else ''      "/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:substringAfterReviewOf">
		      <xsl:param name="inputReviewTitle"/>
		      <xsl:value-of select="substring-after($inputReviewTitle,'Review of:')"/>
	   </xsl:function>
</xsl:stylesheet>