<?xml version="1.0" encoding="UTF-8"?>
<!--
 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.de/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<!-- 
	Transformations from eSciDoc PubItem Schema to Highwire citation tags (for Google Scholar indexing)
	Author: Markus Haarländer (initial creation) 
	$Author: MWalter $ (last changed)
	$Revision: $ 
	$LastChangedDate: $
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:escidoc="${xsd.metadata.escidocprofile.types}"
	xmlns:func="urn:my-functions" 
	xmlns:dc="${xsd.metadata.dc}"
	xmlns:dcterms="${xsd.metadata.dcterms}"
	xmlns:ei="${xsd.soap.item.item}"
	xmlns:mdr="${xsd.soap.common.mdrecords}"
	xmlns:mdp="${xsd.metadata.escidocprofile}"
	xmlns:e="${xsd.metadata.escidocprofile.types}"
	xmlns:ec="${xsd.soap.item.components}"
	xmlns:prop="${xsd.soap.common.prop}"
	xmlns:pub="${xsd.metadata.publication}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:eterms="${xsd.metadata.terms}"
	xmlns:escidocComponents="${xsd.soap.item.components}"
	xmlns:escidocItem="${xsd.soap.item.item}">
	


	<xsl:import href="escidoc-publication-item2html-meta-tags.xsl"/>
	
	<xsl:variable name="key-title" select="'DC.title'" />
	<xsl:variable name="key-author" select="'DC.creator'" />
	<xsl:variable name="key-author-affiliation" select="''" />
	<xsl:variable name="key-author-institution" select="'DC.creator'" />
	<xsl:variable name="key-publication-date" select="'DC.issued'" />
	<xsl:variable name="key-pdf-url" select="'DC.identifier'" />
	<xsl:variable name="key-language" select="'DC.language'" />
	<xsl:variable name="key-fulltext-html-url" select="'DC.identifier'" />
	
	<xsl:variable name="key-doi" select="'DC.identifier'" />
	<xsl:variable name="prefix-doi" select="'http://dx.doi.org/'" />
	
	<xsl:variable name="key-arxiv-id" select="''" />
	<xsl:variable name="prefix-arxiv-id" select="''" />
	
	<xsl:variable name="key-pmid" select="''" />
	<xsl:variable name="prefix-pmid" select="''" />
	
	<xsl:variable name="key-isbn" select="'DC.identifier'" />
	<xsl:variable name="prefix-isbn" select="'urn:ISBN:'" />
	
	<xsl:variable name="key-keywords" select="'DC.subject'" />
	<xsl:variable name="key-conference" select="'DC.relation.ispartof'" />
	<xsl:variable name="key-dissertation-institution" select="'DC.publisher'" />
	<xsl:variable name="key-journal-title" select="'DC.relation.ispartof'" />
	<xsl:variable name="key-journal-abbrev" select="''" />
	<xsl:variable name="key-volume" select="'DC.citation.volume'" />
	<xsl:variable name="key-issue" select="'DC.citation.issue'" />
	<xsl:variable name="key-firstpage" select="'DC.citation.spage'" />
	<xsl:variable name="key-lastpage" select="'DC.citation.epage'" />
	<xsl:variable name="key-publisher" select="'DC.publisher'" />
	
	<xsl:variable name="key-issn" select="'DC.identifier'" />
	<xsl:variable name="prefix-issn" select="'urn:ISSN:'" />

</xsl:stylesheet>