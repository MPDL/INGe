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


 Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<!-- 
	Transformations from eDoc Item to eSciDoc PubItem 
	Author: Julia Kurt (initial creation) 
	$Author: kurt $ (last changed)
	$Revision: 747 $ 
	$LastChangedDate: 2008-07-21 19:15:26 +0200 (Mo, 21 Jul 2008) $
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:ei="${xsd.soap.item.item}" xmlns:mdr="${xsd.soap.common.mdrecords}" xmlns:mdp="${xsd.metadata.escidocprofile}" xmlns:ec="${xsd.soap.item.components}" xmlns:prop="${xsd.soap.common.prop}" xmlns:srel="${xsd.soap.common.srel}" xmlns:version="${xsd.soap.common.version}" xmlns:release="${xsd.soap.common.release}" xmlns:file="${xsd.metadata.file}" xmlns:pub="${xsd.metadata.publication}" xmlns:person="${xsd.metadata.person}" xmlns:source="${xsd.metadata.source}" xmlns:eterms="${xsd.metadata.terms}" xmlns:event="${xsd.metadata.event}" xmlns:organization="${xsd.metadata.organization}" xmlns:escidocFunctions="urn:escidoc:functions" xmlns:escidoc="${xsd.metadata.terms}" xmlns:Util="java:de.mpg.escidoc.services.transformation.Util" xmlns:itemlist="${xsd.soap.item.itemlist}" xmlns:eprints="http://purl.org/eprint/terms/">
	
	<xsl:import href="../../vocabulary-mappings.xsl"/>
	
	<xsl:variable name="bpc-files">
		<!--  <xsl:value-of select="document('https://zim01.gwdg.de/repos/smc/tags/public/Migration/edoc_pdfs.txt')"/>-->
		<xsl:value-of select="document('edoc_pdfs.txt')"/>
	</xsl:variable>
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="is-item-list" select="true()"/>
	
	<xsl:param name="useAuthorList" select="false()"/>
	<xsl:param name="removeSpacesInInitials" select="false()"/>
	<xsl:param name="createLocatorsForPublicComponents" select="false()"/>
	
	<xsl:param name="user" select="'dummy-user'"/>
	<xsl:param name="context" select="'dummy-context'"/>
	<xsl:param name="external-ou"/>
	<xsl:param name="root-ou" select="'dummy-root-ou'"/>
	<xsl:param name="source-name" select="'eDoc'"/>
	
	<!-- Configuration parameters -->
	<xsl:param name="import-name" select="'OTHER'"/>
	<xsl:param name="CoNE" select="'true'"/>
	
	<xsl:param name="content-model" select="'dummy-content-model'"/>

	<xsl:variable name="dependentGenre">
		<type>article</type>
		<type>conference-paper</type>
		<type>conference-report</type>
		<type>book-item</type>
		<type>issue</type>
		<type>paper</type>
		<type>poster</type>
		<type>talk-at-event</type>
		<type>contribution-to-handbook</type>
		<type>contribution-to-encyclopedia</type>
		<type>contribution-to-festschrift</type>
		<type>contribution-to-commentary</type>
		<type>contribution-to-collected-edition</type>
		<type>editorial</type>
		<type>newspaper-article</type>
		<type>book-review</type>
		<type>opinion</type>
		<type>commentary</type>
	</xsl:variable>
	
	<xsl:variable name="collection-mapping">
		
		<mapping ou="AEI">
			<edoc-collection>MPI für Gravitationsphysik</edoc-collection>
			<escidoc-ou>MPI for Gravitational Physics, Max Planck Society</escidoc-ou>
			<escidoc-id>escidoc:24007</escidoc-id>
		</mapping>
		<mapping ou="AEI-Golm">
			<edoc-collection>Geometric Analysis and Gravitation</edoc-collection>
			<escidoc-ou>Geometric Analysis and Gravitation, AEI-Golm, MPI for Gravitational Physics, Max Planck Society</escidoc-ou>
			<escidoc-id>escidoc:24012</escidoc-id>
		</mapping>
		<mapping ou="AEI-Golm">
			<edoc-collection>GEO 600</edoc-collection>
			<escidoc-ou>AEI-Hannover, MPI for Gravitational Physics, Max Planck Society</escidoc-ou>
			<escidoc-id>escidoc:24009</escidoc-id>
		</mapping>
		<mapping ou="AEI-Golm">
			<edoc-collection>Astrophysical Relativity</edoc-collection>
			<escidoc-ou>Astrophysical Relativity, AEI-Golm, MPI for Gravitational Physics, Max Planck Society</escidoc-ou>
			<escidoc-id>escidoc:24013</escidoc-id>
		</mapping>
		<mapping ou="AEI-Golm">
			<edoc-collection>Quantum Gravity and Unified Theories</edoc-collection>
			<escidoc-ou>Quantum Gravity &amp; Unified Theories, AEI-Golm, MPI for Gravitational Physics, Max Planck Society</escidoc-ou>
			<escidoc-id>escidoc:24014</escidoc-id>
		</mapping>
		<mapping ou="AEI-Golm">
			<edoc-collection>Teilinstitut Hannover</edoc-collection>
			<escidoc-ou>AEI-Hannover, MPI for Gravitational Physics, Max Planck Society</escidoc-ou>
			<escidoc-id>escidoc:24009</escidoc-id>
		</mapping>
		<mapping ou="AEI-Golm">
			<edoc-collection>Cactus group</edoc-collection>
			<escidoc-ou>Cactus Group, AEI-Golm, MPI for Gravitational Physics, Max Planck Society</escidoc-ou>
			<escidoc-id>escidoc:67202</escidoc-id>
		</mapping>
		<mapping ou="AEI-Golm">
			<edoc-collection>Laser Interferometry &amp; Gravitational Wave Astronomy</edoc-collection>
			<escidoc-ou>Laser Interferometry &amp; Gravitational Wave Astronomy, AEI-Hannover, MPI for Gravitational Physics, Max Planck Society</escidoc-ou>
			<escidoc-id>escidoc:24010</escidoc-id>
		</mapping>
		<mapping ou="AEI-Golm">
			<edoc-collection>Observational Relativity and Cosmology</edoc-collection>
			<escidoc-ou>Observational Relativity and Cosmology, AEI-Hannover, MPI for Gravitational Physics, Max Planck Society</escidoc-ou>
			<escidoc-id>escidoc:24011</escidoc-id>
		</mapping>
		<mapping ou="AEI-Golm">
			<edoc-collection>Microscopic Quantum Structure &amp; Dynamics of Spacetime</edoc-collection>
			<escidoc-ou>Microscopic Quantum Structure &amp; Dynamics of Spacetime, AEI-Golm, MPI for Gravitational Physics, Max Planck Society</escidoc-ou>
			<escidoc-id>escidoc:67201</escidoc-id>
		</mapping>
		<mapping ou="AEI-Golm">
			<edoc-collection>Duality &amp; Integrable Structures</edoc-collection>
			<escidoc-ou>Duality &amp; Integrable Structures, AEI-Golm, MPI for Gravitational Physics, Max Planck Society</escidoc-ou>
			<escidoc-id>escidoc:24016</escidoc-id>
		</mapping>
		<mapping ou="AEI-Golm">
			<edoc-collection>Theoretical Gravitational Wave Physics</edoc-collection>
			<escidoc-ou>Theoretical Gravitational Wave Physics, AEI-Golm, MPI for Gravitational Physics, Max Planck Society</escidoc-ou>
			<escidoc-id>escidoc:24015</escidoc-id>
		</mapping>
		<mapping ou="AEI-Golm">
			<edoc-collection>IT Department</edoc-collection>
			<escidoc-ou>IT Department, AEI-Golm, MPI for Gravitational Physics, Max Planck Society</escidoc-ou>
			<escidoc-id>escidoc:67203</escidoc-id>
		</mapping>
		<mapping ou="AEI-Golm">
			<edoc-collection>Canonical and Covariant Dynamics of Quantum Gravity</edoc-collection>
			<escidoc-ou>Canonical and Covariant Dynamics of Quantum Gravity, AEI-Golm, MPI for Gravitational Physics, Max Planck Society</escidoc-ou>
			<escidoc-id>escidoc:102878</escidoc-id>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Language Acquisition</edoc-collection>
			<escidoc-ou>Language Acquisition Group</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Acquisition Group</edoc-collection>
			<escidoc-ou>Language Acquisition Group</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Adaptive Listening</edoc-collection>
			<escidoc-ou>Adaptive Listening</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Language and Cognition Group</edoc-collection>
			<escidoc-ou>Categories across Language and Cognition</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Categories across Language and Cognition</edoc-collection>
			<escidoc-ou>Categories across Language and Cognition</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Communication Before Language</edoc-collection>
			<escidoc-ou>Communication before Language</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Comparative Cognitive Anthropology</edoc-collection>
			<escidoc-ou>Comparative Cognitive Anthropology</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Decoding Continuous Speech</edoc-collection>
			<escidoc-ou>Decoding Continuous Speech</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Event Representation</edoc-collection>
			<escidoc-ou>Event Representation</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Evolutionary Processes in Language and Culture</edoc-collection>
			<escidoc-ou>Evolutionary Processes in Language and Culture</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Gesture</edoc-collection>
			<escidoc-ou>Gesture</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Information Structure in Language Acquisition</edoc-collection>
			<escidoc-ou>Information Structure in Language Acquisition</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Language and Genetics</edoc-collection>
			<escidoc-ou>Language and Genetics</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Language in Action</edoc-collection>
			<escidoc-ou>Language in Action</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Island Melanesia</edoc-collection>
			<escidoc-ou>Pioneers of Island Melanesia</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Multimodal Interaction</edoc-collection>
			<escidoc-ou>Multimodal Interaction</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Neurocognition of Language Processing</edoc-collection>
			<escidoc-ou>Neurocognition of Language Processing</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Other Research</edoc-collection>
			<escidoc-ou>Other Research</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Phonological Learning for Speech Perception</edoc-collection>
			<escidoc-ou>Phonological Learning for Speech Perception</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Sign Language Typology</edoc-collection>
			<escidoc-ou>Sign Language Typology</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Space</edoc-collection>
			<escidoc-ou>Space</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Technical Group</edoc-collection>
			<escidoc-ou>Technical Group</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>The Comparative Study of L2 Acquisition</edoc-collection>
			<escidoc-ou>The Comparative Study of L2 Acquisition</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>The Dynamics of Learner Varieties</edoc-collection>
			<escidoc-ou>The Dynamics of Learner Varieties</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>The Dynamics of Multilingual Processing</edoc-collection>
			<escidoc-ou>The Dynamics of Multilingual Processing</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>The Neurobiology of Language</edoc-collection>
			<escidoc-ou>The Neurobiology of Language</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Unification</edoc-collection>
			<escidoc-ou>Unification</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>The Role of Finiteness</edoc-collection>
			<escidoc-ou>The Role of Finiteness</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Utterance Encoding</edoc-collection>
			<escidoc-ou>Utterance Encoding</escidoc-ou>
		</mapping>
		<mapping ou="MPIPL">
			<edoc-collection>Utterance Encoding</edoc-collection>
			<escidoc-ou>Utterance Encoding</escidoc-ou>
		</mapping>
	</xsl:variable>
	
	<xsl:variable name="genre-mapping">
		<genres>
			<genre type="mpik">
				<edoc-genre>Proceedings</edoc-genre>
				<pubman-genre>publisher-version</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>proceedings</edoc-genre>
				<pubman-genre>publisher-version</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>preprint version</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>preprint version of the paper</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Preprint version of the paper</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Preprint version of this paper</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Prprint version of this paper</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>preliminary version</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>draft version of proceedings</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>draft of proceedings</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>preprint version of proceedings</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>full text (preliminary version)</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Full text</edoc-genre>
				<pubman-genre>publisher-version</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Full text</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>full text</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Full-text</edoc-genre>
				<pubman-genre>publisher-version</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Full-text version</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Full text version</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Fulltext version</edoc-genre>
				<pubman-genre>publisher-version</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Full text version of this article</edoc-genre>
				<pubman-genre>publisher-version</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Fullt text version</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Full-text of the Ph.D thesis</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Full text of PhD-Thesis</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Full text of this PhD-Thesis</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Full text of the conference paper</edoc-genre>
				<pubman-genre>publisher-version</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>full text available</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Full text available</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Full text pdf-version</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Full text PostScript</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Full text</edoc-genre>
				<pubman-genre>Post-Print</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Full text pdf version</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>PDF: Fulltext version of the article</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>PDF file of proceedings</edoc-genre>
				<pubman-genre>publisher-version</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>Full text version of this conference paper</edoc-genre>
				<pubman-genre>publisher-version</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>lanl.arXiv.org e-Print archive</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>PostScript: Fulltext version of the article</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>JPEG of the telescope</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>High resolution photo of the participants of the Symposium on Cosmic Rays" 1939"</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>JPEG-picture of H.E.S.S. telescopes</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>slides shown at the conference</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="mpik">
				<edoc-genre>slides shown at the conference and published on CD</edoc-genre>
				<pubman-genre>any-fulltext </pubman-genre>
			</genre>
			
			<genre type="MPIGF">
				<edoc-genre>Abstract</edoc-genre>
				<pubman-genre>abstract</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>audio file</edoc-genre>
				<pubman-genre>supplementary-material</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>Draft</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>Einleitung und Inhaltsverzeichnis</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>First print: full text</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>full text</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>full text  Springer online open</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>full text : read only</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>full text First Print</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>full text Gewerkschaftliche Monatshefte</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>full text im Nachdruck</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>full text vol. 1</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>full text vol. 2</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>full text: Vortrag</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>full texts</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>fulltesxt</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>Fulltext</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>index</edoc-genre>
				<pubman-genre>table-of-contents</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>Inhalt und Einleitung</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>Inhalt und Vorwort</edoc-genre>
				<pubman-genre>table-of-contents</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>Inhaltsverzeichnis</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>Introduction</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>Manuskript</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>pdf pre-print</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>Pre-print</edoc-genre>
				<pubman-genre>pre-print</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>Reading sample</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>Table of content</edoc-genre>
				<pubman-genre>table-of-contents</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>table-of-contents</edoc-genre>
				<pubman-genre>table-of-contents</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>ToC</edoc-genre>
				<pubman-genre>table-of-contents</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>ToC im Nachdruck</edoc-genre>
				<pubman-genre>table-of-contents</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>Volltext</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>Volltext mit freundlicher Genehmigung des Hampp-Verlages</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="MPIGF">
				<edoc-genre>Volltext Vortrag</edoc-genre>
				<pubman-genre>any-fulltext</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Article</edoc-genre>
				<pubman-genre>article</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Book</edoc-genre>
				<pubman-genre>book</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Conference-Paper</edoc-genre>
				<pubman-genre>conference-paper</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Conference-Report</edoc-genre>
				<pubman-genre>conference-report</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Habilitation</edoc-genre>
				<pubman-genre>thesis</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>InBook</edoc-genre>
				<pubman-genre>book-item</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Issue</edoc-genre>
				<pubman-genre>issue</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Interactive Resource</edoc-genre>
				<pubman-genre>other</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Journal</edoc-genre>
				<pubman-genre>journal</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Lecture / Courseware</edoc-genre>
				<pubman-genre>courseware-lecture</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Other</edoc-genre>
				<pubman-genre>other</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Paper</edoc-genre>
				<pubman-genre>paper</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>PhD-Thesis</edoc-genre>
				<pubman-genre>thesis</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Poster</edoc-genre>
				<pubman-genre>poster</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Proceedings</edoc-genre>
				<pubman-genre>proceedings</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Series</edoc-genre>
				<pubman-genre>series</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Software</edoc-genre>
				<pubman-genre>software</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Talk at Event</edoc-genre>
				<pubman-genre>talk-at-event</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Thesis</edoc-genre>
				<pubman-genre>thesis</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Booklet</edoc-genre>
				<pubman-genre>other</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>book-review</edoc-genre>
				<pubman-genre>book-review</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>catalogue-article</edoc-genre>
				<pubman-genre>article</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Catalogue-entry</edoc-genre>
				<pubman-genre>Other</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>collection-article</edoc-genre>
				<pubman-genre>contribution-to-collected-edition</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>newspaper-article</edoc-genre>
				<pubman-genre>newspaper-article</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>contribution-to-encyclopedia</edoc-genre>
				<pubman-genre>contribution-to-encyclopedia</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>festschrift-article</edoc-genre>
				<pubman-genre>contribution-to-festschrift</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>online-article</edoc-genre>
				<pubman-genre>article</pubman-genre>
			</genre>
			<genre type="default">
				<edoc-genre>Thesis</edoc-genre>
				<pubman-genre>thesis</pubman-genre>
			</genre>			
		</genres>
	</xsl:variable>
	
	<xsl:variable name="evolbio-author-comment-exceptions">
		<edoc>
			<id>65099</id>
			<id>65100</id>
			<id>225770</id>
			<id>225790</id>
			<id>230557</id>
			<id>236429</id>
			<id>241176</id>
			<id>241177</id>
			<id>246861</id>
			<id>250731</id>
			<id>250732</id>
			<id>255734</id>
			<id>256554</id>
			<id>260983</id>
			<id>261137</id>
			<id>269957</id>
			<id>275435</id>
			<id>275660</id>
			<id>276986</id>
			<id>277561</id>
			<id>277564</id>
			<id>277565</id>
			<id>281439</id>
			<id>281746</id>
			<id>282184</id>
			<id>282340</id>
			<id>282387</id>
			<id>284832</id>
			<id>284867</id>
			<id>284954</id>
			<id>284976</id>
			<id>285103</id>
			<id>285660</id>
			<id>285848</id>
			<id>285906</id>
			<id>285907</id>
			<id>285908</id>
			<id>285909</id>
			<id>285910</id>
			<id>285914</id>
			<id>285991</id>
			<id>287322</id>
			<id>287331</id>
			<id>287357</id>
			<id>442360</id>
			<id>442366</id>
		</edoc>
	</xsl:variable>
	
	<xsl:variable name="evolbio-copyright-exceptions">
		<edoc>
			<id>35332</id>
			<id>219656</id>
			<id>219661</id>
			<id>219666</id>
			<id>219667</id>
			<id>219668</id>
			<id>219670</id>
			<id>233550</id>
			<id>233553</id>
			<id>233555</id>
			<id>237825</id>
			<id>278038</id>
			<id>278043</id>
			<id>278047</id>
			<id>281416</id>
			<id>281417</id>
			<id>281418</id>
			<id>281419</id>
			<id>281420</id>
			<id>281421</id>
			<id>281425</id>
			<id>281427</id>
			<id>281888</id>
			<id>281891</id>
			<id>281894</id>
			<id>281901</id>
			<id>281908</id>
			<id>281914</id>
			<id>281918</id>
			<id>281922</id>
			<id>281972</id>
			<id>281973</id>
			<id>281974</id>
			<id>281975</id>
			<id>281978</id>
			<id>282170</id>
			<id>282184</id>
			<id>282340</id>
			<id>282358</id>
			<id>282361</id>
			<id>282364</id>
			<id>282368</id>
			<id>282369</id>
			<id>282370</id>
			<id>282371</id>
			<id>282373</id>
			<id>282380</id>
			<id>284825</id>
			<id>284826</id>
			<id>284827</id>
			<id>284828</id>
			<id>284829</id>
			<id>284830</id>
			<id>284832</id>
			<id>284860</id>
			<id>284861</id>
			<id>284863</id>
			<id>284864</id>
			<id>284865</id>
			<id>284867</id>
			<id>285106</id>
			<id>287322</id>
			<id>287331</id>
			<id>287343</id>
			<id>287357</id>
			<id>289963</id>
			<id>474956</id>
			<id>475487</id>
			<id>498979</id>
		</edoc>
	</xsl:variable>
	
	<xsl:variable name="evolbio-file-comment-exceptions">
		<edoc>
			<id>210673</id>
			<id>237825</id>
			<id>282184</id>
			<id>284832</id>
			<id>284867</id>
			<id>287322</id>
			<id>287357</id>
			<id>475487</id>
		</edoc>
	</xsl:variable>
	
	<xsl:variable name="mpiis-comments">
		<comment>pp</comment>
		<comment>pp.</comment>
		<comment>pages</comment>
		<comment>(pp)</comment>
		<comment>(pp.)</comment>
		<comment>(pages)</comment>
	</xsl:variable>
	
	<xsl:variable name="authors">
		<authors/>
	</xsl:variable>
	
	<xsl:variable name="organizational-units">
		<organizational-units>
			<ou name="root" id="{$root-ou}"/>
			<ou name="external" id="{$external-ou}"/>
		</organizational-units>
	</xsl:variable>
	
	<xsl:variable name="mpiis-subjects">
		<subject>ZWE EDV</subject>
		<subject>Nanofluidics</subject>
		<subject>Wetting and Capillarity</subject>
		<subject>Entropic Forces</subject>
		<subject>Critical Phenomena</subject>
		<subject>Collective Dynamics</subject>
		<subject>Soft Matter at Interfaces</subject>
		<subject>Morphometry</subject>
		<subject>Miscellaneous</subject>
	</xsl:variable>
	<xsl:function name="escidocFunctions:ou-name">
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
					<xsl:value-of select="escidocFunctions:ou-name($organizational-units//ou[@name = $name or @alias = $name]/../@name)"/>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'External Organizations'"/>
			</xsl:otherwise>
		</xsl:choose>
	
	</xsl:function>
	
	<xsl:function name="escidocFunctions:ou-id">
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
	
	<xsl:template match="/*">
		<!-- <xsl:call-template name="validation"/> -->
		<xsl:choose>
			<xsl:when test="$is-item-list">
				<itemlist:item-list>
					<xsl:apply-templates select="//record/metadata"/>
				</itemlist:item-list>
			</xsl:when>
			<xsl:when test="count(//record/metadata) = 1">
				<xsl:apply-templates select="//record/metadata"/>
			</xsl:when>
			<xsl:when test="count(//record/metadata) = 0">
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:NoSourceForSingleTarget' ), 'Single item was selected as target, but the source contained no items')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:MultipleSourceForSingleTarget' ), 'Single item was selected as target, but the source contained multiple items')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="record/metadata">
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<xsl:element name="srel:context">
					<xsl:attribute name="objid" select="$context"/>
				</xsl:element>
				<srel:content-model objid="{$content-model}"/>
				<xsl:element name="prop:content-model-specific">
					<xsl:choose>
						<xsl:when test="../MPGyearbook = '2009'">
							<local-tags>
								<local-tag>Yearbook 2009</local-tag>
							</local-tags>
						</xsl:when>
						<xsl:when test="../MPGyearbook = '2010'">
							<local-tags>
								<local-tag>Yearbook 2010</local-tag>
							</local-tags>
						</xsl:when>
						<xsl:when test="$import-name = 'CBS'">
							<xsl:call-template name="authorcommentCBS"/>
						</xsl:when>
						<xsl:when test="$import-name = 'BiblHertz'">
							<xsl:call-template name="localTags"/>
						</xsl:when>
					</xsl:choose>
				</xsl:element>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:apply-templates select="basic"/>
				</mdr:md-record>
			</xsl:element>
			<xsl:element name="ec:components">
				<xsl:for-each select="basic/fturl">					
					<!-- duplicate filenames -->
					<xsl:variable name="filename" select="@filename"/>
					<xsl:choose>
						<xsl:when test="not(preceding-sibling::fturl/@filename = $filename)">
							<xsl:variable name="access">
								<xsl:choose>
									<xsl:when test="following-sibling::fturl[@filename=$filename]/@viewftext='USER' or @viewftext='USER'">USER</xsl:when>
									<xsl:when test="following-sibling::fturl[@filename=$filename]/@viewftext='INSTITUT' or @viewftext='INSTITUT'">INSTITUT</xsl:when>
									<xsl:when test="following-sibling::fturl[@filename=$filename]/@viewftext='MPG' or @viewftext='MPG'">MPG</xsl:when>
									<xsl:when test="following-sibling::fturl[@filename=$filename]/@viewftext='PUBLIC' or @viewftext='PUBLIC'">PUBLIC</xsl:when>
									<xsl:when test="following-sibling::fturl[@filename=$filename]/@viewftext='INTERNAL' or @viewftext='INTERNAL'">INSTITUT</xsl:when>
									<xsl:otherwise>
										<!-- ERROR -->
										<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownAccessLevel' ), concat('acces level [', @viewftext, '] of fulltext is not supported at eSciDoc, record ', ../../../@id))"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							
							<xsl:call-template name="createComponent">
								<xsl:with-param name="filename" select="$filename"/>
								<xsl:with-param name="access" select="$access"/>
							</xsl:call-template>

						</xsl:when>
					</xsl:choose>
				</xsl:for-each>
				<xsl:if test="$import-name = 'MPIGF'">
					<xsl:for-each select="identifiers/identifier[@type = 'url']">
						<ec:component>		
							<ec:properties>
								<prop:description><xsl:value-of select="@comment"/></prop:description>
								<prop:visibility>public</prop:visibility>
								<prop:content-category>
									<xsl:choose>
										<xsl:when test="@comment = 'Fulltext via Publisher'"><xsl:value-of select="$contentCategory-ves/enum[. = 'publisher-version']/@uri"/></xsl:when>
										<xsl:when test="@comment = 'Abstract'"><xsl:value-of select="$contentCategory-ves/enum[. = 'abstract']/@uri"/></xsl:when>
										<xsl:otherwise><xsl:value-of select="$contentCategory-ves/enum[. = 'any-fulltext']/@uri"/></xsl:otherwise>
									</xsl:choose>
								</prop:content-category>
								<prop:file-name><xsl:value-of select="."/></prop:file-name>
							</ec:properties>
							<ec:content xlink:href="{.}" storage="external-url"/>
							<mdr:md-records>
								<mdr:md-record>
									<file:file>
										<dc:title><xsl:value-of select="."/></dc:title>
										<dc:description><xsl:value-of select="@comment"/></dc:description>
										<eterms:content-category>
											<xsl:choose>
												<xsl:when test="@comment = 'Fulltext via Publisher'"><xsl:value-of select="$contentCategory-ves/enum[. = 'publisher-version']/@uri"/></xsl:when>
												<xsl:when test="@comment = 'Abstract'"><xsl:value-of select="$contentCategory-ves/enum[. = 'abstract']/@uri"/></xsl:when>
												<xsl:otherwise><xsl:value-of select="$contentCategory-ves/enum[. = 'any-fulltext']/@uri"/></xsl:otherwise>
											</xsl:choose>
										</eterms:content-category>
									</file:file>
								</mdr:md-record>
							</mdr:md-records>
						</ec:component>
					</xsl:for-each>
				</xsl:if>
				<xsl:if test="$import-name = 'BPC'">
					<xsl:if test="not(exists(basic/fturl)) and contains($bpc-files, ../@id)">
						<xsl:comment>BPC IMPORT: Record has a File in BPC server. Create a Component for this file</xsl:comment>
						<xsl:call-template name="createComponent">
							<xsl:with-param name="filename">
								<xsl:value-of select="../@id"/>
								<xsl:text>.pdf</xsl:text>
							</xsl:with-param>
							<xsl:with-param name="access" select="INSTITUT"/>
						</xsl:call-template>
					</xsl:if>
				</xsl:if>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<!-- NOT USED!!! -->
	<!--  <xsl:template match="fturl">
		<xsl:call-template name="createComponent">
			<xsl:with-param name="filename" select="@filename"/>
			<xsl:with-param  select="@viewftext"/>
		</xsl:call-template>
	</xsl:template>-->
	
	<xsl:template name="createComponent">
		<xsl:param name="filename"/>
		<xsl:param name="access"/>
		<!-- FILE -->

		<xsl:if test="not(exists(@size)) or @size != ''">

			<xsl:element name="ec:component">
							
				<!-- fturl-comment as content-category? -->
				<xsl:variable name="comment" select="@comment"/>
				<xsl:variable name="edoc-id" select="../../../@id"/>

				<!-- Mime-type -->
				<xsl:variable name="mime-type">
					<xsl:if test="$CoNE = 'true'">
						<xsl:copy-of select="Util:queryCone('escidocmimetypes', concat('&quot;', escidocFunctions:suffix($filename), '&quot;'))"/>
					</xsl:if>
				</xsl:variable>

				<ec:properties>
					<xsl:choose>
						<xsl:when test="$import-name = 'BPC'">
							<xsl:choose>
								<xsl:when test="contains($bpc-files, ../../../@id)">
									<prop:visibility>audience</prop:visibility>
								</xsl:when>
								<xsl:when test="$access= 'USER'">
									<prop:visibility>private</prop:visibility>
								</xsl:when>
								<xsl:when test="$access='MPG' or $access = 'INSTITUT'">
									<prop:visibility>audience</prop:visibility>
								</xsl:when>
								<xsl:when test="$access='PUBLIC'">
									<prop:visibility>public</prop:visibility>
								</xsl:when>
								<xsl:otherwise>
									<!-- ERROR -->
									<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownAccessLevel' ), concat('access level [', $access, '] of fulltext is not supported at eSciDoc, record ', ../../../@id))"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:when test="$import-name = 'FHI'">
							<xsl:choose>
								<xsl:when test="$access='USER' or $access='INSTITUT'">
									<prop:visibility>private</prop:visibility>
								</xsl:when>
								<xsl:when test="$access='MPG'">
									<prop:visibility>audience</prop:visibility>
								</xsl:when>
								<xsl:when test="$access='PUBLIC'">
									<prop:visibility>public</prop:visibility>
								</xsl:when>
								<xsl:otherwise>
									<!-- ERROR -->
									<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownAccessLevel' ), concat('access level [', $access, '] of fulltext is not supported at eSciDoc, record ', ../../../@id))"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:when test="$import-name = 'CBS' or $import-name = 'MPI MoleGen'">
							<xsl:choose>
								<xsl:when test="$access='MPG' or $access='INSTITUT' or $access='INTERNAL'">
									<prop:visibility>audience</prop:visibility>
								</xsl:when>
								<xsl:when test="$access='PUBLIC'">
									<prop:visibility>public</prop:visibility>
								</xsl:when>
								<xsl:otherwise>
									<!-- ERROR -->
									<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownAccessLevel' ), concat('access level [', $access, '] of fulltext is not supported at eSciDoc, record ', ../../../@id))"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIMET'">
							<xsl:choose>
								<xsl:when test="$access='MPG' or $access='INSTITUT'">
									<prop:visibility>audience</prop:visibility>
								</xsl:when>
								<xsl:when test="$access='PUBLIC'">
									<prop:visibility>public</prop:visibility>
								</xsl:when>
								<xsl:otherwise>
									<!-- ERROR -->
									<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownAccessLevel' ), concat('access level [', $access, '] of fulltext is not supported at eSciDoc, record ', ../../../@id))"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIE' or $import-name = 'MPIA'">
							<prop:visibility>private</prop:visibility>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIMF' or $import-name = 'MPIIS'">
							<prop:visibility>audience</prop:visibility>
						</xsl:when>
						<xsl:when test="$import-name = 'EVOLBIO'">
							<xsl:choose>
								<xsl:when test="$access='INSTITUT' or $access='MPG' or $access = 'USER'">
									<prop:visibility>audience</prop:visibility>
								</xsl:when>
								<xsl:when test="$access='PUBLIC'">
									<prop:visibility>public</prop:visibility>
								</xsl:when>
								<xsl:otherwise>
									<!-- ERROR -->
									<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownAccessLevel' ), concat('access level [', $access, '] of fulltext is not supported at eSciDoc, record ', ../../../@id))"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIGF'">
							<xsl:choose>
								<xsl:when test="$access='INSTITUT' or $access='MPG' or $access = 'USER'">
									<prop:visibility>audience</prop:visibility>
								</xsl:when>
								<xsl:when test="$access='PUBLIC'">
									<prop:visibility>public</prop:visibility>
								</xsl:when>
								<xsl:otherwise>
									<!-- ERROR -->
									<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownAccessLevel' ), concat('access level [', $access, '] of fulltext is not supported at eSciDoc, record ', ../../../@id))"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIINF'">
							<prop:visibility>private</prop:visibility>
						</xsl:when>
						<xsl:when test="$import-name = 'MPQ'">
							<prop:visibility>audience</prop:visibility>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="$access='USER'">
									<prop:visibility>private</prop:visibility>
								</xsl:when>
								<xsl:when test="$access='INSTITUT' or $access='MPG' or $access='INTERNAL'">
									<prop:visibility>audience</prop:visibility>
								</xsl:when>
								<xsl:when test="$access='PUBLIC'">
									<prop:visibility>public</prop:visibility>
								</xsl:when>
								<xsl:otherwise>
									<!-- ERROR -->
									<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownAccessLevel' ), concat('access level [', $access, '] of fulltext is not supported at eSciDoc, record ', ../../../@id))"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
					
					<xsl:comment>
						<xsl:value-of select="$comment"/>
					</xsl:comment>
					<xsl:comment>
						<xsl:value-of select="$genre-mapping/genres/genre[edoc-genre = $comment]"/>
					</xsl:comment>
					
					<xsl:choose>
						<!-- Customized - AEI: prop:content-category -->
						<xsl:when test="$source-name = 'eDoc-AEI'">
							<xsl:variable name="content-category">
								<xsl:choose>
									<xsl:when test="contains(lower-case(@comment), 'arxiv')">pre-print</xsl:when>
									<xsl:when test="contains(lower-case(@comment), 'preprint')">pre-print</xsl:when>
									<xsl:when test="contains(lower-case(@comment), 'online journal')">publisher-version</xsl:when>
									<xsl:when test="contains(lower-case(@comment), 'open access journal')">publisher-version</xsl:when>
									<xsl:when test="contains(lower-case(@comment), 'open access article')">publisher-version</xsl:when>
									<xsl:when test="@comment = '' or not(exists(@comment))">publisher-version</xsl:when>
									<xsl:otherwise>any-fulltext</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<prop:content-category>
								<xsl:value-of select="$contentCategory-ves/enum[. = $content-category]/@uri"/>
							</prop:content-category>
						</xsl:when>
						<!-- Customized - FHI: prop:content-category -->
						<xsl:when test="$import-name = 'FHI'">
							<xsl:variable name="content-category">
								<xsl:choose>
									<xsl:when test="contains(lower-case(@comment), 'abstract')">abstract</xsl:when>
									<xsl:when test="contains(lower-case(@comment), 'arxiv')">pre-print</xsl:when>
									<xsl:when test="contains(lower-case(@comment), 'preprint')">pre-print</xsl:when>
									<xsl:when test="contains(lower-case(@comment), 'author version')">pre-print</xsl:when>
									<xsl:when test="contains(lower-case(@comment), 'fulltext')">publisher-version</xsl:when>
									<xsl:when test="contains(lower-case(@comment), 'open choice')">publisher-version</xsl:when>
									<xsl:when test="contains(lower-case(@comment), 'open access')">publisher-version</xsl:when>
									<xsl:when test="contains(lower-case(@comment), 'figure')">supplementary-material</xsl:when>
									<xsl:when test="contains(lower-case(@comment), '.mpeg-video file')">supplementary-material</xsl:when>
									<xsl:when test="contains(lower-case(@comment), 'diagramme')">supplementary-material</xsl:when>
									<xsl:when test="contains(lower-case(@comment), 'fragebogen')">supplementary-material</xsl:when>
									<xsl:when test="contains(lower-case(@comment), 'supporting online material')">supplementary-material</xsl:when>
									<xsl:otherwise>any-fulltext</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:comment>Comment: <xsl:value-of select="lower-case(@comment)"/>
							</xsl:comment>
							<prop:content-category>
								<xsl:value-of select="$contentCategory-ves/enum[. = $content-category]/@uri"/>
							</prop:content-category>
						</xsl:when>
						<xsl:when test="$import-name = 'BPC' or $import-name = 'MPIMET' or $import-name = 'MPIPF'">
							<prop:content-category>
								<xsl:value-of select="$contentCategory-ves/enum[. = 'publisher-version']/@uri"/>
							</prop:content-category>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIA' or $import-name = 'MPIE' or $import-name = 'ETH' or $import-name = 'MPIMF' or $import-name = 'MPI MoleGen'">
							<prop:content-category>
								<xsl:value-of select="$contentCategory-ves/enum[. = 'any-fulltext']/@uri"/>
							</prop:content-category>
						</xsl:when>
						<xsl:when test="$import-name = 'EVOLBIO'">
							<xsl:comment>EVOLBIO</xsl:comment>
							<prop:content-category>
								<xsl:choose>
									<xsl:when test="lower-case($comment) = 'scan'">
										<xsl:value-of select="$contentCategory-ves/enum[. = 'any-fulltext']/@uri"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$contentCategory-ves/enum[. = 'publisher-version']/@uri"/>
									</xsl:otherwise>
								</xsl:choose>
							</prop:content-category>
						</xsl:when>
						<xsl:when test="exists($genre-mapping/genres/genre[@type = $import-name and edoc-genre = $comment])">
							<xsl:variable name="content-category" select="$genre-mapping/genres/genre[@type = $import-name and edoc-genre = $comment]/pubman-genre"/>
							<prop:content-category>
								<xsl:value-of select="$contentCategory-ves/enum[. = $content-category]/@uri"/>
							</prop:content-category>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIGF' and exists(../../identifiers/identifier[@type = 'doi' or @comment = 'Fulltext via Publisher'])">
							<prop:content-category><xsl:value-of select="$contentCategory-ves/enum[. = 'publisher-version']/@uri"/></prop:content-category>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIGF' and not(exists($genre-mapping/genres/genre[@type = $import-name and edoc-genre = $comment]))">
							<prop:content-category>
								<xsl:value-of select="$contentCategory-ves/enum[. = 'any-fulltext']/@uri"/>
							</prop:content-category>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIP'">
							<prop:content-category><xsl:value-of select="$contentCategory-ves/enum[. = 'any-fulltext']/@uri"/></prop:content-category>
						</xsl:when>
						<xsl:when test="$import-name = 'MPQ'">
							<prop:content-category><xsl:value-of select="$contentCategory-ves/enum[. = 'publisher-version']/@uri"/></prop:content-category>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIIS'">
							<prop:content-category><xsl:value-of select="$contentCategory-ves/enum[. = 'abstract']/@uri"/></prop:content-category>
						</xsl:when>
						<!-- Default: prop:content-category -->
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="$access='USER' or $access='INSTITUT' or $access='MPG'">
									<prop:content-category>
										<xsl:value-of select="$contentCategory-ves/enum[. = 'publisher-version']/@uri"/>
									</prop:content-category>
								</xsl:when>
								<xsl:when test="$access='PUBLIC'">
									<prop:content-category>
										<xsl:value-of select="$contentCategory-ves/enum[. = 'any-fulltext']/@uri"/>
									</prop:content-category>
								</xsl:when>
								<xsl:otherwise>
									<!-- ERROR -->
									<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownAccessLevel' ), concat('acces level [', $access, '] of fulltext is not supported at eSciDoc, record ', ../../../@id))"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="exists($mime-type/cone/rdf:RDF/rdf:Description/dc:relation/rdf:Description/dc:title)">
							<prop:mime-type>
								<xsl:value-of select="$mime-type/cone/rdf:RDF/rdf:Description/dc:relation/rdf:Description/dc:title"/>
							</prop:mime-type>
						</xsl:when>
						<xsl:when test="$CoNE = 'false'">
							<xsl:comment>CoNE disabled, therefore no mime type</xsl:comment>
						</xsl:when>
						<xsl:otherwise>
							<xsl:comment>Mime Type for <xsl:value-of select="$filename"/> not found in CONE</xsl:comment>
							<!-- ERROR -->
									<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownMimeTypeSuffix' ), concat('Mime Type for ', $filename, ' not found in CONE'))"/>
						</xsl:otherwise>
					</xsl:choose>
					<!--  <xsl:choose>
						<xsl:when test="ends-with($filename, '.doc')">
							<prop:mime-type>application/msword</prop:mime-type>
						</xsl:when>
						<xsl:when test="ends-with($filename, '.zip')">
							<prop:mime-type>application/zip</prop:mime-type>
						</xsl:when>
						<xsl:otherwise>
							<prop:mime-type>application/pdf</prop:mime-type>
						</xsl:otherwise>
					</xsl:choose>-->
				</ec:properties>
				<xsl:element name="ec:content">
					<xsl:choose>
						<xsl:when test="$import-name = 'BPC' and not(exists(../../basic/fturl)) and contains($bpc-files, ../@id)">
							<xsl:attribute name="xlink:href">
								<xsl:text>http://www.mpibpc.mpg.de/fb/pdfs/</xsl:text>
								<xsl:value-of select="$filename"/>
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="xlink:href" select="."/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:attribute name="storage" select="'internal-managed'"/>
				</xsl:element>
				<xsl:element name="mdr:md-records">
					<mdr:md-record name="escidoc">
						<xsl:element name="file:file">
							<xsl:element name="dc:title">
								<xsl:choose>
									<xsl:when test="exists(@filename)">
										<xsl:value-of select="@filename"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$filename"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:element>
							<xsl:choose>
								<!-- Customized - AEI: prop:content-category -->
								<xsl:when test="$source-name = 'eDoc-AEI'">
									<xsl:variable name="content-category">
										<xsl:choose>
											<xsl:when test="contains(lower-case(@comment), 'arxiv')">pre-print</xsl:when>
											<xsl:when test="contains(lower-case(@comment), 'preprint')">pre-print</xsl:when>
											<xsl:when test="contains(lower-case(@comment), 'online journal')">publisher-version</xsl:when>
											<xsl:when test="contains(lower-case(@comment), 'open access journal')">publisher-version</xsl:when>
											<xsl:when test="contains(lower-case(@comment), 'open access article')">publisher-version</xsl:when>
											<xsl:when test="@comment = '' or not(exists(@comment))">publisher-version</xsl:when>
											<xsl:otherwise>any-fulltext</xsl:otherwise>
										</xsl:choose>
									</xsl:variable>
									<eterms:content-category>
										<xsl:value-of select="$contentCategory-ves/enum[. = $content-category]/@uri"/>
									</eterms:content-category>
								</xsl:when>
								<!-- Customized - FHI: prop:content-category -->
								<xsl:when test="$import-name = 'FHI'">
									<xsl:variable name="content-category">
										<xsl:choose>
											<xsl:when test="contains(lower-case(@comment), 'abstract')">abstract</xsl:when>
											<xsl:when test="contains(lower-case(@comment), 'arxiv')">pre-print</xsl:when>
											<xsl:when test="contains(lower-case(@comment), 'preprint')">pre-print</xsl:when>
											<xsl:when test="contains(lower-case(@comment), 'author version')">pre-print</xsl:when>
											<xsl:when test="contains(lower-case(@comment), 'fulltext')">publisher-version</xsl:when>
											<xsl:when test="contains(lower-case(@comment), 'open choice')">publisher-version</xsl:when>
											<xsl:when test="contains(lower-case(@comment), 'open access')">publisher-version</xsl:when>
											<xsl:when test="contains(lower-case(@comment), 'figure')">supplementary-material</xsl:when>
											<xsl:when test="contains(lower-case(@comment), '.mpeg-video file')">supplementary-material</xsl:when>
											<xsl:when test="contains(lower-case(@comment), 'diagramme')">supplementary-material</xsl:when>
											<xsl:when test="contains(lower-case(@comment), 'fragebogen')">supplementary-material</xsl:when>
											<xsl:when test="contains(lower-case(@comment), 'supporting online material')">supplementary-material</xsl:when>
											<xsl:otherwise>any-fulltext</xsl:otherwise>
										</xsl:choose>
									</xsl:variable>
									<eterms:content-category>
										<xsl:value-of select="$contentCategory-ves/enum[. = $content-category]/@uri"/>
									</eterms:content-category>
								</xsl:when>
								<xsl:when test="$import-name = 'BPC' or $import-name = 'MPIMET'">
									<eterms:content-category>
										<xsl:value-of select="$contentCategory-ves/enum[. = 'publisher-version']/@uri"/>
									</eterms:content-category>
								</xsl:when>
								<xsl:when test="$import-name = 'EVOLBIO'">
									<eterms:content-category>
										<xsl:choose>
											<xsl:when test="lower-case($comment) = 'scan'">
												<xsl:value-of select="$contentCategory-ves/enum[. = 'any-fulltext']/@uri"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="$contentCategory-ves/enum[. = 'publisher-version']/@uri"/>
											</xsl:otherwise>
										</xsl:choose>
									</eterms:content-category>
								</xsl:when>
								<xsl:when test="$import-name = 'MPIA' or $import-name = 'MPIE' or $import-name = 'ETH' or $import-name = 'MPINEURO'">
									<eterms:content-category>
										<xsl:value-of select="$contentCategory-ves/enum[. = 'any-fulltext']/@uri"/>
									</eterms:content-category>
								</xsl:when>
								<xsl:when test="$genre-mapping/genres/genre[@type = $import-name and edoc-genre = $comment]">
									<xsl:variable name="content-category" select="$genre-mapping/genres/genre[@type = $import-name and edoc-genre = $comment]/pubman-genre"/>
									<eterms:content-category>
										<xsl:value-of select="$contentCategory-ves/enum[. = $content-category]/@uri"/>
									</eterms:content-category>
								</xsl:when>
								<xsl:when test="$import-name = 'MPIGF' and not(exists($genre-mapping/genres/genre[@type = $import-name and edoc-genre = $comment]))">
									<eterms:content-category>
										<xsl:value-of select="$contentCategory-ves/enum[. = 'any-fulltext']/@uri"/>
									</eterms:content-category>
								</xsl:when>
								<xsl:when test="$import-name = 'MPIP' or $import-name = 'MPI MoleGen'">
									<eterms:content-category><xsl:value-of select="$contentCategory-ves/enum[. = 'any-fulltext']/@uri"/></eterms:content-category>
								</xsl:when>
								<xsl:when test="$import-name = 'MPQ'">
									<eterms:content-category><xsl:value-of select="$contentCategory-ves/enum[. = 'publisher-version']/@uri"/></eterms:content-category>
								</xsl:when>
								<xsl:when test="$import-name = 'MPIIS'">
									<eterms:content-category><xsl:value-of select="$contentCategory-ves/enum[. = 'abstract']/@uri"/></eterms:content-category>
								</xsl:when>
								<!-- Default: eterms:content-category -->
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when test="$access='USER' or $access='INSTITUT' or $access='MPG'">
											<eterms:content-category>
												<xsl:value-of select="$contentCategory-ves/enum[. = 'publisher-version']/@uri"/>
											</eterms:content-category>
										</xsl:when>
										<xsl:when test="$access='PUBLIC'">
											<eterms:content-category>
												<xsl:value-of select="$contentCategory-ves/enum[. = 'any-fulltext']/@uri"/>
											</eterms:content-category>
										</xsl:when>
										<xsl:otherwise>
											<!-- ERROR -->
											<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownAccessLevel' ), concat('acces level [', $access, '] of fulltext is not supported at eSciDoc, record ', ../../../@id))"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:choose>
								<xsl:when test="$import-name = 'FHI' and contains(lower-case(@comment), 'arxiv')">
									<xsl:element name="dc:description">
										<xsl:value-of select="@comment"/>
									</xsl:element>
								</xsl:when>
								<xsl:when test="($import-name = 'MPIE' or $import-name = 'MPIA' or $import-name = 'MPIPF') and exists(@comment)">
									<xsl:element name="dc:description">
										<xsl:value-of select="@comment"/>
									</xsl:element>
								</xsl:when>
								<xsl:when test="$import-name = 'EVOLBIO'">
									<xsl:if test="exists($evolbio-file-comment-exceptions/edoc[id = $edoc-id]) or exists($evolbio-author-comment-exceptions/edoc[id = $edoc-id])">
										<xsl:element name="dc:description">
											<xsl:if test="exists($evolbio-file-comment-exceptions/edoc[id = $edoc-id])">
												<xsl:value-of select="@comment"/>
											</xsl:if>
											<xsl:if test="exists($evolbio-file-comment-exceptions/edoc[id = $edoc-id]) and exists($evolbio-author-comment-exceptions/edoc[id = $edoc-id])">
												<xsl:text> / </xsl:text>
											</xsl:if>
											<xsl:if test="exists($evolbio-author-comment-exceptions/edoc[id = $edoc-id])">
												<xsl:value-of select="../authorcomment"/>
											</xsl:if>
										</xsl:element>
									</xsl:if>
								</xsl:when>
							</xsl:choose>
							<xsl:choose>
								<xsl:when test="exists($mime-type/cone/rdf:RDF/rdf:Description/dc:relation/rdf:Description/dc:title)">
									<dc:format xsi:type="dcterms:IMT">
										<xsl:value-of select="$mime-type/cone/rdf:RDF/rdf:Description/dc:relation/rdf:Description/dc:title"/>
									</dc:format>
								</xsl:when>
								<xsl:when test="$CoNE = 'false'">
									<xsl:comment>CoNE disabled, therefore no mime type</xsl:comment>
								</xsl:when>
								<xsl:otherwise>
									<!-- ERROR -->
									<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownMimeTypeSuffix' ), concat('Mime Type for ', $filename, ' not found in CONE'))"/>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:if test="exists(@size) and @size != ''">
								<dcterms:extent>
									<xsl:value-of select="@size"/>
								</dcterms:extent>
							</xsl:if>
							<xsl:choose>
								<xsl:when test="$import-name = 'FHI'">
									<!-- <xsl:call-template name="copyrightFHI"/>--></xsl:when>
								<xsl:when test="$import-name = 'MPIMET'">
									<xsl:element name="dc:rights">
										<xsl:value-of select="concat('eDoc_access: ', $access)"/>
									</xsl:element>
									<xsl:element name="dc:rights">
										<xsl:value-of select="../../../rights/copyright"/>
									</xsl:element>
								</xsl:when>
								<xsl:when test="$import-name = 'MPIGF' or $import-name = 'MPIINF' or $import-name = 'MPIP'">
									<xsl:if test="exists(../../../rights/copyright)">
										<xsl:element name="dc:rights">
											<xsl:value-of select="../../../rights/copyright"/>
										</xsl:element>
									</xsl:if>
								</xsl:when>
								<xsl:when test="$import-name = 'BPC' or $import-name = 'MPIA' or $import-name = 'MPIE'"></xsl:when>
								<xsl:when test="$import-name = 'EVOLBIO'">
									<xsl:if test="exists($evolbio-copyright-exceptions/edoc[id = $edoc-id])">
										<xsl:element name="dc:rights">
											<xsl:value-of select="../../../rights/copyright"/>
										</xsl:element>
									</xsl:if>
								</xsl:when>
								<xsl:when test="$import-name = 'MPINEURO'">
									<xsl:variable name="copyright" select="../../../rights/copyright"/>
									<xsl:if test="$comment != '' or $copyright != ''">
										<xsl:element name="dc:rights">
											<xsl:if test="$comment != ''">
												<xsl:value-of select="$comment"/>
											</xsl:if>
											<xsl:if test="$comment != '' and $copyright != ''">; </xsl:if>
											<xsl:if test="$copyright != ''">
												<xsl:value-of select="$copyright"/>
											</xsl:if>
										</xsl:element>
									</xsl:if>
								</xsl:when>
								<xsl:otherwise>
									<xsl:element name="dc:rights">
										<xsl:value-of select="concat('eDoc_access: ', $access)"/>
									</xsl:element>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:element>
					</mdr:md-record>
				</xsl:element>
			</xsl:element>
			
		</xsl:if>
	
	</xsl:template>
	
	<!--  <xsl:template name="createLocator">
		<xsl:param name="filename"/>
		<xsl:param name="access"/>
		<xsl:element name="ec:component">
			<ec:properties>
				<prop:visibility>public</prop:visibility>
				<prop:content-category>
					<xsl:value-of select="$contentCategory-ves/enum[. = 'supplementary-material']/@uri"/>
				</prop:content-category>
			</ec:properties>
			<xsl:element name="ec:content">
				<xsl:attribute name="xlink:href" select="."/>
				<xsl:attribute name="storage" select="'external-url'"/>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:element name="file:file">
						<xsl:comment><xsl:value-of select="$access"/></xsl:comment>
						<xsl:choose>
							<xsl:when test="$access='USER'">
								<xsl:element name="dc:title">restricted access to full text (selected user)</xsl:element>
								<xsl:element name="dc:description">
									<xsl:value-of select="@filename"/>
								</xsl:element>
							</xsl:when>
							<xsl:when test="$access='INSTITUT'">
								<xsl:element name="dc:title">restricted access to full text (institute-wide)</xsl:element>
								<xsl:element name="dc:description">
									<xsl:value-of select="@filename"/>
								</xsl:element>
							</xsl:when>
							<xsl:when test="$access='MPG'">
								<xsl:element name="dc:title">restricted access to full text (MPS-wide)</xsl:element>
								<xsl:element name="dc:description">
									<xsl:value-of select="@filename"/>
								</xsl:element>
							</xsl:when>
							<xsl:when test="$access='PUBLIC'">
								<xsl:element name="dc:title">
									<xsl:value-of select="@filename"/>
								</xsl:element>
							</xsl:when>
							<xsl:otherwise>
								<xsl:comment>ERROR</xsl:comment>
							</xsl:otherwise>
						</xsl:choose>
						<xsl:if test="$import-name = 'FHI'">
							<xsl:call-template name="copyrightFHI"/>
						</xsl:if>
					</xsl:element>
				</mdr:md-record>
			</xsl:element>
		</xsl:element>
	</xsl:template>-->
	

	<!-- BASIC -->
	<xsl:template match="basic">
		<xsl:choose>
			<xsl:when test="genre='Article'">
				<xsl:choose>
					<xsl:when test="$import-name = 'MPIEVA' and contains(lower-case(title), '[abstract]')">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="gen" select="'meeting-abstract'"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="$import-name = 'MPIGF' and (contains(title, 'Rezension') or contains(title, 'Book Review'))">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="gen" select="'book-review'"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="createEntry">
							<xsl:with-param name="gen" select="'article'"/>
						</xsl:call-template>
					</xsl:otherwise>	
				</xsl:choose>
			</xsl:when>
			<xsl:when test="genre='Report'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'report'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Book'">
				<xsl:choose>
					<xsl:when test="$import-name = 'MPIGF'">
						<xsl:choose>
							<xsl:when test="exists(../creators/creator[@role = 'editor'])">
								<xsl:call-template name="createEntry">
									<xsl:with-param name="gen" select="'collected-edition'"/>
								</xsl:call-template>
							</xsl:when>
							<xsl:when test="exists(corporatebody)">
								<xsl:call-template name="createEntry">
									<xsl:with-param name="gen" select="'collected-edition'"/>
								</xsl:call-template>
							</xsl:when>
							<xsl:when test="exists(../creators/creator[@role = 'author'])">
								<xsl:call-template name="createEntry">
									<xsl:with-param name="gen" select="'book'"/>
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownGenre' ), 'The genre mapping for this item is ambiguous. Please check the creator section.')"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="createEntry">
							<xsl:with-param name="gen" select="'book'"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="genre='booklet'">
				<xsl:choose>
					<xsl:when test="$import-name = 'BiblHertz'">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="gen" select="'other'"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="genre='book-review'">
				<xsl:choose>
					<xsl:when test="$import-name = 'BiblHertz'">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="gen" select="'book-review'"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="genre='catalogue-article'">
				<xsl:choose>
					<xsl:when test="$import-name = 'BiblHertz'">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="gen" select="'article'"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="genre='catalogue-entry'">
				<xsl:choose>
					<xsl:when test="$import-name = 'BiblHertz'">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="gen" select="'other'"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="genre='collection-article'">
				<xsl:choose>
					<xsl:when test="$import-name = 'BiblHertz'">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="gen" select="'contribution-to-collected-edition'"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="genre='Conference-Paper'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'conference-paper'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Conference-Report'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'conference-report'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='contribution-to-encyclopedia'">
				<xsl:choose>
					<xsl:when test="$import-name = 'BiblHertz'">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="gen" select="'contribution-to-encyclopedia'"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="genre='festschrift-article'">
				<xsl:choose>
					<xsl:when test="$import-name = 'BiblHertz'">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="gen" select="'contribution-to-festschrift'"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="genre='Habilitation'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'thesis'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='InBook'">
				<xsl:choose>
					<xsl:when test="$import-name = 'MPIGF'">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="gen" select="'contribution-to-collected-edition'"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="createEntry">
							<xsl:with-param name="gen" select="'book-item'"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="genre='Issue'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'issue'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Interactive Resource'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'other'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Journal'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'journal'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Lecture / Courseware'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'courseware-lecture'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='newspaper-article'">
				<xsl:choose>
					<xsl:when test="$import-name = 'BiblHertz'">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="gen" select="'newspaper-article'"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="genre='Other'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'other'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='online-article'">
				<xsl:choose>
					<xsl:when test="$import-name = 'BiblHertz'">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="gen" select="'article'"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="genre='Paper'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'paper'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='PhD-Thesis'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'thesis'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Poster'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'poster'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Proceedings'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'proceedings'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Series'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'series'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Software'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'other'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Talk at Event'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'talk-at-event'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Thesis'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'thesis'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownGenre' ), concat(genre, ' is not mapped to an eSciDoc publication genre'))"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Create eSciDoc Entry -->
	<xsl:template name="createEntry">
		<xsl:param name="gen"/>
		
		<xsl:variable name="has-source" as="xs:boolean">
			<xsl:choose>
				<xsl:when test="journaltitle">
					<xsl:value-of select="true()"/>
				</xsl:when>
				<xsl:when test="issuetitle">
					<xsl:value-of select="true()"/>
				</xsl:when>
				<xsl:when test="booktitle">
					<xsl:value-of select="true()"/>
				</xsl:when>
				<xsl:when test="titleofproceedings">
					<xsl:value-of select="true()"/>
				</xsl:when>
				<xsl:when test="titleofseries">
					<xsl:value-of select="true()"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="false()"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:element name="pub:publication">
			<xsl:attribute name="type" select="$genre-ves/enum[. = $gen]/@uri"/>
			<!-- creator -->
			<xsl:for-each select="../creators/creator">
				<xsl:element name="eterms:creator">
					<xsl:call-template name="createCreator"/>
				</xsl:element>
			</xsl:for-each>
			<xsl:apply-templates select="corporatebody"/>
			<!-- TITLE -->
			<xsl:element name="dc:title">
				<xsl:choose>
					<xsl:when test="$import-name = 'MPIINF'">
						<xsl:value-of select="replace(title, '\{|\}', '')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="title"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
			<!-- LANGUAGE -->
			<xsl:apply-templates select="language"/>
			<!--ALTTITLE -->
			<xsl:apply-templates select="titlealt"/>
			<!-- IDENTIFIER -->
			<xsl:call-template name="createIdentifier">
				<xsl:with-param name="gen" select="$gen"/>
				<xsl:with-param name="has-source" select="$has-source"/>
			</xsl:call-template>			
			<!-- PUBLISHING-INFO -->
			<xsl:choose>
				<xsl:when test="(exists(publisher) or exists(editiondescription)) and not($dependentGenre[type = $gen])">
					<xsl:choose>
						<xsl:when test="$import-name = 'MPI MoleGen' and exists(editiondescription) and not(exists(publisher)) and $gen != 'thesis'"/>
						<xsl:otherwise>
							<!-- case: book or proceedings -->
							<xsl:element name="eterms:publishing-info">
								<xsl:call-template name="createPublishinginfo">
									<xsl:with-param name="genre" select="$gen"/>
								</xsl:call-template>
							</xsl:element>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$gen='book-item'">
					<!-- case: book-item without source book -->
					<xsl:if test="not(exists(booktitle))">
						<xsl:element name="eterms:publishing-info">
							<xsl:call-template name="createPublishinginfo">
								<xsl:with-param name="genre" select="$gen"/>
							</xsl:call-template>
						</xsl:element>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
			
			<!-- DATES -->
			<xsl:call-template name="createDates"/>
			
			<!-- REVIEW METHOD -->
			<xsl:apply-templates select="refereed"/>
			
			<!-- Number of sources -->
			<xsl:variable name="sources-count">
				<xsl:value-of select="count(journaltitle|issuetitle|booktitle|titleofseries|titleofproceedings)"/>
			</xsl:variable>
			
			<!-- 
				  Source identifiers type:
				  Source with ISBN = booktitle , titleofproceedings , issuetitle
				  Source with ISSN = titleofseries , journaltitle
				  
				  If a publication has an identifier (ISSN or ISBN), but doesn't have a source 
				  to store it, then this ID will be store in an other source.
				  
				  Example:
				  1) Publication has: * 1 ISBN and 1 ISSN.
				  				      * 1 booktitle
				  	After transformation,  the booktitle will get both identifiers.
				  
				  2) Publication has: * 1 ISBN and 1 ISSN.
				  				      * 1 booktitle, one journal
				  	After transformatin,  the booktitle will get the isbn, the issue the ISSN.
			 -->
			
			<!-- Check whether there is 1 source which will save the isbn -->
			<xsl:variable name="isbn-save" select="booktitle or issuetitle or titleofproceedings" as="xs:boolean"/>
			<!-- Check whether there is 1 source which will save the issn -->
			<xsl:variable name="issn-save" select="titleofseries or journaltitle" as="xs:boolean"/>
			
			<xsl:if test="issuetitle">
				<xsl:element name="source:source">
					<xsl:call-template name="createIssue">
						<xsl:with-param name="sources-count" select="$sources-count"/>
						<xsl:with-param name="gen" select="$gen"/>
						<xsl:with-param name="issn-save" select="$issn-save"/>
					</xsl:call-template>
				</xsl:element>
			</xsl:if>
			
			<xsl:if test="journaltitle">
				<xsl:element name="source:source">
					<xsl:call-template name="createJournal">
						<xsl:with-param name="sources-count" select="$sources-count"/>
						<xsl:with-param name="gen" select="$gen"/>
						<xsl:with-param name="isbn-save" select="$isbn-save"/>
					</xsl:call-template>
				</xsl:element>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="booktitle and ($import-name = 'MPIGF')">
					<xsl:element name="source:source">
						<xsl:call-template name="createCollectedEdition">
							<xsl:with-param name="sources-count" select="$sources-count"/>
							<xsl:with-param name="gen" select="$gen"/>
							<xsl:with-param name="issn-save" select="$issn-save"/>
						</xsl:call-template>
					</xsl:element>
				</xsl:when>
				<xsl:when test="booktitle and ($import-name = 'BiblHertz') and ($gen = 'contribution-to-collected-edition')">
					<xsl:element name="source:source">
						<xsl:call-template name="createCollectedEdition">
							<xsl:with-param name="sources-count" select="$sources-count"/>
							<xsl:with-param name="gen" select="$gen"/>
							<xsl:with-param name="issn-save" select="$issn-save"/>
						</xsl:call-template>
					</xsl:element>
				</xsl:when>
				<xsl:when test="booktitle and ($import-name = 'BiblHertz') and ($gen = 'contribution-to-encyclopedia')">
					<xsl:element name="source:source">
						<xsl:call-template name="createEncyclopedia">
							<xsl:with-param name="sources-count" select="$sources-count"/>
							<xsl:with-param name="gen" select="$gen"/>
							<xsl:with-param name="issn-save" select="$issn-save"/>
						</xsl:call-template>
					</xsl:element>
				</xsl:when>
				<xsl:when test="booktitle and ($import-name = 'BiblHertz') and ($gen = 'contribution-to-festschrift')">
					<xsl:element name="source:source">
						<xsl:call-template name="createFestschrift">
							<xsl:with-param name="sources-count" select="$sources-count"/>
							<xsl:with-param name="gen" select="$gen"/>
							<xsl:with-param name="issn-save" select="$issn-save"/>
						</xsl:call-template>
					</xsl:element>
				</xsl:when>
				<xsl:when test="booktitle and ($gen != 'contribution-to-festschrift') and ($gen != 'contribution-to-encyclopedia') and ($gen != 'contribution-to-collected-edition')">
					<xsl:element name="source:source">
						<xsl:call-template name="createBook">
							<xsl:with-param name="sources-count" select="$sources-count"/>
							<xsl:with-param name="gen" select="$gen"/>
							<xsl:with-param name="issn-save" select="$issn-save"/>
						</xsl:call-template>
					</xsl:element>
				</xsl:when>
			</xsl:choose>
			<xsl:if test="titleofproceedings">
				<xsl:element name="source:source">
					<xsl:call-template name="createProceedings">
						<xsl:with-param name="sources-count" select="$sources-count"/>
						<xsl:with-param name="gen" select="$gen"/>
						<xsl:with-param name="issn-save" select="$issn-save"/>
					</xsl:call-template>
				</xsl:element>
			</xsl:if>
			
			<xsl:if test="titleofseries">
				<xsl:element name="source:source">
					<xsl:call-template name="createSeries">
						<xsl:with-param name="sources-count" select="$sources-count"/>
						<xsl:with-param name="gen" select="$gen"/>
						<xsl:with-param name="isbn-save" select="$isbn-save"/>
					</xsl:call-template>
				</xsl:element>
			</xsl:if>
			
			<!-- NOT MAPPED ANYMORE !!! isPartOf RELATION -->
			<!--  <xsl:if test="../relations/relation[@reltype='ispartof']">
				<xsl:element name="source:source">
					<xsl:attribute name="type" select="'series'"/>
					<xsl:element name="dc:title">
						<xsl:value-of select="../relations/relation[@reltype='ispartof']"/>
					</xsl:element>
				</xsl:element>
			</xsl:if>-->

			<!-- EVENT -->
			<xsl:if test="exists(nameofevent) or ($import-name = 'MPIINF' and (exists(placeofevent) or exists(dateofevent) or exists(enddateofevent)))">
				<xsl:call-template name="createEvent"/>
			</xsl:if>
			<!-- TOTAL NUMBER OF PAGES -->
			<xsl:variable name="authorcomment" select="authorcomment"/>
			<xsl:choose>
				<xsl:when test="$import-name = 'MPIIS' and exists($mpiis-comments/comment[contains($authorcomment, .)])">
					<xsl:element name="eterms:total-number-of-pages">
						<xsl:value-of select="authorcomment"/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="phydesc and ($gen = 'paper' or $gen = 'issue')">
					<xsl:call-template name="phydescPubl"/>
				</xsl:when>
				<xsl:when test="phydesc and not($dependentGenre[type = $gen] and (exists(titleofproceedings) or exists(booktitle) or exists(issuetitle) or exists(journaltitle) or exists(titleofseries)))">
					<xsl:call-template name="phydescPubl"/>
				</xsl:when>
			</xsl:choose>
			
			<!-- DEGREE -->
			<xsl:variable name="degree-type">
				<xsl:choose>
					<xsl:when test="'phd-thesis' = lower-case(genre)">phd</xsl:when>
					<xsl:when test="'habilitation' = lower-case(genre)">habilitation</xsl:when>
					<xsl:when test="'thesis' = lower-case(genre)">
						<xsl:choose>
							<xsl:when test="'diplom' = lower-case(editiondescription)">diploma</xsl:when>
							<xsl:when test="'magister' = lower-case(editiondescription)">magister</xsl:when>
							<xsl:when test="'staatsexamen' = lower-case(editiondescription)">staatsexamen</xsl:when>
							<xsl:when test="'master' = lower-case(editiondescription)">master</xsl:when>
							<xsl:when test="'bachelor' = lower-case(editiondescription)">bachelor</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="editiondescription"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
			</xsl:variable>
			<xsl:comment>DEGREE-TYPE: <xsl:value-of select="$degree-type"/>
			</xsl:comment>
			<xsl:if test="exists($degree-type) and $degree-type != ''">
				<xsl:element name="eterms:degree">
					<xsl:value-of select="$degree-ves/enum[. = $degree-type]/@uri"/>
				</xsl:element>
			</xsl:if>
			
			<!-- ABSTRACT -->
			<xsl:if test="$import-name = 'MPIGF' and exists(docaff_reasearchcontext)">
				<xsl:element name="dcterms:abstract">
					<xsl:value-of select="docaff_reasearchcontext"/>
				</xsl:element>
			</xsl:if>
			
			<xsl:apply-templates select="abstract"/>
			<xsl:call-template name="abstractMPIEMPIA"/>
			
			<!-- SUBJECT -->
			<xsl:apply-templates select="discipline"/>
			
			<xsl:call-template name="dcTermsSubject"/>
			
			<!-- TOC -->
			<xsl:apply-templates select="toc"/>
			
			<!--end publication-->
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="createPublishinginfo">
		<xsl:param name="genre"/>
		<xsl:apply-templates select="publisher"/>
		<xsl:apply-templates select="publisheradd"/>
		<xsl:choose>
			<xsl:when test="$import-name = 'MPIGF' and exists(authorcomment)">
				<xsl:apply-templates select="authorcomment"/>
			</xsl:when>
			<xsl:when test="$genre != 'thesis'">
				<xsl:apply-templates select="editiondescription"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="corporatebody">
		<xsl:call-template name="createPublCreatorOrga"/>
	</xsl:template>
	<xsl:template match="issuecorporatebody">
		<xsl:call-template name="createSourceCreatorOrga"/>
	</xsl:template>
	<xsl:template match="seriescorporatebody">
		<xsl:call-template name="createSourceCreatorOrga"/>
	</xsl:template>
	<xsl:template match="bookcorporatebody">
		<xsl:call-template name="createSourceCreatorOrga"/>
	</xsl:template>
	
	<xsl:template name="createPublCreatorOrga">
		<xsl:element name="eterms:creator">
			<xsl:attribute name="role" select="$creator-ves/enum[. = 'editor']/@uri"/>
			<xsl:element name="organization:organization">
				<xsl:element name="dc:title">
					<xsl:value-of select="."/>
				</xsl:element>
				<dc:identifier>
					<xsl:value-of select="$organizational-units/ou[@name = .]/@id"/>
				</dc:identifier>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="createSourceCreatorOrga">
		<xsl:element name="eterms:creator">
			<xsl:attribute name="role" select="$creator-ves/enum[. = 'editor']/@uri"/>
			<xsl:element name="organization:organization">
				<xsl:element name="dc:title">
					<xsl:value-of select="."/>
				</xsl:element>
				<dc:identifier>
					<xsl:value-of select="$organizational-units/ou[@name = .]/@id"/>
				</dc:identifier>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="createIdentifier">
		<xsl:param name="gen"/>
		<xsl:param name="has-source" as="xs:boolean"/>
		<!-- eDoc ID -->
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type" select="'eterms:EDOC'"/>
			<xsl:value-of select="../../@id"/>
		</xsl:element>
		<xsl:for-each select="../identifiers/identifier[@type != 'url' or $import-name != 'MPIGF']">
			<xsl:call-template name="createIDs">
				<xsl:with-param name="gen" select="$gen"/>
				<xsl:with-param name="has-source" select="$has-source"/>
				<xsl:with-param name="is-source" select="false()"/>
				<xsl:with-param name="sources-count" select="0"/>
			</xsl:call-template>
		</xsl:for-each>
		<xsl:if test="$import-name = 'MPIKOFO'">
			<xsl:for-each select="../relations/relation[@type = 'url' and @reltype='hasreferences']">
				<dc:identifier xsi:type="eterms:URI"><xsl:value-of select="identifier"/></dc:identifier>
			</xsl:for-each>
			<xsl:for-each select="../relations/relation[@type = 'doi' and @reltype='hasreferences']">
				<dc:identifier xsi:type="eterms:DOI"><xsl:value-of select="identifier"/></dc:identifier>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>
	
	<!-- 
		Create Identifiers (doi, issn, isbn, uri, isi, other)
		For ISBN-ISSN:	
		1) Bei unabhängigen Genres wird die ISBN und ISSN immer in die Publikation geschrieben.
		2) Bei abhängigen Genres wird die ISBN und ISSN immer in die Quelle geschrieben.
		3) Bei abhängigen Genres mit 2 Quellen werden ISBN/ISSN folgendermaßen aufgeteilt
	-->
	
	<xsl:template name="createIDs">
		<xsl:param name="gen"/>
		<xsl:param name="has-source" as="xs:boolean"/>
		<xsl:param name="is-source" as="xs:boolean"/>
		<xsl:param name="sources-count"/>
		
		<xsl:comment>@type = <xsl:value-of select="@type"/>
		$is-source = <xsl:value-of select="$is-source"/>
		$sources-count = <xsl:value-of select="$sources-count"/>
		</xsl:comment>
		
		<xsl:if test="(
						(
							@type='issn' 
						or 
							@type='isbn'
						) 
						and
						not(
							$dependentGenre[type = $gen]
						) 
						and 
						not(
							$is-source
						) 
						and 
						(
							$sources-count &lt; 2
						)
					) 
					or 
					(
						(
							@type='issn' 
						or 
							@type='isbn'
						) 
						and 
							$dependentGenre[type = $gen] 
						and 
							$is-source 
						and 
						(
							$sources-count = 1
						)
					) 
					or 
					(
						(
							@type='issn' 
						or 
							@type='isbn'
						) 
						and 
						$dependentGenre[type = $gen] 
						and 
						(
							$sources-count &gt; 1
						)
					) 
					or 
					(
						not(
							@type='issn' 
						or 
							@type='isbn'
						) 
						and 
						not(
							$is-source
						)
					)">
						
			<xsl:element name="dc:identifier">
				<xsl:choose>
					<xsl:when test="@type='doi'">
						<xsl:attribute name="xsi:type" select="'eterms:DOI'"/>
						<xsl:value-of select="."/>
					</xsl:when>
					<xsl:when test="@type='issn'">
						<xsl:attribute name="xsi:type" select="'eterms:ISSN'"/>
						<xsl:value-of select="."/>
					</xsl:when>
					<xsl:when test="@type='isbn'">
						<xsl:attribute name="xsi:type" select="'eterms:ISBN'"/>
						<xsl:value-of select="."/>
					</xsl:when>
					<xsl:when test="@type='uri' or @type='url'">
						<xsl:attribute name="xsi:type" select="'eterms:URI'"/>
						<xsl:value-of select="."/>
					</xsl:when>
					<xsl:when test="@type='isi'">
						<xsl:attribute name="xsi:type" select="'eterms:ISI'"/>
						<xsl:value-of select="."/>
					</xsl:when>
					<xsl:when test="@type='localid' and $import-name = 'MPINEURO'">
						<xsl:attribute name="xsi:type" select="'eterms:OTHER'"/>
						<xsl:text>Local-ID: </xsl:text>
						<xsl:value-of select="."/>
					</xsl:when>
					<xsl:when test="@type='localid' and $import-name = 'MPIINF'">
						<xsl:attribute name="xsi:type" select="'eterms:OTHER'"/>
						<xsl:text>Local-ID: </xsl:text>
						<xsl:value-of select="."/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="xsi:type" select="'eterms:OTHER'"/>
						<xsl:value-of select="."/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
		</xsl:if>
	
	</xsl:template>
	
