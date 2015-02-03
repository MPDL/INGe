<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ CDDL HEADER START
  ~
  ~ The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
  ~ only (the "License"). You may not use this file except in compliance with the License.
  ~
  ~ You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
  ~ for the specific language governing permissions and limitations under the License.
  ~
  ~ When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
  ~ license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
  ~ brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
  ~
  ~ CDDL HEADER END
  ~
  ~ Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
  ~ and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
  ~ terms.
  -->

<!--
Notes:
-following fields are indexed:
    all properties + all attributes of all properties as path
    item/@last-modification-date
    all metadata + all attributes of all metadata as path of md-record with name=escidoc
    path-list of md-element organization/identifier of md-record with name=escidoc
    name-attribute, namespace-uri of each md-record concatenated with |
-output-format:
    <id>escidoc:1</id>
    <latest-release-date></latest-release-date>
    <last-modification-date></last-modification-date>
    <context-id></context-id>
    <organizational-unit-id></ organizational-unit-id >
    … + parent ous
    <organizational-unit-id></ organizational-unit-id>
    <deleted>false/true</deleted>
    
-store=yes: 
    -all fields for display: xml_representation
    -all fields for sorting
    -just all fields, except PID and sortfields, this is because scan-operation needs stored fields
-!!all fields are stored because of the scan-request!!
-sorting can be done for last-modification-date.
-additional sortfields can be defined in variable sortfields
-additional compound indexfields can be defined in variable userdefined-indexes
 -->
