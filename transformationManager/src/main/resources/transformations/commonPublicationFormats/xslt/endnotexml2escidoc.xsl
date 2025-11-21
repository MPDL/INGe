<?xml version="1.0" encoding="UTF-8"?>
<!-- CDDL HEADER START The contents of this file are subject to the terms 
	of the Common Development and Distribution License, Version 1.0 only (the 
	"License"). You may not use this file except in compliance with the License. 
	You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.org/license. 
	See the License for the specific language governing permissions and limitations 
	under the License. When distributing Covered Code, include this CDDL HEADER 
	in each file and include the License file at license/ESCIDOC.LICENSE. If 
	applicable, add the following below this CDDL HEADER, with the fields enclosed 
	by brackets "[]" replaced with your own identifying information: Portions 
	Copyright [yyyy] [name of copyright owner] CDDL HEADER END Copyright 2006-2012 
	Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische 
	Information mbH and Max-Planck- Gesellschaft zur Förderung der Wissenschaft 
	e.V. All rights reserved. Use is subject to license terms. -->
<!-- Transformations from EndNote Item to eSciDoc PubItem See http://colab.mpdl.mpg.de/mediawiki/PubMan_Func_Spec_Endnote_Mapping 
	and http://colab.mpdl.mpg.de/mediawiki/Talk:PubMan_Func_Spec_Endnote_Mapping#revised_mapping 
	Author: Vlad Makarenko (initial creation) $Author: mfranke $ (last changed) 
	$Revision: 2750 $ $LastChangedDate: 2010-02-05 11:38:47 +0100 (Fri, 05 Feb 
	2010) $ -->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:dc="${xsd.metadata.dc}" xmlns:dcterms="${xsd.metadata.dcterms}"
	xmlns:mdr="${xsd.soap.common.mdrecords}"
	xmlns:ei="${xsd.soap.item.item}" xmlns:srel="${xsd.soap.common.srel}"
	xmlns:prop="${xsd.core.properties}"
	xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/"
	xmlns:escidocComponents="${xsd.soap.item.components}"
	xmlns:file="${xsd.metadata.file}"
	xmlns:pub="${xsd.metadata.publication}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:eterms="${xsd.metadata.terms}"
	xmlns:escidoc="${xsd.metadata.terms}"
 xmlns:AuthorDecoder="https://pubman.mpdl.mpg.de/author-decoder-functions"
	xmlns:Util="https://pubman.mpdl.mpg.de/util-functions"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:esc="http://purl.org/escidoc/metadata/terms/0.1/"
	xmlns:itemlist="${xsd.soap.item.itemlist}"
	xmlns:eprints="http://purl.org/eprint/terms/"
	xmlns:escidocFunctions="urn:escidoc:functions">
	<xsl:import href="../../vocabulary-mappings.xsl" />
	<xsl:output method="xml" encoding="UTF-8" indent="yes" />
	<xsl:param name="user" select="'dummy:user'" />
	<xsl:param name="context" select="'dummy:context'" />
	<xsl:param name="content-model" select="'dummy-content-model'" />
	<xsl:param name="root-ou" />
	<xsl:param name="external-ou" />
	<!-- Configuration parameters -->
	<xsl:param name="Flavor" select="'OTHER'" />
	<xsl:param name="CoNE" select="'false'" />
	<xsl:param name="is-item-list" select="true()" />
	<xsl:param name="source-name" select="''" />
	<xsl:param name="refType" />
	<xsl:param name="Organisation" select="''" />

	<xsl:variable name="vm"
		select="document('../../ves-mapping.xml')/mappings" />
	<xsl:variable name="fulltext-location">
		<xsl:if test="$Flavor = 'MPIMP' or $Flavor = 'MPIMPExt'">
			<xsl:value-of
				select="'https://vm50.mpdl.mpg.de/upload/MPIMP/Pubman/'" />
		</xsl:if>
		<xsl:if test="$Flavor = 'CAESAR'">
			<xsl:value-of
				select="'http://ftp.mpdl.mpg.de/caesar/PDF/'" />
		</xsl:if>
	</xsl:variable>
	<xsl:variable name="genreMap">
		<m key="Book">book</m>
		<m key="Edited Book">book</m>
		<m key="Electronic Book">book</m>
		<m key="Book Section">book-item</m>
		<m key="Conference Paper">conference-paper</m>
		<m key="Conference Proceedings">proceedings</m>
		<m key="Journal Article">article</m>
		<m key="Magazine Article">magazine-article</m>
		<m key="Meeting Abstract">meeting-abstract</m>
		<m key="Newspaper Article">newspaper-article</m>
		<m key="Electronic Article">article</m>
		<m key="Patent">patent</m>
		<m key="Report">report</m>
		<m key="Manuscript">manuscript</m>
		<m key="Talk at Event">talk-at-event</m>
		<m key="Thesis">thesis</m>
		<m key="Generic">other</m>
		<m key="GenericMPIGEM">paper</m>
		<m key="Courseware">courseware-lecture</m>
		<m key="Computer Program">software</m>
		<m key="Dataset">data-publication</m>
		<m key="Blog">blog-post</m>
		<m key="Interview">interview</m>
		<m key="Film or Broadcast">film</m>
	</xsl:variable>
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$is-item-list">
				<itemlist:item-list>
					<xsl:apply-templates select="//item" />
				</itemlist:item-list>
			</xsl:when>
			<xsl:when test="count(//item) = 1">
				<xsl:apply-templates select="//item" />
			</xsl:when>
			<xsl:when test="count(//item) = 0">
				<xsl:value-of
					select="error(QName('http://www.escidoc.de', 'err:NoSourceForSingleTarget' ), 'Single item was selected as target, but the source contained no items')" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of
					select="error(QName('http://www.escidoc.de', 'err:MultipleSourceForSingleTarget' ), 'Single item was selected as target, but the source contained multiple items')" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="//item">
		<ei:item>
			<ei:properties>
				<srel:context objid="{$context}" />
				<srel:content-model objid="{$content-model}" />
				<prop:content-model-specific>
					<local-tags>
						<xsl:choose>
							<xsl:when
								test="($Flavor = 'MPIMP' or $Flavor = 'MPIMPExt')">
								<xsl:if test="NUM_4 = '0'">
									<local-tag>kooperative Publikationen</local-tag>
								</xsl:if>
								<xsl:if test="NUM_4 = '1'">
									<local-tag>MPIMP</local-tag>
								</xsl:if>
								<xsl:if test="NUM_4 = '2'">
									<local-tag>Problemfälle</local-tag>
								</xsl:if>
								<xsl:if test="NUM_4 = '3'">
									<local-tag>Externe Publikationen</local-tag>
								</xsl:if>
								<xsl:if test="NUM_9 = 'Review'">
									<local-tag>Review</local-tag>
								</xsl:if>
							</xsl:when>
						</xsl:choose>
						<xsl:value-of select="MORE" />
					</local-tags>
				</prop:content-model-specific>
			</ei:properties>
			<mdr:md-records>
				<mdr:md-record name="escidoc">
					<xsl:call-template name="itemMetadata" />
				</mdr:md-record>
			</mdr:md-records>
			<xsl:element name="escidocComponents:components">
				<xsl:if
					test="MORE and ($Flavor = 'MPIMP' or $Flavor = 'MPIMPExt' or $Flavor = 'CAESAR')">
					<xsl:variable name="oa" select="EQUAL = '1'" />
					<xsl:for-each select="MORE">
						<xsl:call-template name="component">
							<xsl:with-param name="oa" select="$oa" />
							<xsl:with-param name="internal-managed"
								select="true()" />
						</xsl:call-template>
					</xsl:for-each>
				</xsl:if>
				<xsl:if test="U">
					<xsl:for-each select="U">
						<xsl:call-template name="component">
							<xsl:with-param name="internal-managed"
								select="false()" />
						</xsl:call-template>
					</xsl:for-each>
				</xsl:if>
			</xsl:element>
		</ei:item>
	</xsl:template>
	<!-- GENRE -->
	<xsl:template name="itemMetadata">
		<xsl:variable name="refType"
			select="normalize-space(NUM_0)" />
		<xsl:variable name="curGenre">
			<xsl:choose>
				<xsl:when
					test="($Flavor = 'MPIMP' or $Flavor = 'MPIMPExt') and NUM_9 = 'Meeting Abstract'">
					<xsl:value-of
						select="$genreMap/m[@key='Meeting Abstract']" />
				</xsl:when>
				<xsl:when
					test="($refType = 'Generic' or $refType = 'Conference Paper' or $refType = 'Report') and NUM_9 and (lower-case(normalize-space(NUM_9)) = 'talk')">
					<xsl:value-of
						select="$genreMap/m[@key='Talk at Event']" />
				</xsl:when>
				<!-- Spezial-Genre-Mapping für MPI Gemeinschaftsgüter -->
				<xsl:when
					test="($Flavor = 'MPIGEM' and $refType = 'Generic')">
					<xsl:text>paper</xsl:text>
				</xsl:when>
				<xsl:when
					test="($Flavor = 'MPIGEM' and $refType = 'Artwork')">
					<xsl:text>book-review</xsl:text>
				</xsl:when>
				<xsl:when
					test="($Flavor = 'MPIGEM' and $refType = 'Classical Work')">
					<xsl:text>other</xsl:text>
				</xsl:when>
				<!-- Ende Spezial-Mapping MPIGEM -->
				<xsl:otherwise>
					<xsl:value-of select="$genreMap/m[@key=$refType]" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="curGenreURI"
			select="$genre-ves/enum[.=$curGenre]/@uri" />
		<xsl:choose>
			<xsl:when test="$refType=''">
				<xsl:value-of
					select="error(QName('http://www.escidoc.de', 'err:NoGenreFound' ), 'Endnote import must have a filled &quot;%0&quot; type to describe the publication genre.')" />
			</xsl:when>
			<xsl:when
				test="$curGenre='' and normalize-space(NUM_9) != '' ">
				<xsl:value-of
					select="error(QName('http://www.escidoc.de', 'err:NotAbleToSetGenre' ), concat('Could not set genre. (maybe $refType is wrong or overwritten with &lt;', NUM_9, '&gt;?)'))" />
			</xsl:when>
			<xsl:when test="$curGenre != ''">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="$curGenreURI" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of
					select="error(QName('http://www.escidoc.de', 'err:NotMappedGenre' ), concat('Endnote genre: ', $refType,' is not mapped.'))" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- Create eSciDoc Entry -->
	<xsl:template name="createEntry">
		<xsl:param name="gen" />
		<xsl:variable name="refType"
			select="normalize-space(NUM_0)" />
		<!-- Seit einiger Zeit (?) wird von Endnote selbst der ZS-Titel bei ZS-Artikeln 
			statt in %J in %B ausgegeben!!! deshalb Anpassung (Erndt, 10.7.15) -->
		<!-- Zudem Ergänzung für MPI IPP für Altdatenimport aus Libero. Haben hin 
			und wieder ZS-Angaben auch bei Genre Conference Paper! -->
		<xsl:variable name="sourceGenre"
			select="  if ( ($Flavor = 'CAESAR' and B and $refType = 'Journal Article' ) and not(J)) then $genre-ves/enum[.='journal']/@uri else  if ( ($Flavor = 'MPIGEM' and B and $refType = 'Journal Article' ) and not(J)) then $genre-ves/enum[.='journal']/@uri else  if ( $Flavor = 'IPP' and J and $refType = 'Conference Paper' ) then $genre-ves/enum[.='journal']/@uri else  if ( B and $refType = ('Book', 'Edited Book', 'Manuscript', 'Report') ) then $genre-ves/enum[.='series']/@uri else  if ( B and $refType = 'Book Section' ) then $genre-ves/enum[.='book']/@uri else  if ( B and $refType = ('Electronic Article', 'Magazine Article', 'Journal Article') ) then $genre-ves/enum[.='journal']/@uri else  if ( B and $refType = 'Newspaper Article' ) then $genre-ves/enum[.='newspaper']/@uri else  if ( B and $refType = 'Conference Paper' ) then $genre-ves/enum[.='proceedings']/@uri else  if ( J and $refType = 'Journal Article' ) then $genre-ves/enum[.='journal']/@uri else  if ( S and $refType = ('Book Section', 'Conference Proceedings') ) then $genre-ves/enum[.='series']/@uri else  ''  " />
		<xsl:variable name="secondSourceGenre"
			select="  if ( S and $sourceGenre = $genre-ves/enum[.='book']/@uri) then $genre-ves/enum[.='series']/@uri else   ''  " />
		<xsl:element name="pub:publication">
			<xsl:attribute name="type">
				<xsl:value-of select="$gen" />
			</xsl:attribute>
			<!-- CREATORS -->
			<xsl:call-template name="createCreators">
				<xsl:with-param name="gen" select="$gen" />
			</xsl:call-template>
			<!-- TITLE -->
			<xsl:variable name="vol"
				select="  if ($refType = ('Book', 'Edited Book') and N and V) then concat(' vol. ', V) else ''  " />
			<xsl:element name="dc:title">
				<xsl:value-of select="concat(T, $vol)" />
			</xsl:element>
			<!-- LANGUAGE -->
			<!-- 25: %G English 62: %G Englisch 91: %G Language: eng 120: %G Language: 
				eng 148: %G eng 172: %G Language: eng 202: %G French; Summaries in English. 
				245: %G eng 282: %G Language: eng 308: %G English 329: %G English 377: %G 
				eng 418: %G Language: eng 463: %G eng 499: %G eng 543: %G de 579: %G english 
				607: %G eng 639: %G eng 673: %G eng 709: %G eng 743: %G eng 773: %G Language: 
				eng 798: %G eng 826: %G de -->
			<xsl:if test="G">
				<xsl:for-each select="G">
					<xsl:variable name="g" select="." />
					<xsl:choose>
						<xsl:when test="$vm/language/v1-to-v2/map[$g=.]!=''">
							<dc:language>
								<xsl:attribute name="xsi:type">dcterms:ISO639-3</xsl:attribute>
								<xsl:value-of select="." />
							</dc:language>
						</xsl:when>
						<xsl:when
							test="$vm/language/v2-to-edoc/map=$g and $vm/language/v2-to-edoc/map[$g=.]/@v2 != ''">
							<dc:language>
								<xsl:attribute name="xsi:type">dcterms:ISO639-3</xsl:attribute>
								<xsl:value-of
									select="$vm/language/v2-to-edoc/map[$g=.]/@v2" />
							</dc:language>
						</xsl:when>
					</xsl:choose>
				</xsl:for-each>
			</xsl:if>
			<!--ALTTITLE -->
			<xsl:for-each
				select="  B[$refType = ('Electronic Book')]  |  O[$refType = ('Book', 'Book Section', 'Manuscript', 'Edited Book', 'Electronic Article', 'Report')]  |  Q  |  EXCLAMATION[../T!=.]  |  S[$refType = ('Generic')]  |   STAR[$refType = ('Generic', 'Book Section', 'Journal Article', 'Magazine Article', 'Newspaper Article')]  ">
				<xsl:element name="dcterms:alternative">
					<xsl:value-of
						select="if (name(.)='STAR') then concat('Review of: ', .) else ." />
				</xsl:element>
			</xsl:for-each>
			<!-- IDENTIFIERS -->
			<xsl:for-each select="L">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:OTHER</xsl:attribute>
					<xsl:value-of select="." />
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="M">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type"
						select="  if (substring(., 1, 4) = 'ISI:') then 'eterms:ISI'  else if (substring(., 1, 4) = 'WOS:' and ($Flavor = 'MPIO' or $Flavor = 'CAESAR')) then 'eterms:ISI'  else 'eterms:OTHER'  " />
					<xsl:value-of
						select="if (substring(., 1, 4) = 'WOS:' and ($Flavor = 'MPIO' or $Flavor = 'CAESAR')) then substring-after(., ':') else ." />
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="R">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:DOI</xsl:attribute>
					<xsl:value-of select="." />
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each
				select="NUM_6[  $refType = 'Manuscript'  ]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:OTHER</xsl:attribute>
					<xsl:value-of select="." />
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each
				select="AT[  $refType = ('Book', 'Conference Proceedings', 'Edited Book', 'Electronic Book')   ]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:ISBN</xsl:attribute>
					<xsl:value-of select="." />
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each
				select="AT[  $refType = ('Book Section') and $sourceGenre != $genre-ves/enum[.='book']/@uri   ]">
				<dc:identifier>
					<xsl:attribute name="xsi:type">eterms:ISBN</xsl:attribute>
					<xsl:value-of select="." />
				</dc:identifier>
			</xsl:for-each>
			<xsl:for-each
				select="AT[  $refType = ('Electronic Article', 'Journal Article', 'Magazine Article', 'Newspaper Article')   ]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:ISSN</xsl:attribute>
					<xsl:value-of select="." />
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="AT[  $refType = 'Report'  ]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:OTHER</xsl:attribute>
					<xsl:value-of select="." />
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="AT[  $refType = 'Patent'  ]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:PATENT_NR</xsl:attribute>
					<xsl:value-of select="." />
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="U[  $refType = 'Patent'  ]">
				<dc:identifier xsi:type="eterms:OTHER">
					<xsl:value-of select="." />
				</dc:identifier>
			</xsl:for-each>
			<xsl:for-each select="V[  $refType = 'Patent'  ]">
				<dc:identifier xsi:type="eterms:OTHER">
					<xsl:value-of select="." />
				</dc:identifier>
			</xsl:for-each>
			<xsl:for-each
				select="AMPERSAND[  $refType = 'Patent'  ]">
				<dc:identifier xsi:type="eterms:PATENT_NR">
					<xsl:value-of select="." />
				</dc:identifier>
			</xsl:for-each>
			<xsl:for-each select="N[  $refType = 'Patent'  ]">
				<dc:identifier xsi:type="eterms:PATENT_APPLICATION_NR">
					<xsl:value-of select="." />
				</dc:identifier>
			</xsl:for-each>
			<xsl:for-each
				select="N[  $refType = 'Generic' and $Flavor = 'MPIGEM'  ]">
				<dc:identifier xsi:type="eterms:OTHER">
					<xsl:value-of select="." />
				</dc:identifier>
			</xsl:for-each>
			<!-- END OF IDENTIFIERS -->
			<!-- PUBLISHING INFO -->
			<xsl:variable name="publisher"
				select="  if (I and B and J and S) then ''  else if (B and I and $refType = 'Thesis') then string-join((B, I), ', ')  else if (I and $refType = ('Book', 'Conference Proceedings', 'Edited Book', 'Electronic Book', 'Generic', 'Thesis', 'Classical Work' )) then I  else if ((I or Y or QUESTION) and $refType = 'Report') then string-join((I, Y, QUESTION), ', ')  else ''  " />
			<!-- "Classical Work ergänzt für MPIGEM (Gemeinschaftsgüter) -->
			<xsl:variable name="edition"
				select="  if (NUM_7 and B and J and S) then ''  else if (NUM_7 and $refType = ('Book', 'Conference Proceedings', 'Edited Book', 'Electronic Book', 'Generic', 'Report')) then NUM_7  else if (ROUND_RIGHT_BRACKET and not(NUM_7) and $refType = ('Book', 'Edited Book', 'Generic')) then ROUND_RIGHT_BRACKET  else ''  " />
			<xsl:if test="$publisher != '' or $edition != ''">
				<eterms:publishing-info>
					<dc:publisher>
						<xsl:value-of select="$publisher" />
					</dc:publisher>
					<xsl:variable name="place"
						select="  if (C and ($refType = ('Book', 'Edited Book', 'Electronic Book', 'Manuscript', 'Report', 'Thesis', 'Magazine Article', 'Classical Work') or ($refType = 'Generic' and (not(NUM_9) or (lower-case(normalize-space(NUM_9)) != 'talk'))))) then C  else ''  " />
					<!-- "Classical Work ergänzt für MPIGEM (Gemeinschaftsgüter) -->
					<xsl:if test="$place!=''">
						<eterms:place>
							<xsl:value-of select="$place" />
						</eterms:place>
					</xsl:if>
					<xsl:if test="$edition!=''">
						<eterms:edition>
							<xsl:value-of select="$edition" />
						</eterms:edition>
					</xsl:if>
				</eterms:publishing-info>
			</xsl:if>
			<!-- END OF PUBLISHING INFO -->
			<!-- DATES -->
			<!-- date created -->
			<!-- Änderung Erndt: Prüfung, ob Datumsangaben von %7 (NUM_7) Buchstaben 
				enthält. Falls ja oder %7 nicht da, dann %D nehmen, falls da -->
			<xsl:variable name="alpha"
				select="'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'" />
			<xsl:variable name="dateCreated">
				<xsl:choose>
					<!-- Wenn %7 und da kein Buchstabe drin ist, nimm %7 -->
					<xsl:when
						test="NUM_7 and not(translate(NUM_7, translate(NUM_7, $alpha, ''), ''))">
						<xsl:value-of select="NUM_7" />
					</xsl:when>
					<!-- Wenn zwar %7, aber da Buchstabe drin ist, nimm %D, wenn vorhanden -->
					<xsl:when
						test="NUM_7 and translate(NUM_7, translate(NUM_7, $alpha, ''), '') and D and normalize-space(D) != '9998'">
						<!-- MPIGEM hat bei noch nicht veröffentlichten Publikationen '9998' 
							stehen. Wird weiter unten umgewandelt in ein dateAccepted -->
						<xsl:value-of select="D" />
					</xsl:when>
					<!-- Wenn kein %7, aber %D, nimm %D -->
					<xsl:when
						test="not(NUM_7) and D and normalize-space(D) != '9998'">
						<!-- MPIGEM hat bei noch nicht veröffentlichten Publikationen '9998' 
							stehen. Wird weiter unten umgewandelt in ein dateAccepted -->
						<xsl:value-of select="D" />
					</xsl:when>
					<!-- Wenn kein %7 oder da Buchstabe drin ist und auch kein %D, dann 
						keine Infos für creation date vorhanden -->
					<xsl:otherwise>
						<xsl:value-of select="''" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<!-- Ende Erndt -->
			<xsl:if test="$dateCreated!=''">
				<dcterms:issued xsi:type="dcterms:W3CDTF">
					<xsl:value-of select="$dateCreated" />
				</dcterms:issued>
			</xsl:if>
			<!-- date published online -->
			<!-- Änderung Erndt: Prüfung, ob Datumsangaben von %8 (NUM_8) Buchstaben 
				enthält. Falls ja oder %8 nicht da, dann eben keine Angabe zu Online-Datum -->
			<xsl:variable name="datePublishedOnline">
				<xsl:choose>
					<!-- Wenn %8 und da kein Buchstabe drin ist, nimm %8 -->
					<xsl:when
						test="NUM_8 and not(translate(NUM_8, translate(NUM_8, $alpha, ''), ''))">
						<xsl:value-of select="NUM_8" />
					</xsl:when>
					<!-- Wenn zwar %8, aber da Buchstabe drin ist, kein Online date -->
					<xsl:when
						test="NUM_8 and translate(NUM_8, translate(NUM_8, $alpha, ''), '')">
						<xsl:value-of select="''" />
					</xsl:when>
					<!-- Wenn kein %8, dann kein Online date -->
					<xsl:otherwise>
						<xsl:value-of select="''" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<!-- Ende Erndt -->
			<xsl:if test="$datePublishedOnline!=''">
				<eterms:published-online
					xsi:type="dcterms:W3CDTF">
					<xsl:value-of select="$datePublishedOnline" />
				</eterms:published-online>
			</xsl:if>
			<xsl:variable name="dateModified"
				select="  if (EQUAL and not($Flavor = 'MPIMP' or $Flavor = 'MPIMPExt')) then escidocFunctions:normalizeDate(EQUAL)  else ''  " />
			<xsl:if test="$dateModified!=''">
				<dcterms:modified xsi:type="dcterms:W3CDTF">
					<xsl:value-of select="$dateModified" />
				</dcterms:modified>
			</xsl:if>
			<!-- dateAccepted mit MPIGEM-Besonderheit (angenommene Publikationen haben 
				'9998' in %D. Soll zu 2015-01-02 werden -->
			<xsl:choose>
				<xsl:when
					test="$Flavor='MPIGEM' and normalize-space(D)='9998'">
					<xsl:variable name="dateAccepted">
						2015-01-02
					</xsl:variable>
					<xsl:if test="$dateAccepted != ''">
						<dcterms:dateAccepted
							xsi:type="dcterms:W3CDTF">
							<xsl:value-of select="$dateAccepted" />
						</dcterms:dateAccepted>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="dateAccepted"
						select="  if ($dateCreated and $refType = 'Thesis') then $dateCreated  else ''  " />
					<xsl:if test="$dateAccepted != ''">
						<dcterms:dateAccepted
							xsi:type="dcterms:W3CDTF">
							<xsl:value-of select="$dateAccepted" />
						</dcterms:dateAccepted>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
			<!-- früherer Abschnitt dateAccepted: <xsl:variable name="dateAccepted" 
				select=" if ($dateCreated and $refType = 'Thesis') then $dateCreated else 
				'' "/><xsl:if test="$dateAccepted != ''"><dcterms:dateAccepted xsi:type="dcterms:W3CDTF"><xsl:value-of 
				select="$dateAccepted"/></dcterms:dateAccepted></xsl:if> -->
			<!-- end of DATES -->
			<!-- review-method / Expertenbegutachtung für MPI f. Gemeinschaftsgüter 
				(MPIGEM) -->
			<xsl:if
				test="($Flavor = 'MPIGEM' and NUM_1 = 'peer-reviewed')">
				<eterms:review-method>http://purl.org/eprint/status/PeerReviewed
				</eterms:review-method>
			</xsl:if>
			<!-- SOURCE -->
			<xsl:if test="$sourceGenre!=''">
				<xsl:call-template name="createSource">
					<xsl:with-param name="sgen" select="$sourceGenre" />
					<xsl:with-param name="identifier" select="AT" />
				</xsl:call-template>
			</xsl:if>
			<!-- SECOND SOURCE -->
			<xsl:if
				test="$secondSourceGenre = $genre-ves/enum[.='series']/@uri">
				<xsl:call-template name="createSecondSource">
					<xsl:with-param name="ssgen"
						select="$secondSourceGenre" />
				</xsl:call-template>
			</xsl:if>
			<!-- TOTAL NUMBER OF PAGES -->
			<xsl:if
				test="P and $refType = ('Book', 'Edited Book', 'Electronic Book', 'Thesis', 'Generic', 'Conference Proceedings', 'Manuscript', 'Report')">
				<xsl:element name="eterms:total-number-of-pages">
					<xsl:value-of select="P" />
				</xsl:element>
			</xsl:if>
			<xsl:if test="AMPERSAND and $refType = 'Book'">
				<xsl:element name="eterms:total-number-of-pages">
					<xsl:value-of select="AMPERSAND" />
				</xsl:element>
			</xsl:if>
			<!-- EVENT -->
			<xsl:if
				test="B and $Flavor != 'IPP' and ($refType = ('Conference Paper', 'Conference Proceedings') or ($refType = 'Generic' and NUM_9 and (lower-case(normalize-space(NUM_9)) = 'talk')))">
				<xsl:element name="event:event">
					<xsl:element name="dc:title">
						<xsl:value-of select="B" />
					</xsl:element>
					<xsl:if test="D and $refType = 'Conference Proceedings'">
						<xsl:element name="eterms:start-date">
							<xsl:value-of select="D" />
						</xsl:element>
					</xsl:if>
					<xsl:if test="C">
						<xsl:element name="eterms:place">
							<xsl:value-of select="C" />
						</xsl:element>
					</xsl:if>
					<xsl:if test="D">
						<eterms:start-date xsi:type="dcterms:W3CDTF">
							<xsl:value-of select="D" />
						</eterms:start-date>
					</xsl:if>
				</xsl:element>
			</xsl:if>
			<!-- Besonderheit für IPP -->
			<xsl:if
				test="S and $Flavor = 'IPP' and ($refType = ('Conference Paper', 'Conference Proceedings', 'Courseware') or ($refType = 'Generic' and NUM_9 and (lower-case(normalize-space(NUM_9)) = 'talk')))">
				<xsl:element name="event:event">
					<xsl:element name="dc:title">
						<xsl:value-of select="S" />
					</xsl:element>
					<xsl:if test="C">
						<xsl:element name="eterms:place">
							<xsl:value-of select="C" />
						</xsl:element>
					</xsl:if>
					<xsl:if test="NUM_1">
						<eterms:start-date xsi:type="dcterms:W3CDTF">
							<xsl:value-of select="NUM_1" />
						</eterms:start-date>
					</xsl:if>
					<xsl:if test="NUM_2">
						<eterms:end-date xsi:type="dcterms:W3CDTF">
							<xsl:value-of select="NUM_2" />
						</eterms:end-date>
					</xsl:if>
				</xsl:element>
			</xsl:if>
			<!-- DEGREE -->
			<!-- ??????? Check! -->
			<xsl:if test="$refType = 'Thesis'">
				<xsl:variable name="dgr"
					select="escidocFunctions:normalizeDegree(V)" />
				<xsl:variable name="dgr"
					select="$degree-ves/enum[$dgr=.]/@uri" />
				<xsl:if test="$dgr!=''">
					<xsl:element name="eterms:degree">
						<xsl:value-of select="$dgr" />
					</xsl:element>
				</xsl:if>
			</xsl:if>
			<!-- ABSTRACT -->
			<xsl:if test="X">
				<xsl:element name="dcterms:abstract">
					<xsl:choose>
						<xsl:when
							test="($Flavor = 'MPIMP' or $Flavor = 'MPIMPExt') and EQUAL != '1'" />
						<xsl:otherwise>
							<xsl:value-of select="X" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
			</xsl:if>
			<!-- SUBJECT -->
			<xsl:if test="K">
				<xsl:element name="dcterms:subject">
					<xsl:value-of select="K" />
				</xsl:element>
			</xsl:if>
			<!-- tableOfContents -->
			<!-- <xsl:if test="ROUND_LEFT_BRACKET and $refType='Report'"> -->
			<!-- <xsl:element name="dcterms:tableOfContents"> -->
			<!-- <xsl:value-of select="ROUND_LEFT_BRACKET"/> -->
			<!-- </xsl:element> -->
			<!-- </xsl:if> -->
			<xsl:if test="SLASH and $refType='Report'">
				<xsl:element name="dcterms:tableOfContents">
					<xsl:value-of select="SLASH" />
				</xsl:element>
			</xsl:if>
			<!-- LOCATION -->
			<!-- <xsl:if test="I and $refType = 'Manuscript'"> -->
			<!-- <xsl:element name="eterms:location"> -->
			<!-- <xsl:value-of select="I"/> -->
			<!-- </xsl:element> -->
			<!-- </xsl:if> -->
		</xsl:element>
	</xsl:template>
	<!-- SOURCE -->
	<xsl:template name="createSource">
		<xsl:param name="sgen" />
		<xsl:param name="identifier" />
		<xsl:variable name="refType"
			select="normalize-space(NUM_0)" />
		<xsl:element name="source:source">
			<!-- SOURCE GENRE -->
			<xsl:attribute name="type">
				<xsl:value-of select="$sgen" />
			</xsl:attribute>
			<!-- SOURCE TITLE -->
			<xsl:element name="dc:title">
				<xsl:choose>
					<xsl:when test="B">
						<xsl:value-of select="B" />
					</xsl:when>
					<xsl:when
						test="J and $refType = ('Journal Article', 'Magazine Article')">
						<xsl:value-of select="J" />
					</xsl:when>
					<!-- Ergänzung für MPI IPP für Altdatenimport aus Libero. Haben hin 
						und wieder ZS-Angaben auch bei Genre Conference Paper! -->
					<xsl:when
						test="J and $Flavor = 'IPP' and $refType = 'Conference Paper'">
						<xsl:value-of select="J" />
					</xsl:when>
					<xsl:when
						test="S and $refType = ('Conference Proceedings')">
						<xsl:value-of select="S" />
					</xsl:when>
				</xsl:choose>
			</xsl:element>
			<!-- SOURCE ALTTITLE -->
			<xsl:for-each
				select="  J[  exists(../B)  and  $refType = ('Journal Article', 'Magazine Article')   ]  |  O[  $refType = ('Journal Article', 'Magazine Article')   ]  ">
				<xsl:element name="dcterms:alternative">
					<xsl:value-of select="." />
				</xsl:element>
			</xsl:for-each>
			<!-- SOURCE CREATORS -->
			<xsl:for-each
				select="  E[  $refType =   ('Book', 'Edited Book', 'Report', 'Conference Proceedings', 'Book Section', 'Conference Paper')  ]  |  Y[  $refType = ('Conference Proceedings')  ]  ">
				<xsl:call-template name="createCreator">
					<xsl:with-param name="role"
						select="$creator-ves/enum[.='editor']/@uri" />
					<xsl:with-param name="isSource" select="true()" />
				</xsl:call-template>
			</xsl:for-each>
			<!-- SOURCE VOLUME -->
			<xsl:if test="N and $refType = ('Book', 'Edited Book')">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="N" />
				</xsl:element>
			</xsl:if>
			<xsl:if
				test="V and not(N) and $refType = ('Book', 'Edited Book')">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V" />
				</xsl:element>
			</xsl:if>
			<xsl:if
				test="V and $refType = ('Electronic Article', 'Journal Article', 'Magazine Article', 'Newspaper Article')">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V" />
				</xsl:element>
			</xsl:if>
			<xsl:if
				test="V and B and ($refType = ('Manuscript') or $refType = ('Book Section'))">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V" />
				</xsl:element>
			</xsl:if>
			<xsl:if
				test="V and S and $refType = ('Conference Proceedings')">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V" />
				</xsl:element>
			</xsl:if>
			<!-- caesar will %V-Angabe auch in Quelle Konferenzband haben. IPP hat 
				ZS-Angaben auch bei Conference Paper! -->
			<xsl:if
				test="V and (B or J) and $refType = ('Conference Paper') and ($Flavor = 'CAESAR' or $Flavor = 'IPP')">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V" />
				</xsl:element>
			</xsl:if>
			<xsl:if test="V and not(N) and B and $refType = 'Report'">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V" />
				</xsl:element>
			</xsl:if>
			<xsl:if
				test="V and not(NUM_6 or N) and B and $refType = 'Report'">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V" />
				</xsl:element>
			</xsl:if>
			<!-- SOURCE ISSUE -->
			<xsl:if
				test="N and $refType = ('Magazine Article', 'Electronic Article', 'Journal Article')">
				<xsl:element name="eterms:issue">
					<xsl:value-of select="N" />
				</xsl:element>
			</xsl:if>
			<!-- caesar will %N-Angabe (Issue) auch in Quelle Konferenzband haben -->
			<xsl:if
				test="N and B and $refType = ('Conference Paper') and $Flavor = 'CAESAR'">
				<xsl:element name="eterms:issue">
					<xsl:value-of select="N" />
				</xsl:element>
			</xsl:if>
			<!-- SOURCE PAGES -->
			<xsl:if
				test="P and $refType = ('Electronic Article', 'Journal Article', 'Magazine Article', 'Newspaper Article', 'Book Section', 'Conference Paper' )">
				<xsl:variable name="pages"
					select="tokenize(normalize-space(P), '[-–]+')" />
				<!-- Änderung Erndt für Seitenzahlen mit mehr als einem "-" (z. B. "631809-1-631809-10" 
					bei caesar-Daten) -->
				<xsl:if test="count($pages)=4 and $pages[4]!=''">
					<xsl:element name="eterms:start-page">
						<xsl:value-of
							select="normalize-space(concat($pages[1], '/', $pages[2]))" />
					</xsl:element>
					<xsl:element name="eterms:end-page">
						<xsl:value-of
							select="normalize-space(concat($pages[3], '/', $pages[4]))" />
					</xsl:element>
				</xsl:if>
				<xsl:if test="count($pages)=3 and $pages[3]!=''">
					<xsl:element name="eterms:start-page">
						<xsl:value-of
							select="normalize-space(concat($pages[1], '/', $pages[2]))" />
					</xsl:element>
					<xsl:element name="eterms:end-page">
						<xsl:value-of select="normalize-space($pages[3])" />
					</xsl:element>
				</xsl:if>
				<xsl:if test="count($pages)=1 and $pages[1]!=''">
					<xsl:element name="eterms:start-page">
						<xsl:value-of select="normalize-space($pages[1])" />
					</xsl:element>
				</xsl:if>
				<xsl:if test="count($pages)=2 and $pages[2]!=''">
					<xsl:element name="eterms:start-page">
						<xsl:value-of select="normalize-space($pages[1])" />
					</xsl:element>
					<xsl:element name="eterms:end-page">
						<xsl:value-of select="normalize-space($pages[2])" />
					</xsl:element>
				</xsl:if>
			</xsl:if>
			<xsl:if test="N and not(P) and $refType = 'Newspaper Article'">
				<xsl:element name="eterms:start-page">
					<xsl:value-of select="N" />
				</xsl:element>
			</xsl:if>
			<xsl:if
				test="AMPERSAND and not(P) and $refType = ('Journal Article', 'Magazine Article', 'Manuscript')">
				<xsl:element name="eterms:start-page">
					<xsl:value-of select="AMPERSAND" />
				</xsl:element>
			</xsl:if>
			<!-- SOURCE SEQUENCE NUMBER -->
			<xsl:if test="N and B and $refType = ('Report', 'Manuscript')">
				<xsl:element name="eterms:sequence-number">
					<xsl:value-of select="N" />
				</xsl:element>
			</xsl:if>
			<xsl:if test="AMPERSAND and $refType = 'Book Section'">
				<xsl:element name="eterms:sequence-number">
					<xsl:value-of select="AMPERSAND" />
				</xsl:element>
			</xsl:if>
			<xsl:if test="SQUARE_RIGHT_BRACKET and ($refType = 'Electronic Article' or $refType = 'Journal Article' or $refType = 'Magazine Article' or $refType = 'Newspaper Article')">
				<xsl:element name="eterms:sequence-number">
					<xsl:value-of select="SQUARE_RIGHT_BRACKET"/>
				</xsl:element>
			</xsl:if>
			<!-- SOURCE PUBLISHINGINFO -->
			<xsl:variable name="publisher"
				select="  if (I and $refType = ('Book Section', 'Conference Paper', 'Electronic Article', 'Magazine Article', 'Newspaper Article', 'Journal Article')) then I else ''  " />
			<xsl:if test="$publisher!=''">
				<xsl:element name="eterms:publishing-info">
					<xsl:element name="dc:publisher">
						<xsl:value-of select="$publisher" />
					</xsl:element>
					<xsl:variable name="place"
						select="  if (C and $Flavor != 'IPP' and $refType = ('Book Section', 'Newspaper Article')) then C  else if ($Flavor = 'IPP' and NUM_3 and $refType = 'Journal Article') then NUM_3   else ''  " />
					<xsl:if test="$place!=''">
						<xsl:element name="eterms:place">
							<xsl:value-of select="$place" />
						</xsl:element>
					</xsl:if>
					<xsl:variable name="edition"
						select="  if (NUM_7 and $refType = ('Book Section', 'Electronic Article', 'Magazine Article', 'Newspaper Article', 'Report')) then NUM_7  else if (ROUND_RIGHT_BRACKET and not(NUM_7) and $refType = ('Book Section', 'Magazine Article')) then ROUND_RIGHT_BRACKET   else ''  " />
					<xsl:if test="$edition!=''">
						<xsl:element name="eterms:edition">
							<xsl:value-of select="$edition" />
						</xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:if>
			<!-- SOURCE IDENTIFIER -->
			<xsl:if
				test="$identifier and $refType = ('Book Section') and $sgen = $genre-ves/enum[.='book']/@uri">
				<dc:identifier>
					<xsl:attribute name="xsi:type">eterms:ISBN</xsl:attribute>
					<xsl:value-of select="$identifier" />
				</dc:identifier>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	<!-- END OF SOURCE -->
	<!-- SECOND SOURCE -->
	<xsl:template name="createSecondSource">
		<xsl:param name="ssgen" />
		<xsl:variable name="refType"
			select="normalize-space(NUM_0)" />
		<source:source>
			<!-- SOURCE GENRE -->
			<xsl:attribute name="type">
				<xsl:value-of select="$ssgen" />
			</xsl:attribute>
			<!-- SOURCE TITLE -->
			<dc:title>
				<xsl:choose>
					<xsl:when test="S">
						<xsl:value-of select="S" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of
							select="error(QName('http://www.escidoc.de', 'err:NoSeriesTitle' ), concat('There is no series title -', $ssgen))" />
					</xsl:otherwise>
				</xsl:choose>
			</dc:title>
			<xsl:if test="N and $refType = ('Book Section')">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="N" />
				</xsl:element>
			</xsl:if>
			<xsl:if test="V and not(N) and $refType = ('Book Section')">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V" />
				</xsl:element>
			</xsl:if>
			<xsl:for-each
				select="  Y[  $refType = ('Book Section')  ]  ">
				<xsl:call-template name="createCreator">
					<xsl:with-param name="role"
						select="$creator-ves/enum[.='editor']/@uri" />
					<xsl:with-param name="isSource" select="true()" />
				</xsl:call-template>
			</xsl:for-each>
		</source:source>
	</xsl:template>
	<!-- END OF SECOND SOURCE -->
	<!-- CREATORS -->
	<xsl:template name="createCreators">
		<xsl:param name="gen" />
		<xsl:variable name="refType"
			select="normalize-space(NUM_0)" />
		<xsl:for-each select="A|E|Y|QUESTION|NUM_3">
			<xsl:if test="name(.)='A'">
				<xsl:choose>
					<xsl:when
						test="  $refType = (  'Generic',   'Book',   'Book Section',   'Conference Paper',   'Conference Proceedings',   'Electronic Article',   'Electronic Book',   'Journal Article',   'Magazine Article',   'Newspaper Article',   'Manuscript',   'Report',   'Thesis',  'Artwork',   'Classical Work',  'Courseware', 'Dataset', 'Blog', 'Film or Broadcast' )">
						<!-- Artwork und Classical Work sind Endnote-Sondertypen des MPIGEM 
							(Gemeinschaftsgüter -->
						<xsl:variable name="currentAuthorPosition"
							select="position()" />
						<xsl:comment>
							--Author --
						</xsl:comment>
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role"
								select="$creator-ves/enum[.='author']/@uri" />
							<xsl:with-param name="pos"
								select="count(../A[position() &lt; $currentAuthorPosition]) + 1" />
							<xsl:with-param name="gen" select="$gen" />
						</xsl:call-template>
					</xsl:when>
					<!-- INTERVIEW -->
					<xsl:when
						test="  $refType = (  'Interview'  )">
						<xsl:variable name="currentAuthorPosition"
							select="position()" />
						<xsl:comment>
							--Author --
						</xsl:comment>
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role"
								select="$creator-ves/enum[.='interviewee']/@uri" />
							<xsl:with-param name="pos"
								select="count(../A[position() &lt; $currentAuthorPosition]) + 1" />
							<xsl:with-param name="gen" select="$gen" />
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="$refType='Edited Book'">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role"
								select="$creator-ves/enum[.='editor']/@uri" />
							<xsl:with-param name="gen" select="$gen" />
						</xsl:call-template>
					</xsl:when>
					<!-- SOFTWARE -->
					<xsl:when
						test="  $refType = (  'Computer Program'  )">
						<xsl:variable name="currentAuthorPosition"
							select="position()" />
						<xsl:comment>
						    --Author --
						</xsl:comment>
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role"
								select="$creator-ves/enum[.='developer']/@uri" />
							<xsl:with-param name="pos"
								select="count(../A[position() &lt; $currentAuthorPosition]) + 1" />
							<xsl:with-param name="gen" select="$gen" />
						</xsl:call-template>
					</xsl:when>
					
				</xsl:choose>
			</xsl:if>
			<!-- Besonderheit MPI Gemeinschaftsgüter: Immer Editoren berücksichtigen, 
				egal welches Genre! Erndt, 31.07.2015 -->
			<xsl:if test="name(.)='E' and $Flavor != 'MPIGEM'">
				<xsl:choose>
					<xsl:when test="$refType='Generic'">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role"
								select="$creator-ves/enum[.='contributor']/@uri" />
							<xsl:with-param name="gen" select="$gen" />
						</xsl:call-template>
					</xsl:when>
					<xsl:when
						test="$refType = ('Conference Proceedings', 'Electronic Book')">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role"
								select="$creator-ves/enum[.='editor']/@uri" />
							<xsl:with-param name="gen" select="$gen" />
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:if>
			<xsl:if test="name(.)='E' and $Flavor = 'MPIGEM'">
				<xsl:call-template name="createCreator">
					<xsl:with-param name="role"
						select="$creator-ves/enum[.='editor']/@uri" />
					<xsl:with-param name="gen" select="$gen" />
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="name(.)='E'">
				<xsl:choose>
					<xsl:when test="$refType = 'Interview' ">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role"
								select="$creator-ves/enum[.='interviewer']/@uri" />
							<xsl:with-param name="gen" select="$gen" />
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="$refType = ('Dataset', 'Blog', 'Film or Broadcast', 'Computer Program')">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role"
								select="$creator-ves/enum[.='editor']/@uri" />
							<xsl:with-param name="gen" select="$gen" />
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:if>
			<xsl:if test="name(.)='Y'">
				<xsl:choose>
					<xsl:when
						test="$refType='Generic' or ($refType='Conference Proceedings' and S)">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role"
								select="$creator-ves/enum[.='contributor']/@uri" />
							<xsl:with-param name="gen" select="$gen" />
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="$refType='Thesis'">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role"
								select="$creator-ves/enum[.='advisor']/@uri" />
							<xsl:with-param name="gen" select="$gen" />
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:if>
			<xsl:if test="name(.)='QUESTION'">
				<xsl:if
					test="$refType = ('Book', 'Book Section', 'Edited Book')">
					<xsl:call-template name="createCreator">
						<xsl:with-param name="role"
							select="$creator-ves/enum[.='translator']/@uri" />
						<xsl:with-param name="gen" select="$gen" />
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="$refType = ('Generic')">
					<xsl:call-template name="createCreator">
						<xsl:with-param name="role"
							select="$creator-ves/enum[.='contributor']/@uri" />
						<xsl:with-param name="gen" select="$gen" />
					</xsl:call-template>
				</xsl:if>
			</xsl:if>
			<!-- Besonderheit für MPI IPP Altdatenimport: in %3 sind herausgebende 
				Organisationen angegeben. Sollen verwertet werden! Erndt, 18.8.15 -->
			<xsl:if test="$Flavor='IPP' and name(.)='NUM_3'">
				<xsl:call-template name="createOrganization">
					<xsl:with-param name="role"
						select="$creator-ves/enum[.='editor']/@uri" />
				</xsl:call-template>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<!-- Besonderheit für MPI IPP Altdatenimport: in %3 sind herausgebende Organisationen 
		angegeben. Sollen verwertet werden! template "createOrganization" hinzugefügt 
		(Erndt, 18.8.15) -->
	<xsl:template name="createOrganization">
		<xsl:param name="role" />
		<xsl:variable name="orga-name"
			select="fn:normalize-space(.)" />
		<xsl:variable name="external-organization">
			<xsl:text>ou_persistent22</xsl:text>
		</xsl:variable>
		<xsl:element name="eterms:creator">
			<xsl:attribute name="role">
				<xsl:value-of select="$role" />
			</xsl:attribute>
			<xsl:element name="organization:organization">
				<xsl:element name="dc:title">
					<xsl:value-of select="$orga-name" />
				</xsl:element>
				<xsl:element name="dc:identifier">
					<xsl:value-of select="$external-organization" />
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template name="createCreator">
		<xsl:param name="role" />
		<xsl:param name="isSource" />
		<xsl:param name="pos" select="0" />
		<xsl:param name="gen" />
		<xsl:choose>
			<xsl:when test="$isSource">
				<xsl:element name="eterms:creator">
					<xsl:attribute name="role">
						<xsl:value-of select="$role" />
					</xsl:attribute>
					<xsl:call-template name="createPerson">
						<xsl:with-param name="isSource" select="$isSource" />
						<xsl:with-param name="gen" select="$gen" />
					</xsl:call-template>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="eterms:creator">
					<xsl:attribute name="role">
						<xsl:value-of select="$role" />
					</xsl:attribute>
					<xsl:call-template name="createPerson">
						<xsl:with-param name="isSource" select="$isSource" />
						<xsl:with-param name="gen" select="$gen" />
					</xsl:call-template>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="createPerson">
		<xsl:param name="isSource" />
		<xsl:param name="pos" select="0" />
		<xsl:param name="gen" />
		<xsl:variable name="familyname"
			select="fn:normalize-space(fn:substring-before(. , ','))" />
		<xsl:variable name="givenname"
			select="fn:normalize-space(fn:substring-after(. , ','))" />
		<xsl:choose>
			<xsl:when test="$CoNE = 'false'">
				<xsl:element name="person:person">
					<xsl:element name="eterms:family-name">
						<xsl:value-of select="$familyname" />
					</xsl:element>
					<xsl:element name="eterms:given-name">
						<xsl:value-of select="$givenname" />
					</xsl:element>
					<xsl:choose>
						<xsl:when test="not($isSource) and $Flavor != 'MPFI'">
							<organization:organization>
								<dc:title>Max Planck Society</dc:title>
								<dc:identifier>
									<xsl:value-of select="$root-ou" />
								</dc:identifier>
							</organization:organization>
						</xsl:when>
						<xsl:when test="not($isSource) and $Flavor = 'MPFI'">
							<organization:organization>
								<dc:title>Max Planck Florida Institute for Neuroscience
								</dc:title>
								<dc:identifier>ou_1950288</dc:identifier>
							</organization:organization>
						</xsl:when>
					</xsl:choose>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="cone-creator">
					<xsl:choose>
						<xsl:when
							test="($Flavor = 'MPIMP' or $Flavor = 'MPIMPExt')">
							<xsl:copy-of
								select="Util:queryConeExact('persons', concat($familyname, ', ', $givenname), 'Max Planck Institute of Molecular Plant Physiology')" />
						</xsl:when>
						<xsl:when test="($Flavor = 'CAESAR')">
							<xsl:copy-of
								select="Util:queryConeExact('persons', concat($familyname, ', ', $givenname), 'Center of Advanced European Studies and Research (caesar)')" />
						</xsl:when>
						<xsl:when test="($Flavor = 'IPP')">
							<xsl:copy-of
								select="Util:queryConeExact('persons', concat($familyname, ', ', $givenname), 'Max Planck Institute for Plasma Physics')" />
						</xsl:when>
						<xsl:when test="($Flavor = 'MPIO')">
							<xsl:copy-of
								select="Util:queryConeExact('persons', concat($familyname, ', ', $givenname), 'Max Planck Institut für Ornithologie')" />
						</xsl:when>
						<xsl:when test="($Flavor = 'MPFI')">
							<xsl:copy-of
								select="Util:queryConeExact('persons', concat($familyname, ', ', $givenname), 'Max Planck Florida Institute for Neuroscience')" />
						</xsl:when>
						<xsl:when test="($Flavor = 'MPIGEM')">
							<xsl:copy-of
								select="Util:queryConeExact('persons', concat($familyname, ', ', $givenname), 'Max Planck Institute for Research on Collective Goods')" />
						</xsl:when>
						<xsl:when test="($Flavor = 'MPIB')">
							<xsl:copy-of
								select="Util:queryConeExact('persons', concat($familyname, ', ', $givenname), 'Max Planck Institute for Human Development')" />
						</xsl:when>
						<xsl:when test="($Flavor = 'MPIEA')">
							<xsl:copy-of
								select="Util:queryConeExact('persons', concat($familyname, ', ', $givenname), 'Max Planck Institute for Empirical Aesthetics')" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy-of
								select="Util:queryCone('persons', concat('&quot;',$familyname, ', ', $givenname, '&quot;'))" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="multiplePersonsFound"
					select="exists($cone-creator/cone/rdf:RDF/rdf:Description[@rdf:about != $cone-creator/cone/rdf:RDF/rdf:Description/@rdf:about])" />
				<xsl:if test="$multiplePersonsFound">
					<xsl:value-of
						select="error(QName('http://www.escidoc.de', 'err:MultipleCreatorsFound' ), concat('There is more than one CoNE entry matching -', concat($familyname, ', ', $givenname), '-'))" />
				</xsl:if>
				<person:person>
					<eterms:family-name>
						<xsl:value-of select="$familyname" />
					</eterms:family-name>
					<eterms:given-name>
						<xsl:value-of select="$givenname" />
					</eterms:given-name>
					<!-- Besonderheit für Import von externen caesar-Publikationen -->
					<!--<xsl:if test="exists($cone-creator/cone/rdf:RDF/rdf:Description) 
						and $Flavor = 'CAESAR'"><organization:organization><dc:title><xsl:text>External 
						Organizations</xsl:text></dc:title><dc:identifier><xsl:text>ou_persistent22</xsl:text></dc:identifier></organization:organization></xsl:if> -->
					<!-- Affiliated Institution depends on publication-date) -->
					<!-- <xsl:if test="$Flavor != 'CAESAR'"> -->
					<xsl:if test="$Flavor != ''">
						<xsl:variable name="publication-date">
							<xsl:choose>
								<xsl:when test="./../D">
									<xsl:value-of select="./../D" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when
											test="(gen = 'http://purl.org/escidoc/metadata/ves/publication-types/article' or gen = 'http://purl.org/escidoc/metadata/ves/publication-types/magazine-article') and exists(NUM_7) and NUM_7 != ''">
											<xsl:value-of select="NUM_7" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:choose>
												<xsl:when
													test="(gen != 'http://purl.org/escidoc/metadata/ves/publication-types/article' or gen = 'http://purl.org/escidoc/metadata/ves/publication-types/magazine-article') and exists(NUM_8) and NUM_8 != ''">
													<xsl:value-of select="NUM_8" />
												</xsl:when>
											</xsl:choose>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:comment>
							Publication-Date:
							<xsl:value-of select="$publication-date" />
						</xsl:comment>
						<xsl:choose>
							<xsl:when
								test="($cone-creator/cone[1]/rdf:RDF[1]/rdf:Description/escidoc:position[escidocFunctions:smaller(rdf:Description/escidoc:start-date, $publication-date)   and escidocFunctions:smaller($publication-date, rdf:Description/escidoc:end-date)])   and ($Flavor != 'MPIMPExt')">
								<xsl:for-each
									select="$cone-creator/cone[1]/rdf:RDF[1]/rdf:Description/escidoc:position">
									<xsl:comment>
										pubdate:
										<xsl:value-of select="$publication-date" />
									</xsl:comment>
									<xsl:comment>
										start:
										<xsl:value-of
											select="rdf:Description/escidoc:start-date" />
									</xsl:comment>
									<xsl:comment>start
										&lt; pubdate
										<xsl:value-of
											select="escidocFunctions:smaller(rdf:Description/escidoc:start-date, $publication-date)" />
									</xsl:comment>
									<xsl:comment>
										end:
										<xsl:value-of
											select="rdf:Description/escidoc:end-date" />
									</xsl:comment>
									<xsl:comment>pubdate
										&lt; end
										<xsl:value-of
											select="escidocFunctions:smaller($publication-date, rdf:Description/escidoc:end-date)" />
									</xsl:comment>
									<xsl:if
										test="escidocFunctions:smaller(rdf:Description/escidoc:start-date, $publication-date) and escidocFunctions:smaller($publication-date, rdf:Description/escidoc:end-date)">
										<xsl:comment>
											Case: affiliated institute found for publishing date
										</xsl:comment>
										<organization:organization>
											<dc:title>
												<xsl:value-of
													select="rdf:Description/eprints:affiliatedInstitution" />
											</dc:title>
											<dc:identifier>
												<xsl:value-of
													select="rdf:Description/dc:identifier" />
											</dc:identifier>
										</organization:organization>
										<!-- Übernahme der CoNE-ID nachträglich ergänzt (Erndt, 21.04.15) -->
										<dc:identifier xsi:type="CONE">
											<xsl:value-of
												select="$cone-creator/cone[1]/rdf:RDF[1]/rdf:Description[1]/@rdf:about" />
										</dc:identifier>
									</xsl:if>
								</xsl:for-each>
							</xsl:when>
							<!-- MPI IPP will für Altdatenimport Institut als Affiliation, falls 
								Publikationsdatum nicht in eventuellen Zeitraum d. CoNE-Affiliations fällt -->
							<xsl:when
								test="($cone-creator/cone[1]/rdf:RDF[1]/rdf:Description) and not($cone-creator/cone[1]/rdf:RDF[1]/rdf:Description/escidoc:position[escidocFunctions:smaller(rdf:Description/escidoc:start-date, $publication-date)   and escidocFunctions:smaller($publication-date, rdf:Description/escidoc:end-date)])   and ($Flavor = 'IPP')">
								<organization:organization>
									<dc:title>
										<xsl:text>Max Planck Institute for Plasma Physics</xsl:text>
									</dc:title>
									<dc:identifier>
										<xsl:text>ou_persistent27</xsl:text>
									</dc:identifier>
								</organization:organization>
								<!-- Übernahme der CoNE-ID nachträglich ergänzt (Erndt, 21.04.15) -->
								<dc:identifier xsi:type="CONE">
									<xsl:value-of
										select="$cone-creator/cone[1]/rdf:RDF[1]/rdf:Description[1]/@rdf:about" />
								</dc:identifier>
							</xsl:when>
							<!-- MPI IPP will für Altdatenimport "External Organizations", falls 
								kein CoNE-Treffer -->
							<xsl:when
								test="not($cone-creator/cone[1]/rdf:RDF[1]/rdf:Description) and ($Flavor = 'IPP')">
								<organization:organization>
									<dc:title>
										<xsl:text>External Organizations</xsl:text>
									</dc:title>
									<dc:identifier>
										<xsl:text>ou_persistent22</xsl:text>
									</dc:identifier>
								</organization:organization>
							</xsl:when>
						</xsl:choose>
					</xsl:if>
				</person:person>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- END OF CREATORS -->
	<xsl:template name="component">
		<xsl:param name="oa" select="false()" />
		<xsl:param name="internal-managed" select="true()" />
		<xsl:choose>
			<xsl:when test="$internal-managed">
				<xsl:variable name="suffix">
					<xsl:choose>
						<xsl:when test="contains(., '.')">
							<xsl:value-of select="substring-after(., '.')" />
						</xsl:when>
						<xsl:otherwise>
							pdf
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<!-- PATH(-SUFFIX) AND FILENAME uri-encoded -->
				<xsl:variable name="path_encoded">
					<xsl:choose>
						<xsl:when
							test="$Flavor = 'MPIMP' or $Flavor = 'MPIMPExt' or $Flavor = 'CAESAR'">
							<xsl:value-of
								select="fn:replace(concat(encode-for-uri(substring-before(., '/')), '/'), ' ', '%20')" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="''" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="filename_encoded">
					<xsl:choose>
						<xsl:when test="contains(., '.')">
							<xsl:choose>
								<xsl:when
									test="$Flavor = 'MPIMP' or $Flavor = 'MPIMPExt' or $Flavor = 'CAESAR'">
									<xsl:value-of
										select="fn:replace(encode-for-uri(substring-after(., '/')), ' ', '%20')" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of
										select="encode-for-uri(fn:replace(., ' ', '%20'))" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when
									test="$Flavor = 'MPIMP' or $Flavor = 'MPIMPExt'">
									<xsl:value-of
										select="fn:replace(substring-after(., '/'), ' ', '%20')" />
									.
									<xsl:value-of select="$suffix" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="fn:replace(., ' ', '%20')" />
									.
									<xsl:value-of select="$suffix" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<!-- PATH(-SUFFIX) AND FILENAME without uri-encoded for display -->
				<xsl:variable name="path">
					<xsl:choose>
						<xsl:when
							test="$Flavor = 'MPIMP' or $Flavor = 'MPIMPExt' or $Flavor = 'CAESAR'">
							<xsl:value-of
								select="fn:replace(concat(substring-before(., '/'), '/'), ' ', '%20')" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="''" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="filename">
					<xsl:choose>
						<xsl:when test="contains(., '.')">
							<xsl:choose>
								<xsl:when
									test="$Flavor = 'MPIMP' or $Flavor = 'MPIMPExt' or $Flavor = 'CAESAR'">
									<xsl:value-of
										select="fn:replace(substring-after(., '/'), ' ', '%20')" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="fn:replace(., ' ', '%20')" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when
									test="$Flavor = 'MPIMP' or $Flavor = 'MPIMPExt'">
									<xsl:value-of
										select="fn:replace(substring-after(., '/'), ' ', '%20')" />
									.
									<xsl:value-of select="$suffix" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="fn:replace(., ' ', '%20')" />
									.
									<xsl:value-of select="$suffix" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="mimetype">
					<xsl:value-of select="Util:getMimetype($filename)" />
				</xsl:variable>
				<escidocComponents:component>
					<escidocComponents:properties
						xmlns:xlink="http://www.w3.org/1999/xlink">
						<prop:visibility>
							<xsl:choose>
								<xsl:when test="not($Flavor = 'CAESAR') and $oa">
									public
								</xsl:when>
								<xsl:when
									test="not($oa) and $Flavor = 'MPIMP' or $Flavor = 'MPIMPExt'">
									audience
								</xsl:when>
								<xsl:when test="$Flavor = 'CAESAR'">
									private
								</xsl:when>
								<xsl:otherwise>
									private
								</xsl:otherwise>
							</xsl:choose>
						</prop:visibility>
						<prop:content-category>
							<xsl:value-of
								select="$contentCategory-ves/enum[.='any-fulltext']" />
						</prop:content-category>
						<prop:file-name>
							<xsl:value-of select="$filename" />
						</prop:file-name>
						<prop:mime-type>
							<xsl:value-of select="$mimetype" />
						</prop:mime-type>
					</escidocComponents:properties>
					<escidocComponents:content
						xlink:type="simple" xlink:title="{$filename}"
						xlink:href="{$fulltext-location}{$path}{$filename}"
						storage="internal-managed" />
					<mdr:md-records
						xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.5">
						<mdr:md-record name="escidoc">
							<file:file
								xmlns:file="http://purl.org/escidoc/metadata/profiles/0.1/file"
								xmlns:dc="http://purl.org/dc/elements/1.1/"
								xmlns:dcterms="http://purl.org/dc/terms/"
								xmlns:e="http://purl.org/escidoc/metadata/terms/0.1/"
								xmlns:eidt="http://purl.org/escidoc/metadata/terms/0.1/idtypes/"
								xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
								<dc:title>
									<xsl:value-of
										select="fn:replace($filename, '%20', ' ')" />
								</dc:title>
								<dc:format xsi:type="dcterms:IMT">
									<xsl:value-of select="$mimetype" />
								</dc:format>
								<dcterms:extent>
									<xsl:value-of
										select="Util:getSize(concat($fulltext-location, $path, $filename))" />
								</dcterms:extent>
							</file:file>
						</mdr:md-record>
					</mdr:md-records>
				</escidocComponents:component>
			</xsl:when>
			<xsl:otherwise>
				<escidocComponents:component>
					<escidocComponents:properties
						xmlns:xlink="http://www.w3.org/1999/xlink">
						<prop:visibility>public</prop:visibility>
						<prop:content-category>
							<xsl:choose>
								<xsl:when
									test="$Flavor='MPIGEM' and contains(., 'http://www.coll.mpg.de/pdf_dat')">
									<xsl:value-of
										select="$contentCategory-ves/enum[.='pre-print']" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of
										select="$contentCategory-ves/enum[.='any-fulltext']" />
								</xsl:otherwise>
							</xsl:choose>
						</prop:content-category>
						<prop:file-name>
							<xsl:value-of select="normalize-space(.)" />
						</prop:file-name>
					</escidocComponents:properties>
					<escidocComponents:content
						xlink:type="simple" xlink:title="{normalize-space(.)}"
						xlink:href="{normalize-space(.)}" storage="external-url" />
					<mdr:md-records
						xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.5">
						<mdr:md-record name="escidoc">
							<file:file
								xmlns:file="http://purl.org/escidoc/metadata/profiles/0.1/file"
								xmlns:dc="http://purl.org/dc/elements/1.1/"
								xmlns:dcterms="http://purl.org/dc/terms/"
								xmlns:e="http://purl.org/escidoc/metadata/terms/0.1/"
								xmlns:eidt="http://purl.org/escidoc/metadata/terms/0.1/idtypes/"
								xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
								<dc:title>
									<xsl:value-of select="normalize-space(.)" />
								</dc:title>
							</file:file>
						</mdr:md-record>
					</mdr:md-records>
				</escidocComponents:component>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- FUNCTIONS -->
	<xsl:function name="escidocFunctions:get-part">
		<xsl:param name="text" />
		<xsl:param name="delimiter" />
		<xsl:param name="pos" />
		<xsl:choose>
			<xsl:when
				test="$pos &gt; 1 and not(contains($text, $delimiter))">
				<xsl:value-of
					select="error(QName('http://www.escidoc.de', 'err:MatchingStringPartNotFound' ), concat('Unable to find part ', $pos, ' in ~', $text, '~ split by ~', $delimiter, '~.'))" />
			</xsl:when>
			<xsl:when test="$pos &gt; 1">
				<xsl:value-of
					select="escidocFunctions:get-part(substring-after($text, $delimiter), $delimiter, $pos - 1)" />
			</xsl:when>
			<xsl:when test="contains($text, $delimiter)">
				<xsl:value-of
					select="substring-before($text, $delimiter)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<!-- see http://colab.mpdl.mpg.de/mediawiki/Talk:PubMan_Func_Spec_Endnote_Mapping#Date_proper_formats -->
	<xsl:function name="escidocFunctions:normalizeDate">
		<xsl:param name="d" />
		<xsl:variable name="d"
			select="replace(replace(normalize-space($d), '^\s+', ''), '\s+$', '')" />
		<xsl:variable name="nd" as="item()*">
			<xsl:choose>
				<xsl:when test="matches($d,'^\d{1,2}\\\d{1,2}\\\d{4}$')">
					<xsl:variable name="dmy" select="tokenize($d, '\\')" />
					<xsl:copy-of
						select="$dmy[3], escidocFunctions:add0($dmy[2]), escidocFunctions:add0($dmy[1])" />
				</xsl:when>
				<xsl:when test="matches($d,'^\d{4}\\\d{1,2}\\\d{1,2}$')">
					<xsl:variable name="dmy" select="tokenize($d, '\\')" />
					<xsl:copy-of
						select="$dmy[1], escidocFunctions:add0($dmy[2]), escidocFunctions:add0($dmy[3])" />
				</xsl:when>
				<xsl:when test="matches($d,'^\d{4}\\\d{1,2}$')">
					<xsl:variable name="dmy" select="tokenize($d, '\\')" />
					<xsl:copy-of
						select="$dmy[1], escidocFunctions:add0($dmy[2])" />
				</xsl:when>
				<xsl:when test="matches($d,'^\d{1,2}/\d{1,2}/\d{4}$')">
					<xsl:variable name="dmy" select="tokenize($d, '/')" />
					<xsl:copy-of
						select="$dmy[3], escidocFunctions:add0($dmy[2]), escidocFunctions:add0($dmy[1])" />
				</xsl:when>
				<xsl:when test="matches($d,'^\d{4}/\d{1,2}/\d{1,2}$')">
					<xsl:variable name="dmy" select="tokenize($d, '/')" />
					<xsl:copy-of
						select="$dmy[1], escidocFunctions:add0($dmy[2]), escidocFunctions:add0($dmy[3])" />
				</xsl:when>
				<xsl:when test="matches($d,'^\d{4}/\d{1,2}$')">
					<xsl:variable name="dmy" select="tokenize($d, '/')" />
					<xsl:copy-of
						select="$dmy[1], escidocFunctions:add0($dmy[2])" />
				</xsl:when>
				<xsl:when
					test="matches($d,'^\d{1,2}[-.]\d{1,2}[-.]\d{4}$')">
					<xsl:variable name="dmy" select="tokenize($d, '[-.]')" />
					<xsl:copy-of
						select="$dmy[3], escidocFunctions:add0($dmy[2]), escidocFunctions:add0($dmy[1])" />
				</xsl:when>
				<xsl:when
					test="matches($d,'^\d{4}[-.]\d{1,2}[-.]\d{1,2}$')">
					<xsl:variable name="dmy" select="tokenize($d, '[-.]')" />
					<xsl:copy-of
						select="$dmy[1], escidocFunctions:add0($dmy[2]), escidocFunctions:add0($dmy[3])" />
				</xsl:when>
				<xsl:when test="matches($d,'^\d{4}[-.]\d{1,2}$')">
					<xsl:variable name="dmy" select="tokenize($d, '[-.]')" />
					<xsl:copy-of
						select="$dmy[1], escidocFunctions:add0($dmy[2])" />
				</xsl:when>
				<xsl:when test="matches($d,'^\d{4}$')">
					<xsl:copy-of select="$d" />
				</xsl:when>
				<xsl:when
					test="matches($d,'^\w{3,}\s+\d{1,2}\s*,\s*\d{4}$')">
					<xsl:analyze-string
						regex="(\w{{3,}})\s+(\d{{1,2}})\s*,\s*(\d{{4}})" select="$d">
						<xsl:matching-substring>
							<xsl:variable name="m"
								select="escidocFunctions:getMonthNum(regex-group(1))" />
							<xsl:copy-of
								select="  if ($m!='') then   (  regex-group(3),  $m,  escidocFunctions:add0(regex-group(2))  )  else ()   " />
						</xsl:matching-substring>
					</xsl:analyze-string>
				</xsl:when>
				<xsl:when test="matches($d,'^\w{3,}\s+\d{4}$')">
					<xsl:analyze-string
						regex="(\w{{3,}})\s+(\d{{4}})" select="$d">
						<xsl:matching-substring>
							<xsl:variable name="m"
								select="escidocFunctions:getMonthNum(regex-group(1))" />
							<xsl:copy-of
								select="  if ($m!='') then   (  regex-group(2),  $m  )  else ()   " />
						</xsl:matching-substring>
					</xsl:analyze-string>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="()" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- <xsl:message select=" -->
		<!-- concat('I am here, inp:', $d, ', out:', if (exists($nd)) then string-join( 
			$nd[.!=''], '-' ) else '' ) -->
		<!-- "/> -->
		<xsl:value-of
			select="  if (exists($nd)) then string-join( $nd[.!=''], '-' ) else ''  " />
	</xsl:function>
	<xsl:function name="escidocFunctions:getMonthNum">
		<xsl:param name="m" />
		<xsl:variable name="m"
			select="lower-case(substring($m, 1, 3))" />
		<xsl:value-of
			select="  if ($m='jan') then '01' else  if ($m='feb') then '02' else  if ($m='mar') then '03' else  if ($m='apr') then '04' else  if ($m='may') then '05' else  if ($m='jun') then '06' else  if ($m='jul') then '07' else  if ($m='aug') then '08' else  if ($m='sep') then '09' else  if ($m='oct') then '10' else  if ($m='nov') then '11' else  if ($m='dec') then '12' else  ''  " />
	</xsl:function>
	<xsl:function name="escidocFunctions:add0">
		<xsl:param name="d" />
		<xsl:value-of
			select="if (string-length($d)=1) then concat('0', $d) else $d" />
	</xsl:function>
	<xsl:function name="escidocFunctions:normalizeDegree">
		<xsl:param name="d" />
		<xsl:value-of
			select="lower-case(replace($d, '[.\s]+', ''))" />
	</xsl:function>
	<xsl:function name="escidocFunctions:smaller"
		as="xs:boolean">
		<xsl:param name="value1" />
		<xsl:param name="value2" />
		<xsl:choose>
			<xsl:when test="not(exists($value1)) or $value1 = ''">
				<xsl:value-of select="true()" />
			</xsl:when>
			<xsl:when test="not(exists($value2)) or $value2 = ''">
				<xsl:value-of select="true()" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="date1"
					select="substring(concat($value1, '-01-01'), 1, 10)" />
				<xsl:variable name="date2"
					select="substring(concat($value2, '-ZZ-ZZ'), 1, 10)" />
				<xsl:value-of select="compare($date1, $date2) != 1" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
</xsl:stylesheet>