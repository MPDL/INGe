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
Following index fields are written by this stylesheet:
(All fields are written unstored!)

all resources:
    -permissions-filter.objecttype
    -permissions-filter.PID
item:
    -permissions-filter.parent
    -permissions-filter.content-model-id
    -permissions-filter.context-id
    -permissions-filter.context.organizational-unit-id
    -permissions-filter.created-by
    -permissions-filter.latest-release.number
    -permissions-filter.latest-release.pid
    -permissions-filter.latest-version.number
    -permissions-filter.version.status
    -permissions-filter.version.modified-by
    -permissions-filter.public-status
    -permissions-filter.lock-status
    -permissions-filter.lock-owner
    -permissions-filter.component-id
    -permissions-filter.component.content-category
    -permissions-filter.component.valid-status
    -permissions-filter.component.visibility
    -permissions-filter.component.created-by
    -permissions-filter.content-relation
    
container:
    -permissions-filter.parent
    -permissions-filter.content-model-id
    -permissions-filter.context-id
    -permissions-filter.context.organizational-unit-id
    -permissions-filter.created-by
    -permissions-filter.latest-release.number
    -permissions-filter.latest-release.pid
    -permissions-filter.latest-version.number
    -permissions-filter.version.status
    -permissions-filter.version.modified-by
    -permissions-filter.public-status
    -permissions-filter.lock-status
    -permissions-filter.lock-owner
    -permissions-filter.member
    -permissions-filter.content-relation

context:
    -permissions-filter.created-by
    -permissions-filter.public-status
    -permissions-filter.organizational-unit-id
    
content-model:
    -permissions-filter.created-by
    -permissions-filter.version.status
    -permissions-filter.version.modified-by
    -permissions-filter.latest-version.number

content-relation:
    -permissions-filter.created-by
    -permissions-filter.public-status

organizational-unit:
    -permissions-filter.created-by
    -permissions-filter.public-status
    -permissions-filter.parent
 -->
