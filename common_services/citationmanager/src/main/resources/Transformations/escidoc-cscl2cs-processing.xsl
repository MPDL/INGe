<?xml version="1.0" encoding="UTF-8"?>
	<!--
		CDDL HEADER START The contents of this file are subject to the terms
		of the Common Development and Distribution License, Version 1.0 only
		(the "License"). You may not use this file except in compliance with
		the License. You can obtain a copy of the license at
		license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the
		License for the specific language governing permissions and
		limitations under the License. When distributing Covered Code, include
		this CDDL HEADER in each file and include the License file at
		license/ESCIDOC.LICENSE. If applicable, add the following below this
		CDDL HEADER, with the fields enclosed by brackets "[]" replaced with
		your own identifying information: Portions Copyright [yyyy] [name of
		copyright owner] CDDL HEADER END Copyright 2006-2010
		Fachinformationszentrum Karlsruhe Gesellschaft für
		wissenschaftlich-technische Information mbH and Max-Planck-
		Gesellschaft zur Förderung der Wissenschaft e.V. All rights reserved.
		Use is subject to license terms.
	-->
	<!--
		Transformations from eSciDoc Citation Style Configuration Language 
		to the Citation Style Execution XSLT 
		See http://colab.mpdl.mpg.de/mediawiki/CitationStyleConfigurationConcept
		and https://zim02.gwdg.de/repos/common/trunk/common_services/citationmanager/src/main/resources/Schemas/citation-style.xsd
		Author: Vlad Makarenko (initial creation) $Author: $ (last changed)
		$Revision: $ $LastChangedDate: $
	-->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fn="http://www.w3.org/2005/xpath-functions" 
	xmlns:cit="http://www.escidoc.de/citationstyle"  

	xmlns:jfunc="java:de.mpg.escidoc.services.citationmanager.utils.XsltHelper"
	xmlns:func="http://www.escidoc.de/citationstyle/functions"	
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
	
	xmlns:ei="${xsd.soap.item.item}"
	xmlns:mdr="${xsd.soap.common.mdrecords}"
    xmlns:mdp="${xsd.metadata.escidocprofile}"
    xmlns:pub="${xsd.metadata.publication}"
    xmlns:e="${xsd.metadata.escidocprofile.types}"
	xmlns:dc="${xsd.metadata.dc}"    
    xmlns:dcterms="${xsd.metadata.dcterms}"
	
	xsi:schemaLocation="http://www.escidoc.de/citationstyle ../../Schemas/citation-style.xsd"
	>
