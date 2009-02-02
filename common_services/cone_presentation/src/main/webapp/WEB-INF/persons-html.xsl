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
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/">
	
	<xsl:output method="xml" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" 
     doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" encoding="UTF-8" media-type="text/html"/>

	<xsl:param name="citation-link"/>

	<xsl:template match="/">
		<xsl:apply-templates select="rdf:RDF/rdf:Description"/>
	</xsl:template>

	<xsl:template match="rdf:Description">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<title>CoNE - <xsl:value-of select="dc:title"/></title>
				<style>
					.label {
						float:left;
						width: 300px;
						text-align: right;
						padding: 4px;
					}
					
					.value {
						float: left;
						width: 300px;
						padding: 4px;
					}
					
					.linebreak {
						clear: both;
					}
				</style>
			</head>
			<body>
				<table>
					<tr>
						<td colspan="2">
							<h1 style="float: left">
								<xsl:value-of select="dc:title"/><xsl:if test="degree != ''">, <xsl:value-of select="degree"/></xsl:if>
							</h1>
							<div class="linebreak">
								<xsl:if test="photo != ''">
									<img src="{photo}" alt="{dc:title}" height="100"/>
								</xsl:if>
							</div>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<h2 class="linebreak">
								<xsl:for-each select="ou">
									<xsl:value-of select="."/><br/>
								</xsl:for-each>
							</h2>
						</td>
					</tr>
					<xsl:if test="position != ''">
						<tr>
							<td>
								Current position:
							</td>
							<td>
								<xsl:value-of select="position"/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="awards != ''">
						<tr>
							<td>
								Awards:
							</td>
							<td>
								<xsl:value-of select="awards"/>
							</td>
						</tr>
					</xsl:if>
					<tr>
						<td>
							Researcher ID:
						</td>
						<td>
							<xsl:value-of select="@rdf:about"/>
						</td>
					</tr>
					<xsl:if test="dcterms:identifier != ''">
						<tr>
							<td>
								Additional IDs:
							</td>
							<td>
								<xsl:for-each select="dcterms:identifier">
									<xsl:if test="position() &gt; 1">, </xsl:if>
									<xsl:value-of select="."/>
								</xsl:for-each>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="keyword != ''">
						<tr>
							<td>
								Research fields:
							</td>
							<td>
								<ul>
									<xsl:for-each select="keyword">
										<li>
											<xsl:value-of select="."/>
										</li>
									</xsl:for-each>
								</ul>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="ddc != ''">
						<tr>
							<td>
								Subject:
							</td>
							<td>
								<xsl:value-of select="ddc"/>
							</td>
						</tr>
					</xsl:if>
					<tr>
						<td colspan="2" id="result">
						
						</td>
					</tr>
				</table>
				<script type="text/javascript">
					var xmlhttp;
					
					function requestDone()
					{
						if (xmlhttp.readyState == 4)
						{
							// if "OK"
							if (xmlhttp.status == 200)
							{
								document.getElementById('result').innerHTML = xmlhttp.responseText;
							}
						}
					}

					if (!(navigator.appName.indexOf('MSIE') == -1))
					{
						xmlhttp=new ActiveXObject("Microsoft.XMLHTTP")
					}
					else
					{
						xmlhttp=new XMLHttpRequest();
					}
					if (xmlhttp!=null)
					{
						xmlhttp.onreadystatechange = requestDone;
						
						var url = '<xsl:value-of select="$citation-link"/>';
						var ampEsc = '&amp;';
						var amp = ampEsc.substring(0,1);
						url = url.replace(/&amp;/g, amp);

						xmlhttp.open('GET', url, true);
						xmlhttp.send(null);
					}
				</script>
			</body>
		</html>
	</xsl:template>
	
</xsl:stylesheet>
