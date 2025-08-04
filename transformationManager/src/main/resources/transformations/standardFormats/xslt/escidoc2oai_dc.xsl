<xsl:stylesheet
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:escidocComponents="http://www.escidoc.de/schemas/components/0.9"
	xmlns:escidocItem="http://www.escidoc.de/schemas/item/0.10"
    xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.5"
	xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
	xmlns:event="http://purl.org/escidoc/metadata/profiles/0.1/event"
	xmlns:file="http://purl.org/escidoc/metadata/profiles/0.1/file"
	xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
	xmlns:organization="http://purl.org/escidoc/metadata/profiles/0.1/organization"
	xmlns:person="http://purl.org/escidoc/metadata/profiles/0.1/person"
	xmlns:project="http://purl.org/escidoc/metadata/profiles/0.1/project"
	xmlns:prop="http://escidoc.de/core/01/properties/"
	xmlns:publication="http://purl.org/escidoc/metadata/profiles/0.1/publication"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">

	<xsl:output method="xml" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>

	<xsl:preserve-space elements="*"/>

    <xsl:param name="inge.pid.handle.url" select="'has.to.be.defined.in.pubman.properties'"/>

	<xsl:template match="node()|@*">
		    <xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="escidocItem:item">
		<oai_dc:dc xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">

			<!--  dc:title  -->
			<xsl:variable name="title" select="normalize-space(./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dc:title)"/>
			<xsl:if test="$title != ''">
				<dc:title>
					<xsl:value-of select="$title"/>
				</dc:title>
			</xsl:if>

		    <!--  dc:identifier Object Handle -->
		    <xsl:variable name="pidObject" select="./escidocItem:properties/prop:pid"/>
		    <xsl:if test="$pidObject != ''">
			    <dc:identifier>
			    	<xsl:value-of select="concat($inge.pid.handle.url, replace($pidObject, 'hdl:', ''))"/>
    			</dc:identifier>
	    	</xsl:if>

			<!--  dc:identifier File Handles -->
			<xsl:for-each select="./escidocComponents:components/escidocComponents:component[escidocComponents:content/@storage='internal-managed']/escidocComponents:properties">
			    <xsl:variable name="pidFile" select="prop:pid"/>
			    <xsl:if test="$pidFile != ''">
				    <dc:identifier>
				    	<xsl:value-of select="concat($inge.pid.handle.url, replace($pidFile, 'hdl:', ''))"/>
	    			</dc:identifier>
		    	</xsl:if>
			</xsl:for-each>

			<!--  dc:format File Handles -->
			<xsl:for-each select="./escidocComponents:components/escidocComponents:component[escidocComponents:content/@storage='internal-managed']/escidocComponents:properties">
			    <xsl:variable name="format" select="prop:mime-type"/>
			    <xsl:if test="$format != ''">
				    <dc:format>
					    <xsl:value-of select="$format"/>
	    			</dc:format>
		    	</xsl:if>
			</xsl:for-each>

			<!--  dc:rights File Handles -->
				<xsl:choose>
					<xsl:when test="./escidocComponents:components/escidocComponents:component[escidocComponents:content/@storage='internal-managed']/escidocComponents:properties[prop:visibility='public']/prop:content-category='any-fulltext'
						or ./escidocComponents:components/escidocComponents:component[escidocComponents:content/@storage='internal-managed']/escidocComponents:properties[prop:visibility='public']/prop:content-category='publisher-version'
						or ./escidocComponents:components/escidocComponents:component[escidocComponents:content/@storage='internal-managed']/escidocComponents:properties[prop:visibility='public']/prop:content-category='post-print'
						or ./escidocComponents:components/escidocComponents:component[escidocComponents:content/@storage='internal-managed']/escidocComponents:properties[prop:visibility='public']/prop:content-category='pre-print'">
						<dc:rights>
							<xsl:value-of select="'info:eu-repo/semantics/openAccess'"/>
						</dc:rights>
					</xsl:when>
					<xsl:when test="./escidocComponents:components/escidocComponents:component[escidocComponents:content/@storage='internal-managed']/escidocComponents:properties[prop:visibility='audience']/prop:content-category='any-fulltext'
						or ./escidocComponents:components/escidocComponents:component[escidocComponents:content/@storage='internal-managed']/escidocComponents:properties[prop:visibility='public']/prop:content-category='publisher-version'
						or ./escidocComponents:components/escidocComponents:component[escidocComponents:content/@storage='internal-managed']/escidocComponents:properties[prop:visibility='public']/prop:content-category='post-print'
						or ./escidocComponents:components/escidocComponents:component[escidocComponents:content/@storage='internal-managed']/escidocComponents:properties[prop:visibility='public']/prop:content-category='pre-print'">
						<dc:rights>
							<xsl:value-of select="'info:eu-repo/semantics/restrictedAccess'"/>
						</dc:rights>
					</xsl:when>
					<xsl:when test="./escidocComponents:components/escidocComponents:component[escidocComponents:content/@storage='internal-managed']/escidocComponents:properties[prop:visibility='private']/prop:content-category='any-fulltext'
						or ./escidocComponents:components/escidocComponents:component[escidocComponents:content/@storage='internal-managed']/escidocComponents:properties[prop:visibility='public']/prop:content-category='publisher-version'
						or ./escidocComponents:components/escidocComponents:component[escidocComponents:content/@storage='internal-managed']/escidocComponents:properties[prop:visibility='public']/prop:content-category='post-print'
						or ./escidocComponents:components/escidocComponents:component[escidocComponents:content/@storage='internal-managed']/escidocComponents:properties[prop:visibility='public']/prop:content-category='pre-print'">
						<dc:rights>
						<xsl:value-of select="'info:eu-repo/semantics/closedAccess'"/>
						</dc:rights>
					</xsl:when>
				</xsl:choose>

			<!--  dc:rights File Handles license -->
				<xsl:choose>
					<xsl:when test="./escidocComponents:components/escidocComponents:component[escidocComponents:content/@storage='internal-managed']/escidocMetadataRecords:md-records[escidocMetadataRecords:md-record/file:file/dcterms:license!='']">
						<xsl:variable name="license" select="./escidocComponents:components/escidocComponents:component[escidocComponents:content/@storage='internal-managed']/escidocMetadataRecords:md-records[escidocMetadataRecords:md-record/file:file/dcterms:license!='']/escidocMetadataRecords:md-record/file:file/dcterms:license"/>
						<xsl:if test="$license != ''">
							<dc:rights>
								<xsl:value-of select="$license[position()=1]"/>
							</dc:rights>
						</xsl:if>
					</xsl:when>
				</xsl:choose>

			<!--  dc:type  -->
			<dc:type>
				<xsl:variable name="publication-type">
					<xsl:value-of select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/@type"/>
				</xsl:variable>

				<xsl:choose>
					<xsl:when test="$publication-type = 'http://purl.org/eprint/type/Thesis'">
						<xsl:variable name="degree">
							<xsl:value-of select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/eterms:degree"/>
						</xsl:variable>
						<xsl:choose>
							<xsl:when test="$degree = 'http://purl.org/escidoc/metadata/ves/academic-degrees/master'">
								<xsl:value-of select="'info:eu-repo/semantics/masterThesis'"/>
							</xsl:when>

							<xsl:when test="$degree = 'http://purl.org/escidoc/metadata/ves/academic-degrees/diploma'">
								<xsl:value-of select="'info:eu-repo/semantics/other'"/>
							</xsl:when>

							<xsl:when test="$degree = 'http://purl.org/escidoc/metadata/ves/academic-degrees/magister'">
								<xsl:value-of select="'info:eu-repo/semantics/other'"/>
							</xsl:when>

							<xsl:when test="$degree = 'http://purl.org/escidoc/metadata/ves/academic-degrees/phd'">
								<xsl:value-of select="'info:eu-repo/semantics/doctoralThesis'"/>
							</xsl:when>

							<xsl:when test="$degree = 'http://purl.org/escidoc/metadata/ves/academic-degrees/staatsexamen'">
								<xsl:value-of select="'info:eu-repo/semantics/other'"/>
							</xsl:when>

							<xsl:when test="$degree = 'http://purl.org/escidoc/metadata/ves/academic-degrees/habilitation'">
								<xsl:value-of select="'info:eu-repo/semantics/other'"/>
							</xsl:when>

							<xsl:when test="$degree = 'http://purl.org/escidoc/metadata/ves/academic-degrees/bachelor'">
								<xsl:value-of select="'info:eu-repo/semantics/bachelorThesis'"/>
							</xsl:when>

							<xsl:otherwise>
								<xsl:value-of select="'info:eu-repo/semantics/other'"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/eprint/type/Book'">
						<xsl:value-of select="'info:eu-repo/semantics/book'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/eprint/type/BookItem'">
						<xsl:value-of select="'info:eu-repo/semantics/bookPart'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/eprint/type/ConferencePaper'">
						<xsl:value-of select="'info:eu-repo/semantics/conferenceObject'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/eprint/type/ConferencePoster'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/eprint/type/Patent'">
						<xsl:value-of select="'info:eu-repo/semantics/patent'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/eprint/type/Report'">
						<xsl:value-of select="'info:eu-repo/semantics/report'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/article'">
						<xsl:value-of select="'info:eu-repo/semantics/article'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/book-review'">
						<xsl:value-of select="'info:eu-repo/semantics/review'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/case-note'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/case-study'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/collected-edition'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/commentary'">
						<xsl:value-of select="'info:eu-repo/semantics/annotation'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/conference-report'">
						<xsl:value-of select="'info:eu-repo/semantics/conferenceObject'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-collected-edition'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-commentary'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-encyclopedia'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-festschrift'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-handbook'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/courseware-lecture'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/editorial'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/encyclopedia'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/festschrift'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/film'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/handbook'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/issue'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/journal'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/manual'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/manuscript'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/meeting-abstract'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/monograph'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/multi-volume'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/newspaper'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/newspaper-article'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/opinion'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/other'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/paper'">
						<xsl:value-of select="'info:eu-repo/semantics/workingPaper'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/proceedings'">
						<xsl:value-of select="'info:eu-repo/semantics/conferenceObject'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/series'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event'">
						<xsl:value-of select="'info:eu-repo/semantics/lecture'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/eprint/type/blog-post'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/data-publication'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/interview'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/eprint/type/preprint'">
						<xsl:value-of select="'info:eu-repo/semantics/preprint'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/eprint/type/pre-registration-paper'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/eprint/type/registered-report'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/eprint/type/review-article'">
						<xsl:value-of select="'info:eu-repo/semantics/article'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/software'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:when test="$publication-type = 'http://purl.org/escidoc/metadata/ves/publication-types/magazine-article'">
						<xsl:value-of select="'info:eu-repo/semantics/other'"/>
					</xsl:when>

					<xsl:otherwise>
						<xsl:value-of select="$publication-type"/>
					</xsl:otherwise>
				</xsl:choose>
			</dc:type>

			<!--  dc:creator  -->
			<xsl:for-each select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/eterms:creator">
				<xsl:choose>
					<xsl:when test="@role = 'http://www.loc.gov/loc.terms/relators/AUT' and not(./organization:organization)">
						<dc:creator>
							<xsl:call-template name="person">
								<xsl:with-param name="person" select="person:person"/>
							</xsl:call-template>
						</dc:creator>
					</xsl:when>

					<xsl:when test="@role = 'http://www.loc.gov/loc.terms/relators/EDT' or ./organization:organization">
						<dc:contributor>
							<xsl:choose>
								<xsl:when test="./person:person">
									<xsl:call-template name="person">
										<xsl:with-param name="person" select="./person:person"/>
									</xsl:call-template>
								</xsl:when>

								<xsl:when test="./organization:organization">
									<xsl:call-template name="organization">
										<xsl:with-param name="organization" select="./organization:organization"/>
									</xsl:call-template>
								</xsl:when>
							</xsl:choose>
						</dc:contributor>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>

			<!--  dc:language  -->
			<xsl:for-each select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dc:language">
				<xsl:if test=". != ''">
					<dc:language>
						<xsl:value-of select="normalize-space(.)"/>
					</dc:language>
				</xsl:if>
			</xsl:for-each>

			<!--  dcterms:alternative -> dc:title  -->
			<xsl:for-each select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dcterms:alternative">
				<dc:title>
					<xsl:value-of select="."/>
				</dc:title>
			</xsl:for-each>

			<!--  dc:publisher  -->
			<xsl:variable name="publisher" select="normalize-space(./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/eterms:publishing-info/dc:publisher)"/>
			<xsl:if test="$publisher != ''">
				<dc:publisher>
					<xsl:value-of select="$publisher"/>
				</dc:publisher>
			</xsl:if>

			<!--  dc:date  -->
			<xsl:variable name="date">
				<xsl:choose>
					<xsl:when test="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dcterms:issued != ''">
						<xsl:value-of select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dcterms:issued"/>
					</xsl:when>

					<xsl:when test="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/eterms:published-online != ''">
						<xsl:value-of select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/eterms:published-online"/>
					</xsl:when>

					<xsl:when test="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dcterms:dateAccepted != ''">
						<xsl:value-of select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dcterms:dateAccepted"/>
					</xsl:when>

					<xsl:when test="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dcterms:dateSubmitted != ''">
						<xsl:value-of select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dcterms:dateSubmitted"/>
					</xsl:when>

					<xsl:when test="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dcterms:modified != ''">
						<xsl:value-of select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dcterms:modified"/>
					</xsl:when>

					<xsl:when test="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dcterms:created != ''">
						<xsl:value-of select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dcterms:created"/>
					</xsl:when>

					<xsl:when test="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/event:event/eterms:start-date != ''">
						<xsl:value-of select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/event:event/eterms:start-date"/>
					</xsl:when>
				</xsl:choose>
			</xsl:variable>

			<xsl:if test="$date != ''">
				<dc:date>
					<xsl:value-of select="$date"/>
				</dc:date>
			</xsl:if>

			<!--  dc:relation  -->
			<xsl:for-each select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/project:project-info">
				<xsl:variable name="identifier1" select="./project:funding-info/project:funding-organization/dc:identifier"/>
				<xsl:variable name="identifier2" select="./project:funding-info/project:funding-program/dc:identifier"/>
				<xsl:variable name="identifier3" select="./dc:identifier"/>
				<xsl:if test="$identifier1 != '' and $identifier2 != '' and $identifier3 != ''">
					<dc:relation>
						<xsl:variable name="relationString" select="concat('info:eu-repo/grantAgreement/', $identifier1, '/', $identifier2, '/', $identifier3 )"/>
						<xsl:value-of select="$relationString"/>
					</dc:relation>
				</xsl:if>
			</xsl:for-each>

			<xsl:for-each select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dc:identifier">
				<dc:relation>
					<xsl:choose>
						<xsl:when test="@xsi:type = 'eterms:ISSN'">
							<xsl:value-of select="concat('info:eu-repo/semantics/altIdentifier/pissn/', normalize-space(.))"/>
						</xsl:when>

						<xsl:when test="@xsi:type = 'eterms:ISBN'">
							<xsl:value-of select="concat('info:eu-repo/semantics/altIdentifier/isbn/', normalize-space(.))"/>
						</xsl:when>

						<xsl:when test="@xsi:type = 'eterms:DOI'">
							<xsl:value-of select="concat('info:eu-repo/semantics/altIdentifier/doi/', normalize-space(.))"/>
						</xsl:when>

						<xsl:when test="@xsi:type = 'eterms:ARXIV'">
							<xsl:value-of select="concat('info:eu-repo/semantics/altIdentifier/arxiv/', normalize-space(.))"/>
						</xsl:when>

						<xsl:when test="@xsi:type = 'eterms:PMID'">
							<xsl:value-of select="concat('info:eu-repo/semantics/altIdentifier/pmid/', normalize-space(.))"/>
						</xsl:when>

						<xsl:when test="@xsi:type = 'eterms:URN'">
							<xsl:value-of select="concat('info:eu-repo/semantics/altIdentifier/urn/', normalize-space(.))"/>
						</xsl:when>

						<xsl:when test="@xsi:type = 'eterms:URI'">
							<xsl:value-of select="concat('info:eu-repo/semantics/altIdentifier/urn/', normalize-space(.))"/>
						</xsl:when>
					</xsl:choose>
				</dc:relation>
			</xsl:for-each>

			<!--  dc:description  -->
			<xsl:for-each select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dcterms:abstract">
				<xsl:if test=". != ''">
					<dc:description>
						<xsl:value-of select="normalize-space(.)"/>
					</dc:description>
				</xsl:if>
			</xsl:for-each>

			<xsl:variable name="table-of-contents" select="normalize-space(./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dcterms:tableOfContents)"/>

			<xsl:if test="$table-of-contents!=''">
				<dc:description>
					<xsl:value-of select="$table-of-contents"/>
				</dc:description>
			</xsl:if>

			<!--  dc:subject  -->
			<xsl:for-each select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/dc:subject">
				<xsl:if test=". != ''">
					<dc:subject>
						<xsl:value-of select="normalize-space(.)"/>
					</dc:subject>
				</xsl:if>
			</xsl:for-each>

            <!--  dc:source  -->
            <xsl:for-each select="./escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/publication:publication/source:source/dc:title">
                <xsl:if test=". != ''">
                    <dc:source>
                        <xsl:value-of select="normalize-space(.)"/>
                    </dc:source>
                </xsl:if>
            </xsl:for-each>
		</oai_dc:dc>
	</xsl:template>

	<xsl:template name="person">
		<xsl:param name="person"/>

		<xsl:value-of select="$person/eterms:family-name"/>

		<xsl:if test="$person/eterms:given-name != ''">
			<xsl:text>, </xsl:text>
			<xsl:value-of select="concat(substring($person/eterms:given-name, 1, 1), '.')"/>
		</xsl:if>

		<xsl:if test="$person/eterms:orcid != ''">
			<xsl:text> ; </xsl:text>
			<xsl:value-of select="$person/eterms:orcid"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="organization">
		<xsl:param name="organization"/>
		<xsl:value-of select="$organization/dc:title"/>
	</xsl:template>

</xsl:stylesheet>