<!--	xmlns:snippet="http://www.escidoc.de/citationstyle/snippet"-->	
	<xsl:output method="xml" encoding="UTF-8" indent="no"
		cdata-section-elements="" />
	
	<!-- Default delimiter for undefined delimiter and internal-delimiter	-->
	<xsl:param name="default-delimiter" select="' '"/>
	
	<xsl:template match="/*">
		<xsl:element name="xsl:stylesheet"  
			>
			<xsl:namespace name="xsl">http://www.w3.org/1999/XSL/Transform</xsl:namespace>
			<xsl:namespace name="xlink">http://www.w3.org/1999/xlink</xsl:namespace>
			<xsl:namespace name="cit">http://www.escidoc.de/citationstyle</xsl:namespace>
			<xsl:namespace name="fn">http://www.w3.org/2005/xpath-functions</xsl:namespace>
			<xsl:namespace name="jfunc">java:de.mpg.escidoc.services.citationmanager.utils.XsltHelper</xsl:namespace>
			<xsl:namespace name="func">http://www.escidoc.de/citationstyle/functions</xsl:namespace>
			<xsl:namespace name="functx">http://www.functx.com</xsl:namespace>
			
			<xsl:namespace name="ei">${xsd.soap.item.item}</xsl:namespace>
			<xsl:namespace name="mdr">${xsd.soap.common.mdrecords}</xsl:namespace>
			<xsl:namespace name="mdp">${xsd.metadata.escidocprofile}</xsl:namespace>
			<xsl:namespace name="pub">${xsd.metadata.publication}</xsl:namespace>
			<xsl:namespace name="e">${xsd.metadata.escidocprofile.types}</xsl:namespace>
			<xsl:namespace name="prop">${xsd.soap.common.prop}</xsl:namespace>
			<xsl:namespace name="escidocComponents">${xsd.soap.item.components}</xsl:namespace>
			<xsl:namespace name="source">${xsd.metadata.source}</xsl:namespace>
			<xsl:namespace name="eterms">${xsd.metadata.escidocprofile.types}</xsl:namespace>
			<xsl:namespace name="event">${xsd.metadata.event}</xsl:namespace>
			<xsl:namespace name="organization">${xsd.metadata.organization}</xsl:namespace>
			<xsl:namespace name="person">${xsd.metadata.person}</xsl:namespace>
			<xsl:namespace name="legalCase">http://purl.org/escidoc/metadata/profiles/0.1/legal-case</xsl:namespace>
			
			<xsl:namespace name="dc">${xsd.metadata.dc}</xsl:namespace>
			<xsl:namespace name="dcterms">${xsd.metadata.dcterms}</xsl:namespace>
			
			<xsl:namespace name="xsi">http://www.w3.org/2001/XMLSchema-instance</xsl:namespace>
			<xsl:namespace name="xs">http://www.w3.org/2001/XMLSchema</xsl:namespace>
			
			
			<xsl:attribute name="version">2.0</xsl:attribute>
			
			<xsl:element name="xsl:output" >
				<xsl:attribute name="method">xml</xsl:attribute>
				<xsl:attribute name="encoding">UTF-8</xsl:attribute>
				<xsl:attribute name="indent">yes</xsl:attribute>
 				<xsl:attribute name="cdata-section-elements" select="concat(@citation-placeholder-tag, ' dcterms:abstract')"/>
 				 
			</xsl:element> 
			
			<xsl:element name="xsl:param" >
				<xsl:attribute name="name" select="'pubman_instance'"/>
			</xsl:element>
			
			<xsl:variable name="variables" >
				<xsl:call-template name="createVariables"/>
			</xsl:variable>	

			<xsl:variable name="predefinedLayoutElements" >
				<xsl:call-template name="createPredefinedLayoutElements"/>
			</xsl:variable>
				
			<xsl:variable name="citationStyleLayoutDefinitions" >
				<xsl:call-template name="createCitationStyleLayoutDefinitions"/>
			</xsl:variable>	

			<xsl:element name="xsl:template">
				<xsl:attribute name="match" select="'node() | @*'"/>
				<xsl:element name="xsl:copy">
					<xsl:element name="xsl:apply-templates">
						<xsl:attribute name="select" select="'@* | node ()'"/>
					</xsl:element>
				</xsl:element>
			</xsl:element>


			<xsl:element name="xsl:template">
				<xsl:attribute name="match" select="@source-placeholder-tag"/>
				<xsl:element name="xsl:element">
					<xsl:attribute name="name" select="'{name(.)}'"/>
					
					<xsl:element name="xsl:copy-of">
						<xsl:attribute name="select" select="'child::node()'" />
					</xsl:element>
		
					<xsl:element name="xsl:element">
						<xsl:attribute name="name" select="@citation-placeholder-tag" />
		
						<xsl:element name="xsl:variable">
								<xsl:attribute name="name" select="'citation'" />
								<xsl:element name="xsl:for-each">
									<xsl:attribute name="select" select="@md-xpath" />
									<xsl:copy-of select="$variables" />
									<xsl:copy-of select="$predefinedLayoutElements" />
									<xsl:copy-of select="$citationStyleLayoutDefinitions" />
								</xsl:element>
						</xsl:element>
						
						<xsl:element name="xsl:value-of">
							<xsl:attribute name="select" select="'func:cleanCitation($citation)'" />
						</xsl:element>
		
					</xsl:element>
			
			
				</xsl:element>
			</xsl:element>
			
			<xsl:call-template name="insertIncludes"/>
			
			<xsl:call-template name="insertFunctions"/>
			
		</xsl:element>
		
		
	</xsl:template>
	

	<!-- Font Styles -->
	<xsl:variable name="font-styles">
		<xsl:variable name="fs" select="concat(/cit:citation-style/@name, '/font-styles.xml')"/>
		<xsl:choose>
			<xsl:when test="doc-available($fs)">
				<xsl:copy-of select="document($fs)/font-styles-collection/*"/>				
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="document('font-styles.xml')/font-styles-collection/*"/>				
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	
	
	<!-- ##### VARIABLES ##### -->
	<xsl:template name="createVariables">

		<!-- load default variables from the -->	
		<!-- global set of default variables -->
		<xsl:if test="@include-global-default-variables='yes'">
			<xsl:call-template name="insertGlobalDefaultVariables"/>
		</xsl:if>
		<!-- set of default variables for the citation style -->
		<xsl:if test="@include-default-variables='yes'">
			<xsl:call-template name="insertDefaultVariables"/>
		</xsl:if>
	
		<xsl:if test="count (/cit:citation-style/cit:variables/cit:variable)>0">
			<xsl:comment>### Variables ###</xsl:comment>
			<xsl:text>
	</xsl:text>
			<xsl:copy-of select="func:generateVariables(/cit:citation-style/cit:variables/cit:variable)"/>
			<xsl:comment>### End of Variables ###</xsl:comment>
			<xsl:text>
	</xsl:text>
		</xsl:if>
	</xsl:template>
	
	
	<!-- ##### PREDEFINED LAYOUT ELEMENTS ##### -->
	<xsl:template name="createPredefinedLayoutElements">
	
		<!-- load global default layout elements -->	
		<xsl:if test="@include-global-layout-elements='yes'">
			<xsl:call-template name="insertGlobalLayoutElements"/>
		</xsl:if>
		<!-- load local default layout elements -->	
		<xsl:if test="@include-default-layout-elements='yes'">
			<xsl:call-template name="insertDefaultLayoutElements"/>
		</xsl:if>	
	
	
	
		<xsl:if test="count (/cit:citation-style/cit:predefined-layout-elements/cit:layout-element)>0">
			<xsl:comment>### Predefined Layout Elements ###</xsl:comment>
			<xsl:text>
	</xsl:text>
	
			<xsl:for-each select="/cit:citation-style/cit:predefined-layout-elements/cit:layout-element">
				<xsl:call-template name="createLayoutElement">
					<xsl:with-param name="le" select="." />
				</xsl:call-template>
			</xsl:for-each>
			<xsl:comment>### End of Predefined Layout Elements ###</xsl:comment>
			<xsl:text>
	</xsl:text>
		</xsl:if>
	</xsl:template>
	
	
	
	
	
	<!-- ##### CITATION STYLE LAYOUT DEFINITIONS ##### -->
	<xsl:template name="createCitationStyleLayoutDefinitions">
		<xsl:if test="count(/cit:citation-style/cit:cs-layout-definition)>0"> 
			<xsl:comment>### Citation Style Layout Definitions ###</xsl:comment>
			<xsl:text>
	</xsl:text>

			<xsl:element name="xsl:choose">
			
				<xsl:for-each select="/cit:citation-style/cit:cs-layout-definition">
						 
						<xsl:if test="cit:parameters/cit:valid-if">
							<xsl:element name="xsl:when">
									<xsl:attribute name="test" select="cit:parameters/cit:valid-if"/>
