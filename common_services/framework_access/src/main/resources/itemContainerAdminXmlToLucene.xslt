<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ CDDL HEADER START
  ~
  ~ The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
  ~ only (the "License"). You may not use this file except in compliance with the License.
  ~
  ~ You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.org/license. See the License
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
        xmlns:element-type-helper="xalan://de.escidoc.sb.gsearch.xslt.ElementTypeHelper"
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
	<xsl:variable name="ITEM_METADATAPATH" select="/*[local-name()='item']/*[local-name()='md-records']/*[local-name()='md-record'][@name='escidoc']"/>
	<xsl:variable name="CONTAINER_METADATAPATH" select="/*[local-name()='container']/*[local-name()='md-records']/*[local-name()='md-record'][@name='escidoc']"/>

	
	<xsl:variable name="ITEM_MDRECORDSPATH" select="/*[local-name()='item']/*[local-name()='md-records']"/>
    <xsl:variable name="CONTAINER_MDRECORDSPATH" select="/*[local-name()='container']/*[local-name()='md-records']"/>
    
	
    <!-- Paths to Properties -->
    <xsl:variable name="ITEM_PROPERTIESPATH" select="/*[local-name()='item']/*[local-name()='properties']"/>
    <xsl:variable name="CONTAINER_PROPERTIESPATH" select="/*[local-name()='container']/*[local-name()='properties']"/>
    <xsl:variable name="COMPONENT_PROPERTIESPATH" select="/*[local-name()='item']/*[local-name()='components']/*[local-name()='component']/*[local-name()='properties']"/>
    <xsl:variable name="CONTENT_MODEL_SPECIFIC_PATH" select="/*[local-name()='item']/*[local-name()='properties']/*[local-name()='content-model-specific']"/>

    
    
    
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
	             <!-- ADDITIONALLY WRITE VALUE IN metadata-index -->
				<xsl:call-template name="writeIndexField">
					<xsl:with-param name="context" select="$CONTEXTNAME"/>
					<xsl:with-param name="fieldname">metadata</xsl:with-param>
					<xsl:with-param name="fieldvalue">
						<xsl:call-template name="removeSubSupStr">
							<xsl:with-param name="name" select="$path"/>
							<xsl:with-param name="str" select="text()"/>
						</xsl:call-template>
					</xsl:with-param>
	<!--				<xsl:with-param name="fieldvalue" select="text()"/>-->
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
                <!--  WRITE XLINK-TITLE-ATTRIBUTES AS SPECIAL FIELD -->
                <xsl:if test="string(.) and normalize-space(.)!=''
                        and string($path) and normalize-space($path)!='' 
                        and namespace-uri()='http://www.w3.org/1999/xlink'
                        and local-name()='title'">
			<xsl:call-template name="writeIndexField">
				<xsl:with-param name="context" select="$context"/>
				<xsl:with-param name="fieldname" select="concat($path,$FIELDSEPARATOR,'xLinkTitle')"/>
				<xsl:with-param name="fieldvalue" select="."/>
				<xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
				<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
			</xsl:call-template> 
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
        <xsl:variable name="visibility" select="$components[$num]/*[local-name()='properties']/*[local-name()='visibility']"/>
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
        <xsl:variable name="isDateOrDecimal" select="element-type-helper:isDateOrDecimal($fieldvalue)"/>
            <IndexField termVector="NO">
                <xsl:attribute name="index">
                	<xsl:choose>
						<xsl:when test="$isDateOrDecimal = true()">
							<xsl:value-of select="string('UN_TOKENIZED')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$indextype"/>
						</xsl:otherwise>
					</xsl:choose>
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
		        
		        <xsl:call-template name="removeSubSupStr">
					<xsl:with-param name="name" select="$fieldname"/>
					<xsl:with-param name="str">
                		<xsl:value-of select="$fieldvalue"/>
                	</xsl:with-param>
				</xsl:call-template>
				
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
	

	<!-- REMOVE SUB AND SUP TAGS -->
	<xsl:template name="removeSubSup">
		<xsl:param name="elem" />
		<xsl:call-template name="removeSubSupStr">
			<xsl:with-param name="name" select="local-name($elem)" />
			<xsl:with-param name="str" select="$elem"/>
		</xsl:call-template>
	</xsl:template>
		
	<!-- REMOVE SUB AND SUP TAGS -->
	<xsl:template name="removeSubSupStr">
		<xsl:param name="name" />
		<xsl:param name="str" />
		<xsl:choose>
	<!--				FIELDS WHERE SUB/SUPs should be removed-->
			<xsl:when test=" contains( concat( ',title,alternative,abstract,', ',md-records/md-record/publication/title,md-records/md-record/publication/alternative,md-records/md-record/publication/abstract,', ',md-records/md-record/publication/source/title,md-records/md-record/publication/source/alternative,md-records/md-record/publication/source/abstract,' ), concat(',', $name, ',') )">
				
				<xsl:call-template name="removeTag">
					<xsl:with-param name="str">
						<xsl:call-template name="removeTag">
							<xsl:with-param name="str" select="$str" />
							<xsl:with-param name="tag" select="'sub'" />
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="tag" select="'sup'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$str" />
			</xsl:otherwise>
		</xsl:choose>
	
	</xsl:template>	
	
	<!-- REMOVE TAG -->
	<xsl:template name="removeTag">
		<xsl:param name="str"/>
		<xsl:param name="tag"/>
		<xsl:choose>
			<xsl:when test="contains($str, concat('&lt;', $tag, '&gt;'))">
				<xsl:call-template name="replace-substring">
					<xsl:with-param name="original">
						<xsl:call-template name="replace-substring">
							<xsl:with-param name="original" select="$str"/>
							<xsl:with-param name="substring" select="concat('&lt;', $tag, '&gt;')"/>
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="substring" select="concat('&lt;/', $tag, '&gt;')"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$str"/>
			</xsl:otherwise>
		</xsl:choose>
	
	</xsl:template>
	
	
	<!-- Substring before delimeter. If no delimter found, return original string -->
	<xsl:template name="substring-before">
		<xsl:param name="str"/>
		<xsl:param name="delimiter"/>
		<xsl:choose>
			<xsl:when test="contains($str, $delimiter)">
				<xsl:value-of select="substring-before($str, $delimiter)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$str"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	<!-- REPLACE STRING -->
	<xsl:template name="replace-substring">
		<xsl:param name="original"/>
		<xsl:param name="substring"/>
		<xsl:param name="replacement" select="''"/>
		<xsl:variable name="first">
			<xsl:choose>
				<xsl:when test="contains($original, $substring)">
					<xsl:value-of select="substring-before($original, $substring)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$original"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="middle">
			<xsl:choose>
				<xsl:when test="contains($original, $substring)">
					<xsl:value-of select="$replacement"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="last">
			<xsl:choose>
				<xsl:when test="contains($original, $substring)">
					<xsl:choose>
						<xsl:when test="contains(substring-after($original, $substring), $substring)">
							<xsl:call-template name="replace-substring">
								<xsl:with-param name="original">
									<xsl:value-of select="substring-after($original, $substring)"/>
								</xsl:with-param>
								<xsl:with-param name="substring">
									<xsl:value-of select="$substring"/>
								</xsl:with-param>
								<xsl:with-param name="replacement">
									<xsl:value-of select="$replacement"/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="substring-after($original, $substring)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="concat($first, $middle, $last)"/>
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
        
        
        
	<!-- USER DEFINED INDEX: /md-records/md-record/publication/creator/person/compound/person-complete-name -->
	<userdefined-index name="md-records/md-record/publication/creator/person/compound/person-complete-name">
		<xsl:attribute name="context">
			<xsl:value-of select="$CONTEXTNAME"/>
		</xsl:attribute>
		<xsl:for-each select="$ITEM_METADATAPATH/*[local-name()='publication']/*[local-name()='creator']/*[local-name()='person']">
			<element index="TOKENIZED">
				<xsl:value-of select="concat(./*[local-name()='family-name'],' ', ./*[local-name()='given-name'])"/>
			</element>
			<element index="TOKENIZED">
				<xsl:value-of select="concat(./*[local-name()='given-name'],' ', ./*[local-name()='family-name'])"/>
			</element>
		</xsl:for-each>
		<xsl:for-each select="$CONTAINER_METADATAPATH/*[local-name()='publication']/*[local-name()='creator']/*[local-name()='person']">
			<element index="TOKENIZED">
				<xsl:value-of select="concat(./*[local-name()='family-name'],' ', ./*[local-name()='given-name'])"/>
			</element>
			<element index="TOKENIZED">
				<xsl:value-of select="concat(./*[local-name()='given-name'],' ', ./*[local-name()='family-name'])"/>
			</element>
		</xsl:for-each>
	</userdefined-index>
	
	
	
	<!-- USER DEFINED INDEX: for every creator, create an index with role in index name  and name or identifier as index value -->
	<!-- e.g. /md-records/md-record/publication/creator/person/compound/role-person/AUT -->
	<!-- e.g. /md-records/md-record/publication/creator/organization/compound/role-organization/EDT -->
	<xsl:for-each select="$ITEM_METADATAPATH/*[local-name()='publication']/*[local-name()='creator']">
		<xsl:if test="./*[local-name()='person']">
		<userdefined-index>
			<xsl:attribute name="name">
				<xsl:value-of select="concat('md-records/md-record/publication/creator/person/compound/role-person/', string-helper:getSubstringAfterLast(@role,'/'))"/>
			</xsl:attribute>
			<xsl:attribute name="context">
				<xsl:value-of select="$CONTEXTNAME"/>
			</xsl:attribute>
			<element index="TOKENIZED">
				<xsl:value-of select="concat(./*[local-name()='person']/*[local-name()='family-name'],' ', ./*[local-name()='person']/*[local-name()='given-name'])"/>
			</element>
			<element index="TOKENIZED">
				 <xsl:value-of select="concat(./*[local-name()='person']/*[local-name()='given-name'],' ', ./*[local-name()='person']/*[local-name()='family-name'])"/>
			</element>
			<element index="TOKENIZED">
				 <xsl:value-of select="./*[local-name()='person']/*[local-name()='identifier']"/>
			</element>
		</userdefined-index>
		</xsl:if>
	
		<xsl:if test="./*[local-name()='organization']">
		<userdefined-index>
			<xsl:attribute name="name">
				<xsl:value-of select="concat('md-records/md-record/publication/creator/organization/compound/role-organization/', string-helper:getSubstringAfterLast(@role,'/'))"/>
			</xsl:attribute>
			<xsl:attribute name="context">
				<xsl:value-of select="$CONTEXTNAME"/>
			</xsl:attribute>
			<element index="TOKENIZED">
				<xsl:value-of select="./*[local-name()='organization']/*[local-name()='title']"/>
			</element>
			<element index="TOKENIZED">
				 <xsl:value-of select="./*[local-name()='organization']/*[local-name()='identifier']"/>
			</element>
		</userdefined-index>
		</xsl:if>
	</xsl:for-each>
	
	
	<!-- USER DEFINED INDEX: /md-records/md-record/publication/creator/compound/organization-path-identifiers -->
	<userdefined-index name="md-records/md-record/publication/creator/compound/organization-path-identifiers">
		<xsl:attribute name="context">
			<xsl:value-of select="$CONTEXTNAME"/>
		</xsl:attribute>
		<xsl:for-each select="$ITEM_METADATAPATH/*[local-name()='publication']/*[local-name()='creator']//*[local-name()='organization']/*[local-name()='identifier']">
			<element index="TOKENIZED">
				<xsl:variable name="objectId" select="normalize-space(.)"/>
				<xsl:if test="string($objectId) and normalize-space($objectId)!=''">
					<xsl:value-of select="escidoc-core-accessor:getObjectAttribute( concat('/oum/organizational-unit/',$objectId,'/resources/path-list'),'/organizational-unit-path-list/organizational-unit-path/organizational-unit-ref','href','http://www.w3.org/1999/xlink','false','true')"/>
				</xsl:if>
			</element>
		</xsl:for-each>
		<xsl:for-each select="$CONTAINER_METADATAPATH/*[local-name()='publication']/*[local-name()='creator']//*[local-name()='organization']/*[local-name()='identifier']">
			<element index="TOKENIZED">
				<xsl:variable name="objectId" select="normalize-space(.)"/>
				<xsl:if test="string($objectId) and normalize-space($objectId)!=''">
					<xsl:value-of select="escidoc-core-accessor:getObjectAttribute( concat('/oum/organizational-unit/',$objectId,'/resources/path-list'),'/organizational-unit-path-list/organizational-unit-path/organizational-unit-ref','href','http://www.w3.org/1999/xlink','false','true')"/>
				</xsl:if>
			</element>
		</xsl:for-each>
	</userdefined-index>

        
	<!-- USER DEFINED INDEX: /md-records/md-record/publication/source/compound/any-title -->
	<userdefined-index name="md-records/md-record/publication/source/compound/any-title">
		<xsl:attribute name="context">
			<xsl:value-of select="$CONTEXTNAME"/>
		</xsl:attribute>
		<xsl:for-each select="$ITEM_METADATAPATH/*[local-name()='publication']//*[local-name()='source']">
			<element index="TOKENIZED">
				<xsl:call-template name="removeSubSup">
					<xsl:with-param name="elem" select="./*[local-name()='title']"/>
				</xsl:call-template>
			</element>
			<element index="TOKENIZED">
				<xsl:call-template name="removeSubSup">
					<xsl:with-param name="elem" select="./*[local-name()='alternative']"/>
				</xsl:call-template>
			</element>
		</xsl:for-each>
		<xsl:for-each select="$CONTAINER_METADATAPATH/*[local-name()='publication']//*[local-name()='source']">
			<element index="TOKENIZED">
				<xsl:call-template name="removeSubSup">
					<xsl:with-param name="elem" select="./*[local-name()='title']"/>
				</xsl:call-template>
			</element>
			<element index="TOKENIZED">
				<xsl:call-template name="removeSubSup">
					<xsl:with-param name="elem" select="./*[local-name()='alternative']"/>
				</xsl:call-template>
			</element>
		</xsl:for-each>
	</userdefined-index>
	
	<userdefined-index name="any-dates">
		<xsl:attribute name="context">
			<xsl:value-of select="$CONTEXTNAME"/>
		</xsl:attribute>
		<xsl:for-each select="$ITEM_METADATAPATH//*">
			<element index="TOKENIZED">
				<xsl:value-of select="./*[local-name()='issued']"/>
			</element>
			<element index="TOKENIZED">
				<xsl:value-of select="./*[local-name()='published-online']"/>
			</element>
			<element index="TOKENIZED">
				<xsl:value-of select="./*[local-name()='dateAccepted']"/>
			</element>
			<element index="TOKENIZED">
				<xsl:value-of select="./*[local-name()='dateSubmitted']"/>
			</element>
			<element index="TOKENIZED">
				<xsl:value-of select="./*[local-name()='modified']"/>
			</element>
			<element index="TOKENIZED">
				<xsl:value-of select="./*[local-name()='created']"/>
			</element>
		</xsl:for-each>
		<xsl:for-each select="$CONTAINER_METADATAPATH//*">
			<element index="TOKENIZED">
				<xsl:value-of select="./*[local-name()='issued']"/>
			</element>
			<element index="TOKENIZED">
				<xsl:value-of select="./*[local-name()='published-online']"/>
			</element>
			<element index="TOKENIZED">
				<xsl:value-of select="./*[local-name()='dateAccepted']"/>
			</element>
			<element index="TOKENIZED">
				<xsl:value-of select="./*[local-name()='dateSubmitted']"/>
			</element>
			<element index="TOKENIZED">
				<xsl:value-of select="./*[local-name()='modified']"/>
			</element>
			<element index="TOKENIZED">
				<xsl:value-of select="./*[local-name()='created']"/>
			</element>
		</xsl:for-each>
	</userdefined-index>
	
	<userdefined-index name="any-dates-year-only">
		<xsl:attribute name="context">
			<xsl:value-of select="$CONTEXTNAME"/>
		</xsl:attribute>
		<xsl:for-each select="$ITEM_METADATAPATH//*">
			<element index="TOKENIZED">
				<xsl:call-template name="substring-before">
					<xsl:with-param name="str" select="./*[local-name()='issued']"/>
					<xsl:with-param name="delimiter" select="'-'"/>
				</xsl:call-template>
			</element>
			<element index="TOKENIZED">
				<xsl:call-template name="substring-before">
					<xsl:with-param name="str" select="./*[local-name()='published-online']"/>
					<xsl:with-param name="delimiter" select="'-'"/>
				</xsl:call-template>
			</element>
			<element index="TOKENIZED">
				<xsl:call-template name="substring-before">
					<xsl:with-param name="str" select="./*[local-name()='dateAccepted']"/>
					<xsl:with-param name="delimiter" select="'-'"/>
				</xsl:call-template>
			</element>
			<element index="TOKENIZED">
				<xsl:call-template name="substring-before">
					<xsl:with-param name="str" select="./*[local-name()='dateSubmitted']"/>
					<xsl:with-param name="delimiter" select="'-'"/>
				</xsl:call-template>
			</element>
			<element index="TOKENIZED">
				<xsl:call-template name="substring-before">
					<xsl:with-param name="str" select="./*[local-name()='modified']"/>
					<xsl:with-param name="delimiter" select="'-'"/>
				</xsl:call-template>
			</element>
			<element index="TOKENIZED">
				<xsl:call-template name="substring-before">
					<xsl:with-param name="str" select="./*[local-name()='created']"/>
					<xsl:with-param name="delimiter" select="'-'"/>
				</xsl:call-template>
			</element>
		</xsl:for-each>
		<xsl:for-each select="$CONTAINER_METADATAPATH//*">
			<element index="TOKENIZED">
				<xsl:call-template name="substring-before">
					<xsl:with-param name="str" select="./*[local-name()='issued']"/>
					<xsl:with-param name="delimiter" select="'-'"/>
				</xsl:call-template>
			</element>
			<element index="TOKENIZED">
				<xsl:call-template name="substring-before">
					<xsl:with-param name="str" select="./*[local-name()='published-online']"/>
					<xsl:with-param name="delimiter" select="'-'"/>
				</xsl:call-template>
			</element>
			<element index="TOKENIZED">
				<xsl:call-template name="substring-before">
					<xsl:with-param name="str" select="./*[local-name()='dateAccepted']"/>
					<xsl:with-param name="delimiter" select="'-'"/>
				</xsl:call-template>
			</element>
			<element index="TOKENIZED">
				<xsl:call-template name="substring-before">
					<xsl:with-param name="str" select="./*[local-name()='dateSubmitted']"/>
					<xsl:with-param name="delimiter" select="'-'"/>
				</xsl:call-template>
			</element>
			<element index="TOKENIZED">
				<xsl:call-template name="substring-before">
					<xsl:with-param name="str" select="./*[local-name()='modified']"/>
					<xsl:with-param name="delimiter" select="'-'"/>
				</xsl:call-template>
			</element>
			<element index="TOKENIZED">
				<xsl:call-template name="substring-before">
					<xsl:with-param name="str" select="./*[local-name()='created']"/>
					<xsl:with-param name="delimiter" select="'-'"/>
				</xsl:call-template>
			</element>
		</xsl:for-each>
	</userdefined-index>
	
	<userdefined-index name="any-identifier">
		<xsl:attribute name="context">
			<xsl:value-of select="$CONTEXTNAME"/>
		</xsl:attribute>
		<element index="TOKENIZED">
			<xsl:value-of select="string-helper:removeVersionIdentifier(string-helper:getSubstringAfterLast(/*[local-name()='item']/@*[local-name()='href'], '/'))"/>
		</element>
		<element index="TOKENIZED">
			<xsl:value-of select="$ITEM_PROPERTIESPATH/*[local-name()='pid']"/>
		</element>
		<element index="TOKENIZED">
			<xsl:value-of select="$ITEM_PROPERTIESPATH/*[local-name()='latest-release']/*[local-name()='pid']"/>
		</element>
		<element index="TOKENIZED">
			<xsl:value-of select="string-helper:removeVersionIdentifier(/*[local-name()='container']/@objid)"/>
		</element>
		<element index="TOKENIZED">
			<xsl:value-of select="$CONTAINER_PROPERTIESPATH/*[local-name()='pid']"/>
		</element>
		<element index="TOKENIZED">
			<xsl:value-of select="$CONTAINER_PROPERTIESPATH/*[local-name()='latest-release']/*[local-name()='pid']"/>
		</element>
		<xsl:for-each select="$COMPONENT_PATH">
			<element index="TOKENIZED">
				<xsl:value-of select="./*[local-name()='properties']/*[local-name()='pid']"/>
			</element>
		</xsl:for-each>
		<xsl:for-each select="$ITEM_METADATAPATH//*[local-name()='identifier']">
			<xsl:variable name="idtype" select="string-helper:getSubstringAfterLast(./@*[local-name()='type'],':')" />
			<xsl:if test="string($idtype) and normalize-space($idtype)!=''">
				<element index="TOKENIZED">
					<xsl:value-of select="concat($idtype,':',.)"/>
				</element>
				<element index="TOKENIZED">
					<xsl:value-of select="concat($idtype,' ',.)"/>
				</element>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="$CONTAINER_METADATAPATH//*[local-name()='identifier']">
			<xsl:variable name="idtype" select="string-helper:getSubstringAfterLast(./@*[local-name()='type'],':')" />
			<xsl:if test="string($idtype) and normalize-space($idtype)!=''">
				<element index="TOKENIZED">
					<xsl:value-of select="concat($idtype,':',.)"/>
				</element>
				<element index="TOKENIZED">
					<xsl:value-of select="concat($idtype,' ',.)"/>
				</element>
			</xsl:if>
		</xsl:for-each>
	</userdefined-index>
	
	
	<userdefined-index name="md-records/md-record/publication/event/compound/any">
		<xsl:attribute name="context">
			<xsl:value-of select="$CONTEXTNAME"/>
		</xsl:attribute>
		<xsl:variable name="fields">
			<xsl:copy-of select="$ITEM_METADATAPATH//*[local-name()='event']/*[local-name()='title']"/>
			<xsl:copy-of select="$ITEM_METADATAPATH//*[local-name()='event']/*[local-name()='alternative']"/>
			<xsl:copy-of select="$ITEM_METADATAPATH//*[local-name()='event']/*[local-name()='place']"/>
			<xsl:copy-of select="$CONTAINER_METADATAPATH//*[local-name()='event']/*[local-name()='title']"/>
			<xsl:copy-of select="$CONTAINER_METADATAPATH//*[local-name()='event']/*[local-name()='alternative']"/>
			<xsl:copy-of select="$CONTAINER_METADATAPATH//*[local-name()='event']/*[local-name()='place']"/>
		</xsl:variable>
		<xsl:for-each select="xalan:nodeset($fields)/*">
			<xsl:variable name="name" select="name()"/>
			<element index="TOKENIZED">
				<xsl:value-of select="."/>
			</element>
		</xsl:for-each>
	</userdefined-index>
	
	<!-- USER DEFINED INDEX: /properties/creation-date/date
	contains pure date without time -->
	<userdefined-index name="properties/creation-date/date">
		<xsl:attribute name="context">
			<xsl:value-of select="$CONTEXTNAME"/>
		</xsl:attribute>
		<element index="UN_TOKENIZED">
			<xsl:value-of select="substring-before($ITEM_PROPERTIESPATH//*[local-name()='creation-date'], 'T')"/>
		</element>
	</userdefined-index>

	<!-- USER DEFINED INDEX: /last-modification-date/date
	contains pure date without time -->
	<userdefined-index name="last-modification-date/date">
		<xsl:attribute name="context">
			<xsl:value-of select="$CONTEXTNAME"/>
		</xsl:attribute>
		<element index="UN_TOKENIZED">
			<xsl:value-of select="substring-before(/*[local-name()='item']/@last-modification-date, 'T')"/>
		</element>
	</userdefined-index>
        
	<userdefined-index name="metadata">
		<xsl:attribute name="context">
			<xsl:value-of select="$CONTEXTNAME"/>
		</xsl:attribute>
		<xsl:for-each select="$ITEM_METADATAPATH//*[local-name()='identifier']">
			<xsl:variable name="idtype" select="string-helper:getSubstringAfterLast(./@*[local-name()='type'],':')" />
			<xsl:if test="string($idtype) and normalize-space($idtype)!=''">
				<element index="TOKENIZED">
					<xsl:value-of select="concat($idtype,':',.)"/>
				</element>
				<element index="TOKENIZED">
					<xsl:value-of select="concat($idtype,' ',.)"/>
				</element>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="$CONTAINER_METADATAPATH//*[local-name()='identifier']">
			<xsl:variable name="idtype" select="string-helper:getSubstringAfterLast(./@*[local-name()='type'],':')" />
			<xsl:if test="string($idtype) and normalize-space($idtype)!=''">
				<element index="TOKENIZED">
					<xsl:value-of select="concat($idtype,':',.)"/>
				</element>
				<element index="TOKENIZED">
					<xsl:value-of select="concat($idtype,' ',.)"/>
				</element>
			</xsl:if>
		</xsl:for-each>
		<element index="TOKENIZED">
			<xsl:value-of select="string-helper:removeVersionIdentifier(string-helper:getSubstringAfterLast(/*[local-name()='item']/@*[local-name()='href'], '/'))"/>
		</element>
		<element index="TOKENIZED">
			<xsl:value-of select="string-helper:removeVersionIdentifier(string-helper:getSubstringAfterLast(/*[local-name()='container']/@*[local-name()='href'], '/'))"/>
		</element>
	</userdefined-index>
	
	
	<!-- Publication status drawn from dates -->
	<userdefined-index name="publication-status">
		<xsl:attribute name="context">
			<xsl:value-of select="$CONTEXTNAME"/>
		</xsl:attribute>
		<element index="UN_TOKENIZED">
			<xsl:choose>
				<xsl:when test="$ITEM_METADATAPATH//*[local-name()='issued'] and $ITEM_METADATAPATH//*[local-name()='issued'] != ''">
					<xsl:value-of select="'published-in-print'"/>
				</xsl:when>
				<xsl:when test="$ITEM_METADATAPATH//*[local-name()='published-online'] and $ITEM_METADATAPATH//*[local-name()='published-online'] != ''">
					<xsl:value-of select="'published-online'"/>
				</xsl:when>
				<xsl:when test="$ITEM_METADATAPATH//*[local-name()='dateAccepted'] and $ITEM_METADATAPATH//*[local-name()='dateAccepted'] != ''">
					<xsl:value-of select="'accepted'"/>
				</xsl:when>
				<xsl:when test="$ITEM_METADATAPATH//*[local-name()='dateSubmitted'] and $ITEM_METADATAPATH//*[local-name()='dateSubmitted'] != ''">
					<xsl:value-of select="'submitted'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'not-specified'"/>
				</xsl:otherwise>
			</xsl:choose>
		</element>
	</userdefined-index>
	
	<userdefined-index name="any-organization-pids">
		<xsl:attribute name="context">
			<xsl:value-of select="$CONTEXTNAME"/>
		</xsl:attribute>
		<xsl:for-each select="$ITEM_METADATAPATH//*[local-name()='creator']//*[local-name()='organization']/*[local-name()='identifier']">
			<element index="TOKENIZED">
				<xsl:variable name="objectId" select="normalize-space(.)"/>
				<xsl:if test="string($objectId) and normalize-space($objectId)!=''">
					<xsl:value-of select="escidoc-core-accessor:getObjectAttribute( concat('/oum/organizational-unit/',$objectId,'/resources/path-list'),'/organizational-unit-path-list/organizational-unit-path/organizational-unit-ref','href','http://www.w3.org/1999/xlink','false','true')"/>
				</xsl:if>
			</element>
		</xsl:for-each>
		<xsl:for-each select="$CONTAINER_METADATAPATH//*[local-name()='creator']//*[local-name()='organization']/*[local-name()='identifier']">
			<element index="TOKENIZED">
				<xsl:variable name="objectId" select="normalize-space(.)"/>
				<xsl:if test="string($objectId) and normalize-space($objectId)!=''">
					<xsl:value-of select="escidoc-core-accessor:getObjectAttribute( concat('/oum/organizational-unit/',$objectId,'/resources/path-list'),'/organizational-unit-path-list/organizational-unit-path/organizational-unit-ref','href','http://www.w3.org/1999/xlink','false','true')"/>
				</xsl:if>
			</element>
		</xsl:for-each>
	</userdefined-index>
	
	<userdefined-index name="genre-without-uri">
		<xsl:attribute name="context">
			<xsl:value-of select="$CONTEXTNAME"/>
		</xsl:attribute>
		<element index="TOKENIZED">
			<xsl:value-of select="string-helper:getSubstringAfterLast($ITEM_METADATAPATH/*[local-name()='publication']/@type,'/')"/>
		</element>
	</userdefined-index>
	
	
	<!-- Compound index for project information -->
	<userdefined-index name="md-records/md-record/publication/compound/project-info">
		<xsl:attribute name="context">
			<xsl:value-of select="$CONTEXTNAME"/>
		</xsl:attribute>
		<element index="TOKENIZED">
			<xsl:value-of select="$ITEM_METADATAPATH/*[local-name()='publication']/*[local-name()='project-info']"/>
		</element>
	</userdefined-index>
	
	
    <!-- Create index field escidoc.internal-file.visibility to avoid finding locators when searching for public files -->
     <xsl:for-each select="/*[local-name()='item']/*[local-name()='components']/*[local-name()='component' and *[local-name()='content']/@storage='internal-managed']">
	     <xsl:variable name="storage" select="*[local-name()='content']/@storage"/>
	     <userdefined-index name="components/component/internal-managed/visibility">
	             <xsl:attribute name="context">
	                     <xsl:value-of select="$CONTEXTNAME"/>
	             </xsl:attribute>
	             <element index="TOKENIZED">
	                     <xsl:value-of select="*[local-name()='properties']/*[local-name()='visibility']"/>
	             </element>
	     </userdefined-index>
     </xsl:for-each>
	
       
    </xsl:variable>

</xsl:stylesheet>   
