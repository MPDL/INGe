<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"  xmlns:escidocAdminDescriptor="http://www.escidoc.de/schemas/admindescriptor/0.3" xmlns:escidocContext="http://www.escidoc.de/schemas/context/0.3" xmlns:escidocContextList="http://www.escidoc.de/schemas/contextlist/0.3" xmlns:escidocComponents="http://www.escidoc.de/schemas/components/0.3" xmlns:escidocItem="http://www.escidoc.de/schemas/item/0.3" xmlns:escidocItemList="http://www.escidoc.de/schemas/itemlist/0.3" xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.3" xmlns:escidocRelations="http://www.escidoc.de/schemas/relations/0.3" xmlns:escidocResources="http://www.escidoc.de/schemas/resources/0.2" xmlns:escidocSearchResult="http://www.escidoc.de/schemas/searchresult/0.3" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.escidoc.de/schemas/item/0.3 soap/item/0.3/item.xsd" version="2.0"> 

	<xsl:output method="text" encoding="UTF-8"/>

	<xsl:include href="C:/repository/common_services/edoc_migration/src/main/resources/mpipl_ous.xml"/>
	
	<xsl:param name="id-prefix" select="'sb'"/>
	
	<xsl:template match="/">
	
		<xsl:for-each select="/authors/author[cone/@display = 'true']">
			<xsl:variable name="pos" select="position()"/>
			<xsl:variable name="id" select="concat('urn:cone:', $id-prefix, $pos)"/>
			
			insert into triples values ('<xsl:value-of select="$id"/>', 'http://purl.org/dc/elements/1.1/title', '<xsl:value-of select="familyname"/>, <xsl:value-of select="givenname"/>', null, 'persons');
			insert into triples values ('<xsl:value-of select="$id"/>', 'http://xmlns.com/foaf/0.1/familyname', '<xsl:value-of select="familyname"/>', null, 'persons');
			insert into triples values ('<xsl:value-of select="$id"/>', 'http://xmlns.com/foaf/0.1/givenname', '<xsl:value-of select="givenname"/>', null, 'persons');
			<xsl:for-each select="aliases/alias[familyname != ../../familyname or givenname != ../../givenname]">
				insert into triples values ('<xsl:value-of select="$id"/>', 'http://purl.org/dc/terms/alternative', '<xsl:value-of select="familyname"/>, <xsl:value-of select="givenname"/>', null, 'persons');
			</xsl:for-each>
			<xsl:for-each select="departments/department">
				<xsl:variable name="pos2" select="position()"/>
				<xsl:variable name="genid">genid:<xsl:value-of select="$id-prefix"/><xsl:value-of select="$pos"/>_<xsl:value-of select="$pos2"/></xsl:variable>
				<xsl:variable name="ou" select="."/>
				<xsl:if test="$ou != ''">
					<xsl:variable name="parent-ou" select="$organizational-units//ou[ou/@name = $ou]/@name"/>
					<xsl:variable name="ou-id" select="$organizational-units//ou[@name = $ou]/@id"/>
					insert into triples values ('<xsl:value-of select="$id"/>', 'http://escidoc.mpg.de/position', '<xsl:value-of select="$genid"/>', null, 'persons');
					<xsl:choose>
						<xsl:when test="$ou = 'External Organizations' or $parent-ou = 'external'">
							insert into triples values ('<xsl:value-of select="$genid"/>', 'http://escidoc.mpg.de/organization', '<xsl:value-of select="$ou"/>', null, null);
						</xsl:when>
						<xsl:otherwise>
							insert into triples values ('<xsl:value-of select="$genid"/>', 'http://escidoc.mpg.de/organization', '<xsl:value-of select="$ou"/>, Max-Planck-Gesellschaft', null, null);
						</xsl:otherwise>
					</xsl:choose>
					insert into triples values ('<xsl:value-of select="$genid"/>', 'http://purl.org/dc/elements/1.1/identifier', '<xsl:value-of select="$ou-id"/>', null, null);
				</xsl:if>
				insert into triples values ('<xsl:value-of select="$id"/>', 'http://purl.org/dc/terms/modified', '<xsl:value-of select="current-date()"/>', null, 'persons');
				insert into triples values ('<xsl:value-of select="$id"/>', 'http://purl.org/dc/terms/created', '<xsl:value-of select="current-date()"/>', null, 'persons');
				insert into triples values ('<xsl:value-of select="$id"/>', 'http://escidoc.mpg.de/modified-by', 'urn:cone:persons102', null, 'persons');
				insert into triples values ('<xsl:value-of select="$id"/>', 'http://escidoc.mpg.de/created-by', 'urn:cone:persons102', null, 'persons');
			</xsl:for-each>
		</xsl:for-each>
	
	</xsl:template>

</xsl:stylesheet>