<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xalan="http://xml.apache.org/xalan"
        xmlns:xsltxsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:lastdate-helper="xalan://de.escidoc.sb.gsearch.xslt.LastdateHelper"
        xmlns:string-helper="xalan://de.escidoc.sb.gsearch.xslt.StringHelper"
        xmlns:sortfield-helper="xalan://de.escidoc.sb.gsearch.xslt.SortFieldHelper"
        xmlns:escidoc-core-accessor="xalan://de.escidoc.sb.gsearch.xslt.EscidocCoreAccessor" 
        extension-element-prefixes="lastdate-helper string-helper sortfield-helper escidoc-core-accessor">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <!-- Include stylesheet that writes important fields for gsearch -->
    <xsl:include href="index/gsearchAttributes.xslt"/>
    
    <!-- Store Fields for Scan-Operation-->
    <xsl:variable name="STORE_FOR_SCAN">NO</xsl:variable>

    <xsl:variable name="CONTEXTNAME">escidoc</xsl:variable>
    <xsl:variable name="SORTCONTEXTPREFIX">sort</xsl:variable>

    <!-- Paths to Metadata -->
    <xsl:variable name="ITEM_METADATAPATH" select="/*[local-name()='item']/*[local-name()='md-records']/*[local-name()='md-record'][@name='escidoc']"/>
    <xsl:variable name="CONTAINER_METADATAPATH" select="/*[local-name()='container']/*[local-name()='md-records']/*[local-name()='md-record'][@name='escidoc']"/>
    
    <!-- Paths to Properties -->
    <xsl:variable name="ITEM_PROPERTIESPATH" select="/*[local-name()='item']/*[local-name()='properties']"/>
    <xsl:variable name="CONTAINER_PROPERTIESPATH" select="/*[local-name()='container']/*[local-name()='properties']"/>

    <xsl:template match="/">
        <xsl:variable name="type">
            <xsl:for-each select="*">
                <xsl:if test="position() = 1">
                    <xsl:value-of select="local-name()"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
        <IndexDocument> 
        <!-- Call this template immediately after opening IndexDocument-element! -->
        <xsl:call-template name="processGsearchAttributes"/>
        <xsl:choose>
            <xsl:when test="$type='item'">
                <xsl:call-template name="processItem"/>
            </xsl:when>
            <xsl:when test="$type='container'">
                <xsl:call-template name="processContainer"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="processContainer"/>
            </xsl:otherwise>
        </xsl:choose>
        </IndexDocument> 
    </xsl:template>

    <!-- WRITE THE XML THAT GETS RETURNED BY THE SEARCH -->
    <xsl:template name="writeSearchXmlItem">
        <oai-object:oai-object xmlns:oai-object="http://www.escidoc.de/schemas/oai-object/0.1">
            <oai-object:resource-type>item</oai-object:resource-type>
            <oai-object:id>
                <xsl:value-of select="string-helper:removeVersionIdentifier(string-helper:getSubstringAfterLast(/*[local-name()='item']/@*[local-name()='href'], '/'))"/>
            </oai-object:id>
            <oai-object:last-modification-date>
                <xsl:value-of select="/*[local-name()='item']/@last-modification-date"/>
            </oai-object:last-modification-date>
            <oai-object:latest-release-date>
                <xsl:value-of select="/*[local-name()='item']/*[local-name()='properties']/*[local-name()='latest-release']/*[local-name()='date']"/>
            </oai-object:latest-release-date>
            <oai-object:context-id>
                <xsl:value-of select="string-helper:getSubstringAfterLast(/*[local-name()='item']/*[local-name()='properties']/*[local-name()='context']/@*[local-name()='href'], '/')"/>
            </oai-object:context-id>
            <xsl:for-each select="$ITEM_METADATAPATH//*[local-name()='creator']//*[local-name()='organization']/*[local-name()='identifier']">
                <xsl:variable name="objectId" select="normalize-space(.)"/>
                <xsl:if test="string($objectId) and normalize-space($objectId)!=''">
                    <xsl:variable name="parentous" select="escidoc-core-accessor:getObjectAttribute(
                        concat('/oum/organizational-unit/',$objectId,'/resources/path-list'),'/organizational-unit-path-list/organizational-unit-path/organizational-unit-ref','href','http://www.w3.org/1999/xlink','true','true')"/>
                    <xsl:for-each select="xalan:tokenize($parentous, ' ')">
                        <oai-object:organizational-unit-id>
                            <xsl:value-of select="."/>
                        </oai-object:organizational-unit-id>
                    </xsl:for-each>
                </xsl:if>
            </xsl:for-each>
            <oai-object:deleted>
                <xsl:choose>
                   <xsl:when test="/*[local-name()='item']/*[local-name()='properties']/*[local-name()='public-status']='withdrawn'">true</xsl:when>
                    <xsl:otherwise>false</xsl:otherwise>
                </xsl:choose>
           </oai-object:deleted>
        </oai-object:oai-object>
    </xsl:template>

    <!-- WRITE THE XML THAT GETS RETURNED BY THE SEARCH -->
    <xsl:template name="writeSearchXmlContainer">
        <oai-object:oai-object xmlns:oai-object="http://www.escidoc.de/schemas/oai-object/0.1">
            <oai-object:resource-type>container</oai-object:resource-type>
            <oai-object:id>
                <xsl:value-of select="string-helper:removeVersionIdentifier(string-helper:getSubstringAfterLast(/*[local-name()='container']/@*[local-name()='href'], '/'))"/>
            </oai-object:id>
            <oai-object:last-modification-date>
                <xsl:value-of select="/*[local-name()='container']/@last-modification-date"/>
            </oai-object:last-modification-date>
            <oai-object:latest-release-date>
                <xsl:value-of select="/*[local-name()='container']/*[local-name()='properties']/*[local-name()='latest-release']/*[local-name()='date']"/>
            </oai-object:latest-release-date>
            <oai-object:context-id>
                <xsl:value-of select="string-helper:getSubstringAfterLast(/*[local-name()='container']/*[local-name()='properties']/*[local-name()='context']/@*[local-name()='href'], '/')"/>
            </oai-object:context-id>
            <xsl:for-each select="$CONTAINER_METADATAPATH//*[local-name()='creator']//*[local-name()='organization']/*[local-name()='identifier']">
                <xsl:variable name="objectId" select="normalize-space(.)"/>
                <xsl:if test="string($objectId) and normalize-space($objectId)!=''">
                    <xsl:variable name="parentous" select="escidoc-core-accessor:getObjectAttribute(
                        concat('/oum/organizational-unit/',$objectId,'/resources/path-list'),'/organizational-unit-path-list/organizational-unit-path/organizational-unit-ref','href','http://www.w3.org/1999/xlink','true','true')"/>
                    <xsl:for-each select="xalan:tokenize($parentous, ' ')">
                        <oai-object:organizational-unit-id>
                            <xsl:value-of select="."/>
                        </oai-object:organizational-unit-id>
                    </xsl:for-each>
                </xsl:if>
            </xsl:for-each>
            <oai-object:deleted>
                <xsl:choose>
                   <xsl:when test="/*[local-name()='container']/*[local-name()='properties']/*[local-name()='public-status']='withdrawn'">true</xsl:when>
                    <xsl:otherwise>false</xsl:otherwise>
                </xsl:choose>
            </oai-object:deleted>
        </oai-object:oai-object>
    </xsl:template>

    <xsl:template name="processItem">
        <xsl:call-template name="writeIndexField">
            <xsl:with-param name="context" select="$CONTEXTNAME"/>
            <xsl:with-param name="fieldname">objecttype</xsl:with-param>
            <xsl:with-param name="fieldvalue">item</xsl:with-param>
            <xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
            <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
        </xsl:call-template>

        <!-- Write xml-representation for search-result -->
        <IndexField IFname="xml_representation" index="NO" store="YES" termVector="NO">
            <xsl:text disable-output-escaping="yes">
                &lt;![CDATA[
            </xsl:text>
                <xsl:call-template name="writeSearchXmlItem"/>
            <xsl:text disable-output-escaping="yes">
                ]]&gt;
            </xsl:text>
        </IndexField>
        
        <!-- WRITE ALL PROPERTIES -->
        <xsl:for-each select="$ITEM_PROPERTIESPATH">
            <xsl:call-template name="processElementTree">
                <xsl:with-param name="path"/>
                <xsl:with-param name="context" select="$CONTEXTNAME"/>
                <xsl:with-param name="nametype">path</xsl:with-param>
                <xsl:with-param name="withAttributes">YES</xsl:with-param>
                <xsl:with-param name="sort">NO</xsl:with-param>
            </xsl:call-template>
        </xsl:for-each>

        <!-- WRITE ALL METADATA WITH NAME=escidoc -->
        <xsl:for-each select="$ITEM_METADATAPATH">
            <xsl:call-template name="processElementTree">
                <xsl:with-param name="path"/>
                <xsl:with-param name="context" select="$CONTEXTNAME"/>
                <xsl:with-param name="nametype">path</xsl:with-param>
                <xsl:with-param name="withAttributes">YES</xsl:with-param>
                <xsl:with-param name="sort">NO</xsl:with-param>
            </xsl:call-template>
        </xsl:for-each>

        <!-- SORT FIELDS -->
        <xsl:for-each select="xalan:nodeset($sortfields)/sortfield">
            <xsl:if test="./@type='item'">
                <xsl:call-template name="writeSortField">
                    <xsl:with-param name="context" select="$CONTEXTNAME"/>
                    <xsl:with-param name="fieldname" select="./@name"/>
                    <xsl:with-param name="fieldvalue" select="./@path"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>
        
        <!-- USER DEFINED INDEXES -->
        <xsl:call-template name="writeUserdefinedIndexes" />
    </xsl:template>

    <xsl:template name="processContainer">
        <xsl:call-template name="writeIndexField">
            <xsl:with-param name="context" select="$CONTEXTNAME"/>
            <xsl:with-param name="fieldname">objecttype</xsl:with-param>
            <xsl:with-param name="fieldvalue">container</xsl:with-param>
            <xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
            <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
        </xsl:call-template>

        <!-- Write xml-representation for search-result -->
        <IndexField IFname="xml_representation" index="NO" store="YES" termVector="NO">
            <xsl:text disable-output-escaping="yes">
                &lt;![CDATA[
            </xsl:text>
                <xsl:call-template name="writeSearchXmlContainer"/>
            <xsl:text disable-output-escaping="yes">
                ]]&gt;
            </xsl:text>
        </IndexField>
        
        <!-- WRITE ALL PROPERTIES -->
        <xsl:for-each select="$CONTAINER_PROPERTIESPATH">
            <xsl:call-template name="processElementTree">
                <xsl:with-param name="path"/>
                <xsl:with-param name="context" select="$CONTEXTNAME"/>
                <xsl:with-param name="nametype">path</xsl:with-param>
                <xsl:with-param name="withAttributes">YES</xsl:with-param>
                <xsl:with-param name="sort">NO</xsl:with-param>
            </xsl:call-template>
        </xsl:for-each>

        <!-- WRITE ALL METADATA WITH NAME=escidoc -->
        <xsl:for-each select="$CONTAINER_METADATAPATH">
            <xsl:call-template name="processElementTree">
                <xsl:with-param name="path"/>
                <xsl:with-param name="context" select="$CONTEXTNAME"/>
                <xsl:with-param name="nametype">path</xsl:with-param>
                <xsl:with-param name="withAttributes">YES</xsl:with-param>
                <xsl:with-param name="sort">NO</xsl:with-param>
            </xsl:call-template>
        </xsl:for-each>

        <!-- SORT FIELDS -->
        <xsl:for-each select="xalan:nodeset($sortfields)/sortfield">
            <xsl:if test="./@type='container'">
                <xsl:call-template name="writeSortField">
                    <xsl:with-param name="context" select="$CONTEXTNAME"/>
                    <xsl:with-param name="fieldname" select="./@name"/>
                    <xsl:with-param name="fieldvalue" select="./@path"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <!-- USER DEFINED INDEXES -->
        <xsl:call-template name="writeUserdefinedIndexes" />
    </xsl:template>

    <!-- RECURSIVE ITERATION OF ELEMENTS -->
    <!-- ITERATE ALL ELEMENTS AND WRITE ELEMENT-NAME AND ELEMENT-VALUE -->
    <xsl:template name="processElementTree">
        <xsl:param name="path"/>
        <xsl:param name="context"/>
        <!-- nametype defines if paths are used for indexnames or elementname only -->
        <!-- eg first-name or publication.creator.person.first-name -->
        <!-- can be 'path' or 'element' -->
        <xsl:param name="nametype"/>
        <xsl:param name="withAttributes"/>
        <xsl:param name="sort"/>
        <xsl:if test="string(text()) and normalize-space(text())!=''">
            <xsl:call-template name="writeIndexField">
                <xsl:with-param name="context" select="$context"/>
                <xsl:with-param name="fieldname" select="$path"/>
                <xsl:with-param name="fieldvalue" select="text()"/>
                <xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
                <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
                <xsl:with-param name="sort" select="$sort"/>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="local-name()!='md-record' and local-name()!='properties' and $withAttributes='YES'">
            <xsl:for-each select="@*">
                <xsl:if test="string(.) and normalize-space(.)!='' 
                        and namespace-uri()!='http://www.w3.org/1999/xlink'">
                    <xsl:call-template name="writeIndexField">
                        <xsl:with-param name="context" select="$context"/>
                        <xsl:with-param name="fieldname" select="concat($path,'.',local-name())"/>
                        <xsl:with-param name="fieldvalue" select="."/>
                        <xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
                        <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
                        <xsl:with-param name="sort" select="$sort"/>
                    </xsl:call-template>
                </xsl:if>
                <!--  WRITE HREF-ATTRIBUTES AS ID (EXTRACT ID OUT OF HREF) -->
                <xsl:if test="string(.) and normalize-space(.)!=''
                        and namespace-uri()='http://www.w3.org/1999/xlink'
                        and local-name()='href'">
                	<xsl:variable name="objectId" select="string-helper:getSubstringAfterLast(., '/')"/>
                	<xsl:if test="string($objectId) and normalize-space($objectId)!=''
                        and contains($objectId, ':')">
                    	<xsl:call-template name="writeIndexField">
                        	<xsl:with-param name="context" select="$context"/>
                        	<xsl:with-param name="fieldname" select="concat($path,'.','objid')"/>
                        	<xsl:with-param name="fieldvalue" select="$objectId"/>
                        	<xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
                        	<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
                        	<xsl:with-param name="sort" select="$sort"/>
                    	</xsl:call-template>
                    </xsl:if>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:for-each select="./*">
            <xsl:variable name="fieldname">
                <xsl:choose>
                    <xsl:when test="$nametype='element'">
                            <xsl:value-of select="local-name()"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:choose>
                            <xsl:when test="string($path) and normalize-space($path)!=''">
                                <xsl:value-of select="concat($path,'.',local-name())"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="local-name()"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:call-template name="processElementTree">
                <xsl:with-param name="context" select="$context"/>
                <xsl:with-param name="path" select="$fieldname"/>
                <xsl:with-param name="nametype" select="$nametype"/>
                <xsl:with-param name="withAttributes" select="$withAttributes"/>
                <xsl:with-param name="sort" select="$sort"/>
            </xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <!--  WRITE INDEXFIELD -->
    <xsl:template name="writeIndexField">
        <xsl:param name="context"/>
        <xsl:param name="fieldname"/>
        <xsl:param name="fieldvalue"/>
        <xsl:param name="indextype"/>
        <xsl:param name="store"/>
        <xsl:param name="sort"/>
        <xsl:if test="string($fieldvalue) and normalize-space($fieldvalue)!=''">
            <IndexField termVector="NO">
                <xsl:attribute name="index">
                	<xsl:value-of select="$indextype"/>
                </xsl:attribute>
                <xsl:attribute name="store">
                    <xsl:value-of select="$store"/>
                </xsl:attribute>
                <xsl:attribute name="IFname">
                    <xsl:value-of select="concat($context,'.',$fieldname)"/>
                </xsl:attribute>
                <xsl:value-of select="$fieldvalue"/>
            </IndexField>
            <xsl:if test="string($sort) and normalize-space($sort)!='' and $sort='YES'">
                <xsl:call-template name="writeSortField">
                    <xsl:with-param name="context" select="$context"/>
                    <xsl:with-param name="fieldname" select="$fieldname"/>
                    <xsl:with-param name="fieldvalue" select="$fieldvalue"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>
        
    <!--  WRITE SORTFIELD -->
    <xsl:template name="writeSortField">
        <xsl:param name="context"/>
        <xsl:param name="fieldname"/>
        <xsl:param name="fieldvalue"/>
        <xsl:if test="string($fieldvalue) 
                    and normalize-space($fieldvalue)!=''
                    and sortfield-helper:checkSortField(concat($SORTCONTEXTPREFIX,'.',$context,'.',$fieldname)) = false()">
            <IndexField termVector="NO" index="UN_TOKENIZED" store="NO">
                <xsl:attribute name="IFname">
                    <xsl:value-of select="concat($SORTCONTEXTPREFIX,'.',$context,'.',$fieldname)"/>
                </xsl:attribute>
                <xsl:value-of select="string-helper:getNormalizedString($fieldvalue)"/>
            </IndexField>
        </xsl:if>
    </xsl:template>
        
    <!-- WRITE USERDEFINED INDEX -->
    <xsl:template name="writeUserdefinedIndexes">
        <xsl:for-each select="xalan:nodeset($userdefined-indexes)/userdefined-index">
            <xsl:variable name="index-name" select="./@name"/>
            <xsl:variable name="context" select="./@context"/>
            <xsl:for-each select="./element">
                <xsl:if test="string(.) and normalize-space(.)!=''">
                    <xsl:call-template name="writeIndexField">
                        <xsl:with-param name="context" select="$context"/>
                        <xsl:with-param name="fieldname" select="$index-name"/>
                        <xsl:with-param name="fieldvalue" select="."/>
                        <xsl:with-param name="indextype" select="./@index"/>
                        <xsl:with-param name="store" select="./@store"/>
                        <xsl:with-param name="sort" select="./@sortfield"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>
        
    <!-- SORTFIELDS -->
    <xsl:variable name="sortfields">
    </xsl:variable>
    
    <!-- USER DEFINED INDEX FIELDS -->
    <xsl:variable name="userdefined-indexes">
        <userdefined-index name="last-modification-date">
            <xsl:attribute name="context">
                <xsl:value-of select="$CONTEXTNAME"/>
            </xsl:attribute>
            <element index="TOKENIZED" store="NO" sortfield="YES">
                <xsl:value-of select="/*[local-name()='item']/@last-modification-date"/>
            </element>
            <element index="TOKENIZED" store="NO" sortfield="YES">
                <xsl:value-of select="/*[local-name()='container']/@last-modification-date"/>
            </element>
        </userdefined-index>
        <userdefined-index name="objid">
            <xsl:attribute name="context">
                <xsl:value-of select="$CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED" store="NO" sortfield="NO">
                <xsl:value-of select="string-helper:removeVersionIdentifier(string-helper:getSubstringAfterLast(/*[local-name()='item']/@*[local-name()='href'], '/'))"/>
            </element>
            <element index="UN_TOKENIZED" store="NO" sortfield="NO">
                <xsl:value-of select="string-helper:removeVersionIdentifier(string-helper:getSubstringAfterLast(/*[local-name()='container']/@*[local-name()='href'], '/'))"/>
            </element>
        </userdefined-index>
        <userdefined-index name="md-record-identifier">
            <xsl:attribute name="context">
                <xsl:value-of select="$CONTEXTNAME"/>
            </xsl:attribute>
            <xsl:for-each select="/*[local-name()='item']/*[local-name()='md-records']/*[local-name()='md-record']">
                <element index="TOKENIZED" store="NO" sortfield="NO">
                    <xsl:value-of select="concat(./@name,'@',namespace-uri(./*))"/>
                </element>
            </xsl:for-each>
            <xsl:for-each select="/*[local-name()='container']/*[local-name()='md-records']/*[local-name()='md-record']">
                <element index="TOKENIZED" store="NO" sortfield="NO">
                    <xsl:value-of select="concat(./@name,'@',namespace-uri(./*))"/>
                </element>
            </xsl:for-each>
	    </userdefined-index>
	
		<xsl:for-each select="//*[local-name()='component']">
			<xsl:variable name="storage" select="*[local-name()='content']/@storage" />
			<xsl:if test="$storage='internal-managed'">
					
					<userdefined-index name="component.internal-managed.visibility">
						<xsl:attribute name="context">
		                	<xsl:value-of select="$CONTEXTNAME" />
		                </xsl:attribute>
		
						<element index="TOKENIZED" store="NO" sortfield="NO">
							<xsl:value-of select="*[local-name()='properties']/*[local-name()='visibility']" />
						</element>
					</userdefined-index>
		
					<userdefined-index name="component.internal-managed.content-category">
						<xsl:attribute name="context">
		                	<xsl:value-of select="$CONTEXTNAME" />
		                </xsl:attribute>
		                
						<element index="TOKENIZED" store="NO" sortfield="NO">
							<xsl:value-of select="*[local-name()='properties']/*[local-name()='content-category']" />
						</element>
		
					</userdefined-index>
			</xsl:if>
		</xsl:for-each>

    </xsl:variable>

</xsl:stylesheet>   
