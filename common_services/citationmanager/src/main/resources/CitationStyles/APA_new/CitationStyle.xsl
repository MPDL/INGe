<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cit="http://www.escidoc.de/citationstyle"
                xmlns:snippet="http://www.escidoc.de/citationstyle/snippet"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:escidocItem="http://www.escidoc.de/schemas/item/0.8"
                xmlns:jfunc="java:de.mpg.escidoc.services.citationmanager.utils.XsltHelper"
                xmlns:func="http://www.escidoc.de/citationstyle/functions"
                xmlns:ei="http://www.escidoc.de/schemas/item/0.8"
                xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.4"
                xmlns:mdp="http://escidoc.mpg.de/metadataprofile/schema/0.1/"
                xmlns:pub="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication"
                xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                version="2.0">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:template match="/">
        <snippet:snippets>
            <xsl:for-each select="//ei:item/mdr:md-records/mdr:md-record"><!--### Variables ###-->
	<xsl:variable name="objid">
                    <xsl:value-of select="../../@objid"/>
                </xsl:variable>
                <xsl:variable name="genre">
                    <xsl:value-of select="mdp:publication/@type"/>
                </xsl:variable>
                <xsl:variable name="source-type">
                    <xsl:value-of select="mdp:publication/pub:source[1]/@type"/>
                </xsl:variable>
                <xsl:variable name="hasPublication">
                    <xsl:value-of select="exists(mdp:publication)"/>
                </xsl:variable>
                <xsl:variable name="authorsCount">
                    <xsl:value-of select="count(mdp:publication/pub:creator[@role='author'])"/>
                </xsl:variable>
                <xsl:variable name="editorsCount">
                    <xsl:value-of select="count(mdp:publication/pub:creator[@role='editor'])"/>
                </xsl:variable>
                <xsl:variable name="ed-postfix">
                    <xsl:value-of select="if ($editorsCount=1) then ' (Ed.)' else ' (Eds.)'"/>
                </xsl:variable>
                <xsl:variable name="sourceEditorsCount">
                    <xsl:value-of select="count(mdp:publication/pub:source[1]/pub:creator[@role='editor'])"/>
                </xsl:variable>
                <xsl:variable name="source-ed-postfix">
                    <xsl:value-of select="if ($sourceEditorsCount=1) then ' (Ed.)' else ' (Eds.)'"/>
                </xsl:variable>
                <xsl:variable name="genre-exception">
                    <xsl:value-of select="$genre=( 'poster', 'talk-at-event', 'courseware-lecture' )"/>
                </xsl:variable>
                <xsl:variable name="date">
                    <xsl:value-of select="&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;if ($genre='manuscript' and mdp:publication/dcterms:created) &#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(mdp:publication/dcterms:created)  &#xA;&#x9;&#x9;&#x9;&#x9;else if ($genre-exception and mdp:publication/pub:event/e:start-date)&#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(mdp:publication/pub:event/e:start-date) &#xA;&#x9;&#x9;&#x9;&#x9;else if (mdp:publication/dcterms:issued)&#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(mdp:publication/dcterms:issued) &#xA;&#x9;&#x9;&#x9;&#x9;else if (mdp:publication/pub:published-online) &#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(mdp:publication/pub:published-online) &#xA;&#x9;&#x9;&#x9;&#x9;else if (( $genre-exception and $genre='manuscript') and mdp:publication/dcterms:dateAccepted)&#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(mdp:publication/dcterms:dateAccepted)&#xA;&#x9;&#x9;&#x9;&#x9;else if (mdp:publication/dcterms:dateAccepted) &#xA;&#x9;&#x9;&#x9;&#x9;then 'in press' &#xA;&#x9;&#x9;&#x9;&#x9;else if&#x9;(( $genre-exception or $genre='manuscript') and mdp:publication/dcterms:dateSubmitted)&#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(mdp:publication/dcterms:dateSubmitted)&#xA;&#x9;&#x9;&#x9;&#x9;else if (mdp:publication/dcterms:dateSubmitted) &#xA;&#x9;&#x9;&#x9;&#x9;then 'submitted' &#xA;&#x9;&#x9;&#x9;&#x9;else if&#x9;(( $genre-exception or $genre='manuscript') and mdp:publication/dcterms:modified)&#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(mdp:publication/dcterms:modified)&#xA;&#x9;&#x9;&#x9;&#x9;else if (( $genre-exception or $genre='manuscript') and mdp:publication/dcterms:created)&#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(mdp:publication/dcterms:created)&#xA;&#x9;&#x9;&#x9;&#x9;else if (mdp:publication/dcterms:modified or mdp:publication/dcterms:created) &#xA;&#x9;&#x9;&#x9;&#x9;then 'in preparation' &#xA;&#x9;&#x9;&#x9;&#x9;else if (not($genre = ( 'journal', 'series')))&#xA;&#x9;&#x9;&#x9;&#x9;then 'n.d.'&#xA;&#x9;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;"/>
                </xsl:variable>
                <xsl:variable name="start-date-or-date">
                    <xsl:value-of select="&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;if (mdp:publication/pub:event/e:start-date) &#xA;&#x9;&#x9;&#x9;&#x9;then mdp:publication/pub:event/e:start-date&#xA;&#x9;&#x9;&#x9;&#x9;else $date&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;"/>
                </xsl:variable>
                <xsl:variable name="date-for-thesis">
                    <xsl:value-of select="&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;if (mdp:publication/dcterms:issued)&#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(mdp:publication/dcterms:issued) &#xA;&#x9;&#x9;&#x9;&#x9;else if (mdp:publication/dcterms:dateAccepted)&#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(mdp:publication/dcterms:dateAccepted) &#xA;&#x9;&#x9;&#x9;&#x9;else if (mdp:publication/pub:published-online)&#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(mdp:publication/pub:published-online) &#xA;&#x9;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;"/>
                </xsl:variable>
                <xsl:variable name="notPublishedRule">
                    <xsl:value-of select="&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;($date = ( 'submitted', 'in preparation')) and&#xA;&#x9;&#x9;&#x9;&#x9;not( $genre = ('manuscript', 'courseware-lecture', 'talk-at-event', 'poster') )&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;"/>
                </xsl:variable>
                <xsl:variable name="degree">
                    <xsl:value-of select="&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;if (mdp:publication/pub:degree)&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;then &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;concat (&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;if (mdp:publication/pub:degree='phd')&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;then 'PhD'&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;else mdp:publication/pub:degree,&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;' Thesis'&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;)&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;"/>
                </xsl:variable>
                <xsl:variable name="doi">
                    <xsl:value-of select="(mdp:publication/e:identifier[@type='eidt:DOI'])[1]"/>
                </xsl:variable>
                <xsl:variable name="uri">
                    <xsl:value-of select="(mdp:publication/e:identifier[@type='dcterms:URI'])[1]"/>
                </xsl:variable>
                <xsl:variable name="published-online-and-external-locator">
                    <xsl:value-of select="&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;if (not(mdp:publication/dcterms:issued) and mdp:publication/pub:published-online)&#xA;&#x9;&#x9;&#x9;&#x9;then &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;(&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;if (not($doi='')) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;then concat('doi:', $doi)&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;else concat('Retrieved from ', $uri)&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;)&#xA;&#x9;&#x9;&#x9;&#x9;else ''&#x9;&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;"/>
                </xsl:variable>
                <!--### End of Variables ###-->
	<!--### Predefined Layout Elements ###-->
	<xsl:variable name="editors"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                    <!--valid-if--><xsl:variable name="var">
                        <xsl:if test="$editorsCount&gt;0">
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="editors-Equal1"><!--### Repeatable Layout Element ###-->
	<xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$editorsCount=1">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <xsl:for-each select="mdp:publication/pub:creator[@role='editor']">
                                                                        <le position-delimiter=", ">
                                                                            <xsl:call-template name="applyDelimiter">
                                                                                <xsl:with-param name="les">
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:organization/e:organization-name/text()"/>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:person/e:family-name/text()"/>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(e:person/e:given-name/text())"/>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                </xsl:with-param>
                                                                                <xsl:with-param name="delimiter" select="', '"/>
                                                                            </xsl:call-template>
                                                                        </le>
                                                                    </xsl:for-each>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$editors-Equal1"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="editors-MoreThan1LessOrEqual6"><!--### Repeatable Layout Element ###-->
	<xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$editorsCount&gt;1 and $editorsCount&lt;=6">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <xsl:for-each select="mdp:publication/pub:creator[@role='editor']">
                                                                        <xsl:choose>
                                                                            <xsl:when test="position()=last()">
                                                                                <le position-delimiter=", &amp; ">
                                                                                    <xsl:call-template name="applyDelimiter">
                                                                                        <xsl:with-param name="les">
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:organization/e:organization-name/text()"/>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </xsl:variable>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </le>
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:person/e:family-name/text()"/>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </xsl:variable>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </le>
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(e:person/e:given-name/text())"/>
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
	<!--### @ref is available ###--><xsl:variable name="var" select="e:organization/e:organization-name/text()"/>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </xsl:variable>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </le>
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:person/e:family-name/text()"/>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </xsl:variable>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </le>
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(e:person/e:given-name/text())"/>
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
                                                                <xsl:with-param name="delimiter" select="', &amp; '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$editors-MoreThan1LessOrEqual6"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="editors-MoreThan6"><!--### Repeatable Layout Element ###-->
	<xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$editorsCount&gt;6">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <xsl:for-each select="mdp:publication/pub:creator[@role='editor']">
                                                                        <xsl:if test="position()&lt;=6">
                                                                            <le position-delimiter=", ">
                                                                                <xsl:call-template name="applyDelimiter">
                                                                                    <xsl:with-param name="les">
                                                                                        <le>
                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:organization/e:organization-name/text()"/>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </le>
                                                                                        <le>
                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:person/e:family-name/text()"/>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </le>
                                                                                        <le>
                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(e:person/e:given-name/text())"/>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </le>
                                                                                    </xsl:with-param>
                                                                                    <xsl:with-param name="delimiter" select="', '"/>
                                                                                </xsl:call-template>
                                                                            </le>
                                                                        </xsl:if>
                                                                    </xsl:for-each>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="not($var='')">
                                                                <xsl:text>, et al.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$editors-MoreThan6"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$ed-postfix"/>
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
                <xsl:variable name="editors-with-dot"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$editors"/>
                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                        <xsl:copy-of select="$var"/>
                        <xsl:if test="not($var='')">
                            <xsl:text>. </xsl:text>
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
                                                                <xsl:variable name="authors-Equal1"><!--### Repeatable Layout Element ###-->
	<xsl:variable name="var" select="''"/>
                                                                    <!--valid-if--><xsl:variable name="var">
                                                                        <xsl:if test="$authorsCount=1">
                                                                            <xsl:variable name="var">
                                                                                <xsl:call-template name="applyDelimiter">
                                                                                    <xsl:with-param name="les">
                                                                                        <xsl:for-each select="mdp:publication/pub:creator[@role='author']">
                                                                                            <le position-delimiter=", ">
                                                                                                <xsl:call-template name="applyDelimiter">
                                                                                                    <xsl:with-param name="les">
                                                                                                        <le>
                                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:organization/e:organization-name/text()"/>
                                                                                                                <xsl:copy-of select="$var"/>
                                                                                                            </xsl:variable>
                                                                                                            <xsl:copy-of select="$var"/>
                                                                                                        </le>
                                                                                                        <le>
                                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:person/e:family-name/text()"/>
                                                                                                                <xsl:copy-of select="$var"/>
                                                                                                            </xsl:variable>
                                                                                                            <xsl:copy-of select="$var"/>
                                                                                                        </le>
                                                                                                        <le>
                                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(e:person/e:given-name/text())"/>
                                                                                                                <xsl:copy-of select="$var"/>
                                                                                                            </xsl:variable>
                                                                                                            <xsl:copy-of select="$var"/>
                                                                                                        </le>
                                                                                                    </xsl:with-param>
                                                                                                    <xsl:with-param name="delimiter" select="', '"/>
                                                                                                </xsl:call-template>
                                                                                            </le>
                                                                                        </xsl:for-each>
                                                                                    </xsl:with-param>
                                                                                    <xsl:with-param name="delimiter" select="', '"/>
                                                                                </xsl:call-template>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:if>
                                                                    </xsl:variable>
                                                                    <xsl:copy-of select="$var"/>
                                                                </xsl:variable>
                                                                <xsl:copy-of select="$authors-Equal1"/>
                                                            </le>
                                                            <le>
                                                                <xsl:variable name="authors-MoreThan1LessOrEqual6"><!--### Repeatable Layout Element ###-->
	<xsl:variable name="var" select="''"/>
                                                                    <!--valid-if--><xsl:variable name="var">
                                                                        <xsl:if test="$authorsCount&gt;1 and $authorsCount&lt;=6">
                                                                            <xsl:variable name="var">
                                                                                <xsl:call-template name="applyDelimiter">
                                                                                    <xsl:with-param name="les">
                                                                                        <xsl:for-each select="mdp:publication/pub:creator[@role='author']">
                                                                                            <xsl:choose>
                                                                                                <xsl:when test="position()=last()">
                                                                                                    <le position-delimiter=", &amp; ">
                                                                                                        <xsl:call-template name="applyDelimiter">
                                                                                                            <xsl:with-param name="les">
                                                                                                                <le>
                                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:organization/e:organization-name/text()"/>
                                                                                                                        <xsl:copy-of select="$var"/>
                                                                                                                    </xsl:variable>
                                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                                </le>
                                                                                                                <le>
                                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:person/e:family-name/text()"/>
                                                                                                                        <xsl:copy-of select="$var"/>
                                                                                                                    </xsl:variable>
                                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                                </le>
                                                                                                                <le>
                                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(e:person/e:given-name/text())"/>
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
	<!--### @ref is available ###--><xsl:variable name="var" select="e:organization/e:organization-name/text()"/>
                                                                                                                        <xsl:copy-of select="$var"/>
                                                                                                                    </xsl:variable>
                                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                                </le>
                                                                                                                <le>
                                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:person/e:family-name/text()"/>
                                                                                                                        <xsl:copy-of select="$var"/>
                                                                                                                    </xsl:variable>
                                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                                </le>
                                                                                                                <le>
                                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(e:person/e:given-name/text())"/>
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
                                                                                    <xsl:with-param name="delimiter" select="', &amp; '"/>
                                                                                </xsl:call-template>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:if>
                                                                    </xsl:variable>
                                                                    <xsl:copy-of select="$var"/>
                                                                </xsl:variable>
                                                                <xsl:copy-of select="$authors-MoreThan1LessOrEqual6"/>
                                                            </le>
                                                            <le>
                                                                <xsl:variable name="authors-MoreThan6"><!--### Repeatable Layout Element ###-->
	<xsl:variable name="var" select="''"/>
                                                                    <!--valid-if--><xsl:variable name="var">
                                                                        <xsl:if test="$authorsCount&gt;6">
                                                                            <xsl:variable name="var">
                                                                                <xsl:call-template name="applyDelimiter">
                                                                                    <xsl:with-param name="les">
                                                                                        <xsl:for-each select="mdp:publication/pub:creator[@role='author']">
                                                                                            <xsl:if test="position()&lt;=6">
                                                                                                <le position-delimiter=", ">
                                                                                                    <xsl:call-template name="applyDelimiter">
                                                                                                        <xsl:with-param name="les">
                                                                                                            <le>
                                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:organization/e:organization-name/text()"/>
                                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                                </xsl:variable>
                                                                                                                <xsl:copy-of select="$var"/>
                                                                                                            </le>
                                                                                                            <le>
                                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:person/e:family-name/text()"/>
                                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                                </xsl:variable>
                                                                                                                <xsl:copy-of select="$var"/>
                                                                                                            </le>
                                                                                                            <le>
                                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(e:person/e:given-name/text())"/>
                                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                                </xsl:variable>
                                                                                                                <xsl:copy-of select="$var"/>
                                                                                                            </le>
                                                                                                        </xsl:with-param>
                                                                                                        <xsl:with-param name="delimiter" select="', '"/>
                                                                                                    </xsl:call-template>
                                                                                                </le>
                                                                                            </xsl:if>
                                                                                        </xsl:for-each>
                                                                                    </xsl:with-param>
                                                                                    <xsl:with-param name="delimiter" select="', '"/>
                                                                                </xsl:call-template>
                                                                            </xsl:variable>
                                                                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                                <xsl:copy-of select="$var"/>
                                                                                <xsl:if test="not($var='')">
                                                                                    <xsl:text>, et al.</xsl:text>
                                                                                </xsl:if>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </xsl:if>
                                                                    </xsl:variable>
                                                                    <xsl:copy-of select="$var"/>
                                                                </xsl:variable>
                                                                <xsl:copy-of select="$authors-MoreThan6"/>
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
                                            <xsl:variable name="source-editors-Equal1"><!--### Repeatable Layout Element ###-->
	<xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$sourceEditorsCount=1">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <xsl:for-each select="mdp:publication/pub:source[1]/pub:creator[@role='editor']">
                                                                        <le position-delimiter="">
                                                                            <xsl:call-template name="applyDelimiter">
                                                                                <xsl:with-param name="les">
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:organization/e:organization-name/text()"/>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(e:person/e:given-name/text())"/>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:person/e:family-name/text()"/>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                </xsl:with-param>
                                                                                <xsl:with-param name="delimiter" select="''"/>
                                                                            </xsl:call-template>
                                                                        </le>
                                                                    </xsl:for-each>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="''"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$source-editors-Equal1"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="source-editors-MoreThan1LessOrEqual6"><!--### Repeatable Layout Element ###-->
	<xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$sourceEditorsCount&gt;1 and $sourceEditorsCount&lt;=6">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <xsl:for-each select="mdp:publication/pub:source[1]/pub:creator[@role='editor']">
                                                                        <xsl:choose>
                                                                            <xsl:when test="position()=last()">
                                                                                <le position-delimiter=", &amp; ">
                                                                                    <xsl:call-template name="applyDelimiter">
                                                                                        <xsl:with-param name="les">
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:organization/e:organization-name/text()"/>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </xsl:variable>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </le>
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(e:person/e:given-name/text())"/>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </xsl:variable>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </le>
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:person/e:family-name/text()"/>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </xsl:variable>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </le>
                                                                                        </xsl:with-param>
                                                                                        <xsl:with-param name="delimiter" select="''"/>
                                                                                    </xsl:call-template>
                                                                                </le>
                                                                            </xsl:when>
                                                                            <xsl:otherwise>
                                                                                <le position-delimiter=", ">
                                                                                    <xsl:call-template name="applyDelimiter">
                                                                                        <xsl:with-param name="les">
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:organization/e:organization-name/text()"/>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </xsl:variable>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </le>
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(e:person/e:given-name/text())"/>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </xsl:variable>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </le>
                                                                                            <le>
                                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:person/e:family-name/text()"/>
                                                                                                    <xsl:copy-of select="$var"/>
                                                                                                </xsl:variable>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </le>
                                                                                        </xsl:with-param>
                                                                                        <xsl:with-param name="delimiter" select="''"/>
                                                                                    </xsl:call-template>
                                                                                </le>
                                                                            </xsl:otherwise>
                                                                        </xsl:choose>
                                                                    </xsl:for-each>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', &amp; '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$source-editors-MoreThan1LessOrEqual6"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="source-editors-MoreThan6"><!--### Repeatable Layout Element ###-->
	<xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$sourceEditorsCount&gt;6">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <xsl:for-each select="mdp:publication/pub:source[1]/pub:creator[@role='editor']">
                                                                        <xsl:if test="position()&lt;=6">
                                                                            <le position-delimiter=", ">
                                                                                <xsl:call-template name="applyDelimiter">
                                                                                    <xsl:with-param name="les">
                                                                                        <le>
                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:organization/e:organization-name/text()"/>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </le>
                                                                                        <le>
                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_initials(e:person/e:given-name/text())"/>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </le>
                                                                                        <le>
                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="e:person/e:family-name/text()"/>
                                                                                                <xsl:copy-of select="$var"/>
                                                                                            </xsl:variable>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </le>
                                                                                    </xsl:with-param>
                                                                                    <xsl:with-param name="delimiter" select="''"/>
                                                                                </xsl:call-template>
                                                                            </le>
                                                                        </xsl:if>
                                                                    </xsl:for-each>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:copy-of select="$var"/>
                                                            <xsl:if test="not($var='')">
                                                                <xsl:text>, et al.</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <xsl:copy-of select="$source-editors-MoreThan6"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-ed-postfix"/>
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
                <xsl:variable name="year"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$date"/>
                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                        <xsl:if test="not($var='')">
                            <xsl:text> (</xsl:text>
                        </xsl:if>
                        <xsl:copy-of select="$var"/>
                        <xsl:if test="not($var='')">
                            <xsl:text>). </xsl:text>
                        </xsl:if>
                    </xsl:variable>
                    <!--font-style--><xsl:variable name="var">
                        <xsl:if test="not($var='')">
                            <span class="DISPLAY_DATE_STATUS">
                                <xsl:copy-of select="$var"/>
                            </span>
                        </xsl:if>
                    </xsl:variable>
                    <xsl:copy-of select="$var"/>
                </xsl:variable>
                <xsl:variable name="year-for-thesis"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$date-for-thesis"/>
                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                        <xsl:if test="not($var='')">
                            <xsl:text> (</xsl:text>
                        </xsl:if>
                        <xsl:copy-of select="$var"/>
                        <xsl:if test="not($var='')">
                            <xsl:text>). </xsl:text>
                        </xsl:if>
                    </xsl:variable>
                    <!--font-style--><xsl:variable name="var">
                        <xsl:if test="not($var='')">
                            <span class="DISPLAY_DATE_STATUS">
                                <xsl:copy-of select="$var"/>
                            </span>
                        </xsl:if>
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
                        <xsl:if test="not($var='')">
                            <xsl:text> (</xsl:text>
                        </xsl:if>
                        <xsl:copy-of select="$var"/>
                        <xsl:if test="not($var='')">
                            <xsl:text>). </xsl:text>
                        </xsl:if>
                    </xsl:variable>
                    <!--font-style--><xsl:variable name="var">
                        <xsl:if test="not($var='')">
                            <span class="DISPLAY_DATE_STATUS">
                                <xsl:copy-of select="$var"/>
                            </span>
                        </xsl:if>
                    </xsl:variable>
                    <xsl:copy-of select="$var"/>
                </xsl:variable>
                <xsl:variable name="published-online-and-external-locator"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                    <xsl:variable name="var">
                        <xsl:call-template name="applyDelimiter">
                            <xsl:with-param name="les">
                                <le>
                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$published-online-and-external-locator"/>
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
                        <xsl:if test="not($var='')">
                            <xsl:text>. </xsl:text>
                        </xsl:if>
                        <xsl:copy-of select="$var"/>
                    </xsl:variable>
                    <xsl:copy-of select="$var"/>
                </xsl:variable>
                <xsl:variable name="year-authors-or-editors-are-presented"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year"/>
                    <!--valid-if--><xsl:variable name="var">
                        <xsl:if test="$authorsCount&gt;0 or $editorsCount&gt;0"><!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:copy-of select="$var"/>
                                <xsl:if test="not($var='')">
                                    <xsl:text>. </xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:if>
                    </xsl:variable>
                    <xsl:copy-of select="$var"/>
                </xsl:variable>
                <xsl:variable name="year-authors-or-editors-are-not-presented"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year"/>
                    <!--valid-if--><xsl:variable name="var">
                        <xsl:if test="$authorsCount=0 and $editorsCount=0"><!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:copy-of select="$var"/>
                                <xsl:if test="not($var='')">
                                    <xsl:text>. </xsl:text>
                                </xsl:if>
                            </xsl:variable>
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
                                            <xsl:if test="mdp:publication/dcterms:issued">
                                                <xsl:variable name="var">
                                                    <xsl:call-template name="applyDelimiter">
                                                        <xsl:with-param name="les">
                                                            <le>
                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_year(mdp:publication/dcterms:issued/text())"/>
                                                                    <xsl:copy-of select="$var"/>
                                                                </xsl:variable>
                                                                <xsl:copy-of select="$var"/>
                                                            </le>
                                                            <le>
                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:get_month_name(mdp:publication/dcterms:issued/text())"/>
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
                                            <xsl:if test="not(mdp:publication/dcterms:issued)">
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
                        <xsl:if test="not($var='')">
                            <xsl:text> (</xsl:text>
                        </xsl:if>
                        <xsl:copy-of select="$var"/>
                        <xsl:if test="not($var='')">
                            <xsl:text>). </xsl:text>
                        </xsl:if>
                    </xsl:variable>
                    <!--font-style--><xsl:variable name="var">
                        <xsl:if test="not($var='')">
                            <span class="DISPLAY_DATE_STATUS">
                                <xsl:copy-of select="$var"/>
                            </span>
                        </xsl:if>
                    </xsl:variable>
                    <xsl:copy-of select="$var"/>
                </xsl:variable>
                <xsl:variable name="year-and-month-authors-are-presented"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-and-month-name"/>
                    <!--valid-if--><xsl:variable name="var">
                        <xsl:if test="$authorsCount&gt;0"><!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:copy-of select="$var"/>
                                <xsl:if test="not($var='')">
                                    <xsl:text>. </xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:if>
                    </xsl:variable>
                    <xsl:copy-of select="$var"/>
                </xsl:variable>
                <xsl:variable name="year-and-month-authors-are-not-presented"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-and-month-name"/>
                    <!--valid-if--><xsl:variable name="var">
                        <xsl:if test="$authorsCount=0"><!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:copy-of select="$var"/>
                                <xsl:if test="not($var='')">
                                    <xsl:text>. </xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:if>
                    </xsl:variable>
                    <xsl:copy-of select="$var"/>
                </xsl:variable>
                <xsl:variable name="year-editors-are-presented"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year"/>
                    <!--valid-if--><xsl:variable name="var">
                        <xsl:if test="$editorsCount&gt;0"><!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:copy-of select="$var"/>
                                <xsl:if test="not($var='')">
                                    <xsl:text>. </xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:if>
                    </xsl:variable>
                    <xsl:copy-of select="$var"/>
                </xsl:variable>
                <xsl:variable name="year-editors-are-not-presented"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year"/>
                    <!--valid-if--><xsl:variable name="var">
                        <xsl:if test="$editorsCount=0"><!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:copy-of select="$var"/>
                                <xsl:if test="not($var='')">
                                    <xsl:text>. </xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <xsl:copy-of select="$var"/>
                        </xsl:if>
                    </xsl:variable>
                    <xsl:copy-of select="$var"/>
                </xsl:variable>
                <xsl:variable name="title-with-dot"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/dc:title/text()"/>
                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                        <xsl:if test="not($var='')">
                            <xsl:text> </xsl:text>
                        </xsl:if>
                        <xsl:copy-of select="$var"/>
                        <xsl:if test="not($var='')">
                            <xsl:text>. </xsl:text>
                        </xsl:if>
                    </xsl:variable>
                    <xsl:copy-of select="$var"/>
                </xsl:variable>
                <xsl:variable name="title-italic"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/dc:title/text()"/>
                    <!--font-style--><xsl:variable name="var">
                        <xsl:if test="not($var='')">
                            <span class="ITALIC">
                                <xsl:copy-of select="$var"/>
                            </span>
                        </xsl:if>
                    </xsl:variable>
                    <xsl:copy-of select="$var"/>
                </xsl:variable>
                <xsl:variable name="title-with-dot-italic"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title-italic"/>
                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                        <xsl:if test="not($var='')">
                            <xsl:text> </xsl:text>
                        </xsl:if>
                        <xsl:copy-of select="$var"/>
                        <xsl:if test="not($var='')">
                            <xsl:text>. </xsl:text>
                        </xsl:if>
                    </xsl:variable>
                    <xsl:copy-of select="$var"/>
                </xsl:variable>
                <xsl:variable name="start-page-end-page"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                    <xsl:variable name="var">
                        <xsl:call-template name="applyDelimiter">
                            <xsl:with-param name="les">
                                <le>
                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:source[1]/e:start-page"/>
                                        <xsl:copy-of select="$var"/>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </le>
                                <le>
                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:source[1]/e:end-page"/>
                                        <xsl:copy-of select="$var"/>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </le>
                            </xsl:with-param>
                            <xsl:with-param name="delimiter" select="'-'"/>
                        </xsl:call-template>
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
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:source[1]/e:publishing-info/e:edition"/>
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
                                            <xsl:if test="not($var='')">
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
                        <xsl:if test="not($var='')">
                            <xsl:text>(</xsl:text>
                        </xsl:if>
                        <xsl:copy-of select="$var"/>
                        <xsl:if test="not($var='')">
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
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:source[1]/e:volume"/>
                                        <!--font-style--><xsl:variable name="var">
                                            <xsl:if test="not($var='')">
                                                <span class="ITALIC">
                                                    <xsl:copy-of select="$var"/>
                                                </span>
                                            </xsl:if>
                                        </xsl:variable>
                                        <xsl:copy-of select="$var"/>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </le>
                                <le>
                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:source[1]/e:issue"/>
                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                            <xsl:if test="not($var='')">
                                                <xsl:text>(</xsl:text>
                                            </xsl:if>
                                            <xsl:copy-of select="$var"/>
                                            <xsl:if test="not($var='')">
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
                        <xsl:if test="not($var='')">
                            <xsl:text>, </xsl:text>
                        </xsl:if>
                        <xsl:copy-of select="$var"/>
                    </xsl:variable>
                    <xsl:copy-of select="$var"/>
                </xsl:variable>
                <xsl:variable name="source-title"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:source[1]/dc:title/text()"/>
                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                        <xsl:if test="not($var='')">
                            <xsl:text> </xsl:text>
                        </xsl:if>
                        <xsl:copy-of select="$var"/>
                    </xsl:variable>
                    <!--font-style--><xsl:variable name="var">
                        <xsl:if test="not($var='')">
                            <span class="ITALIC">
                                <xsl:copy-of select="$var"/>
                            </span>
                        </xsl:if>
                    </xsl:variable>
                    <xsl:copy-of select="$var"/>
                </xsl:variable>
                <xsl:variable name="source-place-publisher"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                    <xsl:variable name="var">
                        <xsl:call-template name="applyDelimiter">
                            <xsl:with-param name="les">
                                <le>
                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                      select="mdp:publication/pub:source[1]/e:publishing-info/e:place/text()"/>
                                        <xsl:copy-of select="$var"/>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </le>
                                <le>
                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                      select="mdp:publication/pub:source[1]/e:publishing-info/dc:publisher/text()"/>
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
                        <xsl:if test="not($var='')">
                            <xsl:text> </xsl:text>
                        </xsl:if>
                        <xsl:copy-of select="$var"/>
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
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/e:publishing-info/e:place/text()"/>
                                        <xsl:copy-of select="$var"/>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </le>
                                <le>
                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/e:publishing-info/dc:publisher/text()"/>
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
                        <xsl:if test="not($var='')">
                            <xsl:text> </xsl:text>
                        </xsl:if>
                        <xsl:copy-of select="$var"/>
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
                                            <xsl:if test="not($var='')">
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
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/dc:title/text()"/>
                                        <xsl:copy-of select="$var"/>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </le>
                                <le>
                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/dcterms:abstract[contains(.,'APA')]"/>
                                        <xsl:copy-of select="$var"/>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </le>
                                <le>
                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/dc:subject"/>
                                        <xsl:copy-of select="$var"/>
                                    </xsl:variable>
                                    <xsl:copy-of select="$var"/>
                                </le>
                                <le>
                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/dcterms:issued/text()"/>
                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                            <xsl:if test="not($var='')">
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
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/e:published-online"/>
                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                            <xsl:if test="not($var='')">
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
                                            <xsl:if test="not($var='')">
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
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/dcterms:dateAccepted"/>
                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                            <xsl:if test="not($var='')">
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
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/dcterms:dateSubmitted"/>
                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                            <xsl:if test="not($var='')">
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
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/dcterms:modified"/>
                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                            <xsl:if test="not($var='')">
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
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/dcterms:created"/>
                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                            <xsl:if test="not($var='')">
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
                        <xsl:if test="not($var='')">
                            <xsl:text>debug:</xsl:text>
                        </xsl:if>
                        <xsl:copy-of select="$var"/>
                    </xsl:variable>
                    <!--font-style--><xsl:variable name="var">
                        <xsl:if test="not($var='')">
                            <span class="BLUE">
                                <xsl:copy-of select="$var"/>
                            </span>
                        </xsl:if>
                    </xsl:variable>
                    <xsl:copy-of select="$var"/>
                </xsl:variable>
                <!--### End of Predefined Layout Elements ###-->
	<!--### Citation Style Layout Definitions ###-->
	<xsl:choose>
                    <xsl:when test="not($hasPublication)">
                        <snippet:snippet>
                            <xsl:attribute name="objid" select="$objid"/>
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
                                    <xsl:if test="not($var='')">
                                        <xsl:text>## Citaion style layout is not defined for metadata record: </xsl:text>
                                    </xsl:if>
                                    <xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <xsl:copy-of select="$isNotPublication"/>
                        </snippet:snippet>
                    </xsl:when>
                    <xsl:when test="$notPublishedRule">
                        <snippet:snippet>
                            <xsl:attribute name="objid" select="$objid"/>
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
                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                    <xsl:copy-of select="$var"/>
                                    <xsl:if test="not($var='')">
                                        <xsl:text>.</xsl:text>
                                    </xsl:if>
                                </xsl:variable>
                                <xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <xsl:copy-of select="$submitted-or-in-preparation"/>
                        </snippet:snippet>
                    </xsl:when>
                    <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;$genre = ( 'article', 'paper') &#xA;&#x9;&#x9;&#x9;&#x9;or ( $genre = ( 'other', 'paper', 'conference-report', 'conference-paper') and $source-type = 'journal' )  &#xA;&#x9;&#x9;&#x9;">
                        <snippet:snippet>
                            <xsl:attribute name="objid" select="$objid"/>
                            <xsl:variable name="journal-article-etc"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                <xsl:variable name="var">
                                    <xsl:call-template name="applyDelimiter">
                                        <xsl:with-param name="les">
                                            <le>
                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$debugBlock"/>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </le>
                                            <le>
                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$authors-or-editors-and-year-and-title-with-dot"/>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </le>
                                            <le>
                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-title"/>
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
	<!--### @ref is available ###--><xsl:variable name="var" select="$volume-issue"/>
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
                                                            <xsl:with-param name="delimiter" select="', '"/>
                                                        </xsl:call-template>
                                                    </xsl:variable>
                                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text>, </xsl:text>
                                                        </xsl:if>
                                                        <xsl:copy-of select="$var"/>
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
                                        <xsl:with-param name="delimiter" select="''"/>
                                    </xsl:call-template>
                                </xsl:variable>
                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                    <xsl:copy-of select="$var"/>
                                    <xsl:if test="not($var='')">
                                        <xsl:text>.</xsl:text>
                                    </xsl:if>
                                </xsl:variable>
                                <xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <xsl:copy-of select="$journal-article-etc"/>
                        </snippet:snippet>
                    </xsl:when>
                    <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;$genre = ( 'book', 'proceedings') &#xA;&#x9;&#x9;&#x9;&#x9;or ( $genre = ( 'conference-report', 'conference-paper') and &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;not($source-type = ('journal', 'proceedings', 'book' ))  &#xA;&#x9;&#x9;&#x9;&#x9;)  &#xA;&#x9;&#x9;&#x9;&#x9;or ( $genre = 'other' and not($source-type) )&#xA;&#x9;&#x9;&#x9;">
                        <snippet:snippet>
                            <xsl:attribute name="objid" select="$objid"/>
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
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/e:publishing-info/e:edition"/>
                                                                        <!--valid-if--><xsl:variable name="var">
                                                                            <xsl:if test="$genre = 'book'"><!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                                    <xsl:if test="not($var='')">
                                                                                        <xsl:text> (</xsl:text>
                                                                                    </xsl:if>
                                                                                    <xsl:copy-of select="$var"/>
                                                                                    <xsl:if test="not($var='')">
                                                                                        <xsl:text>)</xsl:text>
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
                                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                        <xsl:copy-of select="$var"/>
                                                        <xsl:if test="not($var='')">
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
                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                    <xsl:copy-of select="$var"/>
                                    <xsl:if test="not($var='')">
                                        <xsl:text>.</xsl:text>
                                    </xsl:if>
                                </xsl:variable>
                                <xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <xsl:copy-of select="$book-etc"/>
                        </snippet:snippet>
                    </xsl:when>
                    <xsl:when test=" $genre = 'thesis' and (mdp:publication/dcterms:issued/text() or mdp:publication/dcterms:dateAccepted) ">
                        <snippet:snippet>
                            <xsl:attribute name="objid" select="$objid"/>
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
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/dc:title/text()"/>
                                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                        <xsl:copy-of select="$var"/>
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text>.</xsl:text>
                                                        </xsl:if>
                                                    </xsl:variable>
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
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/e:publishing-info/dc:publisher/text()"/>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </xsl:variable>
                                                                    <xsl:copy-of select="$var"/>
                                                                </le>
                                                                <le>
                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/e:publishing-info/e:place/text()"/>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </xsl:variable>
                                                                    <xsl:copy-of select="$var"/>
                                                                </le>
                                                            </xsl:with-param>
                                                            <xsl:with-param name="delimiter" select="', '"/>
                                                        </xsl:call-template>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$degree-place-publisher"/>
                                            </le>
                                        </xsl:with-param>
                                        <xsl:with-param name="delimiter" select="' '"/>
                                    </xsl:call-template>
                                </xsl:variable>
                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                    <xsl:copy-of select="$var"/>
                                    <xsl:if test="not($var='')">
                                        <xsl:text>.</xsl:text>
                                    </xsl:if>
                                </xsl:variable>
                                <xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <xsl:copy-of select="$thesis-in-print-or-accepted"/>
                        </snippet:snippet>
                    </xsl:when>
                    <xsl:when test=" $genre = 'thesis' and mdp:publication/pub:published-online ">
                        <snippet:snippet>
                            <xsl:attribute name="objid" select="$objid"/>
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
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/e:publishing-info/dc:publisher/text()"/>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </xsl:variable>
                                                                    <xsl:copy-of select="$var"/>
                                                                </le>
                                                                <le>
                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/e:publishing-info/e:place/text()"/>
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
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text>(</xsl:text>
                                                        </xsl:if>
                                                        <xsl:copy-of select="$var"/>
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text>).</xsl:text>
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
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text> Retrieved from </xsl:text>
                                                        </xsl:if>
                                                        <xsl:copy-of select="$var"/>
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text>. </xsl:text>
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
                            <xsl:copy-of select="$thesis-published-online"/>
                        </snippet:snippet>
                    </xsl:when>
                    <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;$genre = 'book-item'&#xA;&#x9;&#x9;&#x9;&#x9;or ( $genre = 'other' and $source-type = 'book' )  &#xA;&#x9;&#x9;&#x9;&#x9;or ( &#xA;&#x9;&#x9;&#x9;&#x9;&#x9; $genre = ( 'conference-report', 'conference-paper' )   &#xA;&#x9;&#x9;&#x9;&#x9;&#x9; and  $source-type = ( 'book', 'proceedings' )  &#xA;&#x9;&#x9;&#x9;&#x9;)  &#xA;&#x9;&#x9;&#x9;">
                        <snippet:snippet>
                            <xsl:attribute name="objid" select="$objid"/>
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
                                                                            <xsl:if test="not($var='')">
                                                                                <xsl:text>, </xsl:text>
                                                                            </xsl:if>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </xsl:variable>
                                                                    <xsl:copy-of select="$var"/>
                                                                </le>
                                                                <le>
                                                                    <xsl:variable name="book-title"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                                        <xsl:variable name="var">
                                                                            <xsl:call-template name="applyDelimiter">
                                                                                <xsl:with-param name="les">
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:source[1]/dc:title/text()"/>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                </xsl:with-param>
                                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                                            </xsl:call-template>
                                                                        </xsl:variable>
                                                                        <!--font-style--><xsl:variable name="var">
                                                                            <xsl:if test="not($var='')">
                                                                                <span class="ITALIC">
                                                                                    <xsl:copy-of select="$var"/>
                                                                                </span>
                                                                            </xsl:if>
                                                                        </xsl:variable>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </xsl:variable>
                                                                    <xsl:copy-of select="$book-title"/>
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
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text> In </xsl:text>
                                                        </xsl:if>
                                                        <xsl:copy-of select="$var"/>
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text>.</xsl:text>
                                                        </xsl:if>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$var"/>
                                            </le>
                                            <le>
                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-place-publisher"/>
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
                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                    <xsl:copy-of select="$var"/>
                                    <xsl:if test="not($var='')">
                                        <xsl:text>.</xsl:text>
                                    </xsl:if>
                                </xsl:variable>
                                <xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <xsl:copy-of select="$book-item-etc"/>
                        </snippet:snippet>
                    </xsl:when>
                    <xsl:when test="$genre = 'issue'">
                        <snippet:snippet>
                            <xsl:attribute name="objid" select="$objid"/>
                            <xsl:variable name="issue"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                <xsl:variable name="var">
                                    <xsl:call-template name="applyDelimiter">
                                        <xsl:with-param name="les">
                                            <le>
                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$editors-with-dot"/>
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
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                    <xsl:variable name="var">
                                                        <xsl:call-template name="applyDelimiter">
                                                            <xsl:with-param name="les">
                                                                <le>
                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/dc:title/text()"/>
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
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text> </xsl:text>
                                                        </xsl:if>
                                                        <xsl:copy-of select="$var"/>
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text> [Special Issue].</xsl:text>
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
                                        <xsl:with-param name="delimiter" select="''"/>
                                    </xsl:call-template>
                                </xsl:variable>
                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                    <xsl:copy-of select="$var"/>
                                    <xsl:if test="not($var='')">
                                        <xsl:text>.</xsl:text>
                                    </xsl:if>
                                </xsl:variable>
                                <xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <xsl:copy-of select="$issue"/>
                        </snippet:snippet>
                    </xsl:when>
                    <xsl:when test="$genre = 'journal'">
                        <snippet:snippet>
                            <xsl:attribute name="objid" select="$objid"/>
                            <xsl:variable name="journal"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                <xsl:variable name="var">
                                    <xsl:call-template name="applyDelimiter">
                                        <xsl:with-param name="les">
                                            <le>
                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$editors-with-dot"/>
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
                                    <xsl:if test="not($var='')">
                                        <xsl:text>.</xsl:text>
                                    </xsl:if>
                                </xsl:variable>
                                <xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <xsl:copy-of select="$journal"/>
                        </snippet:snippet>
                    </xsl:when>
                    <xsl:when test="$genre = 'manuscript'">
                        <snippet:snippet>
                            <xsl:attribute name="objid" select="$objid"/>
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
                                        <xsl:with-param name="delimiter" select="''"/>
                                    </xsl:call-template>
                                </xsl:variable>
                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                    <xsl:copy-of select="$var"/>
                                    <xsl:if test="not($var='')">
                                        <xsl:text>. Unpublished Manuscript.</xsl:text>
                                    </xsl:if>
                                </xsl:variable>
                                <xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <xsl:copy-of select="$manuscript"/>
                        </snippet:snippet>
                    </xsl:when>
                    <xsl:when test="$genre = 'series'">
                        <snippet:snippet>
                            <xsl:attribute name="objid" select="$objid"/>
                            <xsl:variable name="series"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                <xsl:variable name="var">
                                    <xsl:call-template name="applyDelimiter">
                                        <xsl:with-param name="les">
                                            <le>
                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$editors-with-dot"/>
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
                                                <xsl:variable name="volumes"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                    <xsl:variable name="var">
                                                        <xsl:call-template name="applyDelimiter">
                                                            <xsl:with-param name="les">
                                                                <le>
                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:source[1]/e:volume"/>
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
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text> (Vols. </xsl:text>
                                                        </xsl:if>
                                                        <xsl:copy-of select="$var"/>
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text>).</xsl:text>
                                                        </xsl:if>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$volumes"/>
                                            </le>
                                            <le>
                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$place-publisher"/>
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
                                    <xsl:if test="not($var='')">
                                        <xsl:text>.</xsl:text>
                                    </xsl:if>
                                </xsl:variable>
                                <xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <xsl:copy-of select="$series"/>
                        </snippet:snippet>
                    </xsl:when>
                    <xsl:when test="$genre = 'talk-at-event'">
                        <snippet:snippet>
                            <xsl:attribute name="objid" select="$objid"/>
                            <xsl:variable name="talk-at-event"><!--### Plain Layout Element ###-->
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
                                                <xsl:variable name="event-title"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                    <xsl:variable name="var">
                                                        <xsl:call-template name="applyDelimiter">
                                                            <xsl:with-param name="les">
                                                                <le>
                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:event/dc:title/text()"/>
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
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text> </xsl:text>
                                                        </xsl:if>
                                                        <xsl:copy-of select="$var"/>
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text>.</xsl:text>
                                                        </xsl:if>
                                                    </xsl:variable>
                                                    <!--font-style--><xsl:variable name="var">
                                                        <xsl:if test="not($var='')">
                                                            <span class="ITALIC">
                                                                <xsl:copy-of select="$var"/>
                                                            </span>
                                                        </xsl:if>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$event-title"/>
                                            </le>
                                            <le>
                                                <xsl:variable name="event-place"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                    <xsl:variable name="var">
                                                        <xsl:call-template name="applyDelimiter">
                                                            <xsl:with-param name="les">
                                                                <le>
                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:event/e:place/text()"/>
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
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text> </xsl:text>
                                                        </xsl:if>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$event-place"/>
                                            </le>
                                            <le>
                                                <xsl:variable name="dates-of-event"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                    <xsl:variable name="var">
                                                        <xsl:call-template name="applyDelimiter">
                                                            <xsl:with-param name="les">
                                                                <le>
                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:event/e:start-date"/>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </xsl:variable>
                                                                    <xsl:copy-of select="$var"/>
                                                                </le>
                                                                <le>
                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:event/e:end-date"/>
                                                                        <xsl:copy-of select="$var"/>
                                                                    </xsl:variable>
                                                                    <xsl:copy-of select="$var"/>
                                                                </le>
                                                            </xsl:with-param>
                                                            <xsl:with-param name="delimiter" select="' - '"/>
                                                        </xsl:call-template>
                                                    </xsl:variable>
                                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text>, </xsl:text>
                                                        </xsl:if>
                                                        <xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <xsl:copy-of select="$dates-of-event"/>
                                            </le>
                                        </xsl:with-param>
                                        <xsl:with-param name="delimiter" select="''"/>
                                    </xsl:call-template>
                                </xsl:variable>
                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                    <xsl:copy-of select="$var"/>
                                    <xsl:if test="not($var='')">
                                        <xsl:text>.</xsl:text>
                                    </xsl:if>
                                </xsl:variable>
                                <xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <xsl:copy-of select="$talk-at-event"/>
                        </snippet:snippet>
                    </xsl:when>
                    <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;$genre = 'courseware-lecture'&#xA;&#x9;&#x9;&#x9;&#x9;">
                        <snippet:snippet>
                            <xsl:attribute name="objid" select="$objid"/>
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
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:event/dc:title/text()"/>
                                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text> </xsl:text>
                                                        </xsl:if>
                                                        <xsl:copy-of select="$var"/>
                                                        <xsl:if test="not($var='')">
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
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:event/e:place/text()"/>
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
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:event/e:start-date"/>
                                                                                            <xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:event/e:end-date"/>
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
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text> </xsl:text>
                                                        </xsl:if>
                                                        <xsl:copy-of select="$var"/>
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
                                    <xsl:if test="not($var='')">
                                        <xsl:text>.</xsl:text>
                                    </xsl:if>
                                </xsl:variable>
                                <xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <xsl:copy-of select="$courseware-lecture"/>
                        </snippet:snippet>
                    </xsl:when>
                    <xsl:when test="$genre = 'poster'">
                        <snippet:snippet>
                            <xsl:attribute name="objid" select="$objid"/>
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
                                                        <xsl:if test="mdp:publication/pub:event/dc:title/text()">
                                                            <xsl:variable name="var">
                                                                <xsl:call-template name="applyDelimiter">
                                                                    <xsl:with-param name="les">
                                                                        <le>
                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:event/dc:title/text()"/>
                                                                                <xsl:copy-of select="$var"/>
                                                                            </xsl:variable>
                                                                            <xsl:copy-of select="$var"/>
                                                                        </le>
                                                                        <le>
                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="mdp:publication/pub:event/e:place/text()"/>
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
                                                                <xsl:if test="not($var='')">
                                                                    <xsl:text> Poster presented at the </xsl:text>
                                                                </xsl:if>
                                                                <xsl:copy-of select="$var"/>
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
                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                    <xsl:copy-of select="$var"/>
                                    <xsl:if test="not($var='')">
                                        <xsl:text>.</xsl:text>
                                    </xsl:if>
                                </xsl:variable>
                                <xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <xsl:copy-of select="$poster"/>
                        </snippet:snippet>
                    </xsl:when>
                    <xsl:when test="$genre = 'report'">
                        <snippet:snippet>
                            <xsl:attribute name="objid" select="$objid"/>
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
	<!--### @ref is available ###--><xsl:variable name="var" select="(mdp:publication/e:identifier[@type='eidt:OTHER'])[1]"/>
                                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                            <xsl:if test="not($var='')">
                                                                                <xsl:text>(</xsl:text>
                                                                            </xsl:if>
                                                                            <xsl:copy-of select="$var"/>
                                                                            <xsl:if test="not($var='')">
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
                                                        <xsl:if test="not($var='')">
                                                            <xsl:text> </xsl:text>
                                                        </xsl:if>
                                                        <xsl:copy-of select="$var"/>
                                                        <xsl:if test="not($var='')">
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
                                    <xsl:if test="not($var='')">
                                        <xsl:text>.</xsl:text>
                                    </xsl:if>
                                </xsl:variable>
                                <xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <xsl:copy-of select="$report"/>
                        </snippet:snippet>
                    </xsl:when>
                </xsl:choose>
                <!--### End of Citation Style Layout Definitions ###-->
	</xsl:for-each>
        </snippet:snippets>
    </xsl:template>
    <!--### Runtime Templates ###-->
	<xsl:template name="applyDelimiter">
		      <xsl:param name="les"/>
		      <xsl:param name="delimiter"/>
		      <xsl:for-each select="$les/le/child::node()">
			         <xsl:if test="not(.='')">
				            <xsl:value-of select="if (position()&gt;1) then @position-delimiter else ''"/>
				            <xsl:copy-of select="." copy-namespaces="no"/>
				            <xsl:if test="position()!=last() and not(@position-delimiter)">
					               <xsl:value-of select="$delimiter"/>
				            </xsl:if>
			         </xsl:if>
		      </xsl:for-each>
	   </xsl:template>
    <xsl:function name="func:get_year">
		      <xsl:param name="date"/>
		      <xsl:value-of select="substring($date,1,4)"/>
	   </xsl:function>
    <xsl:function name="func:get_month">
		      <xsl:param name="date"/>
		      <xsl:value-of select="substring($date,6,2)"/>
	   </xsl:function>
    <xsl:function name="func:get_month_name">
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
    <xsl:function name="func:get_initials">
		      <xsl:param name="str"/>
		      <xsl:variable name="delim" select="if (contains ($str, '-')) then '-' else ' '"/>
		      <xsl:for-each select="tokenize(normalize-space ($str), '\s+|\.\s+|\-\s*')">
			         <xsl:value-of select="concat(substring (., 1, 1), if (position()!=last())then concat ('.', $delim) else '.')"/>
		      </xsl:for-each>
	   </xsl:function>
</xsl:stylesheet>