<!--									<xsl:element name="snippet:snippet">-->
									<!--
								
										NOTE: 
										objid should be always defined to ensure item-snippet binding!!!
									
										
										<xsl:element name="xsl:attribute">
											<xsl:attribute name="name" select="'objid'"/>
											<xsl:attribute name="select" select="'$objid'"/>
										</xsl:element>-->
										
										<xsl:call-template name="createLayoutElement">
											<xsl:with-param name="le" select="."/>
										</xsl:call-template>
<!--									</xsl:element>-->
							</xsl:element>
						</xsl:if>
						<xsl:if test="not(cit:parameters/cit:valid-if)">
							<xsl:element name="xsl:otherwise">
								<!--<xsl:element name="snippet:snippet">
								
									NOTE: 
									objid should be always defined to ensure item-snippet binding!!!
									
									<xsl:element name="xsl:attribute">
										<xsl:attribute name="name" select="'objid'"/>
										<xsl:attribute name="select" select="'$objid'"/>
									</xsl:element>
									-->																	
									<xsl:call-template name="createLayoutElement">
										<xsl:with-param name="le" select="."/>
									</xsl:call-template>
<!--								</xsl:element>-->
							</xsl:element>
						</xsl:if>
									
				</xsl:for-each>
				
			</xsl:element>
							
			<xsl:comment>### End of Citation Style Layout Definitions ###</xsl:comment>
			<xsl:text>
	</xsl:text>
		</xsl:if>
	</xsl:template>


	<!-- ##### LAYOUT ELEMENT ##### -->
	<xsl:template name="createLayoutElement">
		<xsl:param name="le"/>
		<!-- TODO: Create the LE if it's referenced at least once in other LEs  -->
		<xsl:variable name="params" select="$le/cit:parameters"/>
		<xsl:variable name="var-name" select="if ($le/@name) then $le/@name else 'var'"/>
		<xsl:element name="xsl:variable">
			<xsl:attribute name="name" select="$var-name"/> 
			<xsl:choose>
				<xsl:when test="$le/@repeatable='yes'">
				<!-- Repeatable LEs -->
				
				
					<xsl:call-template name="createRepeatableLayoutElement">
						<xsl:with-param name="le" select="$le"/>
					</xsl:call-template>
					
				
				</xsl:when>
				<xsl:otherwise>
					<!-- Plain LEs -->
					<xsl:call-template name="createPlainLayoutElement">
						<xsl:with-param name="le" select="$le"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
		
