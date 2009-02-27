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
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:escidoc="http://escidoc.mpg.de/" xmlns:foaf="http://xmlns.com/foaf/0.1/">
	
	<xsl:output method="xml" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" 
     doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" encoding="UTF-8" media-type="text/html"/>

	<xsl:param name="citation-link"/>
	<xsl:param name="item-link"/>
	<xsl:param name="lang" select="'en'"/>
	
	<xsl:variable name="defaultLang" select="'en'"/>

	<xsl:template match="/">
		<xsl:apply-templates select="rdf:RDF/rdf:Description"/>
	</xsl:template>

	<xsl:template match="rdf:Description">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<title>CoNE - <xsl:value-of select="dc:title"/></title>
				<link href="/cone/resources/eSciDoc_CSS_v2/main.css" type="text/css" rel="stylesheet"/>
				<link href="/cone/resources/eSciDoc_CSS_v2/themes/blue/styles/theme.css" id="blueTheme" type="text/css" title="blue" rel="stylesheet"/>
				<style type="text/css">
					.Italic {
						font-style:italic;
					}
				</style>
				<script type="text/javascript" language="JavaScript" src="/cone/js/jquery-1.2.6.min.js">;</script>
				<script>
				
					var ampEsc = '&amp;';
					var amp = ampEsc.substring(0,1);
				
					$(document).ready(function() {
						var requestString = '<xsl:value-of select="$citation-link"/>';
						requestString = requestString.replace(/&amp;/g, amp);
						
						$.get(requestString, function(itemList){
							var allItems = itemList.getElementsByTagName('escidocItem:item');
							
							var element = '';
							var itemURL = '';
							var publicationTitle = '';
							
							
							for(var i=0; itemList.getElementsByTagName('escidocItem:item').length <xsl:text disable-output-escaping="yes"> > </xsl:text>i; i++){
							
							var citation = allItems[i].getElementsByTagName('dcterms:bibliographicCitation')[0];
								
								if (typeof citation!= 'undefined')
								{
									itemURL = '';
									itemURL = '<xsl:value-of select="$item-link"/>'.replace('$1', $(allItems[i]).attr('objid') + ':' + $(allItems[i].getElementsByTagName('prop:latest-release')[0].getElementsByTagName('release:number')[0]).text());

									element = '<span class="xHuge_area0 xTiny_marginLExcl endline citationBlock">' + $(citation).text() + ' [<a href="' + itemURL + '" target="_blank" >PubMan</a>]' + '</span>';
									
									$('.publicationsArea').append('<b class="xLarge_area0 endline labelLine">&#160;<span class="noDisplay">: </span></b>');
									$('.publicationsArea').append(element);

									$('.publicationsArea:last-child').find('span.Default').each(function(k, elem){
										$(elem).replaceWith($(elem).html());
									});
								}
							}
							
						});
					});
					
					function changeLanguage(element)
					{
						var queryString = location.search;
						
						if (queryString == null || queryString == '')
						{
							queryString = '?lang=' + element.options[element.selectedIndex].value;
						}
						else if (queryString.indexOf('?lang=') <xsl:text disable-output-escaping="yes">></xsl:text>= 0 || queryString.indexOf(amp + 'lang=') <xsl:text disable-output-escaping="yes">></xsl:text>= 0)
						{
							var regExp = new RegExp('(\\?|' + amp + ')lang=[^' + amp + ']+', 'g');
							queryString = queryString.replace(regExp, '$1lang=' + element.options[element.selectedIndex].value);
						}
						else
						{
							queryString += amp + 'lang=' + element.options[element.selectedIndex].value;
						}
						location.href = location.pathname + queryString;
					}
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
								<h1><xsl:value-of select="escidoc:label('researcher_portfolio')"/></h1>
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
								<h6><xsl:value-of select="escidoc:label('provided_by_cone')"/></h6>
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
								<span style="text-align: right;float: right;">
									<xsl:comment>
										<xsl:if test="not(exists($labels/language[@id = $lang]))">
											<span style="color: red"><xsl:value-of select="escidoc:label('language_not_provided')"/></span>
										</xsl:if>
									</xsl:comment>
									<select onchange="changeLanguage(this)">
										<xsl:for-each select="$labels/language">
											<xsl:sort select="@label"/>
											<option value="{@id}">
												<xsl:if test="@id = $lang">
													<xsl:attribute name="selected"/>
												</xsl:if>
												<xsl:value-of select="@label"/>
											</option>
										</xsl:for-each>
										<xsl:if test="not(exists($labels/language[@id = $lang]))">
											<option selected=""><xsl:value-of select="$lang"/></option>
										</xsl:if>
									</select>
								</span>
								<span class="free_area0_p8 endline itemHeadline">
									<h2>
										<xsl:if test="escidoc:degree != ''"><xsl:value-of select="escidoc:degree"/><xsl:text> </xsl:text></xsl:if> <xsl:value-of select="dc:title"/>
									</h2>
									<h3>
										<xsl:for-each select="escidoc:position/rdf:Description[not(exists(dc:end-date)) or dc:end-date = '' or xs:date(dc:end-date) &gt;= current-date()]/escidoc:organization">
											<xsl:sort select="."/>
											<xsl:value-of select="."/><xsl:if test="position() != last()">, </xsl:if> 
										</xsl:for-each>
										&#160;
									</h3>
								</span>
								<div class="statusArea free_area0">
									<xsl:if test="foaf:depiction != ''">
										<img src="{foaf:depiction}" title="{dc:title}" width="120" />
									</xsl:if>
									&#160;
								</div>
							</div>
							<div class="full_area0 itemBlock">
								<h3 class="xLarge_area0_p8 endline blockHeader">
									<xsl:value-of select="escidoc:label('researcher_profile')"/>
								</h3>
								<span class="seperator">&#160;</span>
								<div class="free_area0 itemBlockContent endline">
									<xsl:for-each select="escidoc:position/rdf:Description[not(exists(dc:end-date)) or dc:end-date = '' or xs:date(dc:end-date) &gt;= current-date()]">
										<xsl:sort select="escidoc:organization[0]"/>
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												<xsl:value-of select="escidoc:label('current_position')"/><span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:value-of select="escidoc:position-name"/>
												<xsl:if test="escidoc:organization">
													(<xsl:for-each select="escidoc:organization">
														<xsl:value-of select="."/>
														<xsl:if test="position() != last()">, </xsl:if>
													</xsl:for-each>)
												</xsl:if>
											</span>
										</div>
									</xsl:for-each>
									<xsl:for-each select="escidoc:position/rdf:Description[dc:end-date != '' and xs:date(dc:end-date) &lt; current-date()]">
										<xsl:sort select="dc:end-date" order="descending"/>	
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												<xsl:value-of select="escidoc:label('former_position')"/><span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:value-of select="escidoc:position-name"/>
												<xsl:if test="escidoc:organization">
													(<xsl:value-of select="escidoc:organization"/>)
												</xsl:if>
											</span>
										</div>
									</xsl:for-each>
									<xsl:if test="exists(escidoc:award)">
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												<xsl:value-of select="escidoc:label('awards')"/><span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:value-of select="escidoc:award"/>
											</span>
										</div>
									</xsl:if>
									<xsl:if test="exists(foaf:tel)">	
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												<xsl:value-of select="escidoc:label('phone')"/><span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:for-each select="foaf:tel">
													<xsl:value-of select="."/> <br />
												</xsl:for-each>
											</span>
										</div>
									</xsl:if>
									<xsl:if test="exists(foaf:email)">	
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												<xsl:value-of select="escidoc:label('email')"/><span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:for-each select="foaf:email">
													<xsl:value-of select="."/> <br />
												</xsl:for-each>
											</span>
										</div>
									</xsl:if>
									<xsl:if test="exists(foaf:homepage)">	
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												<xsl:value-of select="escidoc:label('homepages')"/><span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:for-each select="foaf:homepage/rdf:Description">
													<a>
														<xsl:attribute name="href">
															<xsl:value-of select="rdf:value"/>
														</xsl:attribute>
														<xsl:attribute name="title">
															<xsl:value-of select="dc:description"/>
														</xsl:attribute>
														<xsl:attribute name="target">_blank</xsl:attribute>
														<xsl:value-of select="dc:description"/>
													</a>
													<br />
												</xsl:for-each>
											</span>
										</div>
									</xsl:if>
									<xsl:if test="exists(dc:identifier)">
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												<xsl:value-of select="escidoc:label('additional_ids')"/><span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:for-each select="dc:identifier/rdf:Description">
													<xsl:if test="position() &gt; 1"> <br /> </xsl:if>
													<xsl:value-of select="escidoc:idtype"/>: <xsl:value-of select="rdf:value"/>
												</xsl:for-each>
											</span>
										</div>
									</xsl:if>
									<div class="free_area0 endline itemLine noTopBorder">
										<b class="xLarge_area0 endline labelLine">
											<xsl:value-of select="escidoc:label('researcher_id')"/><span class="noDisplay">: </span>
										</b>
										<span class="xHuge_area0 xTiny_marginLExcl endline">
											<xsl:value-of select="@rdf:about"/>
										</span>
									</div>
									<xsl:if test="exists(dc:subject)">
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												<xsl:value-of select="escidoc:label('research_fields')"/><span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:value-of select="dc:subject"/>
											</span>
										</div>
									</xsl:if>
									<xsl:if test="exists(dcterms:subject)">	
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">
												<xsl:value-of select="escidoc:label('subject')"/><span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<xsl:for-each select="dcterms:subject/rdf:Description">
													-<xsl:text> </xsl:text><xsl:value-of select="dc:identifier"/> - <xsl:value-of select="dc:title"/> <br />
												</xsl:for-each>
											</span>
										</div>
									</xsl:if>


								</div>
							</div>
							<div class="full_area0 itemBlock">
								<h3 class="xLarge_area0_p8 endline blockHeader">
									<xsl:value-of select="escidoc:label('publications')"/>
								</h3>
								<span class="seperator">&#160;</span>
								<div class="free_area0 itemBlockContent endline publicationsArea">		
									&#160;
								</div>
							</div>				
							
						</div>
						
					</div>
				<!-- end: content section -->
				</div>
			
			</body>
		</html>
	</xsl:template>
	
	<xsl:variable name="labels">
		<language id="en" label="English">
			<label id="researcher_portfolio">Researcher Portfolio</label>
			<label id="provided_by_cone">This Researcher Portfolio is provided by the eSciDoc CoNE service.</label>
			<label id="language_not_provided">The selected language is not supported by this service.</label>
			<label id="researcher_profile">Researcher Profile</label>
			<label id="current_position">Current Position</label>
			<label id="former_position">Former Position</label>
			<label id="awards">Awards</label>
			<label id="phone">Phone</label>
			<label id="email">email</label>
			<label id="homepages">Researcher Homepage(s)</label>
			<label id="additional_ids">Additional IDs</label>
			<label id="researcher_id">Researcher ID</label>
			<label id="research_fields">Research Fields</label>
			<label id="subject">Subject</label>
			<label id="publications">Publications</label>
		</language>
		<language id="de" label="Deutsch">
			<label id="researcher_portfolio">Forscher Portfolio</label>
			<label id="provided_by_cone">Diese Forscher Portfolio wird von der eSciDoc CoNE Service bereitgestellt</label>
			<label id="language_not_provided">Die gewählte Sprache wird von diesem Service nicht unterstützt.</label>
			<label id="researcher_profile">Forscherprofil</label>
			<label id="current_position">Aktuelle Position</label>
			<label id="former_position">Ehemalige Position</label>
			<label id="awards">Auszeichnungen</label>
			<label id="phone">Telefon</label>
			<label id="email">E-Mail</label>
			<label id="homepages">Forscher Homepage(s)</label>
			<label id="additional_ids">Zusätzliche IDs</label>
			<label id="researcher_id">Forscher ID</label>
			<label id="research_fields">Forschungsgebiete</label>
			<label id="subject">Subjekt</label>
			<label id="publications">Veröffentlichungen</label>
		</language>
		<language id="ja" label="日本語 ">
			<label id="researcher_portfolio">研究者ポートフォリオ</label>
			<label id="provided_by_cone">研究者ポートフォリオはeSciDoc CoNEサービスにより提供されています。</label>
			<label id="language_not_provided">このサービスでは指定された言語に対応していません。</label>
			<label id="researcher_profile">研究者プロフィール</label>
			<label id="current_position">現職</label>
			<label id="former_position">旧役職</label>
			<label id="awards">受賞歴</label>
			<label id="phone">電話番号</label>
			<label id="email">Eメール</label>
			<label id="homepages">ホームページ</label>
			<label id="additional_ids">関連ID</label>
			<label id="researcher_id">研究者ID</label>
			<label id="research_fields">研究分野</label>
			<label id="subject">主題</label>
			<label id="publications">出版物</label>
		</language>

	</xsl:variable>
	
	<xsl:function name="escidoc:label">
		<xsl:param name="name"/>
		<xsl:choose>
			<xsl:when test="not(exists($labels/language[@id = $lang]))">
				<xsl:choose>
					<xsl:when test="not(exists($labels/language[@id = $defaultLang]/label[@id = $name]))">### Label '<xsl:value-of select="$name"/>' nor found ###</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$labels/language[@id = $defaultLang]/label[@id = $name]"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="not(exists($labels/language[@id = $lang]/label[@id = $name]))">### Label '<xsl:value-of select="$name"/>' nor found ###</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$labels/language[@id = $lang]/label[@id = $name]"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
</xsl:stylesheet>