<!-- ***********************************************SOURCE TEMPLATES ***************************************************************** -->	


	<!-- JOURNAL TEMPLATE -->
	<xsl:template name="createJournal">
		<xsl:param name="sources-count"/>
		<xsl:param name="gen"/>
		<xsl:param name="isbn-save"/>
		<!-- TITLE -->
		<xsl:if test="journaltitle">
			<xsl:attribute name="type" select="$genre-ves/enum[. = 'journal']/@uri"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="journaltitle"/>
			</xsl:element>
		</xsl:if>		
		<!-- ALTERNATIVE TITLE -->
		<xsl:apply-templates select="journalabbreviation"/>
		
		<!-- VOLUME -->
		<xsl:apply-templates select="volume"/>
		
		<!-- ISSUE -->
		<xsl:apply-templates select="issuenr"/>
		
		<xsl:if test="not(exists(issuetitle))">
			<!-- START_PAGE -->
			<xsl:apply-templates select="spage"/>
			<!-- END-PAGE -->
			<xsl:apply-templates select="epage"/>
		</xsl:if>
		
		<xsl:if test="not(exists(issuetitle))">
			<!-- SEQUENCE_NR -->
			<xsl:apply-templates select="artnum"/>
		</xsl:if>
		
		<!-- Total number of pages -->
		<xsl:call-template name="phydescPubl"/>

		<!-- PUBLISHININFO -->
		<xsl:if test="not(exists(issuetitle)) and (exists(publisher) or exists(editiondescription))">
			<xsl:if test="not($import-name = 'MPI MoleGen' and exists(editiondescription) and not(exists(publisher)) and $gen != 'thesis')">
				<xsl:element name="eterms:publishing-info">
					<xsl:call-template name="createPublishinginfo"/>
				</xsl:element>
			</xsl:if>
		</xsl:if>
		<xsl:for-each select="../identifiers/identifier[@type != 'isbn' or not($isbn-save)]">
			<xsl:call-template name="createIDs">
				<xsl:with-param name="gen" select="$gen"/>
				<xsl:with-param name="has-source" select="true()"/>
				<xsl:with-param name="is-source" select="true()"/>
				<xsl:with-param name="sources-count" select="$sources-count"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	
	<!-- SOURCE IDENTIFIERS -->
	<!--  <xsl:template name="createSourceIdentifiers">
		<xsl:for-each select="../identifiers/identifier[@type = 'issn' or @type = 'isbn']">
			
			<xsl:element name="dc:identifier">
				<xsl:choose>
					<xsl:when test="@type='issn'">
						<xsl:attribute name="xsi:type" select="'eterms:ISSN'"/>
						<xsl:value-of select="."/>
					</xsl:when>
					<xsl:when test="@type='isbn'">
						<xsl:attribute name="xsi:type" select="'eterms:ISBN'"/>
						<xsl:value-of select="."/>
					</xsl:when>
				</xsl:choose>
			</xsl:element>
		
		</xsl:for-each>
	</xsl:template>-->
	
	<!-- ISSUE TEMPLATE -->
	<xsl:template name="createIssue">
		<xsl:param name="sources-count"/>
		<xsl:param name="issn-save"/>
		<xsl:param name="gen"/>
		<!-- TITLE -->
		<xsl:if test="issuetitle">
			<xsl:attribute name="type" select="$genre-ves/enum[. = 'issue']/@uri"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="issuetitle"/>
			</xsl:element>
		</xsl:if>
		<!-- CREATOR -->
		<xsl:for-each select="creators/creator[@type = 'issuecontributorfn']">
			<xsl:element name="eterms:creator">
				<xsl:call-template name="createCreator">
					<xsl:with-param name="source" select="true()"/>
				</xsl:call-template>
			</xsl:element>
		</xsl:for-each>
		<xsl:apply-templates select="issuecorporatebody"/>
		
		<!-- START_PAGE -->
		<xsl:apply-templates select="spage"/>
		<!-- END-PAGE -->
		<xsl:apply-templates select="epage"/>

		<!-- SEQUENCE_NR -->
		<xsl:apply-templates select="artnum"/>
			
		<!-- Total number of pages -->
		<xsl:call-template name="phydescPubl"/>
	
		<!--  <xsl:call-template name="createSourceIdentifiers"/>-->
		<xsl:for-each select="../identifiers/identifier[@type != 'issn' or not($issn-save)]">
			<xsl:call-template name="createIDs">
				<xsl:with-param name="gen" select="$gen"/>
				<xsl:with-param name="has-source" select="true()"/>
				<xsl:with-param name="is-source" select="true()"/>
				<xsl:with-param name="sources-count" select="$sources-count"/>
			</xsl:call-template>
		</xsl:for-each>
	
	</xsl:template>
	
	<!-- BOOK TEMPLATE -->
	<xsl:template name="createBook">
		<xsl:param name="sources-count"/>
		<xsl:param name="gen"/>
		<xsl:param name="issn-save"/>
		<!-- TITLE -->
		<xsl:if test="booktitle">
			<xsl:attribute name="type" select="$genre-ves/enum[. = 'book']/@uri"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="booktitle"/>
			</xsl:element>
		</xsl:if>
		<!-- CREATOR -->
		<xsl:for-each select="creators/creator[@type='bookcontributorfn' or @type='bookcreatorfn']">
			<xsl:element name="eterms:creator">
				<xsl:call-template name="createCreator">
					<xsl:with-param name="source" select="true()"/>
				</xsl:call-template>
			</xsl:element>
		</xsl:for-each>
		<xsl:apply-templates select="bookcorporatebody"/>
		<!-- VOLUME -->
		<xsl:if test="not(exists(titleofseries))">
			<xsl:apply-templates select="volume"/>
		</xsl:if>
		
		<!-- START_PAGE -->
		<xsl:apply-templates select="spage"/>
		<!-- END-PAGE -->
		<xsl:apply-templates select="epage"/>
		<!-- SEQUENCE_NR -->
		<xsl:apply-templates select="artnum"/>
		
		<!-- Total number of pages -->
		<xsl:call-template name="phydescPubl"/>
		
		<xsl:if test="exists(publisher) or exists(editiondescription)">
			<xsl:if test="not($import-name = 'MPI MoleGen' and exists(editiondescription) and not(exists(publisher)) and $gen != 'thesis')">
				<xsl:element name="eterms:publishing-info">
					<xsl:call-template name="createPublishinginfo"/>
				</xsl:element>
			</xsl:if>
		</xsl:if>
		
		<!--  <xsl:call-template name="createSourceIdentifiers"/>-->
		<xsl:for-each select="../identifiers/identifier[@type != 'issn' or not($issn-save)]">
			<xsl:call-template name="createIDs">
				<xsl:with-param name="gen" select="$gen"/>
				<xsl:with-param name="has-source" select="true()"/>
				<xsl:with-param name="is-source" select="true()"/>
				<xsl:with-param name="sources-count" select="$sources-count"/>
			</xsl:call-template>
		</xsl:for-each>
	
	</xsl:template>
	
	<!-- COLLECTED EDITION TEMPLATE -->
	<xsl:template name="createCollectedEdition">
		<xsl:param name="sources-count"/>
		<xsl:param name="gen"/>
		<xsl:param name="issn-save"/>
		<!-- TITLE -->
		<xsl:if test="booktitle">
			<xsl:attribute name="type" select="$genre-ves/enum[. = 'collected-edition']/@uri"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="booktitle"/>
			</xsl:element>
		</xsl:if>
		<!-- CREATOR -->
		<xsl:for-each select="creators/creator[@type='bookcontributorfn' or @type='bookcreatorfn']">
			<xsl:element name="eterms:creator">
				<xsl:call-template name="createCreator">
					<xsl:with-param name="source" select="true()"/>
				</xsl:call-template>
			</xsl:element>
		</xsl:for-each>
		<xsl:apply-templates select="bookcorporatebody"/>
		<!-- VOLUME -->
		<xsl:if test="not(exists(titleofseries))">
			<xsl:apply-templates select="volume"/>
		</xsl:if>
		
		<!-- START_PAGE -->
		<xsl:apply-templates select="spage"/>
		<!-- END-PAGE -->
		<xsl:apply-templates select="epage"/>
		<!-- SEQUENCE_NR -->
		<xsl:apply-templates select="artnum"/>
		
		<!-- Total number of pages -->
		<xsl:call-template name="phydescPubl"/>
		
		<xsl:if test="exists(publisher) or exists(editiondescription)">
			<xsl:if test="not($import-name = 'MPI MoleGen' and exists(editiondescription) and not(exists(publisher)) and $gen != 'thesis')">
				<xsl:element name="eterms:publishing-info">
					<xsl:call-template name="createPublishinginfo"/>
				</xsl:element>
			</xsl:if>
		</xsl:if>
		
		<!--  <xsl:call-template name="createSourceIdentifiers"/>-->
		<xsl:for-each select="../identifiers/identifier[@type != 'issn' or not($issn-save)]">
			<xsl:call-template name="createIDs">
				<xsl:with-param name="gen" select="$gen"/>
				<xsl:with-param name="has-source" select="true()"/>
				<xsl:with-param name="is-source" select="true()"/>
				<xsl:with-param name="sources-count" select="$sources-count"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	
	<!-- Encyclopedia TEMPLATE -->
	<xsl:template name="createEncyclopedia">
		<xsl:param name="sources-count"/>
		<xsl:param name="gen"/>
		<xsl:param name="issn-save"/>
		<!-- TITLE -->
		<xsl:if test="booktitle">
			<xsl:attribute name="type" select="$genre-ves/enum[. = 'encyclopedia']/@uri"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="booktitle"/>
			</xsl:element>
		</xsl:if>
		<!-- CREATOR -->
		<xsl:for-each select="creators/creator[@type='bookcontributorfn' or @type='bookcreatorfn']">
			<xsl:element name="eterms:creator">
				<xsl:call-template name="createCreator">
					<xsl:with-param name="source" select="true()"/>
				</xsl:call-template>
			</xsl:element>
		</xsl:for-each>
		<xsl:apply-templates select="bookcorporatebody"/>
		<!-- VOLUME -->
		<xsl:if test="not(exists(titleofseries))">
			<xsl:apply-templates select="volume"/>
		</xsl:if>
		
		<!-- START_PAGE -->
		<xsl:apply-templates select="spage"/>
		<!-- END-PAGE -->
		<xsl:apply-templates select="epage"/>
		<!-- SEQUENCE_NR -->
		<xsl:apply-templates select="artnum"/>
		
		<!-- Total number of pages -->
		<xsl:call-template name="phydescPubl"/>
		
		<xsl:if test="exists(publisher) or exists(editiondescription)">
			<xsl:if test="not($import-name = 'MPI MoleGen' and exists(editiondescription) and not(exists(publisher)) and $gen != 'thesis')">
				<xsl:element name="eterms:publishing-info">
					<xsl:call-template name="createPublishinginfo"/>
				</xsl:element>
			</xsl:if>
		</xsl:if>
		
		<!--  <xsl:call-template name="createSourceIdentifiers"/>-->
		<xsl:for-each select="../identifiers/identifier[@type != 'issn' or not($issn-save)]">
			<xsl:call-template name="createIDs">
				<xsl:with-param name="gen" select="$gen"/>
				<xsl:with-param name="has-source" select="true()"/>
				<xsl:with-param name="is-source" select="true()"/>
				<xsl:with-param name="sources-count" select="$sources-count"/>
			</xsl:call-template>
		</xsl:for-each>
	
	</xsl:template>
	
	<!-- Festschrift TEMPLATE -->
	<xsl:template name="createFestschrift">
		<xsl:param name="sources-count"/>
		<xsl:param name="gen"/>
		<xsl:param name="issn-save"/>
		<!-- TITLE -->
		<xsl:if test="booktitle">
			<xsl:attribute name="type" select="$genre-ves/enum[. = 'festschrift']/@uri"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="booktitle"/>
			</xsl:element>
		</xsl:if>
		<!-- CREATOR -->
		<xsl:for-each select="creators/creator[@type='bookcontributorfn' or @type='bookcreatorfn']">
			<xsl:element name="eterms:creator">
				<xsl:call-template name="createCreator">
					<xsl:with-param name="source" select="true()"/>
				</xsl:call-template>
			</xsl:element>
		</xsl:for-each>
		<xsl:apply-templates select="bookcorporatebody"/>
		<!-- VOLUME -->
		<xsl:if test="not(exists(titleofseries))">
			<xsl:apply-templates select="volume"/>
		</xsl:if>
		
		<!-- START_PAGE -->
		<xsl:apply-templates select="spage"/>
		<!-- END-PAGE -->
		<xsl:apply-templates select="epage"/>
		<!-- SEQUENCE_NR -->
		<xsl:apply-templates select="artnum"/>
		
		<!-- Total number of pages -->
		<xsl:call-template name="phydescPubl"/>
		
		<xsl:if test="exists(publisher) or exists(editiondescription)">
			<xsl:if test="not($import-name = 'MPI MoleGen' and exists(editiondescription) and not(exists(publisher)) and $gen != 'thesis')">
				<xsl:element name="eterms:publishing-info">
					<xsl:call-template name="createPublishinginfo"/>
				</xsl:element>
			</xsl:if>
		</xsl:if>
		
		<!--  <xsl:call-template name="createSourceIdentifiers"/>-->
		<xsl:for-each select="../identifiers/identifier[@type != 'issn' or not($issn-save)]">
			<xsl:call-template name="createIDs">
				<xsl:with-param name="gen" select="$gen"/>
				<xsl:with-param name="has-source" select="true()"/>
				<xsl:with-param name="is-source" select="true()"/>
				<xsl:with-param name="sources-count" select="$sources-count"/>
			</xsl:call-template>
		</xsl:for-each>
	
	</xsl:template>
	
	<xsl:template name="phydescPubl">
		<xsl:element name="eterms:total-number-of-pages">
			<xsl:value-of select="phydesc"/>
		</xsl:element>
	</xsl:template>
	
	<!-- <xsl:template name="phydescSource">
		<xsl:element name="eterms:sequence-number">
			<xsl:value-of select="phydesc"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="phydesc">
		<xsl:element name="eterms:total-number-of-pages">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>-->
	
	<xsl:template match="publisheradd">
		<xsl:element name="eterms:place">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="editiondescription">
		<xsl:element name="eterms:edition">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- SERIES TEMPLATE -->
	<xsl:template name="createSeries">
		<xsl:param name="sources-count"/>
		<xsl:param name="gen"/>
		<xsl:param name="isbn-save"/>
		<!-- TITLE -->
		<xsl:if test="exists(titleofseries)">
			<xsl:attribute name="type" select="$genre-ves/enum[. = 'series']/@uri"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="titleofseries"/>
			</xsl:element>
		</xsl:if>		
		<!-- CREATOR -->
		<xsl:for-each select="creators/creator[@type = 'seriescontributorfn']">
			<xsl:element name="eterms:creator">
				<xsl:call-template name="createCreator">
					<xsl:with-param name="source" select="true()"/>
				</xsl:call-template>
			</xsl:element>
		</xsl:for-each>
		<xsl:apply-templates select="seriescorporatebody"/>		
		
		<!-- VOLUME -->
		<xsl:choose>
			<xsl:when test="exists(volume)">
				<xsl:apply-templates select="volume"/>
			</xsl:when>
			<xsl:when test="$import-name = 'MPII' and exists(editiondescription) and $gen = 'report'">
				<eterms:volume>
					<xsl:value-of select="editiondescription"/>
				</eterms:volume>
			</xsl:when>
		</xsl:choose>
			
		<!-- ISSUE -->
		<xsl:apply-templates select="issuenr"/>
		
		<xsl:if test="not(exists(issuetitle|journaltitle|booktitle|titleofproceedings))">
			<!-- START_PAGE -->
			<xsl:apply-templates select="spage"/>
			<!-- END-PAGE -->
			<xsl:apply-templates select="epage"/>
		</xsl:if>

		<!-- <xsl:call-template name="createSourceIdentifiers"/>-->
		<xsl:for-each select="../identifiers/identifier[@type != 'isbn' or not($isbn-save)]">
			<xsl:call-template name="createIDs">
				<xsl:with-param name="gen" select="$gen"/>
				<xsl:with-param name="has-source" select="true()"/>
				<xsl:with-param name="is-source" select="true()"/>
				<xsl:with-param name="sources-count" select="$sources-count"/>
			</xsl:call-template>
		</xsl:for-each>
	
	</xsl:template>
	
	<!-- PROCEEDINGS TEMPLATE -->
	<xsl:template name="createProceedings">
		<xsl:param name="sources-count"/>
		<xsl:param name="gen"/>
		<xsl:param name="issn-save"/>
		<!-- TITLE -->
		<xsl:if test="titleofproceedings">
			<xsl:attribute name="type" select="$genre-ves/enum[. = 'proceedings']/@uri"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="titleofproceedings"/>
			</xsl:element>
			<xsl:if test="editiondescription">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="editiondescription"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="issuenr">
				<xsl:apply-templates select="issuenr"/>
			</xsl:if>
		
		</xsl:if>
		<!-- CREATOR -->
		<xsl:for-each select="creators/creator[@type = 'proceedingscontributorfn']">
			<xsl:element name="eterms:creator">
				<xsl:call-template name="createCreator">
					<xsl:with-param name="source" select="true()"/>
				</xsl:call-template>
			</xsl:element>
		</xsl:for-each>
		<!-- START_PAGE -->
		<xsl:apply-templates select="spage"/>
		<!-- END-PAGE -->
		<xsl:apply-templates select="epage"/>
				
		<!-- Total number of pages -->
		<xsl:if test="phydesc">
			<xsl:call-template name="phydescPubl"/>
		</xsl:if>
		
		<xsl:if test="exists(publisher)">
			<xsl:if test="not($import-name = 'MPI MoleGen' and exists(editiondescription) and not(exists(publisher)) and $gen != 'thesis')">
				<xsl:element name="eterms:publishing-info">
					<xsl:call-template name="createPublishinginfo"/>
				</xsl:element>
			</xsl:if>
		</xsl:if>
		
		<!-- <xsl:call-template name="createSourceIdentifiers"/>-->
		<xsl:for-each select="../identifiers/identifier[@type != 'issn' or not($issn-save)]">
			<xsl:call-template name="createIDs">
				<xsl:with-param name="gen" select="$gen"/>
				<xsl:with-param name="has-source" select="true()"/>
				<xsl:with-param name="is-source" select="true()"/>
				<xsl:with-param name="sources-count" select="$sources-count"/>
			</xsl:call-template>
		</xsl:for-each>
	
	</xsl:template>
	
	
	<xsl:template match="volume">
		<eterms:volume>
			<xsl:value-of select="."/>
		</eterms:volume>
	</xsl:template>
	
	
	<xsl:template name="createCreator">
		<xsl:param name="source" select="false()"/>
	
		<!-- CREATOR ROLE -->
		<xsl:choose>
			<xsl:when test="@role='advisor'">
				<xsl:attribute name="role" select="$creator-ves/enum[. = 'scientific advisor']/@uri"/>
			</xsl:when>
			<xsl:when test="@role='artist'">
				<xsl:attribute name="role" select="$creator-ves/enum[. = 'artist']/@uri"/>
			</xsl:when>
			<xsl:when test="@role='author'">
				<xsl:attribute name="role" select="$creator-ves/enum[. = 'author']/@uri"/>
			</xsl:when>
			<xsl:when test="@role='contributor'">
				<xsl:attribute name="role" select="$creator-ves/enum[. = 'contributor']/@uri"/>
			</xsl:when>
			<xsl:when test="@role='editor'">
				<xsl:attribute name="role" select="$creator-ves/enum[. = 'editor']/@uri"/>
			</xsl:when>
			<xsl:when test="@role='painter'">
				<xsl:attribute name="role" select="$creator-ves/enum[. = 'painter']/@uri"/>
			</xsl:when>
			<xsl:when test="@role='referee'">
				<xsl:attribute name="role" select="$creator-ves/enum[. = 'referee']/@uri"/>
			</xsl:when>
			<xsl:when test="@role='translator'">
				<xsl:attribute name="role" select="$creator-ves/enum[. = 'translator']/@uri"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:CreatorRoleNotMapped' ), concat(@role, ' is not mapped to an eSciDoc creator role'))"/>
			</xsl:otherwise>
		</xsl:choose>
		<!-- CREATOR -->
		<xsl:variable name="creatornfamily" select="creatornfamily"/>
		<xsl:variable name="creatorngiven" select="creatorngiven"/>
		<xsl:variable name="creatorngivenNew">
			<xsl:choose>
				<xsl:when test="$removeSpacesInInitials">
					<xsl:value-of select="replace(creatorngiven, '([A-Z][a-z]*\.) ([A-Z][a-z]*\.) ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)?', '$1$2$3$4$5$6$7$8')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="creatorngiven"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="creatoriniNew">
			<xsl:choose>
				<xsl:when test="$removeSpacesInInitials">
					<xsl:value-of select="replace(creatorini, '([A-Z][a-z]*\.) ([A-Z][a-z]*\.) ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)?', '$1$2$3$4$5$6$7$8')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="creatorini"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="@creatorType='individual'">
				
				<xsl:variable name="coneCreator">
					<xsl:choose>
						<xsl:when test="$CoNE = 'false' or (@internextern != 'mpg' and ($import-name = 'MPIE' or $import-name = 'MPIA'))">
							<!-- No CoNE --></xsl:when>
						<xsl:when test="$source and ($import-name = 'MPIE' or $import-name = 'MPIA')">
							<!-- No CoNE --></xsl:when>
						<xsl:when test="$source-name = 'eDoc-AEI'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for Gravitational Physics')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'FHI'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'Fritz Haber Institute')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'CBS'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for Human Cognitive and Brain Sciences')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'BPC'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for biophysical chemistry')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIK'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for Nuclear Physics')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIE'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for Extraterrestrial Physics')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIA'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for Astrophysics')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIMET'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for Meteorology')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'ETH'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for Social Anthropology')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'EVOLBIO'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for Evolutionary Biology')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPINEURO'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI of Neurobiology')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIPF'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for Plant Breeding Research')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIINF'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for Informatics')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIMF'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for Medical Research')"/>
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'External Organizations')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIMMG'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for the Study of Religious and Ethnic Diversity')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIGF'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for the Study of Societies')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIS'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for Solar System Research')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIP'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for Polymer Research')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPI MoleGen'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for Molecular Genetics')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'KHI'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'Kunsthistorisches Institut in Florenz, MPI')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPQ'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI of Quantum Optics')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIIS'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for Intelligent Systems (formerly MPI for Metals Research)')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIEVA'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'Max Planck Institute for Evolutionary Anthropology')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIKOFO'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'Max-Planck-Institut für Kohlenforschung')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIKYB'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'Max Planck Institute for Biological Cybernetics')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'BiblHertz'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'Bibliotheca Hertziana (MPI for Art History)')"/>
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'External Organizations')"/>
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for the History of Science')"/>
						</xsl:when>
						<xsl:when test="$import-name = 'MPIeR'">
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'MPI for European Legal History')"/>
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'External Organizations')"/>
							<xsl:copy-of select="Util:queryConeExact('persons', concat($creatornfamily, ', ', $creatorngiven), 'Fachbeirat des MPIeR')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy-of select="Util:queryCone('persons', concat('&quot;',$creatornfamily, ', ', $creatorngiven, '&quot;'))"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				
				<xsl:variable name="multiplePersonsFound" select="exists($coneCreator/cone/rdf:RDF/rdf:Description[@rdf:about != $coneCreator/cone/rdf:RDF/rdf:Description/@rdf:about])"/>
				
				<xsl:choose>
					<xsl:when test="$multiplePersonsFound">
						<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:MultipleCreatorsFound' ), concat('There is more than one CoNE entry matching --', concat($creatornfamily, ', ', creatorngiven), '--'))"/>
					</xsl:when>
					<xsl:when test="not(exists($coneCreator/cone/rdf:RDF/rdf:Description))">
						<xsl:comment>NOT FOUND IN CONE</xsl:comment>
						<xsl:element name="person:person">
							<xsl:element name="eterms:family-name">
								<xsl:value-of select="creatornfamily"/>
							</xsl:element>
							<xsl:choose>
								<xsl:when test="exists(creatorngiven) and not(creatorngiven='')">
									<xsl:element name="eterms:given-name">
										<xsl:value-of select="$creatorngivenNew"/>
									</xsl:element>
								</xsl:when>
								<xsl:otherwise>
									<xsl:element name="eterms:given-name">
										<xsl:value-of select="$creatoriniNew"/>
									</xsl:element>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:variable name="collection" select="../../../docaff/collection"/>
							<xsl:variable name="position" select="position()"/>
							
							<xsl:variable name="has-mpgsunit">
								<xsl:call-template name="check-equality">
									<xsl:with-param name="list" select="$collection-mapping/mapping/edoc-collection"/>
									<xsl:with-param name="value" select="//record/docaff/affiliation/mpgsunit"/>
								</xsl:call-template>
							</xsl:variable>
							
							<xsl:variable name="has-mpgunit">
								<xsl:call-template name="check-equality">
									<xsl:with-param name="list" select="$collection-mapping/mapping/edoc-collection"/>
									<xsl:with-param name="value" select="//record/docaff/affiliation/mpgunit"/>
								</xsl:call-template>
							</xsl:variable>
							
							<xsl:if test="not($source)">
								<xsl:choose>
									<xsl:when test="($import-name = 'MPIK' or $import-name = 'MPINEURO' or $import-name = 'MPIIS') and @internextern='unknown' and exists(../../../docaff/docaff_external)">
										<xsl:comment> Case MPIK for unknown user with external affiliation </xsl:comment>
										<xsl:element name="organization:organization">
											<xsl:element name="dc:title">
												<xsl:value-of select="./../../../docaff/docaff_external"/>
											</xsl:element>
											<dc:identifier>
												<xsl:value-of select="$external-ou"/>
											</dc:identifier>
										</xsl:element>
									</xsl:when>
									<xsl:when test="$import-name = 'CBS' and (@internextern='mpg' or @internextern='unknown')">
										<xsl:comment> Case CBS </xsl:comment>
									</xsl:when>
									<xsl:when test="$import-name = 'AEI' and @internextern='mpg' and exists(../../../docaff/affiliation) and ($has-mpgsunit = true() or $has-mpgunit = true())">
										<!-- Special Case for AEI -->
										<xsl:for-each select="../../../docaff/affiliation">
											<xsl:variable name="mpgunit" select="normalize-space(mpgunit)"/>
											<xsl:variable name="mpgsunit" select="normalize-space(mpgsunit)"/>
											
											<xsl:variable name="is-mpgsunit">
												<xsl:call-template name="check-equality">
													<xsl:with-param name="list" select="$collection-mapping/mapping/edoc-collection"/>
													<xsl:with-param name="value" select="$mpgsunit"/>
												</xsl:call-template>
											</xsl:variable>
											
											<xsl:variable name="is-mpgunit">
												<xsl:call-template name="check-equality">
													<xsl:with-param name="list" select="$collection-mapping/mapping/edoc-collection"/>
													<xsl:with-param name="value" select="$mpgunit"/>
												</xsl:call-template>
											</xsl:variable>
											
											<xsl:choose>
												<xsl:when test="$is-mpgsunit = true() and $mpgsunit != ''">
													<xsl:comment> Case 1a (AEI) </xsl:comment>
													<xsl:element name="organization:organization">
														<xsl:element name="dc:title">
															<xsl:value-of select="$collection-mapping/mapping[lower-case(edoc-collection) = lower-case($mpgsunit)]/escidoc-ou"/>
														</xsl:element>
														<dc:identifier>
															<xsl:value-of select="$collection-mapping/mapping[lower-case(edoc-collection) = normalize-space(lower-case($mpgsunit))]/escidoc-id"/>
														</dc:identifier>
													</xsl:element>
												</xsl:when>
												<xsl:when test="$is-mpgunit = true() and $mpgunit != ''">
													<xsl:comment> Case 1b (AEI) </xsl:comment>
													<xsl:element name="organization:organization">
														<xsl:element name="dc:title">
															<xsl:value-of select="$collection-mapping/mapping[lower-case(edoc-collection) = lower-case($mpgunit)]/escidoc-ou"/>
														</xsl:element>
														<dc:identifier>
															<xsl:value-of select="$collection-mapping/mapping[lower-case(edoc-collection) = normalize-space(lower-case($mpgunit))]/escidoc-id"/>
														</dc:identifier>
													</xsl:element>
												</xsl:when>
											</xsl:choose>
										</xsl:for-each>
									</xsl:when>
									<xsl:when test="@internextern='mpg' and $collection-mapping/mapping[lower-case(edoc-collection) = lower-case($collection)] and not(../../../docaff/affiliation/*[lower-case(.) = lower-case($collection)])">
										<xsl:comment> Case 2 </xsl:comment>
										<organization:organization>
											<dc:title>
												<xsl:value-of select="$collection-mapping/mapping[lower-case(edoc-collection) = lower-case($collection)]/escidoc-ou"/>
											</dc:title>
											<dc:identifier>
												<xsl:value-of select="$collection-mapping/mapping[lower-case(edoc-collection) = lower-case($collection)]/escidoc-id"/>
											</dc:identifier>
										</organization:organization>
									</xsl:when>
									<xsl:when test="@internextern='mpg' and ../../../docaff/affiliation and not(../../../docaff_external)">
										<xsl:comment> Case 3 </xsl:comment>
										<xsl:element name="organization:organization">
											<xsl:element name="dc:title">
												<xsl:value-of select="escidocFunctions:ou-name('root')"/>
											</xsl:element>
											<dc:identifier>
												<xsl:value-of select="$root-ou"/>
											</dc:identifier>
										</xsl:element>
									</xsl:when>
									<xsl:when test=". = ../creator[1] and @internextern='unknown' and not(../creator[@internextern = 'mpg']) and ../../../docaff/affiliation and not(../../../docaff/docaff_external)">
										
										<xsl:for-each select="../../../docaff/affiliation">
											<xsl:variable name="mpgunit" select="normalize-space(mpgunit)"/>
											<xsl:variable name="mpgsunit" select="normalize-space(mpgsunit)"/>
											<xsl:comment> Case 4 </xsl:comment>
											<xsl:if test="$collection-mapping/mapping[(lower-case(edoc-collection) = normalize-space(lower-case($mpgsunit))) or (lower-case(edoc-collection) = normalize-space(lower-case($mpgunit)))]/escidoc-ou != ''">
												<xsl:element name="organization:organization">
													<xsl:element name="dc:title">
														<xsl:choose>
															<xsl:when test="mpgsunit">
																<xsl:value-of select="$collection-mapping/mapping[lower-case(edoc-collection) = normalize-space(lower-case($mpgsunit))]/escidoc-ou"/>
															</xsl:when>
															<xsl:otherwise>
																<xsl:value-of select="$collection-mapping/mapping[lower-case(edoc-collection) = normalize-space(lower-case($mpgunit))]/escidoc-ou"/>
															</xsl:otherwise>
														</xsl:choose>
													</xsl:element>
													<dc:identifier>
														<xsl:choose>
															<xsl:when test="mpgsunit">
																<xsl:value-of select="$collection-mapping/mapping[lower-case(edoc-collection) = normalize-space(lower-case($mpgsunit))]/escidoc-id"/>
															</xsl:when>
															<xsl:otherwise>
																<xsl:value-of select="$collection-mapping/mapping[lower-case(edoc-collection) = normalize-space(lower-case($mpgunit))]/escidoc-id"/>
															</xsl:otherwise>
														</xsl:choose>
													</dc:identifier>
												</xsl:element>
											</xsl:if>
										</xsl:for-each>
									</xsl:when>
									<xsl:when test=". = ../creator[1] and ../../../docaff/docaff_external and not(../creator[@internextern = 'mpg'])">
										<xsl:comment> Case 5 </xsl:comment>
										<xsl:if test="escidocFunctions:ou-name(../../../docaff/docaff_external) != 'External Organizations'">
											<organization:organization>
												<dc:title>
													<xsl:value-of select="escidocFunctions:ou-name(../../../docaff/docaff_external)"/>
												</dc:title>
												<dc:identifier>
													<xsl:value-of select="escidocFunctions:ou-id(../../../docaff/docaff_external)"/>
												</dc:identifier>
											</organization:organization>
										</xsl:if>
									</xsl:when>
									<xsl:when test=". = ../creator[1] and not(../creator[@internextern = 'mpg'])">
										<xsl:comment> Case 6 </xsl:comment>
										<organization:organization>
											<dc:title>
												<xsl:value-of select="escidocFunctions:ou-name('root')"/>
											</dc:title>
											<dc:identifier>
												<xsl:value-of select="escidocFunctions:ou-id('root')"/>
											</dc:identifier>
										</organization:organization>
									</xsl:when>
									<xsl:when test="@internextern = 'mpg' and not(../creator[position() &lt; $position and @internextern = 'mpg'])">
										<xsl:comment> Case 7 </xsl:comment>
										<organization:organization>
											<dc:title>
												<xsl:value-of select="escidocFunctions:ou-name('root')"/>
											</dc:title>
											<dc:identifier>
												<xsl:value-of select="escidocFunctions:ou-id('root')"/>
											</dc:identifier>
										</organization:organization>
									</xsl:when>
								</xsl:choose>
							</xsl:if>
						</xsl:element>
					</xsl:when>
					<xsl:otherwise>
						<xsl:comment>CONE CREATOR</xsl:comment>
						<person:person>
							<eterms:family-name>
								<xsl:value-of select="$creatornfamily"/>
							</eterms:family-name>
							<eterms:given-name>
								<xsl:value-of select="$creatorngivenNew"/>
							</eterms:given-name>
							<dc:identifier xsi:type="eterms:CONE">
								<xsl:value-of select="$coneCreator/cone[1]/rdf:RDF[1]/rdf:Description/@rdf:about"/>
							</dc:identifier>
							
							<!-- CBS OU depend on date (affiliatedInstitution depend on publication-date) -->
							<xsl:variable name="publication-date">
								<xsl:choose>
									<xsl:when test="exists(ancestor::record/metadata/basic/datepublished)">
										<xsl:value-of select="ancestor::record/metadata/basic/datepublished"/>
									</xsl:when>
									<xsl:when test="exists(ancestor::record/metadata/basic/dateaccepted)">
										<xsl:value-of select="ancestor::record/metadata/basic/dateaccepted"/>
									</xsl:when>
									<xsl:when test="exists(ancestor::record/metadata/basic/dateofevent)">
										<xsl:value-of select="ancestor::record/metadata/basic/dateofevent"/>
									</xsl:when>
								</xsl:choose>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="$coneCreator/cone[1]/rdf:RDF[1]/rdf:Description/escidoc:position[escidocFunctions:smaller(rdf:Description/escidoc:start-date, $publication-date) and escidocFunctions:smaller($publication-date, rdf:Description/escidoc:end-date)]">
									<xsl:for-each select="$coneCreator/cone[1]/rdf:RDF[1]/rdf:Description/escidoc:position">
										<xsl:comment>pubdate: <xsl:value-of select="$publication-date"/>
										</xsl:comment>
										<xsl:comment>start: <xsl:value-of select="rdf:Description/escidoc:start-date"/>
										</xsl:comment>
										<xsl:comment>start &lt; pubdate <xsl:value-of select="escidocFunctions:smaller(rdf:Description/escidoc:start-date, $publication-date)"/>
										</xsl:comment>
										<xsl:comment>end: <xsl:value-of select="rdf:Description/escidoc:end-date"/>
										</xsl:comment>
										<xsl:comment>pubdate &lt; end<xsl:value-of select="escidocFunctions:smaller($publication-date, rdf:Description/escidoc:end-date)"/>
										</xsl:comment>
										<xsl:if test="escidocFunctions:smaller(rdf:Description/escidoc:start-date, $publication-date) and escidocFunctions:smaller($publication-date, rdf:Description/escidoc:end-date)">
											<xsl:comment> Case 8 </xsl:comment>
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
											<xsl:value-of select="$external-ou"/>
										</dc:identifier>
									</organization:organization>
								</xsl:otherwise>
							</xsl:choose>
						
						</person:person>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:comment> Case 9 </xsl:comment>
				<xsl:element name="organization:organization">
					<xsl:element name="dc:title">
						<xsl:value-of select="creatornfamily"/>
					</xsl:element>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="title">
		<xsl:element name="dc:title">
			<xsl:value-of select="title"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="journaltitle">
		<xsl:attribute name="type" select="'journal'"/>
	</xsl:template>
	<xsl:template match="booktitle">
		<xsl:attribute name="type" select="'book'"/>
	</xsl:template>
	<xsl:template match="issuetitle">
		<xsl:attribute name="type" select="'issue'"/>
	</xsl:template>
	<xsl:template match="titleofseries">
		<xsl:attribute name="type" select="'series'"/>
	</xsl:template>
	<xsl:template match="titleofproceedings">
		<xsl:attribute name="type" select="'proceedings'"/>
	</xsl:template>
	
	<!-- REVIEW-METHOD TEMPLATE -->
	<xsl:template match="refereed">
		<xsl:choose>
			<xsl:when test="../genre='Article' and exists(../journaltitle) and $source-name = 'eDoc-MPIPL'">
				<xsl:element name="eterms:review-method">
					<xsl:value-of select="$reviewMethod-ves/enum[. = 'peer-reviewed']/@uri"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test=". = 'joureview'">
				<xsl:element name="eterms:review-method">
					<xsl:value-of select="$reviewMethod-ves/enum[. = 'peer-reviewed']/@uri"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test=". = 'notrev'">
				<xsl:element name="eterms:review-method">
					<xsl:value-of select="$reviewMethod-ves/enum[. = 'no-review']/@uri"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test=". = 'intrev'">
				<xsl:element name="eterms:review-method">
					<xsl:value-of select="$reviewMethod-ves/enum[. = 'internal']/@uri"/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="issuecontributorfn">
		<xsl:call-template name="parseContributor">
			<xsl:with-param name="role" select="'editor'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="proceedingscontributorfn">
		<xsl:call-template name="parseContributor">
			<xsl:with-param name="role" select="'editor'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="seriescontributorfn">
		<xsl:call-template name="parseContributor">
			<xsl:with-param name="role" select="'editor'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="bookcontributorfn">
		<xsl:call-template name="parseContributor">
			<xsl:with-param name="role" select="'editor'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="bookcreatorfn">
		<xsl:call-template name="parseContributor">
			<xsl:with-param name="role" select="'author'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="parseContributor">
		<xsl:param name="role"/>
		
		<xsl:element name="creatorstring">
			<xsl:attribute name="role" select="$creator-ves/enum[. = $role]/@uri"/>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="journalabbreviation">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- EVENT TEMPLATE -->
	<xsl:template name="createEvent">
		<xsl:element name="event:event">
			<xsl:element name="dc:title">
				<xsl:choose>
					<xsl:when test="exists(nameofevent)">
						<xsl:value-of select="nameofevent"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Untitled Event</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				
			</xsl:element>
			<xsl:element name="eterms:start-date">
				<xsl:value-of select="dateofevent"/>
			</xsl:element>
			<xsl:element name="eterms:end-date">
				<xsl:value-of select="enddateofevent"/>
			</xsl:element>
			<xsl:element name="eterms:place">
				<xsl:value-of select="placeofevent"/>
			</xsl:element>
			<xsl:if test="invitationstatus = 'invited'">
				<xsl:element name="eterms:invitation-status">
					<xsl:value-of select="invitationstatus"/>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="artnum">
		<eterms:sequence-number>
			<xsl:value-of select="."/>
		</eterms:sequence-number>
	</xsl:template>
	<xsl:template match="spage">
		<eterms:start-page>
			<xsl:value-of select="."/>
		</eterms:start-page>
	</xsl:template>
	<xsl:template match="epage">
		<xsl:if test="$import-name != 'MPIMMG' or ../spage != .">
			<eterms:end-page>
				<xsl:value-of select="."/>
			</eterms:end-page>
		</xsl:if>
	</xsl:template>
	<xsl:template match="issuenr">
		<eterms:issue>
			<xsl:value-of select="."/>
		</eterms:issue>
	</xsl:template>
	<xsl:template match="toc">
		<xsl:element name="dcterms:tableOfContents">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!--<xsl:template match="discipline">
		<xsl:element name="dcterms:subject">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	 <xsl:template match="keywords">
		<xsl:element name="dcterms:subject">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>-->
	
	<!-- 
	################################## ORIGINAL ########################
	<xsl:template match="abstract">
		<xsl:element name="dcterms:abstract">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	################################## ORIGINAL ########################
	 -->
	<!-- #################### TEST TEST TEST ########################## -->
	<xsl:template match="abstract">
		<xsl:choose>
			<xsl:when test="$import-name = 'CBS' and (number(substring(../datepublished,1,4)) &lt;= 2007) and (../genre='Article' or ../genre='InBook')">
				<xsl:comment>
				JAHR &lt;=2007
					<xsl:value-of select="substring(../datepublished,1,4)"></xsl:value-of>
				DATUM
					<xsl:value-of select="../datepublished"/>
				GENRE ARTICLE OR INBOOK
					<xsl:value-of select="../genre"></xsl:value-of>
				</xsl:comment>
				<xsl:element name="dcterms:abstract"/>
			</xsl:when>
			<xsl:when test="$import-name = 'MPIE' or $import-name = 'MPIA'">
				<xsl:comment>Abstract not mapped</xsl:comment>
			</xsl:when>
			<xsl:otherwise>
				<xsl:comment>
				JAHR > 2007
					<xsl:value-of select="substring(../datepublished,1,4)"></xsl:value-of>
				DATUM
					<xsl:value-of select="../datepublished"/>
				GENRE OTHERS
					<xsl:value-of select="../genre"></xsl:value-of>
				</xsl:comment>
				<xsl:element name="dcterms:abstract">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
		
		<!-- 
		<xsl:when test="exists(../../basic/datepublished)">
			<xsl:value-of select="../../basic/datepublished"/>
		</xsl:when>
		 -->
	</xsl:template>
	<!-- #################### TEST TEST TEST ########################## --> 
	
	<!-- Publication dates -->
	<xsl:template name="createDates">
		<xsl:if test="exists(dateaccepted) and dateaccepted != ''">
			<xsl:element name="dcterms:dateAccepted">
				<xsl:value-of select="dateaccepted"/>
			</xsl:element>
		</xsl:if>
		<xsl:if test="exists(datecreated) and datecreated != ''">
			<xsl:element name="dcterms:created">
				<xsl:value-of select="datecreated"/>
			</xsl:element>
		</xsl:if>
		<xsl:if test="exists(datesubmitted) and datesubmitted != ''">
			<xsl:element name="dcterms:dateSubmitted">
				<xsl:value-of select="datesubmitted"/>
			</xsl:element>
		</xsl:if>
		<xsl:if test="exists(datemodified) and datemodified != ''">
			<xsl:element name="dcterms:modified">
				<xsl:value-of select="datemodified"/>
			</xsl:element>
		</xsl:if>
		<xsl:if test="exists(datepublished) and datepublished != ''">
			<xsl:choose>
				<xsl:when test="$import-name = 'MPINEURO' and exists(../identifiers/identifier[@comment != '' and @type = 'doi'])">
					<xsl:element name="eterms:published-online">
						<xsl:value-of select="datepublished"/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="$import-name = 'BiblHertz' and genre = 'online-article'">
					<xsl:element name="eterms:published-online">
						<xsl:value-of select="datepublished"/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="pubstatus = 'accepted'">
					<xsl:element name="dcterms:dateAccepted">
						<xsl:value-of select="datepublished"/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="pubstatus = 'submitted'">
					<xsl:element name="dcterms:dateSubmitted">
						<xsl:value-of select="datepublished"/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="pubstatus = 'unpublished'">
					<xsl:element name="dcterms:created">
						<xsl:value-of select="datepublished"/>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="dcterms:issued">
						<xsl:value-of select="datepublished"/>
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	<!-- old templates -->
	<xsl:template match="datepublished">
		<xsl:element name="dcterms:issued">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="datemodified">
		<xsl:element name="dcterms:modified">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="dateaccepted">
		<xsl:element name="dcterms:dateAccepted">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="datesubmitted">
		<xsl:element name="dcterms:dateSubmitted">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- End dates -->
	
	
	<xsl:template match="titlealt">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="publisher">
		<xsl:element name="dc:publisher">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="language">
		<xsl:variable name="coneLanguage">
			<xsl:if test="$CoNE = 'true'">
				<xsl:copy-of select="Util:queryCone('iso639-3', concat('&quot;', ., '&quot;'))"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="language" select="."/>
		
		<xsl:for-each select="$coneLanguage/cone/rdf:RDF/rdf:Description[dc:title = $language]/dc:identifier">
			<xsl:comment>Language: <xsl:value-of select="."/>
			</xsl:comment>
			<xsl:if test="string-length(.) = 3">
				<xsl:element name="dc:language">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="validation">
		<xsl:variable name="collectionsWithoutOuMatch">
			<xsl:copy-of select="/edoc/record/docaff/collection[not($collection-mapping/mapping/edoc-collection = .)]"/>
		</xsl:variable>
		<xsl:variable name="recordsWithoutOuMatch">
			<xsl:value-of select="/edoc/record[docaff/collection[not($collection-mapping/mapping/edoc-collection = .)]]/@id"/>
		</xsl:variable>
		<xsl:if test="$collectionsWithoutOuMatch != ''">
			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnmatchedCollection' ), concat('Collections [', $collectionsWithoutOuMatch, '] do not match any eSciDoc ou. Records: ', $recordsWithoutOuMatch))"/>
		</xsl:if>
		<xsl:variable name="affiliationsWithoutOuMatch">
			<xsl:copy-of select="/edoc/record/docaff/affiliation/mpgsunit[not($collection-mapping/mapping/edoc-collection = .)]"/>
		</xsl:variable>
		<xsl:variable name="recordsWithoutOuMatch2">
			<xsl:value-of select="/edoc/record[docaff/affiliation/mpgsunit[not($collection-mapping/mapping/edoc-collection = .)]]/@id"/>
		</xsl:variable>
		<xsl:if test="$affiliationsWithoutOuMatch != ''">
			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnmatchedCollection' ), concat('Collections [', $affiliationsWithoutOuMatch, '] do not match any eSciDoc ou. Records: ', $recordsWithoutOuMatch2))"/>
		</xsl:if>
		<xsl:variable name="mappingWithoutOuMatch">
			<xsl:copy-of select="$collection-mapping/mapping/escidoc-ou[not(. = $organizational-units//ou/@name)]"/>
		</xsl:variable>
		<xsl:if test="$mappingWithoutOuMatch != ''">
			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnmatchedCollection' ), concat('OU mappings [', $mappingWithoutOuMatch, '] do not match any eSciDoc ou.'))"/>
		</xsl:if>
		<xsl:variable name="authorOuMappingWithoutOuMatch">
			<xsl:value-of select="$authors/authors/author/departments/department[not(. = $organizational-units//ou/@name)]"/>
		</xsl:variable>
		<xsl:if test="$authorOuMappingWithoutOuMatch != ''">
			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnmatchedAuthorOU' ), concat('OU mappings [', $authorOuMappingWithoutOuMatch, '] do not match any eSciDoc ou.'))"/>
		</xsl:if>
		<!-- Uncomment this to find out which authors are not mapped. -->		<!-- <xsl:variable name="authorsWithoutMatch">			<xsl:for-each select="//creator">				<xsl:sort select="creatornfamily"/>				<xsl:sort select="creatorngiven"/>				<xsl:variable name="creatornfamily" select="creatornfamily"/>				<xsl:variable name="creatorngiven" select="creatorngiven"/>				<xsl:if test="not($authors/authors/author[aliases/alias[lower-case(familyname) = lower-case($creatornfamily) and lower-case(givenname) = lower-case($creatorngiven)]])">					<xsl:value-of select="creatornfamily"/>, <xsl:value-of select="creatorngiven"/><xsl:text></xsl:text>				</xsl:if>			</xsl:for-each>		</xsl:variable>				<xsl:if test="$authorsWithoutMatch != ''">			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnmatchedAuthor' ), concat('Authors ', $authorsWithoutMatch, ' do not match any mapped author.'))"/>		</xsl:if> -->
	</xsl:template>
	
	<xsl:template name="check-equality">
		<xsl:param name="list"/>
		<xsl:param name="value"/>
		<xsl:param name="pos" select="1"/>
		
		<xsl:variable name="list-value" select="lower-case($list[$pos])"/>
		
		<xsl:choose>
			<xsl:when test="$value[normalize-space(lower-case(.)) = $list-value]">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:when test="exists($list[$pos + 1])">
				<xsl:call-template name="check-equality">
					<xsl:with-param name="list" select="$list"/>
					<xsl:with-param name="value" select="$value"/>
					<xsl:with-param name="pos" select="$pos + 1"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	
	</xsl:template>
	
	<!-- All fields mapped into FreeKeywords are here defined since Pubman mask allow only one dcterms:subject -->
	<xsl:template name="dcTermsSubject">
		<xsl:variable name="freekeywords">
			<xsl:if test="exists(keywords)">
				<xsl:value-of select="normalize-space(keywords)"/>
				<xsl:text></xsl:text>
			</xsl:if>
			<xsl:if test="exists(discipline)">
				<xsl:value-of select="normalize-space(discipline)"/>
				<xsl:text></xsl:text>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$import-name = 'FHI' or $import-name = 'MPINEURO'">
					<xsl:if test="exists(../../docaff/docaff_researchcontext)">
						<xsl:value-of select="normalize-space(../../docaff/docaff_researchcontext)"/>
					</xsl:if>
				</xsl:when>
				<xsl:when test="$import-name = 'MPIIS'">
					<xsl:if test="exists(../../docaff/affiliation/mpgssunit[. = $mpiis-subjects/subject])">
						<xsl:for-each select="../../docaff/affiliation/mpgssunit[. = $mpiis-subjects/subject]">
							<xsl:value-of select="normalize-space(.)"/>
							<xsl:text> </xsl:text>
						</xsl:for-each>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="exists($freekeywords) and normalize-space($freekeywords) != ''">
			<xsl:element name="dcterms:subject">
				<xsl:value-of select="$freekeywords"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<!-- FHI Templates -->
	<xsl:template name="copyrightFHI">
		<xsl:element name="dc:rights">
			<xsl:value-of select="../../../rights/copyright"/>
		</xsl:element>
	</xsl:template>
		
	<!-- CBS templates -->
	
	<xsl:template name="authorcommentCBS">
		<xsl:if test="starts-with(lower-case(./basic/authorcomment), lower-case('meeting abstract'))">
			<local-tags>
				<local-tag>
					<xsl:value-of select="./basic/authorcomment"/>
				</local-tag>
			</local-tags>
		</xsl:if>
	</xsl:template>
	
	
	<!-- LocalTags template -->
	
	<xsl:template name="localTags">
		<xsl:comment>in LocalTags!</xsl:comment>
		<xsl:comment>LastModified: <xsl:value-of select="../metametadata/lastmodified"></xsl:value-of></xsl:comment>
		<xsl:comment>FullName: <xsl:value-of select="./metametadata/owner/fullname"></xsl:value-of></xsl:comment>
		<xsl:if test="$import-name = 'BiblHertz'">
			<local-tags>
				<local-tag>
					<xsl:if test="../metametadata/lastmodified">
						<xsl:value-of select="../metametadata/lastmodified"/>
					</xsl:if>
				</local-tag>
				<local-tag>
					<xsl:if test="../metametadata/owner/fullname">
							<xsl:value-of select="../metametadata/owner/fullname"/>
					</xsl:if>
				</local-tag>
			</local-tags>
		</xsl:if>
	</xsl:template>
	
	<!-- MPIE - MPIA templates -->
	<xsl:template name="abstractMPIEMPIA">
		<xsl:if test="exists(authorcomment) and ($import-name = 'MPIE' or $import-name = 'MPIA')">
			<xsl:element name="dcterms:abstract">
				<xsl:value-of select="authorcomment"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	
	<xsl:function name="escidocFunctions:smaller" as="xs:boolean">
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
	
	<xsl:function name="escidocFunctions:suffix">
		<xsl:param name="filename"/>
		<xsl:choose>
			<xsl:when test="contains($filename, '.')"><xsl:value-of select="escidocFunctions:suffix(substring-after($filename, '.'))"/></xsl:when>
			<xsl:otherwise>
				<xsl:text>.</xsl:text>
				<xsl:value-of select="$filename"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
</xsl:stylesheet>