<!--		output the copy-of the variable only in case if it is not in -->
<!--		the scope of predefined layout elements -->
		<xsl:if test="name($le/..) != 'predefined-layout-elements'">
			<xsl:element name="xsl:copy-of">
				<xsl:attribute name="select" select="concat ('$', $var-name)" />
			</xsl:element>			
		</xsl:if>
		
	</xsl:template>	

	<!-- ##### PLAIN LAYOUT ELEMENT ##### -->
	<xsl:template name="createPlainLayoutElement">
		<xsl:param name="le"/>
		<xsl:variable name="params" select="$le/cit:parameters"/>
		<xsl:comment>### Plain Layout Element ###</xsl:comment>
		<xsl:text>
	</xsl:text>
		
		<!-- if there is reference to other LEs/variable, ignore children LEs -->
		<xsl:if test="$le/@ref">
			
			<xsl:comment>### @ref is available ###</xsl:comment>

			<xsl:call-template name="applyParameters">
				<xsl:with-param name="valid-if" select="$params/cit:valid-if"/>
				<xsl:with-param name="start-val" select="$le/@ref"/>
				<xsl:with-param name="params" >
					<xsl:call-template name="applyStartsWithEndsWith">
						<xsl:with-param name="params" select="$params"/>
					</xsl:call-template>
					<xsl:call-template name="applyMaxLengthEndsWith">
						<xsl:with-param name="params" select="$params"/>
					</xsl:call-template>
					<xsl:call-template name="applyI18N">
						<xsl:with-param name="params" select="$params"/>
					</xsl:call-template>
					<xsl:call-template name="applyFontStyle">
						<xsl:with-param name="params" select="$params"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
			
						
		</xsl:if>


		<!-- If there is no reference to other LEs/variable, use children LEs -->
		<xsl:if test="not($le/@ref)">

			<xsl:comment>### @ref is not available ###</xsl:comment>
			
			<xsl:call-template name="applyParameters">
				<xsl:with-param name="valid-if" select="$params/cit:valid-if"/>
				<xsl:with-param name="start-val" select="''''''"/>
				<xsl:with-param name="params">
					<xsl:call-template name="applyLayoutElements">
						<xsl:with-param name="le" select="$le"/>
					</xsl:call-template>
					<xsl:call-template name="applyStartsWithEndsWith">
						<xsl:with-param name="params" select="$params"/>
					</xsl:call-template>
					<xsl:call-template name="applyI18N">
						<xsl:with-param name="params" select="$params"/>
					</xsl:call-template>
					<xsl:call-template name="applyFontStyle">
						<xsl:with-param name="params" select="$params"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>

		</xsl:if>

		
	</xsl:template>
	
	<!-- ##### REPEATABLE LAYOUT ELEMENT ##### -->
	<xsl:template name="createRepeatableLayoutElement">
		<xsl:param name="le"/>
		<xsl:comment>### Repeatable Layout Element ###</xsl:comment>
		<xsl:text>
	</xsl:text>
		
		<!-- 
			For the moment 
				[starts|ends]-with,  font-style 
			are taken from the default parameters
			TODO: separate handling of the params for the position handling
				  for repeatable elements 
		-->
		<xsl:variable name="params" select="$le/cit:parameters[not(@position) or @position='default']"/>
	
		<xsl:call-template name="applyParameters">
			<xsl:with-param name="valid-if" select="$params/cit:valid-if"/>
			<xsl:with-param name="start-val" select="''''''"/>
			<xsl:with-param name="params">
				<xsl:call-template name="applyLayoutElements">
					<xsl:with-param name="le" select="$le"/>
				</xsl:call-template>
				<xsl:call-template name="applyStartsWithEndsWith">
					<xsl:with-param name="params" select="$params"/>
				</xsl:call-template>
				<xsl:call-template name="applyI18N">
					<xsl:with-param name="params" select="$params"/>
				</xsl:call-template>
				<xsl:call-template name="applyFontStyle">
					<xsl:with-param name="params" select="$params"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
				
<!--		<xsl:element name="xsl:copy-of">-->
<!--			<xsl:attribute name="select" select="'$var'" />-->
<!--		</xsl:element>-->
		
	</xsl:template>		
	
	
	<!--#### APPLY PARAMETERS ####-->
	<xsl:template name="applyParameters">
		<xsl:param name="valid-if" />
		<xsl:param name="start-val" />
		<xsl:param name="params" />
		
		<!-- start value for val -->
		<xsl:element name="xsl:variable">
			<xsl:attribute name="name" select="'var'" />
<!--			<xsl:attribute name="select" select="concat('concat(', $start-val, ', '''')')" />-->
			<xsl:attribute name="select" select="$start-val" />
		</xsl:element>
		
		<!-- check valid-if firstly-->
		
		<xsl:choose>
			<xsl:when test="$valid-if and name($valid-if/../..)!='cs-layout-definition'">
				<xsl:comment>valid-if</xsl:comment>
				<xsl:element name="xsl:variable">
					<xsl:attribute name="name" select="'var'" />
					<xsl:element name="xsl:if">
						<xsl:attribute name="test" select="$valid-if" />
						<xsl:copy-of select="$params"/>
						<xsl:element name="xsl:copy-of">
							<xsl:attribute name="select" select="'$var'" />
						</xsl:element>
					</xsl:element>
				</xsl:element>
