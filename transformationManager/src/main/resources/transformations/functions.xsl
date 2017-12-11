<?xml version="1.0" encoding="UTF-8"?>
<!--
 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.org/license.
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
<xsl:stylesheet xml:base="stylesheet" version="2.0"
	xmlns:function="urn:pubman:functions"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:function name="function:substring-before-last">
		<xsl:param name="input" as="xs:string"/>
		<xsl:param name="substr" as="xs:string"/>
		
		<xsl:sequence select="
			if ($substr) then 
				if (contains($input, $substr)) then 
					string-join (tokenize ($input, $substr) [position() ne last()], $substr)
				else ''
			else $input"/>
	</xsl:function>
	
	<xsl:function name="function:substring-after-last">
		<xsl:param name="input" as="xs:string"/>
		<xsl:param name="substr" as="xs:string"/>
		
		<xsl:sequence select="
			if ($substr) then 
				if (contains($input, $substr)) then 
					tokenize ($input, $substr) [last()]
				else ''
			else $input"/>
	</xsl:function>

</xsl:stylesheet>