<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xalan="http://xml.apache.org/xalan"
        xmlns:xsltxsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:lastdate-helper="java:de.escidoc.sb.gsearch.xslt.LastdateHelper"
        xmlns:string-helper="java:de.escidoc.sb.gsearch.xslt.StringHelper"
        xmlns:sortfield-helper="java:de.escidoc.sb.gsearch.xslt.SortFieldHelper"
        xmlns:escidoc-core-accessor="java:de.mpg.escidoc.tools.util.xslt.TriplestoreHelper" 
        extension-element-prefixes="lastdate-helper string-helper sortfield-helper escidoc-core-accessor">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <!-- Parameters that get passed while calling this stylesheet-transformation -->
    <!--  xsl:param name="PID_VERSION_IDENTIFIER"/-->

    <xsl:variable name="PERMISSIONS_CONTEXTNAME">permissions-filter</xsl:variable>

    <!-- Paths to Properties -->
    <xsl:variable name="PERMISSIONS_PROPERTIESPATH" select="/*/*[local-name()='properties']"/>

    <!-- Other Paths -->
    <xsl:variable name="PERMISSIONS_OU_PARENTSPATH" select="/*[local-name()='organizational-unit']/*[local-name()='parents']"/>
    <xsl:variable name="PERMISSIONS_COMPONENTPATH" select="/*[local-name()='item']/*[local-name()='components']/*[local-name()='component']"/>
    <xsl:variable name="PERMISSIONS_CONTENTRELATIONPATH" select="/*/*[local-name()='relations']/*[local-name()='relation']"/>

    <xsl:template name="processPermissionFilters">
        <xsl:variable name="type">
            <xsl:for-each select="*">
                <xsl:if test="position() = 1">
                    <xsl:value-of select="local-name()"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
        <xsl:call-template name="writePermissionFiltersIndexField">
            <xsl:with-param name="context" select="$PERMISSIONS_CONTEXTNAME"/>
            <xsl:with-param name="fieldname">objecttype</xsl:with-param>
            <xsl:with-param name="fieldvalue" select="$type"/>
            <xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="writePermissionFiltersIndexField">
            <xsl:with-param name="context" select="$PERMISSIONS_CONTEXTNAME"/>
            <xsl:with-param name="fieldname">PID</xsl:with-param>
            <xsl:with-param name="fieldvalue" select="string-helper:removeVersionIdentifier(string-helper:getSubstringAfterLast(/*/@*[local-name()='href'], '/'))"/>
            <xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
        </xsl:call-template>
        <xsl:choose>
            <xsl:when test="$type='item'">
                <xsl:call-template name="writeUserdefinedPermissionFiltersIndexes">
                    <xsl:with-param name="fieldvariable" select="$item-permission-filter-indexes"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$type='container'">
                <xsl:call-template name="writeUserdefinedPermissionFiltersIndexes">
                    <xsl:with-param name="fieldvariable" select="$container-permission-filter-indexes"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$type='context'">
                <xsl:call-template name="writeUserdefinedPermissionFiltersIndexes">
                    <xsl:with-param name="fieldvariable" select="$context-permission-filter-indexes"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$type='organizational-unit'">
                <xsl:call-template name="writeUserdefinedPermissionFiltersIndexes">
                    <xsl:with-param name="fieldvariable" select="$organizational-unit-permission-filter-indexes"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$type='content-model'">
                <xsl:call-template name="writeUserdefinedPermissionFiltersIndexes">
                    <xsl:with-param name="fieldvariable" select="$content-model-permission-filter-indexes"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$type='content-relation'">
                <xsl:call-template name="writeUserdefinedPermissionFiltersIndexes">
                    <xsl:with-param name="fieldvariable" select="$content-relation-permission-filter-indexes"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!--  WRITE INDEXFIELD -->
    <xsl:template name="writePermissionFiltersIndexField">
        <xsl:param name="context"/>
        <xsl:param name="fieldname"/>
        <xsl:param name="fieldvalue"/>
        <xsl:param name="indextype"/>
        <xsl:if test="string($fieldvalue) and normalize-space($fieldvalue)!=''">
            <IndexField termVector="NO" store="NO">
                <xsl:attribute name="index">
                	<xsl:value-of select="$indextype"/>
                </xsl:attribute>
                <xsl:attribute name="IFname">
                    <xsl:value-of select="concat($context,'.',$fieldname)"/>
                </xsl:attribute>
                <xsl:value-of select="$fieldvalue"/>
            </IndexField>
        </xsl:if>
    </xsl:template>

    <!-- WRITE USERDEFINED INDEX -->
    <xsl:template name="writeUserdefinedPermissionFiltersIndexes">
        <xsl:param name="fieldvariable"/>
        <xsl:for-each select="xalan:nodeset($fieldvariable)/userdefined-index">
            <xsl:variable name="index-name" select="./@name"/>
            <xsl:variable name="context" select="./@context"/>
            <xsl:for-each select="./element">
                <xsl:if test="string(.) and normalize-space(.)!=''">
                    <xsl:call-template name="writePermissionFiltersIndexField">
                        <xsl:with-param name="context" select="$context"/>
                        <xsl:with-param name="fieldname" select="$index-name"/>
                        <xsl:with-param name="fieldvalue" select="."/>
                        <xsl:with-param name="indextype" select="./@index"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>
        
    <!-- Permission Fields for Items -->
    <xsl:variable name="item-permission-filter-indexes">
    <!--
        <userdefined-index name="parent">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="TOKENIZED">
                <xsl:variable name="objectId" select="string-helper:getSubstringAfterLast(/*/@*[local-name()='href'], '/')"/>
                <xsl:value-of select="escidoc-core-accessor:getObjectAttribute(
                    concat('/ir/item/',$objectId, '/resources/parents'),'/parents/parent','href','http://www.w3.org/1999/xlink','false','true')"/>
            </element>
        </userdefined-index>
        -->
        <userdefined-index name="content-model-id">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='content-model']/@*[local-name()='href'], '/')"/>
            </element>
        </userdefined-index>
        <userdefined-index name="context-id">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='context']/@*[local-name()='href'], '/')"/>
            </element>
        </userdefined-index>
        
        <userdefined-index name="context.organizational-unit-id">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="TOKENIZED">
                <xsl:variable name="contextId" select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='context']/@*[local-name()='href'], '/')"/>
                <xsl:value-of select="escidoc-core-accessor:getObjectAttribute(
                    concat('/ir/context/',$contextId),'/context/properties/organizational-units/organizational-unit','href','http://www.w3.org/1999/xlink','false','true')"/>
            </element>
        </userdefined-index>
       
        <userdefined-index name="created-by">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='created-by']/@*[local-name()='href'], '/')"/>
            </element>
        </userdefined-index>
        <userdefined-index name="latest-release.number">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='latest-release']/*[local-name()='number']"/>
            </element>
        </userdefined-index>
        <userdefined-index name="latest-release.pid">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='latest-release']/*[local-name()='pid']"/>
            </element>
        </userdefined-index>
        <userdefined-index name="latest-version.number">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='latest-version']/*[local-name()='number']"/>
            </element>
        </userdefined-index>
        <userdefined-index name="version.status">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='version']/*[local-name()='status']"/>
            </element>
        </userdefined-index>
        <userdefined-index name="version.modified-by">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='version']/*[local-name()='modified-by']/@*[local-name()='href'], '/')"/>
            </element>
        </userdefined-index>
        <userdefined-index name="public-status">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='public-status']"/>
            </element>
        </userdefined-index>
        <userdefined-index name="lock-status">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='lock-status']"/>
            </element>
        </userdefined-index>
        <userdefined-index name="lock-owner">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='lock-owner']/@*[local-name()='href'], '/')"/>
            </element>
        </userdefined-index>
        <userdefined-index name="component-id">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <xsl:for-each select="$PERMISSIONS_COMPONENTPATH">
                <element index="UN_TOKENIZED">
                    <xsl:value-of select="string-helper:getSubstringAfterLast(./@*[local-name()='href'], '/')"/>
                </element>
            </xsl:for-each>
        </userdefined-index>
        <userdefined-index name="component.content-category">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <xsl:for-each select="$PERMISSIONS_COMPONENTPATH">
                <element index="UN_TOKENIZED">
                    <xsl:value-of select="./*[local-name()='properties']/*[local-name()='content-category']"/>
                </element>
            </xsl:for-each>
        </userdefined-index>
        <userdefined-index name="component.valid-status">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <xsl:for-each select="$PERMISSIONS_COMPONENTPATH">
                <element index="UN_TOKENIZED">
                    <xsl:value-of select="./*[local-name()='properties']/*[local-name()='valid-status']"/>
                </element>
            </xsl:for-each>
        </userdefined-index>
        <userdefined-index name="component.visibility">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <xsl:for-each select="$PERMISSIONS_COMPONENTPATH">
                <element index="UN_TOKENIZED">
                    <xsl:value-of select="./*[local-name()='properties']/*[local-name()='visibility']"/>
                </element>
            </xsl:for-each>
        </userdefined-index>
        <userdefined-index name="component.created-by">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <xsl:for-each select="$PERMISSIONS_COMPONENTPATH">
                <element index="UN_TOKENIZED">
                    <xsl:value-of select="string-helper:getSubstringAfterLast(./*[local-name()='properties']/*[local-name()='created-by']/@*[local-name()='href'], '/')"/>
                </element>
            </xsl:for-each>
        </userdefined-index>
        <userdefined-index name="content-relation">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <xsl:for-each select="$PERMISSIONS_CONTENTRELATIONPATH">
                <element index="UN_TOKENIZED">
                    <xsl:value-of select="concat(./@predicate, '|', string-helper:getSubstringAfterLast(./@*[local-name()='href'], '/'))"/>
                </element>
                <element index="TOKENIZED">
                    <xsl:value-of select="concat(./@predicate, ' ', string-helper:getSubstringAfterLast(./@*[local-name()='href'], '/'))"/>
                </element>
            </xsl:for-each>
        </userdefined-index>
    </xsl:variable>
        
    <!-- Permission Fields for Containers -->
    <xsl:variable name="container-permission-filter-indexes">
    <!--  
        <userdefined-index name="parent">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="TOKENIZED">
                <xsl:variable name="objectId" select="string-helper:getSubstringAfterLast(/*/@*[local-name()='href'], '/')"/>
                <xsl:value-of select="escidoc-core-accessor:getObjectAttribute(
                    concat('/ir/container/',$objectId, '/resources/parents'),'/parents/parent','href','http://www.w3.org/1999/xlink','false','true')"/>
            </element>
        </userdefined-index>
        -->
        <userdefined-index name="content-model-id">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='content-model']/@*[local-name()='href'], '/')"/>
            </element>
        </userdefined-index>
        <userdefined-index name="context-id">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='context']/@*[local-name()='href'], '/')"/>
            </element>
        </userdefined-index>
        
        <!-- 
        <userdefined-index name="context.organizational-unit-id">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="TOKENIZED">
                <xsl:variable name="contextId" select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='context']/@*[local-name()='href'], '/')"/>
                <xsl:value-of select="escidoc-core-accessor:getObjectAttribute(
                    concat('/ir/context/',$contextId),'/context/properties/organizational-units/organizational-unit','href','http://www.w3.org/1999/xlink','false','true')"/>
            </element>
        </userdefined-index>
         -->
        <userdefined-index name="created-by">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='created-by']/@*[local-name()='href'], '/')"/>
            </element>
        </userdefined-index>
        <userdefined-index name="latest-release.number">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='latest-release']/*[local-name()='number']"/>
            </element>
        </userdefined-index>
        <userdefined-index name="latest-release.pid">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='latest-release']/*[local-name()='pid']"/>
            </element>
        </userdefined-index>
        <userdefined-index name="latest-version.number">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='latest-version']/*[local-name()='number']"/>
            </element>
        </userdefined-index>
        <userdefined-index name="version.status">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='version']/*[local-name()='status']"/>
            </element>
        </userdefined-index>
        <userdefined-index name="version.modified-by">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='version']/*[local-name()='modified-by']/@*[local-name()='href'], '/')"/>
            </element>
        </userdefined-index>
        <userdefined-index name="public-status">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='public-status']"/>
            </element>
        </userdefined-index>
        <userdefined-index name="lock-status">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='lock-status']"/>
            </element>
        </userdefined-index>
        <userdefined-index name="lock-owner">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='lock-owner']/@*[local-name()='href'], '/')"/>
            </element>
        </userdefined-index>
        <userdefined-index name="member">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <xsl:for-each select="/*[local-name()='container']/*[local-name()='struct-map']/*/@*[local-name()='href']">
                <element index="UN_TOKENIZED">
                    <xsl:value-of select="string-helper:getSubstringAfterLast(., '/')"/>
                </element>
            </xsl:for-each>
        </userdefined-index>
        <userdefined-index name="content-relation">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <xsl:for-each select="$PERMISSIONS_CONTENTRELATIONPATH">
                <element index="UN_TOKENIZED">
                    <xsl:value-of select="concat(./@predicate, '|', string-helper:getSubstringAfterLast(./@*[local-name()='href'], '/'))"/>
                </element>
                <element index="TOKENIZED">
                    <xsl:value-of select="concat(./@predicate, ' ', string-helper:getSubstringAfterLast(./@*[local-name()='href'], '/'))"/>
                </element>
            </xsl:for-each>
        </userdefined-index>
    </xsl:variable>
        
    <!-- Permission Fields for Contexts -->
    <xsl:variable name="context-permission-filter-indexes">
        <userdefined-index name="created-by">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='created-by']/@*[local-name()='href'], '/')"/>
            </element>
        </userdefined-index>
        <userdefined-index name="public-status">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='public-status']"/>
            </element>
        </userdefined-index>
        <userdefined-index name="organizational-unit-id">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <xsl:for-each select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='organizational-units']/*[local-name()='organizational-unit']">
                <element index="UN_TOKENIZED">
                    <xsl:value-of select="string-helper:getSubstringAfterLast(./@*[local-name()='href'], '/')"/>
                </element>
            </xsl:for-each>
        </userdefined-index>
    </xsl:variable>
        
    <!-- Permission Fields for Content Models -->
    <xsl:variable name="content-model-permission-filter-indexes">
        <userdefined-index name="created-by">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='created-by']/@*[local-name()='href'], '/')"/>
            </element>
        </userdefined-index>
        <userdefined-index name="version.status">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='version']/*[local-name()='status']"/>
            </element>
        </userdefined-index>
        <userdefined-index name="version.modified-by">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='version']/*[local-name()='modified-by']/@*[local-name()='href'], '/')"/>
            </element>
        </userdefined-index>
        <userdefined-index name="latest-version.number">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='latest-version']/*[local-name()='number']"/>
            </element>
        </userdefined-index>
    </xsl:variable>
        
    <!-- Permission Fields for Content Relations -->
    <xsl:variable name="content-relation-permission-filter-indexes">
        <userdefined-index name="created-by">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='created-by']/@*[local-name()='href'], '/')"/>
            </element>
        </userdefined-index>
        <userdefined-index name="public-status">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='public-status']"/>
            </element>
        </userdefined-index>
    </xsl:variable>
        
    <!-- Permission Fields for Organizational Units -->
    <xsl:variable name="organizational-unit-permission-filter-indexes">
        <userdefined-index name="created-by">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="string-helper:getSubstringAfterLast($PERMISSIONS_PROPERTIESPATH/*[local-name()='created-by']/@*[local-name()='href'], '/')"/>
            </element>
        </userdefined-index>
        <userdefined-index name="public-status">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <element index="UN_TOKENIZED">
                <xsl:value-of select="$PERMISSIONS_PROPERTIESPATH/*[local-name()='public-status']"/>
            </element>
        </userdefined-index>
        <userdefined-index name="parent">
            <xsl:attribute name="context">
                <xsl:value-of select="$PERMISSIONS_CONTEXTNAME"/>
            </xsl:attribute>
            <xsl:for-each select="$PERMISSIONS_OU_PARENTSPATH/*[local-name()='parent']">
                <element index="UN_TOKENIZED">
                    <xsl:value-of select="string-helper:getSubstringAfterLast(./@*[local-name()='href'], '/')"/>
                </element>
            </xsl:for-each>
        </userdefined-index>
    </xsl:variable>
        
</xsl:stylesheet>   
