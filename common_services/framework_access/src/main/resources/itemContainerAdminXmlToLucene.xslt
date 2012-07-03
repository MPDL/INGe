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
-item and container are handled separately because of different namespaces
-all properties and metadata-elements of item/container and all components are indexed with their path, separated with /
-fulltexts are indexed if mime-type of component is application/pdf application/msword text/xml application/xml text/plain
-store=yes: 
    -all fields for highlighting: aa_xml_metadata and fulltext
    -all fields for display: aa_xml_representation
    -all fields for sorting
    -just all fields, except PID and sortfields, this is because scan-operation needs stored fields
-!!all fields are stored because of the scan-request!!
-separate fields for highlighting are stored, but not indexed:
    -aa_xml_metadata for hit-terms in the context of the metadata-xml.
     (metadata for indexing is extracted out of the xml-structure)
    -aa_stored_fulltext<n> (for each fulltext one field) for hit-terms in the context of fulltext
     (complete fulltext is stored)
    -aa_stored_filename<n> (for each fulltext one field with the filename. So filename can get displayed in highlighting)
-sorting can be done for all fields that are untokenized and only occur once in a document.
-additional sortfields can be defined in variable sortfields
-additional compound indexfields can be defined in variable userdefined-indexes

-
 -->
<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xalan="http://xml.apache.org/xalan"
        xmlns:lastdate-helper="xalan://de.escidoc.sb.gsearch.xslt.LastdateHelper"
        xmlns:string-helper="xalan://de.escidoc.sb.gsearch.xslt.StringHelper"
        xmlns:sortfield-helper="xalan://de.escidoc.sb.gsearch.xslt.SortFieldHelper"
        xmlns:escidoc-core-accessor="xalan://de.escidoc.sb.gsearch.xslt.EscidocCoreAccessor" 
        extension-element-prefixes="lastdate-helper string-helper sortfield-helper escidoc-core-accessor">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <!-- Include stylesheet that writes important fields for gsearch -->
    <xsl:include href="index/gsearchAttributes.xslt"/>
    
    <!-- Include stylesheet that indexes values for permission-filtering -->
    <xsl:include href="index/permissions.xslt"/>
    
    <!-- Parameters that get passed while calling this stylesheet-transformation -->
    <xsl:param name="SUPPORTED_MIMETYPES"/>

    <!-- Store Fields for Scan-Operation-->
    <xsl:variable name="STORE_FOR_SCAN">YES</xsl:variable>

    <xsl:variable name="CONTEXTNAME"></xsl:variable>
    <xsl:variable name="SORTCONTEXTPREFIX">/sort</xsl:variable>
    <xsl:variable name="FIELDSEPARATOR">/</xsl:variable>

    <!-- Paths to Metadata -->
    <xsl:variable name="ITEM_MDRECORDSPATH" select="/*[local-name()='item']/*[local-name()='md-records']"/>
    <xsl:variable name="CONTAINER_MDRECORDSPATH" select="/*[local-name()='container']/*[local-name()='md-records']"/>
    
    <!-- Paths to Components -->
    <xsl:variable name="COMPONENT_PATH" select="/*[local-name()='item']/*[local-name()='components']/*[local-name()='component']"/>

    <!-- COMPONENT TYPES THAT DONT GET INDEXED -->
    <xsl:variable name="NON_SUPPORTED_COMPONENT_TYPES"> correspondence copyright-transfer-agreement </xsl:variable>
    
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
        <xsl:call-template name="processPermissionFilters"/>
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
        <xsl:copy-of select="/*[local-name()='item']"/>
    </xsl:template>

    <!-- WRITE THE XML THAT GETS RETURNED BY THE SEARCH -->
    <xsl:template name="writeSearchXmlContainer">
        <xsl:copy-of select="/*[local-name()='container']"/>
    </xsl:template>

    <xsl:template name="processItem">
		<xsl:variable name="objectType" select="'item'" />
        <IndexField termVector="NO" index="UN_TOKENIZED" IFname="type">
            <xsl:attribute name="store">
                <xsl:value-of select="$STORE_FOR_SCAN"/>
            </xsl:attribute>
            <xsl:value-of select="$objectType"/>
        </IndexField>
        <IndexField termVector="NO" index="UN_TOKENIZED">
            <xsl:attribute name="store">
                <xsl:value-of select="$STORE_FOR_SCAN"/>
            </xsl:attribute>
            <xsl:attribute name="IFname">
                <xsl:value-of select="concat($SORTCONTEXTPREFIX,$FIELDSEPARATOR,'type')"/>
            </xsl:attribute>
            <xsl:value-of select="$objectType"/>
        </IndexField>
        <xsl:call-template name="writeIndexField">
            <xsl:with-param name="context" select="$CONTEXTNAME"/>
            <xsl:with-param name="fieldname">id</xsl:with-param>
            <xsl:with-param name="fieldvalue" select="string-helper:removeVersionIdentifier(string-helper:getSubstringAfterLast(/*[local-name()='item']/@*[local-name()='href'], '/'))"/>
            <xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
            <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
        </xsl:call-template>
        <IndexField IFname="aa_xml_representation" index="NO" store="YES" termVector="NO">
            <xsl:text disable-output-escaping="yes">
                &lt;![CDATA[
            </xsl:text>
                <xsl:call-template name="writeSearchXmlItem"/>
            <xsl:text disable-output-escaping="yes">
                ]]&gt;
            </xsl:text>
        </IndexField>
        
        <IndexField IFname="aa_xml_metadata" index="NO" store="YES" termVector="NO">
            <xsl:text disable-output-escaping="yes">
                &lt;![CDATA[
            </xsl:text>
                <xsl:copy-of select="$ITEM_MDRECORDSPATH"/>
            <xsl:text disable-output-escaping="yes">
                ]]&gt;
            </xsl:text>
        </IndexField>

        <!-- COMPLETE XML -->
        <xsl:for-each select="./*">
        	<xsl:for-each select="./@*">
            	<xsl:if test="string(.) and normalize-space(.)!=''
                        and (namespace-uri()!='http://www.w3.org/1999/xlink' 
                        or (namespace-uri()='http://www.w3.org/1999/xlink' and local-name()='title'))">
        			<xsl:call-template name="writeIndexField">
            			<xsl:with-param name="context" select="$CONTEXTNAME"/>
            			<xsl:with-param name="fieldname" select="local-name()"/>
            			<xsl:with-param name="fieldvalue" select="."/>
            			<xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
            			<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
        			</xsl:call-template>
        		</xsl:if>
        	</xsl:for-each>
            <xsl:call-template name="processElementTree">
                <xsl:with-param name="path"/>
                <xsl:with-param name="context" select="$CONTEXTNAME"/>
                <xsl:with-param name="indexAttributes">yes</xsl:with-param>
                <xsl:with-param name="nametype">path</xsl:with-param>
            </xsl:call-template>
        </xsl:for-each>
        
        <!-- FULLTEXTS -->
        <xsl:call-template name="processComponents">
            <xsl:with-param name="num" select="0"/>
            <xsl:with-param name="components" select="$COMPONENT_PATH"/>
            <xsl:with-param name="matchNum" select="1"/>
        </xsl:call-template>
        
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
		<xsl:variable name="objectType" select="'container'" />
        <IndexField termVector="NO" index="UN_TOKENIZED" IFname="type">
            <xsl:attribute name="store">
                <xsl:value-of select="$STORE_FOR_SCAN"/>
            </xsl:attribute>
            <xsl:value-of select="$objectType"/>
        </IndexField>
        <IndexField termVector="NO" index="UN_TOKENIZED">
            <xsl:attribute name="store">
                <xsl:value-of select="$STORE_FOR_SCAN"/>
            </xsl:attribute>
            <xsl:attribute name="IFname">
                <xsl:value-of select="concat($SORTCONTEXTPREFIX,$FIELDSEPARATOR,'type')"/>
            </xsl:attribute>
            <xsl:value-of select="$objectType"/>
        </IndexField>
        <xsl:call-template name="writeIndexField">
            <xsl:with-param name="context" select="$CONTEXTNAME"/>
            <xsl:with-param name="fieldname">id</xsl:with-param>
            <xsl:with-param name="fieldvalue" select="string-helper:removeVersionIdentifier(string-helper:getSubstringAfterLast(/*[local-name()='container']/@*[local-name()='href'], '/'))"/>
            <xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
            <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
        </xsl:call-template>
        <IndexField IFname="aa_xml_representation" index="NO" store="YES" termVector="NO">
            <xsl:text disable-output-escaping="yes">
                &lt;![CDATA[
            </xsl:text>
                <xsl:call-template name="writeSearchXmlContainer"/>
            <xsl:text disable-output-escaping="yes">
                ]]&gt;
            </xsl:text>
        </IndexField>
        
        <IndexField IFname="aa_xml_metadata" index="NO" store="YES" termVector="NO">
            <xsl:text disable-output-escaping="yes">
                &lt;![CDATA[
            </xsl:text>
                <xsl:copy-of select="$CONTAINER_MDRECORDSPATH"/>
            <xsl:text disable-output-escaping="yes">
                ]]&gt;
            </xsl:text>
        </IndexField>

        <!-- COMPLETE XML -->
        <xsl:for-each select="./*">
        	<xsl:for-each select="./@*">
            	<xsl:if test="string(.) and normalize-space(.)!=''
                        and (namespace-uri()!='http://www.w3.org/1999/xlink' 
                        or (namespace-uri()='http://www.w3.org/1999/xlink' and local-name()='title'))">
        			<xsl:call-template name="writeIndexField">
            			<xsl:with-param name="context" select="$CONTEXTNAME"/>
            			<xsl:with-param name="fieldname" select="local-name()"/>
            			<xsl:with-param name="fieldvalue" select="."/>
            			<xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
            			<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
        			</xsl:call-template>
        		</xsl:if>
        	</xsl:for-each>
            <xsl:call-template name="processElementTree">
                <xsl:with-param name="path"/>
                <xsl:with-param name="context" select="$CONTEXTNAME"/>
                <xsl:with-param name="indexAttributes">yes</xsl:with-param>
                <xsl:with-param name="nametype">path</xsl:with-param>
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
        <!-- name of index-field -->
        <xsl:param name="path"/>
        <!-- prefix for index-name -->
        <xsl:param name="context"/>
        <!-- if 'yes', also write attributes as index-fields -->
        <xsl:param name="indexAttributes"/>
        <!-- nametype defines if paths are used for indexnames or elementname only -->
        <!-- can be 'path' or 'element' -->
        <!-- eg first-name or publication.creator.person.first-name -->
        <xsl:param name="nametype"/>
        <xsl:if test="string(text()) and normalize-space(text())!=''">
            <xsl:call-template name="writeIndexField">
                <xsl:with-param name="context" select="$context"/>
                <xsl:with-param name="fieldname" select="$path"/>
                <xsl:with-param name="fieldvalue" select="text()"/>
                <xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
                <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="$indexAttributes='yes'">
            <!-- ITERATE ALL ATTRIBUTES AND WRITE ELEMENT-NAME, ATTRIBUTE-NAME AND ATTRIBUTE-VALUE -->
            <!--  EXCEPT FOR XLINK-ATTRIBUTES -->
            <xsl:for-each select="@*">
                <xsl:if test="string(.) and normalize-space(.)!=''
                        and string($path) and normalize-space($path)!='' 
                        and namespace-uri()!='http://www.w3.org/1999/xlink'">
                    <xsl:call-template name="writeIndexField">
                        <xsl:with-param name="context" select="$context"/>
                        <xsl:with-param name="fieldname" select="concat($path,$FIELDSEPARATOR,local-name())"/>
                        <xsl:with-param name="fieldvalue" select="."/>
                        <xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
                        <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
                    </xsl:call-template>
                </xsl:if>
                <!--  WRITE HREF-ATTRIBUTES AS ID (EXTRACT ID OUT OF HREF) -->
                <xsl:if test="string(.) and normalize-space(.)!=''
                        and string($path) and normalize-space($path)!='' 
                        and namespace-uri()='http://www.w3.org/1999/xlink'
                        and local-name()='href'">
                	<xsl:variable name="objectId" select="string-helper:getSubstringAfterLast(., '/')"/>
                	<xsl:if test="string($objectId) and normalize-space($objectId)!=''
                        and contains($objectId, ':')">
                    	<xsl:call-template name="writeIndexField">
                        	<xsl:with-param name="context" select="$context"/>
                        	<xsl:with-param name="fieldname" select="concat($path,$FIELDSEPARATOR,'id')"/>
                        	<xsl:with-param name="fieldvalue" select="$objectId"/>
                        	<xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
                        	<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
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
                                <xsl:value-of select="concat($path,$FIELDSEPARATOR,local-name())"/>
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
                <xsl:with-param name="indexAttributes" select="$indexAttributes"/>
                <xsl:with-param name="path" select="$fieldname"/>
                <xsl:with-param name="nametype" select="$nametype"/>
            </xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <!-- RECURSIVE ITERATION FOR COMPONENTS (FULLTEXTS) -->
    <!-- STORE EVERYTHING IN FIELD fulltext FOR SEARCH-->
    <!-- STORE EACH FULLTEXT IN SEPARATE FIELD aa_stored_fulltext<n> FOR HIGHLIGHTING -->
    <!-- ADDITIONALLY STORE HREF OF COMPONENT IN SEPARATE FIELD aa_stored_filename<n> FOR HIGHLIGHTING THE LOCATION OF THE FULLTEXT-->
    <!-- ONLY INDEX FULLTEXTS IF MIME_TYPE IS text/xml, application/xml, text/plain, application/msword or application/pdf -->
    <!-- ONLY INDEX FULLTEXTS IF component-type IS NOT correspondence OR copyright transfer agreement-->
    <xsl:template name="processComponents" xmlns:xlink="http://www.w3.org/1999/xlink">
        <xsl:param name="num"/>
        <xsl:param name="components"/>
        <xsl:param name="matchNum"/>
        <xsl:variable name="component-type" select="$components[$num]/*[local-name()='properties']/*[local-name()='content-category']"/>
        <!-- xsl:variable name="visibility" select="$components[$num]/*[local-name()='properties']/*[local-name()='visibility']"/ -->
        <xsl:variable name="mime-type">
            <xsl:value-of select="$components[$num]/*[local-name()='properties']/*[local-name()='mime-type']"/>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="string($mime-type) 
                            and contains($SUPPORTED_MIMETYPES,$mime-type)
                            and string($component-type)
                            and contains($NON_SUPPORTED_COMPONENT_TYPES,concat(' ',$component-type,' '))=false">
                <!-- INDEX FULLTEXT -->
                <IndexField index="TOKENIZED" store="YES" termVector="NO">
                    <xsl:attribute name="dsId">
                        <xsl:value-of select="$components[$num]/*[local-name()='content']/@xlink:href"/>
                    </xsl:attribute>
                    <xsl:attribute name="IFname">
                        <xsl:value-of select="concat($FIELDSEPARATOR,'fulltext')"/>
                    </xsl:attribute>
                    <xsl:attribute name="store">
                        <xsl:value-of select="$STORE_FOR_SCAN"/>
                    </xsl:attribute>
                </IndexField>

                <!-- SEPERATELY STORE EACH FULLTEXT IN DIFFERENT FIELD FOR HIGHLIGHTING -->
                <IndexField index="NO" store="YES" termVector="NO">
                    <xsl:attribute name="dsId">
                        <xsl:value-of select="$components[$num]/*[local-name()='content']/@xlink:href"/>
                    </xsl:attribute>
                    <xsl:attribute name="IFname">
                        <xsl:value-of select="concat('aa_stored_fulltext',$matchNum)"/>
                    </xsl:attribute>
                </IndexField>

                <!-- SEPERATELY STORE FILENAME FOR EACH FULLTEXT FOR HIGHLIGHTING -->
                <IndexField index="NO" store="YES" termVector="NO">
                    <xsl:attribute name="IFname">
                        <xsl:value-of select="concat('aa_stored_filename',$matchNum)"/>
                    </xsl:attribute>
                    <xsl:value-of select="$components[$num]/*[local-name()='content']/@xlink:href"/>
                </IndexField>

                <xsl:choose>
                    <xsl:when test="$components[$num + 1]">
                        <xsl:call-template name="processComponents">
                            <xsl:with-param name="num" select="$num + 1"/>
                            <xsl:with-param name="components" select="$components"/>
                            <xsl:with-param name="matchNum" select="$matchNum + 1"/>
                        </xsl:call-template>
                    </xsl:when>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="$components[$num + 1]">
                        <xsl:call-template name="processComponents">
                            <xsl:with-param name="num" select="$num + 1"/>
                            <xsl:with-param name="components" select="$components"/>
                            <xsl:with-param name="matchNum" select="$matchNum"/>
                        </xsl:call-template>
                    </xsl:when>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!--  WRITE INDEXFIELD -->
    <xsl:template name="writeIndexField">
        <xsl:param name="context"/>
        <xsl:param name="fieldname"/>
        <xsl:param name="fieldvalue"/>
        <xsl:param name="indextype"/>
        <xsl:param name="store"/>
        <xsl:param name="noFieldSeparator"/>
        <xsl:if test="string($fieldvalue) and normalize-space($fieldvalue)!=''">
            <IndexField termVector="NO">
                <xsl:attribute name="index">
                	<xsl:value-of select="$indextype"/>
                </xsl:attribute>
                <xsl:attribute name="store">
                    <xsl:value-of select="$store"/>
                </xsl:attribute>
		        <xsl:choose>
		            <xsl:when test="$noFieldSeparator='true'">
		                <xsl:attribute name="IFname">
		                    <xsl:value-of select="concat($context,$fieldname)"/>
		                </xsl:attribute>
		            </xsl:when>
		            <xsl:otherwise>
		                <xsl:attribute name="IFname">
		                    <xsl:value-of select="concat($context,$FIELDSEPARATOR,$fieldname)"/>
		                </xsl:attribute>
		            </xsl:otherwise>
		        </xsl:choose>
                <xsl:value-of select="$fieldvalue"/>
            </IndexField>
            <xsl:call-template name="writeSortField">
                <xsl:with-param name="context" select="$context"/>
                <xsl:with-param name="fieldname" select="$fieldname"/>
                <xsl:with-param name="fieldvalue" select="$fieldvalue"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
        
    <!--  WRITE SORTFIELD -->
    <xsl:template name="writeSortField">
        <xsl:param name="context"/>
        <xsl:param name="fieldname"/>
        <xsl:param name="fieldvalue"/>
        <xsl:if test="string($fieldvalue) 
                    and normalize-space($fieldvalue)!=''
                    and sortfield-helper:checkSortField(concat($SORTCONTEXTPREFIX,$context,$FIELDSEPARATOR,$fieldname)) = false()">
            <IndexField termVector="NO" index="UN_TOKENIZED" store="NO">
                <xsl:attribute name="IFname">
                    <xsl:value-of select="concat($SORTCONTEXTPREFIX,$context,$FIELDSEPARATOR,$fieldname)"/>
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
            <xsl:variable name="noFieldSeparator" select="./@no-field-separator"/>
            <xsl:for-each select="./element">
                <xsl:if test="string(.) and normalize-space(.)!=''">
                    <xsl:call-template name="writeIndexField">
                        <xsl:with-param name="context" select="$context"/>
                        <xsl:with-param name="fieldname" select="$index-name"/>
                        <xsl:with-param name="fieldvalue" select="."/>
                        <xsl:with-param name="indextype" select="./@index"/>
                        <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
                        <xsl:with-param name="noFieldSeparator" select="$noFieldSeparator"/>
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
        <xsl:variable name="objectId" select="string-helper:getSubstringAfterLast(/*/@*[local-name()='href'], '/')"/>
        <userdefined-index name="resources/parent">
            <xsl:attribute name="context">
                <xsl:value-of select="$CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:if test="string($objectId) and $objectId != ''">
                	<xsl:value-of select="escidoc-core-accessor:getObjectAttribute(
                		concat('/ir/', local-name(/*), '/', $objectId, '/resources/parents'),'/parents/parent','href','http://www.w3.org/1999/xlink','false','true')"/>
                </xsl:if>
            </element>
        </userdefined-index>
        
        <xsl:if test="string($objectId) and $objectId != ''">
        	<xsl:variable name="parents" select="escidoc-core-accessor:getObjectAttribute(
                		concat('/ir/', local-name(/*), '/', $objectId, '/resources/parents'),'/parents/parent','href','http://www.w3.org/1999/xlink','false','true')"/>
        	<xsl:if test="not($parents)">
	        	<userdefined-index no-field-separator="true">
	            	<xsl:attribute name="context">
	                	<xsl:value-of select="$CONTEXTNAME"/>
	            	</xsl:attribute>
	            	<xsl:attribute name="name">
	                	<xsl:value-of select="concat('top-level-', local-name(/*), 's')"/>
	            	</xsl:attribute>
	            	<element index="UN_TOKENIZED">true</element>
	        	</userdefined-index>
        	</xsl:if>
        </xsl:if>

	<!--Flag for latest version -->
	<userdefined-index name="isLatestVersion">
            <xsl:attribute name="context">
                <xsl:value-of select="$CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:choose>
                <xsl:when test="/*[local-name()='item']/*[local-name()='properties']/*[local-name()='version']/*[local-name()='number'] = /*[local-name()='item']/*[local-name()='properties']/*[local-name()='latest-version']/*[local-name()='number']">
                	<xsl:value-of select="'true'"/>
                </xsl:when>
                <xsl:otherwise>
                	<xsl:value-of select="'false'"/>
                </xsl:otherwise>
                </xsl:choose>
            </element>
        </userdefined-index>
    </xsl:variable>

</xsl:stylesheet>   