<!--				?????-->
				<xsl:element name="xsl:copy-of">
					<xsl:attribute name="select" select="'$var'" />
				</xsl:element>
<!--				?????-->
								
			</xsl:when>
			<!-- if there is no valid-if, just add value of the variable -->
			<xsl:otherwise>
				<xsl:copy-of select="$params"/>
				<xsl:element name="xsl:copy-of">
					<xsl:attribute name="select" select="'$var'" />
				</xsl:element>
			</xsl:otherwise>			
		</xsl:choose>
			
		
	</xsl:template>	
	
	
	<!--#### APPLY MAX-COUNT ####-->
	<xsl:template name="applyMaxCount">
		<xsl:param name="params" />
		<xsl:param name="les" />
		<xsl:variable  name="max-count" select="
			if ($params/cit:max-count/@value) 
			then concat ('position()&lt;=', $params/cit:max-count/@value) 
			else ''
		"/>
		<xsl:choose>
			<xsl:when test="$max-count!=''">
				<xsl:element name="xsl:if">
					<xsl:attribute name="test" select="$max-count"/>
					<xsl:copy-of select="$les"/>
				</xsl:element>			
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$les"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	<!--#### APPLY POSITION ####-->
	<xsl:template name="applyPosition">
		<xsl:param name="params" />
		<xsl:param name="elems" />
		
		
		<xsl:call-template name="applyMaxCount">
			<xsl:with-param name="params" select="$params"/>
			<xsl:with-param name="les">
				<le>
					<!-- @position-delimiter is needed to overwrite the delimiter -->
					<!-- value for the position handling  -->
					<xsl:attribute name="position-delimiter" select="$params/cit:delimiter/@value"/>
					
					<xsl:element name="xsl:call-template">
						<xsl:attribute name="name" select="'applyDelimiter'"/>
						
						<xsl:element name="xsl:with-param">
							<xsl:attribute name="name" select="'les'"/>
							
							<xsl:for-each select="$elems/cit:layout-element">
								<le>
									<xsl:call-template name="createLayoutElement">
										<xsl:with-param name="le" select="."/>
									</xsl:call-template>
								</le>
							</xsl:for-each>
							 
						</xsl:element>
						
						
						<!-- set internal-delimiter to the $default-delimiter if it is not defined -->
						<xsl:variable
							name="internal-delimiter" 
							select="if ($params/cit:internal-delimiter/@value) then $params/cit:internal-delimiter/@value else $default-delimiter" 
						/>
						<xsl:element name="xsl:with-param">
							<xsl:attribute name="name" select="'delimiter'"/>
							<xsl:attribute name="select" select="concat('''', $internal-delimiter, '''')"/>
						</xsl:element>
					</xsl:element>
				</le>
			</xsl:with-param>
			
		</xsl:call-template>
	
		
		
	</xsl:template>	

	


	<!--#### APPLY START-WITH/ENDS-WITH ####-->
	<xsl:template name="applyStartsWithEndsWith">
		<xsl:param name="params" />
		
		<!-- do not apply when no cit:starts-with/@value or $params/cit:ends-with/@value defined -->
		
		<xsl:if test="$params/cit:starts-with/@value or $params/cit:ends-with/@value">
			<xsl:comment>
				start-with/ends-with
			</xsl:comment>
			
			<xsl:element name="xsl:variable">
				<xsl:attribute name="name" select="'var'" />
				
						<xsl:if test="$params/cit:starts-with/@value">
							
							<!-- check $var. no output if 1) mode is not 'static' and 2) $var is empty -->
							
							<xsl:element name="xsl:if">
								<xsl:attribute name="test" select="
									concat( 
										if ($params/cit:starts-with/@mode='static') then 'true() or ' else '',
										'exists($var) and $var!='''''
									)
								" />
									<xsl:element name="xsl:text">
										<xsl:value-of select="$params/cit:starts-with/@value"/>
									</xsl:element>
							</xsl:element>
						</xsl:if>
						
						<xsl:element name="xsl:copy-of">
							<xsl:attribute name="select"
								select="'$var'" />
						</xsl:element>
						
						<xsl:if test="$params/cit:ends-with/@value">
							<xsl:element name="xsl:if">
								<xsl:attribute name="test" select="
									concat( 
										if ($params/cit:ends-with/@mode='static') then 'true() or ' else '',
										'exists($var) and $var!='''''
									)
								" />
								<xsl:element name="xsl:text">
									<xsl:value-of select="$params/cit:ends-with/@value"/>
								</xsl:element>
							</xsl:element>
						</xsl:if>
						
					</xsl:element>			
			

			
		</xsl:if>
	</xsl:template>


	<!--#### APPLY MAX-LENGTH-ENDS-WITH ####-->
