<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns:misc="http://www.editura.de/ns/2012/misc"
	xmlns:tools="http://www.editura.de/ns/2012/tools"
	xmlns:hidden="http://www.editura.de/ns/2012/misc-hide"
	xmlns:AuthorDecoder="java:de.mpg.escidoc.services.common.util.creators.AuthorDecoder"
	xmlns:Util="java:de.mpg.mpdl.inge.transformation.Util"
	xmlns:dc="${xsd.metadata.dc}"
	xmlns:dcterms="${xsd.metadata.dcterms}"
	xmlns:eprints="http://purl.org/eprint/terms/"
	xmlns:escidocComponents="${xsd.soap.item.components}"
	xmlns:escidocItem="${xsd.soap.item.item}"
	xmlns:escidocItemList="${xsd.soap.item.itemlist}"
	xmlns:escidocMetadataRecords="${xsd.soap.common.metadatarecords}"
	xmlns:eterms="${xsd.metadata.escidocprofile.types}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:eves="http://purl.org/escidoc/metadata/ves/0.1/"
	xmlns:file="${xsd.metadata.file}"
	xmlns:foaf="http://xmlns.com/foaf/0.1/"
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:prop="${xsd.soap.common.prop}"
	xmlns:publication="${xsd.metadata.publication}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:srel="${xsd.soap.common.srel}"
	xmlns:xlink="http://www.w3.org/1999/xlink" exclude-result-prefixes="xsl xs xd misc tools hidden AuthorDecoder Util" version="2.0">
	<xsl:import href="mapping_common-tools.xsl"/>
	<xsl:param name="misc:anonymous-name" as="xs:string?">Anonymous</xsl:param>
	<xsl:param name="misc:anonymous-organization-name" as="xs:string?">unknown organization</xsl:param>
	<xsl:param name="misc:default-organization-name" as="xs:string?">External Affiliation</xsl:param>
	<xsl:param name="inge.pubman.external.organization.id" as="xs:string?">${inge.pubman.external.organization.id}</xsl:param>
	<xsl:param name="misc:doi-resolver-prefix" as="xs:anyURI">https://doi.org/</xsl:param>
	<xsl:param name="Flavor" as="xs:string" select="'other'"/>
	<xsl:param name="misc:run-in-testmode" as="xs:boolean" select="false()"/>
	<xsl:param name="misc:logging-level" as="xs:string">INFO</xsl:param>
	<xsl:variable name="hidden:logging-level" as="xs:integer" select="tools:logging-level($misc:logging-level)"/>
	<xsl:param name="CoNE" as="xs:boolean" select="false()"/>
	<xsl:param name="misc:use-CoNE-organizations-only" as="xs:boolean" select="true()"/>
	<xsl:param name="misc:write-default-organization" as="xs:boolean" select="true()"/>
	<xsl:param name="srel:context-URI" as="xs:anyURI">InsertAProperURI</xsl:param>
	<xsl:param name="srel:content-model-URI" as="xs:anyURI">InsertAProperURI</xsl:param>
	<xsl:param name="srel:origin" as="xs:anyURI">Pubman File-Import</xsl:param>
	<xsl:param name="misc:target-format" as="xs:string">eSciDoc-publication-item-list</xsl:param>
	<xsl:param name="misc:default-publication-title" as="xs:string">Missing Title</xsl:param>
	<xsl:param name="misc:default-content-att_storage" as="xs:string">internal-managed</xsl:param>
	<xsl:param name="misc:default-file-dc-description" as="xs:string" select="concat('File downloaded at ', if ($misc:run-in-testmode) then misc:format-date-time(xs:dateTime('1900-01-01T00:00:01') ) else misc:format-date-time(current-dateTime() ) )"/>
	<xsl:param name="misc:default-prop-visibility" as="xs:string">private</xsl:param>
	<xsl:param name="misc:default-dc-rights" as="xs:string">Please inform yourself about the copyrights on this file.</xsl:param>
	<xsl:param name="misc:default-dcterms-license" as="xs:string">The terms of licensing could not be retrieved. Please check the source of the file.</xsl:param>
	<xsl:param name="inge.cone.service.url" as="xs:anyURI">${inge.cone.service.url}</xsl:param>
	<xsl:param name="misc:force-using-REST-for-CoNE-queries" as="xs:boolean">false</xsl:param>
	<xsl:variable name="hidden:inge.cone.service.url" as="xs:string" select="if ($inge.cone.service.url eq concat('${', 'inge.cone.service.url}')) then ('http://pubman.mpdl.mpg.de/cone/') else $inge.cone.service.url"/>
	<xsl:variable name="hidden:cone-query-prefix-list" as="xs:string" select="concat($hidden:inge.cone.service.url, 'persons/query?q=')"/>
	<xsl:variable name="hidden:cone-query-suffix-list" as="xs:string" select="'&amp;format=rdf'"/>
	<xsl:variable name="hidden:cone-query-prefix-detail" as="xs:string" select="concat($hidden:inge.cone.service.url, 'persons/resource/')"/>
	<xsl:variable name="hidden:cone-query-suffix-detail" as="xs:string" select="'?format=rdf'"/>
	<xsl:variable name="hidden:write-default-organization-id" as="xs:boolean" select="$misc:write-default-organization and normalize-space($inge.pubman.external.organization.id)"/>
	<xsl:template name="misc:make_escidocItemList-item-list" as="element(escidocItemList:item-list)">
		<xsl:param name="escidocItem:item" as="element(escidocItem:item)*" required="yes"/>
		<escidocItemList:item-list>
			<xsl:sequence select="$escidocItem:item"/>
		</escidocItemList:item-list>
	</xsl:template>
	<xsl:template name="misc:make_escidocItem-item" as="element(escidocItem:item)">
		<xsl:param name="escidocItem:properties" as="element(escidocItem:properties)" required="yes"/>
		<xsl:param name="escidocMetadataRecords:md-records" as="element(escidocMetadataRecords:md-records)" required="yes"/>
		<xsl:param name="escidocComponents:components" as="element(escidocComponents:components)" required="yes"/>
		<escidocItem:item>
			<xsl:sequence select="$escidocItem:properties"/>
			<xsl:sequence select="$escidocMetadataRecords:md-records"/>
			<xsl:sequence select="$escidocComponents:components"/>
		</escidocItem:item>
	</xsl:template>
	<xsl:template name="misc:make_escidocItem-properties" as="element(escidocItem:properties)">
		<xsl:param name="srel:context" as="element(srel:context)" required="no" select="misc:create_srel-context($srel:context-URI)"/>
		<xsl:param name="srel:content-model" as="element(srel:content-model)" required="no" select="misc:create_srel-content-model($srel:content-model-URI)"/>
		<xsl:param name="srel:origin" as="element(srel:origin)" required="no" select="misc:create_srel-origin($srel:origin)"/>
		<xsl:param name="prop:content-model-specific" as="element(prop:content-model-specific)" required="no">
			<xsl:call-template name="misc:make_prop-content-model-specific_default-empty"/>
		</xsl:param>
		<escidocItem:properties>
			<xsl:sequence select="$srel:context"/>
			<xsl:sequence select="$srel:content-model"/>
			<xsl:sequence select="$srel:origin"/>
			<xsl:sequence select="$prop:content-model-specific"/>
		</escidocItem:properties>
	</xsl:template>
	<xsl:function name="misc:create_srel-context" as="element(srel:context)">
		<xsl:param name="xlink:href" as="xs:anyURI"/>
		<srel:context xlink:href="{$xlink:href}"/>
	</xsl:function>
	<xsl:function name="misc:create_srel-content-model" as="element(srel:content-model)">
		<xsl:param name="xlink:href" as="xs:anyURI"/>
		<srel:content-model xlink:href="{$xlink:href}"/>
	</xsl:function>
	<xsl:function name="misc:create_srel-origin" as="element(srel:origin)">
		<xsl:param name="xlink:href" as="xs:anyURI"/>
		<srel:origin xlink:href="{$xlink:href}"/>
	</xsl:function>
	<xsl:template name="misc:make_escidocMetadataRecords-md-records" as="element(escidocMetadataRecords:md-records)">
		<xsl:param name="escidocMetadataRecords:md-record" as="element(escidocMetadataRecords:md-record)" required="yes"/>
		<escidocMetadataRecords:md-records>
			<xsl:sequence select="$escidocMetadataRecords:md-record"/>
		</escidocMetadataRecords:md-records>
	</xsl:template>
	<xsl:template name="misc:make_escidocMetadataRecords-md-record" as="element(escidocMetadataRecords:md-record)">
		<xsl:param name="publication:publication_or_file-file" as="element()" required="yes"/>
		<escidocMetadataRecords:md-record name="escidoc">
			<xsl:sequence select="$publication:publication_or_file-file"/>
		</escidocMetadataRecords:md-record>
	</xsl:template>
	<xsl:template name="misc:make_publication-publication" as="element(publication:publication)">
		<xsl:param name="att_type" as="xs:string" required="yes"/>
		<xsl:param name="eterms:creator" as="element(eterms:creator)+" required="yes"/>
		<xsl:param name="dc:title" as="element(dc:title)" required="yes"/>
		<xsl:param name="dc:language" as="element(dc:language)*" required="no"/>
		<xsl:param name="dcterms:alternative" as="element(dcterms:alternative)*" required="no"/>
		<xsl:param name="dc:identifier" as="element(dc:identifier)*" required="no"/>
		<xsl:param name="eterms:publishing-info" as="element(eterms:publishing-info)?" required="no"/>
		<xsl:param name="dcterms:created" as="element(dcterms:created)?" required="no"/>
		<xsl:param name="dcterms:modified" as="element(dcterms:modified)?" required="no"/>
		<xsl:param name="dcterms:dateSubmitted" as="element(dcterms:dateSubmitted)?" required="no"/>
		<xsl:param name="dcterms:dateAccepted" as="element(dcterms:dateAccepted)?" required="no"/>
		<xsl:param name="eterms:published-online" as="element(eterms:published-online)?" required="no"/>
		<xsl:param name="dcterms:issued" as="element(dcterms:issued)?" required="no"/>
		<xsl:param name="eterms:review-method" as="element(eterms:review-method)?" required="no"/>
		<xsl:param name="eterms:court" as="element(eterms:court)*" required="no"/>
		<xsl:param name="source:source" as="element(source:source)*" required="no"/>
		<xsl:param name="event:event" as="element(event:event)?" required="no"/>
		<xsl:param name="eterms:total-number-of-pages" as="element(eterms:total-number-of-pages)?" required="no"/>
		<xsl:param name="eterms:degree" as="element(eterms:degree)?" required="no"/>
		<xsl:param name="dcterms:abstract" as="element(dcterms:abstract)*" required="no"/>
		<xsl:param name="dc:subject" as="element(dc:subject)*" required="no"/>
		<xsl:param name="dcterms:subject" as="element(dcterms:subject)*" required="no"/>
		<xsl:param name="dcterms:tableOfContents" as="element(dcterms:tableOfContents)?" required="no"/>
		<xsl:param name="eterms:location" as="element(eterms:location)?" required="no"/>
		<publication:publication type="{$att_type}">
			<xsl:sequence select="$eterms:creator"/>
			<xsl:sequence select="$dc:title"/>
			<xsl:sequence select="$dc:language"/>
			<xsl:sequence select="$dcterms:alternative"/>
			<xsl:sequence select="$dc:identifier"/>
			<xsl:sequence select="$eterms:publishing-info"/>
			<xsl:sequence select="$dcterms:created"/>
			<xsl:sequence select="$dcterms:modified"/>
			<xsl:sequence select="$dcterms:dateSubmitted"/>
			<xsl:sequence select="$dcterms:dateAccepted"/>
			<xsl:sequence select="$eterms:published-online"/>
			<xsl:sequence select="$dcterms:issued"/>
			<xsl:sequence select="$eterms:review-method"/>
			<xsl:sequence select="$eterms:court"/>
			<xsl:sequence select="$source:source"/>
			<xsl:sequence select="$event:event"/>
			<xsl:sequence select="$eterms:total-number-of-pages"/>
			<xsl:sequence select="$eterms:degree"/>
			<xsl:sequence select="$dcterms:abstract"/>
			<xsl:sequence select="$dc:subject"/>
			<xsl:sequence select="hidden:merge_dcterms-subjects($dcterms:subject)"/>
			<xsl:sequence select="$dcterms:tableOfContents"/>
			<xsl:sequence select="$eterms:location"/>
		</publication:publication>
	</xsl:template>
	<xsl:template name="misc:make_eterms-creator_from_person" as="element(eterms:creator)">
		<xsl:param name="att_role" as="xs:string" required="yes"/>
		<xsl:param name="person:person" as="element(person:person)" required="yes"/>
		<xsl:sequence select="misc:create_eterms-creator($person:person, $att_role)"/>
	</xsl:template>
	<xsl:template name="misc:make_eterms-creator_from_organization" as="element(eterms:creator)">
		<xsl:param name="att_role" as="xs:string" required="yes"/>
		<xsl:param name="organization:organization" as="element(organization:organization)" required="yes"/>
		<xsl:sequence select="misc:create_eterms-creator($organization:organization, $att_role)"/>
	</xsl:template>
	<xsl:template name="misc:make_person-person" as="element(person:person)">
		<xsl:param name="eterms:complete-name" as="element(eterms:complete-name)" required="yes"/>
		<xsl:param name="eterms:family-name" as="element(eterms:family-name)" required="yes"/>
		<xsl:param name="eterms:given-name" as="element(eterms:given-name)?" required="no"/>
		<xsl:param name="eterms:alternative-name" as="element(eterms:alternative-name)*" required="no"/>
		<xsl:param name="eterms:person-title" as="element(eterms:person-title)*" required="no"/>
		<xsl:param name="eterms:pseudonym" as="element(eterms:pseudonym)*" required="no"/>
		<xsl:param name="organization:organization" as="element(organization:organization)*" required="no"/>
		<xsl:param name="dc:identifier" as="element(dc:identifier)*" required="no"/>
		<xsl:variable name="raw-person-person" as="element(person:person)">
			<xsl:call-template name="hidden:make_person-person">
				<xsl:with-param name="eterms:complete-name" as="element(eterms:complete-name)" select="$eterms:complete-name"/>
				<xsl:with-param name="eterms:family-name" as="element(eterms:family-name)" select="$eterms:family-name"/>
				<xsl:with-param name="eterms:given-name" as="element(eterms:given-name)?" select="$eterms:given-name"/>
				<xsl:with-param name="eterms:alternative-name" as="element(eterms:alternative-name)*" select="$eterms:alternative-name"/>
				<xsl:with-param name="eterms:person-title" as="element(eterms:person-title)*" select="$eterms:person-title"/>
				<xsl:with-param name="eterms:pseudonym" as="element(eterms:pseudonym)*" select="$eterms:pseudonym"/>
				<xsl:with-param name="organization:organization" as="element(organization:organization)*" select="$organization:organization"/>
				<xsl:with-param name="dc:identifier" as="element(dc:identifier)*" select="$dc:identifier"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="not($CoNE) and not($misc:use-CoNE-organizations-only)">
				<xsl:sequence select="$raw-person-person"/>
			</xsl:when>
			<xsl:when test="not($CoNE) and $misc:use-CoNE-organizations-only">
				<xsl:call-template name="hidden:make_person-person">
					<xsl:with-param name="eterms:complete-name" as="element(eterms:complete-name)" select="$eterms:complete-name"/>
					<xsl:with-param name="eterms:family-name" as="element(eterms:family-name)" select="$eterms:family-name"/>
					<xsl:with-param name="eterms:given-name" as="element(eterms:given-name)?" select="$eterms:given-name"/>
					<xsl:with-param name="eterms:alternative-name" as="element(eterms:alternative-name)*" select="$eterms:alternative-name"/>
					<xsl:with-param name="eterms:person-title" as="element(eterms:person-title)*" select="$eterms:person-title"/>
					<xsl:with-param name="eterms:pseudonym" as="element(eterms:pseudonym)*" select="$eterms:pseudonym"/>
					<xsl:with-param name="organization:organization" as="element(organization:organization)*" select="()"/>
					<xsl:with-param name="dc:identifier" as="element(dc:identifier)*" select="$dc:identifier"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="hidden:enrich-with-CoNE($raw-person-person)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="hidden:make_person-person" as="element(person:person)">
		<xsl:param name="eterms:complete-name" as="element(eterms:complete-name)" required="yes"/>
		<xsl:param name="eterms:family-name" as="element(eterms:family-name)" required="yes"/>
		<xsl:param name="eterms:given-name" as="element(eterms:given-name)?" required="no"/>
		<xsl:param name="eterms:alternative-name" as="element(eterms:alternative-name)*" required="no"/>
		<xsl:param name="eterms:person-title" as="element(eterms:person-title)*" required="no"/>
		<xsl:param name="eterms:pseudonym" as="element(eterms:pseudonym)*" required="no"/>
		<xsl:param name="organization:organization" as="element(organization:organization)*" required="no"/>
		<xsl:param name="dc:identifier" as="element(dc:identifier)*" required="no"/>
		<person:person>
			<xsl:sequence select="$eterms:complete-name"/>
			<xsl:sequence select="$eterms:family-name"/>
			<xsl:sequence select="$eterms:given-name"/>
			<xsl:sequence select="$eterms:alternative-name"/>
			<xsl:sequence select="$eterms:person-title"/>
			<xsl:sequence select="$eterms:pseudonym"/>
			<xsl:choose>
				<xsl:when test="$organization:organization">
					<xsl:sequence select="$organization:organization"/>
				</xsl:when>
				<xsl:when test="$misc:write-default-organization">
					<xsl:call-template name="misc:make_organization-organization">
						<xsl:with-param name="dc:title" select="misc:create_dc-title($misc:default-organization-name)"/>
						<xsl:with-param name="dc:identifier" as="element(dc:identifier)?">
							<xsl:if test="$hidden:write-default-organization-id">
								<xsl:sequence select="misc:create_dc-identifier('', $inge.pubman.external.organization.id)"/>
							</xsl:if>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
			<xsl:sequence select="$dc:identifier"/>
		</person:person>
	</xsl:template>
	<xsl:template name="misc:make_organization-organization" as="element(organization:organization)">
		<xsl:param name="dc:title" as="element(dc:title)" required="yes"/>
		<xsl:param name="eterms:address" as="element(eterms:address)?" required="no"/>
		<xsl:param name="dc:identifier" as="element(dc:identifier)?" required="no"/>
		<organization:organization>
			<xsl:sequence select="$dc:title"/>
			<xsl:sequence select="$eterms:address"/>
			<xsl:sequence select="$dc:identifier"/>
		</organization:organization>
	</xsl:template>
	<xsl:template name="misc:make_source-source" as="element(source:source)">
		<xsl:param name="att_type" as="xs:string" required="yes"/>
		<xsl:param name="dc:title" as="element(dc:title)" required="yes"/>
		<xsl:param name="dcterms:alternative" as="element(dcterms:alternative)*" required="no"/>
		<xsl:param name="eterms:creator" as="element(eterms:creator)*" required="no"/>
		<xsl:param name="eterms:volume" as="element(eterms:volume)?" required="no"/>
		<xsl:param name="eterms:issue" as="element(eterms:issue)?" required="no"/>
		<xsl:param name="eterms:issued" as="element(eterms:issued)?" required="no"/>
		<xsl:param name="eterms:start-page" as="element(eterms:start-page)?" required="no"/>
		<xsl:param name="eterms:end-page" as="element(eterms:end-page)?" required="no"/>
		<xsl:param name="eterms:sequence-number" as="element(eterms:sequence-number)?" required="no"/>
		<xsl:param name="eterms:total-number-of-pages" as="element(eterms:total-number-of-pages)?" required="no"/>
		<xsl:param name="eterms:publishing-info" as="element(eterms:publishing-info)?" required="no"/>
		<xsl:param name="dc:identifier" as="element(dc:identifier)*" required="no"/>
		<xsl:param name="source:source" as="element(source:source)*" required="no"/>
		<source:source type="{$att_type}">
			<xsl:sequence select="$dc:title"/>
			<xsl:sequence select="$dcterms:alternative"/>
			<xsl:sequence select="$eterms:creator"/>
			<xsl:sequence select="$eterms:volume"/>
			<xsl:sequence select="$eterms:issue"/>
			<xsl:sequence select="$eterms:issued"/>
			<xsl:sequence select="$eterms:start-page"/>
			<xsl:sequence select="$eterms:end-page"/>
			<xsl:sequence select="$eterms:sequence-number"/>
			<xsl:sequence select="$eterms:total-number-of-pages"/>
			<xsl:sequence select="$eterms:publishing-info"/>
			<xsl:sequence select="$dc:identifier"/>
			<xsl:sequence select="$source:source"/>
		</source:source>
	</xsl:template>
	<xsl:template name="misc:make_event-event" as="element(event:event)">
		<xsl:param name="dc:title" as="element(dc:title)" required="yes"/>
		<xsl:param name="dcterms:alternative" as="element(dcterms:alternative)*" required="no"/>
		<xsl:param name="eterms:start-date" as="element(eterms:start-date)?" required="no"/>
		<xsl:param name="eterms:end-date" as="element(eterms:end-date)?" required="no"/>
		<xsl:param name="eterms:place" as="element(eterms:place)?" required="no"/>
		<xsl:param name="eterms:invitation-status" as="element(eterms:invitation-status)?" required="no"/>
		<event:event>
			<xsl:sequence select="$dc:title"/>
			<xsl:sequence select="$dcterms:alternative"/>
			<xsl:sequence select="$eterms:start-date"/>
			<xsl:sequence select="$eterms:end-date"/>
			<xsl:sequence select="$eterms:place"/>
			<xsl:sequence select="$eterms:invitation-status"/>
		</event:event>
	</xsl:template>
	<xsl:template name="misc:make_eterms-publishing-info" as="element(eterms:publishing-info)">
		<xsl:param name="dc:publisher" as="element(dc:publisher)" required="yes"/>
		<xsl:param name="eterms:place" as="element(eterms:place)*" required="no"/>
		<xsl:param name="eterms:edition" as="element(eterms:edition)?" required="no"/>
		<eterms:publishing-info>
			<xsl:sequence select="$dc:publisher"/>
			<xsl:sequence select="$eterms:place"/>
			<xsl:sequence select="$eterms:edition"/>
		</eterms:publishing-info>
	</xsl:template>
	<xsl:template name="misc:make_escidocComponents-components" as="element(escidocComponents:components)">
		<xsl:param name="escidocComponents:component" as="element(escidocComponents:component)*" required="yes"/>
		<escidocComponents:components>
			<xsl:sequence select="$escidocComponents:component"/>
		</escidocComponents:components>
	</xsl:template>
	<xsl:template name="misc:make_escidocComponents-component" as="element(escidocComponents:component)">
		<xsl:param name="escidocComponents:properties" as="element(escidocComponents:properties)" required="yes"/>
		<xsl:param name="escidocComponents:content" as="element(escidocComponents:content)" required="yes"/>
		<xsl:param name="escidocMetadataRecords:md-records" as="element(escidocMetadataRecords:md-records)?" required="no"/>
		<escidocComponents:component>
			<xsl:sequence select="$escidocComponents:properties"/>
			<xsl:sequence select="$escidocComponents:content"/>
			<xsl:sequence select="$escidocMetadataRecords:md-records"/>
		</escidocComponents:component>
	</xsl:template>
	<xsl:template name="misc:make_escidocComponents-properties" as="element(escidocComponents:properties)">
		<xsl:param name="prop:creation-date" as="element(prop:creation-date)?" required="no"/>
		<xsl:param name="srel:created-by" as="element(srel:created-by)?" required="no"/>
		<xsl:param name="prop:description" as="element(prop:description)?" required="no"/>
		<xsl:param name="prop:valid-status" as="element(prop:valid-status)?" required="no"/>
		<xsl:param name="prop:visibility" as="element(prop:visibility)" required="yes"/>
		<xsl:param name="prop:pid" as="element(prop:pid)?" required="no"/>
		<xsl:param name="prop:content-category" as="element(prop:content-category)" required="yes"/>
		<xsl:param name="prop:file-name" as="element(prop:file-name)?" required="no"/>
		<xsl:param name="prop:mime-type" as="element(prop:mime-type)?" required="no"/>
		<xsl:param name="prop:checksum" as="element(prop:checksum)?" required="no"/>
		<xsl:param name="prop:checksum-algorithm" as="element(prop:checksum-algorithm)?" required="no"/>
		<escidocComponents:properties>
			<xsl:sequence select="$prop:creation-date"/>
			<xsl:sequence select="$srel:created-by"/>
			<xsl:sequence select="$prop:description"/>
			<xsl:sequence select="$prop:valid-status"/>
			<xsl:sequence select="$prop:visibility"/>
			<xsl:sequence select="$prop:pid"/>
			<xsl:sequence select="$prop:content-category"/>
			<xsl:sequence select="$prop:file-name"/>
			<xsl:sequence select="$prop:mime-type"/>
			<xsl:sequence select="$prop:checksum"/>
			<xsl:sequence select="$prop:checksum-algorithm"/>
		</escidocComponents:properties>
	</xsl:template>
	<xsl:template name="misc:make_escidocComponents-content" as="element(escidocComponents:content)">
		<xsl:param name="att_xlink-title" as="xs:string?" required="no"/>
		<xsl:param name="att_xlink-href" as="xs:string?" required="no"/>
		<xsl:param name="att_storage" as="xs:string" required="yes"/>
		<escidocComponents:content>
			<xsl:attribute name="xlink:type" select="'simple'"/>
			<xsl:attribute name="xlink:title" select="$att_xlink-title"/>
			<xsl:attribute name="xlink:href" select="$att_xlink-href"/>
			<xsl:attribute name="storage">
				<xsl:choose>
					<xsl:when test="$att_storage = ('internal-managed', 'external-url', 'external-managed')">
						<xsl:sequence select="$att_storage"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="misc:message">
							<xsl:with-param name="level">ERROR</xsl:with-param>
							<xsl:with-param name="show-context" select="false()"/>
							<xsl:with-param name="message">[mapping-commons.xsl#misc:make_escidocComponents-content] "
								<xsl:sequence select="$att_storage"/>" is not a valid input for parameter $att_storage
							</xsl:with-param>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</escidocComponents:content>
	</xsl:template>
	<xsl:template name="misc:make_file-file" as="element(file:file)">
		<xsl:param name="dc:title" as="element(dc:title)" required="yes"/>
		<xsl:param name="dc:description" as="element(dc:description)?" required="no"/>
		<xsl:param name="dc:identifier" as="element(dc:identifier)*" required="no"/>
		<xsl:param name="eterms:content-category" as="element(eterms:content-category)?" required="no"/>
		<xsl:param name="dc:format" as="element(dc:format)*" required="no"/>
		<xsl:param name="dcterms:extent" as="element(dcterms:extent)?" required="no"/>
		<xsl:param name="dcterms:available" as="element(dcterms:available)?" required="no"/>
		<xsl:param name="dcterms:dateCopyrighted" as="element(dcterms:dateCopyrighted)?" required="no"/>
		<xsl:param name="dc:rights" as="element(dc:rights)*" required="no"/>
		<xsl:param name="dcterms:license" as="element(dcterms:license)*" required="no"/>
		<file:file>
			<xsl:sequence select="$dc:title"/>
			<xsl:sequence select="$dc:description"/>
			<xsl:sequence select="$dc:identifier"/>
			<xsl:sequence select="$eterms:content-category"/>
			<xsl:sequence select="$dc:format"/>
			<xsl:sequence select="$dcterms:extent"/>
			<xsl:sequence select="$dcterms:available"/>
			<xsl:sequence select="$dcterms:dateCopyrighted"/>
			<xsl:sequence select="$dc:rights"/>
			<xsl:sequence select="$dcterms:license"/>
		</file:file>
	</xsl:template>
	<xsl:template name="misc:make_prop-content-model-specific" as="element(prop:content-model-specific)">
		<xsl:param name="xs_any" as="element()*" required="yes"/>
		<prop:content-model-specific>
			<xsl:sequence select="$xs_any"/>
		</prop:content-model-specific>
	</xsl:template>
	<xsl:template name="misc:make_prop-content-model-specific_default-empty" as="element(prop:content-model-specific)">
		<prop:content-model-specific>
			<local-tags>
				<local-tag/>
			</local-tags>
		</prop:content-model-specific>
	</xsl:template>
	<xsl:function name="misc:create_eterms-creator" as="element(eterms:creator)">
		<xsl:param name="person-person_or_organization-organization" as="element()"/>
		<xsl:param name="att_role" as="xs:string"/>
		<eterms:creator role="{$att_role}">
			<xsl:sequence select="$person-person_or_organization-organization"/>
		</eterms:creator>
	</xsl:function>
	<xsl:function name="misc:create_eterms-review-method" as="element(eterms:review-method)">
		<xsl:param name="ReviewMethodEnum" as="xs:string?"/>
		<eterms:review-method>
			<xsl:value-of select="$ReviewMethodEnum"/>
		</eterms:review-method>
	</xsl:function>
	<xsl:function name="misc:create_eterms-total-number-of-pages" as="element(eterms:total-number-of-pages)">
		<xsl:param name="total-number-of-pages" as="xs:string?"/>
		<eterms:total-number-of-pages>
			<xsl:value-of select="$total-number-of-pages"/>
		</eterms:total-number-of-pages>
	</xsl:function>
	<xsl:function name="misc:create_eterms-location" as="element(eterms:location)">
		<xsl:param name="location" as="xs:string?"/>
		<eterms:location>
			<xsl:value-of select="$location"/>
		</eterms:location>
	</xsl:function>
	<xsl:function name="misc:create_eterms-volume" as="element(eterms:volume)">
		<xsl:param name="volume" as="xs:string?"/>
		<eterms:volume>
			<xsl:value-of select="$volume"/>
		</eterms:volume>
	</xsl:function>
	<xsl:function name="misc:create_eterms-issue" as="element(eterms:issue)">
		<xsl:param name="issue" as="xs:string?"/>
		<eterms:issue>
			<xsl:value-of select="$issue"/>
		</eterms:issue>
	</xsl:function>
	<xsl:function name="misc:create_eterms-start-page" as="element(eterms:start-page)">
		<xsl:param name="start-page" as="xs:string?"/>
		<eterms:start-page>
			<xsl:value-of select="$start-page"/>
		</eterms:start-page>
	</xsl:function>
	<xsl:function name="misc:create_eterms-end-page" as="element(eterms:end-page)">
		<xsl:param name="end-page" as="xs:string?"/>
		<eterms:end-page>
			<xsl:value-of select="$end-page"/>
		</eterms:end-page>
	</xsl:function>
	<xsl:function name="misc:create_eterms-sequence-number" as="element(eterms:sequence-number)">
		<xsl:param name="sequence-number" as="xs:string?"/>
		<eterms:sequence-number>
			<xsl:value-of select="$sequence-number"/>
		</eterms:sequence-number>
	</xsl:function>
	<xsl:function name="misc:create_eterms-published-online" as="element(eterms:published-online)">
		<xsl:param name="published-online" as="xs:string?"/>
		<eterms:published-online xsi:type="dcterms:W3CDTF">
			<xsl:value-of select="$published-online"/>
		</eterms:published-online>
	</xsl:function>
	<xsl:function name="misc:create_eterms-edition" as="element(eterms:edition)">
		<xsl:param name="edition" as="xs:string?"/>
		<eterms:edition>
			<xsl:value-of select="$edition"/>
		</eterms:edition>
	</xsl:function>
	<xsl:function name="misc:create_eterms-complete-name" as="element(eterms:complete-name)">
		<xsl:param name="complete-name" as="xs:string?"/>
		<eterms:complete-name>
			<xsl:value-of select="$complete-name"/>
		</eterms:complete-name>
	</xsl:function>
	<xsl:function name="misc:create_eterms-complete-name" as="element(eterms:complete-name)">
		<xsl:param name="given-name" as="xs:string?"/>
		<xsl:param name="middle-initial" as="xs:string?"/>
		<xsl:param name="middle-name" as="xs:string?"/>
		<xsl:param name="family-name" as="xs:string?"/>
		<xsl:param name="name-suffix" as="xs:string?"/>
		<xsl:sequence select="misc:create_eterms-complete-name( normalize-space(string-join( ($given-name, if (normalize-space($middle-name)) then $middle-name else $middle-initial, if (normalize-space($family-name)) then $family-name else $misc:anonymous-name, $name-suffix), ' ') ) )"/>
	</xsl:function>
	<xsl:function name="misc:create_eterms-family-name" as="element(eterms:family-name)">
		<xsl:param name="family-name" as="xs:string?"/>
		<xsl:param name="anonymous-and-warn" as="xs:boolean"/>
		<eterms:family-name>
			<xsl:value-of select="normalize-space($family-name)"/>
			<xsl:if test="$anonymous-and-warn">
				<xsl:if test="not(normalize-space($family-name) )">
					<xsl:value-of select="$misc:anonymous-name"/>
					<xsl:call-template name="misc:message">
						<xsl:with-param name="level">WARN</xsl:with-param>
						<xsl:with-param name="show-context" select="false()"/>
						<xsl:with-param name="message">[mapping-commons.xsl#misc:create_eterms-family-name] empty family name</xsl:with-param>
					</xsl:call-template>
				</xsl:if>
			</xsl:if>
		</eterms:family-name>
	</xsl:function>
	<xsl:function name="misc:create_eterms-family-name" as="element(eterms:family-name)">
		<xsl:param name="family-name" as="xs:string?"/>
		<xsl:sequence select="misc:create_eterms-family-name($family-name, true())"/>
	</xsl:function>
	<xsl:function name="misc:create_eterms-given-name" as="element(eterms:given-name)">
		<xsl:param name="given-name" as="xs:string?"/>
		<eterms:given-name>
			<xsl:value-of select="$given-name"/>
		</eterms:given-name>
	</xsl:function>
	<xsl:function name="misc:create_eterms-alternative-name" as="element(eterms:alternative-name)">
		<xsl:param name="alternative-name" as="xs:string?"/>
		<eterms:alternative-name>
			<xsl:value-of select="$alternative-name"/>
		</eterms:alternative-name>
	</xsl:function>
	<xsl:function name="misc:create_eterms-person-title" as="element(eterms:person-title)">
		<xsl:param name="person-title" as="xs:string?"/>
		<eterms:person-title>
			<xsl:value-of select="$person-title"/>
		</eterms:person-title>
	</xsl:function>
	<xsl:function name="misc:create_eterms-pseudonym" as="element(eterms:pseudonym)">
		<xsl:param name="pseudonym" as="xs:string?"/>
		<eterms:pseudonym>
			<xsl:value-of select="$pseudonym"/>
		</eterms:pseudonym>
	</xsl:function>
	<xsl:function name="misc:create_eterms-city" as="element(eterms:city)">
		<xsl:param name="city" as="xs:string?"/>
		<eterms:city>
			<xsl:value-of select="$city"/>
		</eterms:city>
	</xsl:function>
	<xsl:function name="misc:create_eterms-country" as="element(eterms:country)">
		<xsl:param name="country" as="xs:string?"/>
		<eterms:country>
			<xsl:value-of select="$country"/>
		</eterms:country>
	</xsl:function>
	<xsl:function name="misc:create_eterms-start-date" as="element(eterms:start-date)">
		<xsl:param name="start-date" as="xs:string?"/>
		<eterms:start-date>
			<xsl:value-of select="$start-date"/>
		</eterms:start-date>
	</xsl:function>
	<xsl:function name="misc:create_eterms-end-date" as="element(eterms:end-date)">
		<xsl:param name="end-date" as="xs:string?"/>
		<eterms:end-date>
			<xsl:value-of select="$end-date"/>
		</eterms:end-date>
	</xsl:function>
	<xsl:function name="misc:create_eterms-place" as="element(eterms:place)">
		<xsl:param name="place" as="xs:string?"/>
		<eterms:place>
			<xsl:value-of select="$place"/>
		</eterms:place>
	</xsl:function>
	<xsl:function name="misc:create_eterms-invitation-status" as="element(eterms:invitation-status)">
		<xsl:param name="invitation-status" as="xs:string?"/>
		<eterms:invitation-status>
			<xsl:value-of select="$invitation-status"/>
		</eterms:invitation-status>
	</xsl:function>
	<xsl:function name="misc:create_eterms-address" as="element(eterms:address)">
		<xsl:param name="address" as="xs:string?"/>
		<eterms:address>
			<xsl:value-of select="$address"/>
		</eterms:address>
	</xsl:function>
	<xsl:function name="misc:create_eterms-organization-type" as="element(eterms:organization-type)">
		<xsl:param name="organization-type" as="xs:string?"/>
		<eterms:organization-type>
			<xsl:value-of select="$organization-type"/>
		</eterms:organization-type>
	</xsl:function>
	<xsl:function name="misc:create_dc-title" as="element(dc:title)">
		<xsl:param name="title" as="xs:string?"/>
		<dc:title>
			<xsl:value-of select="$title"/>
		</dc:title>
	</xsl:function>
	<xsl:function name="misc:create_dc-publisher" as="element(dc:publisher)">
		<xsl:param name="publisher" as="xs:string?"/>
		<dc:publisher>
			<xsl:value-of select="$publisher"/>
		</dc:publisher>
	</xsl:function>
	<xsl:function name="misc:create_dc-subject" as="element(dc:subject)">
		<xsl:param name="subject" as="xs:string?"/>
		<xsl:sequence select="misc:create_dc-subject($subject, '')"/>
	</xsl:function>
	<xsl:function name="misc:create_dc-subject" as="element(dc:subject)">
		<xsl:param name="subject" as="xs:string?"/>
		<xsl:param name="xml-lang" as="xs:string?"/>
		<dc:subject>
			<xsl:if test="normalize-space($xml-lang)">
				<xsl:attribute name="xml:lang" select="$xml-lang"/>
			</xsl:if>
			<xsl:value-of select="$subject"/>
		</dc:subject>
	</xsl:function>
	<xsl:function name="misc:create_dcterms-subject" as="element(dcterms:subject)">
		<xsl:param name="subject" as="xs:string?"/>
		<xsl:sequence select="misc:create_dcterms-subject($subject, '')"/>
	</xsl:function>
	<xsl:function name="misc:create_dcterms-subject" as="element(dcterms:subject)">
		<xsl:param name="subject" as="xs:string?"/>
		<xsl:param name="xml-lang" as="xs:string?"/>
		<dcterms:subject>
			<xsl:if test="normalize-space($xml-lang)">
				<xsl:attribute name="xml:lang" select="$xml-lang"/>
			</xsl:if>
			<xsl:value-of select="$subject"/>
		</dcterms:subject>
	</xsl:function>
	<xsl:function name="misc:create_dc-identifier" as="element(dc:identifier)">
		<xsl:param name="xsi-type" as="xs:string?"/>
		<xsl:param name="identifier" as="xs:string?"/>
		<dc:identifier>
			<xsl:if test="normalize-space($xsi-type)">
				<xsl:attribute name="xsi:type" select="normalize-space($xsi-type)"/>
			</xsl:if>
			<xsl:value-of select="$identifier"/>
		</dc:identifier>
	</xsl:function>
	<xsl:function name="misc:create_dc-identifier_from_arxiv-id" as="element(dc:identifier)">
		<xsl:param name="arxiv-id" as="xs:string?"/>
		<xsl:sequence select="misc:create_dc-identifier('eterms:ARXIV', $arxiv-id)"/>
	</xsl:function>
	<xsl:function name="misc:create_dc-identifier_from_bmc-id" as="element(dc:identifier)">
		<xsl:param name="bmc-id" as="xs:string?"/>
		<xsl:sequence select="misc:create_dc-identifier('eterms:BMC', $bmc-id)"/>
	</xsl:function>
	<xsl:function name="misc:create_dc-identifier_from_cone-uri" as="element(dc:identifier)">
		<xsl:param name="cone-uri" as="xs:string?"/>
		<xsl:sequence select="misc:create_dc-identifier('eterms:CONE', $cone-uri)"/>
	</xsl:function>
	<xsl:function name="misc:create_dc-identifier_from_doi" as="element(dc:identifier)">
		<xsl:param name="doi" as="xs:string?"/>
		<xsl:sequence select="misc:create_dc-identifier('eterms:DOI', $doi)"/>
	</xsl:function>
	<xsl:function name="misc:create_dc-identifier_from_edoc-number" as="element(dc:identifier)">
		<xsl:param name="edoc-number" as="xs:string?"/>
		<xsl:sequence select="misc:create_dc-identifier('eterms:EDOC', $edoc-number)"/>
	</xsl:function>
	<xsl:function name="misc:create_dc-identifier_from_isbn" as="element(dc:identifier)">
		<xsl:param name="isbn" as="xs:string?"/>
		<xsl:sequence select="misc:create_dc-identifier('eterms:ISBN', $isbn)"/>
	</xsl:function>
	<xsl:function name="misc:create_dc-identifier_from_issn" as="element(dc:identifier)">
		<xsl:param name="issn" as="xs:string?"/>
		<xsl:sequence select="misc:create_dc-identifier('eterms:ISSN', $issn)"/>
	</xsl:function>
	<xsl:function name="misc:create_dc-identifier_from_other" as="element(dc:identifier)">
		<xsl:param name="other-identifier" as="xs:string?"/>
		<xsl:sequence select="misc:create_dc-identifier('eterms:OTHER', $other-identifier)"/>
	</xsl:function>
	<xsl:function name="misc:create_dc-identifier_from_pii" as="element(dc:identifier)">
		<xsl:param name="pii" as="xs:string?"/>
		<xsl:sequence select="misc:create_dc-identifier('eterms:PII', $pii)"/>
	</xsl:function>
	<xsl:function name="misc:create_dc-identifier_from_pmc" as="element(dc:identifier)">
		<xsl:param name="pmc" as="xs:string?"/>
		<xsl:sequence select="misc:create_dc-identifier('eterms:PMC', $pmc)"/>
	</xsl:function>
	<xsl:function name="misc:create_dc-identifier_from_pmid" as="element(dc:identifier)">
		<xsl:param name="pmid" as="xs:string?"/>
		<xsl:sequence select="misc:create_dc-identifier('eterms:PMID', $pmid)"/>
	</xsl:function>
	<xsl:function name="misc:create_dc-identifier_from_uri" as="element(dc:identifier)">
		<xsl:param name="uri" as="xs:string?"/>
		<xsl:sequence select="misc:create_dc-identifier('eterms:URI', $uri)"/>
	</xsl:function>
	<xsl:function name="misc:create_dc-identifier_from_coden" as="element(dc:identifier)">
		<xsl:param name="CODEN" as="xs:string?"/>
		<xsl:sequence select="misc:create_dc-identifier_from_other(concat('(CODEN) ', $CODEN) )"/>
	</xsl:function>
	<xsl:function name="misc:create_dc-language" as="element(dc:language)">
		<xsl:param name="ISO_639-3" as="xs:string?"/>
		<dc:language xsi:type="dcterms:ISO639-3">
			<xsl:value-of select="$ISO_639-3"/>
		</dc:language>
	</xsl:function>
	<xsl:function name="misc:create_dc-description" as="element(dc:description)">
		<xsl:param name="description" as="xs:string?"/>
		<dc:description>
			<xsl:value-of select="$description"/>
		</dc:description>
	</xsl:function>
	<xsl:function name="misc:create_dc-rights" as="element(dc:rights)">
		<xsl:param name="rights" as="xs:string?"/>
		<dc:rights>
			<xsl:value-of select="$rights"/>
		</dc:rights>
	</xsl:function>
	<xsl:function name="misc:create_dc-format" as="element(dc:format)">
		<xsl:param name="mediatype" as="xs:string?"/>
		<dc:format xsi:type="dcterms:IMT">
			<xsl:value-of select="$mediatype"/>
		</dc:format>
	</xsl:function>
	<xsl:function name="misc:create_dcterms-alternative" as="element(dcterms:alternative)">
		<xsl:param name="alternative-title" as="xs:string?"/>
		<xsl:sequence select="misc:create_dcterms-alternative($alternative-title, '')"/>
	</xsl:function>
	<xsl:function name="misc:create_dcterms-alternative" as="element(dcterms:alternative)">
		<xsl:param name="alternative-title" as="xs:string?"/>
		<xsl:param name="type" as="xs:string?"/>
		<dcterms:alternative>
			<xsl:if test="normalize-space($type)">
				<xsl:attribute name="xsi:type" select="$type"/>
			</xsl:if>
			<xsl:value-of select="$alternative-title"/>
		</dcterms:alternative>
	</xsl:function>
	<xsl:function name="misc:create_dcterms-created" as="element(dcterms:created)">
		<xsl:param name="date-created" as="xs:string?"/>
		<dcterms:created xsi:type="dcterms:W3CDTF">
			<xsl:value-of select="$date-created"/>
		</dcterms:created>
	</xsl:function>
	<xsl:function name="misc:create_dcterms-modified" as="element(dcterms:modified)">
		<xsl:param name="date-modified" as="xs:string?"/>
		<dcterms:modified xsi:type="dcterms:W3CDTF">
			<xsl:value-of select="$date-modified"/>
		</dcterms:modified>
	</xsl:function>
	<xsl:function name="misc:create_dcterms-dateSubmitted" as="element(dcterms:dateSubmitted)">
		<xsl:param name="dateSubmitted" as="xs:string?"/>
		<dcterms:dateSubmitted xsi:type="dcterms:W3CDTF">
			<xsl:value-of select="$dateSubmitted"/>
		</dcterms:dateSubmitted>
	</xsl:function>
	<xsl:function name="misc:create_dcterms-dateAccepted" as="element(dcterms:dateAccepted)">
		<xsl:param name="dateAccepted" as="xs:string?"/>
		<dcterms:dateAccepted xsi:type="dcterms:W3CDTF">
			<xsl:value-of select="$dateAccepted"/>
		</dcterms:dateAccepted>
	</xsl:function>
	<xsl:function name="misc:create_dcterms-issued" as="element(dcterms:issued)">
		<xsl:param name="date-issued" as="xs:string?"/>
		<dcterms:issued xsi:type="dcterms:W3CDTF">
			<xsl:value-of select="$date-issued"/>
		</dcterms:issued>
	</xsl:function>
	<xsl:function name="misc:create_dcterms-abstract" as="element(dcterms:abstract)">
		<xsl:param name="abstract" as="xs:string?"/>
		<xsl:sequence select="misc:create_dcterms-abstract($abstract, '')"/>
	</xsl:function>
	<xsl:function name="misc:create_dcterms-abstract" as="element(dcterms:abstract)">
		<xsl:param name="abstract" as="xs:string?"/>
		<xsl:param name="xml-lang" as="xs:string?"/>
		<dcterms:abstract>
			<xsl:if test="normalize-space($xml-lang)">
				<xsl:attribute name="xml:lang" select="$xml-lang"/>
			</xsl:if>
			<xsl:value-of select="$abstract"/>
		</dcterms:abstract>
	</xsl:function>
	<xsl:function name="misc:create_dcterms-tableOfContents" as="element(dcterms:tableOfContents)">
		<xsl:param name="tableOfContents" as="xs:string?"/>
		<dcterms:tableOfContents>
			<xsl:value-of select="$tableOfContents"/>
		</dcterms:tableOfContents>
	</xsl:function>
	<xsl:function name="misc:create_dcterms-extent" as="element(dcterms:extent)">
		<xsl:param name="extent" as="xs:string?"/>
		<dcterms:extent>
			<xsl:value-of select="$extent"/>
		</dcterms:extent>
	</xsl:function>
	<xsl:function name="misc:create_dcterms-dateCopyrighted" as="element(dcterms:dateCopyrighted)">
		<xsl:param name="dateCopyrighted" as="xs:string?"/>
		<dcterms:dateCopyrighted>
			<xsl:value-of select="$dateCopyrighted"/>
		</dcterms:dateCopyrighted>
	</xsl:function>
	<xsl:function name="misc:create_dcterms-license" as="element(dcterms:license)">
		<xsl:param name="license" as="xs:string?"/>
		<dcterms:license>
			<xsl:value-of select="$license"/>
		</dcterms:license>
	</xsl:function>
	<xsl:function name="misc:create_prop-visibility" as="element(prop:visibility)">
		<xsl:param name="visibility" as="xs:string?"/>
		<prop:visibility>
			<xsl:value-of select="$visibility"/>
		</prop:visibility>
	</xsl:function>
	<xsl:function name="misc:create_prop-content-category" as="element(prop:content-category)">
		<xsl:param name="content-category" as="xs:string?"/>
		<prop:content-category>
			<xsl:value-of select="$content-category"/>
		</prop:content-category>
	</xsl:function>
	<xsl:function name="misc:create_prop-mime-type" as="element(prop:mime-type)">
		<xsl:param name="mime-type" as="xs:string?"/>
		<prop:mime-type>
			<xsl:value-of select="$mime-type"/>
		</prop:mime-type>
	</xsl:function>
	<xsl:function name="misc:eterms_creator-is-author" as="xs:boolean">
		<xsl:param name="eterms:creator" as="element(eterms:creator)"/>
		<xsl:sequence select="exists($eterms:creator[@role eq 'http://www.loc.gov/loc.terms/relators/AUT'])"/>
	</xsl:function>
	<xsl:function name="misc:eterms_creator-is-editor" as="xs:boolean">
		<xsl:param name="eterms:creator" as="element(eterms:creator)"/>
		<xsl:sequence select="exists($eterms:creator[@role eq 'http://www.loc.gov/loc.terms/relators/EDT'])"/>
	</xsl:function>
	<xsl:function name="misc:person_person-to-name" as="xs:string?">
		<xsl:param name="person:person" as="element(person:person)"/>
		<xsl:param name="use-misc_anonymous-name-if-empty" as="xs:boolean"/>
		<xsl:variable name="complete-name" as="xs:string" select="normalize-space($person:person/eterms:complete-name)"/>
		<xsl:variable name="given-name" as="xs:string" select="normalize-space($person:person/eterms:given-name)"/>
		<xsl:variable name="family-name" as="xs:string" select="normalize-space($person:person/eterms:family-name)"/>
		<xsl:choose>
			<xsl:when test="$given-name and $family-name">
				<xsl:sequence select="concat($family-name, ', ', $given-name)"/>
			</xsl:when>
			<xsl:when test="$given-name">
				<xsl:sequence select="$given-name"/>
			</xsl:when>
			<xsl:when test="$family-name">
				<xsl:sequence select="$family-name"/>
			</xsl:when>
			<xsl:when test="$complete-name">
				<xsl:sequence select="$complete-name"/>
			</xsl:when>
			<xsl:when test="$use-misc_anonymous-name-if-empty">
				<xsl:sequence select="$misc:anonymous-name"/>
			</xsl:when>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:organization_organization-to-string" as="xs:string">
		<xsl:param name="organization:organization" as="element(organization:organization)"/>
		<xsl:sequence select="normalize-space(concat( normalize-space($organization:organization/dc:title), if (normalize-space($organization:organization/eterms:address)) then ', ' else (), $organization:organization/eterms:address ) )"/>
	</xsl:function>
	<xsl:function name="misc:total-number-of-pages" as="xs:string?">
		<xsl:param name="publication_publication-or-source_source" as="element()"/>
		<xsl:variable name="start-page" as="xs:string" select="normalize-space($publication_publication-or-source_source/eterms:start-page)"/>
		<xsl:variable name="end-page" as="xs:string" select="normalize-space($publication_publication-or-source_source/eterms:end-page)"/>
		<xsl:choose>
			<xsl:when test="normalize-space($publication_publication-or-source_source/eterms:total-number-of-pages)">
				<xsl:sequence select="normalize-space($publication_publication-or-source_source/eterms:total-number-of-pages)"/>
			</xsl:when>
			<xsl:when test="misc:total-number-of-pages($start-page, $end-page)">
				<xsl:sequence select="misc:total-number-of-pages($start-page, $end-page)"/>
			</xsl:when>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:total-number-of-pages" as="xs:string?">
		<xsl:param name="start-page" as="xs:string?"/>
		<xsl:param name="end-page" as="xs:string?"/>
		<xsl:variable name="_start-page" as="xs:string" select="misc:clean-pages($start-page)"/>
		<xsl:variable name="_end-page" as="xs:string" select="misc:clean-pages($end-page)"/>
		<xsl:if test="($_start-page castable as xs:integer) and ($_end-page castable as xs:integer)">
			<xsl:sequence select="string(xs:integer($_end-page) - xs:integer($_start-page) + 1)"/>
		</xsl:if>
	</xsl:function>
	<xsl:function name="misc:dc_identifier-is-online-resource" as="xs:boolean">
		<xsl:param name="dc:identifier" as="element()"/>
		<xsl:sequence select="boolean(misc:dc_identifier-to-url($dc:identifier))"/>
	</xsl:function>
	<xsl:function name="misc:dc_identifier-to-url" as="xs:string?">
		<xsl:param name="dc:identifier" as="element()"/>
		<xsl:variable name="dc_identifier_prepared" as="xs:string?" select="normalize-space($dc:identifier)"/>
		<xsl:choose>
			<xsl:when test="not($dc:identifier/self::dc:identifier)"/>
			<xsl:when test="not($dc_identifier_prepared)"/>
			<xsl:when test="some $i in ('http:', 'doi:') satisfies starts-with($dc_identifier_prepared, $i)">
				<xsl:sequence select="$dc_identifier_prepared"/>
			</xsl:when>
			<xsl:when test="$dc:identifier/@xsi:type eq 'eterms:URI'">
				<xsl:sequence select="$dc_identifier_prepared"/>
			</xsl:when>
			<xsl:when test="$dc:identifier/@xsi:type eq 'eterms:DOI'">
				<xsl:sequence select="concat($misc:doi-resolver-prefix, $dc_identifier_prepared)"/>
			</xsl:when>
			<xsl:when test="$dc:identifier/@xsi:type eq 'eterms:ARXIV'">
				<xsl:choose>
					<xsl:when test="starts-with(lower-case($dc_identifier_prepared), 'arxiv:')">
						<xsl:sequence select="concat('http://arxiv.org/abs/', normalize-space(substring-after($dc_identifier_prepared, ':') ) )"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:sequence select="concat('http://arxiv.org/abs/', $dc_identifier_prepared)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:publication-publication-is-journal-article" as="xs:boolean">
		<xsl:param name="publication:publication" as="element(publication:publication)"/>
		<xsl:sequence select="normalize-space($publication:publication/@type) eq 'http://purl.org/escidoc/metadata/ves/publication-types/article' or normalize-space($publication:publication/@type) eq 'http://purl.org/eprint/type/review-article'"/>
	</xsl:function>
	<xsl:function name="misc:format-volume-issue" as="xs:string?">
		<xsl:param name="volume" as="xs:string?"/>
		<xsl:param name="issue" as="xs:string?"/>
		<xsl:variable name="_volume" as="xs:string?" select="normalize-space($volume)"/>
		<xsl:variable name="_issue" as="xs:string?" select="normalize-space($issue)"/>
		<xsl:choose>
			<xsl:when test="$volume and $issue">
				<xsl:sequence select="concat('Vol. ', $volume, ' (', $issue, ')')"/>
			</xsl:when>
			<xsl:when test="$volume">
				<xsl:sequence select="concat('Vol. ', $volume)"/>
			</xsl:when>
			<xsl:when test="$issue">
				<xsl:sequence select="concat('Issue ', $issue)"/>
			</xsl:when>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:format-date-time" as="xs:string">
		<xsl:param name="date-time" as="xs:dateTime"/>
		<xsl:sequence select="format-dateTime($date-time, '[Y0001]-[M01]-[D01] [H01]:[m01]')"/>
	</xsl:function>
	<xsl:function name="misc:SICI-for-773-q" as="xs:string">
		<xsl:param name="volume" as="xs:string?"/>
		<xsl:param name="issue" as="xs:string?"/>
		<xsl:param name="pages" as="xs:string?"/>
		<xsl:param name="start-page" as="xs:string?"/>
		<xsl:param name="end-page" as="xs:string?"/>
		<xsl:variable name="_volume" as="xs:string" select="normalize-space($volume)"/>
		<xsl:variable name="_issue" as="xs:string" select="normalize-space($issue)"/>
		<xsl:variable name="_pages" as="xs:string" select="normalize-space($pages)"/>
		<xsl:variable name="_start-page" as="xs:string" select="normalize-space($start-page)"/>
		<xsl:variable name="_end-page" as="xs:string" select="normalize-space($end-page)"/>
		<xsl:variable name="item" as="xs:string" select="string-join(($_volume[. castable as xs:integer], $_issue[. castable as xs:integer])[normalize-space(.)], ':')"/>
		<xsl:variable name="contribution" as="xs:string">
			<xsl:choose>
				<xsl:when test="matches($_pages, '^[0-9]+(-[0-9]+)?$')">
					<xsl:sequence select="$_pages"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:sequence select="string-join(($_start-page[. castable as xs:integer], $_end-page[. castable as xs:integer])[normalize-space(.)], '-')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$item or $contribution">
				<xsl:sequence select="concat($item, '&lt;', $contribution)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="''"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:parse-773-q-SICI" as="element()*">
		<xsl:param name="SICI" as="xs:string?"/>
		<xsl:variable name="item" as="xs:string" select="normalize-space(if (contains($SICI, '&lt;')) then substring-before($SICI, '&lt;') else $SICI)"/>
		<xsl:variable name="volume" as="xs:string" select="if (contains($item, ':')) then substring-before($item, ':') else $item"/>
		<xsl:variable name="issue" as="xs:string" select="substring-after($item, ':')"/>
		<xsl:variable name="contribution" as="xs:string" select="substring-after($SICI, '&lt;')"/>
		<xsl:variable name="start-page" as="xs:string" select="if (contains($contribution, '-')) then substring-before($contribution, '-') else $contribution"/>
		<xsl:variable name="end-page" as="xs:string" select="substring-after($contribution, '-')"/>
		<xsl:variable name="total-number-of-pages" as="xs:string?" select="misc:total-number-of-pages($start-page, $end-page)"/>
		<xsl:if test="$volume">
			<xsl:sequence select="misc:create_eterms-volume($volume)"/>
		</xsl:if>
		<xsl:if test="$issue">
			<xsl:sequence select="misc:create_eterms-issue($issue)"/>
		</xsl:if>
		<xsl:if test="$start-page">
			<xsl:sequence select="misc:create_eterms-start-page($start-page)"/>
		</xsl:if>
		<xsl:if test="$end-page">
			<xsl:sequence select="misc:create_eterms-end-page($end-page)"/>
		</xsl:if>
		<xsl:if test="$total-number-of-pages">
			<xsl:sequence select="misc:create_eterms-total-number-of-pages($total-number-of-pages)"/>
		</xsl:if>
	</xsl:function>
	<xsl:function name="misc:parse-pages" as="xs:string*">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:variable name="cleaned-pages" as="xs:string" select="misc:clean-pages($input)"/>
		<xsl:choose>
			<xsl:when test="not(normalize-space($cleaned-pages))"/>
			<xsl:when test="matches($cleaned-pages, '^[0-9]+$')">
				<xsl:sequence select="$cleaned-pages"/>
			</xsl:when>
			<xsl:when test="matches($cleaned-pages, '^[0-9]+-[0-9]+$')">
				<xsl:sequence select="substring-before($cleaned-pages, '-'), substring-after($cleaned-pages, '-')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">DEBUG</xsl:with-param>
					<xsl:with-param name="message">[mapping_commons.xsl#misc:parse-pages] can't parse input "
						<xsl:value-of select="$input"/>"
					</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:clean-pages" as="xs:string">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:variable name="prepared" as="xs:string" select="translate(normalize-space($input), ' ', '')"/>
		<xsl:variable name="cleaned" as="xs:string" select=" replace( lower-case($prepared), 'p(\.|(age)(s)?)|s(\.|(eite)(n)?)|f+\.?', '' )"/>
		<xsl:choose>
			<xsl:when test="matches($cleaned, '^[0-9]+(-[0-9]+)?$')">
				<xsl:sequence select="$cleaned"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="$prepared"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:clean-volume-issue" as="xs:string">
		<xsl:param name="volume-or-issue" as="xs:string?"/>
		<xsl:variable name="prepared" as="xs:string" select="normalize-space($volume-or-issue)"/>
		<xsl:variable name="cleaned" as="xs:string" select=" normalize-space( replace( lower-case($prepared), 'v(ol(ume)?)?\.?|i(ss(ue)?)?\.?|f+\.?|[().]', '' ) )"/>
		<xsl:choose>
			<xsl:when test="matches($cleaned, '^[0-9]+((-|/)[0-9]+)?$')">
				<xsl:sequence select="$cleaned"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="$prepared"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:dcterms-extent-from-file-uri" as="element(dcterms:extent)?">
		<xsl:param name="download-uri" as="xs:string?"/>
		<xsl:choose>
			<xsl:when test="not(normalize-space($download-uri))"/>
			<xsl:when test="function-available('Util:getSize')">
				<xsl:variable name="temp-size" as="xs:string?" select="Util:getSize($download-uri)" use-when="function-available('Util:getSize')"/>
				<xsl:variable name="temp-size" as="xs:string?" select="''" use-when="not(function-available('Util:getSize'))"/>
				<xsl:if test="normalize-space($temp-size)">
					<xsl:sequence select="misc:create_dcterms-extent($temp-size)"/>
				</xsl:if>
			</xsl:when>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:dc-identifier" as="element(dc:identifier)?">
		<xsl:param name="identifier-string" as="xs:string?"/>
		<xsl:variable name="splitted-identifier-string" as="xs:string+" select="misc:split-identifier-string($identifier-string)"/>
		<xsl:variable name="found-type" as="xs:string" select="$splitted-identifier-string[1]"/>
		<xsl:variable name="parsed-type" as="xs:string" select="misc:parse-identifier-types($found-type)"/>
		<xsl:variable name="identifier" as="xs:string" select="$splitted-identifier-string[2]"/>
		<xsl:if test="$identifier">
			<xsl:sequence select="misc:create_dc-identifier($parsed-type, concat ( if ($parsed-type ne 'eterms:OTHER') then () else if ($found-type = ('', 'eterms:OTHER')) then () else concat('(', $found-type, ')'), $identifier ) )"/>
		</xsl:if>
	</xsl:function>
	<xsl:function name="misc:parse-identifier-types" as="xs:string">
		<xsl:param name="string" as="xs:string?"/>
		<xsl:variable name="prepared" as="xs:string" select="for $i in normalize-space(upper-case($string) ) return (if (starts-with($i, 'ETERMS:')) then substring-after($i, 'ETERMS:') else $i)"/>
		<xsl:choose>
			<xsl:when test="$prepared eq 'ESCIDOC'">
				<xsl:sequence select="'eterms:ESCIDOC'"/>
			</xsl:when>
			<xsl:when test="$prepared eq 'EDOC'">
				<xsl:sequence select="'eterms:EDOC'"/>
			</xsl:when>
			<xsl:when test="$prepared eq 'ISSN'">
				<xsl:sequence select="'eterms:ISSN'"/>
			</xsl:when>
			<xsl:when test="$prepared eq 'ISBN'">
				<xsl:sequence select="'eterms:ISBN'"/>
			</xsl:when>
			<xsl:when test="$prepared eq 'CONE'">
				<xsl:sequence select="'eterms:CONE'"/>
			</xsl:when>
			<xsl:when test="$prepared eq 'DOI'">
				<xsl:sequence select="'eterms:DOI'"/>
			</xsl:when>
			<xsl:when test="$prepared eq 'ISI'">
				<xsl:sequence select="'eterms:ISI'"/>
			</xsl:when>
			<xsl:when test="$prepared eq 'PND'">
				<xsl:sequence select="'eterms:PND'"/>
			</xsl:when>
			<xsl:when test="$prepared eq 'URN'">
				<xsl:sequence select="'eterms:URN'"/>
			</xsl:when>
			<xsl:when test="$prepared eq 'URI'">
				<xsl:sequence select="'eterms:URI'"/>
			</xsl:when>
			<xsl:when test="$prepared eq 'PII'">
				<xsl:sequence select="'eterms:PII'"/>
			</xsl:when>
			<xsl:when test="$prepared eq 'PMID'">
				<xsl:sequence select="'eterms:PMID'"/>
			</xsl:when>
			<xsl:when test="$prepared eq 'ARXIV'">
				<xsl:sequence select="'eterms:ARXIV'"/>
			</xsl:when>
			<xsl:when test="$prepared eq 'PMC'">
				<xsl:sequence select="'eterms:PMC'"/>
			</xsl:when>
			<xsl:when test="$prepared eq 'BMC'">
				<xsl:sequence select="'eterms:BMC'"/>
			</xsl:when>
			<xsl:when test="$prepared eq 'ZDB'">
				<xsl:sequence select="'eterms:ZDB'"/>
			</xsl:when>
			<xsl:when test="$prepared eq 'SFX'">
				<xsl:sequence select="'eterms:SFX'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="'eterms:OTHER'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:split-identifier-string" as="xs:string+">
		<xsl:param name="identifier-string" as="xs:string?"/>
		<xsl:variable name="prepared" as="xs:string" select="normalize-space($identifier-string)"/>
		<xsl:variable name="klammer-match" as="xs:string" select="'^(\((.+)\) ?)(.+)$'"/>
		<xsl:variable name="double-colon-match" as="xs:string" select="'^(([^:]+:[^:]+): ?)(.+)$'"/>
		<xsl:variable name="single-colon-match" as="xs:string" select="'^(([^:]+): ?)(.+)$'"/>
		<xsl:choose>
			<xsl:when test="matches($prepared, $klammer-match)">
				<xsl:variable name="typegroup" as="xs:string" select="replace($prepared, $klammer-match, '$1')"/>
				<xsl:variable name="found-type" as="xs:string" select="replace($prepared, $klammer-match, '$2')"/>
				<xsl:variable name="identifier" as="xs:string" select="replace($prepared, $klammer-match, '$3')"/>
				<xsl:sequence select="$found-type, $identifier"/>
			</xsl:when>
			<xsl:when test="matches($prepared, $double-colon-match)">
				<xsl:variable name="typegroup" as="xs:string" select="replace($prepared, $double-colon-match, '$1')"/>
				<xsl:variable name="found-type" as="xs:string" select="replace($prepared, $double-colon-match, '$2')"/>
				<xsl:variable name="identifier" as="xs:string" select="replace($prepared, $double-colon-match, '$3')"/>
				<xsl:sequence select="$found-type, $identifier"/>
			</xsl:when>
			<xsl:when test="matches($prepared, $single-colon-match)">
				<xsl:variable name="typegroup" as="xs:string" select="replace($prepared, $single-colon-match, '$1')"/>
				<xsl:variable name="found-type" as="xs:string" select="replace($prepared, $single-colon-match, '$2')"/>
				<xsl:variable name="identifier" as="xs:string" select="replace($prepared, $single-colon-match, '$3')"/>
				<xsl:sequence select="$found-type, $identifier"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="'', $prepared"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<misc:mapping-table group="language codes">
		<misc:mapping>
			<misc:source>alb</misc:source>
			<misc:target>sqi</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>arm</misc:source>
			<misc:target>hye</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>baq</misc:source>
			<misc:target>eus</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>tib</misc:source>
			<misc:target>bod</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>bur</misc:source>
			<misc:target>mya</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>cze</misc:source>
			<misc:target>ces</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>chi</misc:source>
			<misc:target>zho</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>wel</misc:source>
			<misc:target>cym</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>ger</misc:source>
			<misc:target>deu</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>dut</misc:source>
			<misc:target>nld</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>gre</misc:source>
			<misc:target>ell</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>per</misc:source>
			<misc:target>fas</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>fre</misc:source>
			<misc:target>fra</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>geo</misc:source>
			<misc:target>kat</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>ice</misc:source>
			<misc:target>isl</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>mac</misc:source>
			<misc:target>mkd</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>mao</misc:source>
			<misc:target>mri</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>may</misc:source>
			<misc:target>msa</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>rum</misc:source>
			<misc:target>ron</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>slo</misc:source>
			<misc:target>slk</misc:target>
		</misc:mapping>
	</misc:mapping-table>
	<xsl:variable name="hidden:iso-639-2_diff" as="xs:string+" select=" ('alb', 'arm', 'baq', 'tib', 'bur', 'cze', 'chi', 'wel', 'ger', 'dut', 'gre', 'per', 'fre', 'geo', 'ice', 'mac', 'mao', 'may', 'rum', 'slo')"/>
	<xsl:variable name="hidden:iso-639-3_diff" as="xs:string+" select=" ('sqi', 'hye', 'eus', 'bod', 'mya', 'ces', 'zho', 'cym', 'deu', 'nld', 'ell', 'fas', 'fra', 'kat', 'isl', 'mkd', 'mri', 'msa', 'ron', 'slk')"/>
	<xsl:variable name="hidden:valid_iso-639-1_values" as="xs:string+" select=" ('ab', 'aa', 'af', 'ak', 'sq', 'am', 'ar', 'an', 'hy', 'as', 'av', 'ae', 'ay', 'az', 'bm', 'ba', 'eu', 'be', 'bn', 'bh', 'bi', 'bs', 'br', 'bg', 'my', 'ca', 'ch', 'ce', 'ny', 'zh', 'cv', 'kw', 'co', 'cr', 'hr', 'cs', 'da', 'dv', 'nl', 'dz', 'en', 'eo', 'et', 'ee', 'fo', 'fj', 'fi', 'fr', 'ff', 'gl', 'ka', 'de', 'el', 'gn', 'gu', 'ht', 'ha', 'he', 'hz', 'hi', 'ho', 'hu', 'ia', 'id', 'ie', 'ga', 'ig', 'ik', 'io', 'is', 'it', 'iu', 'ja', 'jv', 'kl', 'kn', 'kr', 'ks', 'kk', 'km', 'ki', 'rw', 'ky', 'kv', 'kg', 'ko', 'ku', 'kj', 'la', 'lb', 'lg', 'li', 'ln', 'lo', 'lt', 'lu', 'lv', 'gv', 'mk', 'mg', 'ms', 'ml', 'mt', 'mi', 'mr', 'mh', 'mn', 'na', 'nv', 'nb', 'nd', 'ne', 'ng', 'nn', 'no', 'ii', 'nr', 'oc', 'oj', 'cu', 'om', 'or', 'os', 'pa', 'pi', 'fa', 'pl', 'ps', 'pt', 'qu', 'rm', 'rn', 'ro', 'ru', 'sa', 'sc', 'sd', 'se', 'sm', 'sg', 'sr', 'gd', 'sn', 'si', 'sk', 'sl', 'so', 'st', 'es', 'su', 'sw', 'ss', 'sv', 'ta', 'te', 'tg', 'th', 'ti', 'bo', 'tk', 'tl', 'tn', 'to', 'tr', 'ts', 'tt', 'tw', 'ty', 'ug', 'uk', 'ur', 'uz', 've', 'vi', 'vo', 'wa', 'cy', 'wo', 'fy', 'xh', 'yi', 'yo', 'za', 'zu')"/>
	<xsl:variable name="hidden:valid_iso-639-2-B_values" as="xs:string+" select="('abk', 'aar', 'afr', 'aka', 'alb', 'amh', 'ara', 'arg', 'arm', 'asm', 'ava', 'ave', 'aym', 'aze', 'bam', 'bak', 'baq', 'bel', 'ben', 'bih', 'bis', 'bos', 'bre', 'bul', 'bur', 'cat', 'cha', 'che', 'nya', 'chi', 'chv', 'cor', 'cos', 'cre', 'hrv', 'cze', 'dan', 'div', 'dut', 'dzo', 'eng', 'epo', 'est', 'ewe', 'fao', 'fij', 'fin', 'fre', 'ful', 'glg', 'geo', 'ger', 'gre', 'grn', 'guj', 'hat', 'hau', 'heb', 'her', 'hin', 'hmo', 'hun', 'ina', 'ind', 'ile', 'gle', 'ibo', 'ipk', 'ido', 'ice', 'ita', 'iku', 'jpn', 'jav', 'kal', 'kan', 'kau', 'kas', 'kaz', 'khm', 'kik', 'kin', 'kir', 'kom', 'kon', 'kor', 'kur', 'kua', 'lat', 'ltz', 'lug', 'lim', 'lin', 'lao', 'lit', 'lub', 'lav', 'glv', 'mac', 'mlg', 'may', 'mal', 'mlt', 'mao', 'mar', 'mah', 'mon', 'nau', 'nav', 'nob', 'nde', 'nep', 'ndo', 'nno', 'nor', 'iii', 'nbl', 'oci', 'oji', 'chu', 'orm', 'ori', 'oss', 'pan', 'pli', 'per', 'pol', 'pus', 'por', 'que', 'roh', 'run', 'rum', 'rus', 'san', 'srd', 'snd', 'sme', 'smo', 'sag', 'srp', 'gla', 'sna', 'sin', 'slo', 'slv', 'som', 'sot', 'spa', 'sun', 'swa', 'ssw', 'swe', 'tam', 'tel', 'tgk', 'tha', 'tir', 'tib', 'tuk', 'tgl', 'tsn', 'ton', 'tur', 'tso', 'tat', 'twi', 'tah', 'uig', 'ukr', 'urd', 'uzb', 'ven', 'vie', 'vol', 'wln', 'wel', 'wol', 'fry', 'xho', 'yid', 'yor', 'zha', 'zul')"/>
	<xsl:function name="misc:is-iso-639-2-b" as="xs:boolean">
		<xsl:param name="language-code" as="xs:string?"/>
		<xsl:choose>
			<xsl:when test="not(normalize-space($language-code))">
				<xsl:sequence select="false()"/>
			</xsl:when>
			<xsl:when test="index-of($hidden:valid_iso-639-2-B_values, $language-code)">
				<xsl:sequence select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:is-iso-639-1" as="xs:boolean">
		<xsl:param name="language-code" as="xs:string?"/>
		<xsl:choose>
			<xsl:when test="not(normalize-space($language-code))">
				<xsl:sequence select="false()"/>
			</xsl:when>
			<xsl:when test="index-of($hidden:valid_iso-639-1_values, $language-code)">
				<xsl:sequence select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:iso-639-2_to_iso-639-3" as="xs:string">
		<xsl:param name="iso-639-2" as="xs:string?"/>
		<xsl:variable name="index-diff" as="xs:integer?" select="index-of($hidden:iso-639-2_diff, string($iso-639-2))"/>
		<xsl:variable name="index-639-3" as="xs:integer?" select="index-of($hidden:iso-639-3_diff, string($iso-639-2))"/>
		<xsl:choose>
			<xsl:when test="$index-diff">
				<xsl:sequence select="$hidden:iso-639-3_diff[$index-diff]"/>
			</xsl:when>
			<xsl:when test="$index-639-3">
				<xsl:sequence select="$iso-639-2"/>
			</xsl:when>
			<xsl:when test="misc:is-iso-639-2-b($iso-639-2)">
				<xsl:sequence select="$iso-639-2"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="''"/>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">INFO</xsl:with-param>
					<xsl:with-param name="message">[mapping-commons.xsl#misc:iso-639-2_to_iso-639-3] can't convert »
						<xsl:value-of select="$iso-639-2"/>« to ISO-639-3
					</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:iso-639-3_to_iso-639-2" as="xs:string">
		<xsl:param name="iso-639-3" as="xs:string?"/>
		<xsl:variable name="index-diff" as="xs:integer?" select="index-of($hidden:iso-639-3_diff, string($iso-639-3))"/>
		<xsl:choose>
			<xsl:when test="$index-diff">
				<xsl:sequence select="$hidden:iso-639-2_diff[$index-diff]"/>
			</xsl:when>
			<xsl:when test="misc:is-iso-639-2-b($iso-639-3)">
				<xsl:sequence select="$iso-639-3"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="''"/>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">INFO</xsl:with-param>
					<xsl:with-param name="message">[mapping-commons.xsl#misc:iso-639-3_to_iso-639-2] can't convert »
						<xsl:value-of select="$iso-639-3"/>« to ISO-639-2
					</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:iso-639-1_to_iso-639-2" as="xs:string">
		<xsl:param name="iso-639-1" as="xs:string?"/>
		<xsl:variable name="position-1" as="xs:integer?" select="index-of($hidden:valid_iso-639-1_values, string($iso-639-1))"/>
		<xsl:choose>
			<xsl:when test="$position-1">
				<xsl:sequence select="$hidden:valid_iso-639-2-B_values[$position-1]"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="''"/>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">INFO</xsl:with-param>
					<xsl:with-param name="message">[mapping-commons.xsl#misc:iso-639-1_to_iso-639-2] can't convert »
						<xsl:value-of select="$iso-639-1"/>« to ISO-639-2
					</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:iso-639-1_to_iso-639-3" as="xs:string">
		<xsl:param name="iso-639-1" as="xs:string?"/>
		<xsl:variable name="iso-639-2" as="xs:string" select="misc:iso-639-1_to_iso-639-2($iso-639-1)"/>
		<xsl:variable name="iso-639-3" as="xs:string" select="misc:iso-639-2_to_iso-639-3($iso-639-2)"/>
		<xsl:choose>
			<xsl:when test="$iso-639-3">
				<xsl:sequence select="$iso-639-3"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="''"/>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">INFO</xsl:with-param>
					<xsl:with-param name="message">[mapping-commons.xsl#misc:iso-639-1_to_iso-639-3] can't convert »
						<xsl:value-of select="$iso-639-1"/>« to ISO-639-3
					</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="hidden:merge_dcterms-subjects" as="element(dcterms:subject)?">
		<xsl:param name="dcterms:subjects" as="element(dcterms:subject)*"/>
		<xsl:variable name="keywords" as="xs:string*">
			<xsl:for-each-group select="$dcterms:subjects[normalize-space(.)]" group-by="string(@xml:lang)">
				<xsl:perform-sort select="current-group()">
					<xsl:sort select="."/>
				</xsl:perform-sort>
			</xsl:for-each-group>
		</xsl:variable>
		<xsl:variable name="language" as="xs:string?">
			<xsl:if test="misc:all-equal-but-not-empty($dcterms:subjects/string(@xml:lang))">
				<xsl:sequence select="($dcterms:subjects/@xml:lang)[1]"/>
			</xsl:if>
		</xsl:variable>
		<xsl:if test="$dcterms:subjects[normalize-space(.)]">
			<xsl:sequence select="misc:create_dcterms-subject(string-join(distinct-values($keywords), '; '), $language)"/>
		</xsl:if>
	</xsl:function>
	<xsl:function name="misc:eterms_sequence-number" as="element(eterms:sequence-number)?">
		<xsl:param name="volume" as="xs:string?"/>
		<xsl:param name="issue" as="xs:string?"/>
		<xsl:param name="start-page" as="xs:string?"/>
		<xsl:param name="write-supplement" as="xs:boolean"/>
		<xsl:if test="$volume and $start-page">
			<xsl:sequence select="misc:create_eterms-sequence-number( concat( $volume, if ($write-supplement and normalize-space($issue)) then concat('(', $issue, ')') else (), ':', $start-page) )"/>
		</xsl:if>
	</xsl:function>
	<xsl:function name="misc:author-decoder" as="element()" use-when="function-available('AuthorDecoder:parseAsNode')">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:call-template name="misc:message">
			<xsl:with-param name="level">DEBUG</xsl:with-param>
			<xsl:with-param name="message">[mapping-commomns.xsl#misc:author-decoder] executing AuthorDecoder:parseAsNode("
				<xsl:value-of select="$input"/>")
			</xsl:with-param>
			<xsl:with-param name="show-context" select="false()"/>
		</xsl:call-template>
		<xsl:variable name="result" as="element()" select="AuthorDecoder:parseAsNode($input)/*"/>
		<xsl:call-template name="misc:message">
			<xsl:with-param name="level">DEBUG</xsl:with-param>
			<xsl:with-param name="message">[mapping-commomns.xsl#misc:author-decoder] ... result</xsl:with-param>
			<xsl:with-param name="show-context" select="false()"/>
		</xsl:call-template>
		<xsl:call-template name="misc:message">
			<xsl:with-param name="level">DEBUG</xsl:with-param>
			<xsl:with-param name="message" select="$result"/>
			<xsl:with-param name="show-context" select="false()"/>
		</xsl:call-template>
		<xsl:sequence select="$result"/>
	</xsl:function>
	<xsl:function name="misc:author-decoder" as="element(authors)" use-when="not(function-available('AuthorDecoder:parseAsNode'))">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:variable name="prepared_input" as="xs:string" select="normalize-space(replace($input, '[\[\(][^\[\(]*[\]\)]', ''))"/>
		<xsl:call-template name="misc:message">
			<xsl:with-param name="level">DEBUG</xsl:with-param>
			<xsl:with-param name="message">[mapping-commomns.xsl#misc:author-decoder] executing misc:author-decoder("
				<xsl:value-of select="$input"/>")
			</xsl:with-param>
			<xsl:with-param name="show-context" select="false()"/>
		</xsl:call-template>
		<xsl:variable name="candidates" as="xs:string+">
			<xsl:choose>
				<xsl:when test="contains($prepared_input, '; ')">
					<xsl:sequence select="tokenize($prepared_input, ' und| and |; ')"/>
				</xsl:when>
				<xsl:when test="matches($prepared_input, ' und | and ')">
					<xsl:sequence select="tokenize($prepared_input, ' und | and |, ')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:sequence select="$prepared_input"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="result" as="element(authors)">
			<authors>
				<xsl:for-each select="$candidates[normalize-space(.)]">
					<xsl:choose>
						<xsl:when test="matches(., '^(\S+ )*\S+,.+$')">
							<xsl:sequence select="hidden:create-author( normalize-space(substring-before(., ',')), normalize-space(substring-after(., ',')) )"/>
						</xsl:when>
						<xsl:when test="matches(., '^(\S+ )+\p{Lu}$')">
							<xsl:variable name="name-parts" as="xs:string+" select="tokenize(., ' ')"/>
							<xsl:sequence select="hidden:create-author( string-join($name-parts[position() lt last()], ' '), $name-parts[last()] )"/>
						</xsl:when>
						<xsl:when test="matches(., '^(\S+ )+(\S)+$')">
							<xsl:variable name="name-parts" as="xs:string+" select="tokenize(., ' ')"/>
							<xsl:sequence select="hidden:create-author( $name-parts[last()], string-join($name-parts[position() lt last()], ' ') )"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:sequence select="hidden:create-author(., ())"/>
							<xsl:call-template name="misc:message">
								<xsl:with-param name="level">DEBUG</xsl:with-param>
								<xsl:with-param name="message">[mapping-commomns.xsl#misc:author-decoder] can't parse "
									<xsl:value-of select="."/>"
								</xsl:with-param>
								<xsl:with-param name="show-context" select="false()"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</authors>
		</xsl:variable>
		<xsl:call-template name="misc:message">
			<xsl:with-param name="level">DEBUG</xsl:with-param>
			<xsl:with-param name="message">[mapping-commomns.xsl#misc:author-decoder] ... result</xsl:with-param>
			<xsl:with-param name="show-context" select="false()"/>
		</xsl:call-template>
		<xsl:call-template name="misc:message">
			<xsl:with-param name="level">DEBUG</xsl:with-param>
			<xsl:with-param name="message" select="$result"/>
			<xsl:with-param name="show-context" select="false()"/>
		</xsl:call-template>
		<xsl:sequence select="$result"/>
	</xsl:function>
	<xsl:function name="hidden:create-author" as="element(author)">
		<xsl:param name="familyname" as="xs:string?"/>
		<xsl:param name="givenname" as="xs:string?"/>
		<xsl:param name="prefix" as="xs:string?"/>
		<xsl:param name="title" as="xs:string?"/>
		<author>
			<familyname>
				<xsl:sequence select="$familyname"/>
			</familyname>
			<xsl:if test="normalize-space($givenname)">
				<givenname>
					<xsl:sequence select="$givenname"/>
				</givenname>
			</xsl:if>
			<xsl:if test="normalize-space($prefix)">
				<prefix>
					<xsl:sequence select="$prefix"/>
				</prefix>
			</xsl:if>
			<xsl:if test="normalize-space($title)">
				<title>
					<xsl:sequence select="$title"/>
				</title>
			</xsl:if>
		</author>
	</xsl:function>
	<xsl:function name="hidden:create-author" as="element(author)">
		<xsl:param name="familyname" as="xs:string?"/>
		<xsl:param name="givenname" as="xs:string?"/>
		<xsl:sequence select="hidden:create-author($familyname, $givenname, (), ())"/>
	</xsl:function>
	<xsl:function name="misc:authors-to-eterms_creator" as="element(eterms:creator)*">
		<xsl:param name="authors" as="element(authors)"/>
		<xsl:param name="att_role" as="xs:string"/>
		<xsl:for-each select="$authors/author">
			<xsl:call-template name="misc:make_eterms-creator_from_person">
				<xsl:with-param name="att_role" select="$att_role"/>
				<xsl:with-param name="person:person" as="element()">
					<xsl:variable name="familyname" as="xs:string" select="concat(if (normalize-space(./prefix)) then concat(./prefix, ' ') else (), ./familyname)"/>
					<xsl:call-template name="hidden:make_person-person">
						<xsl:with-param name="eterms:complete-name" select="misc:create_eterms-complete-name(./givenname, (), (), $familyname, ())"/>
						<xsl:with-param name="eterms:family-name" select="misc:create_eterms-family-name($familyname, true())"/>
						<xsl:with-param name="eterms:given-name" select="misc:create_eterms-given-name(./givenname)"/>
						<xsl:with-param name="eterms:person-title" as="element()?">
							<xsl:if test="normalize-space(./title)">
								<xsl:sequence select="misc:create_eterms-person-title(normalize-space(./title))"/>
							</xsl:if>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:function>
	<xsl:function name="hidden:query-cone" as="element(cone)">
		<xsl:param name="not-url-encoded-input-string" as="xs:string?"/>
		<xsl:variable name="use-REST" as="xs:boolean" select="$misc:force-using-REST-for-CoNE-queries or not(function-available('Util:queryCone'))"/>
		<xsl:variable name="cone-creators" as="element()*">
			<xsl:choose>
				<xsl:when test="$use-REST">
					<xsl:variable name="query-for-list-url" as="xs:string" select="concat($hidden:cone-query-prefix-list, encode-for-uri($not-url-encoded-input-string), $hidden:cone-query-suffix-list)"/>
					<xsl:variable name="author-list" as="element()?" select="if (doc-available($query-for-list-url)) then doc($query-for-list-url)/* else ()"/>
					<xsl:for-each select="$author-list/rdf:Description/@rdf:about[normalize-space(tokenize(., '/')[last()])]">
						<xsl:variable name="query-for-details-url" as="xs:string" select="concat($hidden:cone-query-prefix-detail, tokenize(., '/')[last()], $hidden:cone-query-suffix-detail)"/>
						<xsl:call-template name="misc:message">
							<xsl:with-param name="level">DEBUG</xsl:with-param>
							<xsl:with-param name="message" select="concat('[mapping-commons.xsl#hidden:query-cone] Querying CoNE for details with URL »', $query-for-details-url, '«')"/>
							<xsl:with-param name="show-context" select="false()"/>
						</xsl:call-template>
						<xsl:sequence select="if (doc-available($query-for-details-url)) then doc($query-for-details-url)/* else ()"/>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:sequence select="Util:queryCone('persons', $not-url-encoded-input-string)/*/*" use-when="function-available('Util:queryCone')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<cone>
			<xsl:if test="normalize-space($not-url-encoded-input-string)">
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">DEBUG</xsl:with-param>
					<xsl:with-param name="message">
						<xsl:choose>
							<xsl:when test="$use-REST">
								<xsl:text>[mapping-commons.xsl#hidden:query-cone] using REST</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>[mapping-commons.xsl#hidden:query-cone] using Util:queryCone()</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
				</xsl:call-template>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">DEBUG</xsl:with-param>
					<xsl:with-param name="message">[mapping-commons.xsl#hidden:query-cone] result ...</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
				</xsl:call-template>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">DEBUG</xsl:with-param>
					<xsl:with-param name="message" select="$cone-creators"/>
					<xsl:with-param name="show-context" select="false()"/>
				</xsl:call-template>
				<xsl:sequence select="$cone-creators"/>
			</xsl:if>
		</cone>
	</xsl:function>
	<xsl:function name="hidden:matching-cone-records" as="element(cone)?">
		<xsl:param name="person:person" as="element(person:person)"/>
		<xsl:variable name="CoNE-ID" as="xs:string?" select="replace( $person:person/dc:identifier[misc:parse-identifier-types(@xsi:type) eq 'eterms:CONE']/normalize-space(.)[.][1], (: der Path-Teil der URI verhindert das Finden :) '^http://.*persons/resource/', '' )"/>
		<xsl:variable name="result-from-CoNE-ID" as="element()" select="hidden:query-cone($CoNE-ID)"/>
		<xsl:variable name="result-from-ID" as="element()" select="hidden:query-cone(string-join($person:person/dc:identifier, ' '))"/>
		<xsl:variable name="result-from-familyname-givenname-organization" as="element()" select="hidden:query-cone( string-join( ( $person:person/eterms:given-name, $person:person/eterms:family-name, for $i in $person:person/organization:organization return misc:organization_organization-to-string($i) ), ' ' ) )"/>
		<xsl:variable name="result-from-givenname-familyname" as="element()" select="hidden:query-cone( string-join( ( $person:person/eterms:family-name, $person:person/eterms:given-name ), ', ' ) )"/>
		<xsl:choose>
			<xsl:when test="hidden:all-cone-datasets-refer-to-same-person($result-from-CoNE-ID)">
				<xsl:sequence select="$result-from-CoNE-ID"/>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">DEBUG</xsl:with-param>
					<xsl:with-param name="message">[mapping-commons.xsl#hidden:matching-cone-records] found valid $result-from-CoNE-ID</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="hidden:all-cone-datasets-refer-to-same-person($result-from-ID)">
				<xsl:sequence select="$result-from-ID"/>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">DEBUG</xsl:with-param>
					<xsl:with-param name="message">[mapping-commons.xsl#hidden:matching-cone-records] found valid $result-from-ID</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="hidden:all-cone-datasets-refer-to-same-person($result-from-familyname-givenname-organization)">
				<xsl:sequence select="$result-from-familyname-givenname-organization"/>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">DEBUG</xsl:with-param>
					<xsl:with-param name="message">[mapping-commons.xsl#hidden:matching-cone-records] found valid $result-from-familyname-givenname-organization</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="hidden:all-cone-datasets-refer-to-same-person($result-from-givenname-familyname)">
				<xsl:sequence select="$result-from-givenname-familyname"/>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">DEBUG</xsl:with-param>
					<xsl:with-param name="message">[mapping-commons.xsl#hidden:matching-cone-records] found valid $result-from-givenname-familyname</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">DEBUG</xsl:with-param>
					<xsl:with-param name="message">[mapping-commons.xsl#hidden:matching-cone-records] did not found a valid CoNE record</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="hidden:enrich-with-CoNE" as="element(person:person)">
		<xsl:param name="person:person" as="element(person:person)"/>
		<xsl:sequence select="hidden:enrich-with-CoNE($person:person, hidden:matching-cone-records($person:person))"/>
	</xsl:function>
	<xsl:function name="hidden:enrich-with-CoNE" as="element(person:person)" xml:id="fkt_hidden_enrich-with-CoNE">
		<xsl:param name="person:person" as="element(person:person)"/>
		<xsl:param name="cone-result" as="element(cone)?"/>
		<xsl:choose>
			<xsl:when test="$cone-result">
				<xsl:call-template name="hidden:make_person-person">
					<xsl:with-param name="eterms:complete-name" as="element()">
						<xsl:choose>
							<xsl:when test="$person:person/eterms:complete-name[normalize-space(.)]">
								<xsl:sequence select="$person:person/eterms:complete-name"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:sequence select="misc:create_eterms-complete-name(normalize-space($cone-result/rdf:RDF/rdf:Description/dc:title[normalize-space(.)][1]) )"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="eterms:family-name" as="element()">
						<xsl:choose>
							<xsl:when test="$person:person/eterms:family-name[normalize-space(.)]">
								<xsl:sequence select="$person:person/eterms:family-name"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:sequence select="misc:create_eterms-family-name(normalize-space(($cone-result/rdf:RDF/rdf:Description/foaf:family_name[normalize-space(.)])[1]) )"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="eterms:given-name" as="element()?">
						<xsl:choose>
							<xsl:when test="$person:person/eterms:given-name[normalize-space(.)]">
								<xsl:sequence select="$person:person/eterms:given-name"/>
							</xsl:when>
							<xsl:when test="normalize-space(($cone-result/rdf:RDF/rdf:Description/foaf:givenname[normalize-space(.)])[1])">
								<xsl:sequence select="misc:create_eterms-given-name(normalize-space(($cone-result/rdf:RDF/rdf:Description/foaf:givenname[normalize-space(.)])[1]) )"/>
							</xsl:when>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="eterms:alternative-name" as="element()*">
						<xsl:variable name="collected-alternative-names" as="element(x)">
							<x>
								<xsl:sequence select="$person:person/eterms:alternative-name[normalize-space(.)]"/>
								<xsl:for-each select="distinct-values($cone-result/rdf:RDF/rdf:Description/dcterms:alternative/normalize-space(.)[.])">
									<xsl:sequence select="misc:create_eterms-alternative-name(.)"/>
								</xsl:for-each>
							</x>
						</xsl:variable>
						<xsl:sequence select="$collected-alternative-names/eterms:alternative-name[for $i in . return not($i/preceding-sibling::eterms:alternative-name[normalize-space(.) eq normalize-space($i)])]"/>
					</xsl:with-param>
					<xsl:with-param name="eterms:person-title" as="element()*">
						<xsl:variable name="collected-person-title" as="element(x)">
							<x>
								<xsl:sequence select="$person:person/eterms:person-title[normalize-space(.)]"/>
								<xsl:for-each select="distinct-values($cone-result/rdf:RDF/rdf:Description/eterms:degree/normalize-space(.)[.])">
									<xsl:sequence select="misc:create_eterms-person-title(.)"/>
								</xsl:for-each>
							</x>
						</xsl:variable>
						<xsl:sequence select="$collected-person-title/eterms:person-title[for $i in . return not($i/preceding-sibling::eterms:person-title[normalize-space(.) eq normalize-space($i)])]"/>
					</xsl:with-param>
					<xsl:with-param name="eterms:pseudonym" as="element()*">
						<xsl:sequence select="$person:person/eterms:pseudonym"/>
					</xsl:with-param>
					<xsl:with-param name="organization:organization" as="element(organization:organization)*">
						<xsl:variable name="cone-organizations" as="element(organization:organization)*" select="$cone-result/rdf:RDF/rdf:Description/eterms:position/hidden:cone-organization-to-organization-organization(.) [not(hidden:is-generic-organization-organization(.) )]"/>
						<xsl:variable name="collected-organizations" as="element(x)">
							<x>
								<xsl:if test="not($misc:use-CoNE-organizations-only)">
									<xsl:for-each select="$person:person/organization:organization">
										<xsl:choose>
											<xsl:when test="hidden:is-generic-organization-organization(.)"/>
											<xsl:when test="./dc:title[normalize-space(.)] and not(./dc:identifier[normalize-space(.)])">
												<xsl:variable name="matching-cone-organization-with-dc_identifier" as="element(organization:organization)?" select="$cone-organizations[normalize-space(dc:title[normalize-space(.)]) eq current()/dc:title/normalize-space()][normalize-space(dc:identifier)][1]"/>
												<xsl:choose>
													<xsl:when test="$matching-cone-organization-with-dc_identifier">
														<xsl:call-template name="misc:make_organization-organization">
															<xsl:with-param name="dc:title" select="./dc:title"/>
															<xsl:with-param name="dc:identifier" select="$matching-cone-organization-with-dc_identifier/dc:identifier"/>
															<xsl:with-param name="eterms:address" select="./eterms:address"/>
														</xsl:call-template>
													</xsl:when>
													<xsl:otherwise>
														<xsl:sequence select="."/>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:when>
											<xsl:otherwise>
												<xsl:sequence select="."/>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:for-each>
								</xsl:if>
								<xsl:sequence select="$cone-organizations"/>
							</x>
						</xsl:variable>
						<xsl:sequence select="$collected-organizations/organization:organization[for $i in . return not($i/preceding-sibling::organization:organization[misc:organization_organization_equals(., $i)])]"/>
					</xsl:with-param>
					<xsl:with-param name="dc:identifier" as="element()*">
						<xsl:variable name="collected-ids" as="element(x)">
							<x>
								<xsl:sequence select="$person:person/dc:identifier"/>
								<xsl:for-each select="$cone-result/rdf:RDF/rdf:Description/@rdf:about/normalize-space(.)[.]">
									<xsl:sequence select="misc:create_dc-identifier_from_cone-uri(.)"/>
								</xsl:for-each>
								<xsl:sequence select="$cone-result/rdf:RDF/rdf:Description/dc:identifier"/>
							</x>
						</xsl:variable>
						<xsl:sequence select="$collected-ids/dc:identifier[not(for $i in . return $i/preceding-sibling::dc:identifier[normalize-space(.) eq normalize-space($i)])]"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$misc:use-CoNE-organizations-only">
				<xsl:call-template name="hidden:make_person-person">
					<xsl:with-param name="eterms:complete-name" as="element(eterms:complete-name)" select="$person:person/eterms:complete-name"/>
					<xsl:with-param name="eterms:family-name" as="element(eterms:family-name)" select="$person:person/eterms:family-name"/>
					<xsl:with-param name="eterms:given-name" as="element(eterms:given-name)?" select="$person:person/eterms:given-name"/>
					<xsl:with-param name="eterms:alternative-name" as="element(eterms:alternative-name)*" select="$person:person/eterms:alternative-name"/>
					<xsl:with-param name="eterms:person-title" as="element(eterms:person-title)*" select="$person:person/eterms:person-title"/>
					<xsl:with-param name="eterms:pseudonym" as="element(eterms:pseudonym)*" select="$person:person/eterms:pseudonym"/>
					<xsl:with-param name="organization:organization" as="element(organization:organization)*" select="()"/>
					<xsl:with-param name="dc:identifier" as="element(dc:identifier)*" select="$person:person/dc:identifier"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="$person:person"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="hidden:all-cone-datasets-refer-to-same-person" as="xs:boolean">
		<xsl:param name="cone-element" as="element(cone)"/>
		<xsl:call-template name="misc:message">
			<xsl:with-param name="level">TRACE</xsl:with-param>
			<xsl:with-param name="message">[mapping-commons.xsl#hidden:all-cone-datasets-refer-to-same-person] Input:</xsl:with-param>
			<xsl:with-param name="show-context" select="false()"/>
		</xsl:call-template>
		<xsl:call-template name="misc:message">
			<xsl:with-param name="level">TRACE</xsl:with-param>
			<xsl:with-param name="message" select="$cone-element"/>
			<xsl:with-param name="show-context" select="false()"/>
		</xsl:call-template>
		<xsl:call-template name="misc:message">
			<xsl:with-param name="level">TRACE</xsl:with-param>
			<xsl:with-param name="message">[mapping-commons.xsl#hidden:all-cone-datasets-refer-to-same-person] count($cone-element/rdf:RDF):
				<xsl:sequence select="count($cone-element/rdf:RDF)"/>
			</xsl:with-param>
			<xsl:with-param name="show-context" select="false()"/>
		</xsl:call-template>
		<xsl:choose>
			<xsl:when test="count($cone-element/rdf:RDF) eq 1">
				<xsl:sequence select="true()"/>
			</xsl:when>
			<xsl:when test="misc:all-equal-but-not-empty($cone-element/rdf:RDF/string(rdf:Description/@rdf:about) )">
				<xsl:sequence select="true()"/>
			</xsl:when>
			<xsl:when test=" misc:all-equal-but-not-empty($cone-element/rdf:RDF/string(rdf:Description/foaf:givenname)) and misc:all-equal-but-not-empty($cone-element/rdf:RDF/string(rdf:Description/foaf:family_name)) and ( misc:all-equal-but-not-empty($cone-element/rdf:RDF/string(rdf:Description/eterms:position[1]/rdf:Description[1]/dc:identifier[1])) or misc:all-equal-but-not-empty($cone-element/rdf:RDF/string(rdf:Description/eterms:position[1]/rdf:Description[1]/eprints:affiliatedInstitution[1])) ) ">
				<xsl:sequence select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:all-equal-but-not-empty" as="xs:boolean">
		<xsl:param name="items" as="item()*"/>
		<xsl:choose>
			<xsl:when test=" (every $i in $items satisfies normalize-space($i) ) and (count(distinct-values($items)) eq 1) ">
				<xsl:sequence select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="hidden:cone-organization-to-organization-organization" as="element(organization:organization)" xml:id="hidden:cone-organization-to-organization-organization">
		<xsl:param name="eterms:position" as="element(eterms:position)"/>
		<xsl:call-template name="misc:make_organization-organization">
			<xsl:with-param name="dc:title" as="element(dc:title)">
				<xsl:variable name="title" as="xs:string">
					<xsl:choose>
						<xsl:when test="$eterms:position/rdf:Description/eprints:affiliatedInstitution[normalize-space(.)]">
							<xsl:sequence select="$eterms:position/rdf:Description/eprints:affiliatedInstitution/normalize-space(.)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:sequence select="$misc:anonymous-organization-name"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:sequence select="misc:create_dc-title($title)"/>
			</xsl:with-param>
			<xsl:with-param name="eterms:address" as="element(eterms:address)?"/>
			<xsl:with-param name="dc:identifier" as="element(dc:identifier)?">
				<xsl:sequence select="$eterms:position/rdf:Description/dc:identifier"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:function>
	<xsl:function name="hidden:is-generic-organization-organization" as="xs:boolean">
		<xsl:param name="organization:organization" as="element(organization:organization)"/>
		<xsl:choose>
			<xsl:when test="$organization:organization/dc:title/normalize-space() eq normalize-space($misc:default-organization-name)">
				<xsl:sequence select="true()"/>
			</xsl:when>
			<xsl:when test="$organization:organization/dc:identifier/normalize-space() eq normalize-space($inge.pubman.external.organization.id)">
				<xsl:sequence select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc:organization_organization_equals" as="xs:boolean">
		<xsl:param name="organization:organization_1" as="element(organization:organization)"/>
		<xsl:param name="organization:organization_2" as="element(organization:organization)"/>
		<xsl:choose>
			<xsl:when test="$organization:organization_1/dc:title/normalize-space() eq $organization:organization_2/dc:title/normalize-space()">
				<xsl:sequence select="true()"/>
			</xsl:when>
			<xsl:when test="$organization:organization_1/dc:identifier/normalize-space() eq $organization:organization_2/dc:identifier/normalize-space()">
				<xsl:sequence select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:template name="misc:message">
		<xsl:param name="message" as="item()*" required="no"/>
		<xsl:param name="level" as="xs:string" select="'info'" required="no"/>
		<xsl:param name="show-context" as="xs:boolean" select="true()" required="no"/>
		<xsl:variable name="logging-level" as="xs:integer" select="tools:logging-level(normalize-space($level))"/>
		<xsl:if test="$logging-level ge $hidden:logging-level">
			<xsl:message>
				<xsl:if test="normalize-space($level) and (some $i in $message satisfies normalize-space($i))">
					<xsl:sequence select="concat('[', $level, ']')"/>
				</xsl:if>
				<xsl:if test="$show-context">
					<xsl:sequence select="tools:render-context-and-parent-as-string(.)"/>
				</xsl:if>
				<xsl:if test="some $i in $message satisfies ($i instance of element() )">
					<xsl:text>&#xa;</xsl:text>
				</xsl:if>
				<xsl:sequence select="$message"/>
			</xsl:message>
		</xsl:if>
		<xsl:if test="$logging-level eq 6">
			<xsl:message terminate="yes"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
