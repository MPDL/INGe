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


 Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  xmlns:eprints="http://purl.org/eprint/terms/" xmlns:escidoc="http://www.escidoc.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/" xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:ddc="http://dewey.info/">
	
	<xsl:output method="xml" encoding="UTF-8" media-type="text/html"/>

	<xsl:param name="citation-link"/>
	<xsl:param name="item-link"/>
	<xsl:param name="lang" select="'en'"/>
	<xsl:param name="escidoc.pubman.common.presentation.url"/>
	<xsl:param name="escidoc.pubman.stylesheet.contrast.url"/>
	<xsl:param name="escidoc.pubman.stylesheet.classic.url"/>
	<xsl:param name="escidoc.pubman.stylesheet.standard.url"/>
	
	
	<xsl:variable name="defaultLang" select="'en'"/>

	<xsl:template match="/">
		<xsl:apply-templates select="rdf:RDF/rdf:Description"/>
	</xsl:template>

	<xsl:template match="rdf:Description">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<title>CoNE - <xsl:value-of select="dc:title"/></title>
				<link type="text/css" rel="stylesheet">
					<xsl:attribute name="href"><xsl:value-of select="$escidoc.pubman.common.presentation.url" />resources/cssFramework/main.css</xsl:attribute>
				</link>
				<link id="highContrastTheme" type="text/css" title="kontrastreich" rel="alternate stylesheet">
					<xsl:attribute name="href"><xsl:value-of select="$escidoc.pubman.stylesheet.contrast.url" /></xsl:attribute>
				</link>
				<link id="classicTheme" type="text/css" title="classic" rel="alternate stylesheet">
					<xsl:attribute name="href"><xsl:value-of select="$escidoc.pubman.stylesheet.classic.url" /></xsl:attribute>
				</link>
				<link id="PubManTheme" type="text/css" title="PubMan" rel="stylesheet">
					<xsl:attribute name="href"><xsl:value-of select="$escidoc.pubman.stylesheet.standard.url" /></xsl:attribute>
				</link>
				
		<script language="JavaScript" type="text/javascript">
		  function applyCookieStyle() {
				var cookieValue = ""
				var cookie = "layout=";
				var dc = document.cookie;
				if (dc.length <xsl:text disable-output-escaping="yes"> > </xsl:text> 0) {
					var start = dc.indexOf(cookie);
					if (start != -1) {
						start += cookie.length;
						var stop = dc.indexOf(";", start);
						if (stop == -1) stop = dc.length;
						cookieValue = unescape(dc.substring(start,stop));
					}
				}
				var enableHiddenShemes = false;
				cookie = "enableHiddenSchemes=";
				if (dc.length <xsl:text disable-output-escaping="yes"> > </xsl:text> 0) {
					var start = dc.indexOf(cookie);
					if (start != -1) {
						start += cookie.length;
						var stop = dc.indexOf(";", start);
						if (stop == -1) stop = dc.length;
						if(unescape(dc.substring(start,stop)) == 'true') {enableHiddenShemes = true;};
					}
				}
				if (cookieValue != "" <xsl:text disable-output-escaping="yes"> <![CDATA[&&]]> </xsl:text> document.getElementsByTagName) {
					var el = document.getElementsByTagName("link");
					for (var i = 0; el.length<xsl:text disable-output-escaping="yes"> > </xsl:text>i; i++ ) {
						if (el[i].getAttribute("rel").indexOf("style") != -1 <xsl:text disable-output-escaping="yes"> <![CDATA[&&]]> </xsl:text> el[i].getAttribute("id") == cookieValue <xsl:text disable-output-escaping="yes"> <![CDATA[&&]]> </xsl:text> enableHiddenShemes <xsl:text disable-output-escaping="yes"> <![CDATA[&&]]> </xsl:text> (el[i].getAttribute("title") == null || el[i].getAttribute("title") == "" ) ) {
							el[i].setAttribute("title", el[i].getAttribute("id"));
						}
						if (el[i].getAttribute("rel").indexOf("style") != -1 <xsl:text disable-output-escaping="yes"> <![CDATA[&&]]> </xsl:text> el[i].getAttribute("id")) {
							el[i].disabled = true;
							if (el[i].getAttribute("id") == cookieValue) el[i].disabled = false;
						}
					}
				}
			}
		
			function setStyleCookie() {
				var cookieValue = "";
				if(document.getElementsByTagName) {
					var el = document.getElementsByTagName("link");
					for (var i = 0; el.length<xsl:text disable-output-escaping="yes"> > </xsl:text>i; i++ ) {
						var enabledCounter = 0;
						if (el[i].getAttribute("rel").indexOf("style") != -1 <xsl:text disable-output-escaping="yes"> <![CDATA[&&]]> </xsl:text> el[i].getAttribute("id") <xsl:text disable-output-escaping="yes"> <![CDATA[&&]]> </xsl:text> el[i].getAttribute("title") <xsl:text disable-output-escaping="yes"> <![CDATA[&&]]> </xsl:text> el[i].disabled == false <xsl:text disable-output-escaping="yes"> <![CDATA[&&]]> </xsl:text> enabledCounter == 0) {
							cookieValue = el[i].getAttribute("id");
							enabledCounter++;
						}
					}
				}
				var now = new Date();
				var exp = new Date(now.getTime() + (1000*60*60*24*30));
				if(cookieValue != "") {
					document.cookie = "layout=" + escape(cookieValue) + ";" +
										"expires=" + exp.toGMTString() + ";" +
										"path=/";
				}
			}
			applyCookieStyle();
			window.onunload=function(e){setStyleCookie();};
		</script>
				
				<style type="text/css">
					.Italic {
						font-style:italic;
					}
				</style>
				<script type="text/javascript" language="JavaScript" src="/cone/js/jquery-1.2.6.min.js">;</script>
				<script>
				
				/*START SOLUTION FOR getElementsByTagName FOR NS-XML*/
				function getElementsByTagName(tagName, ns, prefix, scope){
					var elementListForReturn = scope.getElementsByTagName(prefix+':'+tagName);
					if(elementListForReturn.length == 0){
						elementListForReturn = scope.getElementsByTagName(tagName);
						if(elementListForReturn.length == 0){
							elementListForReturn = scope.getElementsByTagName('ns:'+tagName);
							if(!(elementListForReturn.length != 0 || !document.getElementsByTagNameNS)){
								elementListForReturn = scope.getElementsByTagNameNS(ns, tagName);
							}
						}
					}     
				
					return elementListForReturn;
				   }
				   /*STOP SOLUTION FOR getElementsByTagName FOR NS-XML*/
				
					var ampEsc = '&amp;';
					var amp = ampEsc.substring(0,1);
				
					$(document).ready(function() {
						var requestString = '<xsl:value-of select="$citation-link"/>';
						requestString = requestString.replace(/&amp;/g, amp);
						
						$('.publicationsArea').append('<div class="big_imgArea huge_marginLExcl smallThrobber"></div>');
						
						$.get(requestString, function(itemList){
							var allItems = getElementsByTagName('item', 'http://www.escidoc.de/schemas/item/0.7','escidocItem', itemList);
							
							var element = '';
							var itemURL = '';
							var publicationTitle = '';
							
							for(var i=0; getElementsByTagName('item', 'http://www.escidoc.de/schemas/item/0.7','escidocItem', itemList).length <xsl:text disable-output-escaping="yes"> > </xsl:text>i; i++){
							
								var citation = getElementsByTagName('bibliographicCitation', 'http://purl.org/dc/terms/', 'dcterms', allItems[i])[0];
								
								if (typeof citation!= 'undefined')
								{
									itemURL = '';
									itemURL = '<xsl:value-of select="$item-link"/>'.replace('$1', $(allItems[i]).attr('objid') + ':' + $(getElementsByTagName('number', 'http://escidoc.de/core/01/properties/release/', 'release', getElementsByTagName('latest-release', 'http://escidoc.de/core/01/properties/', 'prop', allItems[i])[0])[0]).text());

									element = '<span class="xHuge_area0 xTiny_marginLExcl endline citationBlock">' + $(citation).text() + ' [<a href="' + itemURL + '" target="_blank" >PubMan</a>]' + '</span>';
									
									if($('.publicationsArea').find('.smallThrobber').length != 0) {$('.publicationsArea').find('.smallThrobber').remove();}
									
									$('.publicationsArea').append('<b class="xLarge_area0 endline labelLine">&#160;<span class="noDisplay">: </span></b>');
									$('.publicationsArea').append(element);

									$('.publicationsArea:last-child').find('span.Default').each(function(k, elem){
										$(elem).replaceWith($(elem).html());
									});
								}
							}
							$('.publicationsArea').find('.smallThrobber').remove();							
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
				<div class="full wrapper">
					<div class="full_area0 header clear">
					<!-- start: header section -->
						<span id="metaMenuSkipLinkAnchor" class="full_area0 metaMenu">
							<!-- logo alternate area starts here -->
							<div class="free_area0 small_marginLExcl logoAlternate">
								<a href="" >
									<span>eSciDoc.</span>
									<span>CoNE</span>
								</a>
							</div>
							<!-- logo alternate area starts here -->
							<!-- meta Menu starts here -->
							<xsl:comment>
								<xsl:if test="not(exists($labels/language[@id = $lang]))">
									<span class="free_area0 messageError"><xsl:value-of select="escidoc:label('language_not_provided')"/></span>
								</xsl:if>
							</xsl:comment>
							<select class="medium_select endline replace" onchange="changeLanguage(this)">
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
							<!-- meta Menu ends here -->
						</span>
					<!-- end: header section -->
					</div>
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
								<div class="subHeader" >
									<!-- Subheadline starts here -->
								<!-- <h6><xsl:value-of select="escidoc:label('provided_by_cone')"/></h6> -->
									<!-- Subheadline ends here -->
								</div>
							</div>
						</div>
						<div class="full_area0">
							<div class="full_area0 fullItem">
								<div class="full_area0 itemHeader noTopBorder">
									<span class="xLarge_area0 endline">
										&#160;
									</span>
									<span class="seperator">&#160;</span>
									<span class="free_area0_p8 endline itemHeadline">
										<h2>
											<xsl:if test="eterms:degree != ''"><xsl:value-of select="eterms:degree"/><xsl:text> </xsl:text></xsl:if> <xsl:value-of select="dc:title"/>
										</h2>
										<h3>
											<xsl:for-each select="eterms:position/rdf:Description[not(exists(eterms:end-date)) or eterms:end-date = '' or escidoc:date(eterms:end-date, true()) &gt;= current-date()]/eprints:affiliatedInstitution">
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
									
										<xsl:for-each select="eterms:position/rdf:Description[not(exists(eterms:end-date)) or eterms:end-date = '' or (escidoc:date(eterms:end-date, true()) &gt;= current-date())]">
											<xsl:sort select="eprints:affiliatedInstitution[0]"/>
											<div class="free_area0 endline itemLine noTopBorder">
												<b class="xLarge_area0 endline labelLine">
													<xsl:value-of select="escidoc:label('current_position')"/><span class="noDisplay">: </span>
												</b>
												<span class="xHuge_area0 xTiny_marginLExcl endline">
													<xsl:value-of select="eterms:position-name"/>
													<xsl:text> </xsl:text>
													<xsl:if test="eprints:affiliatedInstitution">
														<xsl:if test="exists(eterms:position-name)">(</xsl:if>
														<xsl:for-each select="eprints:affiliatedInstitution">
															<xsl:value-of select="."/>
															<xsl:if test="position() != last()">, </xsl:if>
														</xsl:for-each>
														<xsl:if test="exists(eterms:position-name)">)</xsl:if>
													</xsl:if>
												</span>
											</div>
										</xsl:for-each>
										<xsl:for-each select="eterms:position/rdf:Description[eterms:end-date != '' and escidoc:date(eterms:end-date, true()) &lt; current-date()]">
											<xsl:sort select="eterms:end-date" order="descending"/>	
											<div class="free_area0 endline itemLine noTopBorder">
												<b class="xLarge_area0 endline labelLine">
													<xsl:value-of select="escidoc:label('former_position')"/><span class="noDisplay">: </span>
												</b>
												<span class="xHuge_area0 xTiny_marginLExcl endline">
													<xsl:value-of select="eterms:position-name"/>
													<xsl:text> </xsl:text>
													<xsl:if test="eprints:affiliatedInstitution">
														<xsl:if test="exists(eterms:position-name)">(</xsl:if>
														<xsl:for-each select="eprints:affiliatedInstitution">
															<xsl:value-of select="."/>
															<xsl:if test="position() != last()">, </xsl:if>
														</xsl:for-each>
														<xsl:if test="exists(eterms:position-name)">)</xsl:if>
													</xsl:if>
												</span>
											</div>
										</xsl:for-each>
										<xsl:if test="exists(eterms:award)">
											<div class="free_area0 endline itemLine noTopBorder">
												<b class="xLarge_area0 endline labelLine">
													<xsl:value-of select="escidoc:label('awards')"/><span class="noDisplay">: </span>
												</b>
												<span class="xHuge_area0 xTiny_marginLExcl endline">
													<xsl:value-of select="eterms:award"/>
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
														<xsl:value-of select="xsi:type"/>: <xsl:value-of select="rdf:value"/>
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
										<xsl:if test="exists(dcterms:subject)">
											<div class="free_area0 endline itemLine noTopBorder">
												<b class="xLarge_area0 endline labelLine">
													<xsl:value-of select="escidoc:label('research_fields')"/><span class="noDisplay">: </span>
												</b>
												<span class="xHuge_area0 xTiny_marginLExcl endline">
													<xsl:value-of select="dcterms:subject"/>
												</span>
											</div>
										</xsl:if>
										
										<xsl:if test="exists(dc:subject)">
											<div class="free_area0 endline itemLine noTopBorder">
												<b class="xLarge_area0 endline labelLine">
													<xsl:value-of select="escidoc:label('subject')"/><span class="noDisplay">: </span>
												</b>
												<span class="xHuge_area0 xTiny_marginLExcl endline">
													<xsl:for-each select="dc:subject/rdf:Description">
													<xsl:text></xsl:text> <xsl:value-of select="ddc:class"/>-<xsl:value-of select="dc:title"/><br/>
													</xsl:for-each>
												</span>
											</div>
										</xsl:if>
										
										
	
	
									</div>
								</div>
								<div class="full_area0 itemBlock">
									<h3 class="xLarge_area0_p8 endline blockHeader"><xsl:value-of select="escidoc:label('external_references')"/></h3>
									<span class="seperator">&#160;</span>
									<div class="free_area0 itemBlockContent endline">
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">WorldCat</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<a>
													<xsl:attribute name="href">http://www.worldcat.org/search?q=au%3A<xsl:value-of select="foaf:givenname"/><xsl:text> </xsl:text><xsl:value-of select="foaf:family_name"/></xsl:attribute>
													Search for <xsl:value-of select="dc:title"/>
												</a>
											</span>
										</div>
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0 endline labelLine">Google Scholar</b>
											<span class="xHuge_area0 xTiny_marginLExcl endline">
												<a>
													<xsl:attribute name="href">http://scholar.google.de/scholar?q=author%3A%22<xsl:value-of select="foaf:givenname"/><xsl:text> </xsl:text><xsl:value-of select="foaf:family_name"/>%22</xsl:attribute>
													Search for <xsl:value-of select="dc:title"/>
												</a>
											</span>
										</div>
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
			<label id="external_references">External references</label>
		</language>
		<language id="de" label="Deutsch">
			<label id="researcher_portfolio">Forscher Portfolio</label>
			<label id="provided_by_cone">Dieses Forscher Portfolio wird vom eSciDoc CoNE Service bereitgestellt</label>
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
			<label id="external_references">Externe Verweise</label>
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
			<label id="external_references">外部参照</label>
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
	
	<xsl:function name="escidoc:date" as="xs:date">
		<xsl:param name="text"/>
		<xsl:param name="last" as="xs:boolean"/>
		
		<xsl:choose>
			<xsl:when test="string-length($text) = 4">
				<xsl:choose>
					<xsl:when test="$last">
						<xsl:value-of select="xs:date(concat($text, '-12-31'))"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="xs:date(concat($text, '-01-01'))"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="string-length($text) = 7">
				<xsl:choose>
					<xsl:when test="$last">
						<xsl:choose>
							<xsl:when test="substring-after($text, '-') = '01'"><xsl:value-of select="xs:date(concat($text, '-31'))"/></xsl:when>
							<xsl:when test="substring-after($text, '-') = '03'"><xsl:value-of select="xs:date(concat($text, '-31'))"/></xsl:when>
							<xsl:when test="substring-after($text, '-') = '05'"><xsl:value-of select="xs:date(concat($text, '-31'))"/></xsl:when>
							<xsl:when test="substring-after($text, '-') = '07'"><xsl:value-of select="xs:date(concat($text, '-31'))"/></xsl:when>
							<xsl:when test="substring-after($text, '-') = '08'"><xsl:value-of select="xs:date(concat($text, '-31'))"/></xsl:when>
							<xsl:when test="substring-after($text, '-') = '10'"><xsl:value-of select="xs:date(concat($text, '-31'))"/></xsl:when>
							<xsl:when test="substring-after($text, '-') = '12'"><xsl:value-of select="xs:date(concat($text, '-31'))"/></xsl:when>
							<xsl:when test="substring-after($text, '-') = '02'"><xsl:value-of select="xs:date(concat($text, '-28'))"/></xsl:when>
							<xsl:otherwise><xsl:value-of select="xs:date(concat($text, '-30'))"/></xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="xs:date(concat($text, '-01'))"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="xs:date($text)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
</xsl:stylesheet>