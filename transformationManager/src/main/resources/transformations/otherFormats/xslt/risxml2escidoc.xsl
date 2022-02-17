<?xml version="1.0" encoding="UTF-8"?>
<!--  CDDL HEADER START  The contents of this file are subject to the terms of the  Common Development and Distribution License, Version 1.0 only  (the "License"). You may not use this file except in compliance  with the License.  You can obtain a copy of the license at license/ESCIDOC.LICENSE  or http://www.escidoc.org/license.  See the License for the specific language governing permissions  and limitations under the License.  When distributing Covered Code, include this CDDL HEADER in each  file and include the License file at license/ESCIDOC.LICENSE.  If applicable, add the following below this CDDL HEADER, with the  fields enclosed by brackets "[]" replaced with your own identifying  information: Portions Copyright [yyyy] [name of copyright owner]  CDDL HEADER END  Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft  für wissenschaftlich-technische Information mbH and Max-Planck-  Gesellschaft zur Förderung der Wissenschaft e.V.  All rights reserved. Use is subject to license terms. -->
<!--   Transformations from RIS Item to eSciDoc PubItem   Author: Julia Kurt (initial creation)   $Author$ (last changed)  $Revision$   $LastChangedDate$ -->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:dc="${xsd.metadata.dc}"
	xmlns:dcterms="${xsd.metadata.dcterms}"
	xmlns:mdr="${xsd.soap.common.mdrecords}"
	xmlns:ei="${xsd.soap.item.item}"
	xmlns:file="${xsd.metadata.file}"
	xmlns:pub="${xsd.metadata.publication}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:eprints="http://purl.org/eprint/terms/"
	xmlns:escidoc="http://purl.org/escidoc/metadata/terms/0.1/"
	xmlns:eterms="${xsd.metadata.terms}"
	xmlns:srel="${xsd.soap.common.srel}"
	xmlns:prop="${xsd.core.properties}"
	xmlns:ec="${xsd.soap.item.components}"
	xmlns:AuthorDecoder="java:de.mpg.mpdl.inge.transformation.util.creators.AuthorDecoder"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:escidocFunction="urn:escidoc:functions"
	xmlns:Util="java:de.mpg.mpdl.inge.transformation.Util"
	xmlns:itemlist="${xsd.soap.item.itemlist}">
	<xsl:import href="../../vocabulary-mappings.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	<xsl:param name="user" select="'dummy:user'"/>
	<xsl:param name="context" select="'dummy:context'"/>
	<xsl:param name="content-model" select="'dummy-content-model'" />
	<xsl:param name="is-item-list" select="true()"/>
	<xsl:param name="external-ou"/>
	<xsl:param name="root-ou" select="'dummy-root-ou'"/>
	<xsl:param name="external-organization" select="'dummy-external-ou'"/>
	<!-- Configuration parameters -->
	<xsl:param name="CoNE" select="'true'"/>
	<xsl:param name="import-name" select="'MPDL'"/>
	<!--  DC XML Header  -->
	<!-- VARIABLEN -->
	<xsl:variable name="organizational-units">
		<organizational-units>
			<ou name="root" id="{$root-ou}"/>
			<ou name="external" id="{$external-organization}"/>
		</organizational-units>
	</xsl:variable>
	<xsl:variable name="sourceGenre">
		<genre item="CHAP" source="book"/>
		<genre item="JOUR" source="journal"/>
		<genre item="MGZN" source="series"/>
		<genre item="NEWS" source="article"/>
	</xsl:variable>
	<xsl:variable name="genre"/>
	<!-- FUNCTIONS -->
	<xsl:function name="escidocFunction:ou-name">
		<xsl:param name="name"/>
		<xsl:choose>
			<xsl:when test="$name = 'root'">
				<!-- TODO: Externalize MPS name -->
				<xsl:value-of select="'Max Planck Society'"/>
			</xsl:when>
			<xsl:when test="$organizational-units//ou[@name = $name or @alias = $name]">
				<xsl:value-of select="$organizational-units//ou[@name = $name or @alias = $name]/@name"/>
				<xsl:if test="$organizational-units//ou[@name = $name or @alias = $name]/../@name != $name">
					<xsl:text>, </xsl:text>
					<xsl:value-of select="escidocFunction:ou-name($organizational-units//ou[@name = $name or @alias = $name]/../@name)"/>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'External Organizations'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="escidocFunction:ou-id">
		<xsl:param name="name"/>
		<xsl:choose>
			<xsl:when test="$organizational-units//ou[@name = $name or @alias = $name]">
				<xsl:value-of select="$organizational-units//ou[@name = $name or @alias = $name]/@id"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$organizational-units//ou[@name = 'root']/@id"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="escidocFunction:smaller" as="xs:boolean">
		<xsl:param name="value1"/>
		<xsl:param name="value2"/>
		<xsl:choose>
			<xsl:when test="not(exists($value1)) or $value1 = ''">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:when test="not(exists($value2)) or $value2 = ''">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="date1" select="substring(concat($value1, '-01-01'), 1, 10)"/>
				<xsl:variable name="date2" select="substring(concat($value2, '-ZZ-ZZ'), 1, 10)"/>
				<xsl:value-of select="compare($date1, $date2) != 1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$is-item-list">
				<itemlist:item-list>
					<xsl:apply-templates select="//item"/>
				</itemlist:item-list>
			</xsl:when>
			<xsl:when test="count(//item) = 1">
				<xsl:apply-templates select="//item"/>
			</xsl:when>
			<xsl:when test="count(//item) = 0">
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:NoSourceForSingleTarget' ), 'Single item was selected as target, but the source contained no items')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:MultipleSourceForSingleTarget' ), 'Single item was selected as target, but the source contained multiple items')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="item">
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<srel:context objid="{$context}" />
				<srel:content-model objid="{$content-model}" />
				<xsl:element name="prop:content-model-specific"></xsl:element>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:call-template name="itemMetadata"/>
				</mdr:md-record>
			</xsl:element>
			<xsl:element name="ec:components"></xsl:element>
		</xsl:element>
	</xsl:template>
	<!-- GENRE -->
	<xsl:template name="itemMetadata">
		<xsl:choose>
			<xsl:when test="TY='BOOK'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen">book</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="TY='CHAP'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen">book-item</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="TY='CONF'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen">proceedings</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="TY='JFULL'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen">journal</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="TY='JOUR'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen">article</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="TY='MGZN'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen">article</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="TY='NEWS'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen">article</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="TY='RPRT'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen">report</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="TY='SER'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen">series</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="TY='THES'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen">thesis</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen">other</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- Create eSciDoc Entry -->
	<xsl:template name="createEntry">
		<xsl:param name="gen"/>
		<xsl:element name="pub:publication">
			<xsl:attribute name="type">
				<xsl:value-of select="$genre-ves/enum[.=$gen]/@uri"/>
			</xsl:attribute>
			<!-- CREATOR -->
			<xsl:choose>
				<xsl:when test="A1">
					<xsl:apply-templates select="A1"/>
				</xsl:when>
				<xsl:when test="AU">
					<xsl:apply-templates select="AU"/>
				</xsl:when>
			</xsl:choose>
			<!-- EDITOR -->
			<xsl:choose>
				<xsl:when test="A2">
					<xsl:apply-templates select="A2"/>
				</xsl:when>
				<xsl:when test="ED">
					<xsl:apply-templates select="ED"/>
				</xsl:when>
			</xsl:choose>
			<!-- TITLE -->
			<xsl:element name="dc:title">
				<xsl:choose>
					<xsl:when test="T1 and not(TY='JFULL')">
						<xsl:value-of select="T1"/>
					</xsl:when>
					<xsl:when test="JF">
						<xsl:value-of select="JF"/>
					</xsl:when>
					<xsl:when test="TI">
						<xsl:value-of select="TI"/>
					</xsl:when>
					<xsl:when test="CT">
						<xsl:value-of select="CT"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:if test="BT">
							<xsl:value-of select="BT"/>
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
			<!--ALTTITLE -->
			<xsl:if test="not($gen='book-item')">
				<xsl:apply-templates select="T2"/>
			</xsl:if>
			<!-- IDENTIFIER -->
			<xsl:apply-templates select="UR"/>
			<xsl:apply-templates select="L1"/>
			<xsl:apply-templates select="L2"/>
			<xsl:apply-templates select="ID"/>
			<xsl:if test="SN and ($gen='journal' or $gen='series' or $gen='book' or $gen='thesis' or $gen='proceedings' or $gen='report')">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">
						<xsl:choose>
							<xsl:when test="$genre='series' or $genre='journal'">eterms:ISSN</xsl:when>
							<xsl:otherwise>eterms:ISBN</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:value-of select="SN"/>
				</xsl:element>
			</xsl:if>
			<!-- PUBLISHING-INFO -->
			<xsl:if test="not($gen='article' or $gen='paper' or $gen='issue' or $gen='other' or $gen='conference-paper' or $gen='book-item') and (PB or CY)">
				<xsl:element name="eterms:publishing-info">
					<xsl:if test="PB">
						<xsl:element name="dc:publisher">
							<xsl:value-of select="PB"/>
						</xsl:element>
					</xsl:if>
					<xsl:if test="CY">
						<xsl:element name="eterms:place">
							<xsl:value-of select="CY"/>
						</xsl:element>
					</xsl:if>
					<xsl:if test="VL and not(ET or JF or JO)">
						<xsl:element name="eterms:edition">
							<xsl:value-of select="VL"/>
						</xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:if>
			<!-- DATES -->
			<xsl:call-template name="createDate"/>
			<!-- SOURCE -->
			<xsl:choose>
				<xsl:when test="BT">
					<xsl:if test="not($gen='book' or $gen='proceedings' or $gen='thesis' or $gen='journal' or $gen='other')">
						<xsl:if test="JF and not(TY='JFULL')">
							<xsl:call-template name="createSource">
								<xsl:with-param name="genre" select="$gen"/>
								<xsl:with-param name="title" select="JF"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="JO">
							<xsl:call-template name="createSource">
								<xsl:with-param name="genre" select="$gen"/>
								<xsl:with-param name="title" select="JO"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="T2 and ($gen='book-item')">
							<xsl:call-template name="createSource">
								<xsl:with-param name="genre" select="$gen"/>
								<xsl:with-param name="title" select="T2"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:if>
					<xsl:if test="((T1 or TI or CT) and T3)">
						<!-- 2nd source series -->
						<xsl:call-template name="createSource">
							<xsl:with-param name="genre" select="$gen"/>
							<xsl:with-param name="title" select="T3"/>
						</xsl:call-template>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<!-- not BT -->
					<xsl:if test="JO">
						<xsl:call-template name="createSource">
							<xsl:with-param name="genre" select="$gen"/>
							<xsl:with-param name="title" select="JO"/>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="JF">
						<xsl:call-template name="createSource">
							<xsl:with-param name="genre" select="$gen"/>
							<xsl:with-param name="title" select="JF"/>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="T2 and ($gen='article' or $gen='book-item')">
						<xsl:call-template name="createSource">
							<xsl:with-param name="genre" select="$gen"/>
							<xsl:with-param name="title" select="T2"/>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="T3">
						<!-- source series -->
						<xsl:call-template name="createSource">
							<xsl:with-param name="genre" select="$gen"/>
							<xsl:with-param name="title" select="T3"/>
						</xsl:call-template>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
			<!-- ABSTRACT -->
			<xsl:call-template name="createAbstract"/>
			<!-- SUBJECT -->
			<xsl:call-template name="createSubject"/>
			<!--end publication-->
		</xsl:element>
	</xsl:template>
	<!-- CREATOR -->
	<xsl:template name="createPerson">
		<xsl:param name="familyname"/>
		<xsl:param name="givenname"/>
		<xsl:param name="title"/>
		<xsl:param name="publicationDate" />
		<xsl:variable name="coneCreator">
			<xsl:choose>
				<xsl:when test="$CoNE = 'false'">
					<!-- No CoNE -->
				</xsl:when>
				<xsl:when test="$import-name = 'MPDL'">
					<xsl:copy-of select="Util:queryConeExact('persons', concat($familyname, ', ', $givenname), 'MPDL')"/>
					<xsl:copy-of select="Util:queryConeExact('persons', concat($familyname, ', ', $givenname), 'External Organizations')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="Util:queryCone('persons', concat('&quot;',$familyname, ', ', $givenname, '&quot;'))"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="multiplePersonsFound" select="exists($coneCreator/cone/rdf:RDF/rdf:Description[@rdf:about != $coneCreator/cone/rdf:RDF/rdf:Description/@rdf:about])"/>
		<xsl:choose>
			<xsl:when test="$multiplePersonsFound">
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:MultipleCreatorsFound' ), concat('There is more than one CoNE entry matching --', concat($familyname, ', ', givenname), '--'))"/>
			</xsl:when>
			<xsl:when test="not(exists($coneCreator/cone/rdf:RDF/rdf:Description))">
				<xsl:comment>NOT FOUND IN CONE</xsl:comment>
				<person:person>
					<eterms:family-name>
						<xsl:value-of select="familyname"/>
					</eterms:family-name>
					<xsl:choose>
						<xsl:when test="exists(givenname) and not(givenname='')">
							<eterms:given-name>
								<xsl:value-of select="$givenname"/>
							</eterms:given-name>
						</xsl:when>
						<!-- TODO alternative: initials? -->
					</xsl:choose>
					<organization:organization>
						<dc:title>
							<xsl:value-of select="escidocFunction:ou-name('external')"/>
						</dc:title>
						<dc:identifier>
							<xsl:value-of select="escidocFunction:ou-id('external')"/>
						</dc:identifier>
					</organization:organization>
				</person:person>
			</xsl:when>
			<xsl:otherwise>
				<xsl:comment>CONE CREATOR</xsl:comment>
				<person:person>
					<eterms:family-name>
						<xsl:value-of select="$familyname"/>
					</eterms:family-name>
					<eterms:given-name>
						<xsl:value-of select="$givenname"/>
					</eterms:given-name>
					<dc:identifier xsi:type="eterms:CONE">
						<xsl:value-of select="$coneCreator/cone[1]/rdf:RDF[1]/rdf:Description/@rdf:about"/>
					</dc:identifier>
					<!-- CBS OU depend on date (affiliatedInstitution depend on publicationDateFormatted) -->
					<xsl:variable name="publicationDateFormatted">
						<xsl:choose>
							<xsl:when test="exists($publicationDate) and fn:matches($publicationDate, '\d+?/\d+?/\d+?/')">
								<xsl:value-of select="fn:substring-before($publicationDate, '/')"/>-
								<xsl:value-of select="fn:substring-before(fn:substring-after($publicationDate, '/'), '/')"/>-
								<xsl:value-of select="fn:substring-before(fn:substring-after(fn:substring-after($publicationDate, '/'), '/'), '/')"/>
							</xsl:when>
							<xsl:when test="exists($publicationDate) and fn:matches($publicationDate/text(), '\d+?/\d+?//')">
								<xsl:value-of select="fn:substring-before($publicationDate, '/')"/>-
								<xsl:value-of select="fn:substring-before(fn:substring-after($publicationDate, '/'), '/')"/>
							</xsl:when>
							<xsl:when test="exists($publicationDate) and fn:matches($publicationDate, '\d+?///')">
								<xsl:value-of select="fn:substring-before($publicationDate, '/')"/>
							</xsl:when>
						</xsl:choose>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="$coneCreator/cone[1]/rdf:RDF[1]/rdf:Description/escidoc:position[escidocFunction:smaller(rdf:Description/escidoc:start-date, $publicationDateFormatted) and escidocFunction:smaller($publicationDateFormatted, rdf:Description/escidoc:end-date)]">
							<xsl:for-each select="$coneCreator/cone[1]/rdf:RDF[1]/rdf:Description/escidoc:position">
								<xsl:comment>pubdate: 
									<xsl:value-of select="$publicationDateFormatted"/>
								</xsl:comment>
								<xsl:comment>start: 
									<xsl:value-of select="rdf:Description/escidoc:start-date"/>
								</xsl:comment>
								<xsl:comment>start &lt; pubdate 
									<xsl:value-of select="escidocFunction:smaller(rdf:Description/escidoc:start-date, $publicationDateFormatted)"/>
								</xsl:comment>
								<xsl:comment>end: 
									<xsl:value-of select="rdf:Description/escidoc:end-date"/>
								</xsl:comment>
								<xsl:comment>pubdate &lt; end 
									<xsl:value-of select="escidocFunction:smaller($publicationDateFormatted, rdf:Description/escidoc:end-date)"/>
								</xsl:comment>
								<xsl:if test="escidocFunction:smaller(rdf:Description/escidoc:start-date, $publicationDateFormatted) and escidocFunction:smaller($publicationDateFormatted, rdf:Description/escidoc:end-date)">
									<xsl:comment> Case 1 </xsl:comment>
									<organization:organization>
										<dc:title>
											<xsl:value-of select="rdf:Description/eprints:affiliatedInstitution"/>
										</dc:title>
										<dc:identifier>
											<xsl:value-of select="rdf:Description/dc:identifier"/>
										</dc:identifier>
									</organization:organization>
								</xsl:if>
							</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<organization:organization>
								<dc:title>External Organizations</dc:title>
								<dc:identifier>
									<xsl:value-of select="$external-organization"/>
								</dc:identifier>
							</organization:organization>
						</xsl:otherwise>
					</xsl:choose>
				</person:person>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="A1|AU">
		<xsl:variable name="var">
			<xsl:copy-of select="AuthorDecoder:parseAsNode(.)"/>
		</xsl:variable>
		<xsl:variable name="publicationDate">
			<xsl:value-of select="../Y1" />
		</xsl:variable>
		<xsl:for-each select="$var/authors/author">
			<eterms:creator>
				<xsl:attribute name="role" select="$creator-ves/enum[.='author']/@uri"/>
				<xsl:call-template name="createPerson">
					<xsl:with-param name="familyname" select="familyname"/>
					<xsl:with-param name="givenname" select="givenname"/>
					<xsl:with-param name="title" select="title"/>
					<xsl:with-param name="publicationDate" select="$publicationDate"/>
				</xsl:call-template>
			</eterms:creator>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="A2|ED">
		<xsl:variable name="var">
			<xsl:copy-of select="AuthorDecoder:parseAsNode(.)"/>
		</xsl:variable>
		<xsl:for-each select="$var/authors/author">
			<xsl:element name="eterms:creator">
				<xsl:attribute name="role" select="$creator-ves/enum[.='contributor']/@uri"/>
				<xsl:call-template name="createPerson">
					<xsl:with-param name="familyname" select="familyname"/>
					<xsl:with-param name="givenname" select="givenname"/>
					<xsl:with-param name="title" select="title"/>
				</xsl:call-template>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="parseCreators">
		<xsl:param name="string"/>
		<xsl:choose>
			<xsl:when test="substring-before($string,';')=''"></xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="parseCreators">
					<xsl:with-param name="string" select="substring-after($string,';')"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- SOURCE -->
	<xsl:template name="createSource">
		<xsl:param name="genre" />
		<xsl:param name="title" />
		<xsl:element name="source:source">
			<!-- SOURCE GENRE -->
			<xsl:attribute name="type">
				<xsl:choose>
					<xsl:when test="not($genre='book' or $genre='proceedings' or $genre='thesis' or $genre='journal' or $genre='series' or $genre='other') and BT">
						<xsl:value-of select="$genre-ves/enum[.='journal']/@uri"/>
					</xsl:when>
					<xsl:when test="TY='CHAP' and not($title=T3)">
						<xsl:value-of select="$genre-ves/enum[.='book']/@uri"/>
					</xsl:when>
					<xsl:when test="TY='JOUR'">
						<xsl:value-of select="$genre-ves/enum[.='journal']/@uri"/>
					</xsl:when>
					<xsl:when test="TY='MGZN'">
						<xsl:value-of select="$genre-ves/enum[.='series']/@uri"/>
					</xsl:when>
					<xsl:when test="TY='NEWS'">
						<xsl:value-of select="$genre-ves/enum[.='series']/@uri"/>
					</xsl:when>
					<xsl:when test="TY='JFULL'">
						<xsl:value-of select="$genre-ves/enum[.='journal']/@uri"/>
					</xsl:when>
					<xsl:when test="T3 and ($title=T3)">
						<xsl:value-of select="$genre-ves/enum[.='series']/@uri"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:SourceGenreNotRecognized' ), concat('RIS genre -', TY, '- for publication genre -', $genre, '- cannot be mapped to an eSciDoc genre'))"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<!-- SOURCE TITLE -->
			<xsl:element name="dc:title">
				<xsl:value-of select="$title" />
			</xsl:element>
			
			<!-- SOURCE ALTTITLE -->
			<xsl:choose>
				<xsl:when test="JA">
					<xsl:element name="dcterms:alternative">
						<xsl:value-of select="JA" />
					</xsl:element>
				</xsl:when>
				<xsl:when test="J1">
					<xsl:element name="dcterms:alternative">
						<xsl:value-of select="J1" />
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="J2">
						<xsl:element name="dcterms:alternative">
							<xsl:value-of select="J2" />
						</xsl:element>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
			<!-- SOURCE CREATOR -->
			<xsl:if test="not($title=T3)">
				<xsl:if test="A3">
					<xsl:variable name="var">
						<xsl:copy-of select="AuthorDecoder:parseAsNode(A3)" />
					</xsl:variable>
					<xsl:for-each select="$var/authors/author">
						<xsl:element name="eterms:creator">
							<xsl:attribute name="role" select="$creator-ves/enum[.='author']/@uri"/>
							<xsl:call-template name="createPerson">
								<xsl:with-param name="familyname" select="familyname" />
								<xsl:with-param name="givenname" select="givenname" />
								<xsl:with-param name="title" select="title" />
							</xsl:call-template>
						</xsl:element>
					</xsl:for-each>
				</xsl:if>
				<!-- SOURCE VOLUME -->
				<xsl:if test="(VL and (JF or JO or ET))">
					<xsl:element name="eterms:volume">
						<xsl:value-of select="VL" />
					</xsl:element>
				</xsl:if>
				<!-- SOURCE ISSUE -->
				<xsl:choose>
					<xsl:when test="IS">
						<xsl:element name="eterms:issue">
							<xsl:value-of select="IS" />
						</xsl:element>
					</xsl:when>
					<xsl:otherwise>
						<xsl:if test="CP">
							<xsl:element name="eterms:issue">
								<xsl:value-of select="CP" />
							</xsl:element>
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
				<!-- SOURCE PAGES -->
				<xsl:if test="EP">
					<xsl:element name="eterms:start-page">
						<xsl:value-of select="SP" />
					</xsl:element>
					<xsl:element name="eterms:end-page">
						<xsl:value-of select="EP" />
					</xsl:element>
				</xsl:if>
				<!-- SOURCE TOTAL NUMBER OF PAGES -->
				<xsl:if test="not(EP) and SP">
					<xsl:element name="eterms:total-number-of-pages">
						<xsl:value-of select="SP" />
					</xsl:element>
				</xsl:if>
				<!-- SOURCE PUBLISHINGINFO -->
				<xsl:if test="($genre='article' or $genre='paper' or $genre='issue' or $genre='other' or $genre='conference-paper' or $genre='book-item') and (PB or CY)">
					<xsl:element name="eterms:publishing-info">
						<xsl:element name="dc:publisher">
							<xsl:value-of select="PB" />
						</xsl:element>
						<xsl:element name="eterms:place">
							<xsl:value-of select="CY" />
						</xsl:element>
						<xsl:if test="ET and ($genre='book' or $genre='thesis' or $genre='proceedings' or $genre='report')">
							<xsl:element name="eterms:edition">
								<xsl:value-of select="ET" />
							</xsl:element>
						</xsl:if>
					</xsl:element>
				</xsl:if>
				<!-- SOURCE IDENTIFIER -->
				<xsl:if test="SN and not($genre='journal' or $genre='series' or $genre='book' or $genre='thesis' or $genre='proceedings' or $genre='report')">
					<xsl:element name="dc:identifier">
						<xsl:attribute name="xsi:type">
							<xsl:choose>
								<xsl:when test="$genre='series' or $genre='journal'">eterms:ISSN</xsl:when>
								<xsl:otherwise>eterms:ISBN</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:value-of select="SN" />
					</xsl:element>
				</xsl:if>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	<xsl:template match="JF">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="JO">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- DATES -->
	<xsl:template name="createDate">
		<xsl:choose>
			<xsl:when test="Y1">
				<xsl:apply-templates select="Y1"/>
			</xsl:when>
			<xsl:when test="PY">
				<xsl:apply-templates select="PY"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="Y2"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="Y1">
		<xsl:element name="dcterms:issued">
			<xsl:call-template name="parseDate"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="PY">
		<xsl:element name="dcterms:issued">
			<xsl:call-template name="parseDate"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="Y2">
		<xsl:element name="dcterms:issued">
			<xsl:call-template name="parseDate"/>
		</xsl:element>
	</xsl:template>
	<xsl:template name="parseDate">
		<xsl:variable name="year" select="substring-before(.,'/')"/>
		<xsl:variable name="string-md" select="substring-after(.,'/')"/>
		<xsl:variable name="month" select="substring-before($string-md, '/')"/>
		<xsl:variable name="day" select="substring-before(substring-after($string-md, '/'),'/')"/>
		<xsl:variable name="date">
			<xsl:if test="not($year='')">
				<xsl:value-of select="$year"/>
				<xsl:if test="not($month='')">
					<xsl:value-of select="concat('-',$month)"/>
				</xsl:if>
				<xsl:if test="not($day='')">
					<xsl:value-of select="concat('-',$day)"/>
				</xsl:if>
			</xsl:if>
			<xsl:if test="$year='' and not(.='')">
				<xsl:value-of select="."/>
			</xsl:if>
		</xsl:variable>
		<xsl:value-of select="$date"/>
	</xsl:template>
	<!-- ALTTITLE -->
	<xsl:template match="T2">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- ABSTRACT -->
	<xsl:template name="createAbstract">
		<xsl:choose>
			<xsl:when test="N2">
				<xsl:element name="dcterms:abstract">
					<xsl:value-of select="N2"/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="AB"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="AB">
		<xsl:element name="dcterms:abstract">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- SUBJECT -->
	<xsl:template name="createSubject">
		<xsl:if test="KW">
			<xsl:element name="dcterms:subject">
				<xsl:value-of select="KW[position()=1]"/>
				<xsl:for-each select="KW[position()>1]">
					<xsl:text>, </xsl:text>
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<!-- PUBLISHINGINFO -->
	<xsl:template name="createEdition">
		<xsl:element name="eterms:publishing-info">
			<xsl:element name="eterms:edition">
				<xsl:value-of select="ET"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<!-- IDENTIFIER -->
	<xsl:template match="UR">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:URI</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="L1">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:URI</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="L2">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:URI</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="ID">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:OTHER</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>