<!--	TODO: 
		1) check emptyness $var 
		2) check mode='static' 
		-->
	
	<xsl:template name="applyMaxLengthEndsWith">
		<xsl:param name="params" />
		<xsl:if test="$params/cit:max-length-ends-with and $params/cit:max-length-ends-with/@value>0">
			<xsl:comment>max-length-ends-with</xsl:comment>
			<xsl:element name="xsl:variable">
				<xsl:attribute name="name" select="'var'"/> 
				
				<xsl:element name="xsl:if">
					<xsl:attribute name="test" select="
									concat( 
										if ($params/cit:max-length-ends-with/@mode='static') then 'true() or ' else '',
										'exists($var) and $var!='''''
									)
					" />
					
					<xsl:element name="xsl:value-of">
						<xsl:attribute name="select" select="
							concat(
								'if (string-length($var)>', 
								$params/cit:max-length-ends-with/@value, 
								') then concat(substring ($var, 1, ',
								$params/cit:max-length-ends-with/@value,
								'),  ''', 
								$params/cit:max-length-ends-with,
								''') else $var'
							)"/>						
						</xsl:element>
				</xsl:element>								
			</xsl:element>
		</xsl:if>
	</xsl:template>


	<!--#### FONT-STYLE ####-->
	<xsl:template name="applyFontStyle">
		<xsl:param name="params" />
		<xsl:variable name="ref" select="$params/cit:font-style/@ref"/>
		
		<xsl:if test="$ref!=''">
			<!--#### TODO: rename to the <style> ####-->
			<xsl:comment>font-style</xsl:comment>
			
			<xsl:element name="xsl:variable">
				<xsl:attribute name="name" select="'var'"/>
				<xsl:element name="xsl:if">
					<xsl:attribute name="test" select="'exists($var) and $var!='''''" />
					
					<xsl:value-of select="concat (
						'&lt;span class=&quot;',
						if ($font-styles/font-style[@name=$ref]/@css-class) 
						then $font-styles/font-style[@name=$ref]/@css-class
						else
							error(
								QName('http://www.escidoc.de/citationstyle', 'err:FontStyleIsNotDefined' ), 
								concat ('Font Style is not defined: ', $ref )
							),
						'&quot;&gt;'	
							)"/>
							
						<xsl:element name="xsl:copy-of">
							<xsl:attribute name="select" select="'$var'"/>
						</xsl:element>
					<xsl:value-of select="'&lt;/span&gt;'"/>
										
<!--					<xsl:element name="span">-->
<!--						<xsl:attribute name="class">-->
<!--							<xsl:value-of select="-->
<!--								if ($font-styles/font-style[@name=$ref]/@css-class) -->
<!--								then $font-styles/font-style[@name=$ref]/@css-class-->
<!--								else-->
<!--									error(-->
<!--										QName('http://www.escidoc.de/citationstyle', 'err:FontStyleIsNotDefined' ), -->
<!--										concat ('Font Style is not defined: ', $ref )-->
<!--									)-->
<!--							"/>-->
<!--						</xsl:attribute>-->
<!--						<xsl:element name="xsl:copy-of">-->
<!--							<xsl:attribute name="select" select="'$var'"/>-->
<!--						</xsl:element>-->
<!--					</xsl:element>-->
					
					
					
				</xsl:element> 
			</xsl:element>
			
		</xsl:if>
	</xsl:template>
	
	<!--#### I18N (localization/internationalization) ####-->
	<xsl:template name="applyI18N">
		<xsl:param name="params" />
		<xsl:variable name="ref" select="$params/cit:i18n/@ref"/>
		
		<xsl:if test="$ref!=''">
			<xsl:comment>i18n</xsl:comment>
			
			<xsl:element name="xsl:variable">
				<xsl:attribute name="name" select="'var'"/>
				<xsl:element name="xsl:if">
					<xsl:attribute name="test" select="'exists($var) and $var!='''''" />
					
					<xsl:value-of select="concat ( '&lt;localized class=&quot;', $ref, '&quot;&gt;' )"/>
						<xsl:element name="xsl:copy-of">
							<xsl:attribute name="select" select="'$var'"/>
						</xsl:element>
					<xsl:value-of select="'&lt;/localized&gt;'"/>
					
				</xsl:element> 
			</xsl:element>
			
		</xsl:if>
	</xsl:template>


						
	<!--#### APPLY LAYOUT ELEMENTS ####-->
