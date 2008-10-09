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


 Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml"/>

	<?xml-stylesheet type="text/xsl" href="../xsl/explain.xsl"?>

	<xsl:template match="/">
		<xsl:processing-instruction name="xml-stylesheet">
			type="text/xsl" href="../xsl/explain.xsl"
		</xsl:processing-instruction>
		<explain>
			<name>
				CoNE
			</name>
			<interface>
				JQuery
			</interface>
			<description>
				Control of Named Entities
			</description>
			<services>
				<xsl:for-each select="/models/model">
					<service>
						<name><xsl:value-of select="description"/></name>
						<link><xsl:value-of select="name"/></link>
						<methods>
							<method>
								<name>query</name>
								<parameters>
									<parameter required="true">
										<name>q</name>
										<type>String</type>
										<value>A string holding the search query.</value>
									</parameter>
									<parameter required="false">
										<name>lang</name>
										<type>String</type>
										<value>A string holding language a) in which should be searched and b) in which the results should be given back.</value>
									</parameter>
								</parameters>
								<return>
									<type>String</type>
									<value>A "|" and newline - separated list of hits matching the given query.</value>
								</return>
							</method>
							<method>
								<name>details</name>
								<parameters>
									<parameter required="true">
										<name>id</name>
										<type>URI</type>
										<value>The identifier if the requested resource.</value>
									</parameter>
								</parameters>
								<return>
									<type>JSON</type>
									<value>A JSON object containing the details of the requested resource</value>
								</return>
							</method>
						</methods>
						<samples>
							<xsl:variable name="name" select="name"/>
							<xsl:for-each select="samples/sample">
								<sample description="{description}">
									<xsl:value-of select="$name"/>/<xsl:value-of select="method/name"/>?<xsl:for-each select="method/parameters/parameter">
										<xsl:if test="position() != 1">&amp;</xsl:if>
										<xsl:value-of select="@name"/>=<xsl:value-of select="."/>
									</xsl:for-each>
								</sample>
							</xsl:for-each>
							<sample>
									<xsl:value-of select="example_page"/>
							</sample>
						</samples>
					</service>
				</xsl:for-each>
			</services>
		</explain>
	</xsl:template>
</xsl:stylesheet>