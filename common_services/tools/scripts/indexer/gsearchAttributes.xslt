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
(Fields are written unstored and untokenized)

-PID (objid without version-identifier but eventually with information if latest-version (LV) or latest-release(LR))

if information about LV or LR is written, additionally write:
(This is used to filter duplicates by HitCollector)
-rootPid (objid without version-identifier)
-type (1 for LR and 0 for LV)

 -->
<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xalan="http://xml.apache.org/xalan"
        xmlns:xsltxsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:string-helper="xalan://de.escidoc.sb.gsearch.xslt.StringHelper"
        extension-element-prefixes="string-helper">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <!-- Parameters that get passed while calling this stylesheet-transformation -->
    <xsl:param name="PID_VERSION_IDENTIFIER" select="':'"/>

    <xsl:template name="processGsearchAttributes">
        <!-- WRITE customized PID in IndexDocument and as IndexField. Important for fedoragsearch to reidentify object  -->
        <xsl:variable name="PID" select="string-helper:removeVersionIdentifier(string-helper:getSubstringAfterLast(/*/@*[local-name()='href'], '/'), $PID_VERSION_IDENTIFIER)"/>
        <xsl:attribute name="PID">
            <xsl:value-of select="$PID"/>
        </xsl:attribute>
        <IndexField IFname="PID" index="UN_TOKENIZED" store="NO" termVector="NO">
            <xsl:value-of select="$PID"/>
        </IndexField>
        <xsl:if test="string($PID_VERSION_IDENTIFIER) and normalize-space($PID_VERSION_IDENTIFIER)!=''">
            <IndexField IFname="distinction.rootPid" index="UN_TOKENIZED" store="NO" termVector="NO">
                <xsl:value-of select="string-helper:removeVersionIdentifier(string-helper:getSubstringAfterLast(/*/@*[local-name()='href'], '/'))"/>
            </IndexField>
            <IndexField IFname="distinction.type" index="UN_TOKENIZED" store="NO" termVector="NO">
                <xsl:choose>
                    <xsl:when test="$PID_VERSION_IDENTIFIER='LR'">
                        <xsl:value-of select="'1'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'0'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </IndexField>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>   