<!--	TODO: 
		1) check emptyness $var 
		-->
	
	<xsl:template name="applyLayoutElements">
		<xsl:param name="le" />
		
		<xsl:variable name="params" select="$le/cit:parameters"/>

		<xsl:element name="xsl:variable">
			<xsl:attribute name="name" select="'var'" />
					
			<!-- repeatable element -->
			<xsl:if test="$le/@repeatable='yes'">
			
			
				<xsl:element name="xsl:call-template">
					<xsl:attribute name="name" select="'applyDelimiter'"/>
					<xsl:element name="xsl:with-param">
						<xsl:attribute name="name" select="'les'"/>
						
						<xsl:element name="xsl:for-each">
							<xsl:attribute name="select" select="$le/@ref"/>

							<!-- there are position handling -->
							<xsl:if test="count($params)>1">
							
								<xsl:element name="xsl:choose">
								
<!--								PROCESSING ORDER: 
									1. position=last
									2. position=<number>
									3. default
-->
									<xsl:for-each select="
										$params[@position='last'], 
										$params[matches(@position, '\d+')], 
										$params[not(@position) or @position='default']
									">
										
										<xsl:if test="@position='last' or matches(@position,'\d+')">
											<xsl:variable name="test_pos" select="
												concat('position()=', 
													if (@position='last') 
													then 'last()'
													else @position
												)
											"/>
											<xsl:variable name="sel_pos" select="@position"/>
											<xsl:element name="xsl:when">
												<xsl:attribute name="test" select="$test_pos"/>
												<xsl:call-template name="applyPosition">
													<xsl:with-param name="params" select="."/>
													<xsl:with-param name="elems" select="
														if ($le/cit:elements[@position=$sel_pos]) 
														then $le/cit:elements[@position=$sel_pos]
														else if ($le/cit:elements[not(@position) or @position='default'])
														then $le/cit:elements[not(@position) or position='default']
														else 
															error(
																QName('http://www.escidoc.de/citationstyle', 'err:DefaultParametersAreNotDefined' ), 
																concat ('Default Elements are not defined the layout element with the position handling. Layout Element: ', $le/@name, '; Position: ', $sel_pos )
															)
													"/>
												</xsl:call-template>
											</xsl:element>
										</xsl:if>
										
									
										<xsl:if test="not(@position) or position='default'">
											<xsl:element name="xsl:otherwise">
												<xsl:call-template name="applyPosition">
													<xsl:with-param name="params" select="."/>
													<xsl:with-param name="elems" select="$le/cit:elements[not(@position or position='default')]"/>
												</xsl:call-template>
											</xsl:element>
										</xsl:if>
									
									</xsl:for-each>
								
								</xsl:element>
							
							</xsl:if>
							
							<!-- there are no position handling, only default behavior -->
							<xsl:if test="not(count($params)>1)">
								<xsl:call-template name="applyPosition">
									<xsl:with-param name="params" select="$params"/>
									<xsl:with-param name="elems" select="$le/cit:elements"/>
								</xsl:call-template>
							</xsl:if>


						</xsl:element>
					
					</xsl:element>
					
					<xsl:element name="xsl:with-param">
						<xsl:attribute name="name" select="'delimiter'"/> 
						<xsl:attribute name="select" select="concat('''', $params[not(@position) or @position='default']/cit:delimiter/@value, '''')"/>
					</xsl:element>
					
				</xsl:element>		
			</xsl:if>
			
			
			<!-- plain element -->
			<xsl:if test="not($le/@repeatable='yes')">
			
				<xsl:variable
					name="delimiter" 
					select="if ($params/cit:delimiter/@value) then $params/cit:delimiter/@value else $default-delimiter" 
				/>
				<xsl:element name="xsl:call-template">
					<xsl:attribute name="name" select="'applyDelimiter'" />
					<xsl:element name="xsl:with-param">
						<xsl:attribute name="name" select="'les'" />
						<xsl:for-each select="$le/cit:elements/cit:layout-element">
							<le>
									<xsl:call-template name="createLayoutElement">
										<xsl:with-param name="le" select="." />
									</xsl:call-template>									
							</le>
						</xsl:for-each>
						
					</xsl:element>
					<xsl:element name="xsl:with-param">
						<xsl:attribute name="name" select="'delimiter'"/>
						<xsl:attribute name="select"
							select="concat('''', $delimiter, '''')" />
					</xsl:element>
				</xsl:element>
			</xsl:if>
			
		</xsl:element>

		
	</xsl:template>
	
	<!--#############################################################-->
	
	<!-- Generate Variables -->
	<xsl:function name="func:generateVariables">
		<xsl:param name="vars"/>
		<xsl:for-each select="$vars">
			<!-- Declare variable -->
			<xsl:element name="xsl:variable">
				<xsl:attribute name="name" select="@name"/>
