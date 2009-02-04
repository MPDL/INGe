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
	<xsl:param name="item-link"/>

	<xsl:template match="/">
		<xsl:apply-templates select="rdf:RDF/rdf:Description"/>
	</xsl:template>

	<xsl:template match="rdf:Description">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<title>CoNE - <xsl:value-of select="dc:title"/></title>
				<link href="../../resources/eSciDoc_CSS_v2/main.css" type="text/css" rel="stylesheet"/>
				<link href="../../resources/eSciDoc_CSS_v2/themes/blue/styles/theme.css" id="blueTheme" type="text/css" title="blue" rel="stylesheet"/>
				<script type="text/javascript" language="JavaScript" src="../../js/jquery-1.2.6.min.js">;</script>
				<script>
					$(document).ready(function() {
						var requestString = '<xsl:value-of select="$citation-link"/>';
						var ampEsc = '&amp;';
						var amp = ampEsc.substring(0,1);
						requestString = requestString.replace(/&amp;/g, amp);
						
						$.get(requestString, function(itemList){
							var allItems = itemList.getElementsByTagName('escidocItem:item');
							
							var element = '';
							var itemURL = '';
							var publicationTitle = '';
							
							
							for(var i=0; itemList.getElementsByTagName('escidocItem:item').length <xsl:text disable-output-escaping="yes"> > </xsl:text>i; i++){
							
								element = '';
								element = '<span class="xHuge_area0 xTiny_marginLExcl endline">' + $(allItems[i].getElementsByTagName('dcterms:bibliographicCitation')[0]).text() + '</span>';
							
								itemURL = '';
								itemURL = '<xsl:value-of select="$item-link"/>'.replace('$1', $(allItems[i]).attr('objid') + ':' + $(allItems[i].getElementsByTagName('prop:latest-release')[0].getElementsByTagName('release:number')[0]).text());
								
								publicationTitle = '';
								publicationTitle = $.trim($(allItems[i].getElementsByTagName('dc:title')[0]).text());
								var elementParts = element.split(publicationTitle);
								if(elementParts.length == 2){
									element = elementParts[0] + '<a href="' + itemURL + '" target="_blank" >' + publicationTitle + '</a>' + elementParts[1];
								}
								
								$('.publicationsArea').append('<b class="xLarge_area0 endline labelLine">&#160;<span class="noDisplay">: </span></b>');
								$('.publicationsArea').append(element);
								
								if ($('.publicationsArea:last-child').find('span.Italic').length == 1) {
									$('.publicationsArea:last-child').find('span.Italic').replaceWith("<i>" + $('.publicationsArea:last-child').find('span.Italic').text() + "</i>");
								};
								$('.publicationsArea:last-child').find('span.Default').each(function(k, elem){
									$(elem).replaceWith($(elem).html());
								});
							}
							
						});
					});
				</script>
			</head>
			<body>
			
				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div id="headerSection">
							<div class="clear free_area0">
								&#160;
							</div>
							<div id="headLine" class="clear headLine">
								<!-- Headline starts here -->
								<h1>Researcher Portfolio</h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div id="subHeaderSection" class="small_marginLIncl">
							<div class="contentMenu">
								<div class="sub">
									&#160;
								</div>
							</div>
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h6>This Researcher Portfolio is provided by the eSciDoc CONE service.</h6>
								<!-- Subheadline ends here -->
							</div>
						</div>
					</div>
					<div class="full_area0">
						<div class="full_area0 fullItem">
							<div class="full_area0 itemHeader">
								<span class="xLarge_area0 endline">
									&#160;
								</span>
								<span class="seperator">&#160;</span>
								<span class="free_area0_p8 endline itemHeadline">
									<h2>
										<xsl:if test="degree != ''"><xsl:value-of select="degree"/></xsl:if> <xsl:value-of select="dc:title"/>
									</h2>
									<h3>
										<xsl:for-each select="ou">
											<xsl:value-of select="."/><xsl:if test="position() != last()">, </xsl:if> 
										</xsl:for-each>
									</h3>
								</span>
								<div class="statusArea free_area0">
									<xsl:if test="photo != ''">
										<img src="{photo}" title="{dc:title}" height="100"/>
									</xsl:if>
								</div>
							</div>
							<div class="full_area0 itemBlock">
								<h3 class="xLarge_area0_p8 endline blockHeader">
									Researcher Profile
								</h3>
								<span class="seperator">&#160;</span>
								<div class="free_area0 itemBlockContent endline">
									<xsl:if test="position != ''">	
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												Current Position<span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:value-of select="position"/>
											</span>
										</div>
									</xsl:if>
									<div class="free_area0 endline itemLine noTopBorder">
										<b class="xLarge_area0 endline labelLine">
											Affiliated Organisations<span class="noDisplay">: <br /></span>
										</b>
										<span class="xHuge_area0 xTiny_marginLExcl endline">
											<xsl:for-each select="ou">
												<xsl:number format="1. " />
												<xsl:value-of select="."/><br/>
											</xsl:for-each>
										</span>
									</div>
									<xsl:if test="phone != ''">	
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												Phone<span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:for-each select="phone">
													<xsl:value-of select="."/> <br />
												</xsl:for-each>
											</span>
										</div>
									</xsl:if>
									<xsl:if test="email != ''">	
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												email<span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:for-each select="email">
													<xsl:value-of select="."/> <br />
												</xsl:for-each>
											</span>
										</div>
									</xsl:if>
									<xsl:if test="url != ''">	
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												My Websites<span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:for-each select="url">
													<a>
														<xsl:attribute name="href">
															<xsl:value-of select="rdf:Description/@rdf:about"/>
														</xsl:attribute>
														<xsl:attribute name="title">
															<xsl:value-of select="*/dc:description"/>
														</xsl:attribute>
														<xsl:attribute name="target">_blank</xsl:attribute>
														<xsl:value-of select="*/dc:title"/>
													</a>
													<br />
												</xsl:for-each>
											</span>
										</div>
									</xsl:if>
									<xsl:if test="awards != ''">
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												Awards<span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:value-of select="awards"/>
											</span>
										</div>
									</xsl:if>
									<div class="free_area0 endline itemLine noTopBorder">
										<b class="xLarge_area0 endline labelLine">
											Researcher ID<span class="noDisplay">: </span>
										</b>
										<span class="xHuge_area0 xTiny_marginLExcl endline">
											<xsl:value-of select="@rdf:about"/>
										</span>
									</div>
									<xsl:if test="dcterms:identifier != ''">
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												Additional IDs<span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:for-each select="dcterms:identifier">
													<xsl:if test="position() &gt; 1"> <br /> </xsl:if>
													<xsl:value-of select="."/>
												</xsl:for-each>
											</span>
										</div>
									</xsl:if>
									<xsl:if test="keyword != ''">
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												Research Fields<span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:for-each select="keyword">
													-<xsl:value-of select="."/> <br />
												</xsl:for-each>
											</span>
										</div>
									</xsl:if>
									<xsl:if test="ddc != ''">	
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												Subject<span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:value-of select="ddc"/>
											</span>
										</div>
									</xsl:if>


								</div>
							</div>
							<div class="full_area0 itemBlock">
								<h3 class="xLarge_area0_p8 endline blockHeader">
									Publications
								</h3>
								<span class="seperator">&#160;</span>
								<div class="free_area0 itemBlockContent endline">
									
									<div class="free_area0 endline itemLine noTopBorder publicationsArea">
										
									</div>
									


								</div>
							</div>				
							
						</div>
						
					</div>
				<!-- end: content section -->
				</div>
				

				<script type="text/javascript">
					/*
				
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
						var item_link = '<xsl:value-of select="$item-link"/>';
						
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
					
					*/
				</script>
			
				
				
				
			</body>
		</html>
	</xsl:template>
	
</xsl:stylesheet>