<!--					<xsl:attribute name="as" select="-->
<!--						if (not(@type)) then 'xs:string' else @type -->
<!--					"/>-->
				<xsl:if test="@type">
					<xsl:attribute name="as" select="@type"/>
				</xsl:if>
				<xsl:element name="xsl:value-of">
					<xsl:attribute name="select" select="."/>
				</xsl:element>
			</xsl:element>
		</xsl:for-each>
		
	</xsl:function>
	
	
	<!-- Includes -->
	<xsl:template name="insertIncludes">  
		<xsl:comment>### Includes ###</xsl:comment>
		<xsl:text>
	</xsl:text>
		<xsl:copy-of select="document('cs-processing-xslt-includes.xml')/xsl:includes/*"/>	
	</xsl:template>




	<xsl:template name="insertGlobalDefaultVariables">
		<xsl:comment>### Global Default Variables ###</xsl:comment>
		<xsl:text>
	</xsl:text>
	
		<xsl:copy-of select="func:generateVariables(document('variables.xml')/cit:variables/*)"/>
	</xsl:template>


	<xsl:template name="insertDefaultVariables">
		<xsl:variable name="csv" select="document(concat(@name, '/variables.xml'))"/>
		<xsl:if test="exists ($csv)">
			<xsl:variable name="csv_ref" select="$csv/cit:variables/@ref" /> 
			<!-- if variables/@ref is defined, add variables from other citation style -->
			<xsl:if test="exists ($csv_ref)">
				<xsl:variable name="csv_ref_inc" select="document(concat($csv_ref, '/variables.xml'))"/>
				<xsl:if test="exists ($csv_ref_inc)">
		<xsl:comment>### <xsl:value-of select="@name"/> specific Default Variables, included from <xsl:value-of select="$csv_ref"/> Citation Style ###</xsl:comment>
		<xsl:text>
	</xsl:text>
					<xsl:copy-of select="func:generateVariables($csv_ref_inc/cit:variables/*)"/>
				</xsl:if>
			</xsl:if>
		<xsl:comment>### <xsl:value-of select="@name"/> specific Default Variables ###</xsl:comment>
		<xsl:text>
	</xsl:text>
			<xsl:copy-of select="func:generateVariables($csv/cit:variables/*)"/>	
		</xsl:if>
	</xsl:template>
	
	
	
	<xsl:template name="insertGlobalLayoutElements">
	
		<xsl:variable name="gle" select="document('layout-elements.xml')/cit:predefined-layout-elements/*"/>
		<xsl:if test="count ($gle)>0">
			<xsl:comment>### Global Predefined Layout Elements ###</xsl:comment>
			<xsl:text>
	</xsl:text>
			<xsl:for-each select="$gle">
				<xsl:call-template name="createLayoutElement">
					<xsl:with-param name="le" select="." />
				</xsl:call-template>
			</xsl:for-each>
			<xsl:comment>### End of Predefined Layout Elements ###</xsl:comment>
			<xsl:text>
	</xsl:text>
		</xsl:if>	
	</xsl:template>
	

	<xsl:template name="insertDefaultLayoutElements">
		<xsl:variable name="dle" select="document(concat(@name, '/layout-elements.xml'))"/>
		<xsl:if test="exists ($dle)">
			<xsl:variable name="dle_ref" select="$dle/cit:predefined-layout-elements/@ref" /> 
			<!-- if predefined-layout-elements/@ref is defined, add predefined-layout-elements from other citation style -->
			<xsl:if test="exists ($dle_ref)">
				<xsl:variable name="dle_ref_inc" select="document(concat($dle_ref, '/layout-elements.xml'))/cit:predefined-layout-elements/*"/>
				<xsl:if test="exists ($dle_ref_inc)">
		<xsl:comment>### <xsl:value-of select="@name"/> specific Default Layout Elements, included from <xsl:value-of select="$dle_ref"/> Citation Style ###</xsl:comment>
		<xsl:text>
	</xsl:text>
			<xsl:for-each select="$dle_ref_inc">
				<xsl:call-template name="createLayoutElement">
					<xsl:with-param name="le" select="." />
				</xsl:call-template>
			</xsl:for-each>
	
				</xsl:if>
			</xsl:if>
			
		<xsl:comment>### <xsl:value-of select="@name"/> specific Default Layout Elements ###</xsl:comment>
		<xsl:text>
	</xsl:text>
			
			<xsl:for-each select="$dle/cit:predefined-layout-elements/*">
				<xsl:call-template name="createLayoutElement">
					<xsl:with-param name="le" select="." />
				</xsl:call-template>
			</xsl:for-each>
			
				
		</xsl:if>
	</xsl:template>

	
	<xsl:template name="insertFunctions">
		<xsl:comment>### Runtime Functions ###</xsl:comment>
		<xsl:text>
	</xsl:text>
		<xsl:copy-of select="document('functions.xml')/cit:functions/*"/>	
	</xsl:template>
	
	

</xsl:stylesheet>
